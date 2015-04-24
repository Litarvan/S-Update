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
package fr.theshark34.supdate;

import java.io.File;

/**
 * A File To Update
 *
 * <p>
 *     A Class to stock the informations of a file that need to be downloaded / unzipped / removed
 * </p>
 *
 * @version 2.0-SNAPSHOT
 * @author TheShark34
 */
public class FileToUpdate {

    /**
     * The DOWNLOAD action
     */
    public static final int DOWNLOAD = 0;

    /**
     * The REMOVE action
     */
    public static final int REMOVE = 2;

    /**
     * The file to update
     */
    private File file;

    /**
     * The action to do, DOWNLOAD / REMOVE
     */
    private int action;

    /**
     * The last modified date to set after downloading the file (if the action is DOWNLOADING)
     */
    private long lastModified;

    /**
     * File to download constructor
     *
     * @param su
     *            The SUpdate object to get the output folder
     * @param onlineFile
     *            The online file to download
     */
    public FileToUpdate(SUpdate su, OnlineFile onlineFile) {
        this.file = new File(su.getOutputFolder(), onlineFile.getFile());
        this.lastModified = onlineFile.getLastModified();
    }

    /**
     * File to remove constructor
     *
     * @param file
     *            The file to remove
     */
    public FileToUpdate(File file) {
        this.file = file;
        this.action = REMOVE;
    }

    /**
     * Returns the file to update
     *
     * @return The file to update
     */
    public File getFile() {
        return this.file;
    }

    /**
     * Returns the last modified date to set after downloading the file
     *
     * @return The last modified date to set
     */
    public long getLastModified() {
        return this.lastModified;
    }

    /**
     * Returns the action to do, DOWNLOAD / REMOVE
     *
     * @return The action to do
     */
    public int getAction() {
        return this.action;
    }

}
