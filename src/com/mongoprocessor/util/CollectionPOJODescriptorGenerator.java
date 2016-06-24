package com.mongoprocessor.util;

import com.mongoprocessor.types.CollectionPOJODescriptor;
import com.mongoprocessor.types.DocumentTypes;
import com.mongoprocessor.types.MongoElement;

import java.util.ArrayList;
import java.util.List;

/**
 * this class will generate the collection structure descriptor, i.e. a class that
 * neatly represents the collections structure which would hence be mapped to create a class or POJO
 */
@SuppressWarnings("unused")
public class CollectionPOJODescriptorGenerator {

    private List<MongoElement> genElements;

    /**
     * the default private constructor accepting the collection structure which is obtained using the <code>CollectionStructureGenerator</code>
     * class provided with the API
     * @param elements represents the <code>ArrayList</code> of generated collection elements.
     */
    private CollectionPOJODescriptorGenerator(List<MongoElement> elements) {
        this.genElements = elements;
    }

    /**
     * a static factory method to get <code>CollectionPOJODescriptor</code> instance required by <code>CollectionCodeGenerator</code>
     * class to generate the code
     * @param elements as list of native collection elements obtained via <code>CollectionStructureGenerator</code>
     * @return <code>CollectionPOJODescriptor</code>
     */
    public static CollectionPOJODescriptor generateDescriptor (List<MongoElement> elements) {
        CollectionPOJODescriptorGenerator generator = new CollectionPOJODescriptorGenerator(elements);
        CollectionPOJODescriptor descriptor = new CollectionPOJODescriptor();

        elements.forEach(consumer -> {
            ++descriptor.totalPropertyCounter;
            // init the root properties
            if (consumer.parent.equals("root")) {
                descriptor.rootElementNames.add(consumer);
                ++descriptor.rootPropertyCounter;
            }

            // init he the nested documents
            if (consumer.type == DocumentTypes.NESTED_DOCUMENT) {
                descriptor.nestedDocuments.put(consumer, generator.elementsUnder(consumer.parent+"."+consumer.name));
            } else if (consumer.type == DocumentTypes.DOC_ARRAY) {
                 descriptor.nestedArrays.put(consumer, generator.arraysUnder(consumer.parent+"."+consumer.name));
            }


        });

        return descriptor;
    }

    /**
     * documentName = parentName + "." + documentName
     * eg. for contact document under root, the documentName would be root.contact
     * @param documentName
     * @return
     */
    private List<MongoElement> elementsUnder (String documentName) {
        List<MongoElement> elements = new ArrayList<>();

        genElements.forEach(consumer -> {
            if (consumer.parent.equals(documentName))
                elements.add(consumer);
        });

        return elements;
    }

    /**
     * get the arrays under the document
     * @param documentName
     * @return
     */
    private List<MongoElement> arraysUnder (String documentName) {
        List<MongoElement> elements = new ArrayList<>();

        genElements.forEach(consumer -> {
            if (consumer.parent.equals(documentName))
                elements.add(consumer);
        });

        return elements;
    }
}
