package com.eaton.api.tests.v1.attributes;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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

public class GetByIdAttributeV1ApiTests {
    private Faker faker = new Faker();
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void getByIdAttributeApi_200Success() {
        SoftAssertions softly = new SoftAssertions();
        Pair<JSONObject, JSONObject> pair = new AttributesCreateBuilder.Builder(Optional.empty())
                .create();
        
        JSONObject createResponse = pair.getValue1();
        String name = createResponse.getString("name");
        Integer id = createResponse.getInt("customAttributeId");

        ExtractableResponse<?> response = ApiCallHelper.get(APIs.Attributes.GET_ATTRIBUTE + id);
        String res = response.asString();
        JSONObject json = new JSONObject(res);
        
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertThat(json).isNotNull();
        softly.assertThat(json.getString("name")).isEqualTo(name);
        softly.assertThat(json.getInt("customAttributeId")).isEqualTo(id);
        softly.assertAll();
    }
    
    //SKIP due to defect
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void getByIdAttributeApi_NotFoundId_404NotFound() {
        String InvalidId = faker.number().digits(9);
        
        ExtractableResponse<?> response = ApiCallHelper.get(APIs.Attributes.GET_ATTRIBUTE + InvalidId);
        
        assertThat(response.statusCode()).isEqualTo(404);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void getByIdAttributeApi_InvalidId_400BadRequest() {
        String InvalidId = faker.number().digits(12);
        
        ExtractableResponse<?> response = ApiCallHelper.get(APIs.Attributes.GET_ATTRIBUTE + InvalidId);
        
        assertThat(response.statusCode()).isEqualTo(400);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void getByIdAttributeApi_EmptyId_404NotFound() {
        ExtractableResponse<?> response = ApiCallHelper.get(APIs.Attributes.GET_ATTRIBUTE + "");
        
        assertThat(response.statusCode()).isEqualTo(404);
    }
}
