package com.eaton.rest.api.drsetup;

import java.io.FileReader;

import org.json.simple.parser.JSONParser;

public class JsonFileHelper {

    public static Object parseJSONFile(String file) {

        JSONParser parser = new JSONParser();
        Object obj;
        try {
            obj = parser.parse(new FileReader(file));
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
