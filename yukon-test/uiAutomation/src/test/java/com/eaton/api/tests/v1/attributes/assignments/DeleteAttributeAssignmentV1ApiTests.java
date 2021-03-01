package com.eaton.api.tests.v1.attributes.assignments;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.Test;

import com.eaton.builders.admin.attributes.AttributeService;
import com.eaton.framework.APIs;
import com.eaton.framework.TestConstants;
import com.eaton.rest.api.common.ApiCallHelper;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class DeleteAttributeAssignmentV1ApiTests {
private Faker faker = new Faker();
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void deleteAttributeApi_200Success() {
        Map<String, Pair<JSONObject, JSONObject>> map = AttributeService.createAttributeWithAssignment(Optional.empty());
        
        Pair<JSONObject, JSONObject> attrAsgmtPair = map.get("AttributeAsgmt");
        
        JSONObject createresponse = attrAsgmtPair.getValue1();
        Integer id = createresponse.getInt("attributeAssignmentId");

        ExtractableResponse<?> response = ApiCallHelper.delete(APIs.AttributeAssignment.DELETE_ATTRIBUTE_ASGMT + id);

        assertThat(response.statusCode()).isEqualTo(200);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void deleteAttributeApi_NotFoundId_400BadRequest() {
        String invalidId = faker.number().digits(9);
        
        ExtractableResponse<?> response = ApiCallHelper.delete(APIs.AttributeAssignment.DELETE_ATTRIBUTE_ASGMT + invalidId);
        
        assertThat(response.statusCode()).isEqualTo(400);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void deleteAttributeApi_InvalidAttrId_400BadRequest() {
        String invalidId = faker.number().digits(12);
        
        ExtractableResponse<?> response = ApiCallHelper.delete(APIs.AttributeAssignment.DELETE_ATTRIBUTE_ASGMT + invalidId);
        
        assertThat(response.statusCode()).isEqualTo(400);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void deleteAttributeApi_EmptyId_404NotFound() {
        ExtractableResponse<?> response = ApiCallHelper.delete(APIs.AttributeAssignment.DELETE_ATTRIBUTE_ASGMT + "");
        
        assertThat(response.statusCode()).isEqualTo(404);
    }
}
