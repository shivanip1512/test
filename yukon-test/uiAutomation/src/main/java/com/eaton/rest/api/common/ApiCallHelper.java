package com.eaton.rest.api.common;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import com.eaton.framework.ConfigFileReader;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ApiCallHelper {
    //private static String userDirectory = System.getProperty("user.dir");
    public static String authToken = new ApiTokenAuthentication().getAuthToken();

    /**
     * Returns <code>ExtractableResponse</code> by invoking corresponding HTTP GET method for specified URI
     * and request parameter.
     * 
     */
    public static ExtractableResponse<?> get(String pathParam) {
        return getHeader().get(pathParam).then().log().all().extract();
    }
    
    public static ExtractableResponse<?> post(String pathParam, Object body) {
        return getHeader().body(body).when().post(pathParam).then().log().all().extract();
    }

    private static RequestSpecification getHeader() {
        return given().accept("application/json").contentType("application/json").header("Authorization",
            "Bearer " + authToken).log().all();
    }
}
