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
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.dr.loadgroup.LoadGroupHelper;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.utilities.RestApiDocumentationUtility;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class HoneywellLoadGroupSetupApiControllerTest {

    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private String paoId = null;
    private FieldDescriptor[] HoneywellFieldDescriptor = null;
    private MockLoadGroupBase loadGroup = null;

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation,method);
        HoneywellFieldDescriptor = new FieldDescriptor[] {
            fieldWithPath("LM_GROUP_HONEYWELL.name").type(JsonFieldType.STRING).description("Load Group Name"), 
            fieldWithPath("LM_GROUP_HONEYWELL.type").type(JsonFieldType.STRING).description("Load Group Type"),
            fieldWithPath("LM_GROUP_HONEYWELL.kWCapacity").type(JsonFieldType.NUMBER).description("KW Capacity"),
            fieldWithPath("LM_GROUP_HONEYWELL.disableGroup").type(JsonFieldType.BOOLEAN).description("Flag to disable Group"),
            fieldWithPath("LM_GROUP_HONEYWELL.disableControl").type(JsonFieldType.BOOLEAN).description("Flag to disable Control")
        };
        loadGroup = LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_HONEYWELL);
    }

    @AfterMethod
    public void tearDown() {
        this.restDocumentation.afterTest();
    }

    @Test
    public void Test_LmHoneywell_Create() {
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", requestFields(HoneywellFieldDescriptor),
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

    @Test(dependsOnMethods = { "Test_LmHoneywell_Create" })
    public void Test_LmHoneywell_Get() {
       List<FieldDescriptor> responseFields = new ArrayList<>(Arrays.asList(HoneywellFieldDescriptor));
       responseFields.add(0,fieldWithPath("LM_GROUP_HONEYWELL.id").type(JsonFieldType.NUMBER).description("Load Group Id"));
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
   
   @Test(dependsOnMethods = { "Test_LmHoneywell_Get" })
   public void Test_LmHoneywell_Update() {
       Response response = given(documentationSpec)
                               .filter(document("{ClassName}/{methodName}", requestFields(HoneywellFieldDescriptor),
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

   @Test(dependsOnMethods = { "Test_LmHoneywell_Update" })
   public void Test_LmHoneywell_Delete() {
       MockLMDto lmDeleteObject = MockLMDto.builder()
               .name("HoneyWell_Group_Test")
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
   }
}
