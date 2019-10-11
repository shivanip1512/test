package com.cannontech.rest.api.constraint.request;

import java.util.List;

import com.cannontech.rest.api.common.model.MockLMDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)

@Builder
@Getter
@Setter
public class MockProgramConstraint {
    private Integer id;
    private String name;
    private MockLMDto seasonSchedule;
    private Integer maxActivateSeconds;
    private Integer maxDailyOps;
    private Integer minActivateSeconds;
    private Integer minRestartSeconds;
    private List<MockDayOfWeek> daySelection;
    private MockLMDto holidaySchedule;
    private MockHolidayUsage holidayUsage;
    private Integer maxHoursDaily;
    private Integer maxHoursMonthly;
    private Integer maxHoursAnnually;
    private Integer maxHoursSeasonal;

}
