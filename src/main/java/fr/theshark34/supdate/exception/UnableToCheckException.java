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
package fr.theshark34.supdate.exception;

import java.io.File;

/**
 * The UnableToCheck Exception
 *
 * <p>
 *     This exception is thrown by the checkFile method of
 *     the CheckMethods when an exception was throwed while
 *     checking a file.
 * </p>
 *
 * @version 3.0.0-BETA
 * @author TheShark34
 */
public class UnableToCheckException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
     * The UnableToCheck Exception
     *
     * @param file
     *            The file that generated the error
     * @param throwedException
     *            The thrown Exception
     */
    public UnableToCheckException(File file, Exception throwedException) {
        super("Can't check the file " + file.getName() + " Exception thrown : " + throwedException);
    }

}
