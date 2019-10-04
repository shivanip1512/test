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
import java.util.ArrayList;
import java.util.Arrays;
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

public class MeterDisconnectProgramSetupApiControllerTest {

    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private Number programId = null;
    private Number copyProgramId = null;
    private FieldDescriptor[] meterDisconnectGearFieldDescriptor = null;

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation, method);
        meterDisconnectGearFieldDescriptor = new FieldDescriptor[] {
                fieldWithPath("programId").type(JsonFieldType.NUMBER).optional().description("Load Program Id"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("Load Program Name"),
                fieldWithPath("type").type(JsonFieldType.STRING).description("Load Program Type"),
                fieldWithPath("operationalState").type(JsonFieldType.STRING).description("Load Program Operational State"),
                fieldWithPath("constraint.constraintId").type(JsonFieldType.NUMBER).description("Constraint Id"),
                fieldWithPath("triggerOffset").type(JsonFieldType.NUMBER).description("Trigger offset. Min Value: 0.0, Max Value: 99999.9999"),
                fieldWithPath("restoreOffset").type(JsonFieldType.NUMBER).description("Restore offset. Min Value: -9999.9999 , Max Value: 99999.9999"),

                fieldWithPath("gears[].gearName").type(JsonFieldType.STRING).description("Gear Name"),
                fieldWithPath("gears[].gearNumber").type(JsonFieldType.NUMBER).description("Gear Number"),
                fieldWithPath("gears[].controlMethod").type(JsonFieldType.STRING).description("Gear Type"),

                fieldWithPath("controlWindow.controlWindowOne.availableStartTimeInMinutes").type(JsonFieldType.NUMBER)
                                                                                           .description("Available Start Time In Minutes"),
                fieldWithPath("controlWindow.controlWindowOne.availableStopTimeInMinutes").type(JsonFieldType.NUMBER)
                                                                                          .description("Available Stop Time In Minutes"),
                fieldWithPath("controlWindow.controlWindowTwo.availableStartTimeInMinutes").type(JsonFieldType.NUMBER)
                                                                                           .description("Available Start Time In Minutes"),
                fieldWithPath("controlWindow.controlWindowTwo.availableStopTimeInMinutes").type(JsonFieldType.NUMBER)
                                                                                          .description("Available Stop Time In Minutes"),

                fieldWithPath("assignedGroups[].groupId").type(JsonFieldType.NUMBER).description("Assigned Load Group Id"),
                fieldWithPath("assignedGroups[].groupName").type(JsonFieldType.STRING).description("Assigned Load Group Name"),
                fieldWithPath("assignedGroups[].type").type(JsonFieldType.STRING).description("Assigned Load Group Type"),

                fieldWithPath("notification.notifyOnAdjust").type(JsonFieldType.BOOLEAN).description("Notify on Adjust"),
                fieldWithPath("notification.enableOnSchedule").type(JsonFieldType.BOOLEAN).description("Enable on schedule"),
                fieldWithPath("notification.assignedNotificationGroups[]").type(JsonFieldType.ARRAY).description("Assigned Notification groups"),
                fieldWithPath("notification.assignedNotificationGroups[].notificationGrpID").type(JsonFieldType.NUMBER).description("Assigned Notification Id"),
                fieldWithPath("notification.assignedNotificationGroups[].notificationGrpName").type(JsonFieldType.STRING)
                                                                                              .description("Assigned Notification Group Name") };
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
     * Test case is to create Load group as we need to pass load group in request of MeterDisconnect Load Program.
     */
    @Test
    public void meterDisconnectAssignedLoadGroup_Create(ITestContext context) {
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", "documentation\\loadprogram\\MeterDisconnectProgramAssignedLoadGroup.json");
        Integer groupId = createResponse.path("groupId");
        context.setAttribute("loadGroupId", groupId.toString());
        context.setAttribute("assignedLoadGroupId", groupId);

        JSONObject jsonObject = JsonFileReader.readJsonFileAsJSONObject("documentation\\loadprogram\\MeterDisconnectProgramAssignedLoadGroup.json");
        JsonPath jsonPath = new JsonPath(jsonObject.toJSONString());
        context.setAttribute("loadGroupName", jsonPath.getString("LM_GROUP_METER_DISCONNECT.name"));
        context.setAttribute("loadGroupType", jsonPath.getString("LM_GROUP_METER_DISCONNECT.type"));
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
    }

    /**
     * Test case is to create Program Constraint as we need to pass constraint in request of MeterDisconnect Load
     * Program.
     */
    @Test(dependsOnMethods={"meterDisconnectAssignedLoadGroup_Create"})
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
     * Test case is to create MeterDisconnect Load Program and to generate Rest api documentation for Load Program
     * create request.
     * @throws IOException
     */
    @Test(dependsOnMethods={"programConstraint_Create"})
    public void Test_MeterDisconnectProgram_Create(ITestContext context) {
        JSONObject jsonObject = buildJSONRequest(context, "documentation\\loadprogram\\MeterDisconnectProgramCreate.json");
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     requestFields(meterDisconnectGearFieldDescriptor),
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
     * Test case is to get MeterDisconnect Load Program created by test case Test_MeterDisconnectProgram_Create and to
     * generate Rest api documentation for get request.
     */
    @Test(dependsOnMethods={"Test_MeterDisconnectProgram_Create"})
    public void Test_MeterDisconnectProgram_Get(ITestContext context) {
        List<FieldDescriptor> list = new ArrayList<>(Arrays.asList(meterDisconnectGearFieldDescriptor));
        list.add(5, fieldWithPath("constraint.constraintName").type(JsonFieldType.STRING).description("Constraint Name"));
        list.add(8, fieldWithPath("gears[].gearId").type(JsonFieldType.NUMBER).description("Gear Id"));
        list.add(18, fieldWithPath("assignedGroups[].groupOrder").type(JsonFieldType.NUMBER).description("Group Order"));
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
     * Test case is to update Load Program created by test case Test_MeterDisconnectProgram_Create and to generate Rest
     * api documentation for Update request.
     */
    @Test(dependsOnMethods={"Test_MeterDisconnectProgram_Get"})
    public void Test_MeterDisconnectProgram_Update(ITestContext context) {
        JSONObject jsonObject = buildJSONRequest(context, "documentation\\loadprogram\\MeterDisconnectProgramCreate.json");
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     requestFields(meterDisconnectGearFieldDescriptor),
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
     * Test case is to create copy of MeterDisconnect Load Program created by test case
     * Test_MeterDisconnectProgram_Create and to generate Rest api documentation for copy request.
     */
    @Test(dependsOnMethods={"Test_MeterDisconnectProgram_Update"})
    public void Test_MeterDisconnectProgram_Copy(ITestContext context) {
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     requestFields(LoadProgramSetupHelper.fieldDescriptorForCopy()),
                                                                     responseFields(LoadProgramSetupHelper.responseFieldDescriptor())))
                                                    .accept("application/json")
                                                    .contentType("application/json")
                                                    .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                                    .body(ApiCallHelper.getInputFile("documentation\\loadprogram\\MeterDisconnectProgramCopy.json"))
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
     * Test case is to delete the MeterDisconnect Load Program created by test case Test_MeterDisconnectProgram_Copy and
     * to generate Rest api documentation for delete request.
     */
    @Test(dependsOnMethods={"Test_MeterDisconnectProgram_Copy"})
    public void Test_MeterDisconnectCopyProgram_Delete() {
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     requestFields(LoadProgramSetupHelper.requestFieldDesriptorForDelete()),
                                                                     responseFields(LoadProgramSetupHelper.responseFieldDescriptor())))
                                                    .accept("application/json")
                                                    .contentType("application/json")
                                                    .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                                    .body(ApiCallHelper.getInputFile("documentation\\loadprogram\\MeterDisconnectCopyProgramDelete.json"))
                                                    .when()
                                                    .delete(ApiCallHelper.getProperty("deleteLoadProgram") + copyProgramId)
                                                    .then()
                                                    .extract()
                                                    .response();

        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to delete the MeterDisconnect Load Program created by test case Test_MeterDisconnectProgram_Create
     * and to generate Rest api documentation for delete request.
     */
    @Test(dependsOnMethods={"Test_MeterDisconnectProgram_Copy"})
    public void Test_MeterDisconnectProgram_Delete() {
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     requestFields(LoadProgramSetupHelper.requestFieldDesriptorForDelete()),
                                                                     responseFields(LoadProgramSetupHelper.responseFieldDescriptor())))
                                                    .accept("application/json")
                                                    .contentType("application/json")
                                                    .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                                    .body(ApiCallHelper.getInputFile("documentation\\loadprogram\\MeterDisconnectProgramDelete.json"))
                                                    .when()
                                                    .delete(ApiCallHelper.getProperty("deleteLoadProgram") + programId)
                                                    .then()
                                                    .extract()
                                                    .response();

        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to Delete Load group we have created for Load Program.
     */
    @Test(dependsOnMethods={"Test_MeterDisconnectProgram_Delete"})
    public void assignedLoadGroup_Delete(ITestContext context) {
        JSONObject payload = JsonFileReader.updateJsonFile("documentation\\loadprogram\\MeterDisconnectProgramAssignedLoadGroupDelete.json",
                                                           "name",
                                                           context.getAttribute("loadGroupName").toString());

        ExtractableResponse<?> response = ApiCallHelper.delete("deleteloadgroup", payload, context.getAttribute("loadGroupId").toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to Delete Constraint which have been created for Load Program.
     */
    @Test(dependsOnMethods={"assignedLoadGroup_Delete"})
    public void programConstraint_Delete(ITestContext context) {
        JSONObject payload = JsonFileReader.updateJsonFile("documentation\\loadprogram\\LoadProgramAssignedConstraintDelete.json",
                                                           "name",
                                                           context.getAttribute("constraintName").toString());

        ExtractableResponse<?> response = ApiCallHelper.delete("deleteProgramConstraint", payload, context.getAttribute("constraintId").toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }
}
