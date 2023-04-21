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

package it.mikeslab.util.book;

import it.mikeslab.Main;
import it.mikeslab.util.Translator;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The type Custom book.
 */
public class CustomBook {

    /**
     * Pin book.
     *
     * @param player the player
     * @param pin    the pin
     */
    public static void pinBook(Player player, int pin) {
        Component bookTitle = Component.text("<red>Pin");
        Component bookAuthor = Component.text("Mikeslab");
        List<String> lines = Main.getInstance().getConfig().getStringList("pin-book.pages");
        Collection<Component> bookPages = Translator.translate(lines, Map.of("{pin}", pin + ""));

        Book book = Book.book(bookTitle, bookAuthor, bookPages);
        player.openBook(book);
    }
}
