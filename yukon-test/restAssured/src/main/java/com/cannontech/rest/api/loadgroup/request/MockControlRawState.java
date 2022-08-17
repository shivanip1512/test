package com.cannontech.rest.api.loadgroup.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@JsonInclude(Include.NON_NULL)
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MockControlRawState {
    private Integer rawState;
    private String stateText;
}
