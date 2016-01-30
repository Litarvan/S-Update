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

import fr.theshark34.supdate.application.Application
import fr.theshark34.supdate.application.ApplicationManager
import fr.theshark34.supdate.check.CheckMethod
import fr.theshark34.supdate.check.integrated.Murmur3CheckMethod
import fr.theshark34.supdate.files.FileManager
import net.wytrem.wylog.LoggerFactory
import java.io.File
import java.io.IOException

/**
 * Unique logger object.
 */
val LOGGER = LoggerFactory.getLogger("S-Update")

/**
 * The SUpdate version message
 */
val VERSION = "S-Update Client version 3.2.0-BETA by Litarvan (Adrien Navratil)"

/**
 * The minimal server version required
 */
val SERVER_MIN_VERSION = "3.2.0-BETA"

/**
 * The folder where are the files
 */
val FILES_FOLDER = "files"

/**
 * The default check method
 */
val DEFAULT_CHECK_METHOD: CheckMethod = Murmur3CheckMethod()

/**
 * The S-Update object
 *
 * The S-Update object contains all the informations about the update
 * like the applications, the server address, the output folder, and
 * things like this.
 *
 *
 * How to start updating ?
 *
 * To start updating, just use the start method.
 *
 *
 * What server are you talking about ?
 *
 * S-Update is an update system. To use it, it must have a distant
 * repository where the original files are hosted, and also he need
 * some indications of the files, and things like this. To do this,
 * use the S-Update Server. Download it here :
 * https://github.com/TheShark34/S-Update-Server/releases
 *
 *
 * What the fuck are applications ?
 *
 * The applications are things to give superpowers to S-Update.
 * Applications can be server-only, client-only, or in both.
 * There is some included applications (but there are not enabled
 * by default). To add an application, use SUpdate.addApplication(
 * class extending Application instance). To create your own
 * application, in client create the Application class, then use
 * the events to add some things, but for some clients things like
 * sending request, or server application, use the wiki in the Github
 * repo.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class SUpdate(var serverUrl: String, var outputFolder: File)
{
    /**
     * The Crash Reporter
     */
    var reporter = CrashReporter(File(outputFolder, "s-update-crashes/"))

    /**
     * The check method used to check the files
     */
    var checkMethod = DEFAULT_CHECK_METHOD

    /**
     * The current application manager
     */
    val applicationManager = ApplicationManager()

    /**
     * The current file manager
     */
    val fileManager = FileManager(this)

    /**
     * The current server requester
     */
    val serverRequester = ServerRequester(this)

    /**
     * The current bar API
     */
    val values = DownloadValues()

    /**
     * If crash reporting is enabled
     */
    var crashReportingEnabled = false

    /**
     * The current updater
     */
    var updater: Updater? = null
        private set

    /**
     * Starts the update ! Create the Updater, call the applications
     * events, and start the Updater !
     *
     * @throws BadServerResponseException If the response of a request wasn't JSON, or things like that
     * @throws BadServerVersionException If the version isn't at least the min version
     * @throws ServerMissingSomethingException If the server is missing the check method or an application
     * @throws IOException If it failed to do the request
     */
    @Throws(BadServerResponseException::class, ServerDisabledException::class, BadServerVersionException::class, ServerMissingSomethingException::class, IOException::class)
    fun start()
    {
        // Creating the updater
        updater = Updater(this)

        try
        {
            // And starting it !
            updater!!.start()
        }
        catch (e: Exception)
        {
            if (crashReportingEnabled)
                reporter.catchError(e)
            else
                throw e
        }
    }

    /**
     * Add an application to S-Update (same as getApplicationManager().addApplication)
     *
     * @param application The application to add
     */
    fun addApplication(application: Application)
    {
        applicationManager.addApplication(this, application)
    }
}