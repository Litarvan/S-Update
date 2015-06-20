package fr.theshark34.supdate.application;

import java.util.ArrayList;

/**
 * The Application Manager
 *
 * <p>
 *     This is the application manager, where all the applications
 *     are registered. It call their events, and things like this.
 * </p>
 *
 * @version 3.0.0-SNAPSHOT
 * @author TheShark34
 */
public class ApplicationManager {

    /**
     * The list of all the loaded applications
     */
    private ArrayList<Application> applications;

    /**
     * Load, then add an application
     *
     * @param application
     *            The application to add
     */
    public void addApplication(Application application) {
        // Adding the application to the list
        applications.add(application);

        // Then sending the onInit event
        application.onInit();
    }

    /**
     * Returns the list of all the loaded applications
     *
     * @return The list of applications
     */
    public ArrayList<Application> getApplications() {
        return applications;
    }

}
