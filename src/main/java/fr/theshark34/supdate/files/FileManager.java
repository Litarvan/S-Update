package fr.theshark34.supdate.files;

import fr.theshark34.supdate.application.Application;
import fr.theshark34.supdate.application.ApplicationManager;
import fr.theshark34.supdate.exception.FileNoPermissionException;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The FileManager
 *
 * <p>
 *     The FileManager class is a class to do some actions to
 *     files, like downloading, renaming, or removing. It should
 *     always be used to do that things because it calls the
 *     onFileAction event of the applications.
 * </p>
 *
 * @version 3.0.0-SNAPSHOT
 * @author TheShark34
 */
public class FileManager {

    /**
     * The current application manager
     */
    private ApplicationManager applicationManager;

    /**
     * The executor pool for Multi-Threaded download
     */
    private ExecutorService pool = Executors.newFixedThreadPool(10);

    /**
     * Simple constructor
     *
     * @param applicationManager
     *            The current application manager
     */
    public FileManager(ApplicationManager applicationManager) {
        this.applicationManager = applicationManager;
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
        for(Application app : applicationManager.getApplications())
            app.onFileAction(FileAction.DOWNLOAD, fileUrl, outputFile);
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
        for(Application app : applicationManager.getApplications())
            app.onFileAction(FileAction.RENAME, file, dest);
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
        for(Application app : applicationManager.getApplications())
            app.onFileAction(FileAction.RENAME, file, dest);
    }

}
