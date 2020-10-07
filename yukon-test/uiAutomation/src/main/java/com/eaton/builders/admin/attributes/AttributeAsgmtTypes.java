package com.eaton.builders.admin.attributes;

import java.util.Random;

public class AttributeAsgmtTypes {
    
    public enum PaoTypes {
        
        LCR6200_RFN("LCR6200_RFN"),
        LCR6600_RFN("LCR6600_RFN"),
        LCR6700_RFN("LCR6700_RFN"),
        MCTBROADCAST("MCTBROADCAST"),
        MCT310IL("MCT310IL"),
        MCT430A("MCT430A"),
        RFN410CL("RFN410CL"),
        RFN420CD("RFN420CD"),
        RFN420FL("RFN420FL"),
        RFN430SL4("RFN430SL4"),
        RFN530S4X("RFN530S4X"),
        VIRTUAL_SYSTEM("VIRTUAL_SYSTEM"),
        WRL420CD("WRL420CD"),
        WRL420CL("WRL420CL");
        
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

    public enum PaoTypesUI {
        LCR6200_RFN("LCR-6200 RFN"),
        LCR6600_RFN("LCR-6600 RFN"),
        LCR6700_RFN("LCR-6700 RFN"),
        MCTBROADCAST("MCT Broadcast"),
        MCT310IL("MCT-310IL"),
        MCT430A("MCT-430A"),
        RFN410CL("RFN-410cL"),
        RFN420CD("RFN-420cD"),
        RFN420FL("RFN-420fL"),
        RFN430SL4("RFN-430SL4"),
        RFN530S4X("RFN-530S4x"),
        VIRTUAL_SYSTEM("Virtual System"),
        WRL420CD("WRL-420cD"),
        WRL420CL("WRL-420cL");

        private final String paoType;

        PaoTypesUI(String paoType) {
            this.paoType = paoType;
        }

        public String getPaoType() {
            return this.paoType;
        }

        public static PaoTypesUI getRandomPaoType() {
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
    
    public enum PointTypesUI {
        STATUS("Status"),
        ANALOG("Analog"),
        PULSE_ACCUMULATOR("Pulse Accumulator"),
        DEMAND_ACCUMULATOR("Demand Accumulator"),
        CALC_ANALOG("Calc Analog"),
        STATUS_OUTPUT("Status Output"),
        ANALOG_OUTPUT("Analog Output"),
        SYSTEM("System"),
        CALC_STATUS("Calc Status");
    
        private final String pointType;
        
        PointTypesUI(String pointType) {
            this.pointType = pointType;
        }
        
        public String getPointType() {
            return this.pointType;
        }
        
        public static PointTypesUI getRandomPointType() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
}
