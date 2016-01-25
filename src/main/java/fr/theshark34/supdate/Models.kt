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

/**
 * The Version Response
 *
 * This is the model of the 'version' request response.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class VersionResponse(val version: String)

/**
 * The State Response
 *
 * This is the model of the '/server/is-enabled' request response.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class StateResponse(val isEnabled: Boolean)

/**
 * The CheckCheckMethod Response
 *
 * This is the model of the 'Check Check Method' request response.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class SizeResponse(val size: Long)

/**
 * The Check Response
 *
 * This is the model of the 'Check something' request response.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class CheckResponse(val isPresent: Boolean)
