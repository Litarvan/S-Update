package fr.theshark34.supdate.application;

import java.io.File;

/**
 * The Application class
 *
 * <p>
 *     This is an Application. An application is a thing giving
 *     superpowers to S-Update. It contains event with some
 *     informations, to modify everything you want.
 * </p>
 *
 * @version 3.0.0-SNAPSHOT
 * @author TheShark34
 */
public abstract class Application {

    /**
     * Returns the name of the application
     *
     * @return The application name
     */
    public abstract String getName();

    /**
     * Called when the application is added to the list
     */
    public abstract void onInit();

    /**
     * Called when the update is starting
     */
    public abstract void onStart();

    /**
     * Called when a file is being checked
     *
     * @param file
     *            The file being checked
     * @param checkResult
     *            The result that the checker gave
     * @return The new check result
     */
    public abstract boolean onFileChecking(File file, boolean checkResult);

    /**
     * When a file action is executed
     *
     * @param args
     *            The args given by the action
     */
    public abstract void onFileAction(Object... args);

    /**
     * Called in loop when a file is being download
     *
     * @param downloadingFile
     *            The file being download
     * @param destFile
     *            The destination file
     */
    public abstract void whileDownloading(File downloadingFile, File destFile, long downloaded, long toDownload);

    /**
     * Called when the update is finished
     */
    public abstract void onUpdateEnd();

}
