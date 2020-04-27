package com.cannontech.rest.api.commChannel.request;

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
public class MockTerminalServerPortDetailBase extends MockPortBase {
    private Integer portNumber;
    private Integer carrierDetectWaitInMilliseconds;
    private MockProtocolWrap protocolWrap;
    private MockPortTiming timing;
    private MockPortSharing sharing;
}
