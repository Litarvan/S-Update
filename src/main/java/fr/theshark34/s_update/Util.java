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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * The Util class, with some util methods for updating
 * 
 * @version RELEASE-1.0
 * @author TheShark34
 */
public final class Util {

	/**
	 * The size of the current unziping file
	 */
	private static long unzipingFileSize;
	
	/**
	 * The current downloading file
	 */
	private static long unzipingLen;
	
	/**
	 * The size of the current downloading file
	 */
	private static long downloadingFileSize;

	/**
	 * The current downloading file
	 */
	private static File downloadingFile;

	/**
	 * Reads a file using nio
	 * 
	 * @param filePath
	 *            The path of the file to read
	 * @return What it read in file (as a string)
	 * @throws IOException
	 *             If it failed
	 */
	public static String readFile(String filePath) throws IOException {
		// Initializing the string
		String readString = "";

		// Getting the file in read only mode
		RandomAccessFile file = new RandomAccessFile(filePath, "r");

		// Getting the file channel
		FileChannel channel = file.getChannel();

		// Creating a buffer with 1 kb allocated
		ByteBuffer buffer = ByteBuffer.allocate(1024);

		// While the channel read method doesn't return a negative result
		while (channel.read(buffer) > 0) {
			// Flipping the buffer
			buffer.flip();

			// Adding the buffer to the string
			for (int i = 0; i < buffer.limit(); i++)
				readString += (char) buffer.get();

			// Clearing the buffer
			buffer.clear();
		}

		// Closing the channel
		channel.close();

		// Closing the file
		file.close();

		// Returning the read string
		return readString;
	}

	/**
	 * Parses a version index - Returns an array list of all versions in it
	 * 
	 * @param versionIndex
	 *            The version index file to parse
	 * @return An {@link ArrayList} of {@link String} containing all versions of
	 *         the file
	 * @throws IOException
	 *             If it failed to read the file
	 */
	public static ArrayList<String> parseVersionIndex(File versionIndex)
			throws IOException {
		// Read the file
		String readVersions = readFile(versionIndex.getAbsolutePath());

		// Deleting all bad characters like line returns
		readVersions = readVersions.replaceAll("[^a-zA-Z0-9/ ]", "");

		// Splitting the vesrion with '/'
		String[] versions = readVersions.split("/");

		// Creating an array list with the array
		ArrayList<String> versionsList = new ArrayList<String>(
				Arrays.asList(versions));

		// Returning the list
		return versionsList;
	}

	/**
	 * Downloads the version index
	 * 
	 * @return The download version index
	 * @throws IOException
	 *             If it fails downloading
	 */
	public static File downloadVersionIndex(S_Update updater) throws IOException {
		// Creating the version index URL
		URL url = new URL(updater.getBaseURL() + "/versionindex.txt");

		// Initializing the output file
		File output = new File(updater.getOutputFolder().getAbsolutePath()
				+ "/.S_Update/versionindex.tmp.txt");

		// If it exists deleting it
		if (output.exists())
			output.delete();

		// If the parent folder (.S_Update) doesn't exist
		if (!output.getParentFile().exists())
			// Creating it
			output.getParentFile().mkdirs();

		// Downloading the version inedx and returning it
		return downloadFile(url, output);
	}

	/**
	 * Downloads a file from an URL to an output file
	 * 
	 * @param url
	 *            The {@link URL} of the file to download
	 * @param output
	 *            The output {@link File}
	 * @return The download file (=output)
	 * @throws IOException
	 *             If it failed downloading
	 */
	public static File downloadFile(URL url, File output) throws IOException {
		// Creating the output directory if it doesn't exist
		output.getParentFile().mkdirs();

		// Setting the current downloading file to the output file
		downloadingFile = output;

		// Creating an Http URL Connection from the URL
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// Say to the server that we are Mozilla Firefox to prevent errors
		connection.addRequestProperty("User-Agent", "Mozilla/4.76");

		// Setting the file size
		downloadingFileSize = connection.getContentLength();

		// Getting the file channel
		ReadableByteChannel rbc = Channels.newChannel(connection.getInputStream());

		// Creating an output stream
		FileOutputStream fos = new FileOutputStream(output);

		// Tranfering files from the channel to the output stream
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

		// Closing the output stream
		fos.close();

		// Closing the file channel
		rbc.close();

		// Returning the output
		return output;
	}

	/**
	 * Parses a version file
	 * 
	 * @param su
	 *            The current S_Update used to know the output folder and the
	 *            base URL
	 * @param version
	 *            The version to parse
	 * @return An ArrayList of {@link FileToUpdate} containing all files to
	 *         download/remove/unzip
	 * @throws IOException
	 *             If it fails to read a file index
	 */
	public static ArrayList<FileToUpdate> parseFileIndex(S_Update su,
			String version) throws IOException {
		// Creating a FileToUpdate list
		ArrayList<FileToUpdate> files = new ArrayList<FileToUpdate>();

		// Downloading the version index
		File fileIndex = downloadFile(
				new URL(su.getBaseURL() + "/" + version.replace(" ", "%20")
						+ ".txt"), new File(su.getOutputFolder()
						.getAbsolutePath()
						+ "/.S_Update/"
						+ version
						+ ".txt.tmp"));

		// Initializing a Buffered Reader
		BufferedReader br = new BufferedReader(new FileReader(fileIndex));

		// While the buffer is ready to read
		while (br.ready()) {
			// Getting the read line
			String line = br.readLine();

			// If the line without space don't start with [
			if (!line.replace(" ", "").startsWith("[")) {
				// Adding it to the list as a file to download
				files.add(new FileToUpdate(line, FileToUpdate.DOWNLOAD));

				// Restarting the loop
				continue;
			}

			// Creating a list of arguments for the file
			ArrayList<String> args = new ArrayList<String>();

			// Creating a string of the current reading argument
			String currentArg = "";

			// Converting the line to a char array
			char[] chars = line.toCharArray();

			int i = 0;
			for (i = 0; i < chars.length; i++) {
				// If the character is a space or [
				if (chars[i] == ' ' || chars[i] == '[')
					// Just restarting the loop to read the argument
					continue;

				// If character is ,
				if (chars[i] == ',') {
					// Adding the current read argument to the list
					args.add(currentArg);

					// Reinitializing the current argument
					currentArg = "";

					// Restarting the loop to read the next argument
					continue;
				}

				// If the character is ]
				if (chars[i] == ']') {
					// It means that the arguments list is finished so
					// adding the last read argument to the list and
					// stopping the loop
					args.add(currentArg);
					break;
				}

				// If there is nothing of that juste adding the read
				// character to the currentArg
				currentArg += chars[i];
			}

			// Getting the number of characters before the file name
			// by adding 1 to i and adding 1 for each space before
			i++;
			while (chars[i] == ' ')
				i++;

			// Initializing a boolean that is true, and will be false
			// if we need to not add the file to the list
			boolean add = true;

			// If there is a windows/mac/linux argument
			if (args.contains("windows") || args.contains("mac")
					|| args.contains("linux")) {
				// It there isn't a windows arg but the user is on
				// windows
				if (!args.contains("windows")
						&& System.getProperty("os.name").toLowerCase()
								.contains("win"))
					// Setting add to false
					add = false;

				// It there isn't a mac arg but the user is on mac
				if (!args.contains("mac")
						&& System.getProperty("os.name").toLowerCase()
								.contains("mac"))
					// Setting add to false
					add = false;

				// It there isn't a linux arg but the user is on linux
				if (!args.contains("linux")
						&& System.getProperty("os.name").toLowerCase()
								.contains("lin"))
					// Setting add to false
					add = false;
			}

			// If there is a 32/64 argument
			if (args.contains("32") || args.contains("64")) {
				// If there isn't a 32 arg but ther user is on a 32
				// bits system
				if (!args.contains("32"))
					if (System.getProperty("os.arch").contains("32")
							|| System.getProperty("os.arch").contains("86"))
						if (!System.getProperty("os.arch").contains("64"))
							// Setting add to false
							add = false;
				// If there isn't a 64 arg but ther user is on a 64
				// bits system
				if (!args.contains("64")
						&& System.getProperty("os.arch").contains("64"))
					// Setting add to false
					add = false;
			}

			// Creating the file by its name with downloading action
			// as default
			FileToUpdate file = new FileToUpdate(line.substring(i),
					FileToUpdate.DOWNLOAD);

			// If there is an unzip argument setting its action to
			// UNZIP
			if (args.contains("unzip"))
				file.setAction(FileToUpdate.UNZIP);
			// If there is an remove argument setting its action to
			// REMOVE
			else if (args.contains("remove"))
				file.setAction(FileToUpdate.REMOVE);

			// If we can add it
			if (add) {
				// And if the list doesn't contains the file already
				if (!contains(files, file))
					// Adding it
					files.add(file);
			}
		}

		// Closing the reader
		br.close();

		// Returning the list
		return files;
	}

	/**
	 * Checks if an instance of a {@link FileToUpdate} is in an {@link ArrayList}
	 * 
	 * @param al
	 *            The ArrayList
	 * @param f
	 *            The file
	 * @return True if it is present
	 */
	public static boolean contains(ArrayList<FileToUpdate> al, FileToUpdate f) {
		// For each file in the list
		for (FileToUpdate file : al)
			// If the names are equals
			if (file.toString().equals(f.toString()))
				// Returning true
				return true;

		// If the loop finished it means that the list doesn't contains the file
		// so returning false
		return false;
	}

	/**
	 * Removes a {@link FileToUpdate} instance in an {@link ArrayList}
	 * 
	 * @param al
	 *            The ArrayList
	 * @param f
	 *            The file to remove
	 */
	public static void remove(ArrayList<FileToUpdate> al, FileToUpdate f) {
		// For each file in the list
		for (int i = 0; i < al.size(); i++)
			// If the names are equals
			if (al.get(i).toString().equals(f.toString())) {
				// Deleting it
				al.remove(i);

				// And stopping the method
				return;
			}
	}

	/**
	 * Unzips the zipFile argument in the outputFolder argument
	 * 
	 * @param fileToUnzip
	 *            The zip file to unzip
	 * @param outputFolder
	 *            The output folder
	 */
	public static void unzip(File fileToUnzip, File outputFolder)
			throws IOException {
		// Getting the zip file
		ZipFile zipFile = new ZipFile(fileToUnzip);

		// Getting an enumeration of all its entries
		Enumeration<?> enu = zipFile.entries();

		// Setting the unzipping file size to 0
		unzipingFileSize = 0;

		// Setting the unzipping length to 0
		unzipingLen = 0;

		// For each entries in the list
		while (enu.hasMoreElements())
			// Adding hist size to the total size
			unzipingFileSize += (long)((ZipEntry) enu.nextElement()).getSize();

		// Reinitializing the enumeration
		enu = zipFile.entries();

		// For each entries in the list
		while (enu.hasMoreElements()) {
			// Getting the entry
			ZipEntry zipEntry = (ZipEntry) enu.nextElement();

			// Getting its name
			String name = zipEntry.getName();

			// Getting the output file
			File file = new File(outputFolder, name);

			// Setting the current file to the unzipping file
			downloadingFile = file;

			// If the name ends with '/'
			if (name.endsWith("/")) {
				// It means that is a directory so creating it
				file.mkdirs();

				// And restarting the loop
				continue;
			}

			// Getting its parent directory
			File parent = file.getParentFile();

			// If it exists
			if (parent != null)
				// Creating it
				parent.mkdirs();

			// Getting an input stream for this entry
			InputStream is = zipFile.getInputStream(zipEntry);

			// Creating an output stream for the file
			FileOutputStream fos = new FileOutputStream(file);

			// Creating a byte buffer
			byte[] bytes = new byte[1024];

			// Initializing the length int
			int length;

			// While the buffer length isn't negative
			while ((length = is.read(bytes)) >= 0) {
				// Writing the buffer
				fos.write(bytes, 0, length);

				// And adding his size to the unzipped files size
				unzipingLen += length;
			}

			// Closing the input stream
			is.close();

			// Closing the output stream
			fos.close();
		}

		// Closing the zip file
		zipFile.close();

		// Removing the __MACOSX/ folder if it exists
		remove(new File(outputFolder, "__MACOSX/"));
	}

	/**
	 * Removes a file or a directory with all of his content
	 * 
	 * @param file
	 *            The file to delete
	 * @throws IOException
	 *            If it failed to remove the file
	 */
	public static void remove(File file) throws IOException {
		// If the file doesn't exist aborting
		if (!file.exists())
			return;

		// If the file is a directory
		if (file.isDirectory()) {
			// Getting a list of its files
			File[] files = file.listFiles();

			// For each files in the list
			for (File f : files)
				// Removing it
				remove(f);
		}

		// Trying to delete the file
		if (!file.delete())
			// If it failed throwing an IOException
			throw new IOException("Can't delete the file \""
					+ file.getAbsolutePath() + "\"");
	}

	/**
	 * Return the current unziping length file
	 * 
	 * @return The current unziping length file
	 */
	public static long getUnzipingFileLen() {
		return unzipingLen;
	}

	/**
	 * Return the current unziping file size
	 * 
	 * @return The current unziping file size
	 */
	public static long getUnzipingFileSize() {
		return unzipingFileSize;
	}

	/**
	 * Return the current downloading file
	 * 
	 * @return The current downloading file
	 */
	public static File getDownloadingFile() {
		return downloadingFile;
	}

	/**
	 * Return the current downloading file size
	 * 
	 * @return The current downloading file size
	 */
	public static long getDownloadingFileSize() {
		return downloadingFileSize;
	}

}
