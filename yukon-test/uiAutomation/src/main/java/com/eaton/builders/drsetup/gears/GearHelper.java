package com.eaton.builders.drsetup.gears;

import org.json.JSONObject;

public class GearHelper {

    public final static JSONObject createGearFields(GearEnums.GearType gearType) {

        JSONObject gear = null;
        switch (gearType) {
        case EcobeeCycle:
            gear = EcobeeCycleGearBuilder.gearBuilder().build();
            break;
        case EcobeeSetpoint:
            gear = EcobeeSetpointGearBuilder.gearBuilder().build();
            break;
        }
        return gear;
    }
}
