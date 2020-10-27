package com.eaton.api.tests.v1.attributes;

import java.util.ArrayList;
import java.util.List;
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

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void getAllAttributeApi_200Success() {
        SoftAssertions softly = new SoftAssertions();
        Pair<JSONObject, JSONObject> pair = new AttributesCreateBuilder.Builder(Optional.empty())
                .create();
        
        JSONObject createResponse = pair.getValue1();
        String name = createResponse.getString("name");
        Integer id = createResponse.getInt("customAttributeId");

        ExtractableResponse<?> response = ApiCallHelper.get(APIs.Attributes.GET_ATTRIBUTES);

        String body = response.asString();
        JSONArray arr = new JSONArray(body);
        List<String> nameList = new ArrayList<>();
        List<Integer> idList = new ArrayList<>();
        
        for (int i = 0; i < arr.length(); i++) {            
            String attrName = arr.getJSONObject(i).getString("name");
            Integer attrId = arr.getJSONObject(i).getInt("customAttributeId"); 
            nameList.add(attrName);   
            idList.add(attrId);
        }       

        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertThat(arr).isNotNull();
        softly.assertThat(arr.length()).isGreaterThan(0);
        softly.assertThat(nameList).contains(name);
        softly.assertThat(idList).contains(id);
        softly.assertAll();
    }
}
