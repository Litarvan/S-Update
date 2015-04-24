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
package fr.theshark34.s_update;

import java.io.File;
import java.io.IOException;

/**
 * The main class - Initialize it with the url of the update folder and
 * the output folder to start updating !
 * 
 * @version 1.0-RELEASE
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
	 * Sends a prepare request to the PHP S-Update and getting the tasks
	 * 
	 * @throws IOException
	 *             If it can't download/unzip/remove a file
	 */
	public void update() throws IOException {
		// Printing a starting message
		System.out.println("[S-Update] Starting updating");

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

}
