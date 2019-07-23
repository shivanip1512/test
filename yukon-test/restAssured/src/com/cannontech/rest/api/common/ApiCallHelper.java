package com.cannontech.rest.api.common;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import org.json.simple.JSONObject;

import io.restassured.response.ExtractableResponse;
import io.restassured.specification.RequestSpecification;

public class ApiCallHelper {
    private static String userDirectory = System.getProperty("user.dir");
    private static String authToken = new ApiTokenAuthentication().getAuthToken();

    /**
     * Returns <code>File</code> object specified by file name.
     * 
     */
    public static File getInputFile(String fileName) {
        return new File(userDirectory + File.separatorChar + "resources" + File.separatorChar + "payload" + File.separatorChar + fileName);
    }

    /**
     * Returns value for the specified key from configuration.properties file.
     * 
     */
    public static String getProperty(String key) {
        try {
            FileReader reader = new FileReader(
                userDirectory + File.separatorChar + "configs" + File.separatorChar + "configuration.properties");
            Properties properties = new Properties();
            properties.load(reader);
            return properties.getProperty(key);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Returns <code>ExtractableResponse</code> by invoking corresponding HTTP GET method for specified URI
     * and request parameter.
     * 
     */
    public static ExtractableResponse<?> get(String key, String param) {
        String uri = getProperty(key);
        return getHeader().get(uri + param).then().log().all().extract();

    }

    /**
     * Returns <code>ExtractableResponse</code> by invoking corresponding HTTP POST method for specified URI
     * and input file
     * 
     */
    public static ExtractableResponse<?> post(String key, String fileName) {
        String uri = getProperty(key);
        File file = getInputFile(fileName);
        return getHeader().body(file).when().post(uri).then().log().all().extract();
    }

    /**
     * Returns <code>ExtractableResponse</code> by invoking corresponding HTTP POST method for specified URI,
     * input file and path variable.
     * 
     */
    public static ExtractableResponse<?> post(String key, String fileName, String param) {
        String uri = getProperty(key);
        File file = getInputFile(fileName);
        return getHeader().body(file).when().post(uri + param).then().log().all().extract();
    }

    /**
     * Returns <code>ExtractableResponse</code> by invoking corresponding HTTP POST method for specified URI
     * and JSON Object payload
     * 
     */
    public static ExtractableResponse<?> post(String key, JSONObject payload) {
        String uri = getProperty(key);
        String body = payload.toJSONString();
        return getHeader().body(body).when().post(uri).then().log().all().extract();
    }

    /**
     * Returns <code>ExtractableResponse</code> by invoking corresponding HTTP POST method for specified URI,
     * JSON Object payload and path variable.
     * 
     */
    
    public static ExtractableResponse<?> post(String key, JSONObject payload, String param) {
        String uri = getProperty(key);
        String body = payload.toJSONString();
        return getHeader().body(body).when().post(uri + param).then().log().all().extract();
    }
    
    /**
     * Returns <code>ExtractableResponse</code> by invoking corresponding HTTP DELETE method for specified URI
     * and request parameter.
     * 
     */

    public static ExtractableResponse<?> delete(String key, String param) {
        String uri = getProperty(key);
        return getHeader().delete(uri + param).then().log().all().extract();
    }

    /**
     * Returns <code>ExtractableResponse</code> by invoking corresponding HTTP DELETE method for specified
     * URI, input file and path variable.
     * 
     */

    public static ExtractableResponse<?> delete(String key, String fileName, String param) {
        String uri = getProperty(key);
        File file = getInputFile(fileName);
        return getHeader().body(file).delete(uri + param).then().extract();
    }

    private static RequestSpecification getHeader() {
        return given().accept("application/json").contentType("application/json").header("Authorization",
            "Bearer " + authToken).log().all();
    }
}
