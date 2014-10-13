package com.cannontech.common.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JsonUtils {
    
    public static final TypeReference<String> STRING_TYPE = new TypeReference<String>() {};
    public static final TypeReference<Integer> INT_TYPE = new TypeReference<Integer>() {};
    public static final TypeReference<Long> LONG_TYPE = new TypeReference<Long>() {};
    
    private static final ObjectReader reader;
    private static final ObjectWriter writer;
    static {
        ObjectMapper mapper = new ObjectMapper();
        // Disabling FAIL_ON_UNKNOWN_PROPERTIES tells jackson to ignore setters in POJOs 
        // for which properties arn't present in the json string. 
        // We still need to add @JsonIgnore to any method (or field for all setters/getters) 
        // if there are multiple setters with the same name.
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        reader = mapper.reader();
        writer = mapper.writer();
    }
    
   /**
   * Serialize JSON into a Java Object
   */
   public static <T> T fromJson(String json, TypeReference<T> type) throws IOException {
       return reader.withType(type).readValue(json);
   }
   
    /**
     * Serialize JSON into a Java Object
     */
    public static <T> T fromJson(String json, Class<T> type) throws IOException {
        return reader.withType(type).readValue(json);
    }
    
    /**
     * @return JSON representation of Object
     */
    public static String toJson(Object object) throws JsonProcessingException {
        return writer.writeValueAsString(object);
    }
    
    public static ObjectWriter getWriter() {
        return writer;
    }
    
}