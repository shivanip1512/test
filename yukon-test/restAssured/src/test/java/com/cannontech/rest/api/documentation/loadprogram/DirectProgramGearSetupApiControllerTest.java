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
import java.util.List;

import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.constraint.request.MockProgramConstraint;
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

public class DirectProgramGearSetupApiControllerTest {

    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private String paoId = null;
    private FieldDescriptor programIdDescriptor = null;
    private MockProgramConstraint programConstraint  = null;
    private List<MockLoadGroupBase> loadGroups = null;

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation, method);
        programIdDescriptor = fieldWithPath("programId").type(JsonFieldType.NUMBER).description("Program Id of Load Program");
        if (programConstraint == null) {
            programConstraint_Create();
        }
        if (loadGroups == null) {
            assignedLoadGroup_Create();
        }
    }

    /**
     * Generic method to get response object specified with request/response descriptors, JSONObject & URL
     */
    private Response getResponseForCreate(List<FieldDescriptor> requestDescriptor, FieldDescriptor responseDescriptor, MockLoadProgram loadProgram, String url) {
        return given(documentationSpec).filter(document("{ClassName}/{methodName}", requestFields(requestDescriptor), responseFields(responseDescriptor)))
                                       .accept("application/json")
                                       .contentType("application/json")
                                       .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                       .body(loadProgram)
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
     * Method to create Load group as we need to pass load group in request of Direct Load Program.
     */

    // Problem: every time a program require new name for assigned Group. So for this

    public void assignedLoadGroup_Create() {
        MockLoadGroupBase loadGroupExpresscomm = LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_EXPRESSCOMM);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroupExpresscomm);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        loadGroups = new ArrayList<>();
        Integer loadGroupId = createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID);
        loadGroupExpresscomm.setId(loadGroupId);
        loadGroups.add(loadGroupExpresscomm);
    }

    /**
     * Method to create Program Constraint as we need to pass constraint in request of Direct Load Program.
     */

    public void programConstraint_Create() {
        programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createProgramConstraint", programConstraint);
        Integer constraintId = createResponse.path(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID);
        programConstraint.setId(constraintId);
        assertTrue("Constraint Id should not be Null", constraintId != null);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
    }

    @Test
    public void Test_LoadProgram_TimeRefreshGear_Create() {
        /*-------Time Refresh Field Descriptor-------*/

        FieldDescriptor[] timeRefreshDescriptor = new FieldDescriptor[] {

                fieldWithPath("gears[].fields.refreshShedTime").type(JsonFieldType.STRING)
                                                               .description("Refresh shed time. Expected: 'FixedCount', 'DynamicShedTime'"),
                fieldWithPath("gears[].fields.shedTime").type(JsonFieldType.NUMBER).description("Shed Time in seconds"),
                fieldWithPath("gears[].fields.numberOfGroups").type(JsonFieldType.NUMBER).description("Number of Groups. Min Value: 0, Max Value: 25"),
                fieldWithPath("gears[].fields.sendRate").type(JsonFieldType.NUMBER).description("Send Rate in seconds"),
                fieldWithPath("gears[].fields.groupSelectionMethod").type(JsonFieldType.STRING)
                                                                    .description("Group Selection Method. Expected: 'LastControlled', 'AlwaysFirstGroup', 'LeastControlTime'"),
                fieldWithPath("gears[].fields.howToStopControl").type(JsonFieldType.STRING)
                                                                .description("How to stop control. Expected :'Restore', 'TimeIn', 'StopCycle', 'RampOutTimeIn', 'RampOutRestore'"),
                fieldWithPath("gears[].fields.stopCommandRepeat").type(JsonFieldType.NUMBER).description("Stop command repeat. Min Value: 0, Max Value: 5"),
                fieldWithPath("gears[].fields.capacityReduction").type(JsonFieldType.NUMBER).description("capacity reduction. MinValue: 0, Max Value: 100"),
                fieldWithPath("gears[].fields.whenToChangeFields").type(JsonFieldType.OBJECT).description("Consists of When to change fields"),
                fieldWithPath("gears[].fields.whenToChangeFields.whenToChange").type(JsonFieldType.STRING)
                                                                               .description("When to change field Expected : 'None', 'Duration', 'Priority', 'TriggerOffset'"), };

        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.TimeRefresh);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_DIRECT_PROGRAM,
                                                                                 loadGroups,
                                                                                 gearTypes,
                                                                                 programConstraint.getId());
        
        Response response = getResponseForCreate(LoadProgramSetupHelper.mergeProgramFieldDescriptors(timeRefreshDescriptor),
                                                 programIdDescriptor,
                                                 loadProgram,
                                                 "saveLoadProgram");

        paoId = response.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID).toString();
        assertTrue("Program Id should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
        ApiCallHelper.delete(Integer.parseInt(paoId), loadProgram.getName(), "deleteLoadProgram");
    }

    @Test
    public void Test_LoadProgram_SmartCycleGear_Create() {
        /*-------Smart cycle Field Descriptor-------*/

        FieldDescriptor[] smartCycleDescriptor = new FieldDescriptor[] {
                fieldWithPath("gears[].fields.noRamp").type(JsonFieldType.BOOLEAN).description("Flag to enable No Ramp"),
                fieldWithPath("gears[].fields.controlPercent").type(JsonFieldType.NUMBER).description("Control percent. Min Value: 5, Max Value: 100"),
                fieldWithPath("gears[].fields.cyclePeriodInMinutes").type(JsonFieldType.NUMBER)
                                                                    .description("Gear cycle period in minutes. Min Value: 1, Max Value : 945"),
                fieldWithPath("gears[].fields.cycleCountSendType").type(JsonFieldType.STRING)
                                                                  .description("Cycle count send type. Expected: 'FixedCount', 'CountDown', 'LimitedCountDown'"),
                fieldWithPath("gears[].fields.maxCycleCount").type(JsonFieldType.NUMBER).description("Maximum cycle count. Min Value: 0, Max Value : 63"),
                fieldWithPath("gears[].fields.startingPeriodCount").type(JsonFieldType.NUMBER)
                                                                   .description("Starting period count. Min Value: 1, Max Value : 63"),
                fieldWithPath("gears[].fields.sendRate").type(JsonFieldType.NUMBER).description("Command Resendend Rate"),
                fieldWithPath("gears[].fields.stopCommandRepeat").type(JsonFieldType.NUMBER).description("Stop command repeat. Min Value: 0, Max Value : 5"),
                fieldWithPath("gears[].fields.howToStopControl").type(JsonFieldType.STRING)
                                                                .description("How to stop control. Expected :'Restore', 'StopCycle'"),
                fieldWithPath("gears[].fields.capacityReduction").type(JsonFieldType.NUMBER)
                                                                 .description("Group Capacity Reduction. Min Value: 0, Max Value : 100"),
                fieldWithPath("gears[].fields.whenToChangeFields").type(JsonFieldType.OBJECT).description("Consists of When to change fields"),
                fieldWithPath("gears[].fields.whenToChangeFields.whenToChange").type(JsonFieldType.STRING)
                                                                               .description("When to change field Expected : None, Duration, Priority, TriggerOffset"), };

        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.SmartCycle);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_DIRECT_PROGRAM,
                                                                                 loadGroups,
                                                                                 gearTypes,
                                                                                 programConstraint.getId());
        
        Response response = getResponseForCreate(LoadProgramSetupHelper.mergeProgramFieldDescriptors(smartCycleDescriptor),
                                                 programIdDescriptor,
                                                 loadProgram,
                                                 "saveLoadProgram");

        paoId = response.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID).toString();
        assertTrue("Program Id should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
        ApiCallHelper.delete(Integer.parseInt(paoId), loadProgram.getName(), "deleteLoadProgram");
    }

    @Test
    public void Test_LoadProgram_MasterCycleGear_Create() {
        /*--------Master Cycle Field Descriptor---------*/

        FieldDescriptor[] masterCycleDescriptor = new FieldDescriptor[] {
                fieldWithPath("gears[].fields.controlPercent").type(JsonFieldType.NUMBER).description("Control Percent. Min Value: 5, Max Value : 100"),
                fieldWithPath("gears[].fields.cyclePeriodInMinutes").type(JsonFieldType.NUMBER)
                                                                    .description("Cycle Period in minutes. Min Value: 5, Max Value : 945"),
                fieldWithPath("gears[].fields.groupSelectionMethod").type(JsonFieldType.STRING)
                                                                    .description("Group selection method. Expected: 'LastControlled', 'AlwaysFirstGroup', 'LeastControlTime'"),
                fieldWithPath("gears[].fields.howToStopControl").type(JsonFieldType.STRING)
                                                                .description("How to stop control. Expected :'Restore', 'TimeIn', 'StopCycle', 'RampOutTimeIn', 'RampOutRestore'"),
                fieldWithPath("gears[].fields.capacityReduction").type(JsonFieldType.NUMBER)
                                                                 .description("Group Capacity Reduction. Min Value: 0, Max Value : 100"),
                fieldWithPath("gears[].fields.whenToChangeFields").type(JsonFieldType.OBJECT).description("Consists of When to change fields"),
                fieldWithPath("gears[].fields.whenToChangeFields.whenToChange").type(JsonFieldType.STRING)
                                                                               .description("When to change field Expected : None, Duration, Priority, TriggerOffset"),

        };
        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.MasterCycle);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_DIRECT_PROGRAM,
                                                                                 loadGroups,
                                                                                 gearTypes,
                                                                                 programConstraint.getId());
        
        Response response = getResponseForCreate(LoadProgramSetupHelper.mergeProgramFieldDescriptors(masterCycleDescriptor),
                                                 programIdDescriptor,
                                                 loadProgram,
                                                 "saveLoadProgram");

        paoId = response.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID).toString();
        assertTrue("Program Id should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
        ApiCallHelper.delete(Integer.parseInt(paoId), loadProgram.getName(), "deleteLoadProgram");
    }

    @Test
    public void Test_LoadProgram_TrueCycleGear_Create() {
        /*--------True Cycle Field Descriptor---------*/

        FieldDescriptor[] trueCycleDescriptor = new FieldDescriptor[] {
                fieldWithPath("gears[].fields.noRamp").type(JsonFieldType.BOOLEAN).description("Flag to enable No Ramp"),
                fieldWithPath("gears[].fields.controlPercent").type(JsonFieldType.NUMBER).description("Control percent. Min Value: 5, Max Value: 100"),
                fieldWithPath("gears[].fields.cyclePeriodInMinutes").type(JsonFieldType.NUMBER)
                                                                    .description("Gear cycle period in minutes. Min Value: 1, Max Value : 945"),
                fieldWithPath("gears[].fields.cycleCountSendType").type(JsonFieldType.STRING)
                                                                  .description("Cycle count send type. Expected:  'FixedCount', 'CountDown', 'LimitedCountDown' "),
                fieldWithPath("gears[].fields.maxCycleCount").type(JsonFieldType.NUMBER).description("Maximum cycle count. Min Value: 0, Max Value : 63"),
                fieldWithPath("gears[].fields.startingPeriodCount").type(JsonFieldType.NUMBER)
                                                                   .description("Starting period count. Min Value: 1, Max Value : 63"),
                fieldWithPath("gears[].fields.sendRate").type(JsonFieldType.NUMBER).description("Command Resendend Rate"),
                fieldWithPath("gears[].fields.stopCommandRepeat").type(JsonFieldType.NUMBER).description("Stop command repeat. Min Value: 0, Max Value : 5"),
                fieldWithPath("gears[].fields.howToStopControl").type(JsonFieldType.STRING)
                                                                .description("How to stop control. Expected :'Restore', 'StopCycle'"),
                fieldWithPath("gears[].fields.capacityReduction").type(JsonFieldType.NUMBER)
                                                                 .description("Group Capacity Reduction. Min Value: 0, Max Value : 100"),
                fieldWithPath("gears[].fields.whenToChangeFields").type(JsonFieldType.OBJECT).description("Consists of When to change fields"),
                fieldWithPath("gears[].fields.whenToChangeFields.whenToChange").type(JsonFieldType.STRING)
                                                                               .description("When to change field Expected : None, Duration, Priority, TriggerOffset"),

        };
        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.TrueCycle);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_DIRECT_PROGRAM,
                                                                                 loadGroups,
                                                                                 gearTypes,
                                                                                 programConstraint.getId());
        
        Response response = getResponseForCreate(LoadProgramSetupHelper.mergeProgramFieldDescriptors(trueCycleDescriptor),
                                                 programIdDescriptor,
                                                 loadProgram,
                                                 "saveLoadProgram");

        paoId = response.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID).toString();
        assertTrue("Program Id should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
        ApiCallHelper.delete(Integer.parseInt(paoId), loadProgram.getName(), "deleteLoadProgram");
    }

    @Test
    public void Test_LoadProgram_MagnitudeCycleGear_Create() {
        /*--------Magnitude Cycle Field Descriptor ---------*/

        FieldDescriptor[] magnitudeCycleDescriptor = new FieldDescriptor[] {
                fieldWithPath("gears[].fields.noRamp").type(JsonFieldType.BOOLEAN).description("Flag to enable No Ramp"),
                fieldWithPath("gears[].fields.controlPercent").type(JsonFieldType.NUMBER).description("Control percent. Min Value: 5, Max Value: 100"),
                fieldWithPath("gears[].fields.cyclePeriodInMinutes").type(JsonFieldType.NUMBER)
                                                                    .description("Gear cycle period in minutes. Min Value: 1, Max Value : 945"),
                fieldWithPath("gears[].fields.cycleCountSendType").type(JsonFieldType.STRING)
                                                                  .description("Cycle count send type. Expected:  'FixedCount', 'CountDown', 'LimitedCountDown' "),
                fieldWithPath("gears[].fields.maxCycleCount").type(JsonFieldType.NUMBER).description("Maximum cycle count. Min Value: 0, Max Value : 63"),
                fieldWithPath("gears[].fields.startingPeriodCount").type(JsonFieldType.NUMBER)
                                                                   .description("Starting period count. Min Value: 1, Max Value : 63"),
                fieldWithPath("gears[].fields.sendRate").type(JsonFieldType.NUMBER).description("Command Resendend Rate"),
                fieldWithPath("gears[].fields.stopCommandRepeat").type(JsonFieldType.NUMBER).description("Stop command repeat. Min Value: 0, Max Value : 5"),
                fieldWithPath("gears[].fields.howToStopControl").type(JsonFieldType.STRING)
                                                                .description("How to stop control. Expected :'Restore', 'StopCycle'"),
                fieldWithPath("gears[].fields.capacityReduction").type(JsonFieldType.NUMBER)
                                                                 .description("Group Capacity Reduction. Min Value: 0, Max Value : 100"),
                fieldWithPath("gears[].fields.whenToChangeFields").type(JsonFieldType.OBJECT).description("Consists of When to change fields"),
                fieldWithPath("gears[].fields.whenToChangeFields.whenToChange").type(JsonFieldType.STRING)
                                                                               .description("When to change field Expected : None, Duration, Priority, TriggerOffset"),

        };

        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.MagnitudeCycle);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_DIRECT_PROGRAM,
                                                                                 loadGroups,
                                                                                 gearTypes,
                                                                                 programConstraint.getId());
        
        Response response = getResponseForCreate(LoadProgramSetupHelper.mergeProgramFieldDescriptors(magnitudeCycleDescriptor),
                                                 programIdDescriptor,
                                                 loadProgram,
                                                 "saveLoadProgram");
        paoId = response.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID).toString();
        assertTrue("Program Id should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
        ApiCallHelper.delete(Integer.parseInt(paoId), loadProgram.getName(), "deleteLoadProgram");
    }

    @Test
    public void Test_LoadProgram_TargetCycleGear_Create() {
        /*--------Target cycle Field Descriptor--------- */

        FieldDescriptor[] targetCycleDescriptor = new FieldDescriptor[] {
                fieldWithPath("gears[].fields.noRamp").type(JsonFieldType.BOOLEAN).description("Flag to enable No Ramp"),
                fieldWithPath("gears[].fields.controlPercent").type(JsonFieldType.NUMBER).description("Control percent. Min Value: 5, Max Value: 100"),
                fieldWithPath("gears[].fields.cyclePeriodInMinutes").type(JsonFieldType.NUMBER)
                                                                    .description("Gear cycle period in minutes. Min Value: 1, Max Value : 945"),
                fieldWithPath("gears[].fields.cycleCountSendType").type(JsonFieldType.STRING)
                                                                  .description("Cycle count send type. Expected:  'FixedCount', 'CountDown', 'LimitedCountDown' "),
                fieldWithPath("gears[].fields.maxCycleCount").type(JsonFieldType.NUMBER).description("Maximum cycle count"),
                fieldWithPath("gears[].fields.startingPeriodCount").type(JsonFieldType.NUMBER)
                                                                   .description("Starting period count. Min Value: 1, Max Value : 63"),
                fieldWithPath("gears[].fields.sendRate").type(JsonFieldType.NUMBER).description("Command Resendend Rate"),
                fieldWithPath("gears[].fields.stopCommandRepeat").type(JsonFieldType.NUMBER).description("Stop command repeat"),
                fieldWithPath("gears[].fields.howToStopControl").type(JsonFieldType.STRING)
                                                                .description("How to stop control. Expected :'Restore', 'StopCycle'"),
                fieldWithPath("gears[].fields.capacityReduction").type(JsonFieldType.NUMBER)
                                                                 .description("Group Capacity Reduction. Min Value: 0, Max Value : 100"),
                fieldWithPath("gears[].fields.kWReduction").type(JsonFieldType.NUMBER).description("KW Reduction. Min Value: 0.0, Max Value: 99999.999"),
                fieldWithPath("gears[].fields.whenToChangeFields").type(JsonFieldType.OBJECT).description("Consists of When to change fields"),
                fieldWithPath("gears[].fields.whenToChangeFields.whenToChange").type(JsonFieldType.STRING)
                                                                               .description("When to change field Expected : None, Duration, Priority, TriggerOffset"),
                                                                               

        };
        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.TargetCycle);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_DIRECT_PROGRAM,
                                                                                 loadGroups,
                                                                                 gearTypes,
                                                                                 programConstraint.getId());
        
        Response response = getResponseForCreate(LoadProgramSetupHelper.mergeProgramFieldDescriptors(targetCycleDescriptor),
                                                 programIdDescriptor,
                                                 loadProgram,
                                                 "saveLoadProgram");
        paoId = response.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID).toString();
        assertTrue("Program Id should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
        ApiCallHelper.delete(Integer.parseInt(paoId), loadProgram.getName(), "deleteLoadProgram");
    }


    @Test
    public void Test_LoadProgram_ThermostatGear_Create() {
        /*---------Thermostat Gear Field Descriptor--------- */

        FieldDescriptor[] thermostatGearDescriptor = new FieldDescriptor[] {
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
        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.ThermostatRamping);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_DIRECT_PROGRAM,
                                                                                 loadGroups,
                                                                                 gearTypes,
                                                                                 programConstraint.getId());
        Response response = getResponseForCreate(LoadProgramSetupHelper.mergeProgramFieldDescriptors(thermostatGearDescriptor),
                                                 programIdDescriptor,
                                                 loadProgram,
                                                 "saveLoadProgram");
        paoId = response.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID).toString();
        assertTrue("Program Id should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
        ApiCallHelper.delete(Integer.parseInt(paoId), loadProgram.getName(), "deleteLoadProgram");
    }

    @Test
    public void Test_LoadProgram_SimpleThermostatGear_Create() {
        /*---------SimpleThermostat Gear Field Descriptor--------- */

        FieldDescriptor[] simpleThermostatGearDescriptor = new FieldDescriptor[] {
                fieldWithPath("gears[].fields.mode").type(JsonFieldType.STRING).description("Mode."),
                fieldWithPath("gears[].fields.randomStartTimeInMinutes").type(JsonFieldType.NUMBER)
                                                                        .description("Random Start Time. Min Value: 0, Max Value: 120"),
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

        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.SimpleThermostatRamping);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_DIRECT_PROGRAM,
                                                                                 loadGroups,
                                                                                 gearTypes,
                                                                                 programConstraint.getId());
        Response response = getResponseForCreate(LoadProgramSetupHelper.mergeProgramFieldDescriptors(simpleThermostatGearDescriptor),
                                                 programIdDescriptor,
                                                 loadProgram,
                                                 "saveLoadProgram");
        paoId = response.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID).toString();
        assertTrue("Program Id should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
        ApiCallHelper.delete(Integer.parseInt(paoId), loadProgram.getName(), "deleteLoadProgram");
    }

    @Test
    public void Test_LoadProgram_LatchingGear_Create() {
        /*---------Latching Gear Field Descriptor--------- */

        FieldDescriptor[] latchingGearDescriptor = new FieldDescriptor[] {
                fieldWithPath("gears[].fields.startControlState").type(JsonFieldType.STRING).description("Control Start state. Expected: 'Open','Close'"),
                fieldWithPath("gears[].fields.capacityReduction").type(JsonFieldType.NUMBER)
                                                                 .description("Group Capacity reduction. Min Value: 0, Max Value: 100"), };

        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.Latching);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_DIRECT_PROGRAM,
                                                                                 loadGroups,
                                                                                 gearTypes,
                                                                                 programConstraint.getId());
        
        Response response = getResponseForCreate(LoadProgramSetupHelper.mergeProgramFieldDescriptors(latchingGearDescriptor),
                                                 programIdDescriptor,
                                                 loadProgram,
                                                 "saveLoadProgram");
        paoId = response.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID).toString();
        assertTrue("Program Id should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
        ApiCallHelper.delete(Integer.parseInt(paoId), loadProgram.getName(), "deleteLoadProgram");
    }

    @Test
    public void Test_LoadProgram_BeatThePeakGear_Create() {
        /*--------Beat the peak Field Descriptor--------- */

        FieldDescriptor[] beatThePeakGearDescriptor = new FieldDescriptor[] {
                fieldWithPath("gears[].fields.indicator").type(JsonFieldType.STRING)
                                                         .description("Beat The Peak LED indicator. Expected value: 'Yellow' & 'Red'"),
                fieldWithPath("gears[].fields.timeoutInMinutes").type(JsonFieldType.NUMBER).description("Time Out in minutes. Min Value: 0, Max Value: 99999"),
                fieldWithPath("gears[].fields.resendInMinutes").type(JsonFieldType.NUMBER)
                                                               .description("Resend Rate in minutes. Min Value: 0, Max Value: 99999"),
                fieldWithPath("gears[].fields.whenToChangeFields").type(JsonFieldType.OBJECT).description("Consists of When to change fields"),
                fieldWithPath("gears[].fields.whenToChangeFields.whenToChange").type(JsonFieldType.STRING)
                                                                               .description("When to change field Expected : None, Duration, Priority, TriggerOffset"),

        };
        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.BeatThePeak);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_DIRECT_PROGRAM,
                                                                                 loadGroups,
                                                                                 gearTypes,
                                                                                 programConstraint.getId());
        
        Response response = getResponseForCreate(LoadProgramSetupHelper.mergeProgramFieldDescriptors(beatThePeakGearDescriptor),
                                                 programIdDescriptor,
                                                 loadProgram,
                                                 "saveLoadProgram");

        paoId = response.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID).toString();
        assertTrue("Program Id should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
        ApiCallHelper.delete(Integer.parseInt(paoId), loadProgram.getName(), "deleteLoadProgram");
    }

    @Test
    public void Test_LoadProgram_NoControlGear_Create() {
        /*-------- No Control Field Descriptor--------- */

        FieldDescriptor[] noControlGearDescriptor = new FieldDescriptor[] {
                fieldWithPath("gears[].fields.whenToChangeFields").type(JsonFieldType.OBJECT).description("Consists of When to change fields"),
                fieldWithPath("gears[].fields.whenToChangeFields.whenToChange").type(JsonFieldType.STRING)
                                                                               .description("When to change field Expected : None, Duration, Priority, TriggerOffset"),

        };

        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.NoControl);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_DIRECT_PROGRAM,
                                                                                 loadGroups,
                                                                                 gearTypes,
                                                                                 programConstraint.getId());
        
        Response response = getResponseForCreate(LoadProgramSetupHelper.mergeProgramFieldDescriptors(noControlGearDescriptor),
                                                 programIdDescriptor,
                                                 loadProgram,
                                                 "saveLoadProgram");

        paoId = response.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID).toString();
        assertTrue("Program Id should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
        ApiCallHelper.delete(Integer.parseInt(paoId), loadProgram.getName(), "deleteLoadProgram");
    }

    @AfterClass
    public void cleanUp() {
        ApiCallHelper.delete(programConstraint.getId(), programConstraint.getName(), "deleteProgramConstraint");
        loadGroups.forEach(group -> {
            ApiCallHelper.delete(group.getId(), group.getName(), "deleteloadgroup");
        });
    }
}
