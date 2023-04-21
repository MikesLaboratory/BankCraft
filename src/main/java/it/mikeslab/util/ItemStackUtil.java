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

package it.mikeslab.util;

import com.cryptomorin.xseries.XMaterial;
import it.mikeslab.Main;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Item stack util.
 */
public class ItemStackUtil {
    private static final Map<String, NamespacedKey> namespacedKeys = new HashMap<>();

    /**
     * Loads all the NamespacedKeys used in the plugin.
     * Actually, it only loads the NamespacedKey for the ATM and for the Credit Card.
     */
    public static void loadNamespacedKeys() {
        namespacedKeys.put("atm", new NamespacedKey(Main.getInstance(), "bank-atm"));
        namespacedKeys.put("creditCard", new NamespacedKey(Main.getInstance(), "credit-card"));
        namespacedKeys.put("cardID", new NamespacedKey(Main.getInstance(), "card-id"));
    }


    /**
     * Create stack item stack.
     *
     * @param material    the material
     * @param displayName the display name
     * @return the item stack
     */
    public static ItemStack createStack(XMaterial material, String displayName) {
        ItemStack itemStack = material.parseItem();
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(displayName);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * Create stack item stack.
     *
     * @param material    the material
     * @param displayName the display name
     * @param lore        the lore
     * @return the item stack
     */
    public static ItemStack createStack(XMaterial material, String displayName, List<String> lore) {
        ItemStack itemStack = material.parseItem();
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }


    /**
     * Create stack item stack.
     *
     * @param material        the material
     * @param displayName     the display name
     * @param lore            the lore
     * @param customModelData the custom model data
     * @param pdcKeyVal       the pdc key val
     * @return the item stack
     */
    public static ItemStack createStack(XMaterial material, String displayName, List<String> lore, int customModelData, Map<String, String> pdcKeyVal) {
        ItemStack itemStack = material.parseItem();

        try {

            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(displayName);
            meta.setLore(lore);
            meta.setCustomModelData(customModelData);

            for (Map.Entry<String, String> entry : pdcKeyVal.entrySet()) {
                meta.getPersistentDataContainer().set(getNamespaceKey(entry.getKey()), PersistentDataType.STRING, entry.getValue());
            }

            itemStack.setItemMeta(meta);
        }catch (Exception e){
            e.printStackTrace();
        }
        return itemStack;
    }


    /**
     * Gets namespace key.
     *
     * @param key the key
     * @return the namespace key
     */
    public static NamespacedKey getNamespaceKey(String key) {
        return ItemStackUtil.namespacedKeys.get(key);
    }


    /**
     * Gets filler.
     *
     * @return the filler
     */
    public static ItemStack getFiller() {
        ItemStack itemStack = XMaterial.GRAY_STAINED_GLASS_PANE.parseItem();
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(" ");
        itemStack.setItemMeta(meta);
        return itemStack;
    }





}
