package com.cannontech.rest.api.attributeAssignment.request;

import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.common.model.MockPointType;
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
public class MockAssignmentModel {
    private Integer attributeId;
    private Integer attributeAssignmentId;
    private MockPaoType paoType;
    private Integer offset;
    private MockPointType pointType;

}
