package com.eaton.rest.api.dbetoweb;

import static org.junit.Assert.assertTrue;

import org.testng.annotations.Test;

import com.eaton.framework.ConfigFileReader;
import com.eaton.rest.api.common.ApiCallHelper;

import io.restassured.response.ExtractableResponse;

public class DBEToWebGetRequest {

    public static ExtractableResponse<?> getLoadGroup(int id) {
        String pathParam = PathParametes.getParam("getLoadGroup") + id;
        ExtractableResponse<?> response = ApiCallHelper.get(pathParam);
        assertTrue("Error in create load group", response.statusCode() == 200);
        return response;
    }

    public static ExtractableResponse<?> getLoadProgram(int id) {
        String pathParam = PathParametes.getParam("getLoadProgram") + id;
        ExtractableResponse<?> response = ApiCallHelper.get(pathParam);
        assertTrue("Error in create load program", response.statusCode() == 200);
        return response;
    }

    public static ExtractableResponse<?> getControlArea(int id) {
        String pathParam = PathParametes.getParam("getControlArea") + id;
        ExtractableResponse<?> response = ApiCallHelper.get(pathParam);
        assertTrue("Error in create control area", response.statusCode() == 200);
        return response;
    }

    public static ExtractableResponse<?> getControlScenario(int id) {
        String pathParam = PathParametes.getParam("getProgramConstraint") + id;
        ExtractableResponse<?> response = ApiCallHelper.get(pathParam);
        assertTrue("Error in create lcontrol scenario", response.statusCode() == 200);
        return response;
    }

    public static ExtractableResponse<?> getProgramConstraint(int id) {
        String pathParam = PathParametes.getParam("getProgramConstraint") + id;
        ExtractableResponse<?> response = ApiCallHelper.get(pathParam);
        assertTrue("Error in create program constraint", response.statusCode() == 200);
        return response;
    }
}