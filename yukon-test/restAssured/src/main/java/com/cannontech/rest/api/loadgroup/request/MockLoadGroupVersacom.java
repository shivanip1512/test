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
public class MockLoadGroupVersacom extends MockLoadGroupBase implements MockLoadGroupRoute {
    // Communication Route Id
    private Integer routeId;
    private String routeName;
    // Addressing
    private Integer utilityAddress;
    private Integer sectionAddress;
    private String classAddress;
    private String divisionAddress;
    private String serialAddress;

    // Addressing Usage
    private List<MockVersacomAddressUsage> addressUsage;
    private List<MockRelays> relayUsage;

}
