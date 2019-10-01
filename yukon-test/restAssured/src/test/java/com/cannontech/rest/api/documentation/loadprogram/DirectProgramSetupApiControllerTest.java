package com.cannontech.rest.api.documentation.loadprogram;

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
    private FieldDescriptor[] LPSmartCycleDescriptor = null;

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation,method);
        LPSmartCycleDescriptor = new FieldDescriptor[] {
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
            
            fieldWithPath("gears[].fields.noRamp").type(JsonFieldType.BOOLEAN).description("No Ramp field"),
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
            fieldWithPath("gears[].fields.whenToChangeFields.whenToChange").type(JsonFieldType.STRING).description("When to change field. Expected : None, Duration, Priority, TriggerOffset"),
        
            fieldWithPath("controlWindow.controlWindowOne.availableStartTimeInMinutes").type(JsonFieldType.NUMBER).description("Available Start Time In Minutes"),
            fieldWithPath("controlWindow.controlWindowOne.availableStopTimeInMinutes").type(JsonFieldType.NUMBER).description("Available Stop Time In Minutes"),
            fieldWithPath("controlWindow.controlWindowTwo.availableStartTimeInMinutes").type(JsonFieldType.NUMBER).description("Available Start Time In Minutes"),
            fieldWithPath("controlWindow.controlWindowTwo.availableStopTimeInMinutes").type(JsonFieldType.NUMBER).description("Available Stop Time In Minutes"),

            fieldWithPath("assignedGroups[].groupId").type(JsonFieldType.NUMBER).description("Assigned Load Group Id"),
            fieldWithPath("assignedGroups[].groupName").type(JsonFieldType.STRING).description("Assigned Load Group Name"),
            fieldWithPath("assignedGroups[].type").type(JsonFieldType.STRING).description("Assigned Load Group Type"),
            
            fieldWithPath("notification.notifyOnAdjust").type(JsonFieldType.BOOLEAN).description("Notify on Adjust"),
            fieldWithPath("notification.enableOnSchedule").type(JsonFieldType.BOOLEAN).description("Enable on schedule"),
            fieldWithPath("notification.assignedNotificationGroups[]").type(JsonFieldType.ARRAY).description("Assigned Notification groups"),
            fieldWithPath("notification.assignedNotificationGroups[].notificationGrpID").type(JsonFieldType.NUMBER).description("Assigned Notification Id"),
            fieldWithPath("notification.assignedNotificationGroups[].notificationGrpName").type(JsonFieldType.STRING).description("Assigned Notification Group Name")
        };
    }
    
    @AfterMethod
    public void tearDown() {
        this.restDocumentation.afterTest();
    }

    private JSONObject buildJSONRequest(ITestContext context) {
        JSONObject jsonObject =
                JsonFileReader.readJsonFileAsJSONObject("documentation\\loadprogram\\DirectProgramCreate.json");
            JsonPath jsonPath = new JsonPath(jsonObject.toJSONString());
            context.setAttribute("loadProgramCopy", jsonPath.getString("name"));

            JSONObject jsonArrayObject = new JSONObject();
            jsonArrayObject.put("groupId", context.getAttribute("assignedLoadGroupId"));
            jsonArrayObject.put("groupName", context.getAttribute("loadGroupName"));
            jsonArrayObject.put("type", context.getAttribute("loadGroupType"));
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(jsonArrayObject);
            JSONObject constraintJson =(JSONObject) jsonObject.get("constraint");
            constraintJson.put("constraintId",context.getAttribute("constraintId"));
            jsonObject.put("constraint", constraintJson);
            jsonObject.put("assignedGroups", jsonArray);
            return jsonObject;
    }

     /**
     * Test case is to create Load group
     * as we need to pass load group in request of Direct Load Program.
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
     * Test case is to create Program Constraint
     * as we need to pass constraint in request of Direct Load Program.
     */
    @Test(priority = 2)
    public void programConstraint_Create(ITestContext context) {
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createProgramConstraint", "documentation\\loadprogram\\DirectProgramAssignedConstraint.json");
        Integer constraintId = createResponse.path("id");
        context.setAttribute("constraintId", constraintId);
        
        JSONObject jsonObject = JsonFileReader.readJsonFileAsJSONObject("documentation\\loadprogram\\DirectProgramAssignedConstraint.json");
        JsonPath jsonPath = new JsonPath(jsonObject.toJSONString());
        context.setAttribute("constraintName", jsonPath.getString("name"));
        
        assertTrue("Constraint ID should not be Null", constraintId != null);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
    }

      /**
     * Test case is to create Direct Load Program
     * and to generate Rest api documentation for Direct Load Program create request.
     */
        @Test(priority = 3)
        public void Test_DirectProgram_Create(ITestContext context) {
            JSONObject jsonObject = buildJSONRequest(context);
            Response response = given(documentationSpec)
                                    .filter(document("{ClassName}/{methodName}", requestFields(LPSmartCycleDescriptor),
                                        responseFields(fieldWithPath("programId").type(JsonFieldType.NUMBER).description("Program Id of Load Program"))))
                                    .accept("application/json")
                                    .contentType("application/json")
                                    .header("Authorization","Bearer " + ApiCallHelper.authToken)
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
     * Test case is to get Direct Load Program created by test case Test_DirectProgram_Create
     * and to generate Rest api documentation for get request.
     */
    @Test(priority = 4)
    public void Test_DirectProgram_Get() {
        List<FieldDescriptor> list = new ArrayList<>(Arrays.asList(LPSmartCycleDescriptor));
        list.add(5,fieldWithPath("constraint.constraintName").type(JsonFieldType.STRING).description("Constraint Name"));
        list.add(8,fieldWithPath("gears[].gearId").type(JsonFieldType.NUMBER).description("Gear Id"));
        list.add(31,fieldWithPath("assignedGroups[].groupOrder").type(JsonFieldType.NUMBER).description("Group Order"));
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", responseFields(list)))
                                .accept("application/json")
                                .contentType("application/json")
                                .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                .when()
                                .get(ApiCallHelper.getProperty("getLoadProgram") + programId)
                                .then()
                                .extract()
                                .response();
    
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to update Load Program created by test case Test_DirectProgram_Create
     * and to generate Rest api documentation for Update request.
     */
    @Test(priority = 5)
    public void Test_DirectProgram_Update(ITestContext context) {
        List<FieldDescriptor> list = new ArrayList<>(Arrays.asList(LPSmartCycleDescriptor));
        list.add(0,fieldWithPath("programId").type(JsonFieldType.NUMBER).description("Load Program Id"));
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", requestFields(LPSmartCycleDescriptor),
                                responseFields(fieldWithPath("programId").type(JsonFieldType.NUMBER).description("Load Program Id"))))
                                .accept("application/json")
                                .contentType("application/json")
                                .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                .body(ApiCallHelper.getInputFile("documentation\\loadprogram\\DirectProgramCreate.json"))
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
     * Test case is to  create copy of Direct Load Program created by test case Test_DirectProgram_Create
     * and to generate Rest api documentation for copy request.
     */
    @Test(priority = 6)
    public void Test_DirectProgram_Copy(ITestContext context) {
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", 
                                    requestFields(
                                    fieldWithPath("name").type(JsonFieldType.STRING).description("Load Program Name"),
                                    fieldWithPath("operationalState").type(JsonFieldType.STRING).description("Operational State"),
                                    fieldWithPath("constraint.constraintId").type(JsonFieldType.NUMBER).description("Constraint Id")), 
                                    responseFields(fieldWithPath("programId").type(JsonFieldType.NUMBER).description("Load program Id"))))
                                .accept("application/json")
                                .contentType("application/json")
                                .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                .body(ApiCallHelper.getInputFile("documentation\\loadprogram\\DirectProgramCopy.json"))
                                .when()
                                .post(ApiCallHelper.getProperty("copyLoadProgram")+ programId)
                                .then()
                                .extract()
                                .response();

        String updatedPaoId = response.path("programId").toString();
        assertTrue("PAO ID should not be Null", updatedPaoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to delete the Load Program created by test case Test_DirectProgram_Create
     * and to generate Rest api documentation for delete request.
     */
    @Test(priority = 7)
    public void Test_DirectProgram_Delete() {
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}",
                                    requestFields(
                                    fieldWithPath("name").type(JsonFieldType.STRING).description("Load Program Name")), 
                                    responseFields(fieldWithPath("programId").type(JsonFieldType.NUMBER).description("Load Program Id"))))
                                .accept("application/json")
                                .contentType("application/json")
                                .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                .body(ApiCallHelper.getInputFile("documentation\\loadprogram\\DirectProgramDelete.json"))
                                .when()
                                .delete(ApiCallHelper.getProperty("deleteLoadProgram") + programId)
                                .then()
                                .extract()
                                .response();
     
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }
    
    /**
     * Test case is to Delete Load group
     * we have created for Direct Load Program.
     */
    @Test(priority = 8)
    public void assignedLoadGroup_Delete(ITestContext context) {
        JSONObject payload =
            JsonFileReader.updateJsonFile("documentation\\loadprogram\\DirectProgramAssignedLoadGroupDelete.json", "name",
                context.getAttribute("loadGroupName").toString());

        ExtractableResponse<?> response =
            ApiCallHelper.delete("deleteloadgroup", payload, context.getAttribute("assignedLoadGroupId").toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }
    
}
