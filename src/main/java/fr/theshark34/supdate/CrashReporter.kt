/*
 * Copyright 2015 Adrien Navratil
 *
 * This file is part of the OpenLauncherLib.
 * The OpenLauncherLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The OpenLauncherLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the OpenLauncherLib.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.theshark34.supdate

import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.JOptionPane

/**
 * The Crash Reporter
 *
 * The Crash Reporter can catch errors and save them as a crash report.
 *
 * @author Litarvan
 * @version 3.2.0-BETA
 */
class CrashReporter(var dir: File)
{
    /**
     * Catch an error and write it to a crash
     *
     * @param e       The error to catch
     */
    fun catchError(e: Exception)
    {
        LOGGER.error("Exception caught !")

        println(makeCrashReport(e))

        val msg: String

        try
        {
            val report = writeError(e)
            msg = "\nThe crash report is in : " + report.absolutePath + ""
        }
        catch (e2: IOException)
        {
            LOGGER.error("Error while writing the crash report")
            e.printStackTrace()
            msg = "\nAnd unable to write the crash report :( : " + e2
        }

        JOptionPane.showMessageDialog(null, "Unable to update : \n" + e + "\n" + msg, "Error", JOptionPane.ERROR_MESSAGE)

        System.exit(1)
    }

    /**
     * Write a stacktrace to a file
     *
     * @param e The exception
     *
     * @return The file where the crash was saved
     *
     * @throws IOException If it failed to write the crash
     */
    @Throws(IOException::class)
    fun writeError(e: Exception): File
    {
        var number = 0
        var file: File = File(dir, "crash-$number.txt")
        while (file.exists())
        {
            number++
            file = File(dir, "crash-$number.txt")
        }

        LOGGER.info("Writing crash report to : ${file.absolutePath}")

        file.parentFile.mkdirs()

        val fw = FileWriter(file)

        fw.write(makeCrashReport(e))

        fw.close()

        return file
    }

    companion object
    {

        /**
         * Create a crash report with an exception
         *
         * @param e           The exception to make the crash report
         *
         * @return The made crash report
         */
        fun makeCrashReport(e: Exception): String
        {
            val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            val date = Date()

            var report = "# S-Update Crash Report\n\r" +
                         "#\n\r" +
                         "# At : " + dateFormat.format(date) + "\n\r" +
                         "#\n\r" +
                         "# Exception : " + e.javaClass.simpleName + "\n\r"

            report += "\n\r# " + e.toString()

            val stackTrace = e.stackTrace
            for (element in stackTrace)
                report += "\n\r#     " + element

            val cause = e.cause
            if (cause != null)
            {
                report += "\n\r# Caused by: " + cause.toString()

                val causeStackTrace = cause.stackTrace
                for (element in causeStackTrace)
                    report += "\n\r#     " + element
            }

            return report
        }
    }
}