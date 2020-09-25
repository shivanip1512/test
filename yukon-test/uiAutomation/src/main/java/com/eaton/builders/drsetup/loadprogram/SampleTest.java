package com.eaton.builders.drsetup.loadprogram;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.gears.GearEnums;
import com.eaton.builders.drsetup.gears.GearHelper;
import com.eaton.builders.drsetup.loadprogram.LoadProgramCreateBuilder;
import com.eaton.builders.drsetup.loadprogram.LoadProgramCreateBuilder.Builder;
import com.eaton.builders.drsetup.loadprogram.ProgramEnums;

public class SampleTest {

    @Test
    public void test1() {
        List<JSONObject> gears = new ArrayList<JSONObject>();
        gears.add(GearHelper.createGearFields(GearEnums.GearType.EcobeeCycle));
        gears.add(GearHelper.createGearFields(GearEnums.GearType.EcobeeSetpoint));

        List<Integer> assignedGroupIds = new ArrayList<Integer>();
        assignedGroupIds.add(1232);
        assignedGroupIds.add(596);

        Pair<JSONObject, JSONObject> pair = LoadProgramCreateBuilder
                .buildLoadProgram(ProgramEnums.ProgramType.ECOBEE_PROGRAM, gears, assignedGroupIds).create();
        Pair<JSONObject, JSONObject> pair2 = LoadProgramCreateBuilder
                .buildLoadProgram(ProgramEnums.ProgramType.ECOBEE_PROGRAM, gears, assignedGroupIds).create();

        Builder builder = LoadProgramCreateBuilder.buildLoadProgram(ProgramEnums.ProgramType.ECOBEE_PROGRAM, gears,
                assignedGroupIds);
        Pair<JSONObject, JSONObject> pair3 = builder.create();
        builder.withName(Optional.of("DummyPgm12"));
        Pair<JSONObject, JSONObject> pair4 = builder.create();
    }
}
