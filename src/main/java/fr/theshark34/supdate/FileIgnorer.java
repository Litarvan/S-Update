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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The FileIgnorer
 *
 * <p>
 *     This class contains a method to get a list of files to keep when
 *     deleting the useless files.
 * </p>
 *
 * @version 2.0-SNAPSHOT
 * @author TheShark34
 */
public class FileIgnorer {

    /**
     * The current SUpdate instance
     */
    private SUpdate su;

    /**
     * The list of files to ignore
     */
    private ArrayList<String> filesToIgnore;

    /**
     * Basic constructor
     *
     * @param su
     *            The current SUpdate instance
     */
    public FileIgnorer(SUpdate su) {
        this.su = su;
        // Initializing the list
        filesToIgnore = new ArrayList<String>();
    }

    /**
     * Creates the list of files to ignore
     *
     * @throws IOException If it failed to read the response or send the request
     */
    public void getFilesToIgnore() throws IOException {
        // Sending the filetoignore request
        BufferedReader br = Server.sendRequest(su, "filestoignore");

        // For each read line
        while(br.ready()) {
            // Getting the line
            String line = br.readLine();

            // Removing the slash if there is one at the end
            if(line.endsWith("/") || line.endsWith("\\"))
                line = line.substring(0, line.length() - 1);

            // Adding it to the file to ignore list
            filesToIgnore.add(line);
        }

        // Closing the reader
        br.close();
    }

    /**
     * Check if we need to ignore a file
     *
     * @param file
     *            The file to check
     * @return True if we need to, false if not
     */
    public boolean needToIgnore(File file) {
        // For each files to ignore
        for(String fileToIgnore : filesToIgnore)
            // If the file to ignore with a '/' at the end equals the file absolute path without the output folder absolute path at the start
            if(fileToIgnore.equals(file.getAbsolutePath().replace(su.getOutputFolder().getAbsolutePath(), "").replace("\\", "/").replaceFirst("/", "")))
                // Returning true
                return true;
        return false;
    }

}
