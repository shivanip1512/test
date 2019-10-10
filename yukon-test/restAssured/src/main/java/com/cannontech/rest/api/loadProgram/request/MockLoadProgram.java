package com.cannontech.rest.api.loadProgram.request;

import java.util.List;

import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.gear.fields.MockOperationalState;
import com.cannontech.rest.api.gear.fields.MockProgramGear;
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
public class MockLoadProgram {

    private Integer programId;
    private String name;

    private MockPaoType type;
    private MockOperationalState operationalState;
    private MockProgramConstraint constraint;

    private Double triggerOffset;
    private Double restoreOffset;

    private List<MockProgramGear> gears;
    private MockProgramControlWindow controlWindow;
    private List<MockProgramGroup> assignedGroups;
    private MockNotification notification;

    private List<MockProgramDirectMemberControl> memberControl;

}
