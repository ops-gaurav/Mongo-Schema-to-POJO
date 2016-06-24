package com.mongoprocessor.util;

import com.mongoprocessor.types.DocumentTypes;
import com.mongoprocessor.types.MongoElement;
import static com.mongoprocessor.types.SupportedTypes.*;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * the native class that works with the mongodb dirctly to extract the collection information into
 * raw ArrayList of MongoElement
 * @author gaurav
 */
@SuppressWarnings("unused")
public class CollectionStructureGenerator {

    private List<MongoElement> elements = new ArrayList<>();
    private Document rootDocument;

    /**
     * the default constructor requests the Document to generate structur, the same could be obtained either
     * natively using MongoDB Driver or using MongoManipulator helper class provided with this API
     * @param rootDocument
     */
    public CollectionStructureGenerator (Document rootDocument) {
        this.rootDocument = rootDocument;
    }

    /**
     * get the structure
     * @return list containing all the nodes of the collection with information
     */
    public  List<MongoElement> getCollectionStructure () {
        try {
            processDocument(rootDocument, "root");
        } catch (ClassNotFoundException error) {
            throw new RuntimeException(error.toString()) ;
        }
        return elements;
    }

    /**
     * recursive function to process a document and hence append it to collection list
     * @param document
     * @param parent
     * @throws ClassNotFoundException
     */
    private void processDocument (Document document, String parent) throws ClassNotFoundException{
        Iterator<String> keySet = document.keySet().iterator();

        while (keySet.hasNext()) {

            String currentKey = keySet.next();

            MongoElement element = new MongoElement();
            element.name = currentKey;
            element.parent = parent;


            if (!elementAlreadyExists(element.name, element.parent)) {

                Object type = document.get(currentKey);
                if (type == null)
                    continue;
                switch (type.getClass().getName()) {
                    case DOC:
                        element.dataType = Class.forName(DOC);
                        element.type = DocumentTypes.NESTED_DOCUMENT;

                        elements.add(element);
                        Document nestedDocument = (Document) document.get(currentKey);
                        processDocument(nestedDocument, parent + "." + currentKey);
                        break;
                    case ARRAY:

                        element.dataType = Class.forName(ARRAY);

                        ArrayList list = (ArrayList) document.get(currentKey);
                        Object firstObject = list.get(0);

                        switch (firstObject.getClass().getName()) {

                            case DOC:
                                element.type = DocumentTypes.DOC_ARRAY;
                                elements.add(element);
                                processDocument((Document) firstObject, parent + "." + currentKey);
                                break;
                            default:
                                element.type = DocumentTypes.RAW_ARRAY;
                                elements.add(element);
                        }
                        break;
                    default:
                        element.dataType = Class.forName(type.getClass().getName());
                        element.type = DocumentTypes.PROPERTY;
                        elements.add(element);
                }
            }
        }
    }

    /**
     * checks whether the element already exists in the collection based on name and parent values as multiple elements
     * can have same names but not same parents.
     * @param name
     * @param parent
     * @return
     */
    private boolean elementAlreadyExists (String name, String parent) {
        for (MongoElement element: elements)
            if (element.name.equals(name) && element.parent.equals(parent))
                return true;
        return false;
    }


}
