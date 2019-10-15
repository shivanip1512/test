package com.cannontech.rest.api.documentation.loadgroup;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.restdocs.ManualRestDocumentation;
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
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupCopy;
import com.cannontech.rest.api.utilities.Log;
import com.cannontech.rest.api.utilities.RestApiDocumentationUtility;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class MeterDisconnectLoadGroupSetupApiControllerTest {

    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private String paoId = null;
    private String copyPaoId = null;
    private FieldDescriptor[] meterDisconnectFieldDescriptor = null;
    private MockLoadGroupBase loadGroup = null;

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation,method);
        meterDisconnectFieldDescriptor = new FieldDescriptor[] {
            fieldWithPath("LM_GROUP_METER_DISCONNECT.name").type(JsonFieldType.STRING).description("Load Group Name"), 
            fieldWithPath("LM_GROUP_METER_DISCONNECT.type").type(JsonFieldType.STRING).description("Load Group Type"),
            fieldWithPath("LM_GROUP_METER_DISCONNECT.kWCapacity").type(JsonFieldType.NUMBER).description("KW Capacity"),
            fieldWithPath("LM_GROUP_METER_DISCONNECT.disableGroup").type(JsonFieldType.BOOLEAN).description("Flag to disable Group"),
            fieldWithPath("LM_GROUP_METER_DISCONNECT.disableControl").type(JsonFieldType.BOOLEAN).description("Flag to disable Control")
        };
        loadGroup = LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_METER_DISCONNECT);
    }

    @AfterMethod
    public void tearDown() {
        this.restDocumentation.afterTest();
    }

    @Test
    public void Test_LmGroupMeterDisconnect_Create() {
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", requestFields(meterDisconnectFieldDescriptor),
                                    responseFields(fieldWithPath("groupId").type(JsonFieldType.NUMBER).description("Load Group Id"))))
                                .accept("application/json")
                                .contentType("application/json")
                                .header("Authorization","Bearer " + ApiCallHelper.authToken)
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

    @Test(dependsOnMethods = { "Test_LmGroupMeterDisconnect_Create" })
    public void Test_LmGroupMeterDisconnect_Get() {
       List<FieldDescriptor> list = new ArrayList<>(Arrays.asList(meterDisconnectFieldDescriptor));
       list.add(0,fieldWithPath("LM_GROUP_METER_DISCONNECT.id").type(JsonFieldType.NUMBER).description("Load Group Id"));
       Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", responseFields(list)))
                                .accept("application/json")
                                .contentType("application/json")
                                .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                .when()
                                .get(ApiCallHelper.getProperty("getloadgroup") + paoId)
                                .then()
                                .extract()
                                .response();
        
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @Test(dependsOnMethods = { "Test_LmGroupMeterDisconnect_Get" })
    public void Test_LmGroupMeterDisconnect_Update() {
       Response response = given(documentationSpec)
                               .filter(document("{ClassName}/{methodName}", requestFields(meterDisconnectFieldDescriptor),
                                   responseFields(fieldWithPath("groupId").type(JsonFieldType.NUMBER).description("Load Group Id"))))
                               .accept("application/json")
                               .contentType("application/json")
                               .header("Authorization","Bearer " + ApiCallHelper.authToken)
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

    @Test(dependsOnMethods = { "Test_LmGroupMeterDisconnect_Update" })
   public void Test_LmGroupMeterDisconnect_Copy() {
       MockLoadGroupCopy loadGroupCopy = MockLoadGroupCopy.builder().name(LoadGroupHelper.getCopiedLoadGroupName(MockPaoType.LM_GROUP_METER_DISCONNECT)).build();
       Response response = given(documentationSpec)
                               .filter(document("{ClassName}/{methodName}", 
                                   requestFields(
                                   fieldWithPath("LOAD_GROUP_COPY.name").type(JsonFieldType.STRING).description("Load Group Copy Name")), 
                                   responseFields(fieldWithPath("groupId").type(JsonFieldType.NUMBER).description("Load Group Copy Id"))))
                               .accept("application/json")
                               .contentType("application/json")
                               .header("Authorization","Bearer " + ApiCallHelper.authToken)
                               .body(loadGroupCopy)
                               .when()
                               .post(ApiCallHelper.getProperty("copyloadgroup")+ paoId)
                               .then()
                               .extract()
                               .response();
       copyPaoId = response.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString();
       assertTrue("Load Group Id should not be Null", copyPaoId != null);
       assertTrue("Status code should be 200", response.statusCode() == 200);
   }

    @Test(dependsOnMethods = { "Test_LmGroupMeterDisconnect_Copy" })
    public void Test_LmGroupMeterDisconnect_Delete() {
        MockLMDto lmDeleteObject = MockLMDto.builder()
                .name(LoadGroupHelper.getLoadGroupName(MockPaoType.LM_GROUP_METER_DISCONNECT))
                .build();

       Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
           requestFields(
               fieldWithPath("name").type(JsonFieldType.STRING).description("Load Group Name")), 
               responseFields(fieldWithPath("groupId").type(JsonFieldType.NUMBER).description("Load Group Id"))))
           .accept("application/json")
           .contentType("application/json")
           .header("Authorization","Bearer " + ApiCallHelper.authToken)
           .body(lmDeleteObject)
           .when()
           .delete(ApiCallHelper.getProperty("deleteloadgroup") + paoId)
           .then()
           .extract()
           .response();
       assertTrue("Status code should be 200", response.statusCode() == 200);
       

       MockLMDto lmDeleteCopyObject = MockLMDto.builder()
                                       .name(LoadGroupHelper.getCopiedLoadGroupName(MockPaoType.LM_GROUP_METER_DISCONNECT))
                                       .build();
       Log.info("Delete Load Group is : " + JsonUtil.beautifyJson(lmDeleteCopyObject.toString()));
       ExtractableResponse<?> copyResponse = ApiCallHelper.delete("deleteloadgroup", lmDeleteCopyObject, copyPaoId);
       assertTrue("Status code should be 200", copyResponse.statusCode() == 200);
   }
}
