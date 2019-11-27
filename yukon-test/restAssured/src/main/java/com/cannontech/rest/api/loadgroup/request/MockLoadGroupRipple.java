package com.cannontech.rest.api.loadgroup.request;

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
public class MockLoadGroupRipple extends MockLoadGroupBase implements MockLoadGroupRoute {
    private Integer routeId;
    private String routeName;
    private String control;
    private String restore;
    private Integer shedTime;
    private MockRippleGroup group;
    private MockRippleAreaCode areaCode;
}
