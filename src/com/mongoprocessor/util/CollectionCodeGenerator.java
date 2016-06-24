package com.mongoprocessor.util;

import com.mongoprocessor.types.CollectionPOJODescriptor;
import com.mongoprocessor.types.MongoElement;

import java.util.ArrayList;
import java.util.List;

import static com.mongoprocessor.types.SupportedTypes.*;

/**
 * this is the main class that will generate the code provided with the CollectionPOJODescriptor class
 * @author gaurav
 */
@SuppressWarnings("unused")
public class CollectionCodeGenerator {

    private  CollectionPOJODescriptor descriptor;
    private StringBuilder importsBuilder = new StringBuilder();
    private StringBuilder mainClassBuilder = new StringBuilder();
    private StringBuilder classesBuilder = new StringBuilder();


    /**
     * the default constructor that accepts CollectionPOJODescriptor as argument
     * @param descriptor represents the POJO description reuired by this class for POJO generation
     */
    public CollectionCodeGenerator (CollectionPOJODescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * call this method to initiate the code generation
     * @param collectionName the name of the target class
     * @return the generated code
     */
    public String generateCode (String collectionName) {
        String generatedCode = "";

        mainClassBuilder.append("@SuppressWarnings(\"unused\")\npublic class "+ collectionName +" { \n\n");
        generateClassCode();
        mainClassBuilder.append(classesBuilder.toString());
        mainClassBuilder.append("}");

        generatedCode += importsBuilder.toString();
        generatedCode += "\n";
        generatedCode+= mainClassBuilder.toString();

        return generatedCode;
    }

    /**
     * private method to generate the class codes
     */
    private void generateClassCode () {
        // processing the core documents first
        descriptor.nestedDocuments.forEach((parent, nodes) -> buildClassesFor(parent, nodes));

        // now build the array document classes
        descriptor.nestedArrays.forEach((parent, nodes) -> buildClassesFor(parent, nodes));

        descriptor.rootElementNames.forEach(consumer -> {
            switch(consumer.dataType.getName()) {
                case INT:
                    mainClassBuilder.append("\t@Expose\n\tpublic int "+ consumer.name +";\n\n");
                    break;
                case DOUBLE:
                    mainClassBuilder.append("\t@Expose\n\tpublic double "+ consumer.name +";\n\n");
                    break;
                case BOOLEAN:
                    mainClassBuilder.append("\t@Expose\n\tpublic boolean "+ consumer.name +";\n\n");
                    break;
                case STR:
                    mainClassBuilder.append("\t@Expose\n\tpublic String "+ consumer.name +";\n\n");
                    break;
                case OBJ_ID:
                    if (!importsBuilder.toString().contains("import org.bson.types.ObjectId;"))
                        importsBuilder.append("import org.bson.types.ObjectId;\n");
                    mainClassBuilder.append("\t@Expose\n\tpublic ObjectId "+ consumer.name +";\n\n");
                    break;
                case DOC:
                    String typeName = generateClassName(getClassKeywordsFor(consumer));
                    String varName = generateVariableName(getClassKeywordsFor(consumer));
                    mainClassBuilder.append("\tpublic "+ typeName +" "+ varName +";\n\n");
                    break;
                case ARRAY:
                    switch (consumer.type){
                        case RAW_ARRAY:
                            if (!importsBuilder.toString().contains("import java.util.ArrayList;"))
                                importsBuilder.append("import java.util.ArrayList;\n");
                            mainClassBuilder.append("\t@Expose\n\tpublic ArrayList<Object> "+ consumer.name +"s;\n\n");
                            break;
                        case DOC_ARRAY:
                            String docClassName = generateClassName(getClassKeywordsFor(consumer));;
                            String vName = generateVariableName(getClassKeywordsFor(consumer));
                            if (!importsBuilder.toString().contains("import java.util.ArrayList;"))
                                importsBuilder.append("import java.util.ArrayList;\n");
                            mainClassBuilder.append("\t@Expose\n\tpublic ArrayList<"+ docClassName +"> "+ vName +"s;\n\n");
                            break;
                    }
                    break;

            }
        });

    }

    /**
     * this module generate the required classes
     * @param parent the root node having type DOCUMENT or ARRAY
     * @param nodes the nodes under the DOC or ARRAY type
     */
    private void buildClassesFor (MongoElement parent, List<MongoElement> nodes) {

        String className = generateClassName(getClassKeywordsFor(parent));

        classesBuilder.append("\t@SuppressWarnings(\"unused\")\n\tclass "+ className + "{\n\n");
        nodes.forEach(consumer -> {
            switch (consumer.dataType.getName()) {
                case INT:
                    classesBuilder.append("\t\t@Expose\n\t\tpublic int "+ consumer.name +";\n\n");
                    break;
                case DOUBLE:
                    classesBuilder.append("\t\t@Expose\n\t\tpublic double "+ consumer.name +";\n\n");
                    break;
                case BOOLEAN:
                    classesBuilder.append("\t\t@Expose\n\t\tpublic boolean "+ consumer.name +";\n\n");
                    break;
                case STR:
                    classesBuilder.append("\t\t@Expose\n\t\tpublic String "+ consumer.name +";\n\n");
                    break;
                case OBJ_ID:
                    if (!importsBuilder.toString().contains("import org.bson.types.ObjectId;"))
                        importsBuilder.append("import org.bson.types.ObjectId;\n");
                    classesBuilder.append("\t\t@Expose\n\t\tpublic ObjectId "+ consumer.name +";\n\n");
                    break;
                case DOC:
                    String documentClassName = generateClassName(getClassKeywordsFor(consumer));
                    String varName = generateVariableName(getClassKeywordsFor(consumer));
                    classesBuilder.append("\t\t@Expose\n\t\tpublic "+ documentClassName +" "+ varName +";\n\n") ;
                    break;
                case ARRAY:
                    switch (consumer.type){
                        case RAW_ARRAY:
                            if (!importsBuilder.toString().contains("import java.util.ArrayList;"))
                                importsBuilder.append("import java.util.ArrayList;\n");
                            classesBuilder.append("\t\t@Expose\n\t\tpublic ArrayList<Object> "+ consumer.name +"s;\n");
                            break;
                        case DOC_ARRAY:
                            String docClassName = generateClassName(getClassKeywordsFor(consumer));;
                            String vName = generateVariableName(getClassKeywordsFor(consumer));
                            if (!importsBuilder.toString().contains("import java.util.ArrayList;"))
                                importsBuilder.append("import java.util.ArrayList;\n");
                            classesBuilder.append("\t\t@Expose\n\t\tpublic ArrayList<"+ docClassName +"> "+ vName +"s;\n");
                            break;
                    }
                    break;
            }
        });
        classesBuilder.append("\t}\n");
    }

    /**
     * detects the appropriate keywords for class creation
     * @param element represents the MongoElement instance to extract keywords from
     * @return list of eligible keywords for type name
     */
    private ArrayList<String> getClassKeywordsFor (MongoElement element) {

        ArrayList<String> keywords = new ArrayList<>();

        if (element.parent.equals("root"))
            keywords.add(element.name);
        else {
            for (String name : element.parent.split("\\."))
                if (!name.equals("root"))
                    keywords.add(name);
            keywords.add(element.name);
        }

        return keywords;

    }

    /**
     * generate class name provided with the keywords
     * @param words
     * @return
     */
    private String generateClassName (ArrayList<String> words) {
        String javaCasedClassName = "";

        for (String word: words)
            javaCasedClassName += (word.charAt(0)+"").toUpperCase()+word.substring(1, word.length());

        return javaCasedClassName;
    }

    /**
     * generate the variable name using keywords
     * @param words
     * @return
     */
    private String generateVariableName (ArrayList<String> words) {
        String javaCasedClassName = "";

        for (String word: words) {
            if (word.equals(words.get(0)))
                javaCasedClassName += word;
            else
                javaCasedClassName += (word.charAt(0) + "").toUpperCase() + word.substring(1, word.length());
        }

        return javaCasedClassName;
    }

}
