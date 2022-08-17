package com.cannontech.rest.api.documentation.controlarea;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.testng.ITestContext;
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
import com.cannontech.rest.api.dr.helper.ControlAreaHelper;
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

public class ControlAreaSetupApiControllerTest {

    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private MockLoadProgram loadProgram = null;
    private Integer controlAreaId = null;
    private List<FieldDescriptor> controlAreaFieldDescriptor = null;
    private MockControlArea controlAreaThresholdPoint = null;
    private MockProgramConstraint programConstraint = null;
    private List<MockLoadGroupBase> loadGroups = null;

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        restDocumentation.beforeTest(getClass(), method.getName());
        documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation, method);
        controlAreaFieldDescriptor = Arrays.asList(ControlAreaHelper.buildResponseDescriptorForGet());
        loadGroups = new ArrayList<>();
        if (loadGroups.isEmpty()) {
            loadGroups.add(LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_EXPRESSCOMM));
        }
        if (programConstraint == null) {
            programConstraint = ProgramConstraintHelper.createProgramConstraint();
        }
        if (loadProgram == null) {
            loadProgram_Create();
        }
    }

    @AfterMethod
    public void tearDown() {
        this.restDocumentation.afterTest();
    }

    /**
     * Method to create load program as we need to pass load program in request of Control Area.
     */
    public void loadProgram_Create() {
        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.TimeRefresh);

        loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_DIRECT_PROGRAM, loadGroups, gearTypes, programConstraint.getId());
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", loadProgram);
        Integer programId = createResponse.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID);
        loadProgram.setProgramId(programId);

        assertTrue("Program Id should not be Null", programId != null);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
    }

    /**
     * Generic method to get response object specified with request/response descriptors, JSONObject & URL
     */
    private Response getResponseForCreate(List<FieldDescriptor> requestDescriptor, List<FieldDescriptor> responseDescriptor, MockControlArea controlArea,
            String url) {
        return given(documentationSpec).filter(document("{ClassName}/{methodName}", requestFields(requestDescriptor), responseFields(responseDescriptor)))
                                       .accept("application/json")
                                       .contentType("application/json")
                                       .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                       .body(controlArea)
                                       .when()
                                       .post(ApiCallHelper.getProperty(url))
                                       .then()
                                       .extract()
                                       .response();
    }

    /**
     * Test case is to create Control Area with Threshold Point Trigger and to generate Rest API documentation for
     * create request.
     */
    @Test
    public void Test_ControlArea_ThresholdPointTrigger_Create() {
        controlAreaThresholdPoint = ControlAreaHelper.buildControlArea(MockControlAreaTriggerType.THRESHOLD_POINT, loadProgram.getProgramId());
        List<FieldDescriptor> requestDescriptor = ControlAreaHelper.buildRequestDescriptor(MockControlAreaTriggerType.THRESHOLD_POINT);
        List<FieldDescriptor> responseDescriptor = ControlAreaHelper.buildResponseDescriptor();
        Response response = getResponseForCreate(requestDescriptor, responseDescriptor, controlAreaThresholdPoint, "saveControlArea");
        controlAreaId = response.path(ControlAreaHelper.CONTEXT_CONTROLAREA_ID);

        assertTrue("Control Area Id should not be Null", controlAreaId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to create Control Area with Threshold Trigger and to generate Rest API documentation for create
     * request.
     */
    @Test(dependsOnMethods = { "Test_ControlArea_ThresholdPointTrigger_Delete" })
    public void Test_ControlArea_ThresholdTrigger_Create() {
        MockControlArea controlArea = ControlAreaHelper.buildControlArea(MockControlAreaTriggerType.THRESHOLD, loadProgram.getProgramId());
        List<FieldDescriptor> requestDescriptor = ControlAreaHelper.buildRequestDescriptor(MockControlAreaTriggerType.THRESHOLD);
        List<FieldDescriptor> responseDescriptor = ControlAreaHelper.buildResponseDescriptor();
        Response response = getResponseForCreate(requestDescriptor, responseDescriptor, controlArea, "saveControlArea");
        Integer controlAreaId = response.path(ControlAreaHelper.CONTEXT_CONTROLAREA_ID);
        controlArea.setControlAreaId(controlAreaId);

        assertTrue("Control Area Id should not be Null", controlAreaId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
        ApiCallHelper.delete(controlArea.getControlAreaId(), controlArea.getName(), "deleteControlArea");
    }

    /**
     * Test case is to create Control Area with Status Trigger and to generate Rest API documentation for create
     * request.
     */
    @Test(dependsOnMethods = { "Test_ControlArea_ThresholdTrigger_Create" })
    public void Test_ControlArea_StatusTrigger_Create() {
        MockControlArea controlArea = ControlAreaHelper.buildControlArea(MockControlAreaTriggerType.STATUS, loadProgram.getProgramId());
        List<FieldDescriptor> requestDescriptor = ControlAreaHelper.buildRequestDescriptor(MockControlAreaTriggerType.STATUS);
        List<FieldDescriptor> responseDescriptor = ControlAreaHelper.buildResponseDescriptor();
        Response response = getResponseForCreate(requestDescriptor, responseDescriptor, controlArea, "saveControlArea");
        Integer controlAreaId = response.path(ControlAreaHelper.CONTEXT_CONTROLAREA_ID);
        controlArea.setControlAreaId(controlAreaId);

        assertTrue("Control Area Id should not be Null", controlAreaId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
        ApiCallHelper.delete(controlArea.getControlAreaId(), controlArea.getName(), "deleteControlArea");
    }

    /**
     * Test case is to get Control Area created by test case Test_ControlArea_ThresholdPointTrigger_Create and to
     * generate Rest api documentation for get request.
     */
    @Test(dependsOnMethods = { "Test_ControlArea_ThresholdPointTrigger_Create" })
    public void Test_ControlArea_ThresholdPointTrigger_Get() {
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}", responseFields(controlAreaFieldDescriptor)))
                                                    .accept("application/json")
                                                    .contentType("application/json")
                                                    .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                                    .when()
                                                    .get(ApiCallHelper.getProperty("getControlArea") + controlAreaId)
                                                    .then()
                                                    .extract()
                                                    .response();
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to update Control Area created by test case Test_ControlArea_ThresholdPointTrigger_Create and to
     * generate Rest api documentation for update request.
     */
    @Test(dependsOnMethods = { "Test_ControlArea_ThresholdPointTrigger_Get" })
    public void Test_ControlArea_ThresholdPointTrigger_Update(ITestContext context) {
        List<FieldDescriptor> requestDescriptor = ControlAreaHelper.buildRequestDescriptor(MockControlAreaTriggerType.THRESHOLD_POINT);
        List<FieldDescriptor> responseDescriptor = ControlAreaHelper.buildResponseDescriptor();
        MockControlArea controlArea = ControlAreaHelper.buildControlArea(MockControlAreaTriggerType.THRESHOLD_POINT, loadProgram.getProgramId());
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     requestFields(requestDescriptor),
                                                                     responseFields(responseDescriptor)))
                                                    .accept("application/json")
                                                    .contentType("application/json")
                                                    .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                                    .body(controlArea)
                                                    .when()
                                                    .post(ApiCallHelper.getProperty("updateControlArea") + controlAreaId)
                                                    .then()
                                                    .extract()
                                                    .response();

        Integer controlAreaId = response.path(ControlAreaHelper.CONTEXT_CONTROLAREA_ID);
        assertTrue("Program Id should not be Null", controlAreaId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * Test case is to delete Control Area created by test case Test_ControlArea_ThresholdPointTrigger_Create and to
     * generate Rest api documentation for delete request.
     */
    @Test(dependsOnMethods = { "Test_ControlArea_ThresholdPointTrigger_Update" })
    public void Test_ControlArea_ThresholdPointTrigger_Delete(ITestContext context) {
        MockLMDto deleteObject = MockLMDto.builder().name(controlAreaThresholdPoint.getName()).build();

        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                                                                     requestFields(ControlAreaHelper.requestFieldDesriptorForDelete()),
                                                                     responseFields(ControlAreaHelper.buildResponseDescriptor())))
                                                    .accept("application/json")
                                                    .contentType("application/json")
                                                    .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                                    .body(deleteObject)
                                                    .when()
                                                    .delete(ApiCallHelper.getProperty("deleteControlArea") + controlAreaId)
                                                    .then()
                                                    .extract()
                                                    .response();

        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @AfterClass
    public void cleanUp() {
        ApiCallHelper.delete(loadProgram.getProgramId(), loadProgram.getName(), "deleteLoadProgram");
        ApiCallHelper.delete(programConstraint.getId(), programConstraint.getName(), "deleteProgramConstraint");
        loadGroups.forEach(group -> {
            ApiCallHelper.delete(group.getId(), group.getName(), "deleteloadgroup");
        });
    }
}
