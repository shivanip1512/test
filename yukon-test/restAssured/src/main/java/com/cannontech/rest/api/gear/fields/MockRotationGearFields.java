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
public class MockRotationGearFields implements MockProgramGearFields {

    private Integer shedTime;
    private Integer numberOfGroups;
    private Integer sendRate;
    private MockGroupSelectionMethod groupSelectionMethod;
    private MockHowToStopControl howToStopControl;
    private Integer capacityReduction;

    private MockWhenToChangeFields whenToChangeFields;

}
