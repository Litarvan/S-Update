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

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;

/**
 * An Util class
 *
 * <p>
 *     A Class containing some usefull methods
 * </p>
 *
 * @version 2.2.0-SNAPSHOT
 * @author TheShark34
 */
public class Util {

    /**
     * Check if a local file exists in the online file list
     *
     * @param su
     *            The current SUpdate instance to get the output folder
     * @param onlineFilesList
     *            The online file list
     * @param localFile
     *            The local file
     * @return True if it exists in, false if not
     */
    public static boolean contains(SUpdate su, ArrayList<OnlineFile> onlineFilesList, File localFile) {
        for(OnlineFile onlineFile : onlineFilesList) {
            File file = new File(su.getOutputFolder(), onlineFile.getFile());
            if(file.getAbsolutePath().equals(localFile.getAbsolutePath()))
                return true;
        }
        return false;
    }

    /**
     * Lists a folder recursively
     *
     * @param folder
     *            The folder to list
     * @return A list of the listed files
     */
    public static ArrayList<File> listFiles(File folder, FileIgnorer ignorer) {
        ArrayList<File> files = new ArrayList<File>();

        if(!folder.isDirectory())
            return files;

        File[] folderFiles = folder.listFiles();
        for(File f : folderFiles)
            if(ignorer.needToIgnore(f))
                continue;
            else if(f.isDirectory())
                files.addAll(listFiles(f, ignorer));
            else
                files.add(f);

        return files;
    }

    /**
     * Gets the MD5 of a file
     *
     * @param file
     *            The file to get the MD5
     * @throws IOException
     *            If it failed to read the file
     * @throws NoSuchAlgorithmException
     *            If it failed to get the MD5 algorithm
     * @return The generated MD5
     */
    public static String getMD5(File file) throws IOException, NoSuchAlgorithmException{
        InputStream fis =  new FileInputStream(file);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0)
                complete.update(buffer, 0, numRead);
        } while (numRead != -1);

        fis.close();

        byte[] bytes = complete.digest();
        String md5 = "";

        for (int i=0; i < bytes.length; i++)
            md5 += Integer.toString( ( bytes[i] & 0xff ) + 0x100, 16).substring( 1 );

        return md5;
    }

    /**
     * Unzip a file in its parent directory
     *
     * @param zip
     *            The file to unzip
     */
    public static void unzip(File zip) throws IOException {
        // Getting the zip file
        java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(zip);

        // Getting an enumeration of all its entries
        Enumeration<?> enu = zipFile.entries();

        // For each entries in the list
        while (enu.hasMoreElements()) {
            // Getting the entry
            ZipEntry zipEntry = (ZipEntry) enu.nextElement();

            // Getting its name
            String name = zipEntry.getName();

            // Getting the output file
            File file = new File(zip.getParentFile(), name);

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
            while ((length = is.read(bytes)) >= 0)
                // Writing the buffer
                fos.write(bytes, 0, length);

            // Closing the input stream
            is.close();

            // Closing the output stream
            fos.close();
        }

        // Closing the zip file
        zipFile.close();
    }

    /**
     * Deletes all the empty folder
     *
     * @param folder
     *            The folder to delete the empty folders
     */
    public static void deleteEmptyFolders(File folder) {
        // Getting the list of the files
        File[] files = folder.listFiles();

        // For each file in the list
        for (File f : files)
            // If its a directory
            if (f.isDirectory()) {
                // Deleting his empty folder
                deleteEmptyFolders(f);

                // Then if the directory is now empty
                if (f.listFiles().length == 0)
                    // Deleting it
                    f.delete();
            }
    }

}
