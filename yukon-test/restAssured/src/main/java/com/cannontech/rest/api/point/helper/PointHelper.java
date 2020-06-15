package com.cannontech.rest.api.point.helper;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.ApiUtils;
import com.cannontech.rest.api.common.model.MockAnalogControlType;
import com.cannontech.rest.api.common.model.MockPointArchiveType;
import com.cannontech.rest.api.common.model.MockPointLogicalGroups;
import com.cannontech.rest.api.common.model.MockPointType;
import com.cannontech.rest.api.point.request.MockAlarmNotificationTypes;
import com.cannontech.rest.api.point.request.MockAlarmTableEntry;
import com.cannontech.rest.api.point.request.MockAnalogPoint;
import com.cannontech.rest.api.point.request.MockFdrDirection;
import com.cannontech.rest.api.point.request.MockFdrInterfaceType;
import com.cannontech.rest.api.point.request.MockFdrTranslation;
import com.cannontech.rest.api.point.request.MockPointAlarming;
import com.cannontech.rest.api.point.request.MockPointAnalog;
import com.cannontech.rest.api.point.request.MockPointAnalogControl;
import com.cannontech.rest.api.point.request.MockPointBase;
import com.cannontech.rest.api.point.request.MockPointLimit;
import com.cannontech.rest.api.point.request.MockPointUnit;
import com.cannontech.rest.api.point.request.MockStaleData;

public class PointHelper {
    public final static String CONTEXT_POINT_ID = "pointId";
    public final static Integer paoId = Integer.valueOf(ApiCallHelper.getProperty("meterNumber"));
    public final static Integer pointOffset = Integer.valueOf(ApiCallHelper.getProperty("pointOffset"));
    public final static Integer uomId = Integer.valueOf(ApiCallHelper.getProperty("uomId"));
    public final static Integer stateGroupId = Integer.valueOf(ApiCallHelper.getProperty("stateGroupId"));
    public final static Integer notificationId = Integer.valueOf(ApiCallHelper.getProperty("notificationGrpID"));

    public final static MockPointBase buildPoint(MockPointType pointType) {
        MockPointBase point = null;

        String name = ApiUtils.buildFriendlyName(MockPointType.Analog, "", "PointTest");

        switch (pointType) {
        case Analog:
            List<MockPointLimit> pointLimit = new ArrayList<>();
            pointLimit.add(buildPointLimit());
            List<MockFdrTranslation> fdrTranslation = new ArrayList<>();
            
            fdrTranslation.add(buildFdrTranslation());
            point = MockAnalogPoint.builder()
                    .paoId(paoId)
                    .pointName(name)
                    .pointType(pointType.name())
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
        default:
            break;
        }
        return point;
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
                .controlInhibited(true)
                .controlOffset(5)
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
                .highLimit(5.0)
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
                .condition("Rate Of Change")
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
}