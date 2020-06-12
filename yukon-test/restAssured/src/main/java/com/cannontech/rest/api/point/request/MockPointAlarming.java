package com.cannontech.rest.api.point.request;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@JsonInclude(Include.NON_NULL)
@SuperBuilder
@Getter
@Setter
@RequiredArgsConstructor
public class MockPointAlarming {
    private Integer notificationGroupId;
    private Boolean notifyOnAck;
    private Boolean notifyOnClear;
    private List<MockAlarmTableEntry> alarmTableList;
}
