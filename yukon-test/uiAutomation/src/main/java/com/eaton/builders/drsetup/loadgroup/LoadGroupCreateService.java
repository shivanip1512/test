package com.eaton.builders.drsetup.loadgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.builders.drsetup.gears.EcobeeCycleGearBuilder;
import com.eaton.builders.drsetup.loadprogram.LoadProgramCreateBuilder;
import com.eaton.builders.drsetup.loadprogram.ProgramEnums;

public class LoadGroupCreateService {

    public static Pair<JSONObject, JSONObject> buildAndCreateVirtualItronLoadGroup() {
        return new LoadGroupItronCreateBuilder.Builder(Optional.empty())
                .withRelay(Optional.empty())
                .withKwCapacity(Optional.empty())
                .withDisableGroup(Optional.empty())
                .withDisableControl(Optional.empty())
                .create();
    }
    
    public static Pair<JSONObject, JSONObject> buildAndCreateEcobeeProgramWithCycleGear() {
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
}
