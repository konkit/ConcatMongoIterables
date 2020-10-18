package tech.konkit.mongoiteratorchain;

import com.google.common.collect.Iterables;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class ConcatMongoIterables {

    private enum EnumType {
        FIRST_VALUE, MIDDLE_VALUE, LAST_VALUE
    }

    private static final Logger logger = LoggerFactory.getLogger(ConcatMongoIterables.class);

    private static final String dbName = "iteratorTest";
    private static final String collectionName = "iteratorTest";

    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase(dbName);
        MongoCollection<Document> collection = database.getCollection(collectionName);

        try {
            insertDocuments(collection);

            FindIterable<Document> iterable1 = collection.find(Filters.eq("enumValue", EnumType.FIRST_VALUE.toString()));
            FindIterable<Document> iterable2 = collection.find(Filters.eq("enumValue", EnumType.MIDDLE_VALUE.toString()));
            FindIterable<Document> iterable3 = collection.find(Filters.eq("enumValue", EnumType.LAST_VALUE.toString()));

            Iterable<Document> result = Iterables.concat(iterable1, iterable2, iterable3);

            result.forEach(x -> logger.warn("Received value: {}", x.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.drop();
        }

    }

    private static void insertDocuments(MongoCollection<Document> collection) {
        List<Document> list = Arrays.asList(
                new Document("enumValue", EnumType.FIRST_VALUE.toString()),
                new Document("enumValue", EnumType.LAST_VALUE.toString()),
                new Document("enumValue", EnumType.MIDDLE_VALUE.toString()),
                new Document("enumValue", EnumType.FIRST_VALUE.toString())
        );

        collection.insertMany(list);
    }
}
