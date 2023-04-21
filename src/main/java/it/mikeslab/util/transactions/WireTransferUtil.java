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

package it.mikeslab.util.transactions;

import it.mikeslab.Main;
import it.mikeslab.util.creditcard.CardType;
import it.mikeslab.util.creditcard.CardTypeUtil;
import it.mikeslab.util.language.LangKey;
import it.mikeslab.util.language.Language;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A utility class for managing wire transfers between player accounts.
 */
public class WireTransferUtil {

    /**
     * Send wire transfer.
     *
     * @param player      the player
     * @param receiver    the receiver
     * @param amount      the amount
     * @param description the description
     * @param currency    the currency
     */
    public static void sendWireTransfer(Player player, Player receiver, double amount, @Nullable String[] description, String currency) {
        EconomyManager economyManager = Main.getInstance().getEconomyManager();

        if (!economyManager.hasBankAccount(player.getUniqueId())) {
            player.sendMessage(Language.getComponentString(LangKey.NO_BANK_ACCOUNT));
            return;
        }

        if (!economyManager.hasBankAccount(receiver.getUniqueId())) {
            player.sendMessage(Language.getComponentString(LangKey.NOT_REGISTERED, Map.of(
                    "%receiver%", receiver.getName()
            )));
            return;
        }

        switch (economyManager.canWithdraw(player.getUniqueId(), amount, currency, true)) {
            case INSUFFICIENT_FUNDS:
                player.sendMessage(Language.getComponentString(LangKey.INSUFFICIENT_FUNDS));
                break;

            case TRANSFER_LIMIT_REACHED:
                player.sendMessage(Language.getComponentString(LangKey.TRANSFER_LIMIT_REACHED));
                break;

            case ACCOUNT_NOT_FOUND:
                player.sendMessage(Language.getComponentString(LangKey.ERROR_OCCURRED));
                break;

            case SUCCESS:
                CardType subjectCardType = CardTypeUtil.getCardTypeForPlayer(receiver.getUniqueId());
                double subjectBalance = economyManager.getBalance(receiver, currency);

                if (subjectBalance + amount > subjectCardType.getDepositLimit()) {
                    player.sendMessage(Language.getComponentString(LangKey.SUBJECT_CANNOT_RECEIVE, Map.of(
                            "%receiver%", receiver.getName()
                    )));
                    return;
                }

                if (economyManager.transfer(player.getUniqueId(), receiver.getUniqueId(), currency, amount, String.join(" ", description))) {
                    player.sendMessage(Language.getComponentString(LangKey.TRANSFER_SUCCESS, Map.of(
                            "%amount%", Double.toString(amount),
                            "%currency%", currency,
                            "%receiver%", receiver.getName(),
                            "%description%", String.join(" ", description)
                    )));
                } else {
                    player.sendMessage(Language.getComponentString(LangKey.ERROR_OCCURRED));
                }
                break;
        }
    }
}
