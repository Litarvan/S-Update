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

import fr.theshark34.supdate.application.Application
import fr.theshark34.supdate.application.ApplicationEvent
import fr.theshark34.supdate.application.FileActionEvent
import fr.theshark34.supdate.application.FileCheckingEvent
import net.wytrem.wylog.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * The Minecraft Natives Manager
 *
 * Used to manage natives using a zip when some times
 * they are deleted by host.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class MinecraftNativesManager(val zipFile: String) : Application()
{
    private val logger = LoggerFactory.getLogger("MinecraftNativesManager")
    val files = ArrayList<File>()
    var isNeedToDownload = false
        private set

    override val name: String
        get() = "Natives Manager"

    override val isServerRequired: Boolean
        get() = false

    override fun onInit(event: ApplicationEvent)
    {
    }

    override fun onStart(event: ApplicationEvent)
    {
        logger.info("Getting file list")
        val osFiles: Array<String>
        if (System.getProperty("os.name").toLowerCase().contains("win"))
            osFiles = WINDOWS_FILES
        else if (System.getProperty("os.name").toLowerCase().contains("mac"))
            osFiles = MAC_FILES
        else
            osFiles = LINUX_FILES

        for (str in osFiles)
            files.add(File(event.sUpdate.outputFolder, NATIVES_FOLDER + "/" + str))

        logger.info("Checking...")

        for (f in files)
        {
            logger.info("Checking " + f.name + "...")
            if (!f.exists())
            {
                logger.info("Not found !")
                isNeedToDownload = true
                break
            }
        }

        if (!isNeedToDownload)
            logger.info("No need to download.")
        else
        {
            logger.info("Need to download the natives...")
            try
            {
                event.sUpdate.fileManager.download(URL(event.sUpdate.serverUrl + "/" + zipFile), File(event.sUpdate.outputFolder, NATIVES_FOLDER + "/" + zipFile))
            }
            catch (e: MalformedURLException)
            {
                logger.error("Can't download the natives !", e)
                return
            }

        }
        logger.info("Done." + if (isNeedToDownload) " Waiting for the end." else "")
    }

    override fun onFileChecking(event: FileCheckingEvent): Boolean
    {
        return event.checkResult
    }

    override fun onFileAction(event: FileActionEvent)
    {
    }

    override fun onUpdateEnd(event: ApplicationEvent)
    {
        if (!isNeedToDownload)
            return

        logger.info("Unzipping the natives")
        val nativesDirectory = File(event.sUpdate.outputFolder, NATIVES_FOLDER)
        val zip = File(nativesDirectory, zipFile)

        try
        {
            unzip(zip, nativesDirectory)
        }
        catch (e: IOException)
        {
            logger.error("Can't unzip the natives !", e)
        }

        logger.info("Deleting the zip")
        if (!zip.delete())
            zip.deleteOnExit()

        logger.info("Done")
    }

    @Throws(IOException::class)
    private fun unzip(zipFile: File, folder: File)
    {
        val buffer = ByteArray(1024)

        if (!folder.exists())
            folder.mkdir()

        val zis = ZipInputStream(FileInputStream(zipFile))
        var ze: ZipEntry? = zis.nextEntry

        while (ze != null)
        {
            val fileName = ze.name
            val newFile = File(folder, fileName)

            newFile.parentFile.mkdirs()

            val fos = FileOutputStream(newFile)

            var len: Int = 1
            while (len > 0)
            {
                len = zis.read(buffer)
                fos.write(buffer, 0, len)
            }

            fos.close()
            ze = zis.nextEntry
        }

        zis.closeEntry()
        zis.close()
    }

    companion object
    {
        val WINDOWS_FILES = arrayOf("jinput-dx8.dll", "jinput-dx8_64.dll", "jinput-raw.dll", "jinput-raw_64.dll", "lwjgl.dll", "lwjgl64.dll", "OpenAL32.dll", "OpenAL64.dll")
        val MAC_FILES = arrayOf("libjinput-osx.jnilib", "liblwjgl.jnilib", "libtwitchsdk.dylib", "openal.dylib")
        val LINUX_FILES = arrayOf("libjinput-linux.so", "libjinput-linux64.so", "liblwjgl.so", "liblwjgl64.so", "liblwjgl.so", "liblwjgl64.so", "libopenal.so", "libopenal64.so")
        val NATIVES_FOLDER = "natives"
    }
}
