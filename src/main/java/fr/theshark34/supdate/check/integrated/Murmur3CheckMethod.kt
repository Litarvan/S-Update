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
package fr.theshark34.supdate.check.integrated

import com.google.gson.reflect.TypeToken
import fr.litarvan.javamurmur.FileMurmurHasher
import fr.theshark34.supdate.SUpdate
import fr.theshark34.supdate.UnableToCheckException
import fr.theshark34.supdate.check.CheckMethod
import fr.theshark34.supdate.check.FileInfos
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.reflect.Type
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * The Murmur3 CheckMethod
 *
 * This is the Murmur3 check method, using the Murmur3 algorithm
 * create file hash and check it.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class Murmur3CheckMethod : CheckMethod()
{
    private val hasher = FileMurmurHasher()

    override val name: String
        get() = "murmur3"

    override val listType: Type
        get() = object : TypeToken<List<MD5FileInfos>>()
        {
        }.type

    @Throws(UnableToCheckException::class)
    override fun checkFile(sUpdate: SUpdate, infos: FileInfos): Boolean
    {
        // Getting the file infos
        val murmur3FileInfo = infos as Murmur3FileInfos

        // Getting the local file
        val localFile = File(sUpdate.outputFolder, infos.fileRelativePath)

        // If the local file doesn't exist
        if (!localFile.exists())
        // Returning true
            return true

        return murmur3FileInfo.hash.equals(hasher.hash(localFile))
    }
}

/**
 * The Murmur3 FileInfos
 *
 * This is the file info for the Murmur3 Check Method, containing
 * the infos about a file, its name and its hash.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class Murmur3FileInfos(fileRelativePath: String, val hash: String) : FileInfos(fileRelativePath)