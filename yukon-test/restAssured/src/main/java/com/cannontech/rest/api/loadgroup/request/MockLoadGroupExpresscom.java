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
public class MockLoadGroupExpresscom extends MockLoadGroupRFNExpresscom implements MockLoadGroupRoute {
    private Integer routeId;
    private String routeName;
}