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
public class MockHoneywellSetpointGearFields implements MockProgramGearFields {

    private Boolean mandatory;
    private Integer setpointOffset;
    private Integer precoolOffset;
    private MockMode mode;

    private MockHowToStopControl howToStopControl;
    private Integer capacityReduction;

    private MockWhenToChangeFields whenToChangeFields;

}