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

/**
 * A File with an action to be executed when updating
 * 
 * @version ALPHA-0.1.1
 * @author TheShark34
 */
public class FileToUpdate {

	/**
	 * If the file need to be download
	 */
	public static final int DOWNLOAD = 0;

	/**
	 * If the file need to be unziped
	 */
	public static final int UNZIP = 1;

	/**
	 * If the file need to be removed
	 */
	public static final int REMOVE = 2;

	/**
	 * If the file need to be download/unziped/removed
	 */
	private int action;

	/**
	 * Path of the file
	 */
	private String file;

	/**
	 * Simple constructor
	 * 
	 * @param file
	 *            The path of the file
	 * @param action
	 *            If the file need to be download/unziped/removed
	 */
	public FileToUpdate(String file, int action) {
		this.file = file;
		this.action = action;
	}

	/**
	 * Set the action
	 * 
	 * @param action
	 *            If the file need to be download/unziped/removed
	 */
	public void setAction(int action) {
		this.action = action;
	}

	/**
	 * Return the action
	 * 
	 * @return If the file need to be download/unziped/removed
	 */
	public int getAction() {
		return this.action;
	}

	/**
	 * Return the file path
	 */
	public String toString() {
		return this.file;
	}

}
