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
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * The main class
 *
 * <p>
 *     Initialize it with the url of the update folder and
 *     the output folder to start updating !
 * </p>
 *
 * @version 2.2.0-SNAPSHOT
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
	 * Synchronise the local folder with the server folder
	 * 
	 * @throws IOException
	 *             If it can't download/remove a file
	 */
	public void update() throws IOException {
        // Stocking the start time
        long startTime = System.currentTimeMillis();

		// Printing the infos
		System.out.println("[S-Update] Starting updating with : ");
        System.out.println("[S-Update]     URL           : " + baseURL);
        System.out.println("[S-Update]     Output folder : " + outputFolder.getAbsolutePath());

        // Printing a message
        System.out.println("[S-Update] Updating server stats");

        // Updating server stats
        Server.updateStats(this);

        // Listing the online files
        ArrayList<OnlineFile> onlineFiles = getOnlineFilesList();

        // Listing the online zip files
        ArrayList<ZipFile> zipFiles = getZipFilesList();

        // Getting the files to download
        ArrayList<FileToUpdate> filesToUpdate = null;
        try {
            filesToUpdate = getFilesToUpdate(onlineFiles, zipFiles);
        } catch (NoSuchAlgorithmException e) {
        }

        // Initializing the download
        Downloader downloader = new Downloader();

        // For each file to update
        for(FileToUpdate ftu : filesToUpdate)
            // If the action is DOWNLOAD
            if(ftu.getAction() == FileToUpdate.DOWNLOAD)
                // Downloading it
                downloader.download(new URL(baseURL + (baseURL.endsWith("/") ? "" : "/") + "files" + ftu.getFile().getAbsolutePath().replace(outputFolder.getAbsolutePath(), "").replace("\\", "/")), ftu.getFile(), ftu.getLastModified());
            // Else if the action is REMOVING
            else if (ftu.getAction() == FileToUpdate.REMOVE) {
                // Printing a message
                System.out.println("[S-Update] Deleting file " + ftu.getFile().getAbsolutePath());

                // Removing it
                if (!ftu.getFile().delete())
                    ftu.getFile().deleteOnExit();
            } else if (ftu.getAction() == FileToUpdate.DOWNLOAD_AND_UNZIP)
                downloader.downloadAndUnzip(new URL(baseURL + (baseURL.endsWith("/") ? "" : "/") + "zips" + ftu.getFile().getAbsolutePath().replace(outputFolder.getAbsolutePath(), "").replace("\\", "/")), ftu.getFile());

        // Closing the pool
        downloader.getPool().shutdown();

        // Printing a message
        System.out.println("[S-Update] Waiting for the downloads");
        try {
            // Waiting for the pool to terminate
            downloader.getPool().awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

            // Printing an "up to date" message
            System.out.println("[S-Update] Terminated ! Total time : " + (System.currentTimeMillis() - startTime) + "ms");
        } catch (InterruptedException e) {
            e.printStackTrace();

            // Printing a message
            System.out.println("[S-Update] Can't wait for the pool !");
        }

        // Printing a message
        System.out.println("[S-Update] Deleting empty folders");

        // Deleting empty folders
        Util.deleteEmptyFolders(this.outputFolder);
	}

    /**
     * Downloads the files index of the server and parse it into an array list of OnlineFile objects
     *
     * @throws IOException
     *            If it failed to read the file
     * @return The list of the online files
     */
    private ArrayList<OnlineFile> getOnlineFilesList() throws IOException {
        // Printing a message
        System.out.println("[S-Update] Downloading the files index");

        // Creating an array list of OnlineFile
        ArrayList<OnlineFile> onlineFiles = new ArrayList<OnlineFile>();

        // Sending the index
        BufferedReader br = Server.getFile(this, "su_files.idx");

        // Printing a message
        System.out.println("[S-Update] Parsing the index");

        // Reading the file
        while(br.ready()) {
            // Getting the read line
            String line = br.readLine();

            // Splitting the line in two parts separated by a '|'
            String[] infos = line.split("\\|");

            // Adding a new OnlineFile object with the first part as the file name
            // and the second part as the file last modified date, if it can't parse
            // the date, then adding it as the MD5
            try {
                onlineFiles.add(new OnlineFile(infos[0], Long.parseLong(infos[1])));
            } catch (NumberFormatException e) {
                onlineFiles.add(new OnlineFile(infos[0], infos[1]));
            }
        }

        // Closing the reader
        br.close();

        return onlineFiles;
    }

    public ArrayList<ZipFile> getZipFilesList() throws IOException {
        // Printing a message
        System.out.println("[S-Update] Downloading the zip files index");

        // Creating an array list of ZipFile
        ArrayList<ZipFile> zipFiles = new ArrayList<ZipFile>();

        // Getting the index
        BufferedReader br = Server.getFile(this, "su_zips.idx");

        // Printing a message
        System.out.println("[S-Update] Parsing the zip index");

        // Reading the file
        while(br.ready()) {
            // Getting the read line
            String line = br.readLine();

            // Splitting the line in two parts separated by a '|'
            String[] infos = line.split("\\|");

            // Adding a new ZipFile object with the first part as the zip name
            // and the second part as a file of the zip that will be checked
            // to know if we need to download the zip
            zipFiles.add(new ZipFile(infos[0], infos[1]));
        }

        // Closing the reader
        br.close();

        return zipFiles;
    }

    /**
     * Returns a list of the files to download / remove
     *
     * @throws IOException
     *            If it failed to get the files to ignore
     * @return A list of files to update
     */
    private ArrayList<FileToUpdate> getFilesToUpdate(ArrayList<OnlineFile> onlineFiles, ArrayList<ZipFile> zipFiles) throws IOException, NoSuchAlgorithmException {
        // Initializing an empty array list
        ArrayList<FileToUpdate> filesToUpdate = new ArrayList<FileToUpdate>();

        // Creating the FileIgnorer
        FileIgnorer fileIgnorer = new FileIgnorer(this);

        // Printing a message
        System.out.println("[S-Update] Getting the files to ignore");

        // Getting the files to ignore
        fileIgnorer.getFilesToIgnore();

        // For each zip file
        for(ZipFile zipFile : zipFiles) {
            // Getting any of the zip file
            File anyFile = new File(this.outputFolder, zipFile.getAnyFile());

            // If the file doesn't exist
            if(!anyFile.exists()) {
                // Adding it to the list as a file to download and unzip
                filesToUpdate.add(new FileToUpdate(this, zipFile));

                // Adding one to the numberOfFilesToDownload
                numberOfFilesToDownload++;
            }

            // Adding the parent folder to the list of files to ignore
            fileIgnorer.addFileToIgnore(anyFile.getParentFile());
        }

        // Printing a message
        System.out.println("[S-Update] Checking files");

        // For each online file
        for(OnlineFile onlineFile : onlineFiles) {
            // Getting the local file
            File localFile = new File(this.outputFolder, onlineFile.getFile());

            // If it doesn't exist or the dates aren't the same or if the date are null, the md5 aren't the same
            if(!localFile.exists() || (onlineFile.getLastModified() != 0 && onlineFile.getLastModified() != localFile.lastModified()) ||
                    (onlineFile.getMD5() != null && !onlineFile.getMD5().equals(Util.getMD5(localFile)))) {
                // Adding it to the list as a file to download
                filesToUpdate.add(new FileToUpdate(this, onlineFile));

                // Adding one to numberOfFilesToDownload
                numberOfFilesToDownload++;
            }
        }

        // Printing a message
        System.out.println("[S-Update] Checking for local files to delete");

        // Getting the list of the files in the outputFolder
        ArrayList<File> localFiles = Util.listFiles(this.outputFolder, fileIgnorer);

        // For each files in the list
        for(File localFile : localFiles)
            // If it isn't in the online files list
            if(!Util.contains(this, onlineFiles, localFile)) {
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

    public static void main(String[] args) throws IOException {
        SUpdate su = new SUpdate("http://localhost/sutesttest/", new File("C:/Users/Adrien/Documents/LEGROSTESTDEOUF"));
        su.update();
    }

}
