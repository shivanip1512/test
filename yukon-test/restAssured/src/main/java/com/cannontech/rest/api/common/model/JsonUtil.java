package com.cannontech.rest.api.common.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    public static String beautifyJson(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonString);
        } catch (JsonProcessingException e) {
        }
        return json;
    }
}
