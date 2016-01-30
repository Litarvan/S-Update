/*
 * Copyright 2015-2016 Adrien Navratil
 *
 * This file is part of S-Update.

 * S-Update is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * S-Update is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with S-Update.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.theshark34.supdate

import com.google.gson.Gson
import fr.theshark34.supdate.application.ApplicationEvent
import fr.theshark34.supdate.application.FileCheckingEvent
import fr.theshark34.supdate.check.FileInfos
import java.io.File
import java.io.IOException
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.URL
import java.util.*
import kotlin.collections.dropLastWhile
import kotlin.collections.toTypedArray
import kotlin.text.*

/**
 * The Updater object
 *
 * The Updater just check all, and update all !
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class Updater(val sUpdate: SUpdate)
{
    /**
     * Starts the update ! Call the applications, check the files
     * download it, etc...
     *
     * @throws BadServerResponseException If the response of a request wasn't JSON, or things like that
     * @throws BadServerVersionException If the version isn't at least the min version
     * @throws ServerMissingSomethingException If the server is missing the check method or an application
     * @throws IOException If it failed to do the request
     */
    @Throws(SUpdateException::class, IOException::class)
    fun start()
    {
        // Printing a beautiful message
        printInfos()

        // Saving the start time
        val startTime = System.currentTimeMillis()

        // Checking the server state
        checkState()

        // Checking the server version
        checkVersion()

        // Checking the check method and the applications
        checkCheckMethodAndApplications()

        // Sending a request to update the stats
        sUpdate.serverRequester.sendPostRequest("stats/update")

        // For each application
        for (app in sUpdate.applicationManager.applications)
        // Sending the onStart event
            app.onStart(ApplicationEvent(sUpdate))

        LOGGER.info("Listing the files")

        // Creating the files list
        val fileList = createFileList()

        // Creating the list of files to download
        val filesToDownload = HashMap<URL, File>()

        // Creating the list of files to download with their relative path
        val filesPaths = ArrayList<String>()

        LOGGER.info("Checking them")

        // For each file infos
        for (fileInfos in fileList)
        {
            // Getting their check result
            var checkResult: Boolean

            try
            {
                checkResult = sUpdate.checkMethod.checkFile(sUpdate, fileInfos)
            }
            catch (e: UnableToCheckException)
            {
                // If it failed printing the error
                e.printStackTrace()

                // And stopping
                return
            }

            // Sending it to all the applications
            for (app in sUpdate.applicationManager.applications)
                checkResult = app.onFileChecking(FileCheckingEvent(sUpdate, fileInfos.fileRelativePath, checkResult))

            // If we need to download the file
            if (checkResult)
            {
                // Getting its infos
                val fileURL = URL((sUpdate.serverUrl + (if (sUpdate.serverUrl.endsWith("/")) "" else "/") + FILES_FOLDER + "/" + fileInfos.fileRelativePath).replace(" ".toRegex(), "%20"))
                val localFile = File(sUpdate.outputFolder, fileInfos.fileRelativePath)

                // Adding them to the filesToDownload map
                filesToDownload.put(fileURL, localFile)

                // Adding it to the files paths list
                filesPaths.add(fileInfos.fileRelativePath)
            }
        }

        // Setting the DownloadValues 'numberOfFileToDownload' variable to the size of the filesToDownload list
        sUpdate.values.numberOfFileToDownload = filesToDownload.size

        LOGGER.info("%d files were checked, %s", fileList.size, (if (filesToDownload.size == 0) "nothing to download" else "need to download " + filesToDownload.size + " of them."))

        // Setting the cookie manager
        val cookieManager = CookieManager(null, CookiePolicy.ACCEPT_ALL)
        CookieHandler.setDefault(cookieManager)

        // If we need to download files
        if (filesPaths.size > 0)
        {
            // Adding to the DownloadValues 'numberOfTotalBytesToDownload' variable, the size of the file to download
            LOGGER.info("Requesting bytes to download... ")
            getBytesToDownload(filesPaths)
            LOGGER.info("Done: %d", sUpdate.values.getNumberOfTotalBytesToDownload())

            LOGGER.info("Starting download the files")
        }

        // Downloading the files
        for (entry in filesToDownload.entries)
            sUpdate.fileManager.download(entry.key, entry.value)

        // Terminating
        sUpdate.fileManager.stop()

        // For each application
        for (app in sUpdate.applicationManager.applications)
        // Sending the onUpdateEnd eventc
            app.onUpdateEnd(ApplicationEvent(sUpdate))

        // Printing the total time
        printTotalTime(startTime)
    }

    /**
     * Print infos about some things about... life... and weather...
     */
    private fun printInfos()
    {
        LOGGER.info(VERSION)
        LOGGER.info("Current time is %s", Date(System.currentTimeMillis()).toString())
        LOGGER.info("Starting updating...")
        LOGGER.info("    Server URL: %s", sUpdate.serverUrl)
        LOGGER.info("    Output Dir: %s", sUpdate.outputFolder.absolutePath)
    }

    /**
     * Checks the server state
     *
     * @throws BadServerResponseException If the response wasn't JSON, or things like that
     * @throws ServerDisabledException If the server is disabled
     * @throws IOException If it failed to do the request
     */
    @Throws(SUpdateException::class, IOException::class)
    private fun checkState()
    {
        LOGGER.info("Connecting to the server... ")

        // Sending a get state request to check the server state
        val stateResponse = sUpdate.serverRequester.sendPostRequest("server/is-enabled", StateResponse::class.java)

        // If the response is a string (so its the raw response because the JSON parse failed)
        if (stateResponse is String)
        // Throwing a BadServerResponse exception
            throw BadServerResponseException(stateResponse)

        // Getting the state
        val enabled = (stateResponse as StateResponse).isEnabled

        LOGGER.info("Server " + (if (enabled) "enabled" else "disabled !"))

        // If the server is disabled
        if (!enabled)
        // Throwing a server disabled exception
            throw ServerDisabledException()
    }

    /**
     * Checks the server version
     *
     * @throws BadServerResponseException If the response wasn't JSON, or things like that
     * @throws BadServerVersionException If the version isn't at least the min version
     * @throws IOException If it failed to do the request
     */
    @Throws(SUpdateException::class, IOException::class)
    private fun checkVersion()
    {
        // Sending a version request to check the server version and ping it
        val versionResponse = sUpdate.serverRequester.sendPostRequest("server/version", VersionResponse::class.java)

        // If the response is a string (so its the raw response because the JSON parse failed)
        if (versionResponse is String)
        // Throwing a BadServerResponse exception
            throw BadServerResponseException(versionResponse)

        // Separating the revision and the version
        val splittedVersion = (versionResponse as VersionResponse).version.split("-".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        val version = Integer.parseInt(splittedVersion[0].replace("\\.".toRegex(), ""))
        val revision = splittedVersion[1]

        val splittedMinVersion = SERVER_MIN_VERSION.split("-".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        val minVersion = Integer.parseInt(splittedMinVersion[0].replace("\\.".toRegex(), ""))
        val minRevision = splittedMinVersion[1]

        // Then checking the revision and the version
        if (revision != minRevision)
            throw BadServerVersionException(minRevision, revision, true)
        else if (version < minVersion)
            throw BadServerVersionException(splittedMinVersion[0], splittedVersion[0], false)

        LOGGER.info("Version : " + versionResponse.version)
    }

    /**
     * Checks if the check method is installed on the server
     */
    @Throws(SUpdateException::class, IOException::class)
    private fun checkCheckMethodAndApplications()
    {
        // Getting the check method name
        val checkMethodName = sUpdate.checkMethod.name

        // Sending a check check method request to the server
        var response = sUpdate.serverRequester.sendPostRequest("server/check/checkmethod/" + checkMethodName.replace(" ".toRegex(), "%20"), CheckResponse::class.java)

        // If the response is a string (so its the raw response because the JSON parse failed)
        if (response is String)
        // Throwing a BadServerResponse exception
            throw BadServerResponseException(response)

        // If the check method is not present on the server
        if (!(response as CheckResponse).isPresent)
        // Throwing a new ServerMissingSomething Exception
            throw ServerMissingSomethingException("the Check Method " + checkMethodName)

        LOGGER.info("CheckMethod : " + checkMethodName)

        var appsList = ""

        // For each applications
        for (i in 0..sUpdate.applicationManager.applications.size - 1)
        {
            // Getting its name
            val applicationName = sUpdate.applicationManager.applications[i].name

            // If it is server required
            if (sUpdate.applicationManager.applications[i].isServerRequired)
            {
                // Sending a check application request to the server
                response = sUpdate.serverRequester.sendPostRequest("server/check/application/" + applicationName.replace(" ".toRegex(), "%20"), CheckResponse::class.java)

                // If the response is a string (so its the raw response because the JSON parse failed)
                if (response is String)
                // Throwing a BadServerResponse exception
                    throw BadServerResponseException(response)

                // If the application is not present on the server
                if (!(response as CheckResponse).isPresent)
                // Throwing a new ServerMissingSomething Exception
                    throw ServerMissingSomethingException("the application " + applicationName)
            }

            if (i + 1 < sUpdate.applicationManager.applications.size)
                appsList += applicationName + ", "
            else
                appsList += applicationName + "."
        }

        LOGGER.info("Applications : %s", appsList)

        // If there is no application
        if (sUpdate.applicationManager.applications.size == 0)
        // Printing 'No application'
            LOGGER.info("No application")
    }

    /**
     * Create the file list
     *
     * @return The list of the files
     */
    @SuppressWarnings("unchecked")
    @Throws(IOException::class, BadServerResponseException::class)
    private fun createFileList(): List<FileInfos>
    {
        // Sending a list files request to the server
        val response = sUpdate.serverRequester.sendPostRequest("server/list/" + sUpdate.checkMethod.name.replace(" ".toRegex(), "%20"), sUpdate.checkMethod.listType)

        // If the response is a string (so its the raw response because the JSON parse failed)
        if (response is String)
        // Throwing a BadServerResponse exception
            throw BadServerResponseException(response)

        return response as List<FileInfos>
    }

    @Throws(IOException::class, BadServerResponseException::class)
    private fun getBytesToDownload(filesToDownload: List<String>)
    {
        // Getting GSON
        val gson = Gson()

        // Sending a get total bytes request to the server
        val response = sUpdate.serverRequester.sendPostRequest("server/size", SizeResponse::class.java, gson.toJson(filesToDownload).replace(" ".toRegex(), "%20").toByteArray())

        // If the response is a string (so its the raw response because the JSON parse failed)
        if (response is String)
        // Throwing a BadServerResponse exception
            throw BadServerResponseException(response)

        // Setting it
        sUpdate.values.setNumberOfTotalBytesToDownload((response as SizeResponse).size)
    }

    /**
     * Print the total update time
     *
     * @param startTime The update start time
     */
    private fun printTotalTime(startTime: Long)
    {
        val totalTime = System.currentTimeMillis() - startTime
        val seconds = (totalTime / 1000).toInt() % 60
        val minutes = ((totalTime / (1000 * 60)) % 60).toInt()
        val hours = ((totalTime / (1000 * 60 * 60)) % 24).toInt()
        val strTime = "$hours hours $minutes minutes $seconds seconds and ${totalTime % 1000} milliseconds."

        LOGGER.info("Update finished, total time : $strTime")
    }
}
