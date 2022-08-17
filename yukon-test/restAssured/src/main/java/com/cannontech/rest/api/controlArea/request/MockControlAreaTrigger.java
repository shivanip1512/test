package com.cannontech.rest.api.controlArea.request;

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
public class MockControlAreaTrigger {

    private Integer triggerId;
    private Integer triggerNumber;
    private MockControlAreaTriggerType triggerType;
    private Integer triggerPointId;
    private Integer normalState;
    private Double threshold;
    private MockControlAreaProjection controlAreaProjection;
    private Integer atku;
    private Double minRestoreOffset;
    private Integer peakPointId;
    private Integer thresholdPointId;
    private String triggerPointName;

}
