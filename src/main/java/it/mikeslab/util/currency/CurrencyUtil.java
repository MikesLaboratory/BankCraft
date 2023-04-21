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

package it.mikeslab.util.currency;

import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import java.text.DecimalFormat;
import java.util.*;

/**
 * The type Currency util.
 */
public class CurrencyUtil {
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("$#,##0.00");
    @Getter private static final Map<String, Map<String, Double>> exchangeRates = new HashMap<>();
    private static double mainCurrencyExchangeRate = 1.0;
    private static String mainCurrency = "USD";
    private static boolean CURRENCY_ENABLED = false;
    private static FileConfiguration config;

    /**
     * Initializes the exchange rates from the plugin configuration file.
     *
     * @param config the plugin configuration file
     */
    public static void initializeExchangeRates(FileConfiguration config) {
        CurrencyUtil.config = config;

        CURRENCY_ENABLED = config.getBoolean("multi-currency-enabled", false);

        mainCurrency = config.getString("main-currency", "USD");
        mainCurrencyExchangeRate = config.getDouble("currencies." + mainCurrency + ".exchange-rate", 1.0);

        // Load exchange rates for other currencies from the config file

        if (config.contains("currencies")) {
            for (String currency : config.getConfigurationSection("currencies").getKeys(false)) {

                if(!Objects.equals(currency, mainCurrency) && !CURRENCY_ENABLED) {
                    continue;
                }

                Map<String, Double> exchangeRate = new HashMap<>();


                for(String currencyRate : config.getConfigurationSection("currencies." + currency + ".exchange-rates").getKeys(false)) {
                    double exchangeValue = config.getDouble("currencies." + currency + ".exchange-rates." + currencyRate);
                    exchangeRate.put(currencyRate, exchangeValue);
                }

                exchangeRates.put(currency, exchangeRate);
            }
        }
    }

    /**
     * Converts the given amount of the specified currency to the main currency.
     *
     * @param amount   the amount of currency to convert
     * @param currency the currency to convert from
     * @return the converted amount in the main currency
     */
    public static double convertToMainCurrency(double amount, String currency) {
        currency = currency.toLowerCase();
        if (exchangeRates.containsKey(currency)) {
            return amount * exchangeRates.get(mainCurrency).get(currency);
        } else if (currency.equals("usd")) {
            return amount;
        } else {
            return 0.0;
        }
    }

    /**
     * Converts the given amount of the main currency to the specified currency.
     *
     * @param amount   the amount of main currency to convert
     * @param currency the currency to convert to
     * @return the converted amount in the specified currency
     */
    public static double convertFromMainCurrency(double amount, String currency) {
        currency = currency.toLowerCase();
        if (exchangeRates.containsKey(currency)) {
            return amount * exchangeRates.get(currency).get(mainCurrency);
        } else if (currency.equals("usd")) {
            return amount;
        } else {
            return 0.0;
        }
    }

    /**
     * Formats the given amount of the main currency for display.
     *
     * @param amount the amount of main currency to format
     * @return the formatted currency string
     */
    public static String formatCurrency(double amount) {
        return CURRENCY_FORMAT.format(amount);
    }


    /**
     * Is currency enabled boolean.
     *
     * @return the boolean
     */
    public static boolean isCurrencyEnabled() {
        return CURRENCY_ENABLED;
    }

    /**
     * Is currency boolean.
     *
     * @param currency the currency
     * @return the boolean
     */
    public static boolean isCurrency(String currency) {
        return exchangeRates.containsKey(currency.toUpperCase());
    }


    /**
     * Gets symbol.
     *
     * @param currency the currency
     * @return the symbol
     */
    public static String getSymbol(String currency) {
       return config.getString("currencies." + currency.toUpperCase() + ".symbol", "$");
    }

    /**
     * Gets currency.
     *
     * @param symbol the symbol
     * @return the currency
     */
    public static String getCurrency(String symbol) {
        for(String currency : config.getConfigurationSection("currencies").getKeys(false)) {
            if(config.getString("currencies." + currency + ".symbol", "$").equals(symbol)) {
                return currency;
            }
        }

        return null;
    }


    /**
     * Gets currencies.
     *
     * @return the currencies
     */
    public static List<String> getCurrencies() {
        return new ArrayList<>(exchangeRates.keySet());
    }

    /**
     * Gets main currency.
     *
     * @return the main currency
     */
    public static String getMainCurrency() {
        return mainCurrency;
    }


    public static String format(double amount) {
        return CURRENCY_FORMAT.format(amount).replace("$", "");
    }

}