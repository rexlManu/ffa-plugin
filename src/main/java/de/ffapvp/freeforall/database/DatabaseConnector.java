/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.ffapvp.freeforall.database;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import lombok.Getter;
import lombok.NonNull;
import lombok.var;
import org.bson.Document;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("ALL")
public class DatabaseConnector implements Closeable {

    private MongoClient client;

    private MongoDatabase database;

    @Getter
    private MongoCollection<Document> statsCollection, inventoriesCollection;

    public DatabaseConnector(DatabaseConfiguration configuration, String statsCollection) throws MongoException {
        if (configuration.isLogin()) {
            MongoCredential credential = MongoCredential.createCredential(configuration.getUser(), "admin", configuration.getPassword().toCharArray());
            this.client = new MongoClient(new ServerAddress(configuration.getHost(), configuration.getPort()), Collections.singletonList(credential));
        } else {
            this.client = new MongoClient(configuration.getHost(), configuration.getPort());
        }
        this.database = this.client.getDatabase(configuration.getDatabase());
        this.statsCollection = this.database.getCollection(statsCollection);
        this.inventoriesCollection = this.database.getCollection("inventories");
    }

    public Document find(String key) {
        return this.statsCollection.find(Filters.eq("key", key)).first();
    }

    public List<Document> findMany(String keyName, String key) {
        return this.statsCollection.find(Filters.eq(keyName, key)).into(new ArrayList<Document>());
    }

    public Boolean has(String key) {
        return this.find(key) != null;
    }

    public DeleteResult delete(String key) {
        return this.statsCollection.deleteOne(Filters.eq("key", key));
    }

    public void insertOrUpdate(String key, @NonNull Document document) {
        if (this.has(key)) {
            this.statsCollection.updateOne(Filters.eq("key", key), new Document("$set", document));
        } else {
            this.statsCollection.insertOne(document);
        }
        return;
    }

    public void insert(Document document) {
        this.statsCollection.insertOne(document);
        return;
    }

    @Override
    public void close() throws IOException {
        if (this.client == null) return;
        try {
            this.client.close();
        } catch (final MongoException e) {
            throw new IOException(e);
        }
    }
}