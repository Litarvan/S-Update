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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
     * Sends a request to the server
     *
     * @param su
     *            The current SUpdate instance to get the base URL
     * @param request
     *            The request to send
     * @throws IOException
     *            If it failed something
     * @return A BufferedReader for the request response
     */
    public static BufferedReader sendRequest(SUpdate su, String request) throws IOException {
        URL serverURL = new URL(su.getBaseURL() + "/server.php");
        HttpURLConnection connection = (HttpURLConnection) serverURL.openConnection();
        connection.setRequestMethod("POST");
        // Send post request
        connection.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes("request=" + request);
        wr.flush();
        wr.close();
        connection.connect();
        return new BufferedReader(new InputStreamReader(connection.getInputStream()));
    }

}
