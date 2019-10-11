package com.cannontech.rest.api.dr.helper;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.constraint.request.MockDayOfWeek;
import com.cannontech.rest.api.constraint.request.MockHolidayUsage;
import com.cannontech.rest.api.constraint.request.MockProgramConstraint;

public class ProgramConstraintHelper {

    public static MockProgramConstraint buildProgramConstraint() {

        MockLMDto seasonSchedule = MockLMDto.builder().id(0).build();

        MockLMDto holidaySchedule = MockLMDto.builder().id(0).build();

        List<MockDayOfWeek> daySelection = new ArrayList<>();
        daySelection.add(MockDayOfWeek.MONDAY);
        daySelection.add(MockDayOfWeek.SUNDAY);

        MockProgramConstraint programConstraint = MockProgramConstraint.builder()
                                                               .name("Program Constraint")
                                                               .holidayUsage(MockHolidayUsage.NONE)
                                                               .holidaySchedule(holidaySchedule)
                                                               .daySelection(daySelection)
                                                               .maxActivateSeconds(10)
                                                               .maxDailyOps(11)
                                                               .maxHoursAnnually(17)
                                                               .maxHoursMonthly(16)
                                                               .maxHoursSeasonal(18)
                                                               .maxHoursDaily(15)
                                                               .minActivateSeconds(12)
                                                               .minRestartSeconds(13)
                                                               .seasonSchedule(seasonSchedule)
                                                               .build();

        return programConstraint;
    }
}
