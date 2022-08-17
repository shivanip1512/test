package com.cannontech.rest.api.controlArea.request;

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
public class MockControlArea {

    private Integer controlAreaId;
    private String name;
    private Integer controlInterval;
    private Integer minResponseTime;
    private MockDailyDefaultState dailyDefaultState;
    private Integer dailyStartTimeInMinutes;
    private Integer dailyStopTimeInMinutes;
    private Boolean allTriggersActiveFlag;

    private List<MockControlAreaTrigger> triggers;
    private List<MockControlAreaProgramAssignment> programAssignment;

}
