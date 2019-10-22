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
public class MockMasterCycleGearFields implements MockProgramGearFields {

    private Integer controlPercent;
    private Integer cyclePeriodInMinutes;
    private MockGroupSelectionMethod groupSelectionMethod;

    private Integer rampInPercent;
    private Integer rampInIntervalInSeconds;

    private MockHowToStopControl howToStopControl;
    private MockStopOrder stopOrder;
    private Integer rampOutPercent;
    private Integer rampOutIntervalInSeconds;;

    private Integer capacityReduction;

    private MockWhenToChangeFields whenToChangeFields;
}
