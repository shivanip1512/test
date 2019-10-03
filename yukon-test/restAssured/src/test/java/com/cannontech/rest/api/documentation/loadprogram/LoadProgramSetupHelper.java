package com.cannontech.rest.api.documentation.loadprogram;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

public class LoadProgramSetupHelper {

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
                fieldWithPath("constraint.constraintId").type(JsonFieldType.NUMBER).description("Constraint Id") };

    }

    public static FieldDescriptor responseFieldDescriptor() {
        return fieldWithPath("programId").type(JsonFieldType.NUMBER).description("Load Program Id");
    }

    public static FieldDescriptor requestFieldDesriptorForDelete() {
        return fieldWithPath("name").type(JsonFieldType.STRING).description("Load Program Name");
    }
}
