package com.cannontech.amr.rfn.service.pointmapping.icd;

import java.io.IOException;
import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class YukonPointMappingIcdParser {
    
    public static PointMappingIcd parse(InputStream inputStream) throws IOException {
        Yaml y = new Yaml();
        
        Object yamlObject = y.load(inputStream);
        
        ObjectMapper jsonFormatter = new ObjectMapper();
        
        byte[] jsonBytes = jsonFormatter.writeValueAsBytes(yamlObject);
        
        PointMappingIcd parsedIcd = jsonFormatter.readValue(jsonBytes, PointMappingIcd.class);
        
        return parsedIcd;
    }
    
    public static String parseToJson(InputStream inputStream) throws JsonProcessingException {
        Yaml y = new Yaml();
        
        Object yamlObject = y.load(inputStream);
        
        ObjectMapper jsonFormatter = new ObjectMapper();
        
        jsonFormatter.enable(SerializationFeature.INDENT_OUTPUT);
        
        return jsonFormatter.writeValueAsString(yamlObject);
    }
}
