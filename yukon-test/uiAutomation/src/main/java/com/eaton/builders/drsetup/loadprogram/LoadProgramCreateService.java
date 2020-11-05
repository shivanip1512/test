package com.eaton.builders.drsetup.loadprogram;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.builders.drsetup.gears.EcobeeCycleGearBuilder;
import com.eaton.builders.drsetup.gears.EcobeeSetpointGearBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupEcobeeCreateBuilder;
import com.eaton.builders.drsetup.loadprogram.ProgramEnums.OperationalState;
import com.eaton.builders.drsetup.loadprogram.ProgramEnums.ProgramType;

public class LoadProgramCreateService {
    
    public static Pair<JSONObject, JSONObject> createEcobeeProgramWithCycleGear() {
        List<JSONObject> gears = new ArrayList<>();
        gears.add(EcobeeCycleGearBuilder.gearBuilder().build());

        Pair<JSONObject, JSONObject> pairLdGrp = new LoadGroupEcobeeCreateBuilder.Builder(Optional.empty())
                .withKwCapacity(Optional.empty()).create();
        
        JSONObject responseLdGrp = pairLdGrp.getValue1();
        int ldGrpId = responseLdGrp.getInt("id");
        List<Integer> assignedGroupIds = new ArrayList<>(List.of(ldGrpId));
        
        return new LoadProgramCreateBuilder.Builder(ProgramEnums.ProgramType.ECOBEE_PROGRAM, gears,
                assignedGroupIds).withGears(gears)
                        .withName(Optional.empty())
                        .withOperationalState(Optional.of(ProgramEnums.OperationalState.Automatic))
                        .create();
    }
    
    public static Pair<JSONObject, JSONObject> createEcobeeProgramAllFieldsWithSetPointGear() {
        Pair<JSONObject, JSONObject> pair = new LoadGroupEcobeeCreateBuilder.Builder(Optional.empty())
                .create();
        
        JSONObject ldGrp = pair.getValue1();
        Integer ldGrpId = ldGrp.getInt("id");
        
        JSONObject gear = EcobeeSetpointGearBuilder.gearBuilder()
                .withName("TestGear")
                .build();

        return LoadProgramCreateBuilder.buildLoadProgram(ProgramType.ECOBEE_PROGRAM, new ArrayList<>(List.of(gear)), new ArrayList<>(List.of(ldGrpId)))
                .withName(Optional.empty())
                .withOperationalState(Optional.of(OperationalState.Manual_Only))
                .withControlWindowOneAvailableStartTimeInMinutes(Optional.of(60))
                .withcontrolWindowOneAvailableStopTimeInMinutes(Optional.of(60))
                .withcontrolWindowTwoAvailableStartTimeInMinutes(Optional.of(60))
                .withcontrolWindowTwoAvailableStopTimeInMinutes(Optional.of(60))
                .create();
    }
}
