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

import fr.theshark34.supdate.SUpdate
import java.util.*

/**
 * The Application Manager
 *
 * This is the application manager, where all the applications
 * are registered. It call their events, and things like this.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class ApplicationManager
{
    /**
     * The list of all the loaded applications
     */
    val applications = ArrayList<Application>()

    /**
     * Load, then add an application
     *
     * @param application The application to add
     */
    fun addApplication(sUpdate: SUpdate, application: Application)
    {
        // Adding the application to the list
        applications.add(application)

        // Then sending the onInit event
        application.onInit(ApplicationEvent(sUpdate))
    }
}
