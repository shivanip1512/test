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
import com.cannontech.rest.api.dr.loadgroup.LoadGroupHelper;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupCopy;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupEmetcon;
import com.cannontech.rest.api.utilities.Log;
import com.cannontech.rest.api.utilities.RestApiDocumentationUtility;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class EmetconLoadGroupSetupApiControllerTest {
    
    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private String paoId = null;
    private String copyPaoId = null;
    private FieldDescriptor[] emetconFieldDescriptor = null;
    private MockLoadGroupEmetcon loadGroup = null;
    
    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation,method);
        emetconFieldDescriptor = new FieldDescriptor[] {
            fieldWithPath("LM_GROUP_EMETCON.name").type(JsonFieldType.STRING).description("Load Group Name"), 
            fieldWithPath("LM_GROUP_EMETCON.type").type(JsonFieldType.STRING).description("Load Group Type"),
            fieldWithPath("LM_GROUP_EMETCON.kWCapacity").type(JsonFieldType.NUMBER).description("KW Capacity"),
            fieldWithPath("LM_GROUP_EMETCON.disableGroup").type(JsonFieldType.BOOLEAN).description("Flag to disable Group"),
            fieldWithPath("LM_GROUP_EMETCON.disableControl").type(JsonFieldType.BOOLEAN).description("Flag to disable Control"),
            fieldWithPath("LM_GROUP_EMETCON.routeId").type(JsonFieldType.NUMBER).description("Route Id"),
            fieldWithPath("LM_GROUP_EMETCON.routeName").type(JsonFieldType.STRING).description("Route Name").optional(),
            fieldWithPath("LM_GROUP_EMETCON.addressUsage").type(JsonFieldType.STRING).description("Address Usage. Must have G (for gold address), S (For silver address)"),
            fieldWithPath("LM_GROUP_EMETCON.relayUsage").type(JsonFieldType.STRING).description("Relay Usgae. Must have A, B, C or S (for All)"),
            fieldWithPath("LM_GROUP_EMETCON.goldAddress").type(JsonFieldType.NUMBER).description("Gold address value."),
            fieldWithPath("LM_GROUP_EMETCON.silverAddress").type(JsonFieldType.NUMBER).description("Silver address value.")
        };
        loadGroup = (MockLoadGroupEmetcon) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_EMETCON);
    }

    @AfterMethod
    public void tearDown() {
        this.restDocumentation.afterTest();
    }
    
    @Test
    public void Test_LmEmetcon_Create() {
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", requestFields(emetconFieldDescriptor),
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

    @Test(dependsOnMethods = { "Test_LmEmetcon_Create" })
    public void Test_LmEmetcon_Get() {
        List<FieldDescriptor> list = new ArrayList<>(Arrays.asList(emetconFieldDescriptor));
        list.add(0,fieldWithPath("LM_GROUP_EMETCON.id").type(JsonFieldType.NUMBER).description("Load Group Id"));
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", 
                                    responseFields(list)))
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

    @Test(dependsOnMethods = { "Test_LmEmetcon_Get" })
    public void Test_LmEmetcon_Update() {
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", requestFields(emetconFieldDescriptor),
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

    @Test(dependsOnMethods = { "Test_LmEmetcon_Update" })
    public void Test_LmEmetcon_Copy() {
        MockLoadGroupCopy loadGroupCopy = MockLoadGroupCopy.builder().name("Test-Emetcom-Copy-Test").routeId(12815).build();
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

    @Test(dependsOnMethods = { "Test_LmEmetcon_Copy" })
    public void Test_LmEmetcon_Delete() {
        MockLMDto lmDeleteObject = MockLMDto.builder()
                                    .name("Emetcon_LoadGroup_Test")
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

        MockLMDto lmDeleteCopyObject = MockLMDto.builder().name("Test-Emetcom-Copy-Test").build();
        Log.info("Delete payload is : " + JsonUtil.beautifyJson(lmDeleteCopyObject.toString()));
        ExtractableResponse<?> copyResponse = ApiCallHelper.delete("deleteloadgroup", lmDeleteCopyObject, copyPaoId);
        assertTrue("Status code should be 200", copyResponse.statusCode() == 200);
    }
}
