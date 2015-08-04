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
package fr.theshark34.supdate.check.md5;

import fr.theshark34.supdate.check.FileInfos;

/**
 * The MD5 FileInfos
 *
 * <p>
 *    This is the file info for the MD5 Check Method, containing
 *    the infos about a file, its name and its MD5.
 * </p>
 *
 * @version 3.0.0-BETA
 * @author TheShark34
 */
public class MD5FileInfos extends FileInfos {

    /**
     * The file MD5
     */
    private String md5;

    /**
     * The MD5 FileInfos
     *
     * @param fileRelativePath
     *            The relative path of the file
     * @param md5
     *            The file MD5
     */
    public MD5FileInfos(String fileRelativePath, String md5) {
        super(fileRelativePath);

        this.md5 = md5;
    }

    /**
     * Return the file MD5
     *
     * @return The file MD5
     */
    public String getMD5() {
        return this.md5;
    }

}
