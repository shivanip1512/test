package com.eaton.api.tests.v1.attributes;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.Test;

import com.eaton.builders.admin.attributes.AttributesCreateBuilder;
import com.eaton.framework.APIs;
import com.eaton.framework.TestConstants;
import com.eaton.rest.api.common.ApiCallHelper;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class CreateAttributeV1ApiTests {
    private Faker faker = new Faker();

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void createAttributeApi_AllFields_201Created() {
        SoftAssertions softly = new SoftAssertions();
        Pair<JSONObject, JSONObject> pair = new AttributesCreateBuilder.Builder(Optional.empty())
                .create();

        JSONObject request = pair.getValue0();
        JSONObject response = pair.getValue1();

        softly.assertThat(response.getString("name")).isEqualTo(request.getString("name"));
        softly.assertThat(response.getInt("customAttributeId")).isNotNull();
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void createAttributeApi_NoName_422Unprocessable() {
        JSONObject request = new AttributesCreateBuilder.Builder(Optional.of(""))
                .build();

        ExtractableResponse<?> createResponse = ApiCallHelper.post(APIs.Attributes.CREATE_ATTRIBUTE, request.toString());

        assertThat(createResponse.statusCode()).isEqualTo(422);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void createAttributeApi_InvalidName_422Unprocessable() {
        JSONObject request = new AttributesCreateBuilder.Builder(Optional.of("Create Attr / \\\\ , ' \\\" |"))
                .build();

        ExtractableResponse<?> createResponse = ApiCallHelper.post(APIs.Attributes.CREATE_ATTRIBUTE, request.toString());

        assertThat(createResponse.statusCode()).isEqualTo(422);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void createAttributeApi_Name61Char_422Unprocessable() {
        JSONObject request = new AttributesCreateBuilder.Builder(Optional.of(faker.lorem().characters(61)))
                .build();

        ExtractableResponse<?> createResponse = ApiCallHelper.post(APIs.Attributes.CREATE_ATTRIBUTE, request.toString());

        assertThat(createResponse.statusCode()).isEqualTo(422);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void createAttributeApi_MissingName_422Unprocessable() {
        JSONObject request = new AttributesCreateBuilder.Builder(Optional.empty())
                .build();
        
        request.remove("name");

        ExtractableResponse<?> createResponse = ApiCallHelper.post(APIs.Attributes.CREATE_ATTRIBUTE, request.toString());

        assertThat(createResponse.statusCode()).isEqualTo(422);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void createAttributeApi_DuplicateName_400BadRequest() {
        String attrName = faker.internet().uuid().replaceAll("-", "");

        new AttributesCreateBuilder.Builder(Optional.of(attrName))
                .create();

        JSONObject request = new AttributesCreateBuilder.Builder(Optional.of(attrName))
                .build();

        ExtractableResponse<?> createResponse = ApiCallHelper.post(APIs.Attributes.CREATE_ATTRIBUTE, request.toString());

        assertThat(createResponse.statusCode()).isEqualTo(400);
    }
}
