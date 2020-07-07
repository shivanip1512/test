package com.eaton.rest.api.dbetoweb;

import org.testng.annotations.Test;

public class SampleTests {

    @Test
    public void test1() {
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.loadgroup\\ecobee.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        DBEToWebCreateRequest.createLoadGroup(body);
    }

    @Test
    public void test2() {

        DBEToWebGetRequest.getLoadGroup(187);
    }
}
