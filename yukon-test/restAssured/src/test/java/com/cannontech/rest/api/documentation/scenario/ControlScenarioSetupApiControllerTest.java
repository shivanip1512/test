package com.cannontech.rest.api.documentation.scenario;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.constraint.request.MockProgramConstraint;
import com.cannontech.rest.api.controlArea.request.MockControlArea;
import com.cannontech.rest.api.controlArea.request.MockControlAreaTriggerType;
import com.cannontech.rest.api.controlScenario.request.MockControlScenario;
import com.cannontech.rest.api.dr.helper.ControlAreaHelper;
import com.cannontech.rest.api.dr.helper.ControlScenarioHelper;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.dr.helper.LoadProgramSetupHelper;
import com.cannontech.rest.api.dr.helper.ProgramConstraintHelper;
import com.cannontech.rest.api.gear.fields.MockGearControlMethod;
import com.cannontech.rest.api.loadProgram.request.MockLoadProgram;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.utilities.RestApiDocumentationUtility;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ControlScenarioSetupApiControllerTest {

    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private MockControlScenario scenario = null;
    private FieldDescriptor[] controlScenarioFieldDescriptor = null;
    private MockControlArea controlArea = null;
    private MockProgramConstraint programConstraint = null;
    private List<MockLoadGroupBase> loadGroups = null;
    private MockLoadProgram loadProgram = null;

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        restDocumentation.beforeTest(getClass(), method.getName());
        documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation, method);
        controlScenarioFieldDescriptor = new FieldDescriptor[] {
                fieldWithPath("id").type(JsonFieldType.NUMBER).optional().description("Scenario Id"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("Scenario name"),
                fieldWithPath("allPrograms[].programId").type(JsonFieldType.NUMBER).description(
                        "Load Program assigned in Scenario having gears. Expected type: Direct, SEP, Honeywell, Itron, Ecobee."),
                fieldWithPath("allPrograms[].category").type(JsonFieldType.STRING).optional()
                        .description("Category of program. This field is optional"),
                fieldWithPath("allPrograms[].startOffsetInMinutes").type(JsonFieldType.NUMBER)
                        .description("Start offset of Scenario in minutes"),
                fieldWithPath("allPrograms[].stopOffsetInMinutes").type(JsonFieldType.NUMBER)
                        .description("Stop offset of scenario in minutes"),
                fieldWithPath("allPrograms[].gears[].id").type(JsonFieldType.NUMBER)
                        .description("selected Gear id from the list of gear available in the program"),
                fieldWithPath("allPrograms[].gears[].name").type(JsonFieldType.STRING).optional()
                        .description("selected Gear name from the list of gear available in the program. This field is optional"),
        };

        controlArea_Create();
    }

    @AfterMethod
    public void tearDown() {
        this.restDocumentation.afterTest();
    }

    /**
     * Method to create control area with load program as we need to pass load program which is available in control area in
     * request of Control Scenario.
     */
    public void controlArea_Create() {
        loadGroups = new ArrayList<>();
        if (loadGroups.isEmpty()) {
            loadGroups.add(LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_EXPRESSCOMM));
        }
        if (programConstraint == null) {
            programConstraint = ProgramConstraintHelper.createProgramConstraint();
        }
        if (loadProgram == null) {

            List<MockGearControlMethod> gearTypes = new ArrayList<>();
            gearTypes.add(MockGearControlMethod.TimeRefresh);

            loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_DIRECT_PROGRAM, loadGroups, gearTypes,
                    programConstraint.getId());
            ExtractableResponse<?> loadProgramResponse = ApiCallHelper.post("saveLoadProgram", loadProgram);

            loadProgram.setProgramId(loadProgramResponse.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID));
            assertTrue("Program Id should not be Null", loadProgram.getProgramId() != null);
            assertTrue("Status code should be 200", loadProgramResponse.statusCode() == 200);

            ExtractableResponse<?> response = ApiCallHelper.get("getLoadProgram",
                    loadProgramResponse.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID).toString());
            assertTrue(response.statusCode() == 200, "Status code should be 200");
            loadProgram = response.as(MockLoadProgram.class);

            if (controlArea == null) {
                controlArea = ControlAreaHelper.buildControlArea(MockControlAreaTriggerType.THRESHOLD_POINT,
                        loadProgram.getProgramId());
                ExtractableResponse<?> controlAreaResponse = ApiCallHelper.post("saveControlArea", controlArea);

                controlArea.setControlAreaId(controlAreaResponse.path(ControlAreaHelper.CONTEXT_CONTROLAREA_ID));
                assertTrue("Control Area Id should not be Null", controlArea.getControlAreaId() != null);
                assertTrue("Status code should be 200", controlAreaResponse.statusCode() == 200);
            }
        }
    }

    /**
     * Test case is to create Control Scenario with Load Program available in Control Area and to generate Rest API documentation
     * for
     * create request.
     */
    @Test
    public void Test_ControlScenario_Create() {
        scenario = ControlScenarioHelper.buildControlScenario(loadProgram);

        Response response = given(documentationSpec)
                .filter(document("{ClassName}/{methodName}", requestFields(controlScenarioFieldDescriptor),
                        responseFields(fieldWithPath(ControlScenarioHelper.CONTEXT_CONTROL_SCENARIO_ID).type(JsonFieldType.NUMBER)
                                .description("Control Scenario Id"))))
                .accept("application/json")
                .contentType("application/json")
                .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                .body(scenario)
                .when()
                .post(ApiCallHelper.getProperty("saveControlScenario"))
                .then()
                .extract()
                .response();

        scenario.setId(response.path(ControlScenarioHelper.CONTEXT_CONTROL_SCENARIO_ID));
        assertTrue("Control Scenario Id should not be Null", scenario.getId() != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);

    }

    /**
     * Test case is to get Control Scenario created by test case Test_ControlScenario_Create and to
     * generate Rest api documentation for get request.
     */
    @Test(dependsOnMethods = { "Test_ControlScenario_Create" })
    public void Test_ControlScenario_Get() {
        Response response = given(documentationSpec)
                .filter(document("{ClassName}/{methodName}", responseFields(controlScenarioFieldDescriptor)))
                .accept("application/json")
                .contentType("application/json")
                .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                .when()
                .get(ApiCallHelper.getProperty("getControlScenario") + scenario.getId())
                .then()
                .extract()
                .response();
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to update Control Area created by test case Test_ControlArea_ThresholdPointTrigger_Create and to
     * generate Rest api documentation for update request.
     */
    @Test(dependsOnMethods = { "Test_ControlScenario_Get" })
    public void Test_ControlScenario_Update() {
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                requestFields(controlScenarioFieldDescriptor),
                responseFields(fieldWithPath("paoId").type(JsonFieldType.NUMBER).description("Control Scenario Id"))))
                .accept("application/json")
                .contentType("application/json")
                .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                .body(scenario)
                .when()
                .post(ApiCallHelper.getProperty("updateControlScenario") + scenario.getId())
                .then()
                .extract()
                .response();

        assertTrue("Scenario Id should not be Null",
                response.path(ControlScenarioHelper.CONTEXT_CONTROL_SCENARIO_ID).toString() != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to delete Control Area created by test case Test_ControlArea_ThresholdPointTrigger_Create and to
     * generate Rest api documentation for delete request.
     */
    @Test(dependsOnMethods = { "Test_ControlScenario_Update" })
    public void Test_ControlScenario_Delete() {
        MockLMDto deleteScenario = MockLMDto.builder().name(scenario.getName()).build();

        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                requestFields(fieldWithPath("name").type(JsonFieldType.STRING).description("Control Scenario name")),
                responseFields(fieldWithPath("paoId").type(JsonFieldType.NUMBER).description("Control Scenario Id"))))
                .accept("application/json")
                .contentType("application/json")
                .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                .body(deleteScenario)
                .when()
                .delete(ApiCallHelper.getProperty("deleteControlScenario") + scenario.getId())
                .then()
                .extract()
                .response();

        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @AfterClass
    public void cleanUp() {
        ApiCallHelper.delete(controlArea.getControlAreaId(), controlArea.getName(), "deleteControlArea");
        ApiCallHelper.delete(loadProgram.getProgramId(), loadProgram.getName(), "deleteLoadProgram");
        ApiCallHelper.delete(programConstraint.getId(), programConstraint.getName(), "deleteProgramConstraint");
        loadGroups.forEach(group -> {
            ApiCallHelper.delete(group.getId(), group.getName(), "deleteloadgroup");
        });
    }
}
