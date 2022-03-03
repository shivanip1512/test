package com.eaton.api.tests.v1.virtualdevices;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;

import com.eaton.builders.assets.virtualdevices.VirtualDeviceCreateService;
import com.eaton.framework.APIs;
import com.eaton.framework.TestConstants;
import com.eaton.rest.api.common.ApiCallHelper;

import io.restassured.response.ExtractableResponse;

public class GetAllVirtualDeviceV1ApiTests {
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VIRTUAL_DEVICES })
    public void getAllVirtualDeviceApi_200Success() {
        SoftAssertions softly = new SoftAssertions();
        Pair<JSONObject, JSONObject> pair = VirtualDeviceCreateService.createVirtualDeviceOnlyRequiredFields();
        
        JSONObject createResponse = pair.getValue1();
        
        Integer deviceId = createResponse.getInt("id");
        String paoType = createResponse.getString("type");
        String name = createResponse.getString("name");
        Boolean enabled = createResponse.getBoolean("enable");

        ExtractableResponse<?> response = ApiCallHelper.get(APIs.VirtualDevice.GET_VIRTUALDEVICES);
        JSONObject body = new JSONObject(response.asString());
        JSONArray deviceList = new JSONArray(body.get("items").toString());
        String deviceDetails = null;
        
        for(int i = 0; i < deviceList.length(); i++) {
            if(deviceList.get(i).toString().contains("\"id\":" + deviceId)) {
                deviceDetails = deviceList.get(i).toString();
            }
        }
        
        softly.assertThat(body.toString()).contains("pageNumber");
        softly.assertThat(body.toString()).contains("itemsPerPage");
        softly.assertThat(body.toString()).contains("totalItems");
        softly.assertThat(deviceDetails).isNotNull();
        softly.assertThat(deviceDetails).contains("\"id\":" + deviceId);
        softly.assertThat(deviceDetails).contains("\"type\":\"" + paoType);
        softly.assertThat(deviceDetails).contains("\"name\":\"" + name);
        softly.assertThat(deviceDetails).contains("\"enable\":" + enabled);
        softly.assertAll();
    }
}
