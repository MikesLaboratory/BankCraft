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

package it.mikeslab.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import it.mikeslab.Perms;
import it.mikeslab.command.inventory.InventoryConfirm;
import it.mikeslab.util.currency.CurrencyUtil;
import it.mikeslab.util.language.LangKey;
import it.mikeslab.util.language.Language;
import it.mikeslab.util.transactions.WireTransferUtil;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * The type Cmd wire transfer.
 */
@CommandPermission(Perms.WIRE_TRANSFER_CMD)
@CommandAlias("wiretransfer|wt")
public class CmdWireTransfer extends BaseCommand {


    /**
     * On help.
     *
     * @param help the help
     */
    @HelpCommand
    public void onHelp(CommandHelp help) {
        help.showHelp();
    }


    /**
     * On send.
     *
     * @param player      the player
     * @param receiver    the receiver
     * @param amount      the amount
     * @param currency    the currency
     * @param description the description
     */
    @Subcommand("send|s")
    @CommandPermission(Perms.WIRE_TRANSFER_SEND)
    @Description("Send money to another player")
    @Syntax("<receiver> <amount> <currency> <description>")
    public void onSend(Player player, OnlinePlayer receiver, double amount, @Optional String currency, @Optional String... description) {

        if (currency == null) {
            currency = CurrencyUtil.getMainCurrency();
        }

        if (description.length == 0) {
            description = new String[]{Language.getComponentString(LangKey.WIRE_TRANSFER_REASON)};
        }

        if (description.length > 10) {
            player.sendMessage(Language.getComponentString(LangKey.REASON_TOO_LONG, Map.of(
                    "%length%", Integer.toString(description.length - 10)
            )));
            return;
        }

        String[] finalDescription = description;
        String finalCurrency = currency;
        InventoryConfirm.openConfirmInventory(player, Language.getComponentString(LangKey.WIRE_TRANSFER_TO, Map.of(
                "%player%", receiver.getPlayer().getName()
        ))).thenAccept((result) -> {
            if (result) {
                WireTransferUtil.sendWireTransfer(player, receiver.getPlayer(), amount, finalDescription, finalCurrency);
            } else {
                player.sendMessage(Language.getComponentString(LangKey.OPERATION_CANCELLED));
            }

            player.closeInventory();
        });

    }
}
