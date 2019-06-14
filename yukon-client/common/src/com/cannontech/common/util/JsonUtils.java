package com.cannontech.common.util;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

public class JsonUtils {
    
    private static final Logger log = YukonLogManager.getLogger(JsonUtils.class);
    
    public static final TypeReference<String> STRING_TYPE = new TypeReference<String>() {};
    public static final TypeReference<Integer> INT_TYPE = new TypeReference<Integer>() {};
    public static final TypeReference<Long> LONG_TYPE = new TypeReference<Long>() {};
    public static final TypeReference<Boolean> BOOLEAN_TYPE = new TypeReference<Boolean>() {};
    
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
    
    /**
     * JSON representation of Object.
     * @param isRenderedOnUI if set to true, the occurrences of "</" is replaced with "<\\/". 
     * This is to prevent XSS issues on UI. 
     */
    public static String toJson(Object object, boolean isRenderedOnUI) throws JsonProcessingException {
        if (isRenderedOnUI) {
            return toJson(object).replace("</", "<\\/");
        } else {
            return toJson(object);
        }
    }
    
    public static String beautifyJson(String jsonString) throws JsonProcessingException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson((new JsonParser()).parse(jsonString));
        } 
    
    public static ObjectWriter getWriter() {
        return writer;
    }
    
    /**
     * Attempts to write the object as a JSON response payload.
     * @param resp - The response to write to.
     * @param json - The object to convert to a JSON payload.
     * @throws {@link JsonReponseException} if anything goes wrong writing out the response.
     * @return null - Returns null as a convenience for request mapping methods that need to return a String view.
     */
    public static String writeResponse(HttpServletResponse resp, Object json) {
        
        resp.setContentType("application/json");
        
        try {
            writer.writeValue(resp.getOutputStream(), json);
        } catch (Exception e) {
            log.error("Unable to write object as JSON response body.", e);
            throw new JsonReponseException("Unable to write object as JSON response body.", e);
        }
        
        // Return null to be used as a view for request mapping methods returning String views.
        return null;
    }
    
}