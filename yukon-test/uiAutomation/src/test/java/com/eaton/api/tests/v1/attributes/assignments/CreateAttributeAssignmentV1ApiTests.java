package com.eaton.api.tests.v1.attributes.assignments;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.admin.attributes.AttributeAsgmtCreateBuilder;
import com.eaton.builders.admin.attributes.AttributeAsgmtTypes;
import com.eaton.builders.admin.attributes.AttributesCreateBuilder;
import com.eaton.framework.APIs;
import com.eaton.framework.TestConstants;
import com.eaton.rest.api.common.ApiCallHelper;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class CreateAttributeAssignmentV1ApiTests {

    private Faker faker = new Faker();
    private String attrId;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        Pair<JSONObject, JSONObject> pair = new AttributesCreateBuilder.Builder(Optional.empty())
                .create();

        JSONObject createdResponse = pair.getValue1();
        Integer id = createdResponse.getInt("customAttributeId");
        attrId = id.toString();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void createAttrAsgmtApi_AllFields_201Created() {
        SoftAssertions softly = new SoftAssertions();
        Pair<JSONObject, JSONObject> pair = new AttributesCreateBuilder.Builder(Optional.empty())
                .create();

        JSONObject response = pair.getValue1();
        Integer attributeId = response.getInt("customAttributeId");
        String attributeName = response.getString("name");

        Pair<JSONObject, JSONObject> asgmtPair = new AttributeAsgmtCreateBuilder.Builder(attributeId)
                .withMultiplier(Optional.empty())
                .withPaoType(Optional.empty())
                .withPointType(Optional.empty())
                .create();

        JSONObject asgmtResponse = asgmtPair.getValue1();
        JSONObject asgmtRequest = asgmtPair.getValue0();
        JSONObject customAttr = asgmtResponse.getJSONObject("customAttribute");

        softly.assertThat(asgmtResponse.getInt("attributeId")).isEqualTo(asgmtRequest.getInt("attributeId"));
        softly.assertThat(asgmtResponse.getInt("attributeAssignmentId")).isNotNull();
        softly.assertThat(asgmtResponse.getString("paoType")).isEqualTo(asgmtRequest.getString("paoType"));
        softly.assertThat(asgmtResponse.getInt("offset")).isEqualTo(asgmtRequest.getInt("offset"));
        softly.assertThat(asgmtResponse.getString("pointType")).isEqualTo(asgmtRequest.getString("pointType"));
        softly.assertThat(customAttr).isNotNull();
        softly.assertThat(customAttr.getInt("customAttributeId")).isEqualTo(attributeId);
        softly.assertThat(customAttr.getString("name")).isEqualTo(attributeName);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void createAttrAsgmtApi_DuplicateAttrAndDeviceType_400BadRequest() {
        Pair<JSONObject, JSONObject> pair = new AttributesCreateBuilder.Builder(Optional.empty())
                .create();

        JSONObject response = pair.getValue1();
        Integer attributeId = response.getInt("customAttributeId");

        JSONObject asgmtJson = new AttributeAsgmtCreateBuilder.Builder(attributeId)
                .withMultiplier(Optional.empty())
                .withPaoType(Optional.empty())
                .withPointType(Optional.empty())
                .build();

        ApiCallHelper.post(APIs.AttributeAssignment.CREATE_ATTRIBUTE_ASGMT, asgmtJson.toString());
        
        ExtractableResponse<?> asgmtResponse = ApiCallHelper.post(APIs.AttributeAssignment.CREATE_ATTRIBUTE_ASGMT, asgmtJson.toString());

        assertThat(asgmtResponse.statusCode()).isEqualTo(400);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void createAttrAsgmtApi_NotFoundAttrId_400BadRequest() {
        String invalidId = faker.number().digits(9);

        JSONObject asgmtJson = new AttributeAsgmtCreateBuilder.Builder(Integer.parseInt(invalidId))
                .withMultiplier(Optional.empty())
                .withPaoType(Optional.empty())
                .withPointType(Optional.empty())
                .build();

        ExtractableResponse<?> response = ApiCallHelper.post(APIs.AttributeAssignment.CREATE_ATTRIBUTE_ASGMT, asgmtJson.toString());

        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void createAttrAsgmtApi_AttrIdMissing_422Unprocessable() {
        String invalidId = faker.number().digits(9);

        JSONObject asgmtJson = new AttributeAsgmtCreateBuilder.Builder(Integer.parseInt(invalidId))
                .withMultiplier(Optional.empty())
                .withPaoType(Optional.empty())
                .withPointType(Optional.empty())
                .build();

        asgmtJson.remove("attributeId");

        ExtractableResponse<?> response = ApiCallHelper.post(APIs.AttributeAssignment.CREATE_ATTRIBUTE_ASGMT, asgmtJson.toString());

        assertThat(response.statusCode()).isEqualTo(422);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void createAttrAsgmtApi_EmptyAttrId_422Unprocessable() {
        String invalidId = faker.number().digits(9);

        JSONObject asgmtJson = new AttributeAsgmtCreateBuilder.Builder(Integer.parseInt(invalidId))
                .withMultiplier(Optional.empty())
                .withPaoType(Optional.empty())
                .withPointType(Optional.empty())
                .build();

        asgmtJson.put("attributeId", "");

        ExtractableResponse<?> response = ApiCallHelper.post(APIs.AttributeAssignment.CREATE_ATTRIBUTE_ASGMT, asgmtJson.toString());

        assertThat(response.statusCode()).isEqualTo(422);
    }

    // TODO getting a 400 malformed json should there be a better error message?
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void createAttrAsgmtApi_InvalidAttrId_400BadRequest() {
        throw new SkipException("Create defect");
//        String invalidId = faker.number().digits(12);
//
//        JSONObject asgmtJson = new AttributeAsgmtCreateBuilder.Builder(1)
//                .withMultiplier(Optional.empty())
//                .withPaoType(Optional.empty())
//                .withPointType(Optional.empty())
//                .build();
//
//        asgmtJson.put("attributeId", invalidId);
//
//        ExtractableResponse<?> response = ApiCallHelper.post(APIs.AttributeAssignment.CREATE_ATTRIBUTE_ASGMT, asgmtJson.toString());
//
//        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void createAttrAsgmtApi_OffsetMissing_422Unprocessable() {
        JSONObject asgmtJson = new AttributeAsgmtCreateBuilder.Builder(Integer.parseInt(attrId))
                .withPaoType(Optional.empty())
                .withPointType(Optional.empty())
                .build();

        ExtractableResponse<?> response = ApiCallHelper.post(APIs.AttributeAssignment.CREATE_ATTRIBUTE_ASGMT, asgmtJson.toString());

        assertThat(response.statusCode()).isEqualTo(422);
    }

    // TODO in the UI offset can not be larger than 99,999,999
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void createAttrAsgmtApi_OffsetMaxLenth_422Unprocessable() {
        throw new SkipException("Create defect");
//        JSONObject asgmtJson = new AttributeAsgmtCreateBuilder.Builder(Integer.parseInt(attrId))
//                .withPaoType(Optional.empty())
//                .withMultiplier(Optional.of(100000000))
//                .withPointType(Optional.empty())
//                .build();
//        
//        ExtractableResponse<?> response = ApiCallHelper.post(APIs.AttributeAssignment.CREATE_ATTRIBUTE_ASGMT, asgmtJson.toString());
//
//        assertThat(response.statusCode()).isEqualTo(422);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void createAttrAsgmtApi_OffsetMinLenth_422Unprocessable() {
        JSONObject asgmtJson = new AttributeAsgmtCreateBuilder.Builder(Integer.parseInt(attrId))
                .withPaoType(Optional.empty())
                .withMultiplier(Optional.of(-1))
                .withPointType(Optional.empty())
                .build();

        ExtractableResponse<?> response = ApiCallHelper.post(APIs.AttributeAssignment.CREATE_ATTRIBUTE_ASGMT, asgmtJson.toString());

        assertThat(response.statusCode()).isEqualTo(422);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void createAttrAsgmtApi_OffsetCantBeZeroForCertainDeviceTypes_422Unprocessable() {
        JSONObject asgmtJson = new AttributeAsgmtCreateBuilder.Builder(Integer.parseInt(attrId))
                .withPaoType(Optional.empty())
                .withMultiplier(Optional.of(0))
                .withPointType(Optional.of(AttributeAsgmtTypes.PointTypes.STATUS))
                .build();

        ExtractableResponse<?> response = ApiCallHelper.post(APIs.AttributeAssignment.CREATE_ATTRIBUTE_ASGMT, asgmtJson.toString());

        assertThat(response.statusCode()).isEqualTo(422);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void createAttrAsgmtApi_DeviceTypeMissing_422Unprocessable() {
        JSONObject asgmtJson = new AttributeAsgmtCreateBuilder.Builder(Integer.parseInt(attrId))
                .withMultiplier(Optional.empty())
                .withPointType(Optional.empty())
                .build();

        ExtractableResponse<?> response = ApiCallHelper.post(APIs.AttributeAssignment.CREATE_ATTRIBUTE_ASGMT, asgmtJson.toString());

        assertThat(response.statusCode()).isEqualTo(422);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void createAttrAsgmtApi_InvalidDeviceType_400BadRequest() {
        JSONObject asgmtJson = new AttributeAsgmtCreateBuilder.Builder(Integer.parseInt(attrId))
                .withMultiplier(Optional.empty())
                .withPointType(Optional.empty())
                .build();
        
        asgmtJson.put("paoType", "invalid");

        ExtractableResponse<?> response = ApiCallHelper.post(APIs.AttributeAssignment.CREATE_ATTRIBUTE_ASGMT, asgmtJson.toString());

        assertThat(response.statusCode()).isEqualTo(400);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void createAttrAsgmtApi_BlankDeviceType_400BadRequest() {
        JSONObject asgmtJson = new AttributeAsgmtCreateBuilder.Builder(Integer.parseInt(attrId))
                .withMultiplier(Optional.empty())
                .withPointType(Optional.empty())
                .build();
        
        asgmtJson.put("paoType", "");

        ExtractableResponse<?> response = ApiCallHelper.post(APIs.AttributeAssignment.CREATE_ATTRIBUTE_ASGMT, asgmtJson.toString());

        assertThat(response.statusCode()).isEqualTo(400);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void createAttrAsgmtApi_PointTypeMissing_422Unprocessable() {
        JSONObject asgmtJson = new AttributeAsgmtCreateBuilder.Builder(Integer.parseInt(attrId))
                .withMultiplier(Optional.empty())
                .withPaoType(Optional.empty())
                .build();

        ExtractableResponse<?> response = ApiCallHelper.post(APIs.AttributeAssignment.CREATE_ATTRIBUTE_ASGMT, asgmtJson.toString());

        assertThat(response.statusCode()).isEqualTo(422);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void createAttrAsgmtApi_InvalidPointType_400BadRequest() {
        JSONObject asgmtJson = new AttributeAsgmtCreateBuilder.Builder(Integer.parseInt(attrId))
                .withMultiplier(Optional.empty())
                .withPaoType(Optional.empty())                
                .build();
        
        asgmtJson.put("pointType", "invalid");

        ExtractableResponse<?> response = ApiCallHelper.post(APIs.AttributeAssignment.CREATE_ATTRIBUTE_ASGMT, asgmtJson.toString());

        assertThat(response.statusCode()).isEqualTo(400);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void createAttrAsgmtApi_BlankPointType_400BadRequest() {
        JSONObject asgmtJson = new AttributeAsgmtCreateBuilder.Builder(Integer.parseInt(attrId))
                .withMultiplier(Optional.empty())
                .withPaoType(Optional.empty())
                .build();
        
        asgmtJson.put("pointType", "");

        ExtractableResponse<?> response = ApiCallHelper.post(APIs.AttributeAssignment.CREATE_ATTRIBUTE_ASGMT, asgmtJson.toString());

        assertThat(response.statusCode()).isEqualTo(400);
    }
}
