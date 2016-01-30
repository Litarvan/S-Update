/*
 * Copyright 2015-2016 Adrien Navratil
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
package fr.theshark34.supdate

import java.io.File
import java.io.IOException

/**
 * The SUpdate Exception
 *
 * The main exception, every exception need to extends this one.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
open class SUpdateException(message: String) : Exception(message)

/**
 * The BadServerVersion Exception
 *
 * This exception is thrown when the server version is not
 * the required version, or the server revision are not the required.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class BadServerVersionException(minVersion: String, serverVersion: String, revisionError: Boolean) : SUpdateException(if (revisionError)
    "Bad server revision, server revision need to be $minVersion but is $serverVersion"
else
    "Bad server version, need to be at least $minVersion but is $serverVersion")

/**
 * The BadServerResponse Exception
 *
 * This exception is thrown when the server version
 * response is bad;
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class BadServerResponseException(response: String) : SUpdateException("Bad server response, we couldn't parse the JSON by the given response : " + response)

/**
 * The FileNoPermission Exception
 *
 * This exception is thrown when we try to do something
 * with a file, but nothing happened.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class FileNoPermissionException(file: File) : IOException("No permission for the file " + file.absolutePath)

/**
 * The ServerDisabledException Exception
 *
 * This exception is thrown when the server is disabled.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class ServerDisabledException : SUpdateException("The server is disabled")

/**
 * The ServerMissingSomething Exception
 *
 * This exception is thrown when the server is missing a
 * check method, an application, or something like this.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class ServerMissingSomethingException(missingThing: String) : SUpdateException("Server need to have " + missingThing)

/**
 * The UnableToCheck Exception
 *
 * This exception is thrown by the checkFile method of
 * the CheckMethods when an exception was thrown while
 * checking a file.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class UnableToCheckException(file: File, thrownException: Exception) : SUpdateException("Can't check the file " + file.name + " Exception thrown : " + thrownException)