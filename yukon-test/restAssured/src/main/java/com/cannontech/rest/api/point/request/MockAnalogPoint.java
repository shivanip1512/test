package com.cannontech.rest.api.point.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@JsonInclude(Include.NON_NULL)
@SuperBuilder
@Getter
@Setter
@RequiredArgsConstructor
public class MockAnalogPoint extends MockPointBase {
    private Integer pointId;
    private Integer stateGroupId;
    private Boolean enable;
    private String archiveType;
    private Integer archiveInterval;
    private String timingGroup;
    private Boolean alarmsDisabled;
    private MockStaleData staleData;
    private List<Integer> limits;
    private MockPointAnalog pointAnalog;
    private MockPointAnalogControl pointAnalogControl;
    
}