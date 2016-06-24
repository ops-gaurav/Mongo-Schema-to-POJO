package com.mongoprocessor.types;

/**
 * the raw mongo collection element descriptor
 * @author gaurav
 */
public class MongoElement {
    public String name;
    public Class dataType;
    public DocumentTypes type;
    public String parent;
}
