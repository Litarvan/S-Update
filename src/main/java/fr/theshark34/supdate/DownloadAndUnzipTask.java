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
import java.io.IOException;
import java.net.URL;

/**
 * The Download Task
 *
 * <p>
 *     A Task that download and unzip a zip file
 * </p>
 *
 * @version 2.2.0-SNAPSHOT
 * @author TheShark34
 */
public class DownloadAndUnzipTask implements Runnable {

    /**
     * The URL of the file
     */
    private URL url;

    /**
     * The file destination
     */
    private File output;

    /**
     * Basic constructor
     *
     * @param url
     *            The URL of the file
     * @param output
     *            The file destination
     */
    public DownloadAndUnzipTask(URL url, File output) {
        this.url = url;
        this.output = output;
    }

    @Override
    public void run() {
        // Printing a message
        System.out.println("[S-Update] Downloading file " + url);

        // Downloading the file
        try {
            Downloader.downloadFile(url, output);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Printing a finished message
            System.out
                    .println("[S-Update] Finished to download file "
                            + url);

            // Printing a unzipping message
            System.out.println("[S-Update] Unzipping file " + output.getAbsolutePath());

            // Unzipping the file
            try {
                Util.unzip(output);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Deleting the zip
            if(!output.delete())
                output.deleteOnExit();
        }
    }

}
