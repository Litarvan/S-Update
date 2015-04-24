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
package fr.theshark34.supdate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The Downloader
 *
 * <p>
 *     Multi Threaded downloader
 * </p>
 *
 * @version 2.0-SNAPSHOT
 * @author TheShark34
 */
public class Downloader {

    private ExecutorService pool = Executors.newFixedThreadPool(10);
    private static int totalDownloaded = 0;

    public void download(URL url, File file) throws IOException {
        pool.execute(new DownloadTask(url, file));
    }

    public static void downloadFile(URL url, File file) throws IOException {
        file.getParentFile().mkdirs();

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla/4.76");
        ReadableByteChannel rbc = Channels.newChannel(connection
                .getInputStream());

        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

        fos.close();
        rbc.close();

        totalDownloaded++;
    }

    public static int getTotalDownloaded() {
        return totalDownloaded;
    }

}
