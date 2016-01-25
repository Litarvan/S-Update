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
package fr.theshark34.supdate.application.integrated

import com.google.gson.reflect.TypeToken
import fr.theshark34.supdate.BadServerResponseException
import fr.theshark34.supdate.FileNoPermissionException
import fr.theshark34.supdate.LOGGER
import fr.theshark34.supdate.SUpdate
import fr.theshark34.supdate.application.Application
import fr.theshark34.supdate.application.ApplicationEvent
import fr.theshark34.supdate.application.FileActionEvent
import fr.theshark34.supdate.application.FileCheckingEvent
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.text.contains

/**
 * The FileDeleter
 *
 * This is application deletes all files not on the server
 * excepted the ones in the ignore list on the server.
 * Like a... bulldozer.
 *
 *
 * _[TT]_j__,(
 * (_)oooo(_)'
 *
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class FileDeleter : Application()
{
    /**
     * The list of files to not delete
     */
    private var ignoreList: MutableList<String>? = ArrayList()

    override val name: String
        get() = "FileDeleter"

    override val isServerRequired: Boolean
        get() = true

    override fun onInit(event: ApplicationEvent)
    {
    }

    override fun onStart(event: ApplicationEvent)
    {
        LOGGER.info("[FileDeleter] Getting the ignore list")

        try
        {
            // Sending a get ignore list request
            val list = event.sUpdate.serverRequester.sendRequest("get-ignore-list", object : TypeToken<List<String>>()
            {
            }.type)

            // If the response is a string (so its the raw response because the JSON parse failed)
            if (list is String)
            // Throwing a BadServerResponse exception
                throw BadServerResponseException(list)

            // Getting the list
            @SuppressWarnings("unchecked")
            val fileList = list as List<String>

            // For each file in the list
            for (file in fileList)
            // Adding it to the list
                ignoreList!!.add(file)
        }
        catch (e: IOException)
        {
            LOGGER.warning("[FileDeleter] Unable to get the ignore list, desactivating the FileDeleter. Error : ", e)

            // Setting the list to null, sign of desactivation
            ignoreList = null
        }
        catch (e: BadServerResponseException)
        {
            LOGGER.warning("[FileDeleter] Unable to get the ignore list, desactivating the FileDeleter. Error : ", e)

            // Setting the list to null, sign of desactivation
            ignoreList = null
        }

    }

    override fun onFileChecking(event: FileCheckingEvent): Boolean
    {
        // If the ignore list is null, the application as been desactivated
        if (ignoreList == null)
        // So stopping
            return event.checkResult

        // If a file is checked, it is present on the server,
        // so adding it to the ignore list
        ignoreList!!.add(event.checkedFilePath)

        // Don't modifying the check result
        return event.checkResult
    }

    override fun onFileAction(event: FileActionEvent)
    {
    }

    override fun onUpdateEnd(event: ApplicationEvent)
    {
        LOGGER.info("[FileDeleter] Deleting the unknown files")

        // Listing the local folder
        val files = listFiles(event.sUpdate.outputFolder)

        // For each file
        for (file in files)
        // If it is not in the ignore list
            if (!isOnIgnoreList(event.sUpdate, file))
            // Deleting it
                try
                {
                    LOGGER.info("[FileDeleter] Deleting file '%s'.", file.absolutePath)
                    event.sUpdate.fileManager.delete(file)
                }
                catch (e: FileNoPermissionException)
                {
                    LOGGER.warning("[FileDeleter] The file '" + file.absolutePath + "' wasn't deleted, error :", e)
                }

    }

    /**
     * Checks if a file is on the ignore list
     *
     * @param file The file to check
     *
     * @return True if it is, false if not
     */
    fun isOnIgnoreList(sUpdate: SUpdate, file: File): Boolean
    {
        // For each file in the ignore list
        for (ignoredFilePath in ignoreList!!)
        {
            // Getting the local file for the file in the list
            val ignoredFile = File(sUpdate.outputFolder, ignoredFilePath)

            // If their paths are equals, or the file path contains the ignored file path
            if (ignoredFile.absolutePath == file.absolutePath || file.absolutePath.contains(ignoredFile.absolutePath))
            // Returning true :3
                return true
        }

        // If this line is executed, so return true wasn't, so
        // returning false.
        return false
    }

    companion object
    {

        /**
         * List all files (recursively) in a folder
         *
         * @param folder The folder to list
         *
         * @return A list of the listed files
         */
        fun listFiles(folder: File): ArrayList<File>
        {
            val files = folder.listFiles()

            val list = ArrayList<File>()

            if (files == null)
                return list

            for (f in files)
                if (f.isDirectory)
                    list.addAll(listFiles(f))
                else
                    list.add(f)

            return list
        }
    }
}
