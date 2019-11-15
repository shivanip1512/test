package com.cannontech.rest.api.loadgroup.request;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@RequiredArgsConstructor
public class MockLoadGroupPoint extends MockLoadGroupBase {
    private Integer deviceIdUsage;
    private Integer pointIdUsage;
    private Integer startControlRawState;

}
