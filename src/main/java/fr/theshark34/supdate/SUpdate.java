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

import fr.theshark34.supdate.application.Application;
import fr.theshark34.supdate.application.ApplicationManager;
import fr.theshark34.supdate.files.FileManager;

import java.io.File;

/**
 * The S-Update object
 *
 * <p>
 *    The S-Update object contains all the informations about the update
 *    like the applications, the server adress, the output folder, and
 *    things like this.
 * </p>
 *
 * How to start updating ?
 *
 * <p>
 *     To start updating, just use the start method.
 * </p>
 *
 * What server are you talking about ?
 *
 * <p>
 *     S-Update is an update system. To use it, it must have a distant
 *     repository where the original files are hosted, and also he need
 *     some indications of the files, and things like this. To do this,
 *     use the S-Update Server. Download it here :
 *     https://github.com/TheShark34/S-Update-Server/releases
 * </p>
 *
 * What the fuck are applications ?
 *
 * <p>
 *     The applications are things to give superpowers to S-Update.
 *     Applications can be server-only, client-only, or in both.
 *     There is some included applications (but there are not enabled
 *     by default). To add an application, use SUpdate.addApplication(
 *     class extending Application instance). To create your own
 *     application, in client create the Application class, then use
 *     the events to add some things, but for some clients things like
 *     sending request, or server application, use the wiki in the Github
 *     repo.
 * </p>
 *
 * @version 3.0.0-SNAPSHOT
 * @author TheShark34
 */
public class SUpdate {

    /**
     * The url of the S-Update server
     */
    private String serverUrl;

    /**
     * The output folder of the update
     */
    private File outputFolder;

    /**
     * The current application manager
     */
    private ApplicationManager applicationManager = new ApplicationManager();

    /**
     * The current file manager
     */
    private FileManager fileManager = new FileManager(this);

    /**
     * The main S-Update constructor, create a basic S-Update object
     *
     * @param serverUrl
     *            The url of the S-Update server
     * @param outputFolder
     *            The output folder of the update
     */
    public SUpdate(String serverUrl, File outputFolder) {
        this.serverUrl = serverUrl;
        this.outputFolder = outputFolder;
    }

    /**
     * Starts the update ! Create the Updater, call the applications
     * events, and start the Updater !
     */
    public void start() {

    }

    /**
     * Sets the URL of the S-Update-Server
     *
     * @param serverUrl
     *            The server url to set
     */
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    /**
     * Returns the URL of the S-Update server, given with the setter
     * or the constructor
     *
     * @return The server url
     */
    public String getServerUrl() {
        return this.serverUrl;
    }

    /**
     * Sets the output folder where to do the update
     *
     * @param outputFolder
     *            The new output folder
     */
    public void setOutputFolder(File outputFolder) {
        this.outputFolder = outputFolder;
    }

    /**
     * Returns the output folder where to do the update, given with
     * the setter or the constructor
     *
     * @return The output folder
     */
    public File getOutputFolder() {
        return this.outputFolder;
    }

    /**
     * Add an application to S-Update (same as getApplicationManager()
     * .addApplication
     *
     * @param application
     *            The application to add
     */
    public void addApplication(Application application) {
        applicationManager.addApplication(this, application);
    }

    /**
     * Returns the current Application Manager
     *
     * @return The application manager
     */
    public ApplicationManager getApplicationManager() {
        return this.applicationManager;
    }

}
