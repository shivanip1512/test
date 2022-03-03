package com.eaton.api.tests.v1.virtualdevices;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.Test;

import com.eaton.builders.assets.virtualdevices.VirtualDeviceCreateBuilder;
import com.eaton.framework.APIs;
import com.eaton.framework.TestConstants;
import com.eaton.rest.api.common.ApiCallHelper;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class GetByIdVirtualDeviceV1ApiTests {
    private Faker faker = new Faker();

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void getByIdVirtualDeviceApi_200Success() {
        SoftAssertions softly = new SoftAssertions();
        Pair<JSONObject, JSONObject> pair = new VirtualDeviceCreateBuilder.Builder(Optional.empty())
                .create();
        JSONObject createResponse = pair.getValue1();
        String name = createResponse.getString("name");
        Integer id = createResponse.getInt("id");
        Boolean enable = createResponse.getBoolean("enable");

        ExtractableResponse<?> response = ApiCallHelper.get(APIs.VirtualDevice.GET_VIRTUALDEVICE + id);
        String res = response.asString();
        JSONObject json = new JSONObject(res);

        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertThat(json).isNotNull();
        softly.assertThat(json.getString("name")).isEqualTo(name);
        softly.assertThat(json.getInt("id")).isEqualTo(id);
        softly.assertThat(json.getBoolean("enable")).isEqualTo(enable);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void getByIdVirtualDeviceApi_NotFoundId_400BadRequest() {
        String nonExistingId = faker.number().digits(9);

        ExtractableResponse<?> response = ApiCallHelper.get(APIs.VirtualDevice.GET_VIRTUALDEVICE + nonExistingId);
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void getByIdVirtualDeviceApi_InvalidId_400BadRequest() {
        String invalidId = faker.number().digits(12);

        ExtractableResponse<?> response = ApiCallHelper.get(APIs.VirtualDevice.GET_VIRTUALDEVICE + invalidId);

        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ASSETS, TestConstants.Features.VIRTUAL_DEVICES })
    public void getByIdVirtualDeviceApi_EmptyId_404NotFound() {
        ExtractableResponse<?> response = ApiCallHelper.get(APIs.VirtualDevice.GET_VIRTUALDEVICE + "");

        assertThat(response.statusCode()).isEqualTo(404);
    }
}
