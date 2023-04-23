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

package it.mikeslab.util.banknote;

import it.mikeslab.Main;
import it.mikeslab.object.Banknote;
import it.mikeslab.util.transactions.EconomyManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

/**
 * The type Banknote util.
 */
public class BanknoteUtil {

    @Getter
    private static final Map<String, Banknote[]> banknotes = new HashMap<>();


    /**
     * Gets ordered map.
     *
     * @param currency the currency
     * @return the ordered map
     */
    public static LinkedList<Banknote> getOrderedMap(String currency) {
        Banknote[] notes = banknotes.get(currency.toLowerCase());

        if (notes == null) {
            return new LinkedList<>();
        }
        LinkedList<Banknote> list = new LinkedList<>(Arrays.asList(notes));
        list.sort(Comparator.comparingDouble(Banknote::getValue));
        return list;
    }


    /**
     * Loads the banknotes from the plugin configuration file.
     *
     * @param config the plugin configuration file
     */
    public static void loadBanknotes(FileConfiguration config) {
        String sectionName = "banknotes";
        if (config.contains(sectionName)) {

            for (String currency : config.getConfigurationSection(sectionName).getKeys(false)) {
                ConfigurationSection banknoteSection = config.getConfigurationSection(sectionName + "." + currency);
                Banknote[] banknotesArray = new Banknote[banknoteSection.getKeys(false).size()];
                int loop = 0;


                for (String banknote : banknoteSection.getKeys(false)) {
                    double value = config.getDouble(sectionName + "." + currency + "." + banknote + ".value");
                    String name = config.getString(sectionName + "." + currency + "." + banknote + ".name");
                    List<String> lore = config.getStringList(sectionName + "." + currency + "." + banknote + ".lore");
                    int customModelData = config.getInt(sectionName + "." + currency + "." + banknote + ".custom-model-data");
                    Material material = Material.getMaterial(config.getString(sectionName + "." + currency + "." + banknote + ".material"));

                    if (material == null) {
                        Bukkit.getLogger().warning("Material " + config.getString(sectionName + "." + currency + "." + banknote + ".material") + " not found, using PAPER instead.");
                        material = Material.PAPER;
                    }

                    ItemStack stack = Banknote.createDefaultItemStack(material, name, lore, customModelData, value, currency);
                    Banknote banknoteObject = new Banknote(value, stack, currency);
                    banknotesArray[loop] = banknoteObject;
                    loop++;
                }

                banknotes.put(currency.toLowerCase(), banknotesArray);
            }
        }
    }


    /**
     * Checks whether the specified ItemStack is a banknote.
     *
     * @param stack the ItemStack to check
     * @return true if the ItemStack is a banknote, false otherwise
     */
    private static boolean isBanknote(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) {
            return false;
        }

        Double value = stack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Main.getInstance(), "banknote-value"), PersistentDataType.DOUBLE);
        String currency = stack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Main.getInstance(), "banknote-currency"), PersistentDataType.STRING);

        return (value != null && currency != null);
    }

    /**
     * Gets the value of a banknote from an ItemStack.
     *
     * @param clickedItem the ItemStack representing the banknote
     * @return the value of the banknote, or 0 if the ItemStack is not a banknote
     */
    public static Banknote fromItemStack(ItemStack clickedItem) {
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return null;
        }

        if (!isBanknote(clickedItem)) {
            return null;
        }

        double value = clickedItem.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Main.getInstance(), "banknote-value"), PersistentDataType.DOUBLE);
        String currency = clickedItem.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Main.getInstance(), "banknote-currency"), PersistentDataType.STRING);

        Banknote[] banknotesArray = banknotes.get(currency.toLowerCase());
        for (Banknote banknote : banknotesArray) {
            if (banknote.getValue() == value) {
                return banknote;
            }
        }

        return null;
    }


    /**
     * Deposit boolean.
     *
     * @param player   the player
     * @param banknote the banknote
     * @return the boolean
     */
    public static boolean deposit(Player player, Banknote banknote) {
        if (player == null || banknote == null) {
            return false;
        }

        double value = banknote.getValue();
        String currency = banknote.getCurrency();

        if(!removeBanknoteFromHand(banknote, player)) {
            return false;
        }

        EconomyManager economyManager = Main.getInstance().getEconomyManager();
        economyManager.deposit(player.getUniqueId(), currency, value);
        return true;
    }


    private static boolean removeBanknoteFromHand(Banknote banknote, Player player) {

        if(player.getInventory().getItemInMainHand().isSimilar(banknote.getItemStack())) {

            int amountInHand = player.getInventory().getItemInMainHand().getAmount();

            if(amountInHand == 1) {
                player.getInventory().setItemInMainHand(null);
            } else {
                player.getInventory().getItemInMainHand().setAmount(amountInHand - 1);
            }


            return true;
        }

        return false;
    }










}
