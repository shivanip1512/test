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
import java.util.List;

import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.ITestContext;
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
import com.cannontech.rest.api.loadProgram.request.MockLoadProgramCopy;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.utilities.RestApiDocumentationUtility;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class EcobeeProgramApiDoc {

    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private Integer programId = null;
    private Integer copyProgramId = null;
    private FieldDescriptor[] ecobeeGearFieldDescriptor = null;
    private List<FieldDescriptor> ecobeeProgramFieldDescriptor = null;
    private MockLoadProgram subOrdinateLoadProgram = null;

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation, method);
        ecobeeGearFieldDescriptor = new FieldDescriptor[] { fieldWithPath("gears[].fields.mandatory").type(JsonFieldType.BOOLEAN).description("Mandatory"),
                fieldWithPath("gears[].fields.rampIn").type(JsonFieldType.BOOLEAN).description("Ramp In").optional(),
                fieldWithPath("gears[].fields.rampOut").type(JsonFieldType.BOOLEAN).description("Ramp Out").optional(),
                fieldWithPath("gears[].fields.controlPercent").type(JsonFieldType.NUMBER).description("Control Percent. Min Value: 5, Max Value: 100").optional(),
                fieldWithPath("gears[].fields.howToStopControl").type(JsonFieldType.STRING).description("How To Stop Control"),
                fieldWithPath("gears[].fields.capacityReduction").type(JsonFieldType.NUMBER)
                                                                 .description("Group Capacity Reduction. Min Value: 0, Max Value: 100"),
                fieldWithPath("gears[].fields.whenToChangeFields").type(JsonFieldType.OBJECT).description("Consists of When to change fields"),
                fieldWithPath("gears[].fields.whenToChangeFields.whenToChange").type(JsonFieldType.STRING)
                                                                               .description("When to change field. Expected : None, Duration, Priority, TriggerOffset"),
                fieldWithPath("gears[].fields.setpointOffset").type(JsonFieldType.NUMBER).description("Setpoint Offset. Min Value: -10 F, Max Value: 10 F").optional(),
                fieldWithPath("gears[].fields.mode").type(JsonFieldType.STRING).description("Mode. COOL, HEAT").optional() };
        ecobeeProgramFieldDescriptor = LoadProgramSetupHelper.mergeProgramFieldDescriptors(ecobeeGearFieldDescriptor);
    }

    @AfterMethod
    public void tearDown() {
        this.restDocumentation.afterTest();
    }

    /**
     * Test case is to create Load group as we need to pass load group in request of Ecobee Load Program.
     */
    @Test
    public void ecobeeAssignedLoadGroup_Create(ITestContext context) {
        MockLoadGroupBase loadGroupEcobee = LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_ECOBEE);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("loadGroups", loadGroupEcobee);
        assertTrue("Status code should be 201", createResponse.statusCode() == 201);
        List<MockLoadGroupBase> loadGroups = new ArrayList<>();
        Integer loadGroupId = createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID);
        loadGroupEcobee.setId(loadGroupId);
        loadGroups.add(loadGroupEcobee);
        context.setAttribute("loadGroups", loadGroups);
    }

    /**
     * Test case is to create Program Constraint as we need to pass constraint in request of Ecobee Load Program.
     */
    @Test(dependsOnMethods={"ecobeeAssignedLoadGroup_Create"})
    public void programConstraint_Create(ITestContext context) {
        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        ExtractableResponse<?> createResponse = ApiCallHelper.post("programConstraints", programConstraint);
        Integer constraintId = createResponse.path(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID);
        context.setAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID, constraintId);
        context.setAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_NAME, programConstraint.getName());
        assertTrue("Constraint Id should not be Null", constraintId != null);
        assertTrue("Status code should be 201", createResponse.statusCode() == 201);
    }

    /**
     * Test case is to create Ecobee Load Program and to generate Rest api documentation for Load Program create
     * request.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    @Test(dependsOnMethods={"programConstraint_Create"})
    public void Test_EcobeeProgram_Create(ITestContext context) {
        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.EcobeeCycle);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_ECOBEE_PROGRAM,
                                                                                 (List<MockLoadGroupBase>) context.getAttribute("loadGroups"),
                                                                                 gearTypes,
                                                                                 (Integer) context.getAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID));
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     requestFields(ecobeeProgramFieldDescriptor),
                                                                     responseFields(LoadProgramSetupHelper.responseFieldDescriptor())))
                                                    .accept("application/json")
                                                    .contentType("application/json")
                                                    .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                                    .body(loadProgram)
                                                    .when()
                                                    .post(ApiCallHelper.getProperty("loadPrograms"))
                                                    .then()
                                                    .extract()
                                                    .response();
        context.setAttribute(LoadProgramSetupHelper.CONTEXT_PROGRAM_NAME, loadProgram.getName());
        programId = response.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID);
        assertTrue("Program Id should not be Null", programId != null);
        assertTrue("Status code should be 201", response.statusCode() == 201);
    }

    /**
     * Test case is to get Load Program created by test case Test_EcobeeProgram_Create and to generate Rest api
     * documentation for get request.
     */
    @Test(dependsOnMethods={"Test_EcobeeProgram_Create"})
    public void Test_EcobeeProgram_Get() {
        List<FieldDescriptor> list = LoadProgramSetupHelper.createFieldDescriptorForGet(ecobeeGearFieldDescriptor, 26);
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}", responseFields(list)))
                                                    .accept("application/json")
                                                    .contentType("application/json")
                                                    .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                                    .when()
                                                    .get(ApiCallHelper.getProperty("loadPrograms") + "/" + programId)
                                                    .then()
                                                    .extract()
                                                    .response();

        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to update Load Program created by test case Test_EcobeeProgram_Create and to generate Rest api
     * documentation for Update request.
     */
    @SuppressWarnings("unchecked")
    @Test(dependsOnMethods={"Test_EcobeeProgram_Get"})
    public void Test_EcobeeProgram_Update(ITestContext context) {
        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.EcobeeCycle);
        subOrdinateLoadProgram = LoadProgramSetupHelper.getMemberControlLoadProgram(context, gearTypes, MockPaoType.LM_ECOBEE_PROGRAM);

        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramUpdateRequest(MockPaoType.LM_ECOBEE_PROGRAM,
                                                                                 (List<MockLoadGroupBase>) context.getAttribute("loadGroups"),
                                                                                 gearTypes,
                                                                                 (Integer) context.getAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID),
                                                                                 subOrdinateLoadProgram);

        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     requestFields(LoadProgramSetupHelper.createFieldDescriptorForUpdate(ecobeeGearFieldDescriptor)),
                                                                     responseFields(LoadProgramSetupHelper.responseFieldDescriptor())))
                                                    .accept("application/json")
                                                    .contentType("application/json")
                                                    .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                                    .body(loadProgram)
                                                    .when()
                                                    .put(ApiCallHelper.getProperty("loadPrograms") + "/" + programId)
                                                    .then()
                                                    .extract()
                                                    .response();

        programId = response.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID);
        assertTrue("Program Id should not be Null", programId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to create copy of Load Program created by test case Test_EcobeeProgram_Create and to generate Rest
     * api documentation for copy request.
     */
    @Test(dependsOnMethods={"Test_EcobeeProgram_Update"})
    public void Test_EcobeeProgram_Copy(ITestContext context) {
        MockLoadProgramCopy loadProgramCopy = LoadProgramSetupHelper.buildLoadProgramCopyRequest(MockPaoType.LM_ECOBEE_PROGRAM,
                                                                                                 (Integer) context.getAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID));

        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     requestFields(LoadProgramSetupHelper.fieldDescriptorForCopy()),
                                                                     responseFields(LoadProgramSetupHelper.responseFieldDescriptor())))
                                                    .accept("application/json")
                                                    .contentType("application/json")
                                                    .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                                    .body(loadProgramCopy)
                                                    .when()
                                                    .post(ApiCallHelper.getProperty("loadPrograms") + "/" + programId + "/copy")
                                                    .then()
                                                    .extract()
                                                    .response();
        context.setAttribute(LoadProgramSetupHelper.CONTEXT_COPIED_PROGRAM_NAME, loadProgramCopy.getName());
        copyProgramId = response.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID);
        String updatedPaoId = copyProgramId.toString();
        assertTrue("Program Id should not be Null", updatedPaoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to delete the Ecobee Load Program created by test case Test_EcobeeProgram_Copy and to generate Rest
     * api documentation for delete request.
     */
    @Test(dependsOnMethods={"Test_EcobeeProgram_Copy"})
    public void Test_EcobeeCopyProgram_Delete(ITestContext context) {
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     responseFields(LoadProgramSetupHelper.deleteFieldDescriptor())))
                                                    .accept("application/json")
                                                    .contentType("application/json")
                                                    .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                                    .when()
                                                    .delete(ApiCallHelper.getProperty("loadPrograms") + "/" + copyProgramId)
                                                    .then()
                                                    .extract()
                                                    .response();

        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to delete Load Program created by test case Test_EcobeeProgram_Create and to generate Rest api
     * documentation for delete request.
     */
    @Test(dependsOnMethods={"Test_EcobeeProgram_Copy"})
    public void Test_EcobeeProgram_Delete(ITestContext context) {
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     responseFields(LoadProgramSetupHelper.deleteFieldDescriptor())))
                                                    .accept("application/json")
                                                    .contentType("application/json")
                                                    .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                                    .when()
                                                    .delete(ApiCallHelper.getProperty("loadPrograms") + "/" + programId)
                                                    .then()
                                                    .extract()
                                                    .response();

        assertTrue("Status code should be 200", response.statusCode() == 200);
        ApiCallHelper.delete("loadPrograms", "/" + subOrdinateLoadProgram.getProgramId());
    }

    /**
     * Test case is to Delete Load group we have created for Load Program.
     */
    @SuppressWarnings("unchecked")
    @Test(dependsOnMethods={"Test_EcobeeProgram_Delete"})
    public void assignedLoadGroup_Delete(ITestContext context) {
        List<MockLoadGroupBase> groups = (List<MockLoadGroupBase>) context.getAttribute("loadGroups");
        groups.forEach(group -> {
            ExtractableResponse<?> response = ApiCallHelper.delete("loadGroups", "/" + group.getId());
            assertTrue("Status code should be 200", response.statusCode() == 200);
        });
    }

    /**
     * Test case is to Delete Constraint which have been created for Load Program.
     */
    @Test(dependsOnMethods={"assignedLoadGroup_Delete"})
    public void programConstraint_Delete(ITestContext context) {
        ExtractableResponse<?> response = ApiCallHelper.delete("programConstraints",
                "/" + (context.getAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID)));
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }
}
