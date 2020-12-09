package com.eaton.api.tests.v1.virtualdevices;

import static org.assertj.core.api.Assertions.assertThat;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.Test;

import com.eaton.builders.assets.virtualdevices.VirtualDeviceCreateService;
import com.eaton.framework.APIs;
import com.eaton.framework.TestConstants;
import com.eaton.rest.api.common.ApiCallHelper;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class DeleteVirtualDeviceV1ApiTests {
    
    private Faker faker = new Faker();

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VIRTUAL_DEVICES })
    public void deleteVirtualDeviceApi_200Success() {
        Pair<JSONObject, JSONObject> pair = VirtualDeviceCreateService.createVirtualDeviceOnlyRequiredFields();
        
        JSONObject createresponse = pair.getValue1();
        Integer id = createresponse.getInt("id");

        ExtractableResponse<?> response = ApiCallHelper.delete(APIs.VirtualDevice.DELETE_VIRTUALDEVICE + id);

        assertThat(response.statusCode()).isEqualTo(200);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void deleteVirtualDevice_NotFoundId_400BadRequest() {
        String invalidId = faker.number().digits(9);
        
        ExtractableResponse<?> response = ApiCallHelper.delete(APIs.VirtualDevice.DELETE_VIRTUALDEVICE + invalidId);
        
        assertThat(response.statusCode()).isEqualTo(400);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void deleteVirtualDevice_InvalidAttrId_400BadRequest() {
        String invalidId = faker.number().digits(12);
        
        ExtractableResponse<?> response = ApiCallHelper.delete(APIs.VirtualDevice.DELETE_VIRTUALDEVICE + invalidId);
        
        assertThat(response.statusCode()).isEqualTo(400);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void deleteVirtualDevice_EmptyId_404NotFound() {
        
        ExtractableResponse<?> response = ApiCallHelper.delete(APIs.VirtualDevice.DELETE_VIRTUALDEVICE + "");
        
        assertThat(response.statusCode()).isEqualTo(404);
    }
}
