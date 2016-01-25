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
package fr.theshark34.supdate.files

import fr.theshark34.supdate.LOGGER
import fr.theshark34.supdate.SUpdate
import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * The Download Task
 *
 * A Task that downloads a file to a destination.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class DownloadTask(val sUpdate: SUpdate, val fileUrl: URL, val dest: File) : Runnable
{
    override fun run()
    {
        // Making the parent folders of the destination file
        dest.parentFile.mkdirs()

        // Printing a message
        LOGGER.info("Downloading file %s", fileUrl)

        try
        {
            // Creating the connection
            val connection = fileUrl.openConnection() as HttpURLConnection

            // Adding some user agents
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.124 Safari/537.36")

            // Creating the data input stream
            val dis = DataInputStream(connection.inputStream)

            // Transfering
            val fileData = ByteArray(connection.contentLength)

            var x: Int
            x = 0
            while (x < fileData.size)
            {
                sUpdate.values.incrementNumberOfTotalDownloadedBytes()
                fileData[x] = dis.readByte()
                x++
            }

            // Closing the input stream
            dis.close()

            // Writing the file
            val fos = FileOutputStream(dest)
            fos.write(fileData)

            // Closing the output stream
            fos.close()

            // Incrementing the DownloadValues 'numberOfDownloadedFiles' variable
            sUpdate.values.numberOfDownloadedFiles = sUpdate.values.numberOfDownloadedFiles + 1
        }
        catch (e: IOException)
        {
            // If it failed printing a warning message
            LOGGER.warning("File $fileUrl wasn't downloaded, error: ", e)
        }
    }
}
