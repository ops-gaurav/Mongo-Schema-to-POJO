package com.mongoprocessor;

import com.mongoprocessor.types.CollectionPOJODescriptor;
import com.mongoprocessor.util.CollectionCodeGenerator;
import com.mongoprocessor.util.CollectionPOJODescriptorGenerator;
import com.mongoprocessor.util.CollectionStructureGenerator;
import com.mongoprocessor.util.MongoManipulator;

/**
 * a standalone class for code generation
 * Created by gaurav on 25/06/16.
 */
@SuppressWarnings("unused")
public class MongoSchemaMapper {

    /**
     * a static method callback for code generation
     * @param db name of mongo database
     * @param collection name of target collection under db
     * @param className name of the class to generate
     * @return String having code corrosponding to this collection
     */
    public static String generateCodeFor (String db, String collection, String className) {

        MongoManipulator manipulator = new MongoManipulator();

        CollectionPOJODescriptor descriptor = CollectionPOJODescriptorGenerator.generateDescriptor(
                new CollectionStructureGenerator(manipulator.getRootDocument(db, collection)).getCollectionStructure()
        );

        manipulator.close();
        return new CollectionCodeGenerator(descriptor).generateCode(className);

    }

}
