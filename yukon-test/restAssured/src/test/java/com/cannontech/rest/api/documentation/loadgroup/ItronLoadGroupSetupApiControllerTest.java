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

public class ItronLoadGroupSetupApiControllerTest {

    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private String paoId = null;
    private String copyPaoId = null;
    private FieldDescriptor[] itronFieldDescriptor = null;

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation,method);
        itronFieldDescriptor = new FieldDescriptor[] {
            fieldWithPath("LM_GROUP_ITRON.name").type(JsonFieldType.STRING).description("Load Group Name"), 
            fieldWithPath("LM_GROUP_ITRON.type").type(JsonFieldType.STRING).description("Load Group Type"),
            fieldWithPath("LM_GROUP_ITRON.kWCapacity").type(JsonFieldType.NUMBER).description("KW Capacity"),
            fieldWithPath("LM_GROUP_ITRON.disableGroup").type(JsonFieldType.BOOLEAN).description("Flag to disable Group"),
            fieldWithPath("LM_GROUP_ITRON.disableControl").type(JsonFieldType.BOOLEAN).description("Flag to disable Control"),
            fieldWithPath("LM_GROUP_ITRON.virtualRelayId").type(JsonFieldType.NUMBER).description("Flag to disable Control")
        };
    }

    @AfterMethod
    public void tearDown() {
        this.restDocumentation.afterTest();
    }

    @Test
    public void Test_LmItron_Create() {
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", requestFields(itronFieldDescriptor),
                                    responseFields(fieldWithPath("groupId").type(JsonFieldType.NUMBER).description("Load Group Id"))))
                                .accept("application/json")
                                .contentType("application/json")
                                .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                .body(ApiCallHelper.getInputFile("documentation\\loadgroup\\ItronCreate.json"))
                                .when()
                                .post(ApiCallHelper.getProperty("saveloadgroup"))
                                .then()
                                .extract()
                                .response();


        paoId = response.path("groupId").toString();
        assertTrue("PAO ID should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @Test(dependsOnMethods = { "Test_LmItron_Create" })
    public void Test_LmItron_Get() {
        List<FieldDescriptor> list = new ArrayList<>(Arrays.asList(itronFieldDescriptor));
        list.add(0,fieldWithPath("LM_GROUP_ITRON.id").type(JsonFieldType.NUMBER).description("Load Group Id"));
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

    @Test(dependsOnMethods = { "Test_LmItron_Get" })
    public void Test_LmItron_Update() {
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", requestFields(itronFieldDescriptor),
                                    responseFields(fieldWithPath("groupId").type(JsonFieldType.NUMBER).description("Load Group Id"))))
                                .accept("application/json")
                                .contentType("application/json")
                                .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                .body(ApiCallHelper.getInputFile("documentation\\loadgroup\\ItronCreate.json"))
                                .when()
                                .post(ApiCallHelper.getProperty("updateloadgroup") + paoId)
                                .then()
                                .extract()
                                .response();

        paoId = response.path("groupId").toString();
        assertTrue("PAO ID should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @Test(dependsOnMethods = { "Test_LmItron_Update" })
    public void Test_LmItron_Copy() {
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", 
                                    requestFields(
                                    fieldWithPath("LOAD_GROUP_COPY.name").type(JsonFieldType.STRING).description("Load Group Copy Name")), 
                                    responseFields(fieldWithPath("groupId").type(JsonFieldType.NUMBER).description("Load Group Copy Id"))))
                                .accept("application/json")
                                .contentType("application/json")
                                .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                .body(ApiCallHelper.getInputFile("documentation\\loadgroup\\ItronCopy.json"))
                                .when()
                                .post(ApiCallHelper.getProperty("copyloadgroup")+ paoId)
                                .then()
                                .extract()
                                .response();
        copyPaoId = response.path("groupId").toString();
        assertTrue("PAO ID should not be Null", copyPaoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @Test(dependsOnMethods = { "Test_LmItron_Copy" })
    public void Test_LmItron_Delete() {
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
            requestFields(
                fieldWithPath("name").type(JsonFieldType.STRING).description("Load Group Name")), 
                responseFields(fieldWithPath("groupId").type(JsonFieldType.NUMBER).description("Load Group Id"))))
            .accept("application/json")
            .contentType("application/json")
            .header("Authorization","Bearer " + ApiCallHelper.authToken)
            .body(ApiCallHelper.getInputFile("documentation\\loadgroup\\ItronDelete.json"))
            .when()
            .delete(ApiCallHelper.getProperty("deleteloadgroup") + paoId)
            .then()
            .extract()
            .response();
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }
}
