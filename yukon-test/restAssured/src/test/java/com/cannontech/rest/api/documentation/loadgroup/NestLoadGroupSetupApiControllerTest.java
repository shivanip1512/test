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

public class NestLoadGroupSetupApiControllerTest {

    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private String paoId = null;
    private FieldDescriptor[] nestFieldDescriptor = null;

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation,method);
        nestFieldDescriptor = new FieldDescriptor[] {
            fieldWithPath("LM_GROUP_NEST.name").type(JsonFieldType.STRING).description("Load Group Name"), 
            fieldWithPath("LM_GROUP_NEST.type").type(JsonFieldType.STRING).description("Load Group Type"),
            fieldWithPath("LM_GROUP_NEST.kWCapacity").type(JsonFieldType.NUMBER).description("KW Capacity"),
            fieldWithPath("LM_GROUP_NEST.disableGroup").type(JsonFieldType.BOOLEAN).description("Flag to disable Group"),
            fieldWithPath("LM_GROUP_NEST.disableControl").type(JsonFieldType.BOOLEAN).description("Flag to disable Control")
        };
    }

    @AfterMethod
    public void tearDown() {
        this.restDocumentation.afterTest();
    }

    @Test(priority=1)
    public void Test_LmNest_Create() {
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", requestFields(nestFieldDescriptor),
                                    responseFields(fieldWithPath("groupId").type(JsonFieldType.NUMBER).description("Load Group Id"))))
                                .accept("application/json")
                                .contentType("application/json")
                                .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                .body(ApiCallHelper.getInputFile("documentation\\loadgroup\\NestCreate.json"))
                                .when()
                                .post(ApiCallHelper.getProperty("saveloadgroup"))
                                .then()
                                .extract()
                                .response();
        paoId = response.path("groupId").toString();
        assertTrue("PAO ID should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

   @Test(priority=2)
    public void Test_LmNest_Get() {
       List<FieldDescriptor> responseFields = new ArrayList<>(Arrays.asList(nestFieldDescriptor));
       responseFields.add(0,fieldWithPath("LM_GROUP_NEST.id").type(JsonFieldType.NUMBER).description("Load Group Id"));
        Response response = given(documentationSpec)
                                 .filter(document("{ClassName}/{methodName}", responseFields(responseFields)))
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

   @Test(priority=3)
   public void Test_LmNest_Update() {
       Response response = given(documentationSpec)
                               .filter(document("{ClassName}/{methodName}", requestFields(nestFieldDescriptor),
                                   responseFields(fieldWithPath("groupId").type(JsonFieldType.NUMBER).description("Load Group Id"))))
                               .accept("application/json")
                               .contentType("application/json")
                               .header("Authorization","Bearer " + ApiCallHelper.authToken)
                               .body(ApiCallHelper.getInputFile("documentation\\loadgroup\\NestCreate.json"))
                               .when()
                               .post(ApiCallHelper.getProperty("updateloadgroup") + paoId)
                               .then()
                               .extract()
                               .response();
       paoId = response.path("groupId").toString();
       assertTrue("PAO ID should not be Null", paoId != null);
       assertTrue("Status code should be 200", response.statusCode() == 200);
   }

   @Test(priority=4)
   public void Test_LmNest_Delete() {
       Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
           requestFields(
               fieldWithPath("name").type(JsonFieldType.STRING).description("Load Group Name")), 
               responseFields(fieldWithPath("groupId").type(JsonFieldType.NUMBER).description("Load Group Id"))))
           .accept("application/json")
           .contentType("application/json")
           .header("Authorization","Bearer " + ApiCallHelper.authToken)
           .body(ApiCallHelper.getInputFile("documentation\\loadgroup\\NestDelete.json"))
           .when()
           .delete(ApiCallHelper.getProperty("deleteloadgroup") + paoId)
           .then()
           .extract()
           .response();
       assertTrue("Status code should be 200", response.statusCode() == 200);
   }
}
