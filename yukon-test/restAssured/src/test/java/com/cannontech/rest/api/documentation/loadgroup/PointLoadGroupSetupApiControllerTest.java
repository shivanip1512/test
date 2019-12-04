package com.cannontech.rest.api.documentation.loadgroup;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

import org.springframework.restdocs.ManualRestDocumentation;

import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.JsonUtil;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupCopy;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupPoint;
import com.cannontech.rest.api.utilities.Log;
import com.cannontech.rest.api.utilities.RestApiDocumentationUtility;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class PointLoadGroupSetupApiControllerTest {
    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private String paoId = null;
    private String copyPaoId = null;
    private FieldDescriptor[] pointGroupFieldDescriptor = null;
    private MockLoadGroupPoint loadGroup = null;

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation, method);
        pointGroupFieldDescriptor = new FieldDescriptor[] {
                fieldWithPath("LM_GROUP_POINT.name").type(JsonFieldType.STRING).description("Load Group Name"),
                fieldWithPath("LM_GROUP_POINT.type").type(JsonFieldType.STRING).description("Load Group Type"),
                fieldWithPath("LM_GROUP_POINT.kWCapacity").type(JsonFieldType.NUMBER).description("KW Capacity"),
                fieldWithPath("LM_GROUP_POINT.disableGroup").type(JsonFieldType.BOOLEAN).description("Flag to disable Group"),
                fieldWithPath("LM_GROUP_POINT.disableControl").type(JsonFieldType.BOOLEAN).description("Flag to disable Control"),
                fieldWithPath("LM_GROUP_POINT.deviceUsage.id").type(JsonFieldType.NUMBER)
                        .description("Control device id"),
                fieldWithPath("LM_GROUP_POINT.pointUsage.id").type(JsonFieldType.NUMBER)
                        .description("Point id of available control device"),
                fieldWithPath("LM_GROUP_POINT.startControlRawState.rawState").type(JsonFieldType.NUMBER)
                        .description("Control start state id of available control Point ")
        };
        loadGroup = (MockLoadGroupPoint) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_POINT);
    }

    @AfterMethod
    public void tearDown() {
        this.restDocumentation.afterTest();
    }

    @Test
    public void Test_LmPointGroup_Create() {
        Response response = given(documentationSpec)
                .filter(document("{ClassName}/{methodName}", requestFields(pointGroupFieldDescriptor),
                        responseFields(fieldWithPath("groupId").type(JsonFieldType.NUMBER).description("Load Group Id"))))
                .accept("application/json")
                .contentType("application/json")
                .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                .body(loadGroup)
                .when()
                .post(ApiCallHelper.getProperty("saveloadgroup"))
                .then()
                .extract()
                .response();

        paoId = response.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString();
        assertTrue("Load Group Id should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @Test(dependsOnMethods = { "Test_LmPointGroup_Create" })
    public void Test_LmPointGroup_Get() {
        List<FieldDescriptor> list = new ArrayList<>(Arrays.asList(pointGroupFieldDescriptor));
        list.add(0, fieldWithPath("LM_GROUP_POINT.id").type(JsonFieldType.NUMBER).description("Load Group Id"));
        list.add(7, fieldWithPath("LM_GROUP_POINT.deviceUsage.name").type(JsonFieldType.STRING).optional()
                .description("Control device usage name."));
        list.add(9, fieldWithPath("LM_GROUP_POINT.pointUsage.name").type(JsonFieldType.STRING).optional()
                .description("Point name of available control device."));
        list.add(11, fieldWithPath("LM_GROUP_POINT.startControlRawState.stateText").type(JsonFieldType.STRING).optional()
                .description("Control start state name of available control Point."));
        Response response = given(documentationSpec)
                .filter(document("{ClassName}/{methodName}",
                        responseFields(list)))
                .accept("application/json")
                .contentType("application/json")
                .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                .when()
                .get(ApiCallHelper.getProperty("getloadgroup") + paoId)
                .then()
                .extract()
                .response();
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @Test(dependsOnMethods = { "Test_LmPointGroup_Get" })
    public void Test_LmPointGroup_Update() {
        Response response = given(documentationSpec)
                .filter(document("{ClassName}/{methodName}", requestFields(pointGroupFieldDescriptor),
                        responseFields(fieldWithPath("groupId").type(JsonFieldType.NUMBER).description("Load Group Id"))))
                .accept("application/json")
                .contentType("application/json")
                .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                .body(loadGroup)
                .when()
                .post(ApiCallHelper.getProperty("updateloadgroup") + paoId)
                .then()
                .extract()
                .response();

        paoId = response.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString();
        assertTrue("Load Group Id should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @Test(dependsOnMethods = { "Test_LmPointGroup_Update" })
    public void Test_LmPointGroup_Copy() {
        MockLoadGroupCopy loadGroupCopy = MockLoadGroupCopy.builder()
                .name(LoadGroupHelper.getCopiedLoadGroupName(MockPaoType.LM_GROUP_POINT)).build();
        Response response = given(documentationSpec)
                .filter(document("{ClassName}/{methodName}",
                        requestFields(
                                fieldWithPath("LOAD_GROUP_COPY.name").type(JsonFieldType.STRING)
                                        .description("Load Group Copy Name")),
                        responseFields(fieldWithPath("groupId").type(JsonFieldType.NUMBER).description("Load Group Copy Id"))))
                .accept("application/json")
                .contentType("application/json")
                .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                .body(loadGroupCopy)
                .when()
                .post(ApiCallHelper.getProperty("copyloadgroup") + paoId)
                .then()
                .extract()
                .response();
        copyPaoId = response.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString();
        assertTrue("Load Group Id should not be Null", copyPaoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @Test(dependsOnMethods = { "Test_LmPointGroup_Copy" })
    public void Test_LmPointGroup_Delete() {

        MockLMDto lmDeleteObject = MockLMDto.builder()
                .name(LoadGroupHelper.getLoadGroupName(MockPaoType.LM_GROUP_POINT))
                .build();

        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                requestFields(
                        fieldWithPath("name").type(JsonFieldType.STRING).description("Load Group Name")),
                responseFields(fieldWithPath("groupId").type(JsonFieldType.NUMBER).description("Load Group Id"))))
                .accept("application/json")
                .contentType("application/json")
                .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                .body(lmDeleteObject)
                .when()
                .delete(ApiCallHelper.getProperty("deleteloadgroup") + paoId)
                .then()
                .extract()
                .response();
        assertTrue("Status code should be 200", response.statusCode() == 200);

        MockLMDto lmDeleteCopyObject = MockLMDto.builder()
                .name(LoadGroupHelper.getCopiedLoadGroupName(MockPaoType.LM_GROUP_POINT))
                .build();
        Log.info("Delete Load Group is : " + JsonUtil.beautifyJson(lmDeleteCopyObject.toString()));
        ExtractableResponse<?> copyResponse = ApiCallHelper.delete("deleteloadgroup", lmDeleteCopyObject, copyPaoId);
        assertTrue("Status code should be 200", copyResponse.statusCode() == 200);
    }
}
