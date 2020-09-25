package com.eaton.builders.admin.attributes;

import java.util.Random;

public class AttributeAssignmentTypes {

    public enum PaoTypes {
        LCR_6200_RFN("LCR-6200 RFN"),
        LCR_6600_RFN("LCR-6600 RFN"),
        LCR_6700_RFN("LCR-6700 RFN"),
        MCT_BROADCAST("MCT Broadcast"),
        MCT_310IL("MCT-310IL"),
        MCT_430A("MCT-430A"),
        RFN_410CL("RFN-410cL"),
        RFN_420CD("RFN-420cD"),
        RFN_420FL("RFN-420fL"),
        RFN_430SL4("RFN-430SL4"),
        RFN_530S4X("RFN-530S4x"),
        VIRTUAL_SYSTEM("Virtual System"),
        WRL_420CD("WRL-420cD"),
        WRL_420CL("WRL-420cL");

        private final String paoType;

        PaoTypes(String paoType) {
            this.paoType = paoType;
        }

        public String getPaoType() {
            return this.paoType;
        }

        public static PaoTypes getRandomPaoType() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
    
    public enum PointTypes {
        STATUS("Status"),
        ANALOG("Analog"),
        PULSE_ACCUMULATOR("PulseAccumulator"),
        DEMAND_ACCUMULATOR("DemandAccumulator"),
        CALC_ANALOG("CalcAnalog"),
        STATUS_OUTPUT("StatusOutput"),
        ANALOG_OUTPUT("AnalogOutput"),
        SYSTEM("System"),
        CALC_STATUS("CalcStatus");
    
        private final String pointType;
        
        PointTypes(String pointType) {
            this.pointType = pointType;
        }
        
        public String getPointType() {
            return this.pointType;
        }
        
        public static PointTypes getRandomPointType() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
}
