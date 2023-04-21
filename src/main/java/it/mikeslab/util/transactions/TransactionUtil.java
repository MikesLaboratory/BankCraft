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
import com.mongodb.client.MongoCollection;
import it.mikeslab.Main;
import it.mikeslab.util.json.GSONUtil;
import it.mikeslab.util.database.MongoDBHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;

import java.util.*;

/**
 * A utility class for managing transactions between player accounts.
 */
@RequiredArgsConstructor
public class TransactionUtil {

    private final GSONUtil gsonUtil = Main.getInstance().getTransactionGson();
    private final boolean useMongo;
    private final MongoDBHandler mongoHandler;
    private final String transactionsCollectionName;
    @Getter private boolean wireTransferTransactionEnabled, bankNoteTransactionEnabled, exchangeTransactionEnabled;

    /**
     * Init.
     */
    public void init() {
        if(!useMongo) return;

        mongoHandler.createCollectionIfNotExists(transactionsCollectionName);
        Bukkit.getLogger().info("Transactions collection initialized.");

        wireTransferTransactionEnabled = Main.getInstance().getConfig().getBoolean("transactions.wire-transfer");
        bankNoteTransactionEnabled = Main.getInstance().getConfig().getBoolean("transactions.banknote-withdrawal");
        exchangeTransactionEnabled = Main.getInstance().getConfig().getBoolean("transactions.exchange");
    }


    /**
     * Adds a new transaction to the collection.
     *
     * @param transaction the transaction to add
     */
    public void addTransaction(Transaction transaction) {
        if (useMongo) {
            Document document = new Document();
            document.append("from", transaction.getFrom().toString())
                    .append("to", transaction.getTo().toString())
                    .append("reason", transaction.getReason())
                    .append("currency", transaction.getCurrency())
                    .append("amount", transaction.getAmount())
                    .append("timestamp", transaction.getTimestamp());
            mongoHandler.insertDocument(transactionsCollectionName, document);
        } else {
            JsonObject transactionJson = gsonUtil.getJson(transaction.getId()).orElseGet(JsonObject::new);
            transactionJson.addProperty("id", transaction.getId().toString());
            transactionJson.addProperty("from", transaction.getFrom().toString());
            transactionJson.addProperty("to", transaction.getTo().toString());
            transactionJson.addProperty("reason", transaction.getReason());
            transactionJson.addProperty("currency", transaction.getCurrency());
            transactionJson.addProperty("amount", transaction.getAmount());
            transactionJson.addProperty("timestamp", transaction.getTimestamp());
            gsonUtil.setJson(transaction.getId(), transactionJson);
            gsonUtil.saveConfig();
        }
    }

    /**
     * Removes a transaction from the collection.
     *
     * @param transactionId the ID of the transaction to remove
     * @return true if the transaction was removed, false if it was not found
     */
    public boolean removeTransaction(String transactionId) {
        if(getTransaction(transactionId) == null) {
            return false;
        }


        if (useMongo) {
            mongoHandler.deleteDocument(transactionsCollectionName, transactionId);
        } else {
            gsonUtil.deleteObject(transactionId);
        }

        return true;
    }

    /**
     * Retrieves a transaction from the collection by ID.
     *
     * @param transactionId the ID of the transaction to retrieve
     * @return the retrieved transaction, or null if not found
     */
    public Transaction getTransaction(String transactionId) {
        if (useMongo) {
            MongoCollection<Document> collection = mongoHandler.getDatabase().getCollection(transactionsCollectionName);
            Document document = collection.find(new Document("_id", new ObjectId(transactionId))).first();
            if (document == null) {
                return null;
            }
            String id = (document.getObjectId("_id").toString());
            UUID from = UUID.fromString(document.getString("from"));
            UUID to = UUID.fromString(document.getString("to"));
            String currency = document.getString("currency");
            String reason = document.getString("reason");
            double amount = document.getDouble("amount");
            long timestamp = document.getLong("timestamp");
            return new Transaction(id, from, to, currency, reason, amount, timestamp);
        } else {
            Optional<JsonObject> transactionJson = gsonUtil.getJson(transactionId);
            if (transactionJson.isEmpty()) {
                return null;
            }
            String id = transactionJson.get().get("id").getAsString();
            UUID from = UUID.fromString(transactionJson.get().get("from").getAsString());
            UUID to = UUID.fromString(transactionJson.get().get("to").getAsString());
            String currency = transactionJson.get().get("currency").getAsString();
            String reason = transactionJson.get().get("reason").getAsString();
            double amount = transactionJson.get().get("amount").getAsDouble();
            long timestamp = transactionJson.get().get("timestamp").getAsLong();
            return new Transaction(id, from, to, currency, reason, amount, timestamp);
        }
    }

    /**
     * Retrieves all transactions involving a specified account.
     *
     * @param accountId the ID of the account to retrieve transactions for
     * @param limit     the maximum number of transactions to retrieve
     * @param offset    the number of transactions to skip
     * @return a JsonArray of all transactions involving the specified account
     */
    public List<Transaction> getTransactionsForAccount(UUID accountId, int limit, int offset) {
        if (useMongo) {
            MongoCollection<Document> collection = mongoHandler.getDatabase().getCollection(transactionsCollectionName);
            Document query = new Document("$or",
                    Arrays.asList(
                            new Document("from", accountId.toString()),
                            new Document("to", accountId.toString())
                    )
            ).append("timestamp", new Document("$lte", System.currentTimeMillis()));


            List<Transaction> transactionList = new ArrayList<>();
            for (Document document : collection.find(query).skip(offset).limit(limit)) {
                Transaction transaction = Transaction.empty();
                transaction.setId(document.getObjectId("_id").toString());
                transaction.setFrom(UUID.fromString(document.getString("from")));
                transaction.setTo(UUID.fromString(document.getString("to")));
                transaction.setAmount(document.getDouble("amount"));
                transaction.setCurrency(document.getString("currency"));
                transaction.setReason(document.getString("reason"));
                transaction.setTimestamp(document.getLong("timestamp"));
                transactionList.add(transaction);
            }
            return transactionList;
        } else {
            List<Transaction> transactionList = new ArrayList<>();
            int count = 0;
            for (String key : gsonUtil.keySet()) {
                Optional<JsonObject> transactionJson = gsonUtil.getJson(key);
                if (transactionJson.get().get("from").getAsString().equals(accountId.toString()) || transactionJson.get().get("to").getAsString().equals(accountId.toString())) {
                    if (count >= offset && count < offset + limit) {
                        Transaction transaction = Transaction.empty();
                        transaction.setId(transactionJson.get().get("id").getAsString());
                        transaction.setFrom(UUID.fromString(transactionJson.get().get("from").getAsString()));
                        transaction.setTo(UUID.fromString(transactionJson.get().get("to").getAsString()));
                        transaction.setReason(transactionJson.get().get("reason").getAsString());
                        transaction.setAmount(transactionJson.get().get("amount").getAsDouble());
                        transaction.setCurrency(transactionJson.get().get("currency").getAsString());
                        transaction.setTimestamp(transactionJson.get().get("timestamp").getAsLong());
                        transactionList.add(transaction);
                    }
                    count++;
                    if (count == offset + limit) {
                        break;
                    }
                }
            }
            return transactionList;
        }
    }

}
