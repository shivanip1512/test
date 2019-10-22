package com.cannontech.rest.api.gear.fields;

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
public class MockItronCycleGearFields implements MockProgramGearFields {

    private MockItronCycleType cycleType;
    private Boolean rampIn;
    private Boolean rampOut;
    private Integer dutyCyclePercent;
    private Integer dutyCyclePeriodInMinutes;
    private Integer criticality;
    private Integer capacityReduction;
    private MockHowToStopControl howToStopControl;
    private MockWhenToChangeFields whenToChangeFields;
}
