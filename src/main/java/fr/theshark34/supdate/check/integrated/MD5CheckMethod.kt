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
import kotlin.text.substring

/**
 * The MD5 CheckMethod
 *
 * This is the MD5 check method, using the MD5s to check the
 * files.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class MD5CheckMethod : CheckMethod()
{
    override val name: String
        get() = "md5"

    override val listType: Type
        get() = object : TypeToken<List<MD5FileInfos>>()
        {
        }.type

    @Throws(UnableToCheckException::class)
    override fun checkFile(sUpdate: SUpdate, infos: FileInfos): Boolean
    {
        // Getting the file infos
        val md5FileInfo = infos as MD5FileInfos

        // Getting the local file
        val localFile = File(sUpdate.outputFolder, infos.fileRelativePath)

        // If the local file doesn't exist
        if (!localFile.exists())
        // Returning true
            return true

        // Getting its MD5
        try
        {
            val fis = FileInputStream(localFile)

            val buffer = ByteArray(1024)
            val complete = MessageDigest.getInstance("MD5")
            var numRead: Int

            do
            {
                numRead = fis.read(buffer)
                if (numRead > 0)
                    complete.update(buffer, 0, numRead)
            }
            while (numRead != -1)

            fis.close()
            val md5Bytes = complete.digest()
            var md5 = ""

            for (b in md5Bytes)
                md5 += Integer.toString((b.toInt().and(255)) + 256, 16).substring(1)

            return md5 != md5FileInfo.mD5
        }
        catch (e: IOException)
        {
            // If it failed, throwing an unable to check exception
            throw UnableToCheckException(localFile, e)
        }
        catch (e: NoSuchAlgorithmException)
        {
            // If it failed, throwing an unable to check exception
            throw UnableToCheckException(localFile, e)
        }
    }
}

/**
 * The MD5 FileInfos
 *
 * This is the file info for the MD5 Check Method, containing
 * the infos about a file, its name and its MD5.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class MD5FileInfos(fileRelativePath: String, val mD5: String) : FileInfos(fileRelativePath)
