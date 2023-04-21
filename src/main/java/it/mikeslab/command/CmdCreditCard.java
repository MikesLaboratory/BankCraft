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
import it.mikeslab.Main;
import it.mikeslab.Perms;
import it.mikeslab.command.inventory.InventoryCardType;
import it.mikeslab.command.inventory.InventoryTransactions;
import it.mikeslab.util.PlayerUtils;
import it.mikeslab.util.Translator;
import it.mikeslab.util.creditcard.CreditCardUtil;
import it.mikeslab.util.language.LangKey;
import it.mikeslab.util.language.Language;
import it.mikeslab.util.transactions.EconomyManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The type Cmd credit card.
 */
@CommandAlias("creditcard|cc")
@CommandPermission(Perms.CREDITCARD_CMD)
public class CmdCreditCard extends BaseCommand {


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
     * On create.
     *
     * @param sender  the sender
     * @param subject the subject
     */
    @Subcommand("create|register")
    @CommandPermission(Perms.CREDITCARD_CREATE)
    @Description("Create your credit card")
    @Syntax("<subject>")
    @CommandCompletion("@players")
    public void onCreate(CommandSender sender, OnlinePlayer subject) {
        CreditCardUtil creditCardUtil = Main.getInstance().getCreditCardUtil();

        if(!(sender instanceof Player)) {
            sender.sendMessage(Language.getComponentString(LangKey.PLAYER_ONLY));
            return;
        }


        if(Main.getInstance().getPlayerDataGson().getJson(subject.getPlayer().getUniqueId()).isPresent()) {
            sender.sendMessage(Language.getComponentString(LangKey.ALREADY_REGISTERED));
            return;
        }

        InventoryCardType.openCardTypeSelector(subject.getPlayer()).thenAccept(cardType -> {
            PlayerUtils.giveItem(subject.getPlayer(), creditCardUtil.createCreditCard(subject.getPlayer().getUniqueId(), cardType));
        });

        sender.sendMessage(Language.getComponentString(LangKey.CREDIT_CARD_BEING_CREATED, Map.of(
                "%player%", subject.getPlayer().getName()
        )));
    }


    /**
     * On delete.
     *
     * @param sender  the sender
     * @param subject the subject
     */
    @Subcommand("delete|remove")
    @CommandPermission(Perms.CREDITCARD_DELETE)
    @Description("Delete your credit card")
    @Syntax("<subject>")
    @CommandCompletion("@players")
    public void onDelete(CommandSender sender, OfflinePlayer subject) {
        CreditCardUtil creditCardUtil = Main.getInstance().getCreditCardUtil();

        if (Main.getInstance().getPlayerDataGson().getJson(subject.getUniqueId()).isEmpty()) {
            sender.sendMessage(Language.getComponentString(LangKey.NOT_REGISTERED, Map.of(
                    "%player%", subject.getName()
            )));
            return;
        }

        creditCardUtil.deleteCreditCard(subject.getUniqueId());
        sender.sendMessage(Language.getComponentString(LangKey.CREDIT_CARD_DELETED, Map.of(
                "%player%", subject.getName()
        )));

    }


    /**
     * On balance.
     *
     * @param sender  the sender
     * @param subject the subject
     */
    @Subcommand("balance|bal")
    @CommandPermission(Perms.CREDITCARD_BALANCE)
    @Description("Check your balance")
    @Syntax("<subject>")
    @CommandCompletion("@players")
    public void onBalance(CommandSender sender, @Optional OfflinePlayer subject) {
        CreditCardUtil creditCardUtil = Main.getInstance().getCreditCardUtil();

        if (subject == null && sender instanceof Player) {
            subject = (OfflinePlayer) sender;
        }


        if (subject == null) {
            sender.sendMessage(Language.getComponentString(LangKey.COMMAND_SPECIFY_SUBJECT));
            return;
        }

        EconomyManager economyManager = Main.getInstance().getEconomyManager();

        if(!economyManager.hasBankAccount(subject.getUniqueId())) {
            sender.sendMessage(Language.getComponentString(LangKey.NOT_REGISTERED, Map.of(
                    "%player%", "" + subject.getName()
            )));

            return;
        }

        UUID subjectUUID = subject.getUniqueId();
        String creditCardNumber = creditCardUtil.getCardNumberFromUUID(subjectUUID);

        List<String> messages = economyManager.getBalanceMessages(creditCardNumber, subject);

        messages.add(Language.getString(LangKey.OPEN_TRANSACTION_MENU));

        messages = Translator.legacyListTranslate(messages);

        for (String message : messages) {
            sender.sendMessage(message);
        }
    }


    /**
     * On transactions.
     *
     * @param sender  the sender
     * @param subject the subject
     */
    @Subcommand("transactions|trans")
    @CommandPermission(Perms.CREDITCARD_TRANSACTIONS)
    @Description("Open transactions menu.")
    @Syntax("<subject>")
    @CommandCompletion("@players")
    public void onTransactions(CommandSender sender, @Optional OfflinePlayer subject) {

        if(!(sender instanceof Player player)) {
            sender.sendMessage(Language.getComponentString(LangKey.PLAYER_ONLY));
            return;
        }

        if(subject != null && !player.hasPermission(Perms.CREDITCARD_TRANSACTIONS_OTHERS)) {
            subject = player.getPlayer();
        }

        if(subject == null) {
            subject = player.getPlayer();
        }

        InventoryTransactions.getTransactionsInventory(player, subject.getUniqueId(), null).show(player);
    }


}
