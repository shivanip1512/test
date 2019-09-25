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
import com.cannontech.rest.api.utilities.RestApiDocumentationUtility;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class DigiSepLoadGroupSetupApiControllerTest {
    
    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private String paoId = null;
    private String copyPaoId = null;
    private FieldDescriptor[] digiSepFieldDescriptor = null;

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation,method);
        digiSepFieldDescriptor = new FieldDescriptor[] {
                         fieldWithPath("LM_GROUP_DIGI_SEP.name").type(JsonFieldType.STRING).description("Load Group Name"),
                         fieldWithPath("LM_GROUP_DIGI_SEP.type").type(JsonFieldType.STRING).description("Load Group Type"),
                         fieldWithPath("LM_GROUP_DIGI_SEP.kWCapacity").type(JsonFieldType.NUMBER).description("KW Capacity"),
                         fieldWithPath("LM_GROUP_DIGI_SEP.disableGroup").type(JsonFieldType.BOOLEAN).description("Flag to disable Group"),
                         fieldWithPath("LM_GROUP_DIGI_SEP.disableControl").type(JsonFieldType.BOOLEAN).description("Flag to disable Control"),
                         fieldWithPath("LM_GROUP_DIGI_SEP.utilityEnrollmentGroup").type(JsonFieldType.NUMBER).description("Utility Enrollment Group"),
                         fieldWithPath("LM_GROUP_DIGI_SEP.deviceClassSet").type(JsonFieldType.ARRAY).description("Device Class Set"),
                         fieldWithPath("LM_GROUP_DIGI_SEP.rampInMinutes").type(JsonFieldType.NUMBER).description("RampIn value in minutes"),
                         fieldWithPath("LM_GROUP_DIGI_SEP.rampOutMinutes").type(JsonFieldType.NUMBER).description("RampOut value in minutes")};
    }

    @AfterMethod
    public void tearDown() {
        this.restDocumentation.afterTest();
    }
    
    @Test
    public void Test_LmDigiSep_Create() {
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", requestFields(digiSepFieldDescriptor),
                                    responseFields(fieldWithPath("groupId").type(JsonFieldType.NUMBER).description("Load Group Id"))))
                                .accept("application/json")
                                .contentType("application/json")
                                .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                .body(ApiCallHelper.getInputFile("documentation\\loadgroup\\DigiSepCreate.json"))
                                .when()
                                .post(ApiCallHelper.getProperty("saveloadgroup"))
                                .then()
                                .extract()
                                .response();

        paoId = response.path("groupId").toString();
        assertTrue("PAO ID should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }
    
    @Test(dependsOnMethods = { "Test_LmDigiSep_Create" })
    public void Test_LmDigiSep_Get() {
        List<FieldDescriptor> list = new ArrayList<>(Arrays.asList(digiSepFieldDescriptor));
        list.add(0,fieldWithPath("LM_GROUP_DIGI_SEP.id").type(JsonFieldType.NUMBER).description("Load Group Id"));
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
    
    @Test(dependsOnMethods = { "Test_LmDigiSep_Get" })
    public void Test_LmDigiSep_Update() {
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", requestFields(digiSepFieldDescriptor),
                                    responseFields(fieldWithPath("groupId").type(JsonFieldType.NUMBER).description("Load Group Id"))))
                                .accept("application/json")
                                .contentType("application/json")
                                .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                .body(ApiCallHelper.getInputFile("documentation\\loadgroup\\DigiSepCreate.json"))
                                .when()
                                .post(ApiCallHelper.getProperty("updateloadgroup") + paoId)
                                .then()
                                .extract()
                                .response();
        paoId = response.path("groupId").toString();
        assertTrue("PAO ID should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @Test(dependsOnMethods = { "Test_LmDigiSep_Update" })
    public void Test_LmDigiSep_Copy() {
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", 
                                    requestFields(
                                    fieldWithPath("LOAD_GROUP_COPY.name").type(JsonFieldType.STRING).description("Load Group Copy Name")), 
                                    responseFields(fieldWithPath("groupId").type(JsonFieldType.NUMBER).description("Load Group Copy Id"))))
                                .accept("application/json")
                                .contentType("application/json")
                                .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                .body(ApiCallHelper.getInputFile("documentation\\loadgroup\\DigiSepCopy.json"))
                                .when()
                                .post(ApiCallHelper.getProperty("copyloadgroup")+ paoId)
                                .then()
                                .extract()
                                .response();
        copyPaoId = response.path("groupId").toString();
        assertTrue("PAO ID should not be Null", copyPaoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @Test(dependsOnMethods = { "Test_LmDigiSep_Copy" })
    public void Test_LmDigiSep_Delete() {
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
            requestFields(
                fieldWithPath("name").type(JsonFieldType.STRING).description("Load Group Name")), 
                responseFields(fieldWithPath("groupId").type(JsonFieldType.NUMBER).description("Load Group Id"))))
            .accept("application/json")
            .contentType("application/json")
            .header("Authorization","Bearer " + ApiCallHelper.authToken)
            .body(ApiCallHelper.getInputFile("documentation\\loadgroup\\DigiSepDelete.json"))
            .when()
            .delete(ApiCallHelper.getProperty("deleteloadgroup") + paoId)
            .then()
            .extract()
            .response();
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }
}
