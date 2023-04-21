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

import it.mikeslab.Main;
import it.mikeslab.util.language.LangKey;
import it.mikeslab.util.language.Language;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The type Anvil pin gui.
 */
public class AnvilPinGUI {
    private final Map<UUID, CompletableFuture<Boolean>> completableFutures = new HashMap<>();

    /**
     * Open pin gui completable future.
     *
     * @param subject    the subject
     * @param correctPin the correct pin
     * @return the completable future
     */
    public CompletableFuture<Boolean> openPinGui(Player subject, int correctPin) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        UUID uuid = subject.getUniqueId();

        completableFutures.put(uuid, completableFuture);

        new AnvilGUI.Builder()
                .onClose(player -> {
                    CompletableFuture<Boolean> future = completableFutures.remove(player.getUniqueId());

                    if (future != null) {
                        future.complete(false);
                    }
                })
                .onComplete((completion) -> {
                    if (completion.getText().equals(String.valueOf(correctPin))) {
                        CompletableFuture<Boolean> future = completableFutures.remove(completion.getPlayer().getUniqueId());

                        if (future != null) {
                            future.complete(true);
                        }
                    }
                    return List.of(AnvilGUI.ResponseAction.close());
                })
                .itemLeft(new ItemStack(Material.PAPER))
                .text(Language.getComponentString(LangKey.ANVIL_PIN_TEXT))
                .title(Language.getComponentString(LangKey.ANVIL_INSERT_PIN))
                .plugin(Main.getInstance())
                .open(subject);

        return completableFuture;
    }






}
