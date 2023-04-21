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

package it.mikeslab.util.json;

import com.google.gson.*;
import net.milkbowl.vault.economy.EconomyResponse;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * The type Gson util.
 */
public class GSONUtil {

    private final Gson gson;
    private final File dataFolder;
    private final File configFile;
    private final Map<String, JsonObject> data;

    /**
     * Creates a new instance of JSONUtil and loads JSON data from the config file.
     *
     * @param dataFolder the data folder where the config file should be saved
     * @param filename   the name of the config file
     */
    public GSONUtil(File dataFolder, String filename) {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.dataFolder = dataFolder;
        this.configFile = new File(dataFolder, filename);
        this.data = new HashMap<>();
        createConfigIfNotExists();
        loadConfig();
    }

    /**
     * Gets the JSON data for the specified UUID.
     *
     * @param uuid the UUID
     * @return the JSON data
     */
    public Optional<JsonObject> getJson(String uuid) {
        return Optional.ofNullable(data.get(uuid));
    }

    /**
     * Gets json.
     *
     * @param uuid the uuid
     * @return the json
     */
    public Optional<JsonObject> getJson(UUID uuid) {
        return Optional.ofNullable(data.get(uuid.toString()));
    }


    /**
     * Sets the JSON data for the specified UUID.
     *
     * @param uuid the UUID
     * @param json the JSON data
     */
    public void setJson(String uuid, JsonObject json) {
        data.put(uuid, json);
        saveConfig();
    }

    /**
     * Saves the JSON data to the config file.
     */
    public void saveConfig() {
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the JSON data from the config file.
     */
    public void loadConfig() {
        try (FileReader reader = new FileReader(configFile)) {
            JsonElement jsonData = JsonParser.parseReader(reader);

            // Check if jsonData is a JsonObject before processing it
            if (jsonData.isJsonObject()) {
                JsonObject jsonObject = jsonData.getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    JsonObject json = entry.getValue().getAsJsonObject();
                    data.put(entry.getKey(), json);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Deletes the JSON data for the specified UUID.
     *
     * @param uuid the UUID
     */
    public void deleteObject(String uuid) {
        data.remove(uuid);
        saveConfig();
    }


    /**
     * Delete object.
     *
     * @param uuid the uuid
     */
    public void deleteObject(UUID uuid) {
        data.remove(uuid.toString());
        saveConfig();
    }

    /**
     * Creates the config file if it doesn't already exist.
     */
    public void createConfigIfNotExists() {
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Key set string [ ].
     *
     * @return the string [ ]
     */
    public String[] keySet() {
        String[] keys = new String[data.size()];
        int i = 0;
        for (String uuid : data.keySet()) {
            keys[i] = uuid;
            i++;
        }
        return keys;
    }


    /**
     * Gets the UUID for the specified credit card number.
     *
     * @param creditCardNumber the credit card number
     * @return the UUID corresponding to the credit card number, or null if not found
     */
    public Optional<String> getUUIDByCreditCardNumber(String creditCardNumber) {
        for (Map.Entry<String, JsonObject> entry : data.entrySet()) {
            JsonObject json = entry.getValue();
            if (json.has("creditCardNumber") && json.get("creditCardNumber").getAsString().equals(creditCardNumber)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

}
