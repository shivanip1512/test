package com.cannontech.rest.api.loadgroup.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@RequiredArgsConstructor
public class MockLoadGroupEmetcon extends MockLoadGroupBase implements MockLoadGroupRoute {
    private Integer goldAddress;
    private Integer silverAddress;
    private MockEmetconAddressUsage addressUsage;
    private MockEmetconRelayUsage relayUsage;
    private Integer routeId;
    private String routeName;

}
