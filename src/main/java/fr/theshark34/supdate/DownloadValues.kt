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

import java.util.concurrent.atomic.AtomicLong

import fr.theshark34.supdate.files.DownloadTask

/**
 * The DownloadValues
 *
 * The DownloadValues has a lot of variables and methods to
 * allow you making the best progress bar, but also
 * YOUR progress bar.
 *
 * @version 3.2.0-BETA
 * @author Litarvan, Wy trem
 */
class DownloadValues
{
    /**
     * The download bytes for the current session.
     * We need an atomic long because files' downloads are multi-threaded.
     */
    private val numberOfTotalDownloadedBytes = AtomicLong()

    /**
     * The number of bytes to download for the current session.
     * A atomic long is not necessary because the using of this field is not multi-threaded, [Updater.start].
     */
    private val numberOfTotalBytesToDownload = AtomicLong()

    /**
     * The number of downloaded files.
     */
    var numberOfDownloadedFiles = 0

    /**
     * The number of files to download.
     */
    var numberOfFileToDownload = 0

    /**
     * Increments the number of downloaded bytes. Used only by [DownloadTask.run].
     */
    fun incrementNumberOfTotalDownloadedBytes()
    {
        numberOfTotalDownloadedBytes.incrementAndGet()
    }

    /**
     * Sets the number of total bytes to download

     * @param total
     * *            The new number of total bytes to download
     */
    fun setNumberOfTotalBytesToDownload(total: Long)
            = numberOfTotalBytesToDownload.set(total)

    /**
     * Return the number of total downloaded bytes
     *
     * @return The downloaded bytes
     */
    fun getNumberOfTotalDownloadedBytes(): Long
            = numberOfTotalDownloadedBytes.get()

    /**
     * Return the number of total bytes to download
     *
     * @return The bytes to download
     */
    fun getNumberOfTotalBytesToDownload(): Long
            = numberOfTotalBytesToDownload.get()

}
