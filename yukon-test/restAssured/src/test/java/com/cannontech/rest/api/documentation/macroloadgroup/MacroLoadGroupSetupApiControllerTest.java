package com.cannontech.rest.api.documentation.macroloadgroup;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

import java.io.IOException;
import java.lang.reflect.Method;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.utilities.JsonFileReader;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class MacroLoadGroupSetupApiControllerTest {
    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private String paoId = null;
    private final static String macroAssignedLoadGroupPayloadfile = "documentation\\macroloadgroup\\lmMacroAssignedLoadgroupCreate.json";

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = new RequestSpecBuilder().addFilter(
                                 documentationConfiguration(this.restDocumentation).operationPreprocessors().withRequestDefaults(
                                 removeHeaders("Authorization"), removeHeaders("Accept"), removeHeaders("Host"),
                                 removeHeaders("Content-Type"), removeHeaders("Content-Length"), prettyPrint()).withResponseDefaults(
                                 removeHeaders("X-Frame-Options"), removeHeaders("X-Content-Type-Options"),
                                 removeHeaders("Content-Security-Policy"), removeHeaders("Strict-Transport-Security"),
                                 removeHeaders("X-XSS-Protection"), removeHeaders("Content-Length"), removeHeaders("Content-Type"),
                                 removeHeaders("Date"), prettyPrint()).and().snippets().withDefaults(HttpDocumentation.httpRequest(),
                                 HttpDocumentation.httpResponse())).build();
    }

    @AfterMethod
    public void tearDown() {
        this.restDocumentation.afterTest();
    }

    /**
     * Test case is to create Load group
     * as we need to pass load group on request of macro loadgroup.
     */
    @Test(priority = 1)
    public void assignedLoadGroup_Create(ITestContext context) {
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", macroAssignedLoadGroupPayloadfile);
        Integer groupId = createResponse.path("groupId");
        context.setAttribute("macroLoadGroupId", groupId.toString());
        context.setAttribute("assignedLoadGroupId", groupId);
        
        JSONObject jsonObject = JsonFileReader.readJsonFileAsJSONObject(macroAssignedLoadGroupPayloadfile);
        JsonPath jsonPath = new JsonPath(jsonObject.toJSONString());
        context.setAttribute("macroLoadGroupName", jsonPath.getString("LM_GROUP_METER_DISCONNECT.name"));
        
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
    }

    /**
     * Test case is to Delete Load group
     * we have created for macro laodgroup.
     */
    @Test(priority = 7)
    public void assignedLoadGroup_Delete(ITestContext context) {
        JSONObject payload =
            JsonFileReader.updateJsonFile("documentation\\macroloadgroup\\lmMacroAssignedLoadGroupDelete.json", "name",
                context.getAttribute("macroLoadGroupName").toString());

        ExtractableResponse<?> response =
            ApiCallHelper.delete("deleteloadgroup", payload, context.getAttribute("macroLoadGroupId").toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    private JSONObject buildCreateRequestJSON(ITestContext context) {
        JSONObject jsonObject =
            JsonFileReader.readJsonFileAsJSONObject("documentation\\macroloadgroup\\lmMacroLoadGroupCreate.json");
        JsonPath jsonPath = new JsonPath(jsonObject.toJSONString());
        context.setAttribute("macroLoadGroupCopy", jsonPath.getString("LOAD_GROUP_COPY.name"));

        JSONObject jsonArrayObject = new JSONObject();
        jsonArrayObject.put("id", context.getAttribute("assignedLoadGroupId"));
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(jsonArrayObject);
        jsonObject.put("assignedLoadGroups", jsonArray);
        return jsonObject;
    }

    /**
     * Test case is to create Macro Load group
     * and to generate Rest api documentation for macro loadgroup create request.
     * @throws IOException 
     */
    @Test(priority = 2)
    public void Test_MacroLoadGroup_Create(ITestContext context) throws IOException {
        JSONObject jsonObject = buildCreateRequestJSON(context);
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", 
                                    requestFields(
                                    fieldWithPath("name").type(JsonFieldType.STRING).description("Macro Load Group Name"), 
                                    fieldWithPath("type").type(JsonFieldType.STRING).description("Macro Load Group Type"),
                                    fieldWithPath("assignedLoadGroups[].id").type(JsonFieldType.NUMBER).description("Assigned Load Group Id")),
                                    responseFields(fieldWithPath("paoId").type(JsonFieldType.NUMBER).description("Macro Load Group Id"))))
                                    .accept("application/json")
                                    .contentType("application/json")
                                    .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                    .body(jsonObject.toJSONString())
                                    .when()
                                    .post(ApiCallHelper.getProperty("saveMacroLoadGroup"))
                                    .then()
                                    .extract()
                                    .response();

        paoId = response.path("paoId").toString();
        assertTrue("PAO ID should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to  update Macro Load group created by test case Test_MacroLoadGroup_Create
     * and to generate Rest api documentation for update request.
     */
    @Test(priority = 3)
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
                                    .get(ApiCallHelper.getProperty("getMacroloadgroup") + paoId)
                                    .then()
                                    .extract()
                                    .response();
    
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to  update Macro Load group created by test case Test_MacroLoadGroup_Create
     * and to generate Rest api documentation for update request.
     */
    @Test(priority=4)
    public void Test_MacroLoadGroup_Update(ITestContext context) {
        JSONObject jsonObject = buildCreateRequestJSON(context);
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", 
                                    requestFields(
                                    fieldWithPath("name").type(JsonFieldType.STRING).description("Macro Load Group Name"), 
                                    fieldWithPath("type").type(JsonFieldType.STRING).description("Macro Load Group Type"),
                                    fieldWithPath("assignedLoadGroups[].id").type(JsonFieldType.NUMBER).description("Assigned Load Group Id")),
                                    responseFields(fieldWithPath("paoId").type(JsonFieldType.NUMBER).description("Macro Load Group Id"))))
                                   .accept("application/json")
                                   .contentType("application/json")
                                   .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                   .body(jsonObject.toJSONString())
                                   .when()
                                   .post(ApiCallHelper.getProperty("updateMacroLoadGroup")+ paoId)
                                   .then()
                                   .extract()
                                   .response();

        paoId = response.path("paoId").toString();
        assertTrue("PAO ID should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to  create copy of Macro Load group created by test case Test_MacroLoadGroup_Create
     * and to generate Rest api documentation for copy request.
     */
    @Test(priority = 5)
    public void Test_MacroLoadGroup_Copy(ITestContext context) {
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", 
                                    requestFields(
                                    fieldWithPath("LOAD_GROUP_COPY.name").type(JsonFieldType.STRING).description("Macro Load Group Copy Name")), 
                                    responseFields(fieldWithPath("paoId").type(JsonFieldType.NUMBER).description("Macro Load Group Copy Id"))))
                                    .accept("application/json")
                                    .contentType("application/json")
                                    .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                    .body(ApiCallHelper.getInputFile("documentation\\macroloadgroup\\MacroLoadGroupCopy.json"))
                                    .when()
                                    .post(ApiCallHelper.getProperty("copyMacroLoadGroup")+ paoId)
                                    .then()
                                    .extract()
                                    .response();

        JSONObject jsonObject = JsonFileReader.readJsonFileAsJSONObject("documentation\\macroloadgroup\\MacroLoadGroupCopy.json");
        JsonPath jsonPath = new JsonPath(jsonObject.toJSONString());
        context.setAttribute("macroLoadGroupCopy", jsonPath.getString("LOAD_GROUP_COPY.name"));
        String updatedPaoId = response.path("paoId").toString();
        context.setAttribute("copyPaoId", updatedPaoId);
        assertTrue("PAO ID should not be Null", updatedPaoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to delete the Macro Load group copy created by test case Test_MacroLoadGroup_Copy
     */
    @Test(priority = 5)
    public void macroLoadGroupCopy_Delete(ITestContext context) {
        JSONObject payload =
            JsonFileReader.updateJsonFile("documentation\\macroloadgroup\\MacroLoadGroupCopyDelete.json", "name",
                context.getAttribute("macroLoadGroupCopy").toString());

        ExtractableResponse<?> response =
            ApiCallHelper.delete("deleteMacroLoadGroup", payload, context.getAttribute("copyPaoId").toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to delete the Macro Load group created by test case Test_MacroLoadGroup_Create
     * and to generate Rest api documentation for delete request.
     */
    @Test(priority=6)
    public void Test_MacroLoadGroup_Delete() {
        Response response = given(documentationSpec)
                               .filter(document("{ClassName}/{methodName}",
                               requestFields(
                               fieldWithPath("name").type(JsonFieldType.STRING).description("Macro Load Group Name")), 
                               responseFields(fieldWithPath("paoId").type(JsonFieldType.NUMBER).description("Macro Load Group Id"))))
                               .accept("application/json")
                               .contentType("application/json")
                               .header("Authorization","Bearer " + ApiCallHelper.authToken)
                               .body(ApiCallHelper.getInputFile("documentation\\macroloadgroup\\lmMacroLoadGroupDelete.json"))
                               .when()
                               .delete(ApiCallHelper.getProperty("deleteMacroLoadGroup") + paoId)
                               .then()
                               .extract()
                               .response();
     
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

}