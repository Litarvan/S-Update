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
package fr.theshark34.supdate.check

import fr.theshark34.supdate.SUpdate
import fr.theshark34.supdate.UnableToCheckException
import java.lang.reflect.Type

/**
 * The Check Method
 *
 * The Check Method is the method to check the files
 * to know if we need to download it or not.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
abstract class CheckMethod
{
    /**
     * The Check Method name, need to be the same as the one
     * on the server, it will be checked to know is the check
     * method is installed on the server.
     */
    abstract val name: String

    /**
     * The Type object for the file list, use something like
     * this :
     *
     * new TypeToken>(){}.getType();
     */
    abstract val listType: Type

    /**
     * Check a file
     *
     * @param sUpdate The current SUpdate instance
     * @param infos The file infos
     *
     * @return If we need to download the file
     */
    @Throws(UnableToCheckException::class)
    abstract fun checkFile(sUpdate: SUpdate, infos: FileInfos): Boolean
}
