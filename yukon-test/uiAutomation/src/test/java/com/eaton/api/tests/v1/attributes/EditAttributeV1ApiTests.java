package com.eaton.api.tests.v1.attributes;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.admin.attributes.AttributeEditBuilder;
import com.eaton.builders.admin.attributes.AttributesCreateBuilder;
import com.eaton.framework.APIs;
import com.eaton.framework.TestConstants;
import com.eaton.rest.api.admin.attributes.AttributesRequest;
import com.eaton.rest.api.common.ApiCallHelper;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class EditAttributeV1ApiTests {
    private Faker faker = new Faker();
    private String attrId;
    private String attrName;
    
    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        Pair<JSONObject, JSONObject> pair = new AttributesCreateBuilder.Builder(Optional.empty())
                .create();

        JSONObject createdResponse = pair.getValue1();
        Integer id = createdResponse.getInt("customAttributeId");    
        attrId = id.toString();
        attrName = createdResponse.getString("name");
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttributeApi_AllFields_200Success() {
        Pair<JSONObject, JSONObject> pair = new AttributesCreateBuilder.Builder(Optional.empty())
                .create();

        JSONObject createdResponse = pair.getValue1();
        Integer id = createdResponse.getInt("customAttributeId");
        
        JSONObject request = new AttributeEditBuilder.Builder(Optional.empty())
                .build();
        
        ExtractableResponse<?> response = AttributesRequest.updateAttribute(id.toString(), request.toString());

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttributeApi_NoName_422Unprocessable() {
        JSONObject request = new AttributeEditBuilder.Builder(Optional.of(""))
                .build();

        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.Attributes.UPDATE_ATTRIBUTE + attrId, request.toString());

        assertThat(response.statusCode()).isEqualTo(422);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttributeApi_InvalidName_422Unprocessable() {
        JSONObject request = new AttributesCreateBuilder.Builder(Optional.of("Create Attr / \\\\ , ' \\\" |"))
                .build();

        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.Attributes.UPDATE_ATTRIBUTE + attrId, request.toString());

        assertThat(response.statusCode()).isEqualTo(422);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttributeApi_Name61Char_422Unprocessable() {
        JSONObject request = new AttributesCreateBuilder.Builder(Optional.of(faker.lorem().characters(61)))
                .build();

        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.Attributes.UPDATE_ATTRIBUTE + attrId, request.toString());

        assertThat(response.statusCode()).isEqualTo(422);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttributeApi_MissingName_422Unprocessable() {
        JSONObject jo = new JSONObject();

        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.Attributes.UPDATE_ATTRIBUTE + attrId, jo.toString());

        assertThat(response.statusCode()).isEqualTo(422);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttributeApi_DuplicateName_400BadRequest() {
        String name = faker.internet().uuid().replaceAll("-", "");
        
        new AttributesCreateBuilder.Builder(Optional.of(name))
                .create();

        JSONObject request = new AttributesCreateBuilder.Builder(Optional.of(name))
                .build();
        
        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.Attributes.UPDATE_ATTRIBUTE + attrId, request.toString());

        assertThat(response.statusCode()).isEqualTo(400);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttributeApi_NotFoundId_404NotFound() {
        throw new SkipException("Inquiry: YUK-23190");
//        String invalidId = faker.number().digits(9);
//        
//        JSONObject request = new AttributesCreateBuilder.Builder(Optional.of(attrName))
//                .build();
//        
//        ExtractableResponse<?> response = ApiCallHelper.patch(invalidId, request.toString());
//        
//        assertThat(response.statusCode()).isEqualTo(404);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttributeApi_InvalidAttrId_400BadRequest() {
        throw new SkipException("Inquiry: YUK-23190");
//        String invalidId = faker.number().digits(12);
//        
//        JSONObject request = new AttributesCreateBuilder.Builder(Optional.of(attrName))
//                .build();
//        
//        ExtractableResponse<?> response = ApiCallHelper.patch(invalidId, request.toString());
//        
//        assertThat(response.statusCode()).isEqualTo(400);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttributeApi_EmptyId_404NotFound() {
        throw new SkipException("Inquiry: YUK-23190");
//        JSONObject request = new AttributesCreateBuilder.Builder(Optional.of(attrName))
//                .build();
//        
//        ExtractableResponse<?> response = ApiCallHelper.patch("", request.toString());
//        
//        assertThat(response.statusCode()).isEqualTo(404);
    }
}