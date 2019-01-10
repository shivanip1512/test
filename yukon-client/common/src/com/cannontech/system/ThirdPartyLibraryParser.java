package com.cannontech.system;

import java.io.IOException;
import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ThirdPartyLibraryParser {
    
    public static ThirdPartyLibrary[] parse(InputStream inputStream) throws IOException {
        Yaml y = new Yaml();
        
        Object yamlObject = y.load(inputStream);
        
        ObjectMapper jsonFormatter = new ObjectMapper();
        
        byte[] jsonBytes = jsonFormatter.writeValueAsBytes(yamlObject);
        
        ThirdPartyLibrary[] parsedLicensing = jsonFormatter.readValue(jsonBytes, ThirdPartyLibrary[].class);
        
        return parsedLicensing;
    }
}
