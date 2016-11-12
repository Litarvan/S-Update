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
import fr.theshark34.supdate.application.event.FileCheckingEvent;
import fr.theshark34.supdate.application.event.fileaction.FileActionEvent;

/**
 * The Application class
 *
 * <p>
 *     This is an Application. An application is a thing giving
 *     superpowers to S-Update. It contains event with some
 *     informations, to modify everything you want.
 * </p>
 *
 * @version 3.0.0-BETA
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
     * Returns if the application need to be on the server
     *
     * @return If the application need to be on the server
     */
    public abstract boolean isServerRequired();

    /**
     * Called when the application is added to the list
     *
     * @param event
     *            An event that contains some useful informations
     */
    public abstract void onInit(ApplicationEvent event);

    /**
     * Called when the update is starting
     *
     * @param event
     *            An event that contains some useful informations
     */
    public abstract void onStart(ApplicationEvent event);

    /**
     * Called when a file is being checked
     *
     * @param event
     *            An event that contains some useful informations
     *
     * @return The new check result
     */
    public abstract boolean onFileChecking(FileCheckingEvent event);

    /**
     * When a file action is executed
     *
     * @param event
     *            An event that contains some useful informations
     */
    public abstract void onFileAction(FileActionEvent event);

    /**
     * Called when the update is finished
     *
     * @param event
     *            An event that contains some useful informations
     */
    public abstract void onUpdateEnd(ApplicationEvent event);

}
