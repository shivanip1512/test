package com.cannontech.rest.api.loadProgram.request;

import com.cannontech.rest.api.gear.fields.MockOperationalState;
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
public class MockLoadProgramCopy {

    private String name;

    private MockProgramConstraint constraint;
    private Boolean copyMemberControl;
    private MockOperationalState operationalState;

}
