package com.cannontech.rest.api.point.request;

import java.util.List;

import com.cannontech.rest.api.common.model.MockPointArchiveType;
import com.cannontech.rest.api.common.model.MockPointLogicalGroups;
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
    private MockPointArchiveType archiveType;
    private Integer archiveInterval;
    private MockPointLogicalGroups timingGroup;
    private Boolean alarmsDisabled;
    private MockStaleData staleData;
    private List<MockPointLimit> limits;
    private MockPointAnalog pointAnalog;
    private MockPointAnalogControl pointAnalogControl;
    private MockPointAlarming alarming;
    private List<MockFdrTranslation> fdrList;
}
