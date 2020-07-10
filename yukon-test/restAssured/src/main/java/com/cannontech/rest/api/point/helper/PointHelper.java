package com.cannontech.rest.api.point.helper;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.ApiUtils;
import com.cannontech.rest.api.common.model.MockAnalogControlType;
import com.cannontech.rest.api.common.model.MockPointArchiveType;
import com.cannontech.rest.api.common.model.MockPointLogicalGroups;
import com.cannontech.rest.api.common.model.MockPointType;
import com.cannontech.rest.api.point.request.MockAccumulatorPoint;
import com.cannontech.rest.api.point.request.MockAlarmNotificationTypes;
import com.cannontech.rest.api.point.request.MockAlarmTableEntry;
import com.cannontech.rest.api.point.request.MockAnalogPoint;
import com.cannontech.rest.api.point.request.MockCalcAnalogBase;
import com.cannontech.rest.api.point.request.MockCalcAnalogPointModel;
import com.cannontech.rest.api.point.request.MockCalcCompType;
import com.cannontech.rest.api.point.request.MockCalcOperation;
import com.cannontech.rest.api.point.request.MockCalcStatusPointModel;
import com.cannontech.rest.api.point.request.MockCalcUpdateType;
import com.cannontech.rest.api.point.request.MockCalculationBase;
import com.cannontech.rest.api.point.request.MockCalculationComponent;
import com.cannontech.rest.api.point.request.MockFdrDirection;
import com.cannontech.rest.api.point.request.MockFdrInterfaceType;
import com.cannontech.rest.api.point.request.MockFdrTranslation;
import com.cannontech.rest.api.point.request.MockPointAccumulator;
import com.cannontech.rest.api.point.request.MockPointAlarming;
import com.cannontech.rest.api.point.request.MockPointAnalog;
import com.cannontech.rest.api.point.request.MockPointAnalogControl;
import com.cannontech.rest.api.point.request.MockPointBase;
import com.cannontech.rest.api.point.request.MockPointLimit;
import com.cannontech.rest.api.point.request.MockPointStatusControl;
import com.cannontech.rest.api.point.request.MockPointUnit;
import com.cannontech.rest.api.point.request.MockStaleData;
import com.cannontech.rest.api.point.request.MockStatusControlType;
import com.cannontech.rest.api.point.request.MockStatusPoint;

public class PointHelper {
    public final static String CONTEXT_POINT_ID = "pointId";
    public final static Integer paoId = Integer.valueOf(ApiCallHelper.getProperty("paoId"));
    public final static Integer pointOffset = Integer.valueOf(ApiCallHelper.getProperty("pointOffset"));
    public final static Integer uomId = Integer.valueOf(ApiCallHelper.getProperty("uomId"));
    public final static Integer stateGroupId = Integer.valueOf(ApiCallHelper.getProperty("stateGroupId"));
    public final static Integer notificationId = Integer.valueOf(ApiCallHelper.getProperty("notificationGrpID"));
    public final static Integer baseLineId = Integer.valueOf(ApiCallHelper.getProperty("baselineId"));
    public final static Double calcAnalogPointId = Double.valueOf(ApiCallHelper.getProperty("pointIdForCalcAnalogCalculation"));
    public final static Double calcStatusPointId = Double.valueOf(ApiCallHelper.getProperty("pointIdForCalcStatusCalculation"));

    public final static MockPointBase buildPoint(MockPointType pointType) {
        MockPointBase point = null;

        String name = ApiUtils.buildFriendlyName(pointType, "", "Point Test");
        List<MockPointLimit> pointLimit = new ArrayList<>();
        pointLimit.add(buildPointLimit());
        List<MockFdrTranslation> fdrTranslation = new ArrayList<>();

        fdrTranslation.add(buildFdrTranslation());
        switch (pointType) {
            case Analog:

                point = MockAnalogPoint.builder()
                                       .paoId(paoId)
                                       .pointName(name)
                                       .pointType(pointType)
                                       .pointOffset(pointOffset)
                                       .pointUnit(buildPointUnit())
                                       .timingGroup(MockPointLogicalGroups.SOE)
                                       .archiveType(MockPointArchiveType.ON_TIMER)
                                       .alarmsDisabled(false)
                                       .stateGroupId(stateGroupId)
                                       .archiveInterval(60)
                                       .enable(true)
                                       .pointAnalog(buildPointAnalog())
                                       .pointAnalogControl(buildPointAnalogControl())
                                       .staleData(buildStaleData())
                                       .limits(pointLimit)
                                       .alarming(buildPointAlarming())
                                       .fdrList(fdrTranslation)
                                       .build();
                break;
            case CalcAnalog:
                point = MockCalcAnalogPointModel.builder()
                                                .paoId(paoId)
                                                .pointName(name)
                                                .pointType(pointType)
                                                .pointOffset(pointOffset)
                                                .pointUnit(buildPointUnit())
                                                .timingGroup(MockPointLogicalGroups.SOE)
                                                .archiveType(MockPointArchiveType.ON_TIMER)
                                                .alarmsDisabled(false)
                                                .stateGroupId(stateGroupId)
                                                .archiveInterval(60)
                                                .enable(true)
                                                .baselineId(baseLineId)
                                                .calcComponents(buildAnalogCalculationComponent())
                                                .calcAnalogBase(buildCalcAnalogBase())
                                                .staleData(buildStaleData())
                                                .limits(pointLimit)
                                                .alarming(buildPointAlarming())
                                                .fdrList(fdrTranslation)
                                                .build();
                break;
            case CalcStatus :
                point = MockCalcStatusPointModel.builder()
                                                .paoId(paoId)
                                                .pointName(name)
                                                .pointType(pointType)
                                                .pointOffset(pointOffset)
                                                .timingGroup(MockPointLogicalGroups.SOE)
                                                .archiveType(MockPointArchiveType.ON_TIMER)
                                                .alarmsDisabled(false)
                                                .stateGroupId(stateGroupId)
                                                .archiveInterval(60)
                                                .enable(true)
                                                .baselineId(baseLineId)
                                                .calculationBase(buildCalculculationBase())
                                                .calcComponents(buildStatusCalculationComponent())
                                                .pointStatusControl(buildPointStatusControl())
                                                .staleData(buildStaleData())
                                                .alarming(buildPointAlarming())
                                                .fdrList(fdrTranslation)
                                                .build();                
                break;
            case DemandAccumulator:
            case PulseAccumulator:

                point = MockAccumulatorPoint.builder()
                                            .paoId(paoId)
                                            .pointName(name)
                                            .pointType(pointType)
                                            .pointOffset(pointOffset)
                                            .pointUnit(buildPointUnit())
                                            .timingGroup(MockPointLogicalGroups.SOE)
                                            .archiveType(MockPointArchiveType.ON_TIMER)
                                            .alarmsDisabled(false)
                                            .stateGroupId(stateGroupId)
                                            .archiveInterval(60)
                                            .enable(true)
                                            .pointAccumulator(buildPointAccumulator())
                                            .staleData(buildStaleData())
                                            .limits(pointLimit)
                                            .alarming(buildPointAlarming())
                                            .fdrList(fdrTranslation)
                                            .build();
                break;
            case Status:
                point = MockStatusPoint.builder()
                                       .paoId(paoId)
                                       .pointName(name)
                                       .pointType(pointType)
                                       .pointOffset(pointOffset)
                                       .timingGroup(MockPointLogicalGroups.SOE)
                                       .archiveType(MockPointArchiveType.NONE)
                                       .alarmsDisabled(false)
                                       .stateGroupId(stateGroupId)
                                       .initialState(0)
                                       .pointStatusControl(buildPointStatusControl())
                                       .archiveInterval(0)
                                       .enable(true)
                                       .staleData(buildStaleData())
                                       .alarming(buildPointAlarming())
                                       .fdrList(fdrTranslation)
                                       .build();
            default:
                break;
        }
        return point;
    }
    
    private static List<MockCalculationComponent> buildAnalogCalculationComponent() {
        List<MockCalculationComponent> mockCalculationComponent = new ArrayList<>();
        mockCalculationComponent.add(MockCalculationComponent.builder()
                .componentType(MockCalcCompType.OPERATION)
                .operation(MockCalcOperation.ADDITION_OPERATION)
                .operand(calcAnalogPointId)
                .build());
        return mockCalculationComponent;
    }

    private static MockCalcAnalogBase buildCalcAnalogBase() {
        return MockCalcAnalogBase.builder()
                .updateType(MockCalcUpdateType.ON_TIMER)
                .periodicRate(15)
                .calculateQuality(true)
                .build();
    }
    
    private static List<MockCalculationComponent> buildStatusCalculationComponent() {
        List<MockCalculationComponent> mockCalculationComponent = new ArrayList<>();
        mockCalculationComponent.add(MockCalculationComponent.builder()
                .componentType(MockCalcCompType.OPERATION)
                .operation(MockCalcOperation.DIVISION_OPERATION)
                .operand(calcStatusPointId)
                .build());
        return mockCalculationComponent;
    }

    private static MockCalculationBase buildCalculculationBase() {
        return MockCalculationBase.builder()
                .updateType(MockCalcUpdateType.ON_TIMER)
                .periodicRate(15)
                .build();
    }
    
    private static MockPointAnalog buildPointAnalog() {
        return MockPointAnalog.builder()
                .dataOffset(0.0)
                .deadband(-1.0)
                .multiplier(1.0)
                .build();
    }

    private static MockPointAnalogControl buildPointAnalogControl() {
        return MockPointAnalogControl.builder()
                .controlInhibited(false)
                .controlOffset(0)
                .controlType(MockAnalogControlType.NONE)
                .build();
    }

    private static MockStaleData buildStaleData() {
        return MockStaleData.builder()
                .time(5)
                .updateStyle(0)
                .build();
    }

    private static MockPointUnit buildPointUnit() {
        return MockPointUnit.builder()
                .uomId(uomId)
                .decimalPlaces(3)
                .highReasonabilityLimit(5.0)
                .lowReasonabilityLimit(4.0)
                .meterDials(0)
                .build();
    }

    private static MockPointLimit buildPointLimit() {
        return MockPointLimit.builder()
                .limitNumber(1)
                .highLimit(7.0)
                .lowLimit(6.0)
                .limitDuration(2)
                .build();
    }
  
    private static MockPointAlarming buildPointAlarming() {
        List<MockAlarmTableEntry> alarmTableEntry = new ArrayList<>();
        alarmTableEntry.add(buildAlarmTableEntry());
        return MockPointAlarming.builder()
                .notificationGroupId(notificationId)
                .notifyOnAck(true)
                .notifyOnClear(false)
                .alarmTableList(alarmTableEntry)
                .build();
    }
    
    private static MockAlarmTableEntry buildAlarmTableEntry() {
        return MockAlarmTableEntry.builder()
                .category("(none)")
                .condition("Non-updated")
                .notify(MockAlarmNotificationTypes.EXCLUDE_NOTIFY)
                .build();
    }
    
    private static MockFdrTranslation buildFdrTranslation() {
        return MockFdrTranslation.builder()
                .direction(MockFdrDirection.RECEIVE)
                .fdrInterfaceType(MockFdrInterfaceType.ACS)
                .translation("Category:PSEUDO;Remote:;Point:;")
                .build();
    }
    
    private static MockPointAccumulator buildPointAccumulator() {
        return MockPointAccumulator.builder()
                .dataOffset(0.0)
                .multiplier(1.0)
                .build();
    }
    
    private static MockPointStatusControl buildPointStatusControl() {
        return MockPointStatusControl.builder()
                .closeTime1(0)
                .closeTime2(0)
                .commandTimeOut(0)
                .controlInhibited(false)
                .controlType(MockStatusControlType.NONE)
                .controlOffset(0)
                .build();
    }
    
}