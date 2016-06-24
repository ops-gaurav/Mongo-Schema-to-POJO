package com.mongoprocessor.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * a simple plain old java object representing the Collection Description to be used by the
 * <code>CollectionCodeGenerator</code> to easily generate the java code
 * @author gaurav
 */
public class CollectionPOJODescriptor {

    public int rootPropertyCounter = 0;
    public int totalPropertyCounter = 0;
    public List<MongoElement> rootElementNames = new ArrayList<>();
    public Map<MongoElement, List<MongoElement>> nestedDocuments = new HashMap<>();
    public Map<MongoElement, List<MongoElement>> nestedArrays = new HashMap<>();

    @Override
    public String toString () {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Total "+ totalPropertyCounter +" properties in this collection\n");
        stringBuilder.append("Root contains "+ rootPropertyCounter + " properties\n");

        stringBuilder.append("Collection Structure\n");
        stringBuilder.append("root\n");
        rootElementNames.forEach(consumer -> {
            if (consumer.type != DocumentTypes.DOC_ARRAY || consumer.type != DocumentTypes.NESTED_DOCUMENT ||
                    consumer.type != DocumentTypes.RAW_ARRAY)
                stringBuilder.append("\t"+ consumer.name+"\n");
        });

        rootElementNames.forEach(consumer -> {
            if (consumer.type == DocumentTypes.NESTED_DOCUMENT) {
                stringBuilder.append("\t"+ consumer.name +"\n");
                nestedDocuments.get(consumer).forEach (consumer2 -> {
                    stringBuilder.append("\t\t"+ consumer2.name);
                }) ;
                stringBuilder.append("\n");
            } else if (consumer.type == DocumentTypes.DOC_ARRAY) {
                stringBuilder.append("\t"+ consumer.name +"\n");
                nestedArrays.get(consumer).forEach(consumer2 -> {
                    stringBuilder.append("\t\t"+ consumer2.name);
                });
                stringBuilder.append("\n");
            }
        });

        return stringBuilder.toString();
    }

}
