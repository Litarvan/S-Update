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
import java.util.ArrayList;

/**
 * An Util class
 *
 * <p>
 *     A Class containing some usefull methods
 * </p>
 *
 * @version 2.0-SNAPSHOT
 * @author TheShark34
 */
public class Util {

    /**
     * Check if a local file exists in the online file list
     *
     * @param su
     *            The current SUpdate instance to get the output folder
     * @param onlineFilesList
     *            The online file list
     * @param localFile
     *            The local file
     * @return True if it exists in, false if not
     */
    public static boolean contains(SUpdate su, ArrayList<OnlineFile> onlineFilesList, File localFile) {
        for(OnlineFile onlineFile : onlineFilesList) {
            File file = new File(su.getOutputFolder(), onlineFile.getFile());
            if(file.getAbsolutePath().equals(localFile.getAbsolutePath()))
                return true;
            else
                // TODO: Remove this test message
                System.out.println("\"" + file.getAbsolutePath() + "\" != \"" + localFile.getAbsolutePath() + "\"");
        }
        return false;
    }

    /**
     * Lists a folder recursively
     *
     * @param folder
     *            The folder to list
     * @return A list of the listed files
     */
    public static ArrayList<File> listFiles(File folder) {
        ArrayList<File> files = new ArrayList<File>();

        if(!folder.isDirectory())
            return files;

        File[] folderFiles = folder.listFiles();
        for(File f : folderFiles)
            if(f.isDirectory())
                files.addAll(listFiles(f));
            else
                files.add(f);

        return files;
    }

}
