package com.cannontech.rest.api.point.request;

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
public class MockPointStatusControl {

    private Integer controlOffset;
    private Boolean controlInhibited;
    private Integer closeTime1;
    private Integer closeTime2;
    private String openCommand;
    private String closeCommand;
    private Integer commandTimeOut;
    private MockStatusControlType controlType;
}
