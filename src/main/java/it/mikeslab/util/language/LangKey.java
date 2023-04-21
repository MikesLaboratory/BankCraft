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

package it.mikeslab.util.language;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LangKey {

    ALREADY_REGISTERED("<red>This player already has a creditcard!"),
    INSUFFICIENT_FUNDS("<red>You don't have enough money to do this!"),
    DAILY_WITHDRAW_LIMIT_REACHED("<red>You have reached the daily withdraw limit!"),
    INVENTORY_FULL_DROPPED("<red>Your inventory is full, item dropped!"),
    INVENTORY_FULL("<red>Your inventory is full!"),
    ATM_RECEIVED("<green>You received a placeable ATM!"),
    ATM_PLACED("<green>You placed an ATM at %location%!"),
    ATM_REMOVED("<green>You removed an ATM at %location%!"),
    NO_PERMISSION("<red>You don't have permission to do this!"),
    NO_CREDIT_CARD("<red>This is not a valid credit card!"),
    WRONG_PIN("<red>Wrong PIN!"),
    NOT_REGISTERED("<red>Player %player% doesn't have a credit card!"),
    NO_BANK_ACCOUNT("<red>You don't have a bank account!"),
    TRANSFER_LIMIT_REACHED("<red>Your amount goes over your transfer limit for this card type!"),
    ERROR_OCCURRED("<red>An error occurred! Please contact MikesLab group!"),
    SUBJECT_CANNOT_RECEIVE("<red>Subject cannot receive money since the amount goes over his deposit limit!"),
    TRANSFER_SUCCESS("<green>You transferred %amount% %currency% to %receiver%. Reason: %description%!"),
    DEPOSITED("<green>You deposited %value% %currency%!"),
    DEPOSIT_FAILED("<red>Deposit failed! Is it a valid banknote?"),
    CURRENCY_NOT_FOUND("<red>Currency %currency% not found!"),
    AMOUNT_MUST_BE_POSITIVE("<red>Amount must be positive!"),
    AMOUNT_TOO_HIGH("<red>Amount too high!"),
    PLAYER_ONLY("<red>This command can only be executed by a player!"),
    NOT_VALID_PLAYER("<red>Player must be specified!"),
    CREDIT_CARD_DELETED("<green>%player%'s credit card has been deleted!"),
    CREDIT_CARD_BEING_CREATED("<green>Creating a new credit card for %player%..."),
    BANK_GIVEN("<green>You added %amount% %currency% to %player%'s Bank account!"),
    BANK_TAKEN("<green>You took %amount% %currency% from %player%'s Bank account!"),
    BANK_SET("<green>You set %player%'s Bank account to %amount% %currency%!"),
    BANK_RESET("<green>You reset %player%'s Bank account!"),
    REASON_TOO_LONG("<red>Reason too long! (%length% words exceeded)"),
    OPERATION_CANCELLED("<red>Operation cancelled!"),
    CANNOT_PERFORM_EXCHANGE("<red>You cannot perform this exchange!"),
    EXCHANGE_PERFORMED("<green>You exchanged %value% %currencyFrom% to %finalValue% %currencyTo%!"),
    EXCHANGE_TRANSACTION_REASON("Exchange %currencyFrom% to %currencyTo%"),
    INVALID_CURRENCY("<red>Invalid currency!"),
    INVALID_VALUE("<red>Invalid value! Format: <number> <currency>"),
    INVALID_NUMBER("<red>Invalid number!"),


    ANVIL_INSERT_PIN("Insert your PIN:"),
    ANVIL_PIN_TEXT("PIN"),

    ATM_TITLE("ATM"),
    ATM_WITHDRAWAL_REASON("Banknote Withdrawal"),

    RETURN_CURRENCIES_SELECTOR("<red>Return to currencies selector"),

    PREVIOUS_PAGE("<red>Previous page (%prevpage%)"),
    NEXT_PAGE("<green>Next page (%nextpage%)"),

    EXCHANGE_ITEM_NAME("<green>Exchange"),

    BALANCE_ITEM_NAME("<green>Balance: %s"),

    SELECT_CARD_TYPE_TITLE("Select card type"),

    CARD_TYPE("<green>Type: %s"),
    CARD_DAILY_WITHDRAW_LIMIT("<green>Daily withdraw limit: %s"),
    CARD_DEPOSIT_LIMIT("<green>Deposit limit: %s"),
    CARD_TRANSFER_LIMIT("<green>Transfer limit: %s"),
    CARD_WITHDRAW_LIMIT("<green>Withdraw limit: %s"),

    CONFIRM("<green>Confirm"),
    CANCEL("<red>Cancel"),

    SELECT_CURRENCY_TITLE("Select currency"),
    SELECT_CURRENCY_ITEM_NAME("<red>Select currency"),
    EXCHANGE_RATES("<green>Exchange rates:"),

    EXCHANGE_TO("<green>Exchange to:"),
    EXCHANGE_FROM("<green>Exchange from:"),
    EXCHANGE("Exchange"),
    BALANCE("Balance"),
    CLOSE("<red>Close"),
    TRANSACTION_HISTORY("Transaction history"),

    FILTER_BY_DATE("<green>Filter by date"),
    REMOVE_DATE_FILTER("<red>Remove date filter"),
    TRANSACTION("<green>Transaction: <yellow>%s"),
    TRANSACTION_AMOUNT("<green>Amount: <yellow>%s"),
    TRANSACTION_SENDER("<green>Sender: <yellow>%s"),
    TRANSACTION_RECEIVER("<green>Receiver: <yellow>%s"),
    TRANSACTION_REASON("<green>Reason: <yellow>%s"),
    TRANSACTION_CURRENCY("<green>Currency: <yellow>%s"),
    TRANSACTION_ID("<green>ID: <yellow>%s"),
    INVALID_DATE_FORMAT("<red>Invalid date format! Please use dd.MM.yy"),
    ENTER_DATE("Enter date (dd.MM.yy)"),
    COMMAND_SPECIFY_SUBJECT("<red>You must specify a player if you are not a player yourself."),
    OPEN_TRANSACTION_MENU("<green>Click here to open <yellow>transactions menu<green>."),

    WIRE_TRANSFER_REASON("Wire Transfer"),
    WIRE_TRANSFER_TO("Wire transfer to %player%");

    private final String defaultValue;

}
