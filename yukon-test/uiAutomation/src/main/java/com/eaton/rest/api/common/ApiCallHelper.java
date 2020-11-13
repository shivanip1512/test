package com.eaton.rest.api.common;

import static io.restassured.RestAssured.given;

import io.restassured.response.ExtractableResponse;
import io.restassured.specification.RequestSpecification;

public class ApiCallHelper {
    // private static String userDirectory = System.getProperty("user.dir");
    public static final String AUTH_TOKEN = new ApiTokenAuthentication().getAuthToken();

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

    public static ExtractableResponse<?> post(String pathParam, Object body, String id) {
        return getHeader().body(body).when().post(pathParam + id).then().log().all().extract();
    }

    public static ExtractableResponse<?> delete(String pathParam) {
        return getHeader().delete(pathParam).then().log().all().extract();
    }

    /**
     * Returns <code>ExtractableResponse</code> by invoking corresponding HTTP PUT method for specified URI
     * and request parameter.
     */
    public static ExtractableResponse<?> put(String pathParam, Object body) {
        return getHeader().body(body).put(pathParam).then().log().all().extract();
    }

    /**
     * Returns <code>ExtractableResponse</code> by invoking corresponding HTTP PATCH method for specified URI
     * and request parameter.
     */
    public static ExtractableResponse<?> patch(String pathParam, Object body) {
        return getHeader().body(body).when().patch(pathParam).then().log().all().extract();
    }

    private static RequestSpecification getHeader() {
        return given().accept("application/json").contentType("application/json").header("Authorization", "Bearer " + AUTH_TOKEN).log().all();
    }
}