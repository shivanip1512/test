package com.eaton.api.tests.v1.attributes;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.Test;

import com.eaton.builders.admin.attributes.AttributesCreateBuilder;
import com.eaton.framework.APIs;
import com.eaton.framework.TestConstants;
import com.eaton.rest.api.admin.attributes.AttributesRequest;
import com.eaton.rest.api.common.ApiCallHelper;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class DeleteAttributeV1ApiTests {
    
    private Faker faker = new Faker();
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void deleteAttributeApi_200Success() {
        Pair<JSONObject, JSONObject> pair = new AttributesCreateBuilder.Builder(Optional.empty())
                .create();
        
        JSONObject createresponse = pair.getValue1();
        Integer id = createresponse.getInt("customAttributeId");

        ExtractableResponse<?> response = AttributesRequest.deleteAttribute(id.toString());

        assertThat(response.statusCode()).isEqualTo(200);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void deleteAttributeApi_NotFoundId_400BadRequest() {
        String invalidId = faker.number().digits(9);
        
        ExtractableResponse<?> response = ApiCallHelper.delete(APIs.Attributes.DELETE_ATTRIBUTE + invalidId);
        
        assertThat(response.statusCode()).isEqualTo(400);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void deleteAttributeApi_InvalidAttrId_400BadRequest() {
        String invalidId = faker.number().digits(12);
        
        ExtractableResponse<?> response = ApiCallHelper.delete(APIs.Attributes.DELETE_ATTRIBUTE + invalidId);
        
        assertThat(response.statusCode()).isEqualTo(400);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void deleteAttributeApi_EmptyId_404NotFound() {
        ExtractableResponse<?> response = ApiCallHelper.delete(APIs.Attributes.DELETE_ATTRIBUTE + "");
        
        assertThat(response.statusCode()).isEqualTo(404);
    }
}
