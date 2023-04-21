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

package it.mikeslab.task;

import it.mikeslab.Main;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;

/**
 * The type Limit task.
 */
public class LimitTask extends BukkitRunnable {

    private final Clock clock;
    private LocalDate lastResetDate;
    private final Main plugin;

    /**
     * Instantiates a new Limit task.
     *
     * @param plugin the plugin
     */
    public LimitTask(Main plugin) {
        this.clock = Clock.system(ZoneId.of(plugin.getConfig().getString("timezone", "America/New_York")));
        this.lastResetDate = LocalDate.now(clock);
        this.plugin = plugin;
    }

    @Override
    public void run() {
        LocalDate currentDate = LocalDate.now(clock);
        if (!currentDate.equals(lastResetDate)) {
            Map<UUID, Double> dailyWithdraws = plugin.getEconomyManager().getDailyWithdraws();
            dailyWithdraws.clear();
            lastResetDate = currentDate;
        }
    }
}

