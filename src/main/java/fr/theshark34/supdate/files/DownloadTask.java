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
package fr.theshark34.supdate.files;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * The Download Task
 *
 * <p>
 *     A Task that downloads a file to a destination.
 * </p>
 *
 * @version 3.0.0-SNAPSHOT
 * @author TheShark34
 */
public class DownloadTask implements Runnable {

    /**
     * The URL of the file to download
     */
    private URL fileUrl;

    /**
     * The destination file
     */
    private File dest;

    /**
     * Simple constructor
     *
     * @param fileUrl
     *            The URL of the file to download
     * @param dest
     *            The destination file
     */
    public DownloadTask(URL fileUrl, File dest) {
        this.fileUrl = fileUrl;
        this.dest = dest;
    }

    @Override
    public void run() {
        // Making the parent folders of the destination file
        dest.getParentFile().mkdirs();

        // Printing a message
        System.out.println("[S-Update] Downloading file " + fileUrl);

        try {
            // Creating the connection
            HttpURLConnection connection = (HttpURLConnection) fileUrl.openConnection();

            // Adding some user agents
            connection.addRequestProperty("User-Agent", "Mozilla/5.0");
            connection.addRequestProperty("User-Agent", "AppleWebKit/537.36 (KHTML, like Gecko)");
            connection.addRequestProperty("User-Agent", "Chrome/43.0.2357.124 Safari/537.36");

            // Creating the byte channel
            ReadableByteChannel rbc = Channels.newChannel(connection
                    .getInputStream());

            // Creating the output stream
            FileOutputStream fos = new FileOutputStream(dest);

            // Transfering the both channels
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            // Closing them
            fos.close();
            rbc.close();
        } catch (IOException e) {
            // If it failed printing a warning message
            System.out.println("[S-Update] WARNING : File " + fileUrl + " wasn't downloaded : " + e);
        }
    }

}
