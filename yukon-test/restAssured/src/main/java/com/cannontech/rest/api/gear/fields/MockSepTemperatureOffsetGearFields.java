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
public class MockSepTemperatureOffsetGearFields implements MockProgramGearFields {

    private Boolean rampIn;
    private Boolean rampOut;
    public MockTemperatureMeasureUnit celsiusOrFahrenheit;
    public MockMode mode;
    private Double offset;
    private Integer criticality;

    private MockHowToStopControl howToStopControl;
    private Integer capacityReduction;

    private MockWhenToChangeFields whenToChangeFields;
}
