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
package fr.theshark34.supdate.application.event;

import fr.theshark34.supdate.SUpdate;

/**
 * The Application Event
 *
 * <p>
 *     This is an event for the application, given to give
 *     some informations to the Application when an event
 *     is called.
 * </p>
 *
 * This class is the parent class for all the event
 *
 * @version 3.0.0-BETA
 * @author TheShark34
 */
public class ApplicationEvent {

    /**
     * The current SUpdate instance
     */
    private SUpdate sUpdate;

    /**
     * The Application Event
     *
     * @param sUpdate
     *            The current SUpdate instance
     */
    public ApplicationEvent(SUpdate sUpdate) {
        this.sUpdate = sUpdate;
    }

    /**
     * Return the current SUpdate instance
     *
     * @return The SUpdate instance
     */
    public SUpdate getSUpdate() {
        return this.sUpdate;
    }

}
