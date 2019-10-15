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
public class MockSimpleThermostatRampingGearFields implements MockProgramGearFields {

    public MockMode mode;
    private Integer randomStartTimeInMinutes;
    private Integer preOpTemp;
    private Integer preOpTimeInMinutes;
    private Integer preOpHoldInMinutes;
    private Float rampPerHour;
    // Better variable name can be used
    private Integer max;

    private Integer rampOutTimeInMinutes;
    private Integer maxRuntimeInMinutes;

    private MockHowToStopControl howToStopControl;

    private MockWhenToChangeFields whenToChangeFields;
}
