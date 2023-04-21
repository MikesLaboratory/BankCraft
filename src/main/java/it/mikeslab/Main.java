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

package it.mikeslab;


import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.MessageType;
import it.mikeslab.command.CmdATM;
import it.mikeslab.command.CmdBank;
import it.mikeslab.command.CmdCreditCard;
import it.mikeslab.command.CmdWireTransfer;
import it.mikeslab.listener.PlayerEventListener;
import it.mikeslab.task.LimitTask;
import it.mikeslab.util.bstat.Metrics;
import it.mikeslab.util.json.GSONUtil;
import it.mikeslab.util.ItemStackUtil;
import it.mikeslab.util.atm.ATMUtil;
import it.mikeslab.util.banknote.BanknoteUtil;
import it.mikeslab.util.creditcard.CardTypeUtil;
import it.mikeslab.util.creditcard.CreditCardUtil;
import it.mikeslab.util.currency.CurrencyUtil;
import it.mikeslab.util.database.MongoDBHandler;
import it.mikeslab.util.language.Language;
import it.mikeslab.util.transactions.EconomyManager;
import it.mikeslab.util.transactions.TransactionUtil;
import it.mikeslab.vault.EconomyCore;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * The type Main.
 */
@Getter
public class Main extends JavaPlugin {
    @Getter private static Main instance;
    private BukkitTask limitTask;
    private GSONUtil transactionGson;
    private GSONUtil playerDataGson;
    private EconomyManager economyManager;
    private CardTypeUtil cardTypeUtil;
    private TransactionUtil transactionUtil;
    private CreditCardUtil creditCardUtil;
    private BukkitCommandManager commandManager;
    private MongoDBHandler transactionHandler;
    private EconomyCore economyCore;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        limitTask = new LimitTask(this).runTaskTimer(this, 0L, 20L * 60L); // Run every minute (20 ticks * 60 seconds)

        transactionGson = new GSONUtil(getDataFolder(), "transactions.json");
        playerDataGson = new GSONUtil(getDataFolder(), "playerdata.json");

        CurrencyUtil.initializeExchangeRates(getConfig());


        Language.initialize(this, getConfig().getString("language"));

        Bukkit.getPluginManager().registerEvents(new PlayerEventListener(), this);

        setupCommandFramework();

        instance = this;


        cardTypeUtil = new CardTypeUtil(getConfig());

        String connectionString = getConfig().getString("mongo.connection-string");
        boolean useMongo = useMongo() && connectionString != null;

        if (useMongo) {
            String transactionsDatabase = getConfig().getString("mongo.database.transactions");
            transactionHandler = new MongoDBHandler(getConfig().getString("mongo.connection-string"), transactionsDatabase);
        }

        transactionUtil = new TransactionUtil(useMongo, transactionHandler, "transactions");
        transactionUtil.init();

        creditCardUtil = new CreditCardUtil();

        ItemStackUtil.loadNamespacedKeys();
        ATMUtil.loadATMs(getConfig());
        BanknoteUtil.loadBanknotes(getConfig());

        economyCore = new EconomyCore(playerDataGson);

        if(!CurrencyUtil.isCurrencyEnabled()) {
            if (!setupEconomy(economyCore)) {
                getLogger().severe("Vault not found and Custom Currencies System not enabled! Disabling plugin...");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }

        economyManager = new EconomyManager(economyCore);


        new Metrics(this, 18253);
    }


    @Override
    public void onDisable() {
        saveDefaultConfig();

        if (limitTask != null) {
            limitTask.cancel();
        }

        ATMUtil.saveATMs(getConfig());

        transactionHandler.close();
    }


    private boolean setupEconomy(EconomyCore economyCore) {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        getServer().getServicesManager().register(Economy.class, economyCore, this, ServicePriority.Highest);
        Bukkit.getLogger().info("Registered Custom Vault Economy");
        return true;
    }



    private void setupCommandFramework() {
        commandManager = new BukkitCommandManager(this);
        commandManager.registerCommand(new CmdATM());
        commandManager.registerCommand(new CmdWireTransfer());
        commandManager.registerCommand(new CmdCreditCard());
        commandManager.registerCommand(new CmdBank());

        commandManager.getCommandCompletions().registerAsyncCompletion("currencies",
                context -> CurrencyUtil.getCurrencies());

        commandManager.enableUnstableAPI("help");
        commandManager.setFormat(MessageType.HELP, ChatColor.GREEN, ChatColor.GOLD);
    }


    /**
     * Use mongo boolean.
     *
     * @return the boolean
     */
    public boolean useMongo() {
        return getConfig().getBoolean("mongo.use-mongo");
    }


}
