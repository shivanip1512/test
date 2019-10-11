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
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupVersacom;
import com.cannontech.rest.api.utilities.Log;
import com.cannontech.rest.api.utilities.RestApiDocumentationUtility;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class VersacomLoadGroupSetupApiControllerTest {

    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private String paoId = null;
    private String copyPaoId = null;
    private MockLoadGroupVersacom loadGroup = null;
    private FieldDescriptor[] versacomFieldDescriptor = null;

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation,method);
        versacomFieldDescriptor = new FieldDescriptor[] {
            fieldWithPath("LM_GROUP_VERSACOM.name").type(JsonFieldType.STRING).description("Load Group Name"), 
            fieldWithPath("LM_GROUP_VERSACOM.type").type(JsonFieldType.STRING).description("Load Group Type"),
            fieldWithPath("LM_GROUP_VERSACOM.kWCapacity").type(JsonFieldType.NUMBER).description("KW Capacity"),
            fieldWithPath("LM_GROUP_VERSACOM.disableGroup").type(JsonFieldType.BOOLEAN).description("Flag to disable Group"),
            fieldWithPath("LM_GROUP_VERSACOM.disableControl").type(JsonFieldType.BOOLEAN).description("Flag to disable Control"),
            fieldWithPath("LM_GROUP_VERSACOM.routeId").type(JsonFieldType.NUMBER).description("Route Id"),
            fieldWithPath("LM_GROUP_VERSACOM.routeName").type(JsonFieldType.STRING).description("Route Name").optional(),
            fieldWithPath("LM_GROUP_VERSACOM.utilityAddress").type(JsonFieldType.NUMBER).description("Value of Utility Address"),
            fieldWithPath("LM_GROUP_VERSACOM.sectionAddress").type(JsonFieldType.NUMBER).description("Value of Section Address"),
            fieldWithPath("LM_GROUP_VERSACOM.classAddress").type(JsonFieldType.STRING).description("Value of Class Address"),
            fieldWithPath("LM_GROUP_VERSACOM.divisionAddress").type(JsonFieldType.STRING).description("Value of Division Address"),
            fieldWithPath("LM_GROUP_VERSACOM.addressUsage").type(JsonFieldType.ARRAY).description("Address Uasge. Select UTILITY, SECTION, CLASS, DIVISION"),
            fieldWithPath("LM_GROUP_VERSACOM.relayUsage").type(JsonFieldType.ARRAY).description("Relay Usage. RELAY_1, RELAY_2, RELAY_3, RELAY_4")
        };
        loadGroup = (MockLoadGroupVersacom) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_VERSACOM);
    }

    @AfterMethod
    public void tearDown() {
        this.restDocumentation.afterTest();
    }

    @Test
    public void Test_LmVersacom_Create() {
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", requestFields(versacomFieldDescriptor),
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

    @Test(dependsOnMethods = { "Test_LmVersacom_Create" })
    public void Test_LmVersacom_Get() {
        List<FieldDescriptor> list = new ArrayList<>(Arrays.asList(versacomFieldDescriptor));
        list.add(0,fieldWithPath("LM_GROUP_VERSACOM.id").type(JsonFieldType.NUMBER).description("Load Group Id"));
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

    @Test(dependsOnMethods = { "Test_LmVersacom_Get" })
    public void Test_LmVersacom_Update() {
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", requestFields(versacomFieldDescriptor),
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

    @Test(dependsOnMethods = { "Test_LmVersacom_Update" })
    public void Test_LmVersacom_Copy() {
        MockLoadGroupCopy loadGroupCopy = MockLoadGroupCopy.builder().name("Versacom-Copy-Test").routeId(12815).build();
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

    @Test(dependsOnMethods = { "Test_LmVersacom_Copy" })
    public void Test_LmVersacom_Delete() {

        MockLMDto lmDeleteObject = MockLMDto.builder()
                                    .name("Test_Versacom_LoadGroup")
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
        
        MockLMDto lmDeleteCopyObject = MockLMDto.builder().name("Versacom-Copy-Test").build();
        Log.info("Delete payload is : " + JsonUtil.beautifyJson(lmDeleteCopyObject.toString()));
        ExtractableResponse<?> copyResponse = ApiCallHelper.delete("deleteloadgroup", lmDeleteCopyObject, copyPaoId);
        assertTrue("Status code should be 200", copyResponse.statusCode() == 200);
    }
}
