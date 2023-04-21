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

package it.mikeslab.util.math;

/**
 * The type Random utils.
 */
public class RandomUtils {

    /**
     * The constant CREDIT_CARD_LENGTH.
     */
    public static final int CREDIT_CARD_LENGTH = 16;
    /**
     * The constant PIN_LENGTH.
     */
    public static final int PIN_LENGTH = 4;
    /**
     * The constant CARD_ID_LENGTH.
     */
    public static final int CARD_ID_LENGTH = 8;

    /**
     * Generates a random integer between the given min and max values (inclusive).
     *
     * @param min the minimum value
     * @param max the maximum value
     * @return a random integer between min and max
     */
    public static int generateRandomInt(int min, int max) {
        return (int) ((Math.random() * (max - min + 1)) + min);
    }

    /**
     * Generates a random integer of the given size, constructed by appending random
     * numbers between the given min and max values. Note that this method may generate
     * a number with leading zeros.
     *
     * @param min  the minimum value
     * @param max  the maximum value
     * @param size the number of digits in the generated number
     * @return a random integer of the given size
     * @throws IllegalArgumentException if size is less than or equal to zero
     */
    public static int generateRandomInt(int min, int max, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than zero");
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(generateRandomInt(min, max));
        }

        return Integer.parseInt(sb.toString());
    }


    /**
     * Generate random long long.
     *
     * @param min  the min
     * @param max  the max
     * @param size the size
     * @return the long
     */
    public static long generateRandomLong(int min, int max, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than zero");
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(generateRandomInt(min, max));
        }

        return Long.parseLong(sb.toString());
    }

}
