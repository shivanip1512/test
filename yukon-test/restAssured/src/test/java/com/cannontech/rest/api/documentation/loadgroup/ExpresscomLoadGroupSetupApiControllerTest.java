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
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupCopy;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupExpresscom;
import com.cannontech.rest.api.utilities.Log;
import com.cannontech.rest.api.utilities.RestApiDocumentationUtility;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ExpresscomLoadGroupSetupApiControllerTest {
    
    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private String paoId = null;
    private String copyPaoId = null;
    private FieldDescriptor[] expresscomFieldDescriptor = null;
    private MockLoadGroupExpresscom loadGroup = null;

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation,method);
        expresscomFieldDescriptor = new FieldDescriptor[] {
            fieldWithPath("LM_GROUP_EXPRESSCOMM.name").type(JsonFieldType.STRING).description("Load Group Name"), 
            fieldWithPath("LM_GROUP_EXPRESSCOMM.type").type(JsonFieldType.STRING).description("Load Group Type"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.kWCapacity").type(JsonFieldType.NUMBER).description("KW Capacity"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.disableGroup").type(JsonFieldType.BOOLEAN).description("Flag to disable Group"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.disableControl").type(JsonFieldType.BOOLEAN).description("Flag to disable Control"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.routeId").type(JsonFieldType.NUMBER).description("Route Id"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.routeName").type(JsonFieldType.STRING).description("Route Name").optional(),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.serviceProvider").type(JsonFieldType.NUMBER).description("Service Provider Id"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.geo").type(JsonFieldType.NUMBER).description("Value of Geographical Address"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.substation").type(JsonFieldType.NUMBER).description("Value of Substation Address"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.feeder").type(JsonFieldType.STRING).description("Value of Fedder Address"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.zip").type(JsonFieldType.NUMBER).description("Value of Zip Address"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.user").type(JsonFieldType.NUMBER).description("Value of User Address"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.program").type(JsonFieldType.NUMBER).description("Value of Program Address"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.splinter").type(JsonFieldType.NUMBER).description("Value of Splinter Address"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.addressUsage").type(JsonFieldType.ARRAY).description("Address Usage. GEO, SUBSTATION, FEEDER, ZIP, USER, LOAD, PROGRAM, SPLINTER"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.relayUsage").type(JsonFieldType.ARRAY).description("Relay Usage. Use LOAD_1 to LOAD_8"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.protocolPriority").type(JsonFieldType.STRING).description("Relay Usage. Use DEFAULT,MEDIUM ,HIGH ,HIGHEST")};
        loadGroup = (MockLoadGroupExpresscom) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_EXPRESSCOMM);
        
    }

    @AfterMethod
    public void tearDown() {
        this.restDocumentation.afterTest();
    }

    @Test
    public void Test_Expresscom_Create() {
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", requestFields(expresscomFieldDescriptor),
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


        paoId = response.path("groupId").toString();
        assertTrue("PAO ID should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @Test(dependsOnMethods = { "Test_Expresscom_Create" })
    public void Test_LmExpresscom_Get() {
        List<FieldDescriptor> list = new ArrayList<>(Arrays.asList(expresscomFieldDescriptor));
        list.add(0,fieldWithPath("LM_GROUP_EXPRESSCOMM.id").type(JsonFieldType.NUMBER).description("Load Group Id"));
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

    @Test(dependsOnMethods = { "Test_LmExpresscom_Get" })
    public void Test_LmExpresscom_Update() {
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", requestFields(expresscomFieldDescriptor),
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

        paoId = response.path("groupId").toString();
        assertTrue("PAO ID should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @Test(dependsOnMethods = { "Test_LmExpresscom_Update" })
    public void Test_LmExpresscom_Copy() {
        MockLoadGroupCopy loadGroupCopy = MockLoadGroupCopy.builder().name("Expresscom-Copy-Test").routeId(12815).build();
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", 
                                    requestFields(
                                    fieldWithPath("LOAD_GROUP_COPY.name").type(JsonFieldType.STRING).description("Load Group Copy Name"),
                                    fieldWithPath("LOAD_GROUP_COPY.routeId").type(JsonFieldType.NUMBER).description("Route Id")), 
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
        copyPaoId = response.path("groupId").toString();
        assertTrue("PAO ID should not be Null", copyPaoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @Test(dependsOnMethods = { "Test_LmExpresscom_Copy" })
    public void Test_LmExpresscom_Delete() {
        MockLMDto lmDeleteObject = MockLMDto.builder()
                .name("Test_ExpressCom_LoadGroup")
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
        
        MockLMDto lmDeleteCopyObject = MockLMDto.builder().name("Expresscom-Copy-Test").build();
        Log.info("Delete payload is : " + JsonUtil.beautifyJson(lmDeleteCopyObject.toString()));
        ExtractableResponse<?> copyResponse = ApiCallHelper.delete("deleteloadgroup", lmDeleteCopyObject, copyPaoId);
        assertTrue("Status code should be 200", copyResponse.statusCode() == 200);
    }
}
