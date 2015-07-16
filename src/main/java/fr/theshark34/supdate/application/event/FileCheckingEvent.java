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
 * The FileChecking Event
 *
 * <p>
 *     This event is given in the onFileCheck application event
 *     to give some informations about the checking file, and
 *     things like this.
 * </p>
 *
 * @version 3.0.0-BETA
 * @author TheShark34
 */
public class FileCheckingEvent extends ApplicationEvent {

    /**
     * The relative path of the checked file
     */
    private String checkedFilePath;

    /**
     * The result of the check
     */
    private boolean checkResult;

    /**
     * The FileChecking event
     *
     * @param sUpdate
     *            The current SUpdate instance
     * @param checkedFilePath
     *            The relative path of the checked file
     * @param checkResult
     *            The result of the check
     */
    public FileCheckingEvent(SUpdate sUpdate, String checkedFilePath, boolean checkResult) {
        super(sUpdate);

        this.checkedFilePath = checkedFilePath;
        this.checkResult = checkResult;
    }

    /**
     * Return the relative path of the checked file
     *
     * @return The checked file path
     */
    public String getCheckedFilePath() {
        return this.checkedFilePath;
    }

    /**
     * Return the result of the check
     *
     * @return The check result
     */
    public boolean getCheckResult() {
        return this.checkResult;
    }

}
