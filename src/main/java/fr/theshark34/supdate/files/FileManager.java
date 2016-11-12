/*
 * Copyright 2015 TheShark34
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
package fr.theshark34.supdate.files;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.supdate.application.Application;
import fr.theshark34.supdate.application.event.fileaction.FileActionDownloadEvent;
import fr.theshark34.supdate.application.event.fileaction.FileActionEvent;
import fr.theshark34.supdate.application.event.fileaction.FileActionRenameEvent;
import fr.theshark34.supdate.exception.FileNoPermissionException;

import static fr.theshark34.supdate.SUpdate.logger;

/**
 * The FileManager
 *
 * <p>
 *     The FileManager class is a class to do some actions to
 *     files, like downloading, renaming, or deleting a file. It
 *     should always be used to do that things because it calls the
 *     onFileAction event of the applications.
 * </p>
 *
 * @version 3.0.0-BETA
 * @author TheShark34
 */
public class FileManager {

    /**
     * The current SUpdate instance
     */
    private SUpdate sUpdate;

    /**
     * The executor pool for Multi-Threaded download
     */
    private ExecutorService pool = Executors.newFixedThreadPool(10);

    /**
     * Simple constructor
     *
     * @param sUpdate
     *            The current SUpdate object
     */
    public FileManager(SUpdate sUpdate) {
        this.sUpdate = sUpdate;
    }

    /**
     * Download a file with a Multi-Threaded system
     *
     * @param fileUrl
     *            The URL of the file to download
     * @param outputFile
     *            The output of the file
     */
    public void download(URL fileUrl, File outputFile) {
        pool.submit(new DownloadTask(fileUrl, outputFile));

        // Then sending a onFileAction event to all the applications
        for(Application app : sUpdate.getApplicationManager().getApplications())
            app.onFileAction(new FileActionDownloadEvent(sUpdate, outputFile, fileUrl));
    }

    /**
     * Try to rename a file
     *
     * @param file
     *            The file to rename
     * @param dest
     *            The destination file
     */
    public void tryToRename(File file, File dest) {
        // Renaming the file
        file.renameTo(dest);

        // Then sending a onFileAction event to all the applications
        for(Application app : sUpdate.getApplicationManager().getApplications())
            app.onFileAction(new FileActionRenameEvent(sUpdate, file, dest));
    }

    /**
     * Rename a file
     *
     * @param file
     *            The file to rename
     * @param dest
     *            The destination file
     * @throws FileNoPermissionException If it failed to rename the file
     */
    public void rename(File file, File dest) throws FileNoPermissionException {
        // Renaming the file and if it failed
        if(!file.renameTo(dest))
            // Throwing a new FileNoPermissionException
            throw new FileNoPermissionException(file);

        // Then sending a onFileAction event to all the applications
        for(Application app : sUpdate.getApplicationManager().getApplications())
            app.onFileAction(new FileActionRenameEvent(sUpdate, file, dest));
    }

    /**
     * Try to delete a file
     *
     * @param file
     *            The file to delete
     */
    public void tryToDelete(File file) {
        // Deleting the file, and if it failed
        if(!file.delete())
            // Deleting it at the end of the program
            file.deleteOnExit();

        // Then sending a onFileAction event to all the applications
        for(Application app : sUpdate.getApplicationManager().getApplications())
            app.onFileAction(new FileActionEvent(sUpdate, FileAction.DELETE, file));
    }

    /**
     * Delete a file
     *
     * @param file
     *            The file to delete
     */
    public void delete(File file) throws FileNoPermissionException {
        // Deleting the file, and if it failed
        if(!file.delete())
            // Throwing a no permission exception
            throw new FileNoPermissionException(file);

        // Then sending a onFileAction event to all the applications
        for(Application app : sUpdate.getApplicationManager().getApplications())
            app.onFileAction(new FileActionEvent(sUpdate, FileAction.DELETE, file));
    }

    /**
     * Stops the download pool and wait for its end
     */
    public void stop() {
        pool.shutdown();

        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
        }
        
        logger.info("Total downloaded bytes: %d", BarAPI.getNumberOfTotalDownloadedBytes());
    }

}
