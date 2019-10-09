package com.cannontech.rest.api.documentation.constraint;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

import java.lang.reflect.Method;

import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.constraint.request.MockProgramConstraint;
import com.cannontech.rest.api.utilities.RestApiDocumentationUtility;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ProgramConstraintControllerTest {

    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private MockProgramConstraint programConstraint = null;
    private String constraintId = null;
    private FieldDescriptor[] programConstraintFieldDescriptor = null;

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation, method);
        programConstraintFieldDescriptor = new FieldDescriptor[] {
            fieldWithPath("name").type(JsonFieldType.STRING).description("Program Constraint Name."),
            fieldWithPath("seasonSchedule.id").type(JsonFieldType.NUMBER).description("Season Schedule ID."),
            fieldWithPath("maxActivateSeconds").type(JsonFieldType.NUMBER).description("Max Acitvate Seconds. Min Value: 0, Max Value: 99999"),
            fieldWithPath("maxDailyOps").type(JsonFieldType.NUMBER).description("Max Daily Operations. Min Value: 0, Max Value: 99999"),
            fieldWithPath("minActivateSeconds").type(JsonFieldType.NUMBER).description("Min Activate Seconds. Min Value: 0, Max Value: 99999"),
            fieldWithPath("minRestartSeconds").type(JsonFieldType.NUMBER).description("Min Restart Seconds. Min Value: 0, Max Value: 99999"),
            fieldWithPath("daySelection").type(JsonFieldType.VARIES).description("Day Selection. Expected: SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY"),
            fieldWithPath("holidaySchedule.id").type(JsonFieldType.NUMBER).description("Holiday Schedule ID."),
            fieldWithPath("holidayUsage").type(JsonFieldType.STRING).description("Holiday Usages. Expected: EXCLUDE, FORCE, NONE"),
            fieldWithPath("maxHoursDaily").type(JsonFieldType.NUMBER).description("Max Hours Daily. Min Value: 0, Max Value: 99999"),
            fieldWithPath("maxHoursMonthly").type(JsonFieldType.NUMBER).description("Max Hours Monthly. Min Value: 0, Max Value: 99999"),
            fieldWithPath("maxHoursAnnually").type(JsonFieldType.NUMBER).description("Max Hours Annually. Min Value: 0, Max Value: 99999"),
            fieldWithPath("maxHoursSeasonal").type(JsonFieldType.NUMBER).description("Max Hours Seasonally. Min Value: 0, Max Value: 99999") };
        if (this.programConstraint == null) {
            this.programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        }
    }

    @AfterMethod
    public void tearDown() {
        this.restDocumentation.afterTest();
    }

    @Test()
    public void Test_ProgramConstraint_Create() {
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", requestFields(programConstraintFieldDescriptor),
                                    responseFields(fieldWithPath("id").type(JsonFieldType.NUMBER).description("Program Constraint."))))
                                .accept("application/json")
                                .contentType("application/json")
                                .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                .body(programConstraint)
                                .when()
                                .post(ApiCallHelper.getProperty("createProgramConstraint"))
                                .then()
                                .extract()
                                .response();

        constraintId = response.path("id").toString();
        assertTrue("Constraint ID should not be Null", constraintId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @Test(dependsOnMethods = {"Test_ProgramConstraint_Create"})
    public void Test_ProgramConstraint_Get() {
        Response response = given(documentationSpec)
                .filter(document("{ClassName}/{methodName}",
                    responseFields(fieldWithPath("id").type(JsonFieldType.NUMBER).description("Program Constraint ID."),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("Program Constraint Name."), 
                        fieldWithPath("seasonSchedule.id").type(JsonFieldType.NUMBER).description("Season Schedule ID."),
                        fieldWithPath("seasonSchedule.name").type(JsonFieldType.STRING).description("Season Schedule Name.").optional(),
                        fieldWithPath("maxActivateSeconds").type(JsonFieldType.NUMBER).description("Max Acitvate Seconds."),
                        fieldWithPath("maxDailyOps").type(JsonFieldType.NUMBER).description("Max Daily Operations."),
                        fieldWithPath("minActivateSeconds").type(JsonFieldType.NUMBER).description("Min Activate Seconds."),
                        fieldWithPath("minRestartSeconds").type(JsonFieldType.NUMBER).description("Min Restart Seconds."),
                        fieldWithPath("daySelection").type(JsonFieldType.ARRAY).description("Day Selection."),
                        fieldWithPath("holidaySchedule.id").type(JsonFieldType.NUMBER).description("Holiday Schedule ID."),
                        fieldWithPath("holidaySchedule.name").type(JsonFieldType.STRING).description("Holiday Schedule Name.").optional(),
                        fieldWithPath("holidayUsage").type(JsonFieldType.STRING).description("Holiday Usages."),
                        fieldWithPath("maxHoursDaily").type(JsonFieldType.NUMBER).description("Max Hours Daily."),
                        fieldWithPath("maxHoursMonthly").type(JsonFieldType.NUMBER).description("Max Hours Monthly."),
                        fieldWithPath("maxHoursAnnually").type(JsonFieldType.NUMBER).description("Max Hours Annually."),
                        fieldWithPath("maxHoursSeasonal").type(JsonFieldType.NUMBER).description("Max Hours Seasonally."))))
                .accept("application/json")
                .contentType("application/json")
                .header("Authorization","Bearer " + ApiCallHelper.authToken)
                .when()
                .get(ApiCallHelper.getProperty("getProgramConstraint") + constraintId)
                .then()
                .extract()
                .response();

        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @Test(dependsOnMethods = {"Test_ProgramConstraint_Get"})
    public void Test_ProgramConstraint_Update() {
        programConstraint.setMaxActivateSeconds(100);
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", requestFields(programConstraintFieldDescriptor),
                                    responseFields(fieldWithPath("id").type(JsonFieldType.NUMBER).description("Program Constraint."))))
                                .accept("application/json")
                                .contentType("application/json")
                                .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                .body(programConstraint)
                                .when()
                                .post(ApiCallHelper.getProperty("updateProgramConstraint") + constraintId)
                                .then()
                                .extract()
                                .response();

        assertTrue("Constraint ID should not be Null", response.path("id").toString() != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

   @Test(priority=4)
   public void Test_ProgramConstraint_Delete() {

        MockLMDto deleteConstraint = MockLMDto.builder().name("Program Constraint").build();

       Response response = given(documentationSpec)
                               .filter(document("{ClassName}/{methodName}", requestFields(
                                   fieldWithPath("name").type(JsonFieldType.STRING).description("Program Constraint Name.")), 
                                   responseFields(fieldWithPath("id").type(JsonFieldType.NUMBER).description("Program Constraint ID."))))
                               .accept("application/json")
                               .contentType("application/json")
                               .header("Authorization","Bearer " + ApiCallHelper.authToken)
                               .body(deleteConstraint)
                               .when()
                               .delete(ApiCallHelper.getProperty("deleteProgramConstraint") + constraintId)
                               .then()
                               .extract()
                               .response();

       assertTrue("Status code should be 200", response.statusCode() == 200);
   }
}
