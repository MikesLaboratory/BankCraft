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
import it.mikeslab.util.creditcard.CardType;
import it.mikeslab.util.language.LangKey;
import it.mikeslab.util.language.Language;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * The type Inventory card type.
 */
public class InventoryCardType {


    /**
     * Open card type selector completable future.
     *
     * @param player the player
     * @return the completable future
     */
    public static CompletableFuture<CardType> openCardTypeSelector(Player player) {
        CompletableFuture<CardType> future = new CompletableFuture<>();

        String[] setup = {
                "         ",
                " aaaaaaa ",
                "   b c   "
        };

        InventoryGui inventoryGui = new InventoryGui(Main.getInstance(), Language.getComponentString(LangKey.SELECT_CARD_TYPE_TITLE), setup);
        List<CardType> cardTypes = Main.getInstance().getCardTypeUtil().getList();

        GuiElementGroup group = new GuiElementGroup('a');

        for(CardType cardType : cardTypes) {

            List<String> lore = new ArrayList<>();
            lore.add(Language.getComponentString(LangKey.CARD_TYPE, Map.of("%s", cardType.getName().toUpperCase())));
            lore.add(Language.getComponentString(LangKey.CARD_DAILY_WITHDRAW_LIMIT, Map.of("%s", "" + cardType.getPerDayWithdrawLimit())));
            lore.add(Language.getComponentString(LangKey.CARD_DEPOSIT_LIMIT, Map.of("%s", "" + cardType.getDepositLimit())));
            lore.add(Language.getComponentString(LangKey.CARD_WITHDRAW_LIMIT, Map.of("%s", "" + cardType.getWithdrawLimit())));
            lore.add(Language.getComponentString(LangKey.CARD_TRANSFER_LIMIT, Map.of("%s", "" + cardType.getTransferLimit())));

            group.addElement(new StaticGuiElement('a', ItemStackUtil.createStack(XMaterial.matchXMaterial(cardType.getMaterial()), Translator.legacyTranslate("<bold><yellow>" + cardType.getDisplayName()), lore), e -> {
                inventoryGui.close();
                future.complete(cardType);
                return true;
            }));

        }


        inventoryGui.addElements(group);

        inventoryGui.addElement(new GuiPageElement('b', new ItemStack(Material.REDSTONE), GuiPageElement.PageAction.PREVIOUS, Language.getComponentString(LangKey.PREVIOUS_PAGE)));
        inventoryGui.addElement(new GuiPageElement('c', new ItemStack(Material.ARROW), GuiPageElement.PageAction.NEXT, Language.getComponentString(LangKey.NEXT_PAGE)));

        inventoryGui.setCloseAction(e -> {return false;});

        inventoryGui.setFiller(ItemStackUtil.getFiller());

        inventoryGui.show(player);


        return future;
    }






}
