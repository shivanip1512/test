package com.cannontech.rest.api.documentation.loadprogram;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.utilities.JsonFileReader;
import com.cannontech.rest.api.utilities.RestApiDocumentationUtility;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ItronProgramSetupApiControllerTest {

    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private Number programId = null;
    private Number copyProgramId = null;
    private FieldDescriptor[] itronGearFieldDescriptor = null;
    private List<FieldDescriptor> itronProgramFieldDescriptor = null;

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation, method);
        itronGearFieldDescriptor = new FieldDescriptor[] { fieldWithPath("gears[].fields.cycleType").type(JsonFieldType.STRING).description("Cycle Type"),
                fieldWithPath("gears[].fields.rampIn").type(JsonFieldType.BOOLEAN).description("Ramp In"),
                fieldWithPath("gears[].fields.rampOut").type(JsonFieldType.BOOLEAN).description("Ramp Out"),
                fieldWithPath("gears[].fields.dutyCyclePercent").type(JsonFieldType.NUMBER).description("Duty Cycle Percent. Min Value: 0, Max Value: 100"),
                fieldWithPath("gears[].fields.dutyCyclePeriodInMinutes").type(JsonFieldType.NUMBER).description("Duty Cycle Period."),
                fieldWithPath("gears[].fields.criticality").type(JsonFieldType.NUMBER).description("Criticality. Min Value: 0, Max Value: 255"),
                fieldWithPath("gears[].fields.capacityReduction").type(JsonFieldType.NUMBER).description("Capacity Reduction. Min Value: 0, Max Value: 100"),
                fieldWithPath("gears[].fields.howToStopControl").type(JsonFieldType.STRING).ignored().description("How To Stop Control"),
                fieldWithPath("gears[].fields.whenToChangeFields").type(JsonFieldType.OBJECT).description("Consists of When to change fields"),
                fieldWithPath("gears[].fields.whenToChangeFields.whenToChange").type(JsonFieldType.STRING)
                                                                               .description("When to change field Expected : None, Duration, Priority, TriggerOffset") };
        itronProgramFieldDescriptor = LoadProgramSetupHelper.mergeProgramFieldDescriptors(itronGearFieldDescriptor);
    }

    @AfterMethod
    public void tearDown() {
        this.restDocumentation.afterTest();
    }

    private JSONObject buildJSONRequest(ITestContext context, String jsonFileName) {
        JSONObject jsonObject = JsonFileReader.readJsonFileAsJSONObject(jsonFileName);
        JsonPath jsonPath = new JsonPath(jsonObject.toJSONString());
        context.setAttribute("loadProgramCopy", jsonPath.getString("name"));

        JSONObject jsonArrayObject = new JSONObject();
        jsonArrayObject.put("groupId", context.getAttribute("assignedLoadGroupId"));
        jsonArrayObject.put("groupName", context.getAttribute("loadGroupName"));
        jsonArrayObject.put("type", context.getAttribute("loadGroupType"));
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(jsonArrayObject);
        JSONObject constraintJson = (JSONObject) jsonObject.get("constraint");
        constraintJson.put("constraintId", context.getAttribute("constraintId"));
        jsonObject.put("constraint", constraintJson);
        jsonObject.put("assignedGroups", jsonArray);
        return jsonObject;
    }

    /**
     * Test case is to create Load group as we need to pass load group in request of Itron Load Program.
     */
    @Test
    public void itronAssignedLoadGroup_Create(ITestContext context) {
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", "documentation\\loadprogram\\ItronProgramAssignedLoadGroup.json");
        Integer groupId = createResponse.path("groupId");
        context.setAttribute("loadGroupId", groupId.toString());
        context.setAttribute("assignedLoadGroupId", groupId);

        JSONObject jsonObject = JsonFileReader.readJsonFileAsJSONObject("documentation\\loadprogram\\ItronProgramAssignedLoadGroup.json");
        JsonPath jsonPath = new JsonPath(jsonObject.toJSONString());
        context.setAttribute("loadGroupName", jsonPath.getString("LM_GROUP_ITRON.name"));
        context.setAttribute("loadGroupType", jsonPath.getString("LM_GROUP_ITRON.type"));
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
    }

    /**
     * Test case is to create Program Constraint as we need to pass constraint in request of Itron Load Program.
     */
    @Test(dependsOnMethods={"itronAssignedLoadGroup_Create"})
    public void programConstraint_Create(ITestContext context) {
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createProgramConstraint", "documentation\\loadprogram\\LoadProgramAssignedConstraint.json");
        Integer constraintId = createResponse.path("id");
        context.setAttribute("constraintId", constraintId);

        JSONObject jsonObject = JsonFileReader.readJsonFileAsJSONObject("documentation\\loadprogram\\LoadProgramAssignedConstraint.json");
        JsonPath jsonPath = new JsonPath(jsonObject.toJSONString());
        context.setAttribute("constraintName", jsonPath.getString("name"));

        assertTrue("Constraint ID should not be Null", constraintId != null);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
    }

    /**
     * Test case is to create Itron Load Program and to generate Rest api documentation for Load Program create request.
     * @throws IOException
     */
    @Test(dependsOnMethods={"programConstraint_Create"})
    public void Test_ItronProgram_Create(ITestContext context) throws IOException {
        JSONObject jsonObject = buildJSONRequest(context, "documentation\\loadprogram\\ItronProgramCreate.json");
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     requestFields(itronProgramFieldDescriptor),
                                                                     responseFields(LoadProgramSetupHelper.responseFieldDescriptor())))
                                                    .accept("application/json")
                                                    .contentType("application/json")
                                                    .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                                    .body(jsonObject.toJSONString())
                                                    .when()
                                                    .post(ApiCallHelper.getProperty("saveLoadProgram"))
                                                    .then()
                                                    .extract()
                                                    .response();

        programId = response.path("programId");
        assertTrue("PAO ID should not be Null", programId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to get Itron Load Program created by test case Test_ItronProgram_Create and to generate Rest api
     * documentation for get request.
     */
    @Test(dependsOnMethods={"Test_ItronProgram_Create"})
    public void Test_ItronProgram_Get() {
        List<FieldDescriptor> list = LoadProgramSetupHelper.createFieldDescriptorForGet(itronGearFieldDescriptor, 27);
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}", responseFields(list)))
                                                    .accept("application/json")
                                                    .contentType("application/json")
                                                    .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                                    .when()
                                                    .get(ApiCallHelper.getProperty("getLoadProgram") + programId)
                                                    .then()
                                                    .extract()
                                                    .response();

        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to update Load Program created by test case Test_ItronProgram_Create and to generate Rest api
     * documentation for Update request.
     */
    @Test(dependsOnMethods={"Test_ItronProgram_Get"})
    public void Test_ItronProgram_Update(ITestContext context) {
        JSONObject jsonObject = buildJSONRequest(context, "documentation\\loadprogram\\ItronProgramCreate.json");
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     requestFields(itronProgramFieldDescriptor),
                                                                     responseFields(LoadProgramSetupHelper.responseFieldDescriptor())))
                                                    .accept("application/json")
                                                    .contentType("application/json")
                                                    .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                                    .body(jsonObject.toJSONString())
                                                    .when()
                                                    .post(ApiCallHelper.getProperty("updateLoadProgram") + programId)
                                                    .then()
                                                    .extract()
                                                    .response();

        programId = response.path("programId");
        assertTrue("PAO ID should not be Null", programId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to create copy of Ecobee Load Program created by test case Test_ItronProgram_Create and to generate
     * Rest api documentation for copy request.
     */
    @Test(dependsOnMethods={"Test_ItronProgram_Update"})
    public void Test_ItronProgram_Copy(ITestContext context) {
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     requestFields(LoadProgramSetupHelper.fieldDescriptorForCopy()),
                                                                     responseFields(LoadProgramSetupHelper.responseFieldDescriptor())))
                                                    .accept("application/json")
                                                    .contentType("application/json")
                                                    .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                                    .body(ApiCallHelper.getInputFile("documentation\\loadprogram\\ItronProgramCopy.json"))
                                                    .when()
                                                    .post(ApiCallHelper.getProperty("copyLoadProgram") + programId)
                                                    .then()
                                                    .extract()
                                                    .response();

        copyProgramId = response.path("programId");
        String updatedPaoId = copyProgramId.toString();
        assertTrue("PAO ID should not be Null", updatedPaoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to delete the Itron Load Program created by test case Test_ItronProgram_Copy and to generate Rest
     * api documentation for delete request.
     */
    @Test(dependsOnMethods={"Test_ItronProgram_Copy"})
    public void Test_ItronCopyProgram_Delete() {
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     requestFields(LoadProgramSetupHelper.requestFieldDesriptorForDelete()),
                                                                     responseFields(LoadProgramSetupHelper.responseFieldDescriptor())))
                                                    .accept("application/json")
                                                    .contentType("application/json")
                                                    .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                                    .body(ApiCallHelper.getInputFile("documentation\\loadprogram\\ItronCopyProgramDelete.json"))
                                                    .when()
                                                    .delete(ApiCallHelper.getProperty("deleteLoadProgram") + copyProgramId)
                                                    .then()
                                                    .extract()
                                                    .response();

        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to delete the Itron Load Program created by test case Test_ItronProgram_Create and to generate Rest
     * api documentation for delete request.
     */
    @Test(dependsOnMethods={"Test_ItronProgram_Copy"})
    public void Test_ItronProgram_Delete() {
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     requestFields(LoadProgramSetupHelper.requestFieldDesriptorForDelete()),
                                                                     responseFields(LoadProgramSetupHelper.responseFieldDescriptor())))
                                                    .accept("application/json")
                                                    .contentType("application/json")
                                                    .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                                    .body(ApiCallHelper.getInputFile("documentation\\loadprogram\\ItronProgramDelete.json"))
                                                    .when()
                                                    .delete(ApiCallHelper.getProperty("deleteLoadProgram") + programId)
                                                    .then()
                                                    .extract()
                                                    .response();

        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to Delete Constraint which have been created for Load Program.
     */
    @Test(dependsOnMethods={"Test_ItronProgram_Delete"})
    public void programConstraint_Delete(ITestContext context) {
        JSONObject payload = JsonFileReader.updateJsonFile("documentation\\loadprogram\\LoadProgramAssignedConstraintDelete.json",
                                                           "name",
                                                           context.getAttribute("constraintName").toString());

        ExtractableResponse<?> response = ApiCallHelper.delete("deleteProgramConstraint", payload, context.getAttribute("constraintId").toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to Delete Load group we have created for Load Program.
     */
    @Test(dependsOnMethods={"programConstraint_Delete"})
    public void assignedLoadGroup_Delete(ITestContext context) {
        JSONObject payload = JsonFileReader.updateJsonFile("documentation\\loadprogram\\ItronProgramAssignedLoadGroupDelete.json",
                                                           "name",
                                                           context.getAttribute("loadGroupName").toString());

        ExtractableResponse<?> response = ApiCallHelper.delete("deleteloadgroup", payload, context.getAttribute("loadGroupId").toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

}
