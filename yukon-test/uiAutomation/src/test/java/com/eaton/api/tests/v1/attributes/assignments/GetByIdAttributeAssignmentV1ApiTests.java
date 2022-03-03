package com.eaton.api.tests.v1.attributes.assignments;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.Test;

import com.eaton.builders.admin.attributes.AttributeService;
import com.eaton.framework.APIs;
import com.eaton.framework.TestConstants;
import com.eaton.rest.api.common.ApiCallHelper;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class GetByIdAttributeAssignmentV1ApiTests {
    private Faker faker = new Faker();
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void getByIdAttributeAsgmtApi_200Success() {
        SoftAssertions softly = new SoftAssertions();
        Map<String, Pair<JSONObject, JSONObject>> map = AttributeService.createAttributeWithAssignment(Optional.empty());
        
        Pair<JSONObject, JSONObject> pair = map.get("AttributeAsgmt");
        
        JSONObject asgmtResponse = pair.getValue1();
        Integer attrId = asgmtResponse.getInt("attributeId");
        Integer attrAsgmtId = asgmtResponse.getInt("attributeAssignmentId");
        String paoType = asgmtResponse.getString("paoType");
        String pointType = asgmtResponse.getString("pointType");
        Integer offset = asgmtResponse.getInt("offset");
        JSONObject attr = new JSONObject(asgmtResponse.get("customAttribute").toString());
        Integer customAttrId = attr.getInt("customAttributeId");
        String name = attr.getString("name");

        ExtractableResponse<?> response = ApiCallHelper.get(APIs.AttributeAssignment.GET_ATTRIBUTE_ASGMTS + attrAsgmtId);
        String res = response.asString();
        JSONObject json = new JSONObject(res);
        JSONObject attrJson = new JSONObject(json.get("customAttribute").toString());
        Integer custAttrId = attrJson.getInt("customAttributeId");
        String attrName = attrJson.getString("name");
        
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertThat(json).isNotNull();
        softly.assertThat(json.getInt("attributeId")).isEqualTo(attrId);
        softly.assertThat(json.getInt("attributeAssignmentId")).isEqualTo(attrAsgmtId);
        softly.assertThat(json.getString("paoType")).isEqualTo(paoType);
        softly.assertThat(json.getString("pointType")).isEqualTo(pointType);
        softly.assertThat(json.getInt("offset")).isEqualTo(offset);
        softly.assertThat(custAttrId).isEqualTo(customAttrId);
        softly.assertThat(attrName).isEqualTo(name);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void getByIdAttributeAsgmtApi_NotFoundId_400BadRequest() {
        //throw new SkipException("Defect: YUK-23189");
        String InvalidId = faker.number().digits(9);
        
        ExtractableResponse<?> response = ApiCallHelper.get(APIs.AttributeAssignment.GET_ATTRIBUTE_ASGMTS + InvalidId);
        
        assertThat(response.statusCode()).isEqualTo(400);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void getByIdAttributeAsgmtApi_InvalidId_400BadRequest() {
        String InvalidId = faker.number().digits(12);
        
        ExtractableResponse<?> response = ApiCallHelper.get(APIs.AttributeAssignment.GET_ATTRIBUTE_ASGMTS + InvalidId);
        
        assertThat(response.statusCode()).isEqualTo(400);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void getByIdAttributeAsgmtApi_EmptyId_404NotFound() {
        ExtractableResponse<?> response = ApiCallHelper.get(APIs.AttributeAssignment.GET_ATTRIBUTE_ASGMTS + "");
        
        assertThat(response.statusCode()).isEqualTo(404);
    }
}
