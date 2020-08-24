package com.cannontech.rest.api.documentation.constraint;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.constraint.request.MockProgramConstraint;
import com.cannontech.rest.api.documentation.DocumentationBase;
import com.cannontech.rest.api.documentation.DocumentationFields;
import com.cannontech.rest.api.documentation.DocumentationFields.Copy;
import com.cannontech.rest.api.documentation.DocumentationFields.Create;
import com.cannontech.rest.api.documentation.DocumentationFields.Delete;
import com.cannontech.rest.api.documentation.DocumentationFields.Get;
import com.cannontech.rest.api.documentation.DocumentationFields.Update;
import com.cannontech.rest.api.dr.helper.ProgramConstraintHelper;

public class ProgramConstraintApiDoc  extends DocumentationBase {

    private String constraintId = null;
    public final static String idStr = "id";
    public final static String idDescStr = "Program Constraint Id";

    private MockProgramConstraint getMockObject() {
        return ProgramConstraintHelper.buildProgramConstraint();
    }

    private static List<FieldDescriptor> getFieldDescriptors() {
        FieldDescriptor[] programConstraintFieldDescriptor = new FieldDescriptor[] {
                fieldWithPath("name").type(JsonFieldType.STRING).description("Program Constraint Name."),
                fieldWithPath("seasonSchedule.id").type(JsonFieldType.NUMBER).description("Season Schedule Id."),
                fieldWithPath("seasonSchedule.name").type(JsonFieldType.STRING).optional().description("Season Schedule Name."),
                fieldWithPath("maxActivateSeconds").type(JsonFieldType.NUMBER).description("Max Acitvate Seconds. Min Value: 0, Max Value: 99999"),
                fieldWithPath("maxDailyOps").type(JsonFieldType.NUMBER).description("Max Daily Operations. Min Value: 0, Max Value: 99999"),
                fieldWithPath("minActivateSeconds").type(JsonFieldType.NUMBER).description("Min Activate Seconds. Min Value: 0, Max Value: 99999"),
                fieldWithPath("minRestartSeconds").type(JsonFieldType.NUMBER).description("Min Restart Seconds. Min Value: 0, Max Value: 99999"),
                fieldWithPath("daySelection").type(JsonFieldType.VARIES).description("Day Selection. Expected: SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY"),
                fieldWithPath("holidaySchedule.id").type(JsonFieldType.NUMBER).description("Holiday Schedule Id."),
                fieldWithPath("holidaySchedule.name").type(JsonFieldType.STRING).optional().description("Holiday Schedule Name."),
                fieldWithPath("holidayUsage").type(JsonFieldType.STRING).description("Holiday Usages. Expected: EXCLUDE, FORCE, NONE"),
                fieldWithPath("maxHoursDaily").type(JsonFieldType.NUMBER).description("Max Hours Daily. Min Value: 0, Max Value: 99999"),
                fieldWithPath("maxHoursMonthly").type(JsonFieldType.NUMBER).description("Max Hours Monthly. Min Value: 0, Max Value: 99999"),
                fieldWithPath("maxHoursAnnually").type(JsonFieldType.NUMBER).description("Max Hours Annually. Min Value: 0, Max Value: 99999"),
                fieldWithPath("maxHoursSeasonal").type(JsonFieldType.NUMBER).description("Max Hours Seasonally. Min Value: 0, Max Value: 99999") };
        
        return new ArrayList<>(Arrays.asList(programConstraintFieldDescriptor));
    }

    @Test
    public void Test_ProgramConstraint_Create() {
        constraintId = createDoc();
    }

    @Test(dependsOnMethods = "Test_ProgramConstraint_Create")
    public void Test_ProgramConstraint_Get() {
        getDoc();
    }

    @Test(dependsOnMethods = "Test_ProgramConstraint_Get")
    public void Test_ProgramConstraint_Update() {
        updateDoc();
    }

    @Test(dependsOnMethods = "Test_ProgramConstraint_Update")
    public void Test_ProgramConstraint_Delete() {
        deleteDoc();
    }

    @Override
    protected Get buildGetFields() {
        List<FieldDescriptor> responseFields = getFieldDescriptors();
        responseFields.add(0, fieldWithPath(idStr).type(JsonFieldType.NUMBER).description(idDescStr));
        String url = ApiCallHelper.getProperty("getProgramConstraint") + constraintId;
        return new DocumentationFields.Get(responseFields, url);
    }

    @Override
    protected Create buildCreateFields() {
        List<FieldDescriptor> requestFields = getFieldDescriptors();
        List<FieldDescriptor> responseFields = Arrays.asList(new FieldDescriptor[] {
                fieldWithPath(idStr).type(JsonFieldType.NUMBER).description(idDescStr) });
        String url = ApiCallHelper.getProperty("saveProgramConstraint");
        return new DocumentationFields.Create(requestFields, responseFields, idStr, idDescStr, getMockObject(), url);
    }

    @Override
    protected Update buildUpdateFields() {
        MockProgramConstraint programConstraint = getMockObject();
        programConstraint.setMaxActivateSeconds(100);
        List<FieldDescriptor> requestFields = getFieldDescriptors();
        List<FieldDescriptor> responseFields = Arrays.asList(new FieldDescriptor[] {
                fieldWithPath(idStr).type(JsonFieldType.NUMBER).description(idDescStr) });
        String url = ApiCallHelper.getProperty("updateProgramConstraint") + constraintId;
        return new DocumentationFields.Update(requestFields, responseFields, idStr, idDescStr, programConstraint, url);
    }

    @Override
    protected Delete buildDeleteFields() {
        List<FieldDescriptor> responseFields = Arrays.asList(new FieldDescriptor[] {
                fieldWithPath("id").type(JsonFieldType.NUMBER).description("Program Constraint Id.") });
        List<FieldDescriptor> requestFields = Arrays.asList(new FieldDescriptor[] {
                fieldWithPath("name").type(JsonFieldType.STRING).description("Program Constraint Name.") });
        MockLMDto deleteConstraint = MockLMDto.builder().name("Program Constraint").build();
        String url = ApiCallHelper.getProperty("deleteProgramConstraint") + constraintId;
        return new DocumentationFields.DeleteWithBody(requestFields, responseFields, deleteConstraint, url);
    }

    @Override
    protected Copy buildCopyFields() {
        return null;
    }
}
