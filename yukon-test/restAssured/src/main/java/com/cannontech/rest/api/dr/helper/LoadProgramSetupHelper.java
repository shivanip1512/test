package com.cannontech.rest.api.dr.helper;

import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.ITestContext;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.ApiUtils;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.gear.fields.MockGearControlMethod;
import com.cannontech.rest.api.gear.fields.MockOperationalState;
import com.cannontech.rest.api.gear.fields.MockProgramGear;
import com.cannontech.rest.api.gear.fields.MockProgramGearFields;
import com.cannontech.rest.api.loadProgram.request.MockLoadProgram;
import com.cannontech.rest.api.loadProgram.request.MockLoadProgramCopy;
import com.cannontech.rest.api.loadProgram.request.MockNotification;
import com.cannontech.rest.api.loadProgram.request.MockNotificationGroup;
import com.cannontech.rest.api.loadProgram.request.MockProgramConstraint;
import com.cannontech.rest.api.loadProgram.request.MockProgramControlWindow;
import com.cannontech.rest.api.loadProgram.request.MockProgramControlWindowFields;
import com.cannontech.rest.api.loadProgram.request.MockProgramDirectMemberControl;
import com.cannontech.rest.api.loadProgram.request.MockProgramGroup;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;

import io.restassured.response.ExtractableResponse;

public class LoadProgramSetupHelper {
    public final static String CONTEXT_PROGRAM_ID = "programId"; 
    public final static String CONTEXT_PROGRAM_NAME = "programName";
    public final static String CONTEXT_COPIED_PROGRAM_NAME = "copiedProgramName";
    
    public static MockLoadProgram buildLoadProgramRequest(MockPaoType type, List<MockLoadGroupBase> loadGroups, List <MockGearControlMethod> gearTypes, Integer constraintId) {

        List<MockProgramGear> gears = new ArrayList<>();
        Integer gearOrder = 1;
        for (MockGearControlMethod gearType : gearTypes) {
            MockProgramGearFields gearFields = GearFieldHelper.createProgramGearFields(gearType);
            MockProgramGear gear = MockProgramGear.builder()
                                          .controlMethod(gearType)
                                          .gearName("TestGear" + gearOrder)
                                          .gearNumber(gearOrder)
                                          .fields(gearFields)
                                          .build();
            gears.add(gear);
            gearOrder++;
        };

        MockProgramControlWindowFields controlWindowOne = MockProgramControlWindowFields.builder()
                                                                                .availableStartTimeInMinutes(0)
                                                                                .availableStopTimeInMinutes(0)
                                                                                .build();

        MockProgramControlWindowFields controlWindowTwo = MockProgramControlWindowFields.builder()
                                                                                .availableStartTimeInMinutes(1)
                                                                                .availableStopTimeInMinutes(5)
                                                                                .build();
        MockProgramControlWindow controlWindow = MockProgramControlWindow.builder()
                                                                 .controlWindowOne(controlWindowOne)
                                                                 .controlWindowTwo(controlWindowTwo)
                                                                 .build();

        List<MockProgramGroup> assignedGroups = new ArrayList<>();
        loadGroups.forEach(group -> {
            MockProgramGroup programGroup = MockProgramGroup.builder()
                                                    .groupId(group.getId())
                                                    .groupName(group.getName())
                                                    .type(group.getType())
                                                    .build();
            assignedGroups.add(programGroup);
        });

        List<MockNotificationGroup> assignedNotificationGroups = new ArrayList<>();
        assignedNotificationGroups.add(getMockNotificationGroup());

        MockNotification notification = MockNotification.builder()
                                                .notifyOnAdjust(false)
                                                .enableOnSchedule(false)
                                                .assignedNotificationGroups(assignedNotificationGroups)
                                                .build();

        MockProgramConstraint constraint = MockProgramConstraint.builder()
                                                        .constraintId(constraintId)
                                                        .build();

        return MockLoadProgram.builder()
                          .name(ApiUtils.buildFriendlyName(type, "LM_", "Test"))
                          .type(type)
                          .triggerOffset(1.0)
                          .restoreOffset(2.0)
                          .operationalState(MockOperationalState.Automatic)
                          .constraint(constraint)
                          .assignedGroups(assignedGroups)
                          .controlWindow(controlWindow)
                          .notification(notification)
                          .gears(gears)
                          .build();

    }


    public static MockLoadProgram buildLoadProgramUpdateRequest(MockPaoType type, List<MockLoadGroupBase> loadGroups,
            List<MockGearControlMethod> gearTypes, Integer constraintId, MockLoadProgram subOrdinateProgram) {
        MockLoadProgram program = buildLoadProgramRequest(type, loadGroups, gearTypes, constraintId);
        program.setMemberControl(getMockProgramDirectMemberControl(subOrdinateProgram));
        return program;
    }

    public static List<MockProgramDirectMemberControl> getMockProgramDirectMemberControl(MockLoadProgram subOrdinateLoadProgram) {
        List<MockProgramDirectMemberControl> memberControls = new ArrayList<>();
        MockProgramDirectMemberControl mockdirectProg = MockProgramDirectMemberControl.builder()
                .subordinateProgId(subOrdinateLoadProgram.getProgramId())
                .subordinateProgName(subOrdinateLoadProgram.getName())
                .build();
        memberControls.add(mockdirectProg);
        return memberControls;

    }

    public static MockLoadProgramCopy buildLoadProgramCopyRequest(MockPaoType programType, Integer constraintId) {
        return MockLoadProgramCopy.builder()
                                  .name(ApiUtils.buildFriendlyName(programType, "LM_", "TestCopy"))
                       .operationalState(MockOperationalState.Automatic)
                       .constraint(MockProgramConstraint.builder().constraintId(constraintId).build())
                       .copyMemberControl(true)
                       .build();

    }

    public static FieldDescriptor[] loadProgramFields() {
        return new FieldDescriptor[] {
                fieldWithPath("name").type(JsonFieldType.STRING).description("Load Program Name"),
                fieldWithPath("type").type(JsonFieldType.STRING).description("Load Program Type"),
                fieldWithPath("operationalState").type(JsonFieldType.STRING).description("Load program Operational State"),
                fieldWithPath("constraint.constraintId").type(JsonFieldType.NUMBER).description("Constraint Id"),
                fieldWithPath("triggerOffset").type(JsonFieldType.NUMBER).description("Trigger offset. Min Value: 0.0, Max Value: 99999.9999"),
                fieldWithPath("restoreOffset").type(JsonFieldType.NUMBER).description("Restore offset. Min Value: -9999.9999 , Max Value: 99999.9999"),

                fieldWithPath("gears[].gearName").type(JsonFieldType.STRING).description("Gear Name"),
                fieldWithPath("gears[].gearNumber").type(JsonFieldType.NUMBER).description("Gear Number"),
                fieldWithPath("gears[].controlMethod").type(JsonFieldType.STRING).description("Gear Type"), };
    }

    public static FieldDescriptor[] loadProgramControlFields() {
        return new FieldDescriptor[] {
                fieldWithPath("controlWindow.controlWindowOne.availableStartTimeInMinutes").type(JsonFieldType.NUMBER)
                                                                                           .description("Available Start Time In Minutes"),
                fieldWithPath("controlWindow.controlWindowOne.availableStopTimeInMinutes").type(JsonFieldType.NUMBER)
                                                                                          .description("Available Stop Time In Minutes"),
                fieldWithPath("controlWindow.controlWindowTwo.availableStartTimeInMinutes").type(JsonFieldType.NUMBER)
                                                                                           .description("Available Start Time In Minutes"),
                fieldWithPath("controlWindow.controlWindowTwo.availableStopTimeInMinutes").type(JsonFieldType.NUMBER)
                                                                                          .description("Available Stop Time In Minutes"),

                fieldWithPath("assignedGroups[].groupId").type(JsonFieldType.NUMBER).description("Assigned Load Group Id"),
                fieldWithPath("assignedGroups[].groupName").type(JsonFieldType.STRING).description("Assigned Load Group Name"),
                fieldWithPath("assignedGroups[].type").type(JsonFieldType.STRING).description("Assigned  Load Group Type"),

                fieldWithPath("notification.notifyOnAdjust").type(JsonFieldType.BOOLEAN).description("Notify on Adjust"),
                fieldWithPath("notification.enableOnSchedule").type(JsonFieldType.BOOLEAN).description("Enable on schedule"),
                fieldWithPath("notification.assignedNotificationGroups[]").type(JsonFieldType.ARRAY).description("Assigned Notification groups"),
                fieldWithPath("notification.assignedNotificationGroups[].notificationGrpID").type(JsonFieldType.NUMBER).description("Assigned Notification Id"),
                fieldWithPath("notification.assignedNotificationGroups[].notificationGrpName").type(JsonFieldType.STRING)
                                                                                              .description("Assigned Notification Group Name") };
    }

    /**
     * Helper method to create member control field descriptor.
     */
    public static FieldDescriptor[] loadProgramMemberControlFields() {
        return new FieldDescriptor[] {
                fieldWithPath("memberControl[].subordinateProgId").type(JsonFieldType.NUMBER)
                        .description("Sub-ordinate Program Id"),
                fieldWithPath("memberControl[].subordinateProgName").type(JsonFieldType.STRING)
                        .description("Sub-ordinate Program Name")
        };
    }

    /**
     * Helper method to merge member control field descriptor in Load Program.
     */
    public static List<FieldDescriptor> createFieldDescriptorForUpdate(FieldDescriptor[] programFieldDescriptor) {
        List<FieldDescriptor> fieldDescriptorList = mergeProgramFieldDescriptors(programFieldDescriptor);
        fieldDescriptorList.addAll(Arrays.asList(loadProgramMemberControlFields()));
        return fieldDescriptorList;
    }
    /**
     * Helper method to create program field descriptor.
     */
    public static List<FieldDescriptor> mergeProgramFieldDescriptors(FieldDescriptor[] gearFieldDescriptor) {
        List<FieldDescriptor> fieldDescriptorList = new ArrayList<FieldDescriptor>();
        fieldDescriptorList.addAll(Arrays.asList(loadProgramFields()));
        fieldDescriptorList.addAll(Arrays.asList(gearFieldDescriptor));
        fieldDescriptorList.addAll(Arrays.asList(loadProgramControlFields()));
        return fieldDescriptorList;
    }

    /**
     * Helper method to create field descriptor for getting the program.
     */
    public static List<FieldDescriptor> createFieldDescriptorForGet(FieldDescriptor[] programFieldDescriptor, int index) {
        List<FieldDescriptor> list = mergeFieldDescriptorWithProgramId(programFieldDescriptor);
        list.add(5, fieldWithPath("constraint.constraintName").type(JsonFieldType.STRING).description("Constraint Name"));
        list.add(8, fieldWithPath("gears[].gearId").type(JsonFieldType.NUMBER).description("Gear Id"));
        list.add(index, fieldWithPath("assignedGroups[].groupOrder").type(JsonFieldType.NUMBER).description("Group Order"));
        return list;
    }

    /**
     * Helper method to merge field descriptor with programId.
     */
    public static List<FieldDescriptor> mergeFieldDescriptorWithProgramId(FieldDescriptor[] programFieldDescriptor) {
        List<FieldDescriptor> list = mergeProgramFieldDescriptors(programFieldDescriptor);
        list.add(0, fieldWithPath("programId").type(JsonFieldType.NUMBER).description("Load Program Id"));
        return list;
    }

    /**
     * Helper method to create field descriptor to copy the program.
     */
    public static FieldDescriptor[] fieldDescriptorForCopy() {
        return new FieldDescriptor[] { fieldWithPath("name").type(JsonFieldType.STRING).description("Load Program Name"),
                fieldWithPath("operationalState").type(JsonFieldType.STRING).description("Operational State"),
                fieldWithPath("constraint.constraintId").type(JsonFieldType.NUMBER).description("Constraint Id"),
                fieldWithPath("copyMemberControl").type(JsonFieldType.BOOLEAN).description("Copy Member Control") };

    }

    public static FieldDescriptor responseFieldDescriptor() {
        return fieldWithPath("programId").type(JsonFieldType.NUMBER).description("Load Program Id");
    }

    public static FieldDescriptor requestFieldDesriptorForDelete() {
        return fieldWithPath("name").type(JsonFieldType.STRING).description("Load Program Name");
    }
    
    private static MockNotificationGroup getMockNotificationGroup() {
        return MockNotificationGroup.builder()
                .notificationGrpID(Integer.valueOf(ApiCallHelper.getProperty("notificationGrpID")))
                .notificationGrpName(ApiCallHelper.getProperty("notificationGrpName"))
                .build();
    }

    public static MockLoadProgram getMemberControlLoadProgram(ITestContext context, List<MockGearControlMethod> gearTypes,
            MockPaoType paoType) {
        MockLoadProgram subOrdinateLoadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(paoType,
                (List<MockLoadGroupBase>) context.getAttribute("loadGroups"),
                gearTypes,
                (Integer) context.getAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID));
        subOrdinateLoadProgram.setName(subOrdinateLoadProgram.getName().concat("MemberControl"));
        ExtractableResponse<?> loadProgramResponse = ApiCallHelper.post("saveLoadProgram", subOrdinateLoadProgram);

        subOrdinateLoadProgram.setProgramId(loadProgramResponse.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID));
        assertTrue("Program Id should not be Null", subOrdinateLoadProgram.getProgramId() != null);
        assertTrue("Status code should be 200", loadProgramResponse.statusCode() == 200);

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getLoadProgram",
                loadProgramResponse.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID).toString());
        assertTrue(getResponse.statusCode() == 200, "Status code should be 200");
        subOrdinateLoadProgram = getResponse.as(MockLoadProgram.class);

        return subOrdinateLoadProgram;
    }
}
