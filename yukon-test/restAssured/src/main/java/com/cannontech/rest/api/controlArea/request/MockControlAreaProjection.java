package com.cannontech.rest.api.controlArea.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class MockControlAreaProjection {

    private MockControlAreaProjectionType projectionType;
    private Integer projectionPoint;
    private Integer projectAheadDuration;

}
