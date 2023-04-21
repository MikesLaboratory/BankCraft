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

package it.mikeslab.util.atm;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import it.mikeslab.Main;
import it.mikeslab.util.ItemStackUtil;
import it.mikeslab.util.LocationUtil;
import it.mikeslab.util.Translator;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;

/**
 * A utility class for managing ATM blocks in a Minecraft plugin.
 */
@UtilityClass
public class ATMUtil {

    // A cache of ATM locations for improved performance
    private final Cache<Location, Boolean> atmLocations = Caffeine.newBuilder()
            .maximumSize(1000)
            .build();

    // The ItemStack representing an ATM block
    private ItemStack atmItem;

    /**
     * Checks whether a given block is an ATM block.
     *
     * @param block the block to check
     * @return true if the block is an ATM block, false otherwise
     */
    public boolean isATM(Block block) {
        return atmLocations.getIfPresent(LocationUtil.getRawLocation(block.getLocation())) != null;
    }

    /**
     * Adds an ATM block to the set of ATM locations.
     *
     * @param block the ATM block to add
     */
    public void addATM(Block block) {
        Location location = block.getLocation();
        atmLocations.put(location, true);
    }

    /**
     * Removes an ATM block from the set of ATM locations.
     *
     * @param block the ATM block to remove
     */
    public void removeATM(Block block) {
        Location location = block.getLocation();
        atmLocations.invalidate(location);
    }

    /**
     * Saves the set of ATM locations to a configuration file.
     *
     * @param config the configuration file to save to
     */
    public void saveATMs(FileConfiguration config) {
        // Remove any existing ATM data from the configuration file
        config.set("atms", null);

        // Create a new section for the ATM data
        ConfigurationSection section = config.createSection("atms");

        // Iterate over the ATM locations in the cache and add them to the configuration section
        int index = 0;

        if(atmLocations.asMap().keySet().isEmpty()) {
            return;
        }

        for (Location location : atmLocations.asMap().keySet()) {
            section.set(index + ".world", location.getWorld().getName());
            section.set(index + ".x", location.getBlockX());
            section.set(index + ".y", location.getBlockY());
            section.set(index + ".z", location.getBlockZ());
            index++;
        }

        // Save the configuration file
        Main.getInstance().saveConfig();

        //clearing cache
        atmLocations.invalidateAll();
    }

    /**
     * Loads the set of ATM locations from a configuration file.
     *
     * @param config the configuration file to load from
     */
    public void loadATMs(FileConfiguration config) {
        // Remove any existing ATM data from the cache
        atmLocations.invalidateAll();

        // Get the ATM data section from the configuration file
        ConfigurationSection section = config.getConfigurationSection("atms");

        // If there is no ATM data in the configuration file, return
        if (section == null || section.getKeys(false).isEmpty()) {
            return;
        }

        // Iterate over the ATM data in the configuration section and add it to the cache
        for (String key : section.getKeys(false)) {
            String worldName = section.getString(key + ".world");
            int x = section.getInt(key + ".x");
            int y = section.getInt(key + ".y");
            int z = section.getInt(key + ".z");

            Location location = new Location(Bukkit.getWorld(worldName), x, y, z);
            atmLocations.put(location, true);
        }
    }

    /**
     * Gets the ItemStack representing an ATM block.
     *
     * @return the ATM block ItemStack
     */
    public ItemStack getATMPlaceableItem() {
        // If the ATM block ItemStack has not
        // been created yet, create it using the configuration file data
        if (atmItem == null) {
            ConfigurationSection section = Main.getInstance().getConfig().getConfigurationSection("atm-item");
            Material material = Material.valueOf(section.getString("material", "CHEST"));
            String displayName = Translator.legacyTranslate(section.getString("display-name", "ATM"));
            List<String> lore = Translator.legacyListTranslate(section.getStringList("lore"));
            int customModelData = section.getInt("custom-model-data", -1);
            atmItem = ItemStackUtil.createStack(material, displayName, lore, customModelData, Map.of("atm", "true"));
        }

        return atmItem;
    }

    /**
     * Checks whether a given ItemStack is an ATM block.
     *
     * @param stack the ItemStack to check
     * @return true if the ItemStack is an ATM block, false otherwise
     */
    public boolean isATM(ItemStack stack) {
        if(stack.getItemMeta() == null) {
            return false;
        }
        return Boolean.parseBoolean(stack.getItemMeta().getPersistentDataContainer().get(ItemStackUtil.getNamespaceKey("atm"), PersistentDataType.STRING));
    }
}
