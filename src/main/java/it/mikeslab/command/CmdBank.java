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

package it.mikeslab.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import it.mikeslab.Main;
import it.mikeslab.Perms;
import it.mikeslab.util.currency.CurrencyUtil;
import it.mikeslab.util.language.LangKey;
import it.mikeslab.util.language.Language;
import it.mikeslab.util.transactions.EconomyManager;
import org.bukkit.command.CommandSender;

import java.util.Map;

/**
 * The type Cmd bank.
 */
@CommandAlias("bank")
@CommandPermission(Perms.BANK_CMD)
public class CmdBank extends BaseCommand {

    /**
     * Give money.
     *
     * @param sender   the sender
     * @param subject  the subject
     * @param amount   the amount
     * @param currency the currency
     */
    @Subcommand("give|g")
    @CommandPermission(Perms.BANK_GIVE)
    @Description("Give money to a player")
    @CommandCompletion("@players @nothing @currencies")
    @Syntax("<subject> <amount> <currency>")
    public void giveMoney(CommandSender sender, OnlinePlayer subject, double amount, @Optional String currency) {
        EconomyManager economyManager = Main.getInstance().getEconomyManager();

        if(currency == null || !CurrencyUtil.isCurrencyEnabled()) {
            currency = CurrencyUtil.getMainCurrency();
        }

        if(!CurrencyUtil.isCurrency(currency)) {
            sender.sendMessage(Language.getComponentString(LangKey.CURRENCY_NOT_FOUND, Map.of(
                    "%currency%", currency
            )));

            return;
        }

        if(!economyManager.hasBankAccount(subject.getPlayer().getUniqueId())) {
            sender.sendMessage(Language.getComponentString(LangKey.NOT_REGISTERED, Map.of(
                    "%player%", subject.getPlayer().getName()
            )));

            return;
        }


        if(amount <= 0) {
            sender.sendMessage(Language.getComponentString(LangKey.AMOUNT_MUST_BE_POSITIVE));
            return;
        }

        if(amount > 1000000000) {
            sender.sendMessage(Language.getComponentString(LangKey.AMOUNT_TOO_HIGH));
            return;
        }


        economyManager.deposit(subject.getPlayer().getUniqueId(), currency, amount);
        sender.sendMessage(Language.getComponentString(LangKey.BANK_GIVEN, Map.of(
                "%amount%", Double.toString(amount),
                "%currency%", currency,
                "%player%", subject.getPlayer().getName()
        )));
    }


    /**
     * Take money.
     *
     * @param sender   the sender
     * @param subject  the subject
     * @param amount   the amount
     * @param currency the currency
     */
    @Subcommand("take|t")
    @CommandPermission(Perms.BANK_TAKE)
    @Description("Take money from a player")
    @CommandCompletion("@players @nothing @currencies")
    @Syntax("<subject> <amount> <currency>")
    public void takeMoney(CommandSender sender, OnlinePlayer subject, double amount, @Optional String currency) {

        if(currency == null || !CurrencyUtil.isCurrencyEnabled()) {
            currency = CurrencyUtil.getMainCurrency();
        }

        if(!CurrencyUtil.isCurrency(currency)) {
            sender.sendMessage(Language.getComponentString(LangKey.CURRENCY_NOT_FOUND, Map.of(
                    "%currency%", currency
            )));
            return;
        }

        if(amount <= 0) {
            sender.sendMessage(Language.getComponentString(LangKey.AMOUNT_MUST_BE_POSITIVE));
            return;
        }

        if(amount > 1000000000) {
            sender.sendMessage(Language.getComponentString(LangKey.AMOUNT_TOO_HIGH));
            return;
        }

        EconomyManager economyManager = Main.getInstance().getEconomyManager();
        economyManager.withdraw(subject.getPlayer().getUniqueId(), currency, amount);
        sender.sendMessage(Language.getComponentString(LangKey.BANK_TAKEN, Map.of(
                "%amount%", Double.toString(amount),
                "%currency%", currency,
                "%player%", subject.getPlayer().getName()
        )));
    }


    /**
     * Sets money.
     *
     * @param sender   the sender
     * @param subject  the subject
     * @param amount   the amount
     * @param currency the currency
     */
    @Subcommand("set|s")
    @CommandPermission(Perms.BANK_SET)
    @Description("Set money of a player")
    @CommandCompletion("@players @nothing @currencies")
    @Syntax("<subject> <amount> <currency>")
    public void setMoney(CommandSender sender, OnlinePlayer subject, double amount, @Optional String currency) {

        if(currency == null || !CurrencyUtil.isCurrencyEnabled()) {
            currency = CurrencyUtil.getMainCurrency();
        }

        if(!CurrencyUtil.isCurrency(currency)) {
            sender.sendMessage(Language.getComponentString(LangKey.CURRENCY_NOT_FOUND, Map.of(
                    "%currency%", currency
            )));
            return;
        }

        if(amount < 0) {
            sender.sendMessage(Language.getComponentString(LangKey.AMOUNT_MUST_BE_POSITIVE));
            return;
        }

        if(amount > 1000000000) {
            sender.sendMessage(Language.getComponentString(LangKey.AMOUNT_TOO_HIGH));
            return;
        }

        EconomyManager economyManager = Main.getInstance().getEconomyManager();
        economyManager.setBalance(subject.getPlayer().getUniqueId(), currency, amount);
        sender.sendMessage(Language.getComponentString(LangKey.BANK_SET, Map.of(
                "%player%", subject.getPlayer().getName(),
                "%amount%", Double.toString(amount),
                "%currency%", currency
        )));
    }

    /**
     * Reset money.
     *
     * @param sender   the sender
     * @param subject  the subject
     * @param currency the currency
     */
    @Subcommand("reset|r")
    @CommandPermission(Perms.BANK_RESET)
    @Description("Reset money of a player to the default value")
    @CommandCompletion("@players @currencies")
    @Syntax("<subject> <currency>")
    public void resetMoney(CommandSender sender, OnlinePlayer subject, @Optional String currency) {

        if(currency == null || !CurrencyUtil.isCurrencyEnabled()) {
            currency = CurrencyUtil.getMainCurrency();
        }

        if(!CurrencyUtil.isCurrency(currency)) {
            sender.sendMessage(Language.getComponentString(LangKey.CURRENCY_NOT_FOUND, Map.of(
                    "%currency%", currency
            )));
            return;
        }

        double defaultAmount = 0;

        EconomyManager economyManager = Main.getInstance().getEconomyManager();
        economyManager.setBalance(subject.getPlayer().getUniqueId(), currency, defaultAmount);
        sender.sendMessage(Language.getComponentString(LangKey.BANK_RESET, Map.of(
                "%player%", subject.getPlayer().getName(),
                "%currency%", currency
        )));

    }
}
