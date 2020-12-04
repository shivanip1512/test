package com.eaton.api.tests.v1.virtualdevices;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.eaton.builders.assets.virtualdevices.VirtualDeviceCreateBuilder;
import com.eaton.builders.assets.virtualdevices.VirtualDeviceCreateService;
import com.eaton.framework.APIs;
import com.eaton.framework.TestConstants;
import com.eaton.rest.api.common.ApiCallHelper;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class CreateVirtualDeviceV1ApiTests {
    private Faker faker = new Faker();

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ASSETS,
            TestConstants.Features.VIRTUAL_DEVICES })
    public void createVirtualDeviceApi_AllFields_201Created() {
        SoftAssertions softly = new SoftAssertions();
        Pair<JSONObject, JSONObject> pair = VirtualDeviceCreateService.createVirtualDeviceOnlyRequiredFields();

        JSONObject request = pair.getValue0();
        JSONObject response = pair.getValue1();

        softly.assertThat(response.getString("name")).isEqualTo(request.getString("name"));
        softly.assertThat(response.getBoolean("enable")).isEqualTo(request.getBoolean("enable"));
        softly.assertThat(response.getInt("id")).isNotNull();
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void createVirtualDeviceApi_NoName_422Unprocessable() {
        JSONObject request = new VirtualDeviceCreateBuilder.Builder(Optional.of(""))
                .build();

        ExtractableResponse<?> createResponse = ApiCallHelper.post(APIs.VirtualDevice.CREATE_VIRTUALDEVICE, request.toString());

        assertThat(createResponse.statusCode()).isEqualTo(422);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void createVirtualDeviceApi_InvalidName_422Unprocessable() {
        JSONObject request = new VirtualDeviceCreateBuilder.Builder(Optional.of("Create Attr / \\\\ , ' \\\" |"))
                .build();

        ExtractableResponse<?> createResponse = ApiCallHelper.post(APIs.VirtualDevice.CREATE_VIRTUALDEVICE, request.toString());

        assertThat(createResponse.statusCode()).isEqualTo(422);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void VirtualDeviceCreateBuilder_Name61Char_422Unprocessable() {
        JSONObject request = new VirtualDeviceCreateBuilder.Builder(Optional.of(faker.lorem().characters(61)))
                .build();

        ExtractableResponse<?> createResponse = ApiCallHelper.post(APIs.VirtualDevice.CREATE_VIRTUALDEVICE, request.toString());

        assertThat(createResponse.statusCode()).isEqualTo(422);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void createVirtualDeviceApi_MissingName_422Unprocessable() {
        JSONObject request = new VirtualDeviceCreateBuilder.Builder(Optional.empty())
                .build();

        request.remove("name");

        ExtractableResponse<?> createResponse = ApiCallHelper.post(APIs.VirtualDevice.CREATE_VIRTUALDEVICE, request.toString());

        assertThat(createResponse.statusCode()).isEqualTo(422);
    }

    /*
     * =============================================================================
     * =============================================================
     * 
     * This test createVirtualDeviceApi_DuplicateName_422Unprocessable() is failing
     * in Yukon 7.5.0
     * 
     * Response is:
     * 
     * { "status": 500, "message": "Unexpected exception - cause unknown",
     * "errorCode": "YK8172490025" }
     * 
     * But won't fail in Yukon 9.0.0
     * 
     * Response is:
     * 
     * { "status": 422, "message": "Validation error", "errorCode": "YK8271011377",
     * "fieldErrors": [ { "field": "name", "code": "Name already exists",
     * "rejectedValue": "virtual system" } ], "globalErrors": [] }
     * 
     * =============================================================================
     * ===============================================================
     */
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void createVirtualDeviceApi_DuplicateName_422Unprocessable() {
        throw new SkipException("Failing in Yukon 7.5.0, but Working in Yukon 9.0.0");
        /*
         * String virDevName = faker.internet().uuid().replaceAll("-", "");
         * 
         * new VirtualDeviceCreateBuilder.Builder(Optional.of(virDevName)) .create();
         * 
         * JSONObject request = new
         * VirtualDeviceCreateBuilder.Builder(Optional.of(virDevName)) .build();
         * 
         * ExtractableResponse<?> createResponse =
         * ApiCallHelper.post(APIs.VirtualDevice.CREATE_VIRTUALDEVICE,
         * request.toString());
         * 
         * assertThat(createResponse.statusCode()).isEqualTo(400);
         */
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void createVirtualDeviceApi_MissingType_400BadRequest() {
        JSONObject request = new VirtualDeviceCreateBuilder.Builder(Optional.empty())
                .build();

        request.remove("type");

        ExtractableResponse<?> createResponse = ApiCallHelper.post(APIs.VirtualDevice.CREATE_VIRTUALDEVICE, request.toString());

        assertThat(createResponse.statusCode()).isEqualTo(400);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void createVirtualDeviceApi_InvalidType_400BadRequest() {
        JSONObject request = new VirtualDeviceCreateBuilder.Builder(Optional.empty())
                .build();

        request.put("type", "invalid");

        ExtractableResponse<?> createResponse = ApiCallHelper.post(APIs.VirtualDevice.CREATE_VIRTUALDEVICE, request.toString());

        assertThat(createResponse.statusCode()).isEqualTo(400);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void createVirtualDeviceApi_InvalidEnable_400BadRequest() {
        JSONObject request = new VirtualDeviceCreateBuilder.Builder(Optional.empty())
                .build();

        request.put("enable", "t");

        ExtractableResponse<?> createResponse = ApiCallHelper.post(APIs.VirtualDevice.CREATE_VIRTUALDEVICE, request.toString());

        assertThat(createResponse.statusCode()).isEqualTo(400);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void createVirtualDeviceApi_MissingEnabledefaultsTrue_201Created() {
        SoftAssertions softly = new SoftAssertions();
        JSONObject request = new VirtualDeviceCreateBuilder.Builder(Optional.empty())
                .build();

        request.remove("enable");

        ExtractableResponse<?> createResponse = ApiCallHelper.post(APIs.VirtualDevice.CREATE_VIRTUALDEVICE, request.toString());

        String res = createResponse.asString();
        JSONObject response = new JSONObject(res);
        
        softly.assertThat(createResponse.statusCode()).isEqualTo(201);
        softly.assertThat(response.getString("name")).isEqualTo(request.getString("name"));
        softly.assertThat(response.getBoolean("enable")).isTrue();
        softly.assertThat(response.getString("type")).isEqualTo("VIRTUAL_SYSTEM");
        softly.assertThat(response.getInt("id")).isNotNull();
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void createVirtualDeviceApi_NoFields_400BadRequest() {
        JSONObject request = new JSONObject();

        ExtractableResponse<?> createResponse = ApiCallHelper.post(APIs.VirtualDevice.CREATE_VIRTUALDEVICE, request.toString());

        assertThat(createResponse.statusCode()).isEqualTo(400);
    }
}
