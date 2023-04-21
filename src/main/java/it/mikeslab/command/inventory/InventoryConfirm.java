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

package it.mikeslab.command.inventory;

import com.cryptomorin.xseries.XMaterial;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import it.mikeslab.Main;
import it.mikeslab.util.ItemStackUtil;
import it.mikeslab.util.language.LangKey;
import it.mikeslab.util.language.Language;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

/**
 * The type Inventory confirm.
 */
public class InventoryConfirm {

    /**
     * Open confirm inventory completable future.
     *
     * @param player the player
     * @param title  the title
     * @return the completable future
     */
    public static CompletableFuture<Boolean> openConfirmInventory(Player player, String title) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        String[] setup = {
                "         ",
                "  a   b  ",
                "         "};

        InventoryGui gui = new InventoryGui(Main.getInstance(), title, setup);

        gui.addElement(new StaticGuiElement('a', ItemStackUtil.createStack(
                XMaterial.LIME_WOOL,
                Language.getComponentString(LangKey.CONFIRM)),

                e -> {
                    future.complete(true);
                    return true;
                }));

        gui.addElement(new StaticGuiElement('b', ItemStackUtil.createStack(
                XMaterial.RED_WOOL,
                Language.getComponentString(LangKey.CANCEL)),
                e -> {
                    future.complete(false);
                    return true;
                }));


        gui.setCloseAction(e -> {
            return false;
        });

        gui.setFiller(ItemStackUtil.getFiller());

        gui.show(player);
        return future;
    }

}
