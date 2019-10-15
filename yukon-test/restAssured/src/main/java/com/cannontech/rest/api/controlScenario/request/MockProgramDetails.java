package com.cannontech.rest.api.controlScenario.request;

import java.util.List;

import com.cannontech.rest.api.common.model.MockLMDto;
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
public class MockProgramDetails {

    private Integer programId;
    private Integer startOffsetInMinutes;
    private Integer stopOffsetInMinutes;
    private String category;
    private List<MockLMDto> gears;
}
