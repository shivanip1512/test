package com.eaton.api.tests.v1.attributes;

import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;

import com.eaton.builders.admin.attributes.AttributesCreateBuilder;
import com.eaton.framework.APIs;
import com.eaton.framework.TestConstants;
import com.eaton.rest.api.common.ApiCallHelper;

import io.restassured.response.ExtractableResponse;

public class GetAllAttributesV1ApiTests {

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void getAllAttributeApi_200Success() {
        SoftAssertions softly = new SoftAssertions();
        Pair<JSONObject, JSONObject> pair = new AttributesCreateBuilder.Builder(Optional.empty())
                .create();
        
        JSONObject createResponse = pair.getValue1();
        String name = createResponse.getString("name");
        Integer id = createResponse.getInt("customAttributeId");

        ExtractableResponse<?> response = ApiCallHelper.get(APIs.Attributes.GET_ATTRIBUTES);

        String body = response.asString();
        JSONArray attrAsgmt = new JSONArray(body);
        String attributeAsgmt = null;
        
        for(int i = 0; i < attrAsgmt.length(); i++) {
            if(attrAsgmt.get(i).toString().contains("\"customAttributeId\":" + id)) {
                attributeAsgmt = attrAsgmt.get(i).toString();
            }
        }

        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertThat(attributeAsgmt).contains("\"customAttributeId\":" + id);
        softly.assertThat(attributeAsgmt).contains("\"name\":\"" + name);
        softly.assertAll();
    }
}
