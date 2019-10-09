package com.cannontech.rest.api.documentation.macroloadgroup;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockLMPaoDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.dr.loadgroup.LoadGroupHelper;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupCopy;
import com.cannontech.rest.api.loadgroup.request.MockMacroLoadGroup;
import com.cannontech.rest.api.utilities.RestApiDocumentationUtility;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class MacroLoadGroupSetupApiControllerTest {
    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private Integer macroGroupId = null;
    private MockLoadGroupBase loadGroup = null;

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation,method);
        loadGroup = LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_METER_DISCONNECT);
    }

    @AfterMethod
    public void tearDown() {
        this.restDocumentation.afterTest();
    }

    /**
     * Test case is to create Load group
     * as we need to pass load group on request of macro load group.
     */
    @Test
    public void AssignedLoadGroup_Create(ITestContext context) {
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        Integer loadGroupId = createResponse.path("groupId");
        context.setAttribute("loadGroupId", loadGroupId);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
    }

    private MockMacroLoadGroup buildMacroLoadGroupCreateRequest(ITestContext context) {
        
        Integer loadGroupId =  (Integer) context.getAttribute("loadGroupId");
        List<MockLMPaoDto> assignedLoadGroups = new ArrayList<>();
        MockLMPaoDto assignedLoadGroup = MockLMPaoDto.builder()
                                    .id(loadGroupId)
                                    .name(loadGroup.getName())
                                    .type(loadGroup.getType())
                                    .build();

        assignedLoadGroups.add(assignedLoadGroup);
        MockMacroLoadGroup macroLoadGroup = MockMacroLoadGroup.builder()
                                                      .name("Macro_Load_Group_Test")
                                                      .type(MockPaoType.MACRO_GROUP)
                                                      .assignedLoadGroups(assignedLoadGroups)
                                                      .build();
        return macroLoadGroup;
    }

    /**
     * Test case is to create Macro Load group
     * and to generate Rest api documentation for macro loadgroup create request.
     * @throws IOException 
     */
    @Test(dependsOnMethods = { "AssignedLoadGroup_Create" })
    public void Test_MacroLoadGroup_Create(ITestContext context) throws IOException {
        MockMacroLoadGroup macroLoadGroup = buildMacroLoadGroupCreateRequest(context);
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", 
                                    requestFields(
                                    fieldWithPath("name").type(JsonFieldType.STRING).description("Macro Load Group Name"), 
                                    fieldWithPath("type").type(JsonFieldType.STRING).description("Macro Load Group Type"),
                                    fieldWithPath("assignedLoadGroups[].id").type(JsonFieldType.NUMBER).description("Assigned Load Group Id"),
                                    fieldWithPath("assignedLoadGroups[].name").type(JsonFieldType.STRING).description("Assigned Load Group Name").optional(),
                                    fieldWithPath("assignedLoadGroups[].type").type(JsonFieldType.STRING).description("Assigned  Load Group Type").optional()),
                                    responseFields(fieldWithPath("paoId").type(JsonFieldType.NUMBER).description("Macro Load Group Id"))))
                                .accept("application/json")
                                .contentType("application/json")
                                .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                .body(macroLoadGroup)
                                .when()
                                .post(ApiCallHelper.getProperty("saveMacroLoadGroup"))
                                .then()
                                .extract()
                                .response();

        macroGroupId = response.path("paoId");
        assertTrue("PAO ID should not be Null", macroGroupId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to  update Macro Load group created by test case Test_MacroLoadGroup_Create
     * and to generate Rest api documentation for update request.
     */
    @Test(dependsOnMethods = { "Test_MacroLoadGroup_Create" })
    public void Test_MacroLoadGroup_Get() {
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}",
                                    responseFields(
                                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("Macro Load Group Id"), 
                                    fieldWithPath("name").type(JsonFieldType.STRING).description("Macro Load Group Name"),
                                    fieldWithPath("type").type(JsonFieldType.STRING).description("Macro Load Group Type"),
                                    fieldWithPath("assignedLoadGroups[].id").type(JsonFieldType.NUMBER).description("Assigned Load Group Id"),
                                    fieldWithPath("assignedLoadGroups[].name").type(JsonFieldType.STRING).description("Assigned Load Group Name"),
                                    fieldWithPath("assignedLoadGroups[].type").type(JsonFieldType.STRING).description("Assigned  Load Group Type"))))
                                .accept("application/json")
                                .contentType("application/json")
                                .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                .when()
                                .get(ApiCallHelper.getProperty("getMacroloadgroup") + macroGroupId)
                                .then()
                                .extract()
                                .response();
    
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to  update Macro Load group created by test case Test_MacroLoadGroup_Create
     * and to generate Rest api documentation for update request.
     */
    @Test(dependsOnMethods = { "Test_MacroLoadGroup_Get" })
    public void Test_MacroLoadGroup_Update(ITestContext context) {
        MockMacroLoadGroup macroLoadGroup = buildMacroLoadGroupCreateRequest(context);
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", 
                                    requestFields(
                                    fieldWithPath("name").type(JsonFieldType.STRING).description("Macro Load Group Name"), 
                                    fieldWithPath("type").type(JsonFieldType.STRING).description("Macro Load Group Type"),
                                    fieldWithPath("assignedLoadGroups[].name").type(JsonFieldType.STRING).description("Assigned Load Group Name").optional(),
                                    fieldWithPath("assignedLoadGroups[].type").type(JsonFieldType.STRING).description("Assigned  Load Group Type").optional(),
                                    fieldWithPath("assignedLoadGroups[].id").type(JsonFieldType.NUMBER).description("Assigned Load Group Id")),
                                    responseFields(fieldWithPath("paoId").type(JsonFieldType.NUMBER).description("Macro Load Group Id"))))
                               .accept("application/json")
                               .contentType("application/json")
                               .header("Authorization","Bearer " + ApiCallHelper.authToken)
                               .body(macroLoadGroup)
                               .when()
                               .post(ApiCallHelper.getProperty("updateMacroLoadGroup")+ macroGroupId)
                               .then()
                               .extract()
                               .response();

        macroGroupId = response.path("paoId");
        assertTrue("Macro Load GroupId should not be Null", macroGroupId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to  create copy of Macro Load group created by test case Test_MacroLoadGroup_Create
     * and to generate Rest api documentation for copy request.
     */
    @Test(dependsOnMethods = { "Test_MacroLoadGroup_Update" })
    public void Test_MacroLoadGroup_Copy(ITestContext context) {
        MockLoadGroupCopy loadGroupCopy = MockLoadGroupCopy.builder().name("Macro-LoadGroup-Copy-Test").build();
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", 
                                    requestFields(
                                    fieldWithPath("LOAD_GROUP_COPY.name").type(JsonFieldType.STRING).description("Macro Load Group Copy Name")), 
                                    responseFields(fieldWithPath("paoId").type(JsonFieldType.NUMBER).description("Macro Load Group Copy Id"))))
                                .accept("application/json")
                                .contentType("application/json")
                                .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                .body(loadGroupCopy)
                                .when()
                                .post(ApiCallHelper.getProperty("copyMacroLoadGroup")+ macroGroupId)
                                .then()
                                .extract()
                                .response();

        context.setAttribute("macroLoadGroupCopy", loadGroupCopy.getName());
        String copiedMacroLoadGroupPaoId = response.path("paoId").toString();
        context.setAttribute("copiedMacroLoadGroupPaoId", copiedMacroLoadGroupPaoId);
        assertTrue("Copied Macro Load Group should not be Null", copiedMacroLoadGroupPaoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to delete the Macro Load group copy created by test case Test_MacroLoadGroup_Copy
     */
    @Test(dependsOnMethods = { "Test_MacroLoadGroup_Copy" })
    public void MacroLoadGroupCopy_Delete(ITestContext context) {
        MockLMDto lmDeleteObject = MockLMDto.builder()
                                    .name(context.getAttribute("macroLoadGroupCopy").toString())
                                    .build();
        
        ExtractableResponse<?> response =
            ApiCallHelper.delete("deleteMacroLoadGroup", lmDeleteObject, context.getAttribute("copiedMacroLoadGroupPaoId").toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to delete the Macro Load group created by test case Test_MacroLoadGroup_Create
     * and to generate Rest api documentation for delete request.
     */
    @Test(dependsOnMethods = { "MacroLoadGroupCopy_Delete" })
    public void Test_MacroLoadGroup_Delete(ITestContext context) {
        MockLMDto lmDeleteMacroLoadGroupObject = MockLMDto.builder().name("Macro_Load_Group_Test").build();
        
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}",
                                    requestFields(
                                    fieldWithPath("name").type(JsonFieldType.STRING).description("Macro Load Group Name")), 
                                    responseFields(fieldWithPath("paoId").type(JsonFieldType.NUMBER).description("Macro Load Group Id"))))
                                .accept("application/json")
                                .contentType("application/json")
                                .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                .body(lmDeleteMacroLoadGroupObject)
                                .when()
                                .delete(ApiCallHelper.getProperty("deleteMacroLoadGroup") + macroGroupId)
                                .then()
                                .extract()
                                .response();
     
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }
    
    /**
     * Test case is to Delete Load group we have created for macro laodgroup.
     */
    @Test(dependsOnMethods = { "Test_MacroLoadGroup_Delete" })
    public void assignedLoadGroup_Delete(ITestContext context) {

        MockLMDto lmDeleteObject = MockLMDto.builder().name(loadGroup.getName()).build();
        
        Integer loadGroupId =  (Integer) context.getAttribute("loadGroupId");
        ExtractableResponse<?> response = ApiCallHelper.delete("deleteloadgroup", lmDeleteObject, loadGroupId.toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }


}