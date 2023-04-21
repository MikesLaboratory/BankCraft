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

package it.mikeslab.listener;

import it.mikeslab.Main;
import it.mikeslab.Perms;
import it.mikeslab.command.inventory.AnvilPinGUI;
import it.mikeslab.command.inventory.InventoryBanknotes;
import it.mikeslab.command.inventory.InventoryCurrencySelector;
import it.mikeslab.object.Banknote;
import it.mikeslab.util.LocationUtil;
import it.mikeslab.util.PlayerUtils;
import it.mikeslab.util.atm.ATMUtil;
import it.mikeslab.util.banknote.BanknoteUtil;
import it.mikeslab.util.creditcard.CreditCardUtil;
import it.mikeslab.util.currency.CurrencyUtil;
import it.mikeslab.util.language.LangKey;
import it.mikeslab.util.language.Language;
import it.mikeslab.util.transactions.EconomyManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Listens to events related to player interactions with ATMs.
 */
public class PlayerEventListener implements Listener {

    /**
     * On atm block place.
     *
     * @param event the event
     */
    @EventHandler
    public void onATMBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!hasPermissionToPlaceATM(player)) {
            cancelEventAndSendNoPermissionMessage(event, player);
            return;
        }

        if (isPlacingATMBlock(event)) {
            addATMAndSendPlaceMessage(player, event.getBlock());
        }
    }

    /**
     * On atm block break.
     *
     * @param event the event
     */
    @EventHandler
    public void onATMBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!hasPermissionToRemoveATM(player)) {
            cancelEventAndSendNoPermissionMessage(event, player);
            return;
        }

        if (isBreakingATMBlock(event)) {
            removeATMAndSendRemoveMessage(player, event.getBlock());
        }
    }

    /**
     * On atm use.
     *
     * @param event the event
     */
    @EventHandler
    public void onATMUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND) {
            if (!isInteractingWithATMBlock(event)) {
                return;
            }

            cancelEvent(event);

            if (!hasPermissionToUseATM(player)) {
                sendNoPermissionMessage(player);
                return;
            }

            handleATMInteraction(event, player);
        }
    }

    private boolean hasPermissionToPlaceATM(Player player) {
        return player.hasPermission(Perms.ATM_PLACE);
    }

    private boolean hasPermissionToRemoveATM(Player player) {
        return player.hasPermission(Perms.ATM_REMOVE);
    }

    private boolean hasPermissionToUseATM(Player player) {
        return player.hasPermission(Perms.ATM_USE);
    }

    private void cancelEventAndSendNoPermissionMessage(Cancellable event, Player player) {
        player.sendMessage(Language.getComponentString(LangKey.NO_PERMISSION));
        event.setCancelled(true);
    }

    private boolean isPlacingATMBlock(BlockPlaceEvent event) {
        return ATMUtil.isATM(event.getItemInHand());
    }

    private void addATMAndSendPlaceMessage(Player player, Block block) {
        ATMUtil.addATM(block);
        Location location = block.getLocation();
        player.sendMessage(Language.getComponentString(LangKey.ATM_PLACED, Map.of(
                "%location%", LocationUtil.getLocationString(location)
        )));
    }

    private boolean isBreakingATMBlock(BlockBreakEvent event) {
        return ATMUtil.isATM(event.getBlock());
    }

    private void removeATMAndSendRemoveMessage(Player player, Block block) {
        ATMUtil.removeATM(block);
        Location location = block.getLocation();
        player.sendMessage(Language.getComponentString(LangKey.ATM_REMOVED, Map.of(
                "%location%", LocationUtil.getLocationString(location)
        )));
    }

    private boolean isInteractingWithATMBlock(PlayerInteractEvent event) {
        if (!event.hasBlock()) {
            return false;
        }
        return event.getClickedBlock() != null && ATMUtil.isATM(event.getClickedBlock());
    }

    private void cancelEvent(PlayerInteractEvent event) {
        event.setCancelled(true);
    }

    private void sendNoPermissionMessage(Player player) {
        player.sendMessage(Language.getComponentString(LangKey.NO_PERMISSION));
    }

    private void handleATMInteraction(PlayerInteractEvent event, Player player) {
        CreditCardUtil creditCardUtil = Main.getInstance().getCreditCardUtil();
        EconomyManager economy = Main.getInstance().getEconomyManager();
        ItemStack mainHand = player.getInventory().getItemInMainHand();

        Banknote banknote = BanknoteUtil.fromItemStack(mainHand);
        if (banknote != null) {
            handleBanknoteDeposit(player, economy, banknote);
            return;
        }
        if (!creditCardUtil.isCreditCard(mainHand)) {
            sendNoCreditCardMessage(player);
            return;
        }

        handleCreditCardInteraction(event, player, mainHand);
    }

    private void handleBanknoteDeposit(Player player, EconomyManager economy, Banknote banknote) {
        if (!economy.hasBankAccount(player.getUniqueId())) {
            player.sendMessage(Language.getComponentString(LangKey.NO_BANK_ACCOUNT));
            return;
        }

        if (BanknoteUtil.deposit(player, banknote)) {
            PlayerUtils.playSound(player, Main.getInstance().getConfig().getString("sounds.banknote-deposit"));

            player.sendActionBar(Language.getComponentString(LangKey.DEPOSITED, Map.of(
                    "%value%", Double.toString(banknote.getValue()),
                    "%currency%", banknote.getCurrency()
            )));
        } else {
            player.sendMessage(Language.getComponentString(LangKey.DEPOSIT_FAILED));
        }
    }

    private void sendNoCreditCardMessage(Player player) {
        player.sendMessage(Language.getComponentString(LangKey.NO_CREDIT_CARD));
    }

    private void handleCreditCardInteraction(PlayerInteractEvent event, Player player, ItemStack mainHand) {
        CreditCardUtil creditCardUtil = Main.getInstance().getCreditCardUtil();
        UUID ownerUUID = creditCardUtil.getOwnerUUID(mainHand);
        int correctPin = creditCardUtil.getPin(ownerUUID);
        String creditCardNumber = creditCardUtil.getCardNumberFromUUID(ownerUUID);

        CompletableFuture<Boolean> auth = new AnvilPinGUI().openPinGui(player, correctPin);

        auth.thenAccept(result -> {
            if (result) {
                openBanknoteInventory(event, player, ownerUUID, creditCardNumber);
            } else {
                player.sendMessage(Language.getComponentString(LangKey.WRONG_PIN));
            }
        });
    }

    private void openBanknoteInventory(PlayerInteractEvent event, Player player, UUID ownerUUID, String creditCardNumber) {
        if (CurrencyUtil.isCurrencyEnabled()) {
            InventoryCurrencySelector.openCurrencySelector(event.getPlayer()).thenAccept(currency -> {
                InventoryBanknotes.openATM(player, ownerUUID, currency, creditCardNumber);
            });
        } else {
            InventoryBanknotes.openATM(event.getPlayer(), ownerUUID, CurrencyUtil.getMainCurrency(), creditCardNumber);
        }
    }
}

