package com.cannontech.rest.api.point.request;

import java.util.Set;

import com.cannontech.rest.api.deviceReadings.request.MockBuiltInAttribute;
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
public class MockPointInfoModel {
    private int pointId;
    private String name;
    private MockPointIdentifier pointIdentifier;
    private int stateGroupId;
    private Set<MockBuiltInAttribute> attributes;
}
