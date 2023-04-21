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

import com.google.gson.JsonObject;
import it.mikeslab.Main;
import it.mikeslab.util.json.GSONUtil;
import it.mikeslab.util.creditcard.CardType;
import it.mikeslab.util.creditcard.CardTypeUtil;
import it.mikeslab.util.currency.CurrencyUtil;
import it.mikeslab.util.math.MathUtil;
import it.mikeslab.vault.EconomyCore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * The type Economy manager.
 */
@AllArgsConstructor
public class EconomyManager {

    private GSONUtil gsonUtil;
    private Economy econ;
    @Getter
    private Map<UUID, Double> dailyWithdraws;

    /**
     * Instantiates a new Economy manager.
     */
    public EconomyManager(EconomyCore economyCore) {
        this.dailyWithdraws = new HashMap<>();
        this.gsonUtil = Main.getInstance().getPlayerDataGson();
        this.econ = economyCore;
    }


    /**
     * Withdraw boolean.
     *
     * @param uuid     the uuid
     * @param currency the currency
     * @param amount   the amount
     * @return the boolean
     */
    public boolean withdraw(UUID uuid, String currency, double amount) {
        currency = currency.toUpperCase();

        if (CurrencyUtil.isCurrencyEnabled()) {
            return withdrawWithCurrencySystem(uuid, amount, currency);
        } else {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            double amountInMainCurrency = CurrencyUtil.convertToMainCurrency(amount, currency);
            if (econ != null && econ.has(player, amountInMainCurrency)) {
                econ.withdrawPlayer(player, amountInMainCurrency);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Deposit.
     *
     * @param uuid     the uuid
     * @param currency the currency
     * @param amount   the amount
     */
    public void deposit(UUID uuid, String currency, double amount) {
        currency = currency.toUpperCase();

        if (CurrencyUtil.isCurrencyEnabled()) {
            depositWithCurrencySystem(uuid, amount, currency);
        } else {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            if (econ != null) {
                double amountInMainCurrency = CurrencyUtil.convertToMainCurrency(amount, currency);
                econ.depositPlayer(player, amountInMainCurrency);
            }
        }
    }

    /**
     * Transfer boolean.
     *
     * @param fromUuid    the from uuid
     * @param toUuid      the to uuid
     * @param currency    the currency
     * @param amount      the amount
     * @param description the description
     * @return the boolean
     */
    public boolean transfer(UUID fromUuid, UUID toUuid, String currency, double amount, String description) {
        currency = currency.toUpperCase();

        double amountInMainCurrency = CurrencyUtil.convertToMainCurrency(amount, currency);

        boolean withdrawSuccess = withdraw(fromUuid, CurrencyUtil.getMainCurrency(), amountInMainCurrency);
        if (withdrawSuccess) {
            deposit(toUuid, CurrencyUtil.getMainCurrency(), amountInMainCurrency);

            Transaction transaction = new Transaction(Transaction.randomId(), fromUuid, toUuid, currency, description, amount, System.currentTimeMillis());
            TransactionUtil transactionUtil = Main.getInstance().getTransactionUtil();

            if(transactionUtil.isWireTransferTransactionEnabled()) {
                transactionUtil.addTransaction(transaction);
            }
            return true;
        } else {
            return false;
        }
    }


    /**
     * Can withdraw transfer result.
     *
     * @param uuid     the uuid
     * @param amount   the amount
     * @param currency the currency
     * @param transfer the transfer
     * @return the transfer result
     */
    public TransferResult canWithdraw(UUID uuid, double amount, String currency, boolean transfer) {
        currency = currency.toUpperCase();

        if (!CurrencyUtil.isCurrencyEnabled()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            if (econ != null && econ.has(player, amount)) {
                return TransferResult.SUCCESS;
            } else {
                return TransferResult.INSUFFICIENT_FUNDS;
            }
        }

        Optional<JsonObject> json = gsonUtil.getJson(uuid);
        CardType cardType = CardTypeUtil.getCardTypeForPlayer(uuid);


        if (json.isPresent()) {
            if (json.get().has("balances") && json.get().getAsJsonObject("balances").has(currency)) {
                double currentBalance = json.get().getAsJsonObject("balances").get(currency).getAsDouble();
                double availableBalance = cardType != null ? Math.min(currentBalance, cardType.getWithdrawLimit()) : currentBalance;

                double dailyWithdraw = dailyWithdraws.getOrDefault(uuid, 0.0);
                double remainingDailyWithdraw = cardType != null ? Math.max(0, cardType.getPerDayWithdrawLimit() - dailyWithdraw) : Double.MAX_VALUE;

                if (!(amount <= availableBalance)) {
                    return TransferResult.INSUFFICIENT_FUNDS;
                }

                if (transfer) {
                    if (!(amount <= cardType.getTransferLimit())) {
                        return TransferResult.TRANSFER_LIMIT_REACHED;
                    }
                } else {
                    if (!(amount <= remainingDailyWithdraw)) {
                        return TransferResult.DAILY_WITHDRAW_LIMIT_REACHED;
                    }
                }


                return TransferResult.SUCCESS;
            } else {
                return TransferResult.INSUFFICIENT_FUNDS;
            }
        } else {
            return TransferResult.ACCOUNT_NOT_FOUND;
        }
    }

    private boolean withdrawWithCurrencySystem(UUID uuid, double amount, String currency) {
        Optional<JsonObject> json = gsonUtil.getJson(uuid);

        if (json.isPresent() && json.get().has("balances")) {
            if (json.get().getAsJsonObject("balances").has(currency)) {
                double currentBalance = json.get().getAsJsonObject("balances").get(currency).getAsDouble();
                if (currentBalance >= amount) {
                    json.get().getAsJsonObject("balances").addProperty(currency, currentBalance - amount);
                    gsonUtil.setJson(uuid.toString(), json.get());
                    gsonUtil.saveConfig();
                    dailyWithdraws.put(uuid, dailyWithdraws.getOrDefault(uuid, 0.0) + amount);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void depositWithCurrencySystem(UUID uuid, double amount, String currency) {
        JsonObject json = gsonUtil.getJson(uuid).orElseGet(JsonObject::new);

        if (!json.has("balances")) {
            json.add("balances", new JsonObject());
        }
        if (!json.getAsJsonObject("balances").has(currency)) {
            json.getAsJsonObject("balances").addProperty(currency, amount);
        } else {
            double currentBalance = json.getAsJsonObject("balances").get(currency).getAsDouble();
            json.getAsJsonObject("balances").addProperty(currency, currentBalance + amount);
        }
        gsonUtil.setJson(uuid.toString(), json);
        gsonUtil.saveConfig();
    }




    /**
     * Gets balance.
     *
     * @param player   the player
     * @param currency the currency
     * @return the balance
     */
    public double getBalance(OfflinePlayer player, String currency) {
        currency = currency.toUpperCase();

        if(!CurrencyUtil.isCurrencyEnabled()) {
            return Main.getInstance().getEconomyCore().getBalance(player);
        }

        Optional<JsonObject> json = gsonUtil.getJson(player.getUniqueId());
        if (json.isPresent() && json.get().has("balances") && json.get().getAsJsonObject("balances").has(currency)) {
            double rawBalance = json.get().getAsJsonObject("balances").get(currency).getAsDouble();
            BigDecimal roundedBalance = new BigDecimal(rawBalance).setScale(2, RoundingMode.HALF_UP);

            return roundedBalance.doubleValue();
        } else {
            return 0;
        }

    }


    /**
     * Sets the balance of the specified player for the given currency.
     *
     * @param playerId the UUID of the player
     * @param currency the currency to set the balance for
     * @param amount   the new balance for the player
     */
    public void setBalance(UUID playerId, String currency, double amount) {
        GSONUtil playerDataGson = Main.getInstance().getPlayerDataGson();
        currency = currency.toUpperCase();

        java.util.Optional<JsonObject> optionalJsonObject = Optional.of(playerDataGson.getJson(playerId).orElseGet(JsonObject::new));
        JsonObject playerData = optionalJsonObject.get();

        if(!playerData.has("balances")) {
            playerData.add("balances", new JsonObject());
        }

        JsonObject balances = playerData.getAsJsonObject("balances");

        balances.addProperty(currency, amount);

        playerDataGson.setJson(playerId.toString(), playerData);
    }


    /**
     * Has bank account boolean.
     *
     * @param uuid the uuid
     * @return the boolean
     */
    public boolean hasBankAccount(UUID uuid) {


        return gsonUtil.getJson(uuid).isPresent();
    }

    /**
     * Compare card id boolean.
     *
     * @param cardID the card id
     * @param uuid   the uuid
     * @return the boolean
     */
    public boolean compareCardID(String cardID, UUID uuid) {
        Optional<JsonObject> json = gsonUtil.getJson(uuid);
        return json.map(jsonObject -> jsonObject.get("cardID").getAsString().equals(cardID)).orElse(false);
    }


    /**
     * Gets balance messages.
     *
     * @param creditCardNumber the credit card number
     * @param subject          the subject
     * @return the balance messages
     */
    public List<String> getBalanceMessages(String creditCardNumber, OfflinePlayer subject) {
        List<String> messages = new ArrayList<>();
        messages.add("<green>Credit Card Number: " + creditCardNumber);

        String mainCurrency = CurrencyUtil.getMainCurrency();
        String currencySymbol = CurrencyUtil.getSymbol(mainCurrency);

        if(CurrencyUtil.isCurrencyEnabled()) {

            for(String currency : CurrencyUtil.getCurrencies()) {
                currencySymbol = CurrencyUtil.getSymbol(currency);
                messages.add("<green>Balance (" + currency.toUpperCase() + "): " + this.getBalance(subject, currency) + currencySymbol);
            }

        } else {
            messages.add("<green>Balance: " + Main.getInstance().getEconomyCore().getBalance(subject) + currencySymbol);
        }

        return messages;
    }

    /**
     * Exchange transfer result.
     *
     * @param uuid         the uuid
     * @param fromCurrency the from currency
     * @param toCurrency   the to currency
     * @param amount       the amount
     * @return the transfer result
     */
    //works only with CurrencySystem Enabled
    public TransferResult exchange(UUID uuid, String fromCurrency, String toCurrency, double amount) {
        double exchangeRate = CurrencyUtil.getExchangeRates().get(fromCurrency).get(toCurrency);

        double amountInToCurrency = MathUtil.round(amount * exchangeRate);


        boolean withdrawSuccess = withdraw(uuid, fromCurrency, amount);

        if (withdrawSuccess) {
            deposit(uuid, toCurrency, amountInToCurrency);
            return TransferResult.SUCCESS;
        } else {
            return TransferResult.INSUFFICIENT_FUNDS;
        }
    }










}
