package com.eaton.api.tests.v1.attributes.assignments;

import java.util.Map;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;

import com.eaton.builders.admin.attributes.AttributeService;
import com.eaton.framework.APIs;
import com.eaton.framework.TestConstants;
import com.eaton.rest.api.common.ApiCallHelper;

import io.restassured.response.ExtractableResponse;

public class GetAllAttributeAssignmentsV1ApiTests {
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void getAllAttrAsgmtApi_200Success() {
        SoftAssertions softly = new SoftAssertions();
        Map<String, Pair<JSONObject, JSONObject>> map = AttributeService.createAttributeWithAssignment(Optional.empty());
        
        Pair<JSONObject, JSONObject> asgmtPair = map.get("AttributeAsgmt");
        
        JSONObject asgmtResponse = asgmtPair.getValue1();
        
        Integer attrId = asgmtResponse.getInt("attributeId");
        Integer attrAsgmtId = asgmtResponse.getInt("attributeAssignmentId");
        String paoType = asgmtResponse.getString("paoType");
        String pointType = asgmtResponse.getString("pointType");
        Integer offset = asgmtResponse.getInt("offset");
        JSONObject attr = new JSONObject(asgmtResponse.get("customAttribute").toString());
        Integer customAttrId = attr.getInt("customAttributeId");
        String name = attr.getString("name");

        ExtractableResponse<?> response = ApiCallHelper.get(APIs.AttributeAssignment.GET_ATTRIBUTE_ASGMT);

        String body = response.asString();
        JSONArray attrAsgmt = new JSONArray(body);
        String attributeAsgmt = null;
        
        for(int i = 0; i < attrAsgmt.length(); i++) {
            if(attrAsgmt.get(i).toString().contains("\"attributeId\":" + attrId)) {
                attributeAsgmt = attrAsgmt.get(i).toString();
            }
        }

        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertThat(attributeAsgmt).isNotNull();
        softly.assertThat(attributeAsgmt).contains("\"attributeId\":" + attrId);
        softly.assertThat(attributeAsgmt).contains("\"attributeAssignmentId\":" + attrAsgmtId.toString());
        softly.assertThat(attributeAsgmt).contains("\"paoType\":\"" + paoType);
        softly.assertThat(attributeAsgmt).contains("\"pointType\":\"" + pointType);
        softly.assertThat(attributeAsgmt).contains("\"offset\":" + offset.toString());
        softly.assertThat(attributeAsgmt).contains("\"customAttribute\":");
        softly.assertThat(attributeAsgmt).contains("\"customAttributeId\":" + customAttrId.toString());
        softly.assertThat(attributeAsgmt).contains("\"name\":\"" + name);
        softly.assertAll();
    }
}
