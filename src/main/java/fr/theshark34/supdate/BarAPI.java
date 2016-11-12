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

import java.util.concurrent.atomic.AtomicLong;

import fr.theshark34.supdate.files.DownloadTask;

/**
 * The BarAPI
 *
 * <p>
 *     The BarAPI has a lot of variables and methods to
 *     allow you making the best progress bar, but also
 *     YOUR progress bar.
 * </p>
 *
 * @version 3.0.0-BETA
 * @author TheShark34, Wy trem
 */
public class BarAPI {

    /**
     * The download bytes for the current session. 
     * We need an atomic long because files' downloads are multi-threaded.
     */
    private static AtomicLong numberOfTotalDownloadedBytes = new AtomicLong();

    /**
     * The number of bytes to download for the current session. 
     * A atomic long is not necessary because the using of this field is not multi-threaded, {@link Updater#start()}.
     */
    private static AtomicLong numberOfTotalBytesToDownload = new AtomicLong();

    /**
     * The number of downloaded files.
     */
    private static int numberOfDownloadedFiles = 0;

    /**
     * The number of files to download.
     */
    private static int numberOfFileToDownload = 0;

    /**
     * Increments the number of downloaded bytes. Used only by {@link DownloadTask#run()}.
     */
    public static void incrementNumberOfTotalDownloadedBytes() {
    	numberOfTotalDownloadedBytes.incrementAndGet();
    }
    
    /**
     * Sets the number of total bytes to download
     *
     * @param total
     *            The new number of total bytes to download
     */
    public static void setNumberOfTotalBytesToDownload(long total) {
    	numberOfTotalBytesToDownload.set(total);
    }
    
    /**
     * Sets the number of downloaded files
     *
     * @param numberOfDownloadedFiles
     *            The new number of downloaded files
     */
    public static void setNumberOfDownloadedFiles(int numberOfDownloadedFiles) {
    	BarAPI.numberOfDownloadedFiles = numberOfDownloadedFiles;
    }

    /**
     * Sets the number of file to download
     *
     * @param numberOfFileToDownload
     *            The new number of file to download
     */
    public static void setNumberOfFileToDownload(int numberOfFileToDownload) {
    	BarAPI.numberOfFileToDownload = numberOfFileToDownload;
    }

    /**
     * Return the number of total downloaded bytes
     *
     * @return The downloaded bytes
     */
    public static long getNumberOfTotalDownloadedBytes() {
    	return numberOfTotalDownloadedBytes.get();
    }

    /**
     * Return the number of total bytes to download
     *
     * @return The bytes to download
     */
    public static long getNumberOfTotalBytesToDownload() {
    	return numberOfTotalBytesToDownload.get();
    }

    /**
     * Return the number of downloaded files
     *
     * @return The downloaded files
     */
    public static int getNumberOfDownloadedFiles() {
    	return numberOfDownloadedFiles;
    }

    /**
     * Return the number of file to download
     *
     * @return The files to download
     */
    public static int getNumberOfFileToDownload() {
    	return numberOfFileToDownload;
    }

}
