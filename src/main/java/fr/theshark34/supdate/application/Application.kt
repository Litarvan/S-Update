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
package fr.theshark34.supdate.application

/**
 * The Application class
 *
 * This is an Application. An application is a thing giving
 * superpowers to S-Update. It contains event with some
 * informations, to modify everything you want.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
abstract class Application
{
    /**
     * Returns the name of the application
     */
    abstract val name: String

    /**
     * Returns if the application need to be on the server
     */
    abstract val isServerRequired: Boolean

    /**
     * Called when the application is added to the list

     * @param event An event that contains some useful informations
     */
    abstract fun onInit(event: ApplicationEvent)

    /**
     * Called when the update is starting
     *
     * @param event An event that contains some useful informations
     */
    abstract fun onStart(event: ApplicationEvent)

    /**
     * Called when a file is being checked
     *
     * @param event An event that contains some useful informations
     *
     * @return The new check result
     */
    abstract fun onFileChecking(event: FileCheckingEvent): Boolean

    /**
     * When a file action is executed
     *
     * @param event An event that contains some useful informations
     */
    abstract fun onFileAction(event: FileActionEvent)

    /**
     * Called when the update is finished
     *
     * @param event An event that contains some useful informations
     */
    abstract fun onUpdateEnd(event: ApplicationEvent)
}
