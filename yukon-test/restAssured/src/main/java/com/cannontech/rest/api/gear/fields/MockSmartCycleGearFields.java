package com.cannontech.rest.api.gear.fields;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonInclude(Include.NON_NULL)
public class MockSmartCycleGearFields implements MockProgramGearFields {

    private Boolean noRamp;
    private Integer controlPercent;
    private Integer cyclePeriodInMinutes;
    private MockCycleCountSendType cycleCountSendType;
    private Integer maxCycleCount;
    private Integer startingPeriodCount;

    private Integer sendRate;
    private Integer stopCommandRepeat;

    private MockHowToStopControl howToStopControl;
    private Integer capacityReduction;

    private MockWhenToChangeFields whenToChangeFields;

}
