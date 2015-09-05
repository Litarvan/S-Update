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
 * The State Response
 *
 * <p>
 *    This is the model of the 'getstate' request response.
 * </p>
 *
 * @version 3.0.0-BETA
 * @author TheShark34
 */
public class StateResponse {

    /**
     * The received state
     */
    private boolean enabled;

    /**
     * The StateResponse
     *
     * @param enabled
     *            The received state
     */
    public StateResponse(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Return the received state
     *
     * @return True if the server is enabled, false if not
     */
    public boolean isEnabled() {
        return enabled;
    }

}
