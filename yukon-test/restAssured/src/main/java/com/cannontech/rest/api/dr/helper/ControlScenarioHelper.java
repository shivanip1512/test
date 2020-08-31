package com.cannontech.rest.api.dr.helper;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.rest.api.common.model.MockLMGearDto;
import com.cannontech.rest.api.controlScenario.request.MockControlScenario;
import com.cannontech.rest.api.controlScenario.request.MockProgramDetails;
import com.cannontech.rest.api.loadProgram.request.MockLoadProgram;

public class ControlScenarioHelper {

    public final static String CONTEXT_CONTROL_SCENARIO_ID = "paoId";
    public final static String CONTEXT_CONTROL_SCENARIO_NAME = "name";

    public static MockControlScenario buildControlScenario(MockLoadProgram loadProgram) {
    	MockLMGearDto gear = MockLMGearDto.builder().gearNumber(loadProgram.getGears().get(0).getGearNumber()).build();
        List<MockLMGearDto> gears = new ArrayList<>();
        gears.add(gear);

        MockProgramDetails program = MockProgramDetails.builder()
                .programId(loadProgram.getProgramId())
                .startOffsetInMinutes(600)
                .stopOffsetInMinutes(300)
                .gears(gears)
                .build();

        List<MockProgramDetails> allPrograms = new ArrayList<>();
        allPrograms.add(program);

        MockControlScenario controlScenario = MockControlScenario.builder()
                .name("ControlScenarioTest")
                .allPrograms(allPrograms)
                .build();

        return controlScenario;

    }
}
