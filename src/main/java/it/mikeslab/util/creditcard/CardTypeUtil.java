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

package it.mikeslab.util.creditcard;

import it.mikeslab.Main;
import it.mikeslab.util.json.GSONUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

/**
 * The type Card type util.
 */
public class CardTypeUtil {

    private static Map<String, CardType> cardTypes;
    private final FileConfiguration config;
    private static GSONUtil playerDataGsonUtil;

    /**
     * Creates a new CardTypeUtil instance using the specified configuration.
     *
     * @param config the configuration containing the card type properties
     */
    public CardTypeUtil(FileConfiguration config) {
        this.config = config;
        cardTypes = new HashMap<>();
        CardTypeUtil.playerDataGsonUtil = Main.getInstance().getPlayerDataGson();
        loadCardTypes();
    }


    /**
     * Gets list.
     *
     * @return the list
     */
    public List<CardType> getList() {
        return new ArrayList<>(cardTypes.values());
    }


    /**
     * Loads the card types from the configuration.
     */
    public void loadCardTypes() {
        if (config.isConfigurationSection("card-types")) {
            for (String cardTypeName : config.getConfigurationSection("card-types").getKeys(false)) {
                String displayName = config.getString("card-types." + cardTypeName + ".display-name", cardTypeName);
                double depositLimit = config.getDouble("card-types." + cardTypeName + ".deposit-limit", 0.0);
                double withdrawLimit = config.getDouble("card-types." + cardTypeName + ".withdraw-limit", 0.0);
                double perDayWithdrawLimit = config.getDouble("card-types." + cardTypeName + ".daily-withdraw-limit", 0.0);
                double transferLimit = config.getDouble("card-types." + cardTypeName + ".transfer-limit", 0.0);
                Material material = Material.getMaterial(config.getString("card-types." + cardTypeName + ".material", "PAPER"));

                CardType cardType = new CardType(cardTypeName, displayName, depositLimit, withdrawLimit, perDayWithdrawLimit, transferLimit, material);
                cardTypes.put(cardTypeName.toLowerCase(), cardType);
            }
        }
    }

    /**
     * Gets the CardType object for the specified card type name.
     *
     * @param cardTypeName the card type name
     * @return the CardType object, or null if the card type is not found
     */
    public CardType getCardType(String cardTypeName) {
        cardTypeName = cardTypeName.toLowerCase();
        return cardTypes.get(cardTypeName);
    }


    /**
     * Gets card type for player.
     *
     * @param uuid the uuid
     * @return the card type for player
     */
    public static CardType getCardTypeForPlayer(UUID uuid) {

        if(playerDataGsonUtil.getJson(uuid).isEmpty()) {
            return null;
        }


        String cardTypeAsString = playerDataGsonUtil.getJson(uuid).get().get("cardType").getAsString();

        if(!cardTypes.containsKey(cardTypeAsString)) {
            return null;
        }


        return cardTypes.get(cardTypeAsString);
    }
}
