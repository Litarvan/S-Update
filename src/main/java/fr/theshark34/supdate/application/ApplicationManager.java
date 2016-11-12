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

import java.util.ArrayList;

import fr.theshark34.supdate.SUpdate;
import fr.theshark34.supdate.application.event.ApplicationEvent;

/**
 * The Application Manager
 *
 * <p>
 *     This is the application manager, where all the applications
 *     are registered. It call their events, and things like this.
 * </p>
 *
 * @version 3.0.0-BETA
 * @author TheShark34
 */
public class ApplicationManager {

    /**
     * The list of all the loaded applications
     */
    private ArrayList<Application> applications = new ArrayList<Application>();

    /**
     * Load, then add an application
     *
     * @param application
     *            The application to add
     */
    public void addApplication(SUpdate sUpdate, Application application) {
        // Adding the application to the list
        applications.add(application);

        // Then sending the onInit event
        application.onInit(new ApplicationEvent(sUpdate));
    }

    /**
     * Returns the list of all the loaded applications
     *
     * @return The list of applications
     */
    public ArrayList<Application> getApplications() {
        return applications;
    }

}
