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
public class MockTimeRefreshGearFields implements MockProgramGearFields {

    private MockCycleCountSendType refreshShedTime;
    private Integer shedTime;
    private Integer numberOfGroups;
    private Integer sendRate;

    private MockGroupSelectionMethod groupSelectionMethod;

    private Integer rampInPercent;
    private Integer rampInIntervalInSeconds;

    private MockHowToStopControl howToStopControl;
    private MockStopOrder stopOrder;

    private Integer rampOutPercent;
    private Integer rampOutIntervalInSeconds;

    private Integer stopCommandRepeat;
    private Integer capacityReduction;

    private MockWhenToChangeFields whenToChangeFields;

}
