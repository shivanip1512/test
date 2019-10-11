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

import org.json.simple.JSONObject;
import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.AfterClass;
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

public class SepProgramGearSetupApiControllerTest {

    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private String paoId = null;
    private FieldDescriptor programIdDescriptor = null;
    private JSONObject constraintJson = null;
    private JSONObject loadGroupJson = null;

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation, method);
        programIdDescriptor = fieldWithPath("programId").type(JsonFieldType.NUMBER).description("Program Id of Load Program");
        if (constraintJson == null) {
            programConstraint_Create();
        }
        if (loadGroupJson == null) {
            assignedLoadGroup_Create();
        }
    }

    /**
     * Generic method to get response object specified with request/response descriptors, JSONObject & URL
     */
    private Response getResponseForCreate(List<FieldDescriptor> requestDescriptor, FieldDescriptor responseDescriptor, JSONObject jsonObject, String url) {
        return given(documentationSpec).filter(document("{ClassName}/{methodName}", requestFields(requestDescriptor), responseFields(responseDescriptor)))
                .accept("application/json")
                .contentType("application/json")
                .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                .body(jsonObject.toJSONString())
                .when()
                .post(ApiCallHelper.getProperty(url))
                .then()
                .extract()
                .response();
    }

    @AfterMethod
    public void tearDown() {
        this.restDocumentation.afterTest();
    }

    /**
     * Method to create Load group as we need to pass load group in request of Sep Load Program.
     */
    @SuppressWarnings("unchecked")
    public void assignedLoadGroup_Create() {
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", "documentation\\loadprogram\\SepProgramAssignedLoadGroup.json");
        Integer groupId = createResponse.path("groupId");
        loadGroupJson = new JSONObject();
        loadGroupJson.put("loadGroupId", groupId);
        loadGroupJson.put("assignedLoadGroupId", groupId);
        JSONObject jsonObject = JsonFileReader.readJsonFileAsJSONObject("documentation\\loadprogram\\SepProgramAssignedLoadGroup.json");
        JsonPath jsonPath = new JsonPath(jsonObject.toJSONString());
        loadGroupJson.put("loadGroupName", jsonPath.getString("LM_GROUP_DIGI_SEP.name"));
        loadGroupJson.put("loadGroupType", jsonPath.getString("LM_GROUP_DIGI_SEP.type"));
    }

    /**
     * Method to create Program Constraint as we need to pass constraint in request of Sep Load Program.
     */
    @SuppressWarnings("unchecked")
    public void programConstraint_Create() {
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createProgramConstraint",
                                                                   "documentation\\loadprogram\\LoadProgramAssignedConstraint.json");
        Integer constraintId = createResponse.path("id");
        constraintJson = new JSONObject();
        constraintJson.put("constraintId", constraintId);
        JSONObject jsonObject = JsonFileReader.readJsonFileAsJSONObject("documentation\\loadprogram\\LoadProgramAssignedConstraint.json");
        JsonPath jsonPath = new JsonPath(jsonObject.toJSONString());
        constraintJson.put("constraintName", jsonPath.getString("name"));
    }

    @Test
    public void Test_LoadProgram_SepTemperatureOffset_Create() {
        /*-------Sep Temperature Field Descriptor-------*/

        FieldDescriptor[] sepTemperatureOffsetDescriptor = new FieldDescriptor[] {
                fieldWithPath("gears[].fields.rampIn").type(JsonFieldType.BOOLEAN).description("Ramp In"),
                fieldWithPath("gears[].fields.rampOut").type(JsonFieldType.BOOLEAN).description("Ramp Out"),
                fieldWithPath("gears[].fields.celsiusOrFahrenheit").type(JsonFieldType.STRING).description("Measurement Unit"),
                fieldWithPath("gears[].fields.mode").type(JsonFieldType.STRING).description("Heat or Cool Mode."),
                fieldWithPath("gears[].fields.offset").type(JsonFieldType.NUMBER)
                                                      .description("Measurement Unit Offset. Min Value: 0.1, Max Value for Celsius: 25.4 and Max Value for Fahrenheit: 77.7"),
                fieldWithPath("gears[].fields.criticality").type(JsonFieldType.NUMBER).description("Criticality. Min Value: 1, Max Value: 15"),
                fieldWithPath("gears[].fields.howToStopControl").type(JsonFieldType.STRING).ignored().description("How To Stop Control"),
                fieldWithPath("gears[].fields.capacityReduction").type(JsonFieldType.NUMBER).description("Capacity Reduction. Min Value: 0, Max Value: 100"),
                fieldWithPath("gears[].fields.whenToChangeFields").type(JsonFieldType.OBJECT).description("Consists of When to change fields"),
                fieldWithPath("gears[].fields.whenToChangeFields.whenToChange").type(JsonFieldType.STRING)
                                                                               .description("When to change field Expected : None, Duration, Priority, TriggerOffset") };

        Response response = getResponseForCreate(LoadProgramSetupHelper.mergeProgramFieldDescriptors(sepTemperatureOffsetDescriptor),
                                                 programIdDescriptor,
                                                 LoadProgramSetupHelper.buildJSONRequest(constraintJson,
                                                                                         loadGroupJson,
                                                                                         "documentation\\loadprogram\\SepTemperatureOffset.json"),
                                                 "saveLoadProgram");

        paoId = response.path("programId").toString();
        assertTrue("PAO ID should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
        JSONObject jsonObject = JsonFileReader.readJsonFileAsJSONObject("documentation\\loadprogram\\SepTemperatureOffset.json");
        LoadProgramSetupHelper.delete(Integer.parseInt(paoId), jsonObject.get("name").toString(), "deleteLoadProgram");
    }

    @Test
    public void Test_LoadProgram_NoControl_Create() {
        /*-------- No Control Field Descriptor--------- */
        FieldDescriptor[] noControlGearDescriptor = new FieldDescriptor[] {
                fieldWithPath("gears[].fields.whenToChangeFields").type(JsonFieldType.OBJECT).description("Consists of When to change fields"),
                fieldWithPath("gears[].fields.whenToChangeFields.whenToChange").type(JsonFieldType.STRING)
                                                                               .description("When to change field Expected : None, Duration, Priority, TriggerOffset"),

        };

        Response response = getResponseForCreate(LoadProgramSetupHelper.mergeProgramFieldDescriptors(noControlGearDescriptor),
                                                 programIdDescriptor,
                                                 LoadProgramSetupHelper.buildJSONRequest(constraintJson,
                                                                                         loadGroupJson,
                                                                                         "documentation\\loadprogram\\SepNoControl.json"),
                                                 "saveLoadProgram");

        paoId = response.path("programId").toString();
        assertTrue("PAO ID should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
        JSONObject jsonObject = JsonFileReader.readJsonFileAsJSONObject("documentation\\loadprogram\\SepNoControl.json");
        LoadProgramSetupHelper.delete(Integer.parseInt(paoId), jsonObject.get("name").toString(), "deleteLoadProgram");
    }

    @AfterClass
    public void cleanUp() {
        LoadProgramSetupHelper.delete(Integer.valueOf(constraintJson.get("constraintId").toString()),
                                      constraintJson.get("constraintName").toString(),
                                      "deleteProgramConstraint");
        LoadProgramSetupHelper.delete(Integer.valueOf(loadGroupJson.get("loadGroupId").toString()),
                                      loadGroupJson.get("loadGroupName").toString(),
                                      "deleteloadgroup");
    }
}
