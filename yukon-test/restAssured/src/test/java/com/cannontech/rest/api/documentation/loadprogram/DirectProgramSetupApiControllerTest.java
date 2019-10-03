package com.cannontech.rest.api.documentation.loadprogram;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

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

public class DirectProgramSetupApiControllerTest {

    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private Number programId = null;
    private Number copyProgramId = null;

    private FieldDescriptor[] smartCycleGearFieldDescriptor = null;
    private List<FieldDescriptor> smartCycleProgramFieldDescriptor = null;

    private FieldDescriptor[] thermostatCycleGearFieldDescriptor = null;
    private List<FieldDescriptor> thermostatProgramFieldDescriptor = null;

    private FieldDescriptor[] simpleThermostatGearFieldDescriptor = null;
    private List<FieldDescriptor> simpleThermostatProgramFieldDescriptor = null;

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation, method);
        smartCycleGearFieldDescriptor = new FieldDescriptor[] { fieldWithPath("gears[].fields.noRamp").type(JsonFieldType.BOOLEAN).description("No Ramp field"),
                fieldWithPath("gears[].fields.controlPercent").type(JsonFieldType.NUMBER).description("Control percent. Min Value: 5, Max Value: 100"),
                fieldWithPath("gears[].fields.cyclePeriodInMinutes").type(JsonFieldType.NUMBER).description("Cycle period. Min Value: 1, Max Value: 945"),
                fieldWithPath("gears[].fields.cycleCountSendType").type(JsonFieldType.STRING).description("Cycle count send type"),
                fieldWithPath("gears[].fields.maxCycleCount").type(JsonFieldType.NUMBER).description("Maximum cycle count"),
                fieldWithPath("gears[].fields.startingPeriodCount").type(JsonFieldType.NUMBER).description("Starting period count"),
                fieldWithPath("gears[].fields.sendRate").type(JsonFieldType.NUMBER).description("Send rate"),
                fieldWithPath("gears[].fields.stopCommandRepeat").type(JsonFieldType.NUMBER).description("Stop command repeat. Min Value: 0, Max Value: 5"),
                fieldWithPath("gears[].fields.howToStopControl").type(JsonFieldType.STRING).description("How to stop control"),
                fieldWithPath("gears[].fields.capacityReduction").type(JsonFieldType.NUMBER).description("Group capacity reduction"),
                fieldWithPath("gears[].fields.whenToChangeFields").type(JsonFieldType.OBJECT).description("Consists of When to change fields"),
                fieldWithPath("gears[].fields.whenToChangeFields.whenToChange").type(JsonFieldType.STRING)
                                                                               .description("When to change field. Expected : None, Duration, Priority, TriggerOffset"), };
        smartCycleProgramFieldDescriptor = LoadProgramSetupHelper.mergeProgramFieldDescriptors(smartCycleGearFieldDescriptor);

        thermostatCycleGearFieldDescriptor = new FieldDescriptor[] {
                fieldWithPath("gears[].fields.absoluteOrDelta").type(JsonFieldType.STRING).description("Absolute or Delta."),
                fieldWithPath("gears[].fields.measureUnit").type(JsonFieldType.STRING).description("Measurement Unit."),
                fieldWithPath("gears[].fields.isHeatMode").type(JsonFieldType.BOOLEAN).description("Heat Mode."),
                fieldWithPath("gears[].fields.isCoolMode").type(JsonFieldType.BOOLEAN).description("Cool Mode."),
                fieldWithPath("gears[].fields.minValue").type(JsonFieldType.NUMBER).description("Min Value."),
                fieldWithPath("gears[].fields.maxValue").type(JsonFieldType.NUMBER).description("Max Value"),
                fieldWithPath("gears[].fields.valueB").type(JsonFieldType.NUMBER).description("Value B for absoluteOrDelta."),
                fieldWithPath("gears[].fields.valueD").type(JsonFieldType.NUMBER).description("Value D for absoluteOrDelta."),
                fieldWithPath("gears[].fields.valueF").type(JsonFieldType.NUMBER).description("Value F for absoluteOrDelta."),
                fieldWithPath("gears[].fields.random").type(JsonFieldType.NUMBER).description("Random."),
                fieldWithPath("gears[].fields.valueTa").type(JsonFieldType.NUMBER).description("Value Ta for absoluteOrDelta."),
                fieldWithPath("gears[].fields.valueTb").type(JsonFieldType.NUMBER).description("Value Tb for absoluteOrDelta."),
                fieldWithPath("gears[].fields.valueTc").type(JsonFieldType.NUMBER).description("Value Tc for absoluteOrDelta."),
                fieldWithPath("gears[].fields.valueTd").type(JsonFieldType.NUMBER).description("Value Td for absoluteOrDelta."),
                fieldWithPath("gears[].fields.valueTe").type(JsonFieldType.NUMBER).description("Value Te for absoluteOrDelta."),
                fieldWithPath("gears[].fields.valueTf").type(JsonFieldType.NUMBER).description("Value Tf for absoluteOrDelta."),
                fieldWithPath("gears[].fields.howToStopControl").type(JsonFieldType.STRING).description("How to stop control"),
                fieldWithPath("gears[].fields.capacityReduction").type(JsonFieldType.NUMBER).description("Group capacity reduction"),
                fieldWithPath("gears[].fields.whenToChangeFields").type(JsonFieldType.OBJECT).description("Consists of When to change fields"),
                fieldWithPath("gears[].fields.whenToChangeFields.whenToChange").type(JsonFieldType.STRING)
                                                                               .description("When to change field. Expected : None, Duration, Priority, TriggerOffset"), };
        thermostatProgramFieldDescriptor = LoadProgramSetupHelper.mergeProgramFieldDescriptors(thermostatCycleGearFieldDescriptor);

        simpleThermostatGearFieldDescriptor = new FieldDescriptor[] {
                fieldWithPath("gears[].fields.mode").type(JsonFieldType.STRING).description("Mode."),
                fieldWithPath("gears[].fields.randomStartTimeInMinutes").type(JsonFieldType.NUMBER).description("Random Start Time. Min Value: 0, Max Value: 120"),
                fieldWithPath("gears[].fields.preOpTemp").type(JsonFieldType.NUMBER).description("PreOp Temp. Min Value: -20, Max Value: 20"),
                fieldWithPath("gears[].fields.preOpTimeInMinutes").type(JsonFieldType.NUMBER).description("PreOp Time. Min Value: 0, Max Value: 300"),
                fieldWithPath("gears[].fields.preOpHoldInMinutes").type(JsonFieldType.NUMBER).description("PreOp Hold. Min Value: 0, Max Value: 300"),
                fieldWithPath("gears[].fields.rampPerHour").type(JsonFieldType.NUMBER).description("Ramp Per Hour. Min Value: -9.9, Max Value: 9.9"),
                fieldWithPath("gears[].fields.max").type(JsonFieldType.NUMBER).description("Max. Min Value: 0, Max Value: 20"),
                fieldWithPath("gears[].fields.rampOutTimeInMinutes").type(JsonFieldType.NUMBER).description("Ramp Out Time. Min Value: 0, Max Value: 300"),
                fieldWithPath("gears[].fields.maxRuntimeInMinutes").type(JsonFieldType.NUMBER).description("Max Runtime. Min Value: 240, Max Value: 1439"),
                fieldWithPath("gears[].fields.howToStopControl").type(JsonFieldType.STRING).description("How to stop control"),
                fieldWithPath("gears[].fields.whenToChangeFields").type(JsonFieldType.OBJECT).description("Consists of When to change fields"),
                fieldWithPath("gears[].fields.whenToChangeFields.whenToChange").type(JsonFieldType.STRING)
                                                                               .description("When to change field. Expected : None, Duration, Priority, TriggerOffset"), };
        simpleThermostatProgramFieldDescriptor = LoadProgramSetupHelper.mergeProgramFieldDescriptors(simpleThermostatGearFieldDescriptor);
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
     * Test case is to create Load group as we need to pass load group in request of Direct Load Program.
     */
    @Test(priority = 1)
    public void assignedLoadGroup_Create(ITestContext context) {
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", "documentation\\loadprogram\\DirectProgramAssignedLoadGroup.json");
        Integer groupId = createResponse.path("groupId");
        context.setAttribute("loadGroupId", groupId.toString());
        context.setAttribute("assignedLoadGroupId", groupId);

        JSONObject jsonObject = JsonFileReader.readJsonFileAsJSONObject("documentation\\loadprogram\\DirectProgramAssignedLoadGroup.json");
        JsonPath jsonPath = new JsonPath(jsonObject.toJSONString());
        context.setAttribute("loadGroupName", jsonPath.getString("LM_GROUP_METER_DISCONNECT.name"));
        context.setAttribute("loadGroupType", jsonPath.getString("LM_GROUP_METER_DISCONNECT.type"));
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
    }

    /**
     * Test case is to create Program Constraint as we need to pass constraint in request of Direct Load Program.
     */
    @Test(priority = 2)
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
     * Test case is to create Direct Load Program with and to generate Rest api documentation for Load Program create
     * request.
     */
    @Test(priority = 3)
    public void Test_ThermostatRampingProgram_Create(ITestContext context) {
        JSONObject jsonObject = buildJSONRequest(context, "documentation\\loadprogram\\ThermostatRampingProgramCreate.json");
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     requestFields(thermostatProgramFieldDescriptor),
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
     * Test case is to delete the Direct Load Program created by test case Test_ThermostatRampingProgram_Create and to
     * generate Rest api documentation for delete request.
     */
    @Test(priority = 4)
    public void Test_ThermostatRampingProgram_Delete() {
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     requestFields(LoadProgramSetupHelper.requestFieldDesriptorForDelete()),
                                                                     responseFields(LoadProgramSetupHelper.responseFieldDescriptor())))
                                                    .accept("application/json")
                                                    .contentType("application/json")
                                                    .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                                    .body(ApiCallHelper.getInputFile("documentation\\loadprogram\\ThermostatRampingProgramDelete.json"))
                                                    .when()
                                                    .delete(ApiCallHelper.getProperty("deleteLoadProgram") + programId)
                                                    .then()
                                                    .extract()
                                                    .response();

        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to create Direct Load Program with and to generate Rest api documentation for Load Program create
     * request.
     */
    @Test(priority = 5)
    public void Test_SimpleThermostatProgram_Create(ITestContext context) {
        JSONObject jsonObject = buildJSONRequest(context, "documentation\\loadprogram\\SimpleThermostatRampingProgramCreate.json");
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     requestFields(simpleThermostatProgramFieldDescriptor),
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
     * Test case is to delete the Direct Load Program created by test case Test_SimpleThermostatProgram_Create and to
     * generate Rest api documentation for delete request.
     */
    @Test(priority = 6)
    public void Test_SimpleThermostatProgram_Delete() {
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     requestFields(LoadProgramSetupHelper.requestFieldDesriptorForDelete()),
                                                                     responseFields(LoadProgramSetupHelper.responseFieldDescriptor())))
                                                    .accept("application/json")
                                                    .contentType("application/json")
                                                    .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                                    .body(ApiCallHelper.getInputFile("documentation\\loadprogram\\SimpleThermostatRampingProgramDelete.json"))
                                                    .when()
                                                    .delete(ApiCallHelper.getProperty("deleteLoadProgram") + programId)
                                                    .then()
                                                    .extract()
                                                    .response();

        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to create Direct Load Program and to generate Rest api documentation for Direct Load Program create
     * request.
     */
    @Test(priority = 7)
    public void Test_DirectProgram_Create(ITestContext context) {
        JSONObject jsonObject = buildJSONRequest(context, "documentation\\loadprogram\\DirectProgramCreate.json");
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     requestFields(smartCycleProgramFieldDescriptor),
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
     * Test case is to get Direct Load Program created by test case Test_DirectProgram_Create and to generate Rest api
     * documentation for get request.
     */
    @Test(priority = 8)
    public void Test_DirectProgram_Get() {
        List<FieldDescriptor> list = LoadProgramSetupHelper.createFieldDescriptorForGet(smartCycleGearFieldDescriptor, 30);
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
     * Test case is to update Load Program created by test case Test_DirectProgram_Create and to generate Rest api
     * documentation for Update request.
     */
    @Test(priority = 9)
    public void Test_DirectProgram_Update(ITestContext context) {
        JSONObject jsonObject = buildJSONRequest(context, "documentation\\loadprogram\\DirectProgramCreate.json");
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     requestFields(smartCycleProgramFieldDescriptor),
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
     * Test case is to create copy of Direct Load Program created by test case Test_DirectProgram_Create and to generate
     * Rest api documentation for copy request.
     */
    @Test(priority = 10)
    public void Test_DirectProgram_Copy(ITestContext context) {
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     requestFields(LoadProgramSetupHelper.fieldDescriptorForCopy()),
                                                                     responseFields(LoadProgramSetupHelper.responseFieldDescriptor())))
                                                    .accept("application/json")
                                                    .contentType("application/json")
                                                    .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                                    .body(ApiCallHelper.getInputFile("documentation\\loadprogram\\DirectProgramCopy.json"))
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
     * Test case is to delete the Direct Load Program created by test case Test_DirectProgram_Copy and to generate Rest api
     * documentation for delete request.
     */
    @Test(priority = 11)
    public void Test_DirectCopyProgram_Delete() {
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     requestFields(LoadProgramSetupHelper.requestFieldDesriptorForDelete()),
                                                                     responseFields(LoadProgramSetupHelper.responseFieldDescriptor())))
                                                    .accept("application/json")
                                                    .contentType("application/json")
                                                    .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                                    .body(ApiCallHelper.getInputFile("documentation\\loadprogram\\DirectProgramCopyDelete.json"))
                                                    .when()
                                                    .delete(ApiCallHelper.getProperty("deleteLoadProgram") + copyProgramId)
                                                    .then()
                                                    .extract()
                                                    .response();

        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to delete the Load Program created by test case Test_DirectProgram_Create and to generate Rest api
     * documentation for delete request.
     */
    @Test(priority = 12)
    public void Test_DirectProgram_Delete() {
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     requestFields(LoadProgramSetupHelper.requestFieldDesriptorForDelete()),
                                                                     responseFields(LoadProgramSetupHelper.responseFieldDescriptor())))
                                                    .accept("application/json")
                                                    .contentType("application/json")
                                                    .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                                    .body(ApiCallHelper.getInputFile("documentation\\loadprogram\\DirectProgramDelete.json"))
                                                    .when()
                                                    .delete(ApiCallHelper.getProperty("deleteLoadProgram") + programId)
                                                    .then()
                                                    .extract()
                                                    .response();

        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to Delete Load group we have created for Direct Load Program.
     */
    @Test(priority = 13)
    public void assignedLoadGroup_Delete(ITestContext context) {
        JSONObject payload = JsonFileReader.updateJsonFile("documentation\\loadprogram\\DirectProgramAssignedLoadGroupDelete.json",
                                                           "name",
                                                           context.getAttribute("loadGroupName").toString());

        ExtractableResponse<?> response = ApiCallHelper.delete("deleteloadgroup", payload, context.getAttribute("assignedLoadGroupId").toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to Delete Constraint which have been created for Load Program.
     */
    @Test(priority = 14)
    public void programConstraint_Delete(ITestContext context) {
        JSONObject payload = JsonFileReader.updateJsonFile("documentation\\loadprogram\\LoadProgramAssignedConstraintDelete.json",
                                                           "name",
                                                           context.getAttribute("constraintName").toString());

        ExtractableResponse<?> response = ApiCallHelper.delete("deleteProgramConstraint", payload, context.getAttribute("constraintId").toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }
}
