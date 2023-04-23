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

package it.mikeslab.object;

import it.mikeslab.Main;
import it.mikeslab.util.Translator;
import it.mikeslab.util.currency.CurrencyUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * The type Banknote.
 */
@RequiredArgsConstructor
@Getter
@Setter
public class Banknote {
    private final double value;
    private final ItemStack itemStack;
    private final String currency;


    /**
     * Creates a default ItemStack for this banknote with the given name and lore.
     *
     * @param material        the material of the banknote ItemStack
     * @param name            the name of the banknote ItemStack
     * @param lore            the lore of the banknote ItemStack
     * @param customModelData the custom model data value for the banknote ItemStack
     * @param value           the value of the banknote
     * @param currency        the currency of the banknote, could be null if the currency system is disabled
     * @return the default ItemStack for this banknote
     */
    public static ItemStack createDefaultItemStack(Material material, String name, List<String> lore, int customModelData, double value, String currency) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(Translator.legacyTranslate(name));
        meta.setLore(Translator.legacyListTranslate(lore));
        meta.setCustomModelData(customModelData);

        meta.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "banknote-value"), PersistentDataType.DOUBLE, value);

        if(!CurrencyUtil.isCurrencyEnabled()) {
            currency = CurrencyUtil.getMainCurrency();
        }

        meta.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "banknote-currency"), PersistentDataType.STRING, currency);

        itemStack.setItemMeta(meta);
        return itemStack;
    }

}

