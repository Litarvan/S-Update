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

import fr.theshark34.supdate.LOGGER
import fr.theshark34.supdate.application.Application
import fr.theshark34.supdate.application.ApplicationEvent
import fr.theshark34.supdate.application.FileActionEvent
import fr.theshark34.supdate.application.FileCheckingEvent
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import kotlin.text.endsWith

/**
 * The Additional File Downloader
 *
 * Download a given list of files if they didn't exist, and
 * forcing check skipping for them
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class AdditionalFileDownloader(private val files: Array<String>) : Application()
{
    override val name: String
        get() = "Aditional file downloader"

    override val isServerRequired: Boolean
        get() = false

    override fun onInit(event: ApplicationEvent)
    {
    }

    override fun onStart(event: ApplicationEvent)
    {
        LOGGER.info("[Additional File Downloader] Starting")

        val su = event.sUpdate
        var input: URL?

        for (file in files)
        {
            val output = File(su.outputFolder, file)

            if (output.exists())
                continue

            try
            {
                input = URL(su.serverUrl + (if (su.serverUrl.endsWith("/")) "" else "/") + file)
            }
            catch (e: MalformedURLException)
            {
                LOGGER.error("[Additional File Downloader] Can't create the URL ! Aborting", e)
                return
            }

            su.fileManager.download(input as URL, output)
        }

        LOGGER.info("[Additional File Downloader] Done")
    }

    override fun onFileChecking(event: FileCheckingEvent): Boolean
    {
        for (file in files)
            if (event.checkedFilePath == file)
                return false

        return event.checkResult
    }

    override fun onFileAction(event: FileActionEvent)
    {
    }

    override fun onUpdateEnd(event: ApplicationEvent)
    {
    }
}
