package fr.theshark34.supdate.exception;

import java.io.File;
import java.io.IOException;

/**
 * The FileNoPermission Exception
 *
 * <p>
 *     This exception is thrown when we try to do something
 *     with a file, but nothing happened.
 * </p>
 *
 * @version 3.0.0-SNAPSHOT
 * @author TheShark34
 */
public class FileNoPermissionException extends IOException {

    public FileNoPermissionException(File file) {
        super("No permission for the file " + file.getName());
    }

}
