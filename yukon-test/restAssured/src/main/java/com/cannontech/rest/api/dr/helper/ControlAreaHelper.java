package com.cannontech.rest.api.dr.helper;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import com.cannontech.rest.api.controlArea.request.MockControlArea;
import com.cannontech.rest.api.controlArea.request.MockControlAreaProgramAssignment;
import com.cannontech.rest.api.controlArea.request.MockControlAreaProjection;
import com.cannontech.rest.api.controlArea.request.MockControlAreaProjectionType;
import com.cannontech.rest.api.controlArea.request.MockControlAreaTrigger;
import com.cannontech.rest.api.controlArea.request.MockControlAreaTriggerType;
import com.cannontech.rest.api.controlArea.request.MockDailyDefaultState;

public class ControlAreaHelper {
    
    public final static String CONTEXT_CONTROLAREA_ID = "controlAreaId";

    public static MockControlArea buildControlArea(MockControlAreaTriggerType triggerType, Integer programId) {
        List<MockControlAreaProgramAssignment> programAssigned = new ArrayList<>();
        List<MockControlAreaTrigger> triggers = new ArrayList<>();
        MockControlAreaProgramAssignment controlAreaProgram = ControlAreaHelper.buildControlAreaProgram(programId);
        MockControlAreaTrigger controlAreaTrigger = ControlAreaHelper.buildTrigger(triggerType);
        programAssigned.add(controlAreaProgram);
        triggers.add(controlAreaTrigger);
        return MockControlArea.builder()
                              .name(WordUtils.capitalizeFully(triggers.get(0).getTriggerType().name() + "_ControlArea" + "_Test"))
                              .controlInterval(0)
                              .minResponseTime(0)
                              .dailyDefaultState(MockDailyDefaultState.None)
                              .dailyStartTimeInMinutes(120)
                              .dailyStopTimeInMinutes(180)
                              .allTriggersActiveFlag(false)
                              .triggers(triggers)
                              .programAssignment(programAssigned)
                              .build();
    }

    public static MockControlAreaProgramAssignment buildControlAreaProgram(Integer programId) {
        return MockControlAreaProgramAssignment.builder()
                                               .programId(programId)
                                               .startPriority(1)
                                               .stopPriority(2)
                                               .build();
    }
   // DirectProgramTest
    private static MockControlAreaProjection buildControlAreaProjection() {
        return MockControlAreaProjection.builder().projectionType(MockControlAreaProjectionType.LSF).projectionPoint(8).projectAheadDuration(2100).build();
    }

    public static MockControlAreaTrigger buildTrigger(MockControlAreaTriggerType triggerType) {
        MockControlAreaTrigger trigger = null;
        switch (triggerType) {
            case THRESHOLD_POINT:
                trigger = MockControlAreaTrigger.builder()
                                                .triggerType(MockControlAreaTriggerType.THRESHOLD_POINT)
                                                .triggerPointId(-110)
                                                .minRestoreOffset(0.00)
                                                .peakPointId(-110)
                                                .thresholdPointId(-110)
                                                .build();
                break;
            case THRESHOLD:
                trigger = MockControlAreaTrigger.builder()
                                                .triggerType(MockControlAreaTriggerType.THRESHOLD)
                                                .triggerPointId(-110)
                                                .threshold(33.33)
                                                .controlAreaProjection(buildControlAreaProjection())
                                                .atku(2)
                                                .minRestoreOffset(0.00)
                                                .peakPointId(-110)
                                                .thresholdPointId(-110)
                                                .build();
                break;
            case STATUS:
                trigger = MockControlAreaTrigger.builder()
                                                .triggerType(MockControlAreaTriggerType.STATUS)
                                                .triggerPointId(-110)
                                                .normalState(0)
                                                .build();
        }
        return trigger;
    }

    public static FieldDescriptor[] controlAreaCommonFields() {
        return new FieldDescriptor[] { fieldWithPath("name").type(JsonFieldType.STRING).description("Control Area Name"),
                fieldWithPath("controlInterval").type(JsonFieldType.NUMBER).description("Hardcoded values ranges from 1 mins to 30 mins"),
                fieldWithPath("minResponseTime").type(JsonFieldType.NUMBER).description("Minimum Response Time"),
                fieldWithPath("dailyDefaultState").type(JsonFieldType.STRING).description("Default state of control program"),
                fieldWithPath("dailyStartTimeInMinutes").type(JsonFieldType.NUMBER).optional().description("Daily start time in mins"),
                fieldWithPath("dailyStopTimeInMinutes").type(JsonFieldType.NUMBER).optional().description("Daily stop tims in mins"),
                fieldWithPath("allTriggersActiveFlag").type(JsonFieldType.BOOLEAN).description("Require all triggers active flag") };
    }

    public static FieldDescriptor[] thresholdPointTriggerFields() {
        return new FieldDescriptor[] { fieldWithPath("triggers[].triggerType").type(JsonFieldType.STRING).description("Trigger type"),
                fieldWithPath("triggers[].triggerPointId").type(JsonFieldType.NUMBER).description("Trigger point Id"),
                fieldWithPath("triggers[].minRestoreOffset").type(JsonFieldType.NUMBER)
                                                            .description("Allowed input types: only numeric, one negative(-) allowed, Range (-99999.9999 to 99999.9999), only 4 decimal points allowed"),
                fieldWithPath("triggers[].peakPointId").type(JsonFieldType.NUMBER).optional().description("Peak Point Id"),
                fieldWithPath("triggers[].thresholdPointId").type(JsonFieldType.NUMBER).description("Threshold Point Id") };
    }

    public static FieldDescriptor[] thresholdTriggerFields() {
        return new FieldDescriptor[] { fieldWithPath("triggers[].triggerType").type(JsonFieldType.STRING).description("Trigger type"),
                fieldWithPath("triggers[].triggerPointId").type(JsonFieldType.NUMBER).description("Trigger point Id"),
                fieldWithPath("triggers[].threshold").type(JsonFieldType.NUMBER).optional().description("Threshold value of trigger "),
                fieldWithPath("triggers[].controlAreaProjection.projectionType").type(JsonFieldType.STRING)
                                                                                .optional()
                                                                                .description("Projection type for control area projection"),
                fieldWithPath("triggers[].controlAreaProjection.projectionPoint").type(JsonFieldType.NUMBER)
                                                                                 .optional()
                                                                                 .description("Projection point for control area projection"),
                fieldWithPath("triggers[].controlAreaProjection.projectAheadDuration").type(JsonFieldType.NUMBER)
                                                                                      .optional()
                                                                                      .description("Projection ahead duration for control area projection"),
                fieldWithPath("triggers[].atku").type(JsonFieldType.NUMBER).optional().description("Automatic threshold kick up offset to be used"),
                fieldWithPath("triggers[].minRestoreOffset").type(JsonFieldType.NUMBER)
                                                            .optional()
                                                            .description("Allowed input types: only numeric, one negative(-) allowed, Range (-99999.9999 to 99999.9999), only 4 decimal points allowed"),
                fieldWithPath("triggers[].peakPointId").type(JsonFieldType.NUMBER).optional().description("Peak Point Id"),
                fieldWithPath("triggers[].thresholdPointId").type(JsonFieldType.NUMBER).optional().description("Threshold Point Id"),
                fieldWithPath("triggers[].triggerPointName").type(JsonFieldType.STRING).optional().description("Threshold Point Name") };
    }

    public static FieldDescriptor[] statusTriggerFields() {
        return new FieldDescriptor[] { fieldWithPath("triggers[].triggerType").type(JsonFieldType.STRING).description("Trigger type"),
                fieldWithPath("triggers[].triggerPointId").type(JsonFieldType.NUMBER).description("Trigger point Id"),
                fieldWithPath("triggers[].thresholdPointId").type(JsonFieldType.NUMBER).optional().description("Threshold Point Id"),
                fieldWithPath("triggers[].normalState").type(JsonFieldType.NUMBER).optional().description("Normal state of Status trigger") };
    }

    public static FieldDescriptor[] assignedProgramFields() {
        return new FieldDescriptor[] { fieldWithPath("programAssignment[].programId").type(JsonFieldType.NUMBER).optional().description("Load Program Id"),
                fieldWithPath("programAssignment[].startPriority").type(JsonFieldType.NUMBER).optional().description("Load Program start priority"),
                fieldWithPath("programAssignment[].stopPriority").type(JsonFieldType.NUMBER).optional().description("Load Program stop priority") };
    }

    public static List<FieldDescriptor> buildResponseDescriptor() {
        List<FieldDescriptor> responseDescriptor = new ArrayList<FieldDescriptor>();
        responseDescriptor.add(fieldWithPath(CONTEXT_CONTROLAREA_ID).type(JsonFieldType.NUMBER).optional().description("Control Area Id"));
        return responseDescriptor;
    }

    public static FieldDescriptor[] buildResponseDescriptorForGet() {
        return new FieldDescriptor[] { fieldWithPath("controlAreaId").type(JsonFieldType.NUMBER).optional().description("Control Area Id"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("Control Area Name"),
                fieldWithPath("controlInterval").type(JsonFieldType.NUMBER).description("Hardcoded values ranges from 1 mins to 30 mins"),
                fieldWithPath("minResponseTime").type(JsonFieldType.NUMBER).description("Minimum Response Time"),
                fieldWithPath("dailyDefaultState").type(JsonFieldType.STRING).description("Default state of control program"),
                fieldWithPath("dailyStartTimeInMinutes").type(JsonFieldType.NUMBER).optional().description("Daily start time in mins"),
                fieldWithPath("dailyStopTimeInMinutes").type(JsonFieldType.NUMBER).optional().description("Daily stop tims in mins"),
                fieldWithPath("allTriggersActiveFlag").type(JsonFieldType.BOOLEAN).description("Require all triggers active flag"),
                fieldWithPath("triggers[].triggerId").type(JsonFieldType.NUMBER).optional().description("Trigger Id"),
                fieldWithPath("triggers[].triggerNumber").type(JsonFieldType.NUMBER).optional().description("Trigger number"),
                fieldWithPath("triggers[].triggerType").type(JsonFieldType.STRING).description("Trigger type"),
                fieldWithPath("triggers[].triggerPointId").type(JsonFieldType.NUMBER).description("Trigger point Id"),
                fieldWithPath("triggers[].normalState").type(JsonFieldType.NUMBER).optional().description("Normal state of Status trigger"),
                fieldWithPath("triggers[].threshold").type(JsonFieldType.NUMBER).optional().description("Threshold value of trigger "),
                fieldWithPath("triggers[].controlAreaProjection.projectionType").type(JsonFieldType.STRING)
                                                                                .optional()
                                                                                .description("Projection type for control area projection"),
                fieldWithPath("triggers[].controlAreaProjection.projectionPoint").type(JsonFieldType.NUMBER)
                                                                                 .optional()
                                                                                 .description("Projection point for control area projection"),
                fieldWithPath("triggers[].controlAreaProjection.projectAheadDuration").type(JsonFieldType.NUMBER)
                                                                                      .optional()
                                                                                      .description("Projection ahead duration for control area projection"),
                fieldWithPath("triggers[].atku").type(JsonFieldType.NUMBER).optional().description("Automatic threshold kick up offset to be used"),
                fieldWithPath("triggers[].minRestoreOffset").type(JsonFieldType.NUMBER)
                                                            .optional()
                                                            .description("Allowed input types: only numeric, one negative(-) allowed, Range (-99999.9999 to 99999.9999), only 4 decimal points allowed"),
                fieldWithPath("triggers[].peakPointId").type(JsonFieldType.NUMBER).optional().description("Peak Point Id"),
                fieldWithPath("triggers[].thresholdPointId").type(JsonFieldType.NUMBER).optional().description("Threshold Point Id"),
                fieldWithPath("triggers[].triggerPointName").type(JsonFieldType.STRING).optional().description("Threshold Point Name"),
                fieldWithPath("programAssignment[].programId").type(JsonFieldType.NUMBER).optional().description("Load Program Id"),
                fieldWithPath("programAssignment[].programName").type(JsonFieldType.STRING).description("Load Program name"),
                fieldWithPath("programAssignment[].startPriority").type(JsonFieldType.NUMBER).optional().description("Load Program start priority"),
                fieldWithPath("programAssignment[].stopPriority").type(JsonFieldType.NUMBER).optional().description("Load Program stop priority") };
    }

    public static List<FieldDescriptor> buildRequestDescriptor(MockControlAreaTriggerType triggerType) {
        List<FieldDescriptor> requestDescriptor = new ArrayList<FieldDescriptor>();
        requestDescriptor.addAll(Arrays.asList(controlAreaCommonFields()));
        switch (triggerType) {
            case THRESHOLD_POINT:
                requestDescriptor.addAll(Arrays.asList(thresholdPointTriggerFields()));
                break;
            case THRESHOLD:
                requestDescriptor.addAll(Arrays.asList(thresholdTriggerFields()));
                break;
            case STATUS:
                requestDescriptor.addAll(Arrays.asList(statusTriggerFields()));
        }
        requestDescriptor.addAll(Arrays.asList(assignedProgramFields()));
        return requestDescriptor;
    }

    public static FieldDescriptor requestFieldDesriptorForDelete() {
        return fieldWithPath("name").type(JsonFieldType.STRING).description("Control Area Name");
    }
}
