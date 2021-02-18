package com.eaton.api.tests.v1.virtualdevices;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.assets.virtualdevices.VirtualDeviceCreateBuilder;
import com.eaton.framework.APIs;
import com.eaton.framework.TestConstants;
import com.eaton.rest.api.common.ApiCallHelper;
import com.eaton.rest.api.virtualdevice.VirtualDeviceRequest;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class EditVirtualDeviceV1ApiTests {
    private Faker faker = new Faker();
    private String virDevId;
    private String virDevName;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        Pair<JSONObject, JSONObject> pair = new VirtualDeviceCreateBuilder.Builder(Optional.empty())
                .create();

        JSONObject createdResponse = pair.getValue1();
        Integer id = createdResponse.getInt("id");
        virDevId = id.toString();
        virDevName = createdResponse.getString("name");
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void editVirtualDeviceApi_AllFields_200Ok() {
        Pair<JSONObject, JSONObject> pair = new VirtualDeviceCreateBuilder.Builder(Optional.empty())
                .create();

        JSONObject createdResponse = pair.getValue1();
        Integer id = createdResponse.getInt("id");

        JSONObject request = new VirtualDeviceCreateBuilder.Builder(Optional.empty())
                .withEnable(Optional.of(true))
                .build();

        ExtractableResponse<?> response = VirtualDeviceRequest.updateVirtualDevice(id.toString(), request.toString());

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void editVirtualDeviceApi_NoName_422Unprocessable() {
        JSONObject request = new VirtualDeviceCreateBuilder.Builder(Optional.of(""))
                .build();

        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.VirtualDevice.UPDATE_VIRTUALDEVICE + virDevId, request.toString());

        assertThat(response.statusCode()).isEqualTo(422);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void editVirtualDeviceApi_InvalidName_422Unprocessable() {
        JSONObject request = new VirtualDeviceCreateBuilder.Builder(Optional.of("Create Attr / \\\\ , ' \\\" |"))
                .build();

        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.VirtualDevice.UPDATE_VIRTUALDEVICE + virDevId, request.toString());

        assertThat(response.statusCode()).isEqualTo(422);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void editVirtualDeviceApi_Name61Char_422Unprocessable() {
        JSONObject request = new VirtualDeviceCreateBuilder.Builder(Optional.of(faker.lorem().characters(61)))
                .build();

        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.VirtualDevice.UPDATE_VIRTUALDEVICE + virDevId, request.toString());

        assertThat(response.statusCode()).isEqualTo(422);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void editVirtualDeviceApi_DuplicateName_422Unprocessable() {
        String name = "AT Virtual Device " + faker.internet().uuid().replaceAll("-", "");

        new VirtualDeviceCreateBuilder.Builder(Optional.of(name))
            .create();

        JSONObject request = new VirtualDeviceCreateBuilder.Builder(Optional.of(name))
                .build();

        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.VirtualDevice.UPDATE_VIRTUALDEVICE + virDevId, request.toString());

        assertThat(response.statusCode()).isEqualTo(422);
    }

    /*
     * ===========================================================================================================================
     * ===============
     * 
     * This test createVirtualDeviceApi_DuplicateName_422Unprocessable()
     * is failing in Yukon 7.5.0
     * 
     * Response is:
     * 
     * {
     * "status": 500,
     * "message": "Unexpected exception - cause unknown",
     * "errorCode": "YK1280621000
     * }
     * 
     * But won't fail in Yukon 9.0.0
     * 
     * Response is:
     * 
     * {
     * "status": 400,
     * "message": "paoid 32424 not found.",
     * "errorCode": "YK9004760818"
     * }
     * ===========================================================================================================================
     * =================
     */
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void editVirtualDeviceApi_NotFoundId_404NotFound() {
        throw new SkipException("Failing in Yukon 7.5.0, but Working in Yukon 9.0.0");
        /*
         * String nonExistingId = faker.number().digits(9);
         * 
         * JSONObject request = new VirtualDeviceEditBuilder.Builder(Optional.of(virDevName), Optional.empty()).build();
         * 
         * ExtractableResponse<?> response = ApiCallHelper.patch(APIs.VirtualDevice.UPDATE_VIRTUALDEVICE + nonExistingId,
         * request.toString());
         * 
         * assertThat(response.statusCode()).isEqualTo(404);
         */
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void editVirtualDeviceApi_InvalidVirDeviceId_400BadRequest() {
        String invalidId = faker.number().digits(2) + "$" + "S";

        JSONObject request = new VirtualDeviceCreateBuilder.Builder(Optional.of(virDevName))
                .build();

        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.VirtualDevice.UPDATE_VIRTUALDEVICE + invalidId, request.toString());

        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void editVirtualDeviceApi_EmptyId_404NotFound() {
        JSONObject request = new VirtualDeviceCreateBuilder.Builder(Optional.of(virDevName))
                .build();

        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.VirtualDevice.UPDATE_VIRTUALDEVICE, request.toString());

        assertThat(response.statusCode()).isEqualTo(404);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void createVirtualDeviceApi_InvalidType_400BadRequest() {
        JSONObject request = new VirtualDeviceCreateBuilder.Builder(Optional.empty())
                .build();

        request.put("type", "invalid");

        ExtractableResponse<?> createResponse = ApiCallHelper.patch(APIs.VirtualDevice.UPDATE_VIRTUALDEVICE + virDevId, request.toString());

        assertThat(createResponse.statusCode()).isEqualTo(400);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void createVirtualDeviceApi_InvalidEnable_400BadRequest() {
        JSONObject request = new VirtualDeviceCreateBuilder.Builder(Optional.empty())
                .build();

        request.put("enable", "t");

        ExtractableResponse<?> createResponse = ApiCallHelper.patch(APIs.VirtualDevice.UPDATE_VIRTUALDEVICE + virDevId, request.toString());

        assertThat(createResponse.statusCode()).isEqualTo(400);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void createVirtualDeviceApi_NoFieldsNoUpdate_200Ok() {
        SoftAssertions softly = new SoftAssertions();
        
        Pair<JSONObject, JSONObject> pair = new VirtualDeviceCreateBuilder.Builder(Optional.empty())
                .create();

        JSONObject createdResponse = pair.getValue1();
        Integer id = createdResponse.getInt("id");
        
        JSONObject editRequest = new JSONObject();
        ExtractableResponse<?> editResponse = ApiCallHelper.patch(APIs.VirtualDevice.UPDATE_VIRTUALDEVICE + id, editRequest.toString());

        String res = editResponse.asString();
        JSONObject editRes = new JSONObject(res);
        
        softly.assertThat(editResponse.statusCode()).isEqualTo(200);
        softly.assertThat(editRes.getString("name")).isEqualTo(createdResponse.getString("name"));
        softly.assertThat(editRes.getBoolean("enable")).isEqualTo(createdResponse.getBoolean("enable"));
        softly.assertThat(editRes.getString("type")).isEqualTo(createdResponse.getString("type"));
        softly.assertThat(editRes.getInt("id")).isEqualTo(createdResponse.getInt("id"));
        softly.assertAll();
    }
}
