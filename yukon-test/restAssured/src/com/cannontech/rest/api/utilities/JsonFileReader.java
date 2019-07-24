package com.cannontech.rest.api.utilities;

import java.awt.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Neeraj Rawat
 *
 */

public class JsonFileReader {

    static String payloadBaseDir = System.getProperty("user.dir") + "\\resources\\payload\\";

    /**
     * This method reads Json file, and returns data as JSONObject
     * 
     * @param payloadFile - relative path ( w.r.t to payload directory)
     * @return JSONObject
     */

    public static JSONObject readJsonFileAsJSONObject(String payloadFile) {

        String payload = payloadBaseDir + payloadFile;
        JSONObject obj = null;

        try {
            FileReader file = new FileReader(payload);
            JSONParser jsonParser = new JSONParser();
            obj = (JSONObject) jsonParser.parse(file);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return obj;
    }

    /**
     * This method updates LoadGroup json data, provided as JSONObject and returns as JSONObject
     * 
     * @param obj - JSONObject
     * @param key - attribute whose value to be update
     *        * @param replaceValueWith - value to update
     * @return JSONObject
     */

    @SuppressWarnings("unchecked")
    public static JSONObject updateLoadGroup(JSONObject obj, String key, String replaceValueWith) {

        Set<String> set = obj.keySet();
        String LGBase = set.iterator().next();
        Log.info("Updating load group : " + LGBase);
        JSONObject obj2 = (JSONObject) obj.get(LGBase);
        obj2.replace(key, replaceValueWith);
        obj.replace(LGBase, obj2);
        Log.info("Updated load group : " + obj);
        return obj;

    }

    /**
     * This method reads LoadGroup Json file, updates the value and returns as JSONObject
     * 
     * @param payloadFile - relative path ( w.r.t to payload directory)
     * @param key - attribute whose value to be update
     *        * @param replaceValueWith - value to update
     * @return JSONObject
     */

    @SuppressWarnings("unchecked")
    public static JSONObject updateLoadGroup(String payloadFile, String key, String replaceValueWith) {

        JSONObject obj = JsonFileReader.readJsonFileAsJSONObject(payloadFile);

        Set<String> set = obj.keySet();
        String LGBase = set.iterator().next();
        Log.info("Updating load group : " + LGBase);

        JSONObject obj2 = (JSONObject) obj.get(LGBase);
        obj2.replace(key, replaceValueWith);
        obj.replace(LGBase, obj2);
        Log.info("Updated load group : " + obj);
        return obj;

    }
}