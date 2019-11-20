package com.cannontech.rest.api.loadgroup.request;

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
public class MockLoadGroupMCT extends MockLoadGroupBase implements MockLoadGroupRoute {

    private Integer routeId;
    private String routeName;
    private MockAddressLevel level;
    private Integer address;
    private Integer mctDeviceId;
    private List<MockRelays> relayUsage;
}