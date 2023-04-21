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
import it.mikeslab.Perms;
import it.mikeslab.util.PlayerUtils;
import it.mikeslab.util.atm.ATMUtil;
import it.mikeslab.util.language.LangKey;
import it.mikeslab.util.language.Language;
import org.bukkit.entity.Player;

/**
 * The type Cmd atm.
 */
@CommandAlias("atm")
@CommandPermission(Perms.ATM_CMD)
@Description("Manage ATM's")
public class CmdATM extends BaseCommand {


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
     * On atm.
     *
     * @param player the player
     */
    @Subcommand("get")
    @Description("Manage ATM's")
    @Syntax("<action>")
    @CommandPermission(Perms.ATM_ADMIN)
    public void onAtm(Player player) {
        if(PlayerUtils.isInventoryFull(player)) {
            player.sendMessage(Language.getComponentString(LangKey.INVENTORY_FULL));
            return;
        }

        player.getInventory().addItem(ATMUtil.getATMPlaceableItem());
        player.sendMessage(Language.getComponentString(LangKey.ATM_RECEIVED));
    }

}



