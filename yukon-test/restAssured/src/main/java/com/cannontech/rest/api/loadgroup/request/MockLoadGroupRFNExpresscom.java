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
public class MockLoadGroupRFNExpresscom extends MockLoadGroupBase {
    private String serialNumber;
    private Integer serviceProvider;
    private Integer geo;
    private Integer substation;
    private String feeder;
    private Integer zip;
    private Integer user;
    private Integer program;
    private Integer splinter;
    private List<MockAddressUsage> addressUsage;
    private List<MockLoads> relayUsage;
    private MockControlPriority protocolPriority;

}