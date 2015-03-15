/*
 * Copyright 2015 TheShark34
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package fr.theshark34.s_update;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * The main class - Initialize it with the url of the update folder and
 * the output folder to start updating !
 * 
 * @version 1.0-RELEASE
 * @author TheShark34
 */
public class S_Update {

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
	 * An {@link ArrayList} of String that will contains all versions to update
	 * after executing checkForUpdate()
	 */
	private ArrayList<String> versionsToUpdate = new ArrayList<String>();

	/**
	 * An {@link ArrayList} of {@link FileToUpdate} that will contains all files
	 * to download after executing createLists()
	 */
	private ArrayList<FileToUpdate> filesToDownload = new ArrayList<FileToUpdate>();

	/**
	 * An {@link ArrayList} of {@link FileToUpdate} that will contains all zip
	 * files to unzip after executing createLists()
	 */
	private ArrayList<FileToUpdate> filesToUnzip = new ArrayList<FileToUpdate>();

	/**
	 * An {@link ArrayList} of {@link FileToUpdate} that will contains all files
	 * to remove after executing createLists()
	 */
	private ArrayList<FileToUpdate> filesToRemove = new ArrayList<FileToUpdate>();

	/**
	 * The version index
	 */
	private File versionIndex;

	/**
	 * The current state, DOWNLOADING, UNZIPPING, or REMOVNG
	 */
	private int state = -1;

	/**
	 * State DOWNLOADING
	 */
	public static final int DOWNLOADING = 0;

	/**
	 * State UNZIPPING
	 */
	public static final int UNZIPPING = 1;

	/**
	 * State REMOVING
	 */
	public static final int REMOVING = 2;

	/**
	 * State FINISH
	 */
	public static final int FINISH = 3;

	/**
	 * The current downloading/unzipping/removing file number
	 */
	private int fileNumber;

	/**
	 * The number of files to download/unzip/remove
	 */
	private int numberOfFiles;

	/**
	 * The current downloading/unzipping/removing file name
	 */
	private String currentFileName;

	/**
	 * Base constructor
	 * 
	 * @param baseURL
	 *            The base URL for files in cloud (ex:
	 *            "https://dl.dropboxusercontent.com/u/12345678/UpdaterFiles/")
	 * @param outputFolder
	 *            The folder that contains the files to update
	 */
	public S_Update(String baseURL, File outputFolder) {
		// Settings base fields
		this.baseURL = baseURL;
		this.outputFolder = outputFolder;
		this.outputFolder.mkdirs();
	}

	/**
	 * Checks for some new versions
	 * 
	 * @return True if there is some new versions, false if not
	 * @throws IOException
	 *             If it failed downloading or reading the version file
	 */
	public boolean checkForUpdate() throws IOException {
		// Printing a message
		System.out.println("[S-Update] Checking for update...");

		// Initializing the local and the downloaded version index
		File localVersionIndex = new File(this.outputFolder.getAbsolutePath()
				+ "/.S_Update/versionindex.txt");
		File tmpVersionIndex = new File(this.outputFolder.getAbsolutePath()
				+ "/.S_Update/versionindex.tmp.txt");

		// Initializing the "updating" file created when the update starts
		// and deleted when it finishes
		File updating = new File(this.outputFolder, "/.S_Update/updating");

		// If it doesn't exist but the downloaded version index exists
		// so an old update was made
		if (!updating.exists() && tmpVersionIndex.exists()) {
			// So deleting the local version index and renaming the tmp
			// version index to the local version index to prevent renaming
			// bugs when the update finished
			localVersionIndex.delete();
			tmpVersionIndex.renameTo(localVersionIndex);
		} else if (updating.exists())
			// But if "updating" file exists, so an old maj didn't finished
			// so deleting the "updating" file and keeping the old version
			// index to restart the previous update
			if (!updating.delete())
				updating.deleteOnExit();

		// Downloading the vesrion index
		versionIndex = Util.downloadVersionIndex(this);

		// Parsing it
		ArrayList<String> versions = Util.parseVersionIndex(versionIndex);

		// If the local version index doesn't exist
		if (!localVersionIndex.exists()) {
			// We need to get all the versions
			versionsToUpdate = versions;

			// Printing a message
			System.out.println("[S-Update] Need to install "
					+ versionsToUpdate.size() + " versions");

			// And returning true because there is updates to do !
			return true;
		}

		// If it exists parsing it
		ArrayList<String> localVersions = Util
				.parseVersionIndex(localVersionIndex);

		// For each versions in the downloaded version index
		for (String version : versions) {
			// If the local version index doesn't have it
			if (!localVersions.contains(version))
				// Adding it to the versions to download
				versionsToUpdate.add(version);
		}

		// This boolean is false if the versions to download
		// list is empty, else it is true
		boolean needUpdate = !versionsToUpdate.isEmpty();

		// So if there is any update
		if (needUpdate)
			// Printing a "need to update" message
			System.out.println("[S-Update] Need to install "
					+ versionsToUpdate.size() + " versions");
		else
			// Else printing an "up to date" message
			System.out.println("[S-Update] Up to date !");

		// Returning if an update is needed
		return needUpdate;
	}

	/**
	 * Creates three lists of files to download, unzip or remove
	 * 
	 * @throws IOException
	 *             If it failed parsing a file index
	 */
	public void createLists() throws IOException {
		// Printing a message
		System.out.println("[S-Update] Creating files list...");

		// For each versions to update
		for (String version : versionsToUpdate) {
			// Printing a message
			System.out
					.println("[S-Update] Parsing the files list of the version \""
							+ version + "\"");

			// Downloading the version file and parsing it
			ArrayList<FileToUpdate> files = Util.parseFileIndex(this, version);

			// For each "FileToUpdate" object parsed
			for (FileToUpdate f : files) {
				switch (f.getAction()) {
				// If we need to download the file
				case FileToUpdate.DOWNLOAD:
					// If it is already in the remove list
					if (Util.contains(filesToRemove, f))
						// Just removing it of the list
						Util.remove(filesToRemove, f);
					else if (!Util.contains(filesToDownload, f))
						// Else if the file isn't already in the list
						// Adding it to the download list
						filesToDownload.add(f);
					break;

				// If we need to download and unzip the file
				case FileToUpdate.UNZIP:
					// If it is already in the remove list
					if (Util.contains(filesToRemove, f))
						// Just removing it of the list
						Util.remove(filesToRemove, f);
					else if (!Util.contains(filesToUnzip, f))
						// Else if the file isn't already in the list
						// Adding it to the unzip list
						filesToUnzip.add(f);
					break;

				// If we need to remove the file
				case FileToUpdate.REMOVE:
					// If it is already in the download list
					if (Util.contains(filesToDownload, f))
						// Just removing it of the list
						Util.remove(filesToDownload, f);
					else if (!Util.contains(filesToRemove, f))
						// Else if the file isn't already in the list
						// Adding it to the remove list
						filesToRemove.add(f);
					break;
				}

			}
		}

		// Printing a message of all files to download and
		// which of them we need to unzip
		System.out.println("[S-Update] Need to download "
				+ (filesToDownload.size() + filesToUnzip.size())
				+ " files and unzip " + filesToUnzip.size() + " of them");

		// Printing a message of all files to remove
		System.out.println("[S-Update] Need to remove " + filesToRemove.size()
				+ " files");
	}

	/**
	 * Downloads all files in the filesToDownload {@link ArrayList}, Downloads
	 * and Unzips all zip files in the filesToUnzip {@link ArrayList} and Removes
	 * all files in the fileToRemove {@link ArrayList}
	 * 
	 * @throws IOException
	 *             If it can't download/unzip/remove a file
	 */
	public void update() throws IOException {
		// Creating the "updating" file in case of stop of the update
		File updating = new File(this.outputFolder, "/.S_Update/updating");

		// Creating it
		updating.createNewFile();

		// Setting the current state to DOWNLOADING
		this.state = DOWNLOADING;

		// Setting the number of files to download
		this.numberOfFiles = filesToDownload.size() + filesToUnzip.size();

		// Setting the current file number to 0
		this.fileNumber = 0;

		// For each files in the download list
		for (int i = 0; i < filesToDownload.size(); i++) {
			// Getting it
			FileToUpdate f = filesToDownload.get(i);

			// Setting the current file name to its name
			this.currentFileName = f.toString();

			// Adding 1 to the current file number
			this.fileNumber++;

			// Printing a "downloading" message
			System.out.println("[S-Update] Downloading " + this.baseURL
					+ "/Files/" + f);

			// Downloading the file to the output folder at the
			// desired location
			Util.downloadFile(new URL(this.baseURL + "/Files/" + f), new File(
					this.outputFolder, f.toString()));
		}

		// For each files in the unzip list
		for (int i = 0; i < filesToUnzip.size(); i++) {
			// Getting it
			FileToUpdate f = filesToUnzip.get(i);

			// Setting the current file name to the its name
			this.currentFileName = f.toString();

			// Adding 1 to the current file number
			this.fileNumber++;

			// Printing a "downloading" message
			System.out.println("[S-Update] Downloading " + this.baseURL
					+ "/Files/" + f);

			// Initializing the file
			File file = Util.downloadFile(
					new URL(this.baseURL + "/Files/" + f), new File(this.outputFolder, f.toString()));

			// Setting the current state to UNZIPPING
			this.state = UNZIPPING;

			// Printing a "unzipping" message
			System.out
					.println("[S-Update] Unzipping " + file.getAbsolutePath());

			// Unzipping the file to its parent location
			Util.unzip(file, file.getParentFile());

			// Deleting the zip file
			Util.remove(file);
		}

		// Setting the current state to REMOVING
		this.state = REMOVING;

		// Setting the number of files to remove
		this.numberOfFiles = filesToRemove.size();

		// Setting the current file number to 0
		this.fileNumber = 0;

		// For each files in the remove list
		for (int i = 0; i < filesToRemove.size(); i++) {
			// Getting it
			FileToUpdate f = filesToRemove.get(i);

			// Setting the current file name to the its name
			this.currentFileName = f.toString();

			// Adding 1 to the current file number
			this.fileNumber++;

			// Printing a "removing" message
			System.out.println("[S-Update] Removing "
					+ this.outputFolder.getAbsolutePath() + "/" + f);

			// removing the file
			Util.remove(new File(this.outputFolder, f.toString()));
		}

		// Getting the local version index
		File localVersionIndex = new File(this.outputFolder.getAbsolutePath()
				+ "/.S_Update/versionindex.txt");

		// Trying to delete it
		if (!localVersionIndex.delete())
			localVersionIndex.deleteOnExit();
		else
			// If it deleted it
			// Renaming the version index to the local version index
			versionIndex.renameTo(localVersionIndex);

		// Setting the current state to FINISH
		this.state = FINISH;

		// Trying to delete the "updating" file
		if (!updating.delete())
			updating.deleteOnExit();

		// Printing an "up to date" message
		System.out.println("[S-Update] Up to date !");
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
	 * Return the current state, DOWNLOADING, UNZIPPING, REMOVING
	 * 
	 * @return The current state
	 */
	public int getState() {
		return this.state;
	}

	/**
	 * Return the current downloading/unzipping/removing file number
	 * 
	 * @return The current file number
	 */
	public int getFileNumber() {
		return this.fileNumber;
	}

	/**
	 * Return the number of files to download/unzip/remove
	 * 
	 * @return The number of files to download/unzip/remove
	 */
	public int getNumberOfFiles() {
		return this.numberOfFiles;
	}

	/**
	 * The current downloading/unzipping/removing file name
	 * 
	 * @return The current downloading/unzipping/removing file name
	 */
	public String getCurrentFileName() {
		return this.currentFileName;
	}
}
