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
package fr.theshark34.supdate.models;

/**
 * The Check Response
 *
 * <p>
 *    This is the model of the 'Check something' request response.
 * </p>
 *
 * @version 3.0.0-BETA
 * @author TheShark34
 */
public class CheckResponse {

    /**
     * If the application/check method is present on the server
     */
    private boolean present;

    /**
     * The CheckApplication Response
     *
     * @param present
     *            If the application/check method is present on the server
     */
    public CheckResponse(boolean present) {
        this.present = present;
    }

    /**
     * Return if the application/check method is present on the server
     *
     * @return If the application/check method is on the server
     */
    public boolean isPresent() {
        return present;
    }

}
