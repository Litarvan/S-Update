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
 * The main class
 * 
 * @version ALPHA-0.1.1
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
	 * Base constructor
	 * 
	 * @param baseURL
	 *            The base URL for files in cloud (ex:
	 *            "https://dl.dropboxusercontent.com/u/12345678/UpdaterFiles/")
	 * @param outputFolder
	 *            The folder that contains the files to update
	 */
	public S_Update(String baseURL, File outputFolder) {
		this.baseURL = baseURL;
		this.outputFolder = outputFolder;
		this.outputFolder.mkdirs();
	}

	/**
	 * Check for some new versions
	 * 
	 * @return True if there is some new versions, false if not
	 * @throws IOException
	 *             If it failed downloading or reading the version file
	 */
	public boolean checkForUpdate() throws IOException {
		File localVersionIndex = new File(this.outputFolder.getAbsolutePath()
				+ "/.S_Update/versionindex.txt");
		File tmpVersionIndex = new File(this.outputFolder.getAbsolutePath()
				+ "/.S_Update/versionindex.tmp.txt");
		if (!localVersionIndex.exists() && tmpVersionIndex.exists())
			tmpVersionIndex.renameTo(localVersionIndex);
		System.out.println("[S-Update] Checking for update...");
		versionIndex = Util.downloadVersionFile(this);
		ArrayList<String> versions = Util.parseVersionIndex(versionIndex);

		if (!localVersionIndex.exists()) {
			versionsToUpdate = versions;
			System.out.println("[S-Update] Need to install "
					+ versionsToUpdate.size() + " versions");
			return true;
		}
		ArrayList<String> localVersions = Util
				.parseVersionIndex(localVersionIndex);
		for (String version : versions) {
			if (!localVersions.contains(version))
				versionsToUpdate.add(version);
		}
		boolean needUpdate = !versionsToUpdate.isEmpty();
		if (needUpdate)
			System.out.println("[S-Update] Need to install "
					+ versionsToUpdate.size() + " versions");
		else
			System.out.println("[S-Update] Up to date !");
		return needUpdate;
	}

	/**
	 * Create three lists of files to download, unzip or remove
	 * 
	 * @throws IOException
	 *             If it failed parsing a file index
	 */
	public void createLists() throws IOException {
		System.out.println("[S-Update] Creating files list...");
		for (String version : versionsToUpdate) {
			System.out
					.println("[S-Update] Parsing the files list of the version \""
							+ version + "\"");
			ArrayList<FileToUpdate> files = Util.parseFileIndex(this, version);
			for (FileToUpdate f : files) {
				switch (f.getAction()) {
				case FileToUpdate.DOWNLOAD:
					if (!Util.contains(filesToDownload, f))
						filesToDownload.add(f);
					break;
				case FileToUpdate.UNZIP:
					if (!Util.contains(filesToUnzip, f))
						filesToUnzip.add(f);
					break;
				case FileToUpdate.REMOVE:
					if (Util.contains(filesToDownload, f)) {
						Util.remove(filesToDownload, f);
						break;
					}
					if (!Util.contains(filesToRemove, f))
						filesToRemove.add(f);
					break;
				}

			}
		}
		System.out.println("[S-Update] Need to download "
				+ (filesToDownload.size() + filesToUnzip.size())
				+ " files and unzip " + filesToUnzip.size() + " of them");
		System.out.println("[S-Update] Need to remove " + filesToRemove.size()
				+ " files");
	}

	/**
	 * Download all files in the filesToDownload {@link ArrayList}, Unzip all
	 * zip files in the filesToUnzip {@link ArrayList}, Remove all files in the
	 * fileToRemove {@link ArrayList}
	 * 
	 * @throws IOException
	 *             If it can't download/unzip/remove a file
	 */
	public void update() throws IOException {
		for (FileToUpdate f : filesToDownload) {
			System.out.println("[S-Update] Downloading " + this.baseURL
					+ "/Files/" + f);
			Util.downloadFile(new URL(this.baseURL + "/Files/" + f), new File(
					this.outputFolder, f.toString()));
		}
		for (FileToUpdate f : filesToUnzip) {
			System.out.println("[S-Update] Downloading " + this.baseURL
					+ "/Files/" + f);
			File zipFile = new File(this.outputFolder, f.toString());
			File file = Util.downloadFile(
					new URL(this.baseURL + "/Files/" + f), zipFile);
			System.out
					.println("[S-Update] Unzipping " + file.getAbsolutePath());
			Util.unzip(file, zipFile.getParentFile());
			Util.remove(zipFile);
		}
		for (FileToUpdate f : filesToRemove) {
			System.out.println("[S-Update] Removing "
					+ this.outputFolder.getAbsolutePath() + "/" + f);
			Util.remove(new File(this.outputFolder, f.toString()));
		}
		File localVersionIndex = new File(this.outputFolder.getAbsolutePath()
				+ "/.S_Update/versionindex.txt");
		if (!localVersionIndex.delete())
			localVersionIndex.deleteOnExit();
		else
			versionIndex.renameTo(localVersionIndex);
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

}
