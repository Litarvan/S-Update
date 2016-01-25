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
package fr.theshark34.supdate.files

import fr.theshark34.supdate.FileNoPermissionException
import fr.theshark34.supdate.LOGGER
import fr.theshark34.supdate.SUpdate
import fr.theshark34.supdate.application.FileActionDownloadEvent
import fr.theshark34.supdate.application.FileActionEvent
import fr.theshark34.supdate.application.FileActionRenameEvent
import java.io.File
import java.net.URL
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * The FileManager
 *
 * The FileManager class is a class to do some actions to
 * files, like downloading, renaming, or deleting a file. It
 * should always be used to do that things because it calls the
 * onFileAction event of the applications.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class FileManager(val sUpdate: SUpdate)
{
    /**
     * The executor pool for Multi-Threaded download
     */
    private val pool = Executors.newFixedThreadPool(10)

    /**
     * Download a file with a Multi-Threaded system
     *
     * @param fileUrl The URL of the file to download
     * @param outputFile The output of the file
     */
    fun download(fileUrl: URL, outputFile: File)
    {
        pool.submit(DownloadTask(sUpdate, fileUrl, outputFile))

        // Then sending a onFileAction event to all the applications
        for (app in sUpdate.applicationManager.applications)
            app.onFileAction(FileActionDownloadEvent(sUpdate, outputFile, fileUrl))
    }

    /**
     * Try to rename a file
     *
     * @param file The file to rename
     * @param dest The destination file
     */
    fun tryToRename(file: File, dest: File)
    {
        // Renaming the file
        file.renameTo(dest)

        // Then sending a onFileAction event to all the applications
        for (app in sUpdate.applicationManager.applications)
            app.onFileAction(FileActionRenameEvent(sUpdate, file, dest))
    }

    /**
     * Rename a file
     *
     * @param file The file to rename
     * @param dest The destination file
     *
     * @throws FileNoPermissionException If it failed to rename the file
     */
    @Throws(FileNoPermissionException::class)
    fun rename(file: File, dest: File)
    {
        // Renaming the file and if it failed
        if (!file.renameTo(dest))
        // Throwing a new FileNoPermissionException
            throw FileNoPermissionException(file)

        // Then sending a onFileAction event to all the applications
        for (app in sUpdate.applicationManager.applications)
            app.onFileAction(FileActionRenameEvent(sUpdate, file, dest))
    }

    /**
     * Try to delete a file
     *
     * @param file The file to delete
     */
    fun tryToDelete(file: File)
    {
        // Deleting the file, and if it failed
        if (!file.delete())
        // Deleting it at the end of the program
            file.deleteOnExit()

        // Then sending a onFileAction event to all the applications
        for (app in sUpdate.applicationManager.applications)
            app.onFileAction(FileActionEvent(sUpdate, FileAction.DELETE, file))
    }

    /**
     * Delete a file
     *
     * @param file The file to delete
     */
    @Throws(FileNoPermissionException::class)
    fun delete(file: File)
    {
        // Deleting the file, and if it failed
        if (!file.delete())
        // Throwing a no permission exception
            throw FileNoPermissionException(file)

        // Then sending a onFileAction event to all the applications
        for (app in sUpdate.applicationManager.applications)
            app.onFileAction(FileActionEvent(sUpdate, FileAction.DELETE, file))
    }

    /**
     * Stops the download pool and wait for its end
     */
    fun stop()
    {
        pool.shutdown()

        try
        {
            pool.awaitTermination(java.lang.Long.MAX_VALUE, TimeUnit.DAYS)
        }
        catch (e: InterruptedException)
        {
        }

        LOGGER.info("Total downloaded bytes: %d", sUpdate.values.getNumberOfTotalDownloadedBytes())
    }
}

/**
 * The FileAction
 *
 * The FileAction are actions that the FileManager class can do
 * to files, downloading, renaming, or deleting.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
enum class FileAction
{
    DOWNLOAD,
    RENAME,
    DELETE
}
