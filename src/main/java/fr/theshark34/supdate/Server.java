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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The Server util class
 *
 * <p>
 *     Contains a method to send a request to the server
 * </p>
 *
 * @version 2.0-SNAPSHOT
 * @author TheShark34
 */
public class Server {

    /**
     * Gets a file of the server
     *
     * @param su
     *            The current SUpdate instance to get the base URL
     * @param file
     *            The file to gets
     * @throws IOException
     *            If it failed something
     * @return A BufferedReader for the file
     */
    public static BufferedReader getFile(SUpdate su, String file) throws IOException {
        URL url = new URL(su.getBaseURL() + "/" + file);
        File tmp = new File(System.getProperty("user.home") + "/.sutmp");
        BufferedReader br;

        Downloader.downloadFile(url, tmp);
        br = new BufferedReader(new FileReader(tmp));

        return br;
    }

    /**
     * Updates the server stats
     *
     * @param su
     *            The current SUpdate instance to get the base URL
     * @throws IOException
     *            If it failed something
     */
    public static void updateStats(SUpdate su) throws IOException {
        URL serverURL = new URL(su.getBaseURL() + "/server.php");
        HttpURLConnection connection = (HttpURLConnection) serverURL.openConnection();
        connection.setRequestMethod("POST");

        // Sending post request
        connection.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes("request=updateStats");
        wr.flush();
        wr.close();

        connection.connect();
    }

}
