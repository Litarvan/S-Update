package fr.theshark34.supdate.check.integrated

import com.google.gson.reflect.TypeToken
import fr.theshark34.supdate.SUpdate
import fr.theshark34.supdate.check.CheckMethod
import fr.theshark34.supdate.check.FileInfos
import java.io.File
import java.lang.reflect.Type

/**
 * The Date And Size Check Method
 *
 * This is the Date and Size check method, using the last
 * modification date and the size to check files
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class DateAndSizeCheckMethod : CheckMethod()
{
    override val name: String
        get() = "date-size"

    override val listType: Type
        get() = object : TypeToken<List<DateAndSizeCheckMethod>>()
        {
        }.type

    override fun checkFile(sUpdate: SUpdate, infos: FileInfos): Boolean
    {
        // Getting the file infos
        val fileInfos = infos as DateAndSizeFileInfos

        // Getting the local file
        val localFile = File(sUpdate.outputFolder, infos.fileRelativePath)

        // If the local file doesn't exist
        if (!localFile.exists())
        // Returning true
            return true

        // Getting its size and its last modification date
        val lastMDate = localFile.lastModified()
        val size = localFile.length()

        // Checking
        return lastMDate != fileInfos.lastMDate || size != fileInfos.size
    }
}

/**
 * The Date and Size check method file infos
 *
 * This is the file info for the Date And Size Check Method, containing
 * the infos about a file, its name, its last modification date and its size.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class DateAndSizeFileInfos(fileRelativePath: String, val lastMDate: Long, val size: Long) : FileInfos(fileRelativePath)
