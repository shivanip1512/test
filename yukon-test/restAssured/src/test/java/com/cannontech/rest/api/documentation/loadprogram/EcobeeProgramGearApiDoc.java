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

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class EcobeeProgramGearApiDoc {
    
    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private String paoId = null;
    private FieldDescriptor programIdDescriptor = null;
    private MockProgramConstraint programConstraint  = null;
    private List<MockLoadGroupBase> loadGroups = null;

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        restDocumentation.beforeTest(getClass(), method.getName());
        documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation, method);
        programIdDescriptor = fieldWithPath("programId").type(JsonFieldType.NUMBER).description("Program Id of Load Program");
        
        if (programConstraint == null) {
            programConstraint = ProgramConstraintHelper.createProgramConstraint();
        }
        if (loadGroups == null) {
            loadGroups = new ArrayList<>();
            loadGroups.add(LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_ECOBEE));
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
    
    @Test
    public void Test_LoadProgram_EcobeeCycleGear_Create() {
        /*-------Ecobee Cycle Field Descriptor-------*/

        FieldDescriptor[] ecobeeCycleDescriptor = new FieldDescriptor[] { fieldWithPath("gears[].fields.mandatory").type(JsonFieldType.BOOLEAN).description("Mandatory"),
                fieldWithPath("gears[].fields.rampIn").type(JsonFieldType.BOOLEAN).description("Ramp In"),
                fieldWithPath("gears[].fields.rampOut").type(JsonFieldType.BOOLEAN).description("Ramp Out"),
                fieldWithPath("gears[].fields.controlPercent").type(JsonFieldType.NUMBER).description("Control Percent. Min Value: 5, Max Value: 100"),
                fieldWithPath("gears[].fields.howToStopControl").type(JsonFieldType.STRING).description("How To Stop Control"),
                fieldWithPath("gears[].fields.capacityReduction").type(JsonFieldType.NUMBER)
                                                                 .description("Group Capacity Reduction. Min Value: 0, Max Value: 100"),
                fieldWithPath("gears[].fields.whenToChangeFields").type(JsonFieldType.OBJECT).description("Consists of When to change fields"),
                fieldWithPath("gears[].fields.whenToChangeFields.whenToChange").type(JsonFieldType.STRING)
                                                                               .description("When to change field. Expected : None, Duration, Priority, TriggerOffset") };

        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.EcobeeCycle);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_ECOBEE_PROGRAM,
                                                                                 loadGroups,
                                                                                 gearTypes,
                                                                                 programConstraint.getId());
        
        Response response = getResponseForCreate(LoadProgramSetupHelper.mergeProgramFieldDescriptors(ecobeeCycleDescriptor),
                                                 programIdDescriptor,
                                                 loadProgram,
                                                 "saveLoadProgram");

        paoId = response.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID).toString();
        assertTrue("Program Id should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
        ApiCallHelper.delete(Integer.parseInt(paoId), loadProgram.getName(), "deleteLoadProgram");
    }
    
    @Test
    public void Test_LoadProgram_EcobeeSetpointGear_Create() {
        /*-------Ecobee Setpoint Field Descriptor-------*/

        FieldDescriptor[] ecobeeSetpointDescriptor = new FieldDescriptor[] { fieldWithPath("gears[].fields.mandatory").type(JsonFieldType.BOOLEAN).description("Mandatory"),
                fieldWithPath("gears[].fields.howToStopControl").type(JsonFieldType.STRING).description("How To Stop Control"),
                fieldWithPath("gears[].fields.capacityReduction").type(JsonFieldType.NUMBER)
                                                                 .description("Group Capacity Reduction. Min Value: 0, Max Value: 100"),
                fieldWithPath("gears[].fields.whenToChangeFields").type(JsonFieldType.OBJECT).description("Consists of When to change fields"),
                fieldWithPath("gears[].fields.whenToChangeFields.whenToChange").type(JsonFieldType.STRING)
                                                                               .description("When to change field. Expected : None, Duration, Priority, TriggerOffset"),
                fieldWithPath("gears[].fields.setpointOffset").type(JsonFieldType.NUMBER).description("Setpoint Offset. Min Value: -10 F, Max Value: 10 F"),
                fieldWithPath("gears[].fields.mode").type(JsonFieldType.STRING).description("Mode. COOL, HEAT") };

        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.EcobeeSetpoint);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_ECOBEE_PROGRAM,
                                                                                 loadGroups,
                                                                                 gearTypes,
                                                                                 programConstraint.getId());
        
        Response response = getResponseForCreate(LoadProgramSetupHelper.mergeProgramFieldDescriptors(ecobeeSetpointDescriptor),
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
