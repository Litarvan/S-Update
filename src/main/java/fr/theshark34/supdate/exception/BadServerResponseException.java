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

/**
 * The BadServerResponse Exception
 *
 * <p>
 *     This exception is thrown when the server version
 *     response is bad;
 * </p>
 *
 * @version 3.0.0-BETA
 * @author TheShark34
 */
public class BadServerResponseException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
     * The BadServerResponse Exception
     *
     * @param response
     *            The returned response
     */
    public BadServerResponseException(String response) {
        super("Bad server response, we couldn't parse the JSON by the given response : " + response);
    }

}
