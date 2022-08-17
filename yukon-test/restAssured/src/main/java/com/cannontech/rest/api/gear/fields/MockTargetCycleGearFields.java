package com.cannontech.rest.api.gear.fields;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "noRamp", "controlPercent", "cyclePeriod", "cycleCountSendType", "maxCycleCount",
    "startingPeriodCount", "sendRate", "stopCommandRepeat", "howToStopControl", "capacityReduction", "kWReduction",
    "whenToChangeFields" })

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class MockTargetCycleGearFields extends MockTrueCycleGearFields {
    @JsonProperty("kWReduction")
    private Double kWReduction;


}
