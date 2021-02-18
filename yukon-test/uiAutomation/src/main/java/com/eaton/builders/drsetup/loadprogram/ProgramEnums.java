package com.eaton.builders.drsetup.loadprogram;

import java.util.Random;

public class ProgramEnums {
    
    public enum ProgramType {
        DIRECT_PROGRAM("LM_DIRECT_PROGRAM", "Direct"),
        SEP_PROGRAM("LM_SEP_PROGRAM", "Sep"), 
        ECOBEE_PROGRAM("LM_ECOBEE_PROGRAM", "Ecobee"), 
        HONEYWELL_PROGRAM("LM_HONEYWELL_PROGRAM", "Honeywell"), 
        ITRON_PROGRAM("LM_ITRON_PROGRAM", "Itron"), 
        NEST_PROGRAM("LM_NEST_PROGRAM", "Nest"), 
        METER_DISCONNECT_PROGRAM("LM_METER_DISCONNECT_PROGRAM", "Disconnect");

        private final String type;
        private final String desc;

        ProgramType(String type, String desc) {
            this.type = type;
            this.desc = desc;
        }

        public String getProgramType() {
            return this.type;
        }
        
        public String getDesc() {
            return this.desc;
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
