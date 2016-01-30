/*
 * Copyright 2015-2016 Adrien Navratil
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
package fr.theshark34.supdate

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL
import kotlin.text.endsWith

/**
 * The ServerRequester
 *
 * This is a class to send a request to the server. Then
 * it will translate the JSON with the given model.
 *
 * @author Litarvan
 * @version 3.2.0-BETA
 */
class ServerRequester(val sUpdate: SUpdate)
{
    /**
     * Sends a request to the server
     *
     * @param request The relative URL of the request
     */
    @Throws(IOException::class)
    fun sendRequest(request: String)
            = send(request, null, null, null, false)

    /**
     * Sends a request to the server, and parse its response as a JSON
     *
     * @param request The relative URL of the request
     * @param model   The model object for the JSON parsing
     */
    @Throws(IOException::class)
    fun sendRequest(request: String, model: Class<*>): Any?
            = send(request, model, null, null, false)

    /**
     * Sends a request to the server, and parse its response as a JSON
     *
     * @param request The relative URL of the request
     * @param type    The type of the model object for the JSON parsing
     */
    @Throws(IOException::class)
    fun sendRequest(request: String, type: Type): Any?
            = send(request, null, type, null, false)

    /**
     * Sends a post request to the server
     *
     * @param request The relative URL of the request
     */
    @Throws(IOException::class)
    fun sendPostRequest(request: String)
            = send(request, null, null, null, true)

    /**
     * Sends a post request to the server, and parse its response as a JSON
     *
     * @param request The relative URL of the request
     * @param model   The model object for the JSON parsing
     */
    @Throws(IOException::class)
    fun sendPostRequest(request: String, model: Class<*>): Any?
            = send(request, model, null, null, true)

    /**
     * Sends a post request to the server, and parse its response as a JSON
     *
     * @param request The relative URL of the request
     * @param type    The type of the model object for the JSON parsing
     */
    @Throws(IOException::class)
    fun sendPostRequest(request: String, type: Type): Any?
            = send(request, null, type, null, true)

    /**
     * Sends a request to the server with post data, and parse its response as a JSON
     *
     * @param request  The relative URL of the request
     * @param model    The model object for the JSON parsing
     * @param postData The post data to send
     */
    @Throws(IOException::class)
    fun sendPostRequest(request: String, model: Class<*>, postData: ByteArray): Any?
            = send(request, model, null, postData, true)

    /**
     * Sends a request to the server with post data, and parse its response as a JSON
     *
     * @param request  The relative URL of the request
     * @param type     The type of the model object for the JSON parsing
     * @param postData The post data to send
     */
    @Throws(IOException::class)
    fun sendPostRequest(request: String, type: Type, postData: ByteArray): Any?
            = send(request, null, type, postData, true)

    @Throws(IOException::class)
    private fun send(request: String, model: Class<*>?, type: Type?, postData: ByteArray?, post: Boolean): Any?
    {
        // Creating the URL
        val requestUrl = URL(sUpdate.serverUrl + (if (sUpdate.serverUrl.endsWith("/")) "" else "/") + "index.php/" + request)

        // Creating the HTTP Connection
        val connection = requestUrl.openConnection() as HttpURLConnection

        // Adding some user agents
        connection.addRequestProperty("User-Agent", "Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.124 Safari/537.36")

        // Setting post enabled if needed
        if (post)
            connection.requestMethod = "POST"

        // Writing the post data if needed
        if (postData != null)
        {
            connection.doOutput = true
            val output = connection.outputStream
            output.write(postData)
        }

        // If we don't need to read the response (the model AND the type are null)
        if (model == null && type == null)
        // Returning nothing
            return null

        // Creating the buffered reader
        val br = BufferedReader(InputStreamReader(connection.inputStream))

        // Reading the response
        var response = ""
        var currentLine: String? = ""

        while (currentLine != null)
        {
            currentLine = br.readLine()
            response += currentLine
        }

        // Parsing the JSON
        val gson = Gson()
        val createdObject: Any?
        try
        {
            createdObject = gson.fromJson<Any>(response, model ?: type)
        }
        catch (e: JsonSyntaxException)
        {
            return response
        }

        if (createdObject == null)
            return response
        else
            return createdObject
    }
}
