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
package fr.theshark34.supdate.application.event;

import fr.theshark34.supdate.SUpdate;

/**
 * The FileDownloading Event
 *
 * <p>
 *     This event is given in the whileDownloading applications
 *     event, to give some informations about the current
 *     downloading file like its relative path, its size, etc...
 * </p>
 *
 * @version 3.0.0-SNAPSHOT
 * @author TheShark34
 */
public class FileDownloadingEvent extends ApplicationEvent {

    /**
     * The relative path of the current downloading file
     */
    private String filePath;

    /**
     * The number of downloaded bytes of the current
     * downloading file
     */
    private long fileDownloadedBytes;

    /**
     * The total size (in bytes) of the current downloading
     * file
     */
    private long downloadingFileSize;

    /**
     * The FileDownloading Event
     *
     * @param sUpdate
     *            The current SUpdate instance
     * @param filePath
     *            The relative path of the current downloading
     *            file
     * @param fileDownloadedBytes
     *            The number of downloaded bytes of the
     *            current downloading file
     * @param downloadingFileSize
     *            The total size (in bytes) of the current
     *            downloading file
     */
    public FileDownloadingEvent(SUpdate sUpdate, String filePath, long fileDownloadedBytes, long downloadingFileSize) {
        super(sUpdate);

        this.filePath = filePath;
        this.fileDownloadedBytes = fileDownloadedBytes;
        this.downloadingFileSize = downloadingFileSize;
    }

    /**
     * Returns the relative path of the current downloading
     * file
     *
     * @return The path of the current file
     */
    public String getFilePath() {
        return this.filePath;
    }

    /**
     * Returns the number of downloaded bytes of the current
     * downloading file
     *
     * @return The file downloaded bytes
     */
    public long getFileDownloadedBytes() {
        return this.fileDownloadedBytes;
    }

    /**
     * Returns the total size (in bytes) of the current
     * downloading file
     *
     * @return The file total size
     */
    public long getDownloadingFileSize() {
        return this.downloadingFileSize;
    }

}
