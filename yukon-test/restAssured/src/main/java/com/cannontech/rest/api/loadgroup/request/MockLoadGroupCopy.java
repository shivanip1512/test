package com.cannontech.rest.api.loadgroup.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Class for setting the additional fields for coping the load group
 */
@SuperBuilder
@Getter
@Setter
@RequiredArgsConstructor
@JsonInclude(Include.NON_NULL)
public class MockLoadGroupCopy extends MockLMCopy {
    private Integer routeId;

}
