package fr.theshark34.supdate;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import fr.theshark34.supdate.exception.BadServerResponseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The ServerRequester
 *
 * <p>
 *    This is a class to send a request to the server. Then
 *    it will translate the JSON with the given model.
 * </p>
 *
 * @version 3.0.0-BETA
 * @author TheShark34
 */
public class ServerRequester {

    /**
     * The current SUpdate instance
     */
    private SUpdate sUpdate;

    /**
     * The ServerRequester
     *
     * @param sUpdate
     *            The current SUpdate instance
     */
    public ServerRequester(SUpdate sUpdate) {
        this.sUpdate = sUpdate;
    }

    /**
     * Sends a request to the server
     *
     * @param request
     *            The relative URL of the request
     * @param model
     *            The model object for the JSON parsing
     */
    public Object sendRequest(String request, Class model, Type type) throws IOException {
        // Creating the URL
        URL requestUrl = new URL(sUpdate.getServerUrl() + (sUpdate.getServerUrl().endsWith("/") ? "" : "/") + request);

        // Creating the HTTP Connection
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();

        // Adding some user agents
        connection.addRequestProperty("User-Agent", "Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.124 Safari/537.36");

        // Creating the buffered reader
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        // Reading the response
        String response = "";
        String currentLine;

        while((currentLine = br.readLine()) != null)
            response += currentLine;

        // Parsing the JSON
        Gson gson = new Gson();
        Object createdObject;
        try {
            createdObject = gson.fromJson(response, model == null ? type : model);
        } catch (JsonSyntaxException e) {
            return response;
        }

        if(createdObject == null)
            return response;
        else
            return createdObject;
    }

}
