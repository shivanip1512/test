package com.cannontech.rest.api.loadProgram.request;

import com.cannontech.rest.api.common.model.MockPaoType;
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
public class MockProgramGroup {

    private Integer groupId;
    private String groupName;
    private Integer groupOrder;
    private MockPaoType type;

}
