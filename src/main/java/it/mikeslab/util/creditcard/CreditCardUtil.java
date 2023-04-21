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

import com.cryptomorin.xseries.XMaterial;
import com.google.gson.JsonObject;
import it.mikeslab.Main;
import it.mikeslab.util.json.GSONUtil;
import it.mikeslab.util.ItemStackUtil;
import it.mikeslab.util.Translator;
import it.mikeslab.util.book.CustomBook;
import it.mikeslab.util.math.RandomUtils;
import it.mikeslab.util.transactions.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * The type Credit card util.
 */
public class CreditCardUtil {
    private final FileConfiguration config = Main.getInstance().getConfig();
    private final GSONUtil gsonUtil = Main.getInstance().getPlayerDataGson();


    /**
     * Creates a credit card ItemStack from a default configuration.
     *
     * @param ownerUuid the UUID of the credit card owner
     * @param cardID    the card id
     * @return a credit card ItemStack
     */
    public ItemStack createCreditCard(UUID ownerUuid, String cardID) {
        return ItemStackUtil.createStack(
                XMaterial.matchXMaterial(config.getString("credit-card.material", "PAPER")).get(),
                Translator.legacyTranslate(config.getString("credit-card.display-name", "<green>Credit Card")),
                Translator.legacyListTranslate(config.getStringList("credit-card.lore")),
                config.getInt("credit-card.custom-model-data", -1),
                Map.of("creditCard", ownerUuid.toString(),
                        "cardID", cardID));
    }


    /**
     * Creates a credit card ItemStack and adds the document to JSONUtil.
     *
     * @param ownerUuid the UUID of the credit card owner
     * @param cardType  the type of credit card to create
     * @return a credit card ItemStack
     */
    public ItemStack createCreditCard(UUID ownerUuid, CardType cardType) {
        ItemStack creditCard = null;
        try {
            String cardID = String.valueOf(RandomUtils.generateRandomLong(1, 9, RandomUtils.CARD_ID_LENGTH));
            String cardNumber = String.valueOf(RandomUtils.generateRandomLong(1, 9, RandomUtils.CREDIT_CARD_LENGTH));
            int pin = RandomUtils.generateRandomInt(1, 9, RandomUtils.PIN_LENGTH);

            creditCard = createCreditCard(ownerUuid, cardID);

            CustomBook.pinBook(Bukkit.getPlayer(ownerUuid), pin);

            saveData(ownerUuid, cardID, cardNumber, pin, cardType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return creditCard;
    }


    /**
     * Delete credit card.
     *
     * @param ownerUuid the owner uuid
     */
    public void deleteCreditCard(UUID ownerUuid) {
        eraseData(ownerUuid);
    }


    /**
     * Verifies if an ItemStack is a credit card.
     *
     * @param item the ItemStack to verify
     * @return true if the ItemStack is a credit card, false otherwise
     */
    public boolean isCreditCard(ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if(!container.has(ItemStackUtil.getNamespaceKey("creditCard"), PersistentDataType.STRING)) {
            return false;
        }

        if(!container.has(ItemStackUtil.getNamespaceKey("cardID"), PersistentDataType.STRING)) {
            return false;
        }


        EconomyManager economyManager = Main.getInstance().getEconomyManager();
        UUID ownerUUID = UUID.fromString(container.get(ItemStackUtil.getNamespaceKey("creditCard"), PersistentDataType.STRING));
        String cardID = container.get(ItemStackUtil.getNamespaceKey("cardID"), PersistentDataType.STRING);

        return economyManager.hasBankAccount(ownerUUID) && economyManager.compareCardID(cardID, ownerUUID);
    }

    /**
     * Gets the UUID from a credit card ItemStack.
     *
     * @param creditCard the credit card ItemStack
     * @return the UUID of the credit card owner, or null if the ItemStack is not a credit card
     */
    public UUID getOwnerUUID(ItemStack creditCard) {
        if (!isCreditCard(creditCard)) {
            return null;
        }

        String ownerUuidString = creditCard.getItemMeta().getPersistentDataContainer().get(ItemStackUtil.getNamespaceKey("creditCard"), PersistentDataType.STRING);
        return UUID.fromString(ownerUuidString);
    }

    /**
     * Gets card number from uuid.
     *
     * @param ownerUUID the owner uuid
     * @return the card number from uuid
     */
    public String getCardNumberFromUUID(UUID ownerUUID) {
        Optional<JsonObject> json = gsonUtil.getJson(ownerUUID);
        if (json.isEmpty() || !json.get().has("creditCardNumber")) {
            return null;
        }
        return json.get().get("creditCardNumber").getAsString();
    }


    /**
     * Gets pin.
     *
     * @param ownerUUID the owner uuid
     * @return the pin
     */
    public int getPin(UUID ownerUUID) {
        Optional<JsonObject> json = gsonUtil.getJson(ownerUUID);

        if (json.isEmpty() || !json.get().has("pin")) {
            return -1;
        }
        return json.get().get("pin").getAsInt();
    }



    private void saveData(UUID uuid, String cardID, String cardNumber, int pin, CardType cardType) {


        //use flatfile
        JsonObject json = new JsonObject();

        json.addProperty("creditCardNumber", RandomUtils.generateRandomLong(1, 9, RandomUtils.CREDIT_CARD_LENGTH));
        json.addProperty("cardType", cardType.getName());
        json.addProperty("pin", pin);
        json.addProperty("cardID", cardID);

        gsonUtil.setJson(uuid.toString(), json);
        gsonUtil.saveConfig();

    }


    private void eraseData(UUID uuid) {
        gsonUtil.deleteObject(uuid);
        gsonUtil.saveConfig();
    }


}

