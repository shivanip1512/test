package com.cannontech.rest.api.common;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import io.restassured.response.ExtractableResponse;
import io.restassured.specification.RequestSpecification;

public class ApiCallHelper {
    private static String userDirectory = System.getProperty("user.dir");
    public static String authToken = new ApiTokenAuthentication().getAuthToken();

    /**
     * Returns <code>File</code> object specified by file name.
     * 
     */
    public static File getInputFile(String fileName) {
        return new File(userDirectory + File.separatorChar +"src" + File.separatorChar+"test"+ File.separatorChar+ "resources" + File.separatorChar + "payload"
            + File.separatorChar + fileName);
    }

    /**
     * Returns value for the specified key from configuration.properties file.
     * 
     */
    public static String getProperty(String key) {
        try {
            FileReader reader = new FileReader(
                userDirectory + File.separatorChar + "src" + File.separatorChar+ "main" + File.separatorChar+ "resources" + File.separatorChar + "configuration.properties");
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
     * and JSON Object payload
     * 
     */
    public static ExtractableResponse<?> post(String key, Object body) {
        String uri = getProperty(key);
        return getHeader().body(body).when().post(uri).then().log().all().extract();
    }

    public static ExtractableResponse<?> post(String key, Object body, String param) {
        String uri = getProperty(key);
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

    
    public static ExtractableResponse<?> delete(String key, Object body, String param) {
        String uri = getProperty(key);
        return getHeader().body(body).delete(uri + param).then().log().all().extract();
    }

    private static RequestSpecification getHeader() {
        return given().accept("application/json").contentType("application/json").header("Authorization",
            "Bearer " + authToken).log().all();
    }
}
