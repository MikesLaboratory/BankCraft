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

package it.mikeslab.util.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The type Mongo db handler.
 */
@Getter
public class MongoDBHandler {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private Gson gson;

    /**
     * Creates a new instance of MongoDBHandler with the specified connection details.
     *
     * @param connectionString the connection string to use
     * @param databaseName     the name of the database to connect to
     */
    public MongoDBHandler(String connectionString, String databaseName) {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
        Logger.getLogger("org.mongodb.driver.cluster").setLevel(Level.WARNING);
        Logger.getLogger("org.mongodb.driver.connection").setLevel(Level.WARNING);
        Logger.getLogger("org.mongodb.driver.management").setLevel(Level.WARNING);
        Logger.getLogger("org.mongodb.driver.protocol.insert").setLevel(Level.WARNING);
        Logger.getLogger("org.mongodb.driver.protocol.query").setLevel(Level.WARNING);
        Logger.getLogger("org.mongodb.driver.protocol.update").setLevel(Level.WARNING);

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new com.mongodb.ConnectionString(connectionString))
                .build();
        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase(databaseName);
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Insert document.
     *
     * @param collectionName the collection name
     * @param document       the document
     */
// Overload for inserting a document without a JSON object
    public void insertDocument(String collectionName, Document document) {
        createCollectionIfNotExists(collectionName);

        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.insertOne(document);
    }



    /**
     * Deletes a document from the specified collection by transaction ID UUID.
     *
     * @param collectionName the name of the collection to delete the document from
     * @param transactionId  the UUID of the transaction to delete
     */
    public void deleteDocument(String collectionName, String transactionId) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.deleteOne(new Document("_id", new ObjectId(transactionId)));
    }

    /**
     * Create collection if not exists.
     *
     * @param collectionName the collection name
     */
    public void createCollectionIfNotExists(String collectionName) {
        if (!database.listCollectionNames().into(new ArrayList<>()).contains(collectionName)) {
            database.createCollection(collectionName);
        }
    }

    /**
     * Closes the connection to the MongoDB server.
     */
    public void close() {
        mongoClient.close();

        mongoClient = null;
        database = null;
        gson = null;
    }
}

