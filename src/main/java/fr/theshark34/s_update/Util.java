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
 * The Util class, with some util methods
 * 
 * @version ALPHA-0.1.1
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
	 * Read a file using nio
	 * 
	 * @param filePath
	 *            The path of the file to read
	 * @return What it read in file (as a string)
	 * @throws IOException
	 *             If it failed
	 */
	public static String readFile(String filePath) throws IOException {
		String readString = "";
		RandomAccessFile file = new RandomAccessFile(filePath, "r");
		FileChannel channel = file.getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		while (channel.read(buffer) > 0) {
			buffer.flip();
			for (int i = 0; i < buffer.limit(); i++)
				readString += (char) buffer.get();
			buffer.clear();
		}
		channel.close();
		file.close();
		return readString;
	}

	/**
	 * Parse a version index
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
		String readVersions = readFile(versionIndex.getAbsolutePath());
		readVersions = readVersions.replaceAll("[^a-zA-Z0-9/ ]", "");
		String[] versions = readVersions.split("/");
		ArrayList<String> versionsList = new ArrayList<String>(
				Arrays.asList(versions));
		return versionsList;
	}

	/**
	 * Download the version file
	 * 
	 * @return The download version file
	 * @throws IOException
	 *             If it fails downloading
	 */
	public static File downloadVersionFile(S_Update updater) throws IOException {
		URL url = new URL(updater.getBaseURL() + "/versionindex.txt");
		File output = new File(updater.getOutputFolder().getAbsolutePath()
				+ "/.S_Update/versionindex.tmp.txt");
		if (output.exists())
			output.delete();
		if (!output.getParentFile().exists())
			output.getParentFile().mkdirs();
		return downloadFile(url, output);
	}

	/**
	 * Download a file
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
		output.getParentFile().mkdirs();
		downloadingFile = output;
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.addRequestProperty("User-Agent", "Mozilla/4.76");
		downloadingFileSize = connection.getContentLength();
		ReadableByteChannel rbc = Channels.newChannel(connection.getInputStream());
		FileOutputStream fos = new FileOutputStream(output);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		return output;
	}

	/**
	 * Parse a file index of a version
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
		ArrayList<FileToUpdate> files = new ArrayList<FileToUpdate>();
		File fileIndex = downloadFile(
				new URL(su.getBaseURL() + "/" + version.replace(" ", "%20")
						+ ".txt"), new File(su.getOutputFolder()
						.getAbsolutePath()
						+ "/.S_Update/"
						+ version
						+ ".txt.tmp"));
		BufferedReader br = new BufferedReader(new FileReader(fileIndex));
		while (br.ready()) {
			String line = br.readLine();
			if (!line.replace(" ", "").startsWith("[")) {
				files.add(new FileToUpdate(line, FileToUpdate.DOWNLOAD));
				continue;
			}
			ArrayList<String> args = new ArrayList<String>();
			String currentArg = "";
			char[] chars = line.toCharArray();
			int i = 0;
			for (i = 0; i < chars.length; i++) {
				if (chars[i] == ' ' || chars[i] == '[')
					continue;
				if (chars[i] == ',') {
					args.add(currentArg);
					currentArg = "";
					continue;
				}
				if (chars[i] == ']') {
					args.add(currentArg);
					break;
				}
				currentArg += chars[i];
			}
			i++;
			while (chars[i] == ' ')
				i++;
			boolean add = true;
			if (args.contains("windows") || args.contains("mac")
					|| args.contains("linux")) {
				if (!args.contains("windows")
						&& System.getProperty("os.name").toLowerCase()
								.contains("win"))
					add = false;
				if (!args.contains("mac")
						&& System.getProperty("os.name").toLowerCase()
								.contains("mac"))
					add = false;
				if (!args.contains("linux")
						&& System.getProperty("os.name").toLowerCase()
								.contains("lin"))
					add = false;
			}
			if (args.contains("32") || args.contains("64")) {
				if (!args.contains("32"))
					if (System.getProperty("os.arch").contains("32")
							|| System.getProperty("os.arch").contains("86"))
						if (!System.getProperty("os.arch").contains("64"))
							add = false;
				if (!args.contains("64")
						&& System.getProperty("os.arch").contains("64"))
					add = false;
			}
			FileToUpdate file = new FileToUpdate(line.substring(i),
					FileToUpdate.DOWNLOAD);
			if (args.contains("unzip"))
				file.setAction(FileToUpdate.UNZIP);
			else if (args.contains("remove"))
				file.setAction(FileToUpdate.REMOVE);
			if (add) {
				if (!contains(files, file))
					files.add(file);
			}
		}
		br.close();
		return files;
	}

	/**
	 * Check if an instance of a {@link FileToUpdate} is in an {@link ArrayList}
	 * 
	 * @param al
	 *            The ArrayList
	 * @param f
	 *            The file
	 * @return True if it is present
	 */
	public static boolean contains(ArrayList<FileToUpdate> al, FileToUpdate f) {
		for (FileToUpdate file : al)
			if (file.toString().equals(f.toString()))
				return true;
		return false;
	}

	/**
	 * Remove a {@link FileToUpdate} instance in an {@link ArrayList}
	 * 
	 * @param al
	 *            The ArrayList
	 * @param f
	 *            The file to remove
	 */
	public static void remove(ArrayList<FileToUpdate> al, FileToUpdate f) {
		for (int i = 0; i < al.size(); i++)
			if (al.get(i).toString().equals(f.toString())) {
				al.remove(i);
				return;
			}
	}

	/**
	 * Unzip the zipFile argument in the outputFolder argument
	 * 
	 * @param fileToUnzip
	 *            The zip file to unzip
	 * @param outputFolder
	 *            The output folder
	 */
	public static void unzip(File fileToUnzip, File outputFolder)
			throws IOException {
		ZipFile zipFile = new ZipFile(fileToUnzip);
		Enumeration<?> enu = zipFile.entries();
		unzipingFileSize = 0;
		unzipingLen = 0;
		while (enu.hasMoreElements())
			unzipingFileSize += (long)((ZipEntry) enu.nextElement()).getSize();
		enu = zipFile.entries();
		while (enu.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry) enu.nextElement();
			String name = zipEntry.getName();
			File file = new File(outputFolder, name);
			downloadingFile = file;
			if (name.endsWith("/")) {
				file.mkdirs();
				continue;
			}
			File parent = file.getParentFile();
			if (parent != null)
				parent.mkdirs();
			InputStream is = zipFile.getInputStream(zipEntry);
			FileOutputStream fos = new FileOutputStream(file);
			byte[] bytes = new byte[1024];
			int length;
			while ((length = is.read(bytes)) >= 0) {
				fos.write(bytes, 0, length);
				unzipingLen += length;
			}
			is.close();
			fos.close();
		}
		zipFile.close();
		remove(new File(outputFolder, "__MACOSX/"));
	}

	/**
	 * Remove a file or a directory with all of his content
	 * 
	 * @param file
	 *            The file to delete
	 */
	public static void remove(File file) throws IOException {
		if (!file.exists())
			return;
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files)
				remove(f);
		}
		if (!file.delete())
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
