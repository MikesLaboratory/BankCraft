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

package it.mikeslab.command.inventory;

import com.cryptomorin.xseries.XMaterial;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import it.mikeslab.Main;
import it.mikeslab.util.ItemStackUtil;
import it.mikeslab.util.Translator;
import it.mikeslab.util.currency.CurrencyUtil;
import it.mikeslab.util.language.LangKey;
import it.mikeslab.util.language.Language;
import it.mikeslab.util.transactions.Transaction;
import it.mikeslab.util.transactions.TransactionUtil;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The type Inventory transactions.
 */
public class InventoryTransactions {


    /**
     * Gets transactions inventory.
     *
     * @param player     the player
     * @param ownerUUID  the owner uuid
     * @param filterDate the filter date
     * @return the transactions inventory
     */
    public static InventoryGui getTransactionsInventory(Player player, UUID ownerUUID, String filterDate) {
        String[] setup = {
                  "         ",
                  " aaaaaaa ",
                  " aaaaaaa ",
                  " aaaaaaa ",
                  " aaaaaaa ",
                  "e  b c  d"};

        String transactionHistoryString = Language.getString(LangKey.TRANSACTION_HISTORY);
        String title = filterDate == null ? transactionHistoryString : transactionHistoryString + " (" + filterDate + ")";

        String finalTitle = Translator.legacyTranslate(title);

        InventoryGui inventoryGui = new InventoryGui(Main.getInstance(), finalTitle, setup);
        TransactionUtil transactionUtil = Main.getInstance().getTransactionUtil();
        List<Transaction> transactions = transactionUtil.getTransactionsForAccount(ownerUUID, 200, 0);

        transactions.sort((t1, t2) -> Long.compare(t2.getTimestamp(), t1.getTimestamp()));

        GuiElementGroup group = new GuiElementGroup('a');

        if (filterDate != null) {
            transactions = transactions.stream()
                    .filter(transaction -> filterDate.equals(formatDate(transaction.getTimestamp())))
                    .collect(Collectors.toList());
        }

        for (Transaction transaction : transactions) {
            String currencySymbol = CurrencyUtil.getSymbol(transaction.getCurrency());
            String displayName = Language.getComponentString(LangKey.TRANSACTION, Map.of("%s", formatDate(transaction.getTimestamp())));
            List<String> lore = new ArrayList<>();
            lore.add(Language.getComponentString(LangKey.TRANSACTION_AMOUNT, Map.of("%s", transaction.getAmount() + currencySymbol)));
            lore.add(Language.getComponentString(LangKey.TRANSACTION_SENDER, Map.of("%s", "" + Bukkit.getOfflinePlayer(transaction.getFrom()).getName())));
            lore.add(Language.getComponentString(LangKey.TRANSACTION_RECEIVER, Map.of("%s", "" + Bukkit.getOfflinePlayer(transaction.getTo()).getName())));
            lore.add(Language.getComponentString(LangKey.TRANSACTION_REASON, Map.of("%s", cutString(transaction.getReason(), 50))));
            if (CurrencyUtil.isCurrencyEnabled())
                lore.add(Language.getComponentString(LangKey.TRANSACTION_CURRENCY, Map.of("%s", transaction.getCurrency())));
            lore.add(Language.getComponentString(LangKey.TRANSACTION_ID, Map.of("%s", transaction.getId())));

            ItemStack itemStack = ItemStackUtil.createStack(XMaterial.PAPER, displayName, lore);


            group.addElement(new StaticGuiElement('a', itemStack));
        }

        inventoryGui.addElements(group);
        inventoryGui.addElement(new GuiPageElement('b', new ItemStack(Material.REDSTONE), GuiPageElement.PageAction.PREVIOUS, Language.getComponentString(LangKey.PREVIOUS_PAGE)));
        inventoryGui.addElement(new GuiPageElement('c', new ItemStack(Material.ARROW), GuiPageElement.PageAction.NEXT, Language.getComponentString(LangKey.NEXT_PAGE)));

        if(filterDate == null) {
            inventoryGui.addElement(new StaticGuiElement('d', ItemStackUtil.createStack(XMaterial.BOOK, Language.getComponentString(LangKey.FILTER_BY_DATE)), click -> {
                openFilterInput(player, ownerUUID);
                return true;
            }));
        } else {
            inventoryGui.addElement(new StaticGuiElement('e', ItemStackUtil.createStack(XMaterial.BARRIER, Language.getComponentString(LangKey.REMOVE_DATE_FILTER)), click -> {
                getTransactionsInventory(player, ownerUUID, null).show(player);
                return true;
            }));
        }


        inventoryGui.setCloseAction(close -> { return false; });

        inventoryGui.setFiller(ItemStackUtil.getFiller());

        return inventoryGui;
    }

    private static void openFilterInput(Player player, UUID ownerUUID) {
        new AnvilGUI.Builder()
                .onClose(plr -> getTransactionsInventory(plr, ownerUUID, null).show(plr))
                .onComplete((context) -> {
                    try {
                        LocalDate.parse(context.getText(), DateTimeFormatter.ofPattern("dd.MM.yy"));
                        getTransactionsInventory(context.getPlayer(), ownerUUID, context.getText()).show(player);
                        return AnvilGUI.Response.close();
                    } catch (Exception e) {
                        context.getPlayer().sendMessage(Language.getComponentString(LangKey.INVALID_DATE_FORMAT));
                        return AnvilGUI.Response.text(Language.getComponentString(LangKey.INVALID_DATE_FORMAT));
                    }
                })
                .text(Language.getComponentString(LangKey.ENTER_DATE))
                .plugin(Main.getInstance())
                .open(player);
    }

    private static String formatDate(long timestamp) {

        // Convert the timestamp to a LocalDateTime object
        LocalDateTime datetime = LocalDateTime.ofEpochSecond(timestamp / 1000, 0, ZoneOffset.UTC);

        // Define the date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");

        // Format the date as a string
        return datetime.format(formatter);
    }


    private static String cutString(String string, int length) {
        if (string.length() > length) {
            return string.substring(0, length) + "...";
        }
        return string;
    }
}
