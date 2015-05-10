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
 * A Zip File
 *
 * <p>
 *     A class containing the infos of an online zip file : His name, and
 *     a file of it that will be checked to know if we need to download
 *     the zip.
 * </p>
 *
 * @version 2.2.0-SNAPSHOT
 * @author TheShark34
 */
public class ZipFile {

    /**
     * Relative path of the zip
     */
    private String zip;

    /**
     * A file of the zip that will be checked to know if we need to
     * download the zip
     */
    private String anyFile;

    /**
     * ZipFile constructor
     *
     * @param zip
     *            Relative path of the zip
     * @param anyFile
     *            A file of the zip that will be checked to know if
     *            we need to download the zip
     */
    public ZipFile(String zip, String anyFile) {
        this.zip = zip;
        this.anyFile = anyFile;
    }

    /**
     * Returns the relative path of the zip
     *
     * @return The zip file
     */
    public String getZip() {
        return zip;
    }

    /**
     * Returns any file of the zip that will be checked to know if we
     * need to download the zip
     *
     * @return Any file of the zip
     */
    public String getAnyFile() {
        return anyFile;
    }

}
