package com.cannontech.rest.api.common;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import io.restassured.response.ExtractableResponse;
import io.restassured.specification.RequestSpecification;

public class ApiCallHelper {
    private static String userDirectory = System.getProperty("user.dir");
    private static String authToken = new ApiTokenAuthentication().getAuthToken();

    /**
     * Returns <code>File</code> object specified by file name.
     * 
     * @param fileName
     * @return
     */
    public static File getInputFile(String fileName) {
        return new File(userDirectory + File.separatorChar + "resources" + File.separatorChar + fileName);
    }

    /**
     * Returns value for the specified key from configuration.properties file.
     * 
     */
    public static String getProperty(String key) {
        try {
            FileReader reader = new FileReader(
                userDirectory + File.separatorChar + "configs" + File.separatorChar + "configuation.properties");
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
     * @param key
     * @param getParam
     * @return ExtractableResponse
     */
    public static ExtractableResponse<?> get(String key, String param) {
        String uri = getProperty(key);
        return getHeader().get(uri + param).then().extract();

    }

    /**
     * Returns <code>ExtractableResponse</code> by invoking corresponding HTTP POST method for specified URI
     * and input file
     * 
     * @param key
     * @param fileName
     * @return ExtractableResponse
     */
    public static ExtractableResponse<?> post(String key, String fileName) {
        String uri = getProperty(key);
        File file = getInputFile(fileName);
        return getHeader().body(file).when().post(uri).then().extract();
    }

    /**
     * Returns <code>ExtractableResponse</code> by invoking corresponding HTTP DELETE method for specified URI
     * and request parameter.
     * 
     * @param key
     * @return ExtractableResponse
     */

    public static ExtractableResponse<?> delete(String key, String param) {
        String uri = getProperty(key);
        return getHeader().delete(uri + param).then().extract();
    }

    private static RequestSpecification getHeader() {
        return given().accept("application/json").contentType("application/json").header("Authorization",
            "Bearer " + authToken);
    }
}
