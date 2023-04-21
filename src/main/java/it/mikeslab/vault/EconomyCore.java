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

package it.mikeslab.vault;

import com.google.gson.JsonObject;
import it.mikeslab.util.json.GSONUtil;
import it.mikeslab.util.currency.CurrencyUtil;
import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class EconomyCore implements Economy {
    private static final String BALANCES_KEY = "balances";
    private static final String CURRENCY_KEY = CurrencyUtil.getMainCurrency();

    private final GSONUtil playerDataGson;

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "BankEconomy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double amount) {
        return CurrencyUtil.format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return null;
    }

    @Override
    public String currencyNameSingular() {
        return CurrencyUtil.getMainCurrency();
    }

    @Override
    public boolean hasAccount(String playerName) {
        return hasAccount(getOfflinePlayer(playerName));
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        Optional<JsonObject> playerObject = playerDataGson.getJson(player.getUniqueId());

        return playerObject.isPresent();
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    @Override
    public double getBalance(String playerName) {
        return getBalance(getOfflinePlayer(playerName));
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        Optional<JsonObject> playerObject = playerDataGson.getJson(player.getUniqueId());

        if (playerObject.isPresent()) {
            JsonObject balancesObject = playerObject.get().getAsJsonObject(BALANCES_KEY);

            if (balancesObject != null && balancesObject.has(CURRENCY_KEY)) {
                return balancesObject.get(CURRENCY_KEY).getAsDouble();
            }
        }

        return 0.0;
    }

    @Override
    public double getBalance(String playerName, String worldName) {
        return getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player, String worldName) {
        return getBalance(player);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return has(getOfflinePlayer(playerName), amount);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    @Override

    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return withdrawPlayer(getOfflinePlayer(playerName), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw a negative amount");
        }

        Optional<JsonObject> playerObject = playerDataGson.getJson(player.getUniqueId());

        if (playerObject.isPresent()) {
            JsonObject balancesObject = playerObject.get().getAsJsonObject(BALANCES_KEY);

            if (balancesObject != null && balancesObject.has(CURRENCY_KEY)) {
                double currentBalance = balancesObject.get(CURRENCY_KEY).getAsDouble();

                if (currentBalance >= amount) {
                    double newBalance = currentBalance - amount;
                    balancesObject.addProperty(CURRENCY_KEY, newBalance);
                    playerDataGson.saveConfig();
                    return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
                } else {
                    return new EconomyResponse(amount, currentBalance, EconomyResponse.ResponseType.FAILURE, "Not enough funds");
                }
            }
        }

        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player account not found");
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return depositPlayer(getOfflinePlayer(playerName), amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot deposit a negative amount");
        }

        Optional<JsonObject> playerObject = playerDataGson.getJson(player.getUniqueId());
        JsonObject balancesObject = playerObject
                .map(obj -> obj.getAsJsonObject(BALANCES_KEY))
                .orElseGet(JsonObject::new);

        double currentBalance = balancesObject.has(CURRENCY_KEY) ? balancesObject.get(CURRENCY_KEY).getAsDouble() : 0.0;
        double newBalance = currentBalance + amount;
        balancesObject.addProperty(CURRENCY_KEY, newBalance);

        if (!playerObject.isPresent()) {
            JsonObject newPlayerObject = new JsonObject();
            newPlayerObject.addProperty("creditCardNumber", "");
            newPlayerObject.addProperty("cardType", "");
            newPlayerObject.addProperty("pin", "");
            newPlayerObject.addProperty("cardID", "");
            playerDataGson.setJson(String.valueOf(player.getUniqueId()), newPlayerObject);
        }

        playerObject.ifPresent(obj -> obj.add(BALANCES_KEY, balancesObject));
        playerDataGson.saveConfig();

        return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String owner) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer owner) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }


    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<>();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return createPlayerAccount(getOfflinePlayer(playerName));
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return createPlayerAccount(player);
    }

    private OfflinePlayer getOfflinePlayer(String playerName) {
        return Bukkit.getOfflinePlayer(playerName);
    }

}

