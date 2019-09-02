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


public class JsonFileReader {

    static String payloadBaseDir = System.getProperty("user.dir") + "\\src\\test\\resources\\payload\\";

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

    /**
     * This method reads any Json file, Iterates through all the keys and updates the value and returns as
     * JSONObject
     * You can also provide the nested keys separated by a dot like key1.key2 if they are nested in hierarchy
     * 
     * @param payloadFile - relative path ( w.r.t to payload directory)
     * @param key - attribute whose value to be update
     * @param replaceValueWith - value to update
     * @return JSONObject
     */
    public static JSONObject updateJsonFile(String payloadFile, String key, String replaceValueWith) {
        JSONObject jsonObj = JsonFileReader.readJsonFileAsJSONObject(payloadFile);
        // when the keys are nested and separated by dot(.)
        if (key.contains(".")) {
            String keyArray[] = key.split("\\.");
            // traverse till the last key and set the key value
            JSONObject newObject = null;
            for (int i = 0; i < keyArray.length - 1; i++) {
                // iterate till the parentKey matches with required key name in keySet
                newObject = (JSONObject) jsonObj.get(keyArray[i]);
            }
            newObject.replace(keyArray[keyArray.length - 1], replaceValueWith);
            return jsonObj;

        } else {
            jsonObj.replace(key, replaceValueWith);
            return jsonObj;
        }
    }
}