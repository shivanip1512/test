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
        LCR6200_RFN("LCR-6200 RFN", 0),
        LCR6600_RFN("LCR-6600 RFN", 1),
        LCR6700_RFN("LCR-6700 RFN", 2),
        MCTBROADCAST("MCT Broadcast", 3),
        MCT310IL("MCT-310IL", 4),
        MCT430A("MCT-430A", 5),
        RFN410CL("RFN-410cL", 6),
        RFN420CD("RFN-420cD", 7),
        RFN420FL("RFN-420fL", 8),
        RFN430SL4("RFN-430SL4", 9),
        RFN530S4X("RFN-530S4x", 10),
        VIRTUAL_SYSTEM("Virtual System", 11),
        WRL420CD("WRL-420cD", 12),
        WRL420CL("WRL-420cL", 13);

        private final String paoType;
        private final Integer index;

        PaoTypesUI(String paoType, Integer index) {
            this.paoType = paoType;
            this.index = index;
        }

        public String getPaoType() {
            return this.paoType;
        }
        
        public Integer getIndex() {
            return this.index;
        }

        public static PaoTypesUI getRandomPaoType() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
    
    public enum PointTypes {
        STATUS("Status"),
        ANALOG("Analog"),
        PULSEACCUMULATOR("PulseAccumulator"),
        DEMANDACCUMULATOR("DemandAccumulator"),
        CALCANALOG("CalcAnalog"),
        STATUSOUTPUT("StatusOutput"),
        ANALOGOUTPUT("AnalogOutput"),
        SYSTEM("System"),
        CALCSTATUS("CalcStatus");
    
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
        PULSEACCUMULATOR("Pulse Accumulator"),
        DEMANDACCUMULATOR("Demand Accumulator"),
        CALCANALOG("Calc Analog"),
        STATUSOUTPUT("Status Output"),
        ANALOGOUTPUT("Analog Output"),
        SYSTEM("System"),
        CALCSTATUS("Calc Status");
    
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
