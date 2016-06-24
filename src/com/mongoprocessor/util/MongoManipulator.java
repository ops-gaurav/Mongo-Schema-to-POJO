package com.mongoprocessor.util;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Iterator;

/**
 * Created by gaurav on 21/06/16.
 */
public class MongoManipulator {

    private MongoClient client;

    public Document getRootDocument (String db, String col) {
        client = new MongoClient();

        MongoDatabase database = client.getDatabase(db);
        MongoCollection collection = database.getCollection(col);

        Iterator<Document> iterator = collection.find().iterator();

        while (iterator.hasNext()) {
            Document current = iterator.next();
            return current;
        }
        return null;

    }

    public void close (){
        client.close();
    }

}
