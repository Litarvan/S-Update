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
package fr.theshark34.supdate.application.event.fileaction;

import java.io.File;
import java.net.URL;

import fr.theshark34.supdate.SUpdate;
import fr.theshark34.supdate.files.FileAction;

/**
 * The 'Download' FileAction Event
 *
 * <p>
 *     This event is called when the Download FileAction is
 *     executed. It is given in argument to the onFileAction
 *     method in the applications.
 * </p>
 *
 * @version 3.0.0-BETA
 * @author TheShark34
 */
public class FileActionDownloadEvent extends FileActionEvent {

    /**
     * The URL of the file to download
     */
    private URL fileUrl;

    /**
     * The 'Download' FileAction Event
     *
     * @param sUpdate
     *            The current SUpdate instance
     * @param file
     *            The destination file
     * @param fileUrl
     *            The URL of the file to download
     */
    public FileActionDownloadEvent(SUpdate sUpdate, File file, URL fileUrl) {
        super(sUpdate, FileAction.DOWNLOAD, file);

        this.fileUrl = fileUrl;
    }

    /**
     * Return the URL of the file to download
     *
     * @return The file URL
     */
    public URL getFileUrl() {
        return this.fileUrl;
    }

}
