package com.cannontech.rest.api.dr.programconstraint;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.constraint.request.MockDayOfWeek;
import com.cannontech.rest.api.constraint.request.MockHolidayUsage;
import com.cannontech.rest.api.constraint.request.MockProgramConstraint;
import com.cannontech.rest.api.dr.helper.ProgramConstraintHelper;
import com.cannontech.rest.api.utilities.Log;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class ProgramConstraintApiTest {
    /**
     * This test case validates creation of Program Constraint with default values
     */
    @Test
    public void programConstraint_01_Create(ITestContext context) {

        Log.startTestCase("programConstraint_01_Create");
        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveProgramConstraint", programConstraint);
        context.setAttribute("prgConstraintId", createResponse.path(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID));
        assertTrue(createResponse.statusCode() == 200, "Status code should be 200");
        assertTrue(createResponse.path(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID) != null,
                "Program Constraint Id should not be Null");
        programConstraint.setId(createResponse.path(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID));
        context.setAttribute("expectedProgramConstraint", programConstraint);
        Log.endTestCase("programConstraint_01_Create");
    }

    /**
     * This test case validate fields of Program Constraint created in programConstraint_01_Create
     */
    @Test(dependsOnMethods = "programConstraint_01_Create")
    public void programConstraint_02_Get(ITestContext context) {

        Log.startTestCase("programConstraint_02_Get");
        Log.info("Group Id of programConstraint created is : " + context.getAttribute("prgConstraintId"));
        ExtractableResponse<?> getResponse = ApiCallHelper.get("getProgramConstraint",
                context.getAttribute("prgConstraintId").toString());
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);
        MockProgramConstraint programConstraintGetResponse = getResponse.as(MockProgramConstraint.class);
        context.setAttribute("ProgConstName", programConstraintGetResponse.getName());
        MockProgramConstraint programConstraint = (MockProgramConstraint) context.getAttribute("expectedProgramConstraint");
        assertTrue(programConstraint.getName().equals(programConstraintGetResponse.getName()),
                "Name Should be : " + programConstraintGetResponse.getName());
        assertTrue(programConstraint.getMaxActivateSeconds() == programConstraintGetResponse.getMaxActivateSeconds(),
                ".MaxActivateSeconds Should be : " + programConstraintGetResponse.getMaxActivateSeconds());
        assertTrue(programConstraint.getMaxDailyOps().equals(programConstraintGetResponse.getMaxDailyOps()),
                "kWCapacity Should be : " + programConstraintGetResponse.getMaxDailyOps());
        assertTrue(programConstraint.getMinActivateSeconds() == programConstraintGetResponse.getMinActivateSeconds(),
                ".MinActivateSeconds Should be : " + programConstraintGetResponse.getMinActivateSeconds());
        assertTrue(programConstraint.getMinRestartSeconds() == programConstraintGetResponse.getMinRestartSeconds(),
                ".MinActivateSeconds Should be : " + programConstraintGetResponse.getMinRestartSeconds());
        assertTrue(programConstraint.getHolidayUsage().equals(programConstraintGetResponse.getHolidayUsage()),
                "HolidayUsage Should be disabled : ");
        Log.endTestCase("programConstraint_02_Get");
    }

    /**
     * This test case updates name of Program Constraint created in programConstraint_01_Create
     */
    @Test(dependsOnMethods = "programConstraint_02_Get")
    public void programConstraint_03_Update(ITestContext context) {

        Log.startTestCase("programConstraint_03_Update");
        MockProgramConstraint programConstraint = (MockProgramConstraint) context.getAttribute("expectedProgramConstraint");
        String name = "ProgramConstraintTest_Name_Update";
        programConstraint.setName(name);
        context.setAttribute("programConstraint_Name", name);
        ExtractableResponse<?> updatedResponse = ApiCallHelper.post("updateProgramConstraint",
                programConstraint,
                context.getAttribute("prgConstraintId").toString());
        assertTrue(updatedResponse.statusCode() == 200, "Status code should be 200");
        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getProgramConstraint",
                context.getAttribute("prgConstraintId").toString());
        MockProgramConstraint updatedProgramConstraintResponse = getupdatedResponse.as(MockProgramConstraint.class);
        assertTrue(name.equals(updatedProgramConstraintResponse.getName()), "Name Should be : " + name);
        Log.endTestCase("programConstraint_03_Update");
    }

    /**
     * This test case create Program Constraint with the same name that is created in
     * programConstraint_03_Update
     */

    @Test(dependsOnMethods = "programConstraint_03_Update")
    public void programConstraint_04_CreateWithExistingName(ITestContext context) {

        Log.startTestCase("programConstraint_04_CreateWithExistingName");
        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setName("programConstraint_Name");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveProgramConstraint", programConstraint);
        assertTrue(createResponse.statusCode() == 200, "Status code should be 200");
        assertTrue(createResponse.path(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID) != null,
                "Program Constraint Id should not be Null");
        Log.endTestCase("programConstraint_04_CreateWithExistingName");
    }

    /**
     * This test case deletes Program Constraint created in programConstraint_01_Create
     */
    @Test(dependsOnMethods = "programConstraint_04_CreateWithExistingName")
    public void programConstraint_05_Delete(ITestContext context) {

        Log.startTestCase("programConstraint_05_Delete");
        MockLMDto lmDeleteObject = MockLMDto.builder().name(context.getAttribute("programConstraint_Name").toString()).build();
        Log.info("Delete Load Group is : " + lmDeleteObject);
        ExtractableResponse<?> deleteResponse = ApiCallHelper.delete("deleteProgramConstraint",
                lmDeleteObject,
                context.getAttribute("prgConstraintId").toString());
        assertTrue(deleteResponse.statusCode() == 200, "Status code should be 200");
        // Get request to validate Program Constraint is deleted
        ExtractableResponse<?> getDeletedLoadGroupResponse = ApiCallHelper.get("getProgramConstraint",
                context.getAttribute("prgConstraintId").toString());
        assertTrue(getDeletedLoadGroupResponse.statusCode() == 400, "Status code should be 400");
        assertTrue(ValidationHelper.validateErrorMessage(getDeletedLoadGroupResponse, "Constraint Id not found"),
                "Expected error message Should contains Text: " + "Program Constraint Id not found");
        Log.endTestCase("programConstraint_05_Delete");
    }

    /**
     * Negative validation when Program Constraint name field is passed value as blank while creation of Program Constraint
     */
    @Test
    public void programConstraint_06_NameAsBlankValidation(ITestContext context) {

        Log.startTestCase("programConstraint_06_Name_As_Blank_Validation");
        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        String name = " ";
        programConstraint.setName(name);
        context.setAttribute("programConstraint_Name", name);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveProgramConstraint", programConstraint);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Name is required."),
                "Expected Error not found:" + "Name is required.");
        Log.endTestCase("programConstraint_06_Name_As_Blank_Validation");
    }

    /**
     * Negative validation when Program Constraint name is passed value with more than 60 characters while creation of Program
     * Constraint
     */
    @Test
    public void programConstraint_07_NameWithMoreThanSixtyCharValidation(ITestContext context) {

        Log.startTestCase("programConstraint_07_NameWithMoreThanSixtyCharValidation");
        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        String name = "TestProgramConstraintName_MoreThanSixtyCharacter_TestProgramConstraintNames";
        programConstraint.setName(name);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveProgramConstraint", programConstraint);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Exceeds maximum length of 60."),
                "Expected Error not found:" + "Exceeds maximum length of 60.");
        Log.endTestCase("programConstraint_07_NameWithMoreThanSixtyCharValidation");
    }

    /**
     * Negative validation when Program Constraint name is passed value with special characters | , / while creation of Program
     * Constraint
     */
    @Test
    public void programConstraint_08_NameWithSpecialCharValidation(ITestContext context) {

        Log.startTestCase("programConstraint_08_NameWithSpecialCharValidation");
        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        String name = "programConstraint_\\,\"Name";
        programConstraint.setName(name);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveProgramConstraint", programConstraint);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "name",
                        "Cannot be blank or include any of the following characters: / \\ , ' \" |"),
                "Expected Error not found:" + "Cannot be blank or include any of the following characters: / \\ , ' \" |");
        Log.endTestCase("programConstraint_08_NameWithSpecialCharValidation");
    }

    /**
     * Negative validation when Program Constraint is created with MaxActivateSeconds field less than Min Value
     */
    @Test
    public void programConstraint_09_MaxActivateMinValueValidation() {

        Log.startTestCase("programConstraint_09_MaxActivateMinValueValidation");
        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setMaxActivateSeconds(-1);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveProgramConstraint", programConstraint);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "maxActivateSeconds", "Must be between 0 and 99,999."),
                "Expected Error not found:" + "Must be between 0 and 99,999.");
        Log.endTestCase("programConstraint_09_MaxActivateMinValueValidation");
    }

    /**
     * Negative validation when Program Constraint is created with MaxActivateSeconds field greater than Max Value
     */
    @Test
    public void programConstraint_10_MaxActivateMaxValueValidation() {

        Log.startTestCase("programConstraint_10_MaxActivateMaxValueValidation");
        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setMaxActivateSeconds(100000);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveProgramConstraint", programConstraint);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "maxActivateSeconds", "Must be between 0 and 99,999."),
                "Expected Error not found:" + "Must be between 0 and 99,999.");
        Log.endTestCase("programConstraint_10_MaxActivateMaxValueValidation");
    }

    /**
     * Negative validation when Program Constraint is created with MaxDailyOps field less than Min Value
     */
    @Test
    public void programConstraint_11_MaxDailyOpsMinValueValidation() {

        Log.startTestCase("programConstraint_11_MaxDailyOpsMinValueValidation");
        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setMaxDailyOps(-1);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveProgramConstraint", programConstraint);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "maxDailyOps", "Must be between 0 and 99,999."),
                "Expected Error not found:" + "Must be between 0 and 99,999.");
        Log.endTestCase("programConstraint_11_MaxDailyOpsMinValueValidation");
    }

    /**
     * Negative validation when Program Constraint is created with MaxDailyOps field greater than Max Value
     */
    @Test
    public void programConstraint_12_MaxDailyOpsMaxValueValidation() {

        Log.startTestCase("programConstraint_12_MaxDailyOpsMaxValueValidation");
        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setMaxDailyOps(100000);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveProgramConstraint", programConstraint);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "maxDailyOps", "Must be between 0 and 99,999."),
                "Expected Error not found:" + "Must be between 0 and 99,999.");
        Log.endTestCase("programConstraint_12_MaxDailyOpsMaxValueValidation");
    }

    /**
     * Negative validation when Program Constraint is created with MaxActivateSeconds field less than Min Value
     */
    @Test
    public void programConstraint_13_MinActivateMinValueValidation() {

        Log.startTestCase("programConstraint_13_MinActivateMinValueValidation");
        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setMinActivateSeconds(-1);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveProgramConstraint", programConstraint);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "minActivateSeconds", "Must be between 0 and 99,999."),
                "Expected Error not found:" + "Must be between 0 and 99,999.");
        Log.endTestCase("programConstraint_13_MinActivateMinValueValidation");
    }

    /**
     * Negative validation when Program Constraint is created with MaxActivateSeconds field greater than Max Value
     */
    @Test
    public void programConstraint_14_MinActivateMaxValueValidation() {

        Log.startTestCase("programConstraint_14_MinActivateMaxValueValidation");
        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setMinActivateSeconds(100000);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveProgramConstraint", programConstraint);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "minActivateSeconds", "Must be between 0 and 99,999."),
                "Expected Error not found:" + "Must be between 0 and 99,999.");
        Log.endTestCase("programConstraint_14_MinActivateMaxValueValidation");
    }

    /**
     * Negative validation when Program Constraint is created with MinRestartSeconds field less than Min Value
     */
    @Test
    public void programConstraint_15_MinRestartMinValueValidation() {

        Log.startTestCase("programConstraint_15_MinRestartMinValueValidation");
        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setMinRestartSeconds(-1);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveProgramConstraint", programConstraint);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "minRestartSeconds", "Must be between 0 and 99,999."),
                "Expected Error not found:" + "Must be between 0 and 99,999.");
        Log.endTestCase("programConstraint_15_MinRestartMinValueValidation");
    }

    /**
     * Negative validation when Program Constraint is created with MinRestartSeconds field greater than Max Value
     */
    @Test
    public void programConstraint_16_MinRestartMaxValueValidation() {

        Log.startTestCase("programConstraint_16_MinRestartMaxValueValidation");
        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setMinRestartSeconds(100000);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveProgramConstraint", programConstraint);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "minRestartSeconds", "Must be between 0 and 99,999."),
                "Expected Error not found:" + "Must be between 0 and 99,999.");
        Log.endTestCase("programConstraint_16_MinRestartMaxValueValidation");
    }

    /**
     * Negative validation when Program Constraint is created with Max Hour Allowance: Daily for less than Min Value
     */
    @Test
    public void programConstraint_17_DailyMinValueValidation() {

        Log.startTestCase("programConstraint_17_DailyMinValueValidation");
        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setMaxHoursDaily(-1);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveProgramConstraint", programConstraint);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "maxHoursDaily", "Must be between 0 and 99,999."),
                "Expected Error not found:" + "Must be between 0 and 99,999.");
        Log.endTestCase("programConstraint_17_DailyMinValueValidation");
    }

    /**
     * Negative validation when Program Constraint is created with Max Hour Allowance: Daily for grater than Max Value
     */
    @Test
    public void programConstraint_18_DailyMaxValueValidation() {

        Log.startTestCase("programConstraint_18_DailyMaxValueValidation");
        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setMaxHoursDaily(100000);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveProgramConstraint", programConstraint);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "maxHoursDaily", "Must be between 0 and 99,999."),
                "Expected Error not found:" + "Must be between 0 and 99,999.");
        Log.endTestCase("programConstraint_18_DailyMaxValueValidation");
    }

    /**
     * Negative validation when Program Constraint is created with Max Hour Allowance: Daily for less than Min Value
     */
    @Test
    public void programConstraint_19_MonthlyMinValueValidation() {

        Log.startTestCase("programConstraint_19_MonthlyMinValueValidation");
        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setMaxHoursMonthly(-1);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveProgramConstraint", programConstraint);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "maxHoursMonthly", "Must be between 0 and 99,999."),
                "Expected Error not found:" + "Must be between 0 and 99,999.");
        Log.endTestCase("programConstraint_19_MonthlyMinValueValidation");
    }

    /**
     * Negative validation when Program Constraint is created with Max Hour Allowance: Daily for grater than Max Value
     */
    @Test
    public void programConstraint_20_MonthlyMaxValueValidation() {

        Log.startTestCase("programConstraint_20_MonthlyMaxValueValidation");
        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setMaxHoursMonthly(100000);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveProgramConstraint", programConstraint);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "maxHoursMonthly", "Must be between 0 and 99,999."),
                "Expected Error not found:" + "Must be between 0 and 99,999.");
        Log.endTestCase("programConstraint_20_MonthlyMaxValueValidation");
    }

    /**
     * This test case validates creation of Program Constraint with All Days selection and HolidayUsage as FORCE
     */
    @Test
    public void programConstraint_21_CreateWithDaySelectionWithAllYes() {

        Log.startTestCase("programConstraint_21_CreateWithDaySelectionWithAllYes");
        List<MockDayOfWeek> daySelection = new ArrayList<>();
        daySelection.add(MockDayOfWeek.MONDAY);
        daySelection.add(MockDayOfWeek.SUNDAY);
        daySelection.add(MockDayOfWeek.TUESDAY);
        daySelection.add(MockDayOfWeek.WEDNESDAY);
        daySelection.add(MockDayOfWeek.THURSDAY);
        daySelection.add(MockDayOfWeek.FRIDAY);
        daySelection.add(MockDayOfWeek.SATURDAY);
        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setDaySelection(daySelection);
        programConstraint.setHolidayUsage(MockHolidayUsage.FORCE);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveProgramConstraint", programConstraint);
        assertTrue(createResponse.statusCode() == 200, "Status code should be 200");
        assertTrue(createResponse.path(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID) != null,
                "Program Constraint Id should not be Null");
        Log.endTestCase("programConstraint_21_CreateWithDaySelectionWithAllYes");
    }

    /**
     * This test case validates creation of Program Constraint with NO Day selection and HolidayUsage as EXCLUDE
     */
    @Test
    public void programConstraint_22_CreateWithDaySelectionWithAllNo() {

        Log.startTestCase("programConstraint_22_CreateWithDaySelectionWithAllNo");
        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setDaySelection(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveProgramConstraint", programConstraint);
        assertTrue(createResponse.path(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID) != null,
                "Program Constraint Id should not be Null");
        assertTrue(createResponse.statusCode() == 200, "Status code should be 200");
        Log.endTestCase("programConstraint_22_CreateWithDaySelectionWithAllNo");
    }
}