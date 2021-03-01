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
import org.testng.ITestContext;
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

public class SepProgramGearApiDoc {

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
            loadGroups = new ArrayList<>();
            loadGroups.add(LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_DIGI_SEP));
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
     * Method to create Program Constraint as we need to pass constraint in request of Sep Load Program.
     */
    public void programConstraint_Create() {
        programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        ExtractableResponse<?> createResponse = ApiCallHelper.post("programConstraints", programConstraint);
        Integer constraintId = createResponse.path(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID);
        programConstraint.setId(constraintId);
        assertTrue("Constraint Id should not be Null", constraintId != null);
        assertTrue("Status code should be 201", createResponse.statusCode() == 201);
    }

    @Test
    public void Test_LoadProgram_SepTemperatureOffset_Create(ITestContext context) {
        /*-------Sep Temperature Field Descriptor-------*/

        FieldDescriptor[] sepTemperatureOffsetDescriptor = new FieldDescriptor[] {
                fieldWithPath("gears[].fields.rampIn").type(JsonFieldType.BOOLEAN).description("Ramp In"),
                fieldWithPath("gears[].fields.rampOut").type(JsonFieldType.BOOLEAN).description("Ramp Out"),
                fieldWithPath("gears[].fields.tempMeasureUnit").type(JsonFieldType.STRING).description("Measurement Unit"),
                fieldWithPath("gears[].fields.mode").type(JsonFieldType.STRING).description("Heat or Cool Mode."),
                fieldWithPath("gears[].fields.offset").type(JsonFieldType.NUMBER)
                                                      .description("Measurement Unit Offset. Min Value: 0.1, Max Value for Celsius: 25.4 and Max Value for Fahrenheit: 77.7"),
                fieldWithPath("gears[].fields.criticality").type(JsonFieldType.NUMBER).description("Criticality. Min Value: 1, Max Value: 15"),
                fieldWithPath("gears[].fields.howToStopControl").type(JsonFieldType.STRING).description("How To Stop Control"),
                fieldWithPath("gears[].fields.capacityReduction").type(JsonFieldType.NUMBER).description("Capacity Reduction. Min Value: 0, Max Value: 100"),
                fieldWithPath("gears[].fields.whenToChangeFields").type(JsonFieldType.OBJECT).description("Consists of When to change fields"),
                fieldWithPath("gears[].fields.whenToChangeFields.whenToChange").type(JsonFieldType.STRING)
                                                                               .description("When to change field Expected : None, Duration, Priority, TriggerOffset") };

        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.SepTemperatureOffset);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_SEP_PROGRAM,
                                                                                 loadGroups,
                                                                                 gearTypes,
                                                                                 programConstraint.getId());
        
        Response response = getResponseForCreate(LoadProgramSetupHelper.mergeProgramFieldDescriptors(sepTemperatureOffsetDescriptor),
                                                 programIdDescriptor,
                                                 loadProgram,
                                                 "loadPrograms");

        paoId = response.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID).toString();
        assertTrue("Program Id should not be Null", paoId != null);
        assertTrue("Status code should be 201", response.statusCode() == 201);
        ApiCallHelper.delete("loadPrograms", "/" + paoId);
    }

    @Test
    public void Test_LoadProgram_NoControl_Create(ITestContext context) {
        /*-------- No Control Field Descriptor--------- */
        FieldDescriptor[] noControlGearDescriptor = new FieldDescriptor[] {
                fieldWithPath("gears[].fields.whenToChangeFields").type(JsonFieldType.OBJECT).description("Consists of When to change fields"),
                fieldWithPath("gears[].fields.whenToChangeFields.whenToChange").type(JsonFieldType.STRING)
                                                                               .description("When to change field Expected : None, Duration, Priority, TriggerOffset"),

        };

        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.NoControl);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_SEP_PROGRAM,
                                                                                 loadGroups,
                                                                                 gearTypes,
                                                                                 programConstraint.getId());
        
        Response response = getResponseForCreate(LoadProgramSetupHelper.mergeProgramFieldDescriptors(noControlGearDescriptor),
                                                 programIdDescriptor,
                                                 loadProgram,
                                                 "loadPrograms");

        paoId = response.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID).toString();
        assertTrue("Program Id should not be Null", paoId != null);
        assertTrue("Status code should be 201", response.statusCode() == 201);
        ApiCallHelper.delete("loadPrograms", "/" + paoId);
    }

    @AfterClass
    public void cleanUp() {
        ApiCallHelper.delete("programConstraints", "/" + programConstraint.getId().toString());
        loadGroups.forEach(group -> {
            ApiCallHelper.delete("loadGroups", "/" + group.getId());
        });

    }
}
