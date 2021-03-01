package com.eaton.rest.api.drsetup;

import org.testng.annotations.Test;

public class SampleTests {

    @Test
    public void test1() {
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.loadgroup\\ecobee.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        DrSetupCreateRequest.createLoadGroup(body);
    }

    @Test
    public void test2() {

        DrSetupGetRequest.getLoadGroup(187);
    }
}
