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

import fr.theshark34.supdate.SUpdate;
import fr.theshark34.supdate.application.event.ApplicationEvent;
import fr.theshark34.supdate.files.FileAction;

/**
 * The FileAction Event
 *
 * <p>
 *     This event is given in the onFileAction applications
 *     event, to give some informations about the current
 *     action like which action it is, to which file, etc...
 * </p>
 *
 * This class is mostly a parent class for each Action event
 *
 * @version 3.0.0-BETA
 * @author TheShark34
 */
public class FileActionEvent extends ApplicationEvent {

    /**
     * The executed action
     */
    private FileAction action;

    /**
     * The target file
     */
    private File targetFile;

    /**
     * The FileAction Event
     *
     * @param sUpdate
     *            The current sUpdate instance
     * @param action
     *            The executed action
     * @param targetFile
     *            The target file
     */
    public FileActionEvent(SUpdate sUpdate, FileAction action, File targetFile) {
        super(sUpdate);

        this.action = action;
        this.targetFile = targetFile;
    }

    /**
     * Returns the executed action
     *
     * @return The executed action
     */
    public FileAction getAction() {
        return this.action;
    }

    /**
     * Returns the relative path of the target file
     *
     * @return The target file path
     */
    public File getTargetFile() {
        return this.targetFile;
    }

}
