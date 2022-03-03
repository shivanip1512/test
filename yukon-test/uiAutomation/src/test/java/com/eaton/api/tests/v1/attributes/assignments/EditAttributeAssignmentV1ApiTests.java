package com.eaton.api.tests.v1.attributes.assignments;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.admin.attributes.AttributeAsgmtEditBuilder;
import com.eaton.builders.admin.attributes.AttributeAsgmtTypes;
import com.eaton.builders.admin.attributes.AttributeService;
import com.eaton.framework.APIs;
import com.eaton.framework.TestConstants;
import com.eaton.rest.api.common.ApiCallHelper;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class EditAttributeAssignmentV1ApiTests {
    private Faker faker = new Faker();
    private Integer attrId;
    private Integer offset;
    private Integer attrAsgmtId;
    private AttributeAsgmtTypes.PointTypes pointType;
    private AttributeAsgmtTypes.PaoTypes paoType;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        Map<String, Pair<JSONObject, JSONObject>> map = AttributeService.createAttributeWithAssignment(Optional.empty());

        Pair<JSONObject, JSONObject> pair = map.get("AttributeAsgmt");
        
        JSONObject createdResponse = pair.getValue1();
        attrAsgmtId = createdResponse.getInt("attributeAssignmentId");
        attrId = createdResponse.getInt("attributeId");
        offset = createdResponse.getInt("offset");
        String dt = createdResponse.getString("paoType");
        String pt = createdResponse.getString("pointType");
        
        pointType = AttributeAsgmtTypes.PointTypes.valueOf(pt.toUpperCase());
        paoType = AttributeAsgmtTypes.PaoTypes.valueOf(dt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttrAsgmtApi_AllFields_200Ok() {
        SoftAssertions softly = new SoftAssertions();
        Map<String, Pair<JSONObject, JSONObject>> map = AttributeService.createAttributeWithSpecificAssignment(AttributeAsgmtTypes.PaoTypes.RFN410CL, AttributeAsgmtTypes.PointTypes.ANALOG, faker.number().numberBetween(1, 99999999), Optional.empty());
        
        Pair<JSONObject, JSONObject> asgmtPair = map.get("AttributeAsgmt");
        Pair<JSONObject, JSONObject> attrPair = map.get("Attribute");

        JSONObject asgmtResponse = asgmtPair.getValue1();
        Integer attrAsgmtId = asgmtResponse.getInt("attributeAssignmentId");
        
        JSONObject attrResponse = attrPair.getValue1();
        Integer attributeId = attrResponse.getInt("customAttributeId");
        String attributeName = attrResponse.getString("name");

        JSONObject jsonAttr = new AttributeAsgmtEditBuilder.Builder(attributeId)
                .withMultiplier(Optional.empty())
                .withPaoType(Optional.empty())
                .withPointType(Optional.empty())
                .build();
        
        ExtractableResponse<?> asgmtRes = ApiCallHelper.patch(APIs.AttributeAssignment.UPDATE_ATTRIBUTE_ASGMT + attrAsgmtId, jsonAttr.toString());

        String resString = asgmtRes.asString();
        JSONObject res = new JSONObject(resString);
        JSONObject customAttr = res.getJSONObject("customAttribute");
        
        softly.assertThat(asgmtRes.statusCode()).isEqualTo(200);
        softly.assertThat(res.getInt("attributeId")).isEqualTo(jsonAttr.getInt("attributeId"));
        softly.assertThat(res.getInt("attributeAssignmentId")).isNotNull();
        softly.assertThat(res.getString("paoType")).isEqualTo(jsonAttr.getString("paoType"));
        softly.assertThat(res.getInt("offset")).isEqualTo(jsonAttr.getInt("offset"));
        softly.assertThat(res.getString("pointType")).isEqualTo(jsonAttr.getString("pointType"));
        softly.assertThat(customAttr).isNotNull();
        softly.assertThat(customAttr.getInt("customAttributeId")).isEqualTo(attributeId);
        softly.assertThat(customAttr.getString("name")).isEqualTo(attributeName);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttrAsgmtApi_DuplicateAttrAndDeviceType_400BadRequest() {
        Pair<JSONObject, JSONObject> asgmtPair = AttributeService.createAsgmtForAttribute(attrId);
        
        JSONObject response = asgmtPair.getValue1();
        
        Integer attrAsgmt = response.getInt("attributeAssignmentId");
        
        JSONObject jsonAttr = new AttributeAsgmtEditBuilder.Builder(attrId)
                .withMultiplier(Optional.of(offset))
                .withPaoType(Optional.of(paoType))
                .withPointType(Optional.of(pointType))
                .build();
        
        ExtractableResponse<?> asgmtRes = ApiCallHelper.patch(APIs.AttributeAssignment.UPDATE_ATTRIBUTE_ASGMT + attrAsgmt, jsonAttr.toString());

        assertThat(asgmtRes.statusCode()).isEqualTo(400);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttrAsgmtApi_NotFoundAttrId_400BadRequest() {
        String invalidId = faker.number().digits(9);

        JSONObject jsonAttr = new AttributeAsgmtEditBuilder.Builder(Integer.parseInt(invalidId))
                .withMultiplier(Optional.of(offset))
                .withPaoType(Optional.of(paoType))
                .withPointType(Optional.of(pointType))
                .build();

        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.AttributeAssignment.UPDATE_ATTRIBUTE_ASGMT + attrAsgmtId, jsonAttr.toString());

        assertThat(response.statusCode()).isEqualTo(400);
    }
    
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttrAsgmtApi_AttrIdMissing_400BadRequest() {
        String invalidId = faker.number().digits(9);

        JSONObject asgmtJson = new AttributeAsgmtEditBuilder.Builder(Integer.parseInt(invalidId))
                .withMultiplier(Optional.of(offset))
                .withPaoType(Optional.of(paoType))
                .withPointType(Optional.of(pointType))
                .build();

        asgmtJson.remove("attributeId");

        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.AttributeAssignment.UPDATE_ATTRIBUTE_ASGMT + attrAsgmtId, asgmtJson.toString());

        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttrAsgmtApi_EmptyAttrId_400BadRequest() {
        String invalidId = faker.number().digits(9);

        JSONObject asgmtJson = new AttributeAsgmtEditBuilder.Builder(Integer.parseInt(invalidId))
                .withMultiplier(Optional.of(offset))
                .withPaoType(Optional.of(paoType))
                .withPointType(Optional.of(pointType))
                .build();

        asgmtJson.put("attributeId", "");

        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.AttributeAssignment.UPDATE_ATTRIBUTE_ASGMT + attrAsgmtId, asgmtJson.toString());

        assertThat(response.statusCode()).isEqualTo(400);
    }

    // TODO getting a 400 malformed json should there be a better error message?
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttrAsgmtApi_InvalidAttrId_400BadRequest() {
        String invalidId = faker.number().digits(12);

        JSONObject asgmtJson = new AttributeAsgmtEditBuilder.Builder(1)
                .withMultiplier(Optional.of(offset))
                .withPaoType(Optional.of(paoType))
                .withPointType(Optional.of(pointType))
                .build();

        asgmtJson.put("attributeId", invalidId);

        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.AttributeAssignment.UPDATE_ATTRIBUTE_ASGMT + attrAsgmtId, asgmtJson.toString());

        assertThat(response.statusCode()).isEqualTo(400);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttrAsgmtApi_InvalidAttrAsgmtId_400BadRequest() {
        String invalidId = faker.number().digits(12);
        
        JSONObject asgmtJson = new AttributeAsgmtEditBuilder.Builder(attrId)
                .withMultiplier(Optional.of(offset))
                .withPaoType(Optional.of(paoType))
                .withPointType(Optional.of(pointType))
                .build();

        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.AttributeAssignment.UPDATE_ATTRIBUTE_ASGMT + invalidId, asgmtJson.toString());

        assertThat(response.statusCode()).isEqualTo(400);
    }    
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttrAsgmtApi_NotFoundAttrAsgmtId_400BadRequest() {
        String invalidId = faker.number().digits(9);
        
        JSONObject asgmtJson = new AttributeAsgmtEditBuilder.Builder(attrId)
                .withMultiplier(Optional.of(offset))
                .withPaoType(Optional.of(paoType))
                .withPointType(Optional.of(pointType))
                .build();

        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.AttributeAssignment.UPDATE_ATTRIBUTE_ASGMT + invalidId, asgmtJson.toString());

        assertThat(response.statusCode()).isEqualTo(400);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttrAsgmtApi_EmptyAttrAsgmtId_400BadRequest() {
        JSONObject asgmtJson = new AttributeAsgmtEditBuilder.Builder(attrId)
                .withMultiplier(Optional.of(offset))
                .withPaoType(Optional.of(paoType))
                .withPointType(Optional.of(pointType))
                .build();

        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.AttributeAssignment.UPDATE_ATTRIBUTE_ASGMT + "", asgmtJson.toString());

        assertThat(response.statusCode()).isEqualTo(404);
    }

    // TODO in the UI offset can not be larger than 99,999,999
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttrAsgmtApi_OffsetMaxLenth_422Unprocessable() {
        throw new SkipException("Create defect");
//        JSONObject asgmtJson = new AttributeAsgmtEditBuilder.Builder(attrId)
//                .withPaoType(Optional.of(paoType))
//                .withMultiplier(Optional.of(100000000))
//                .withPointType(Optional.of(pointType))
//                .build();
//        
//        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.AttributeAssignment.UPDATE_ATTRIBUTE_ASGMT + attrAsgmtId, asgmtJson.toString());
//
//        assertThat(response.statusCode()).isEqualTo(422);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttrAsgmtApi_OffsetMinLenth_422Unprocessable() {
        throw new SkipException("Create defect");
//        JSONObject asgmtJson = new AttributeAsgmtEditBuilder.Builder(attrId)
//                .withPaoType(Optional.of(paoType))
//                .withMultiplier(Optional.of(-1))
//                .withPointType(Optional.of(pointType))
//                .build();
//
//        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.AttributeAssignment.UPDATE_ATTRIBUTE_ASGMT + attrAsgmtId, asgmtJson.toString());
//
//        assertThat(response.statusCode()).isEqualTo(422);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttrAsgmtApi_OffsetCantBeZeroForCertainDeviceTypes_422Unprocessable() {
        throw new SkipException("Create defect");
//        JSONObject asgmtJson = new AttributeAsgmtEditBuilder.Builder(attrId)
//                .withPaoType(Optional.of(paoType))
//                .withMultiplier(Optional.of(0))
//                .withPointType(Optional.of(AttributeAsgmtTypes.PointTypes.STATUS))
//                .build();
//
//        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.AttributeAssignment.UPDATE_ATTRIBUTE_ASGMT + attrAsgmtId, asgmtJson.toString());
//
//        assertThat(response.statusCode()).isEqualTo(422);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttrAsgmtApi_InvalidDeviceType_400BadRequest() {
        JSONObject asgmtJson = new AttributeAsgmtEditBuilder.Builder(attrId)
                .withMultiplier(Optional.of(offset))
                .withPointType(Optional.of(pointType))
                .build();
        
        asgmtJson.put("paoType", "invalid");

        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.AttributeAssignment.UPDATE_ATTRIBUTE_ASGMT + attrAsgmtId, asgmtJson.toString());

        assertThat(response.statusCode()).isEqualTo(400);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttrAsgmtApi_BlankDeviceType_400BadRequest() {
        JSONObject asgmtJson = new AttributeAsgmtEditBuilder.Builder(attrId)
                .withMultiplier(Optional.of(offset))
                .withPointType(Optional.of(pointType))
                .build();
        
        asgmtJson.put("paoType", "");

        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.AttributeAssignment.UPDATE_ATTRIBUTE_ASGMT + attrAsgmtId, asgmtJson.toString());

        assertThat(response.statusCode()).isEqualTo(400);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttrAsgmtApi_InvalidPointType_400BadRequest() {
        JSONObject asgmtJson = new AttributeAsgmtEditBuilder.Builder(attrId)
                .withMultiplier(Optional.of(offset))
                .withPaoType(Optional.of(paoType))               
                .build();
        
        asgmtJson.put("pointType", "invalid");

        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.AttributeAssignment.UPDATE_ATTRIBUTE_ASGMT + attrAsgmtId, asgmtJson.toString());

        assertThat(response.statusCode()).isEqualTo(400);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttrAsgmtApi_BlankPointType_400BadRequest() {
        JSONObject asgmtJson = new AttributeAsgmtEditBuilder.Builder(attrId)
                .withMultiplier(Optional.of(offset))
                .withPaoType(Optional.of(paoType)) 
                .build();
        
        asgmtJson.put("pointType", "");

        ExtractableResponse<?> response = ApiCallHelper.patch(APIs.AttributeAssignment.UPDATE_ATTRIBUTE_ASGMT + attrAsgmtId, asgmtJson.toString());

        assertThat(response.statusCode()).isEqualTo(400);
    }
}
