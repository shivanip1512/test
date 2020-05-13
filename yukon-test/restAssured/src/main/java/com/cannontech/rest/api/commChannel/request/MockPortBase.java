package com.cannontech.rest.api.commChannel.request;

import com.cannontech.rest.api.common.model.MockPaoType;
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
public class MockPortBase {
    private Integer id;
    private String name;
    private Boolean enable;
    private MockBaudRate baudRate;
    private MockPaoType type;
}