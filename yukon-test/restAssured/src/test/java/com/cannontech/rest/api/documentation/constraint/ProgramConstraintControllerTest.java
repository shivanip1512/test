package com.cannontech.rest.api.documentation.constraint;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

import java.lang.reflect.Method;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ProgramConstraintControllerTest {

    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private JSONObject programConstraint = null;
    private String constraintId = null;

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = 
                new RequestSpecBuilder()
                        .addFilter(documentationConfiguration(this.restDocumentation)
                            .operationPreprocessors()
                            .withRequestDefaults(removeHeaders("Authorization"), 
                                                 removeHeaders("Accept"),
                                                 removeHeaders("Host"),
                                                 removeHeaders("Content-Type"),
                                                 removeHeaders("Content-Length"),
                                                 prettyPrint())
                            .withResponseDefaults(removeHeaders("X-Frame-Options"),
                                                  removeHeaders("X-Content-Type-Options"),
                                                  removeHeaders("Content-Security-Policy"),
                                                  removeHeaders("Strict-Transport-Security"),
                                                  removeHeaders("X-XSS-Protection"),
                                                  removeHeaders("Content-Length"),
                                                  removeHeaders("Content-Type"),
                                                  removeHeaders("Date"),
                                                  prettyPrint())
                            .and()
                            .snippets()
                            .withDefaults(HttpDocumentation.httpRequest(), HttpDocumentation.httpResponse()))
                        .build();
        if(this.programConstraint == null)
            this.programConstraint = buildProgramConstraint();
    }

    @SuppressWarnings("unchecked")
    public JSONObject buildProgramConstraint() {
        JSONObject programConstraint = new JSONObject();

        JSONObject schedule = new JSONObject();
        schedule.put("id", Integer.valueOf(1));

        JSONArray daySelection = new JSONArray();
        daySelection.add("SUNDAY");
        daySelection.add("MONDAY");

        programConstraint.put("name", "Program Constraint");
        programConstraint.put("seasonSchedule", schedule);
        programConstraint.put("maxActivateSeconds", 10);
        programConstraint.put("maxDailyOps", 11);
        programConstraint.put("minActivateSeconds", 12);
        programConstraint.put("minRestartSeconds", 13);
        programConstraint.put("daySelection", daySelection);
        programConstraint.put("holidaySchedule", schedule);
        programConstraint.put("holidayUsage", "FORCE");
        programConstraint.put("maxHoursDaily", 15);
        programConstraint.put("maxHoursMonthly", 16);
        programConstraint.put("maxHoursAnnually", 17);
        programConstraint.put("maxHoursSeasonal", 18);

        return programConstraint;
    }

    @AfterMethod
    public void tearDown() {
        this.restDocumentation.afterTest();
    }

    @Test()
    public void Test_ProgramConstraint_Create() {
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", requestFields(
                                    fieldWithPath("name").type(JsonFieldType.STRING).description("Program Constraint Name."), 
                                    fieldWithPath("seasonSchedule.id").type(JsonFieldType.NUMBER).description("Season Schedule ID."),
                                    fieldWithPath("maxActivateSeconds").type(JsonFieldType.NUMBER).description("Max Acitvate Seconds."),
                                    fieldWithPath("maxDailyOps").type(JsonFieldType.NUMBER).description("Max Daily Operations."),
                                    fieldWithPath("minActivateSeconds").type(JsonFieldType.NUMBER).description("Min Activate Seconds.").attributes(),
                                    fieldWithPath("minRestartSeconds").type(JsonFieldType.NUMBER).description("Min Restart Seconds."),
                                    fieldWithPath("daySelection").type(JsonFieldType.VARIES).description("Day Selection. Expected: SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY"),
                                    fieldWithPath("holidaySchedule.id").type(JsonFieldType.NUMBER).description("Holiday Schedule ID."),
                                    fieldWithPath("holidayUsage").type(JsonFieldType.STRING).description("Holiday Usages."),
                                    fieldWithPath("maxHoursDaily").type(JsonFieldType.NUMBER).description("Max Hours Daily."),
                                    fieldWithPath("maxHoursMonthly").type(JsonFieldType.NUMBER).description("Max Hours Monthly."),
                                    fieldWithPath("maxHoursAnnually").type(JsonFieldType.NUMBER).description("Max Hours Annually."),
                                    fieldWithPath("maxHoursSeasonal").type(JsonFieldType.NUMBER).description("Max Hours Seasonally.")),
                                    responseFields(fieldWithPath("id").type(JsonFieldType.NUMBER).description("Program Constraint."))))
                                .accept("application/json")
                                .contentType("application/json")
                                .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                .body(programConstraint.toJSONString())
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
                        fieldWithPath("seasonSchedule.name").type(JsonFieldType.STRING).description("Season Schedule Name."),
                        fieldWithPath("maxActivateSeconds").type(JsonFieldType.NUMBER).description("Max Acitvate Seconds."),
                        fieldWithPath("maxDailyOps").type(JsonFieldType.NUMBER).description("Max Daily Operations."),
                        fieldWithPath("minActivateSeconds").type(JsonFieldType.NUMBER).description("Min Activate Seconds."),
                        fieldWithPath("minRestartSeconds").type(JsonFieldType.NUMBER).description("Min Restart Seconds."),
                        fieldWithPath("daySelection").type(JsonFieldType.ARRAY).description("Day Selection. Expected: SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY"),
                        fieldWithPath("holidaySchedule.id").type(JsonFieldType.NUMBER).description("Holiday Schedule ID."),
                        fieldWithPath("holidaySchedule.name").type(JsonFieldType.STRING).description("Holiday Schedule Name."),
                        fieldWithPath("holidayUsage").type(JsonFieldType.STRING).description("Holiday Usages. Expected: EXCLUDE, FORCE, NONE"),
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

    @SuppressWarnings("unchecked")
    @Test(dependsOnMethods = {"Test_ProgramConstraint_Get"})
    public void Test_ProgramConstraint_Update() {
        programConstraint.put("maxActivateSeconds", 100);
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", requestFields(
                                    fieldWithPath("name").type(JsonFieldType.STRING).description("Program Constraint Name."),
                                    fieldWithPath("seasonSchedule.id").type(JsonFieldType.NUMBER).description("Season Schedule ID."),
                                    fieldWithPath("maxActivateSeconds").type(JsonFieldType.NUMBER).description("Max Acitvate Seconds."),
                                    fieldWithPath("maxDailyOps").type(JsonFieldType.NUMBER).description("Max Daily Operations."),
                                    fieldWithPath("minActivateSeconds").type(JsonFieldType.NUMBER).description("Min Activate Seconds."),
                                    fieldWithPath("minRestartSeconds").type(JsonFieldType.NUMBER).description("Min Restart Seconds."),
                                    fieldWithPath("daySelection").type(JsonFieldType.ARRAY).description("Day Selection. Expected: SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY"),
                                    fieldWithPath("holidaySchedule.id").type(JsonFieldType.NUMBER).description("Holiday Schedule ID."),
                                    fieldWithPath("holidayUsage").type(JsonFieldType.STRING).description("Holiday Usages."),
                                    fieldWithPath("maxHoursDaily").type(JsonFieldType.NUMBER).description("Max Hours Daily."),
                                    fieldWithPath("maxHoursMonthly").type(JsonFieldType.NUMBER).description("Max Hours Monthly."),
                                    fieldWithPath("maxHoursAnnually").type(JsonFieldType.NUMBER).description("Max Hours Annually."),
                                    fieldWithPath("maxHoursSeasonal").type(JsonFieldType.NUMBER).description("Max Hours Seasonally.")),
                                    responseFields(fieldWithPath("id").type(JsonFieldType.NUMBER).description("Program Constraint."))))
                                .accept("application/json")
                                .contentType("application/json")
                                .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                .body(programConstraint.toJSONString())
                                .when()
                                .post(ApiCallHelper.getProperty("updateProgramConstraint") + constraintId)
                                .then()
                                .extract()
                                .response();

        assertTrue("Constraint ID should not be Null", response.path("id").toString() != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

   @SuppressWarnings("unchecked")
   @Test(priority=4)
   public void Test_ProgramConstraint_Delete() {
       JSONObject deleteConstraint = new JSONObject();
       deleteConstraint.put("name", "Program Constraint");
       Response response = given(documentationSpec)
                               .filter(document("{ClassName}/{methodName}", requestFields(
                                   fieldWithPath("name").type(JsonFieldType.STRING).description("Program Constraint Name.")), 
                                   responseFields(fieldWithPath("id").type(JsonFieldType.NUMBER).description("Program Constraint ID."))))
                               .accept("application/json")
                               .contentType("application/json")
                               .header("Authorization","Bearer " + ApiCallHelper.authToken)
                               .body(deleteConstraint.toJSONString())
                               .when()
                               .delete(ApiCallHelper.getProperty("deleteProgramConstraint") + constraintId)
                               .then()
                               .extract()
                               .response();

       assertTrue("Status code should be 200", response.statusCode() == 200);
   }
}
