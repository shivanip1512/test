package com.cannontech.system;

import java.io.IOException;
import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ThirdPartyLibraryParser {
    
    public static ThirdPartyLibraries parse(InputStream inputStream) throws IOException {
        Yaml y = new Yaml();
        
        Object yamlObject = y.load(inputStream);
        
        ObjectMapper jsonFormatter = new ObjectMapper();
        
        byte[] jsonBytes = jsonFormatter.writeValueAsBytes(yamlObject);
        
        ThirdPartyLibraries parsedLicensing = jsonFormatter.readValue(jsonBytes, ThirdPartyLibraries.class);
        
        return parsedLicensing;
    }
}
