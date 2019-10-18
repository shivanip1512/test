package com.cannontech.rest.api.dr.helper;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.constraint.request.MockDayOfWeek;
import com.cannontech.rest.api.constraint.request.MockHolidayUsage;
import com.cannontech.rest.api.constraint.request.MockProgramConstraint;
import com.cannontech.rest.api.utilities.Log;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class ProgramConstraintHelper {
    public final static String CONTEXT_PROGRAM_CONSTRAINT_ID = "id";
    public final static String CONTEXT_PROGRAM_CONSTRAINT_NAME = "constraintName";

    public static MockProgramConstraint buildProgramConstraint() {

        MockLMDto seasonSchedule = MockLMDto.builder().id(1).build();

        MockLMDto holidaySchedule = MockLMDto.builder().id(1).build();

        List<MockDayOfWeek> daySelection = new ArrayList<>();
        daySelection.add(MockDayOfWeek.MONDAY);
        daySelection.add(MockDayOfWeek.SUNDAY);

        MockProgramConstraint programConstraint = MockProgramConstraint.builder()
                                                                       .name("Program Constraint")
                                                                       .holidayUsage(MockHolidayUsage.EXCLUDE)
                                                                       .holidaySchedule(holidaySchedule)
                                                                       .daySelection(daySelection)
                                                                       .maxActivateSeconds(10)
                                                                       .maxDailyOps(11)
                                                                       .maxHoursAnnually(17)
                                                                       .maxHoursMonthly(16)
                                                                       .maxHoursSeasonal(18)
                                                                       .maxHoursDaily(15)
                                                                       .minActivateSeconds(12)
                                                                       .minRestartSeconds(13)
                                                                       .seasonSchedule(seasonSchedule)
                                                                       .build();

        return programConstraint;
    }

    public static void deleteProgramConstraint(String name, String constraintId) {

        Log.info("Delete Program Constraint is : " + name);
        MockLMDto deleteConstraint = MockLMDto.builder().name("Program Constraint").build();

        Response response = given().accept("application/json")
                                   .contentType("application/json")
                                   .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                   .body(deleteConstraint)
                                   .when()
                                   .delete(ApiCallHelper.getProperty("deleteProgramConstraint") + constraintId)
                                   .then()
                                   .extract()
                                   .response();

        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * This method creates Program Constraint and returns Program Constraint object
     * @returns programConstraint
     */
    public static MockProgramConstraint createProgramConstraint() {
        Random rand = new Random();
        int randomInt = rand.nextInt(10000);
        String name = "ProgramConstraint" + randomInt;
        Integer constraintId = null;

        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setName(name);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("createProgramConstraint", programConstraint);
        constraintId = createResponse.path("id");
        assertTrue("Constraint ID should not be Null", constraintId != null);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);

        programConstraint.setId(constraintId);

        return programConstraint;
    }
}
