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

/**
 * An Online File
 *
 * <p>
 *     A Class to stock the informations of an online file given by the Server
 * </p>
 *
 * @version 2.1-SNAPSHOT
 * @author TheShark34
 */
public class OnlineFile {

    /**
     * The file
     */
    private String file;

    /**
     * The last modified date of the file
     */
    private long lastModified = 0;

    /**
     * The MD5 of the file
     */
    private String md5;

    /**
     * Basic constructor (with last modified date)
     *
     * @param file
     *            The file
     * @param lastModified
     *            The last modified date of the file
     */
    public OnlineFile(String file, long lastModified) {
        this.file = file;
        this.lastModified = lastModified;
    }

    /**
     * Basic constructor (with MD5)
     *
     * @param file
     *            The file
     * @param md5
     *            The last modified date of the file
     */
    public OnlineFile(String file, String md5) {
        this.file = file;
        this.md5 = md5;
    }

    /**
     * Returns the file
     *
     * @return The file
     */
    public String getFile() {
        return this.file;
    }

    /**
     * Returns the last modified date of the file
     *
     * @return The last modified date of the file
     */
    public long getLastModified() {
        return this.lastModified;
    }

    /**
     * Returns the md5 of the file
     *
     * @return The md5 date of the file
     */
    public String getMD5() {
        return this.md5;
    }

}
