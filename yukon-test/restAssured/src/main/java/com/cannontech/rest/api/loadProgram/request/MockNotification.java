package com.cannontech.rest.api.loadProgram.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class MockNotification {

    private Integer programStartInMinutes;
    private Integer programStopInMinutes;
    private Boolean notifyOnAdjust;
    private Boolean enableOnSchedule;
    @JsonInclude(Include.NON_EMPTY)
    private List<MockNotificationGroup> assignedNotificationGroups;

}
