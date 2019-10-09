package com.cannontech.rest.api.loadgroup.request;

import java.util.List;

import com.cannontech.rest.api.common.model.MockLMPaoDto;
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
public class MockMacroLoadGroup {

    private Integer id;
    private String name;
    private MockPaoType type;
    private List<MockLMPaoDto> assignedLoadGroups;

}
