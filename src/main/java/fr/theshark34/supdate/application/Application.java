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
package fr.theshark34.supdate.application;

import fr.theshark34.supdate.application.event.ApplicationEvent;
import fr.theshark34.supdate.application.event.fileaction.FileActionEvent;
import fr.theshark34.supdate.application.event.FileCheckingEvent;
import fr.theshark34.supdate.application.event.FileDownloadingEvent;

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
    public abstract void onInit(ApplicationEvent event);

    /**
     * Called when the update is starting
     */
    public abstract void onStart(ApplicationEvent event);

    /**
     * Called when a file is being checked
     *
     * @param file
     *            The file being checked
     * @param checkResult
     *            The result that the checker gave
     * @return The new check result
     */
    public abstract boolean onFileChecking(FileCheckingEvent event);

    /**
     * When a file action is executed
     *
     * @param args
     *            The args given by the action
     */
    public abstract void onFileAction(FileActionEvent event);

    /**
     * Called in loop when a file is being download
     *
     * @param downloadingFile
     *            The file being download
     * @param destFile
     *            The destination file
     */
    public abstract void whileDownloading(FileDownloadingEvent event);

    /**
     * Called when the update is finished
     */
    public abstract void onUpdateEnd(ApplicationEvent event);

}
