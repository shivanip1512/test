package com.eaton.api.tests.virtualdevices;

import static org.assertj.core.api.Assertions.assertThat;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.Test;

import com.eaton.builders.assets.virtualdevices.VirtualDeviceCreateService;
import com.eaton.framework.APIs;
import com.eaton.framework.TestConstants;
import com.eaton.rest.api.common.ApiCallHelper;
import com.eaton.rest.api.virtualdevice.VirtualDeviceRequest;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class VirtualDeviceDeleteApiTests {
    
    private Faker faker = new Faker();

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VIRTUAL_DEVICES })
    public void virtualDevice_Delete_Success() {
        Pair<JSONObject, JSONObject> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceOnlyRequiredFields();
        
        JSONObject createresponse = pair.getValue1();
        Integer id = createresponse.getInt("id");

        ExtractableResponse<?> response = ApiCallHelper.delete(APIs.VirtualDevice.DELETE_VIRTUALDEVICE + id);

        assertThat(response.statusCode()).isEqualTo(200);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void virtualDevice_Delete_NotFoundId_400BadRequest() {
        String invalidId = faker.number().digits(9);
        
        ExtractableResponse<?> response = ApiCallHelper.delete(APIs.VirtualDevice.DELETE_VIRTUALDEVICE + invalidId);
        
        assertThat(response.statusCode()).isEqualTo(400);
    }
}
