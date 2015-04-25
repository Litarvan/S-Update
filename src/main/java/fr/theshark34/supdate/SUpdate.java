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
import java.net.URL;
import java.util.ArrayList;

/**
 * The main class - Initialize it with the url of the update folder and
 * the output folder to start updating !
 *
 * @version 2.0-SNAPSHOT
 * @author TheShark34
 */
public class SUpdate {

	/**
	 * The base URL for files in cloud (ex:
	 * "https://dl.dropboxusercontent.com/u/12345678/UpdaterFiles/")
	 */
	private String baseURL;

	/**
	 * The folder that contains the files to update
	 */
	private File outputFolder;

    /**
     * The number of files to download
     */
    private int numberOfFilesToDownload = 0;

    /**
     * The number of files to remove
     */
    private int numberOfFilesToRemove = 0;

	/**
	 * Base constructor
	 * 
	 * @param baseURL
	 *            The base URL for files in cloud (ex:
	 *            "https://dl.dropboxusercontent.com/u/12345678/UpdaterFiles/")
	 * @param outputFolder
	 *            The folder that contains the files to update
	 */
	public SUpdate(String baseURL, File outputFolder) {
		// Settings base fields
		this.baseURL = baseURL;
		this.outputFolder = outputFolder;
		this.outputFolder.mkdirs();
	}

	/**
	 * Sends the main request to the S-Update server, gets the send files informations,
     * compares it, and synchronyses the local folder.
	 * 
	 * @throws IOException
	 *             If it can't download/unzip/remove a file
	 */
	public void update() throws IOException {
		// Printing the infos
		System.out.println("[S-Update] Starting updating with : ");
        System.out.println("[S-Update]     URL           : " + baseURL);
        System.out.println("[S-Update]     Output folder : " + outputFolder.getAbsolutePath());

        // Listing the online files
        ArrayList<OnlineFile> onlineFiles = getOnlineFilesList();

        // Getting the files to download
        ArrayList<FileToUpdate> filesToUpdate = getFilesToUpdate(onlineFiles);

        // Initializing the download
        Downloader downloader = new Downloader();

        // For each file to update
        for(FileToUpdate ftu : filesToUpdate)
            // If the action is DOWNLOAD
            if(ftu.getAction() == FileToUpdate.DOWNLOAD)
                // Downloading it
                downloader.download(new URL(baseURL + (baseURL.endsWith("/") ? "" : "/") + ftu.getFile().getAbsolutePath().replace(outputFolder.getAbsolutePath(), "")), ftu.getFile(), ftu.getLastModified());
            // Else if the action is REMOVING
            else if (ftu.getAction() == FileToUpdate.REMOVE)
                // Removing it
               if(!ftu.getFile().delete())
                   ftu.getFile().deleteOnExit();

		// Printing an "up to date" message
		System.out.println("[S-Update] Terminated !");
	}

    /**
     * Sends a request to the server and returns the list of the online files returned
     *
     * @throws IOException
     *            If it failed to reads / sends the request
     * @return The list of the online files
     */
    private ArrayList<OnlineFile> getOnlineFilesList() throws IOException {
        // Printing a message
        System.out.println("[S-Update] Sending the 'list' request to the server");

        // Creating an array list of OnlineFile
        ArrayList<OnlineFile> onlineFiles = new ArrayList<OnlineFile>();

        // Sending the request
        BufferedReader br = Server.sendRequest(this, "list");

        System.out.println("[S-Update] Parsing response");

        // Reading the request
        while(br.ready()) {
            // Getting the read line
            String line = br.readLine();

            // Splitting the line in two parts separated by a '|'
            String[] infos = line.split("|");

            // Adding a new OnlineFile object with the first part as the file name
            // and the second part as the file last modified date
            onlineFiles.add(new OnlineFile(infos[0], infos[1]));
        }

        // Closing the reader
        br.close();

        return onlineFiles;
    }

    /**
     * Returns a list of the files to download / remove
     *
     * @throws IOException
     *            If it failed to get the files to ignore
     * @return A list of files to update
     */
    private ArrayList<FileToUpdate> getFilesToUpdate(ArrayList<OnlineFile> onlineFiles) throws IOException {
        // Initializing an empty array list
        ArrayList<FileToUpdate> filesToUpdate = new ArrayList<FileToUpdate>();

        // Printing a message
        System.out.println("[S-Update] Checking files");

        // For each online file
        for(OnlineFile onlineFile : onlineFiles) {
            // Getting the local file
            File localFile = new File(this.outputFolder, onlineFile.getFile());

            // If it doesn't exist or the dates aren't the same
            if(!localFile.exists() || onlineFile.getLastModified() != localFile.lastModified()) {
                // TODO: Remove this test message
                System.out.println(localFile.exists() + " " + onlineFile.getLastModified() + " " + localFile.lastModified());

                // Adding it to the list as a file to download
                filesToUpdate.add(new FileToUpdate(this, onlineFile));

                // Adding one to numberOfFilesToDownload
                numberOfFilesToDownload++;

                // Restarting the loop
                continue;
            }
        }

        // Creating the FileIgnorer
        FileIgnorer fileIgnorer = new FileIgnorer(this);

        // Printing a message
        System.out.println("[S-Update] Getting the files to ignore");

        // Getting the files to ignore
        fileIgnorer.getFilesToIgnore();

        // Printing a message
        System.out.println("[S-Update] Checking for local files to delete");

        // Getting the list of the files in the outputFolder
        ArrayList<File> localFiles = Util.listFiles(this.outputFolder);

        // For each files in the list
        for(File localFile : localFiles)
            // If it isn't in the online files list
            if(!Util.contains(this, onlineFiles, localFile) && !fileIgnorer.needToIgnore(localFile)) {
                // Adding it to the list as a file to remove
                filesToUpdate.add(new FileToUpdate(localFile));

                // Adding one to numberOfFilesToRemove
                numberOfFilesToRemove++;
            }

        // Returning the created list
        return filesToUpdate;
    }

	/**
	 * Return the base URL set in the constructor (as a String)
	 * 
	 * @return The base URL
	 */
	public String getBaseURL() {
		return baseURL;
	}

	/**
	 * Set a new baseURL
	 * 
	 * @param baseURL
	 *            The base URL for files in cloud (ex:
	 *            "https://dl.dropboxusercontent.com/u/12345678/UpdaterFiles/")
	 */
	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	/**
	 * Return the output folder set in the constructor
	 * 
	 * @return The output folder
	 */
	public File getOutputFolder() {
		return outputFolder;
	}

	/**
	 * Set a new output folder
	 * 
	 * @param outputFolder
	 *            The folder that contains the files to update
	 */
	public void setOutputFolder(File outputFolder) {
		this.outputFolder = outputFolder;
		this.outputFolder.mkdirs();
	}

    /**
     * Returns the number of files to download
     *
     * @return The number of files to download
     */
    public int getNumberOfFilesToDownload() {
        return numberOfFilesToDownload;
    }

    /**
     * Returns the number of files to remove
     *
     * @return The number of files to remove
     */
    public int getNumberOfFilesToRemove() {
        return numberOfFilesToRemove;
    }

}
