/*
 * GNU GENERAL PUBLIC LICENSE
 * Version 3, 29 June 2007
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package it.mikeslab.util;

import org.bukkit.Location;

/**
 * The type Location util.
 */
public class LocationUtil {

    /**
     * Gets raw location.
     *
     * @param fullLocation a Location with pitch and yaw   Returns a Location without pitch and yaw
     * @return the raw location
     */
    public static Location getRawLocation(Location fullLocation) {
        return new Location(fullLocation.getWorld(), fullLocation.getX(), fullLocation.getY(), fullLocation.getZ());
    }


    /**
     * Gets location string.
     *
     * @param location a Location   Returns a String with the location in the format "world,x,y,z"
     * @return the location string
     */
    public static String getLocationString(Location location) {
        return location.getX() + ", " + location.getY() + ", " + location.getZ();
    }



}
