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
public class MockThermostatSetbackGearFields implements MockProgramGearFields {

    private MockAbsoluteOrDelta absoluteOrDelta;
    private MockTemperatureMeasureUnit measureUnit;

    public Boolean isHeatMode;
    public Boolean isCoolMode;

    private Integer minValue;
    private Integer maxValue;
    private Integer valueB;
    private Integer valueD;

    private Integer valueF;
    private Integer random;

    private Integer valueTa;
    private Integer valueTb;
    private Integer valueTc;
    private Integer valueTd;
    private Integer valueTe;
    private Integer valueTf;

    private MockHowToStopControl howToStopControl;
    private Integer capacityReduction;

    private MockWhenToChangeFields whenToChangeFields;

}
