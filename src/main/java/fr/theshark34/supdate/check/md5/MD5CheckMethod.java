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
package fr.theshark34.supdate.check.md5;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import fr.theshark34.supdate.SUpdate;
import fr.theshark34.supdate.check.CheckMethod;
import fr.theshark34.supdate.check.FileInfos;
import fr.theshark34.supdate.exception.UnableToCheckException;

/**
 * The MD5 CheckMethod
 *
 * <p>
 *    This is the MD5 check method, using the MD5s to check the
 *    files.
 * </p>
 *
 * @version 3.0.0-BETA
 * @author TheShark34
 */
public class MD5CheckMethod extends CheckMethod {

    public String getName() {
        return "md5-check-method";
    }

    public Type getListType() {
        return new TypeToken<List<MD5FileInfos>>(){}.getType();
    }

    public boolean checkFile(SUpdate sUpdate, FileInfos infos) throws UnableToCheckException {
        // Getting the file infos
        MD5FileInfos md5FileInfo = (MD5FileInfos) infos;

        // Getting the local file
        File localFile = new File(sUpdate.getOutputFolder(), infos.getFileRelativePath());

        // If the local file doesn't exist
        if(!localFile.exists())
            // Returning true
            return true;

        // Getting its MD5
        try {
            InputStream fis = new FileInputStream(localFile);

            byte[] buffer = new byte[1024];
            MessageDigest complete = MessageDigest.getInstance("MD5");
            int numRead;

            do {
                numRead = fis.read(buffer);
                if (numRead > 0)
                    complete.update(buffer, 0, numRead);
            } while (numRead != -1);

            fis.close();
            byte[] md5Bytes = complete.digest();
            String md5 = "";

            for (byte b : md5Bytes)
                md5 += Integer.toString((b & 0xff) + 0x100, 16).substring(1);

            return !md5.equals(md5FileInfo.getMD5());
        } catch (IOException e) {
            // If it failed, throwing an unable to check exception
            throw new UnableToCheckException(localFile, e);
        } catch (NoSuchAlgorithmException e) {
            // If it failed, throwing an unable to check exception
            throw new UnableToCheckException(localFile, e);
        }
    }

}
