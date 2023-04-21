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

import de.themoep.inventorygui.*;
import it.mikeslab.Main;
import it.mikeslab.object.Banknote;
import it.mikeslab.util.ItemStackUtil;
import it.mikeslab.util.PlayerUtils;
import it.mikeslab.util.banknote.BanknoteUtil;
import it.mikeslab.util.currency.CurrencyUtil;
import it.mikeslab.util.language.LangKey;
import it.mikeslab.util.language.Language;
import it.mikeslab.util.transactions.EconomyManager;
import it.mikeslab.util.transactions.Transaction;
import it.mikeslab.util.transactions.TransactionUtil;
import it.mikeslab.util.transactions.TransferResult;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * The type Inventory banknotes.
 */
public class InventoryBanknotes {


    /**
     * Open atm.
     *
     * @param player           the player
     * @param cardHolder       the card holder
     * @param currency         the currency
     * @param creditCardNumber the credit card number
     */
    public static void openATM(Player player, UUID cardHolder, String currency, String creditCardNumber) {

        String[] setup = {
                "         ",
                "daaaaaaa ",
                "f  bec   "
        };

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(cardHolder);

        InventoryGui inventoryGui = new InventoryGui(Main.getInstance(), player, Language.getComponentString(LangKey.ATM_TITLE), setup);
        List<Banknote> banknotes = BanknoteUtil.getOrderedMap(currency);
        EconomyManager economyManager = Main.getInstance().getEconomyManager();

        GuiElementGroup group = new GuiElementGroup('a');

        for (Banknote banknote : banknotes) {

            group.addElement(new StaticGuiElement('a', banknote.getItemStack(), click -> {
                TransferResult result = economyManager.canWithdraw(cardHolder, banknote.getValue(), currency, false);

                if (result != TransferResult.SUCCESS) {

                    player.sendMessage(Language.getComponentString(LangKey.INSUFFICIENT_FUNDS));

                } else {
                    economyManager.withdraw(cardHolder, currency, banknote.getValue());
                    PlayerUtils.giveItem(player, banknote.getItemStack());
                    PlayerUtils.playSound(player, Main.getInstance().getConfig().getString("sounds.banknote-withdrawal"));

                    TransactionUtil transactionUtil = Main.getInstance().getTransactionUtil();

                    if(transactionUtil.isBankNoteTransactionEnabled()) {
                        transactionUtil.addTransaction(new Transaction(Transaction.randomId(), cardHolder, cardHolder, currency, Language.getComponentString(LangKey.ATM_WITHDRAWAL_REASON), banknote.getValue(), System.currentTimeMillis()));
                    }

                    if (click.getType() != ClickType.SHIFT_LEFT) {
                        player.closeInventory();
                    }


                    inventoryGui.draw();

                }

                return true;
            }

            ));
        }

        inventoryGui.addElements(group);
        inventoryGui.addElement(new GuiPageElement('b', new ItemStack(Material.REDSTONE), GuiPageElement.PageAction.PREVIOUS, Language.getComponentString(LangKey.PREVIOUS_PAGE)));
        inventoryGui.addElement(new GuiPageElement('c', new ItemStack(Material.ARROW), GuiPageElement.PageAction.NEXT, Language.getComponentString(LangKey.NEXT_PAGE)));

        inventoryGui.addElement(new StaticGuiElement('f', ItemStackUtil.createStack(Material.REDSTONE, Language.getComponentString(LangKey.RETURN_CURRENCIES_SELECTOR)), click -> {

            InventoryCurrencySelector.openCurrencySelector(player).thenAccept(currencySelected -> {
                InventoryBanknotes.openATM(player, cardHolder, currencySelected, creditCardNumber);
            });


            return true;
        }));

        inventoryGui.addElement(new DynamicGuiElement('e', (viewer) -> {
            double balance = economyManager.getBalance(offlinePlayer, currency);
            String symbol = CurrencyUtil.getSymbol(currency);


            return new StaticGuiElement('e', ItemStackUtil.createStack(Material.SPECTRAL_ARROW, String.format(Language.getComponentString(LangKey.BALANCE_ITEM_NAME), balance + symbol)), click -> {
                inventoryGui.draw();
                return true;
            });
        }));

        if(CurrencyUtil.isCurrencyEnabled()) {
            inventoryGui.addElement(new StaticGuiElement('d', ItemStackUtil.createStack(Material.BOOK, Language.getComponentString(LangKey.EXCHANGE_ITEM_NAME)), click -> {
                InventoryExchange.openExchangeInventory(player, offlinePlayer, creditCardNumber, null, null, 0);
                return true;
            }));
        }

        inventoryGui.setFiller(ItemStackUtil.getFiller());

        inventoryGui.show(player);

        PlayerUtils.playSound(player, Main.getInstance().getConfig().getString("sounds.atm-open"));
    }
}

