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

import com.google.gson.Gson;
import fr.theshark34.supdate.models.SizeResponse;
import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fr.theshark34.supdate.application.Application;
import fr.theshark34.supdate.application.event.ApplicationEvent;
import fr.theshark34.supdate.application.event.FileCheckingEvent;
import fr.theshark34.supdate.check.CheckMethod;
import fr.theshark34.supdate.check.FileInfos;
import fr.theshark34.supdate.check.md5.MD5CheckMethod;
import fr.theshark34.supdate.exception.BadServerResponseException;
import fr.theshark34.supdate.exception.BadServerVersionException;
import fr.theshark34.supdate.exception.ServerDisabledException;
import fr.theshark34.supdate.exception.ServerMissingSomethingException;
import fr.theshark34.supdate.exception.UnableToCheckException;
import fr.theshark34.supdate.models.CheckResponse;
import fr.theshark34.supdate.models.StateResponse;
import fr.theshark34.supdate.models.VersionResponse;

import static fr.theshark34.supdate.SUpdate.logger;

/**
 * The Updater object
 *
 * <p>
 *    The Updater just check all, and update all !
 * </p>
 *
 * @version 3.0.0-BETA
 * @author TheShark34
 */
public class Updater {

    /**
     * The folder where are the files
     */
    public static final String FILES_FOLDER = "files";

    /**
     * The default check method
     */
    public static final CheckMethod DEFAULT_CHECK_METHOD = new MD5CheckMethod();

    /**
     * The current SUpdate instance
     */
    private SUpdate sUpdate;

    /**
     * The Updater
     *
     * @param sUpdate
     *            The current SUpdate instance
     */
    public Updater(SUpdate sUpdate) {
        this.sUpdate = sUpdate;
    }

    /**
     * Starts the update ! Call the applications, check the files
     * download it, etc...
     *
     * @throws BadServerResponseException
     *            If the response of a request wasn't JSON, or things like that
     * @throws BadServerVersionException
     *            If the version isn't at least the min version
     * @throws ServerMissingSomethingException
     *            If the server is missing the check method or an application
     * @throws IOException
     *            If it failed to do the request
     */
    public void start() throws BadServerResponseException, ServerDisabledException, BadServerVersionException, ServerMissingSomethingException, IOException {
        // Printing a beautiful message
        printInfos();

        // Saving the start time
        long startTime = System.currentTimeMillis();

        // Checking the server state
        checkState();

        // Checking the server version
        checkVersion();

        // Checking the check method and the applications
        checkCheckMethodAndApplications();

        // Sending a request to update the stats
        sUpdate.getServerRequester().sendPostRequest("stats/update");

        // For each application
        for(Application app : sUpdate.getApplicationManager().getApplications())
            // Sending the onStart event
            app.onStart(new ApplicationEvent(sUpdate));

        logger.info("Listing the files");

        // Creating the files list
        List<FileInfos> fileList = createFileList();

        // Creating the list of files to download
        Map<URL, File> filesToDownload = new HashMap<URL, File>();

        // Creating the list of files to download with their relative path
        List<String> filesPaths = new ArrayList<String>();

        logger.info("Checking them");

        // For each file infos
        for(FileInfos fileInfos : fileList) {
            // Getting their check result
            boolean checkResult;

            try {
                checkResult = sUpdate.getCheckMethod().checkFile(sUpdate, fileInfos);
            } catch (UnableToCheckException e) {
                // If it failed printing the error
                e.printStackTrace();

                // And stopping
                return;
            }

            // Sending it to all the applications
            for(Application app : sUpdate.getApplicationManager().getApplications())
                checkResult = app.onFileChecking(new FileCheckingEvent(sUpdate, fileInfos.getFileRelativePath(), checkResult));

            // If we need to download the file
            if (checkResult) {
                // Getting its infos
                URL fileURL = new URL((sUpdate.getServerUrl() + (sUpdate.getServerUrl().endsWith("/") ? "" : "/") + FILES_FOLDER + "/" + fileInfos.getFileRelativePath()).replaceAll(" ", "%20"));
                File localFile = new File(sUpdate.getOutputFolder(), fileInfos.getFileRelativePath());

                // Adding them to the filesToDownload map
                filesToDownload.put(fileURL, localFile);

                // Adding it to the files paths list
                filesPaths.add(fileInfos.getFileRelativePath());
            }
        }

        // Setting the BarAPI 'numberOfFileToDownload' variable to the size of the filesToDownload list
        BarAPI.setNumberOfFileToDownload(filesToDownload.size());

        logger.info("%d files were checked, %s", fileList.size(), (filesToDownload.size() == 0 ? "nothing to download" : "need to download " + filesToDownload.size() + " of them."));

        // Setting the cookie manager
        CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        // If we need to download files
        if(filesPaths.size() > 0) {
            // Adding to the BarAPI 'numberOfTotalBytesToDownload' variable, the size of the file to download
            logger.info("Requesting bytes to download... ");
            getBytesToDownload(filesPaths);
            logger.info("Done: %d", BarAPI.getNumberOfTotalBytesToDownload());

            logger.info("Starting download the files");
        }
        
        // Downloading the files
        for(Entry<URL, File> entry : filesToDownload.entrySet())
            sUpdate.getFileManager().download(entry.getKey(), entry.getValue());
        
        // Terminating
        sUpdate.getFileManager().stop();

        // For each application
        for(Application app : sUpdate.getApplicationManager().getApplications())
            // Sending the onUpdateEnd event
            app.onUpdateEnd(new ApplicationEvent(sUpdate));

        // Printing the total time
        printTotalTime(startTime);
    }

    /**
     * Print infos about some things about... life... and weather...
     */
    private void printInfos() {
    	logger.info(SUpdate.VERSION);
    	logger.info("Current time is %s", new Date(System.currentTimeMillis()).toString());
    	logger.info("Starting updating...");
    	logger.info("    Server URL: %s", sUpdate.getServerUrl());
    	logger.info("    Output Dir: %s", sUpdate.getOutputFolder().getAbsolutePath());
    }

    private void checkState() throws BadServerResponseException, IOException, ServerDisabledException {
    	logger.info("Connecting to the server... ");

        // Sending a get state request to check the server state
        Object stateResponse = sUpdate.getServerRequester().sendPostRequest("server/is-enabled", StateResponse.class);

        // If the response is a string (so its the raw response because the JSON parse failed)
        if(stateResponse instanceof String)
            // Throwing a BadServerResponse exception
            throw new BadServerResponseException((String) stateResponse);

        // Getting the state
        boolean enabled = ((StateResponse) stateResponse).isEnabled();

        logger.info("Server " + (enabled ? "enabled" : "disabled !"));

        // If the server is disabled
        if(!enabled)
            // Throwing a server disabled exception
            throw new ServerDisabledException();
    }

    /**
     * Checks the server version
     *
     * @throws BadServerResponseException
     *            If the response wasn't JSON, or things like that
     * @throws BadServerVersionException
     *            If the version isn't at least the min version
     * @throws IOException
     *            If it failed to do the request
     */
    private void checkVersion() throws BadServerResponseException, BadServerVersionException, IOException {
        // Sending a version request to check the server version and ping it
        Object versionResponse = sUpdate.getServerRequester().sendPostRequest("server/version", VersionResponse.class);

        // If the response is a string (so its the raw response because the JSON parse failed)
        if(versionResponse instanceof String)
            // Throwing a BadServerResponse exception
            throw new BadServerResponseException((String) versionResponse);

        // Separating the revision and the version
        String[] splittedVersion = ((VersionResponse) versionResponse).getVersion().split("-");
        int version = Integer.parseInt(splittedVersion[0].replaceAll("\\.", ""));
        String revision = splittedVersion[1];

        String[] splittedMinVersion = SUpdate.SERV_MIN_VERSION.split("-");
        int minVersion = Integer.parseInt(splittedMinVersion[0].replaceAll("\\.", ""));
        String minRevision = splittedMinVersion[1];

        // Then checking the revision and the version
        if(!revision.equals(minRevision))
            throw new BadServerVersionException(minRevision, revision, true);
        else if (version < minVersion)
            throw new BadServerVersionException(splittedMinVersion[0], splittedVersion[0], false);

        logger.info("Version : " + ((VersionResponse) versionResponse).getVersion());
    }

    /**
     * Checks if the check method is installed on the server
     */
    private void checkCheckMethodAndApplications() throws BadServerResponseException, ServerMissingSomethingException, IOException {
        // Getting the check method name
        String checkMethodName = sUpdate.getCheckMethod().getName();

        // Sending a check check method request to the server
        Object response = sUpdate.getServerRequester().sendPostRequest("server/check/checkmethod/" + checkMethodName.replaceAll(" ", "%20"), CheckResponse.class);

        // If the response is a string (so its the raw response because the JSON parse failed)
        if(response instanceof String)
            // Throwing a BadServerResponse exception
            throw new BadServerResponseException((String) response);

        // If the check method is not present on the server
        if(!((CheckResponse) response).isPresent())
            // Throwing a new ServerMissingSomething Exception
            throw new ServerMissingSomethingException("the Check Method " + checkMethodName);

        logger.info("CheckMethod : " + checkMethodName);

        String appsList = "";
        
        // For each applications
        for(int i = 0; i < sUpdate.getApplicationManager().getApplications().size(); i++) {
            // Getting its name
            String applicationName = sUpdate.getApplicationManager().getApplications().get(i).getName();

            // If it is server required
            if(sUpdate.getApplicationManager().getApplications().get(i).isServerRequired()) {
                // Sending a check application request to the server
                response = sUpdate.getServerRequester().sendPostRequest("server/check/application/" + applicationName.replaceAll(" ", "%20"), CheckResponse.class);

                // If the response is a string (so its the raw response because the JSON parse failed)
                if (response instanceof String)
                    // Throwing a BadServerResponse exception
                    throw new BadServerResponseException((String) response);

                // If the application is not present on the server
                if (!((CheckResponse) response).isPresent())
                    // Throwing a new ServerMissingSomething Exception
                    throw new ServerMissingSomethingException("the application " + applicationName);
            }
            
            if(i + 1 < sUpdate.getApplicationManager().getApplications().size())
                appsList += applicationName + ", ";
            else
                appsList += applicationName + ".";
        }
        
        logger.info("Applications : %s", appsList);

        // If there is no application
        if(sUpdate.getApplicationManager().getApplications().size() == 0)
            // Printing 'No application'
        	logger.info("No application");
    }

    /**
     * Create the file list
     *
     * @return The list of the files
     */
    @SuppressWarnings("unchecked")
	private List<FileInfos> createFileList() throws IOException, BadServerResponseException {
        // Sending a list files request to the server
        Object response = sUpdate.getServerRequester().sendPostRequest("server/list/" + sUpdate.getCheckMethod().getName().replaceAll(" ", "%20"), sUpdate.getCheckMethod().getListType());

        // If the response is a string (so its the raw response because the JSON parse failed)
        if(response instanceof String)
            // Throwing a BadServerResponse exception
            throw new BadServerResponseException((String) response);

        return (List<FileInfos>) response;
    }

    private void getBytesToDownload(List<String> filesToDownload) throws IOException, BadServerResponseException {
        // Getting GSON
        Gson gson = new Gson();

        // Sending a get total bytes request to the server
        Object response = sUpdate.getServerRequester().sendPostRequest("server/size", SizeResponse.class, gson.toJson(filesToDownload).replaceAll(" ", "%20").getBytes());

        // If the response is a string (so its the raw response because the JSON parse failed)
        if(response instanceof String)
            // Throwing a BadServerResponse exception
            throw new BadServerResponseException((String) response);

        // Setting it
        BarAPI.setNumberOfTotalBytesToDownload(((SizeResponse) response).getSize());
    }

    /**
     * Print the total update time
     *
     * @param startTime
     *            The update start time
     */
    private void printTotalTime(long startTime) {
        long totalTime = System.currentTimeMillis() - startTime;
        int seconds = (int) (totalTime / 1000) % 60;
        int minutes = (int) ((totalTime / (1000 * 60)) % 60);
        int hours   = (int) ((totalTime / (1000 * 60 * 60)) % 24);
        String strTime = hours + " hours " + minutes + " minutes " + seconds + " seconds and " + totalTime % 1000 + " milliseconds.";
        logger.info("Update finished, total time : " + strTime);
    }

}
