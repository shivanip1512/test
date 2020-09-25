package com.eaton.builders.drsetup.loadprogram;

import java.util.Random;

public class ProgramEnums {
    
    public enum ProgramType {
        DIRECT_PROGRAM("LM_DIRECT_PROGRAM"),
        SEP_PROGRAM("LM_SEP_PROGRAM"), 
        ECOBEE_PROGRAM("LM_ECOBEE_PROGRAM"), 
        HONEYWELL_PROGRAM("LM_HONEYWELL_PROGRAM"), 
        ITRON_PROGRAM("LM_ITRON_PROGRAM"), 
        NEST_PROGRAM("LM_NEST_PROGRAM"), 
        METER_DISCONNECT_PROGRAM("LM_METER_DISCONNECT_PROGRAM");

        private final String type;

        ProgramType(String type) {
            this.type = type;
        }

        public String getProgramType() {
            return this.type;
        }
    }

    public enum OperationalState {
        Automatic("Automatic"),
        Manual_Only("ManualOnly"),
        Timed("Timed");

        private final String operationalState;

        OperationalState(String operationalState) {
            this.operationalState = operationalState;
        }

        public String getOperationalState() {
            return this.operationalState;
        }

        public static OperationalState getRandomOperationalState() {

            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
}
