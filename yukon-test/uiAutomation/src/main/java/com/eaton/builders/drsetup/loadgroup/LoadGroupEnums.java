package com.eaton.builders.drsetup.loadgroup;

import java.util.Random;

public class LoadGroupEnums {

    public enum RelayUsage {
        RELAY_1("RELAY_1"),
        RELAY_2("RELAY_2"),
        RELAY_3("RELAY_3"),
        RELAY_4("RELAY_4");

        private final String addressingRelayUsage;

        RelayUsage(String relayUsage) {
            this.addressingRelayUsage = relayUsage;
        }

        public String getRelayUsage() {
            return this.addressingRelayUsage;
        }

        public static RelayUsage getRandomRelayUsage() {

            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }

    public enum RelayUsageEmetcon {
        RELAY_A("RELAY_A"),
        RELAY_B("RELAY_B"),
        RELAY_C("RELAY_C"),
        RELAY_ALL("RELAY_ALL");

        private final String relayUsage;

        RelayUsageEmetcon(String relayUsage) {
            this.relayUsage = relayUsage;
        }

        public String getRelayUsage() {
            return this.relayUsage;
        }

        public static RelayUsageEmetcon getRandomRelayUsage() {

            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }

    public enum RelayUsageExpresscom {
    	LOAD_1("Load_1"),
        LOAD_2("Load_2"),
        LOAD_3("Load_3"),
        LOAD_4("Load_4"),
        LOAD_5("Load_5"),
        LOAD_6("Load_6"),
        LOAD_7("Load_7"),
        LOAD_8("Load_8");

        private final String relayUsage;

        RelayUsageExpresscom(String relayUsage) {
            this.relayUsage = relayUsage;
        }

        public String getRelayUsage() {
            return this.relayUsage;
        }
        
        public String getRelayUsageValue() {
            return relayUsage;
        }

        public static String getRandomRelayUsage() {

            Random random = new Random();
            return values()[random.nextInt(values().length)].getRelayUsageValue();
        }
    }

    public enum AddressLevelMCT {
        BRONZE("BRONZE"),
        LEAD("LEAD"),
        MCT_ADDRESS("MCT_ADDRESS");

        private final String addressLevel;

        AddressLevelMCT(String addressLevelMCT) {
            this.addressLevel = addressLevelMCT;
        }

        public String getAddressLevelMCT() {
            return this.addressLevel;
        }

        public static AddressLevelMCT getRandomAddressLevelMCT() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }

    public enum AddressUsageEmetcon {
        GOLD("GOLD"),
        SILVER("SILVER");

        private final String addressUsage;

        AddressUsageEmetcon(String addressUsage) {
            this.addressUsage = addressUsage;
        }

        public String getAddressUsage() {
            return this.addressUsage;
        }

        public static AddressUsageEmetcon getRandomAddressUsage() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }

    public enum AddressUsageExpresscom {
        GEO("GEO"),
        SUBSTATION("SUBSTATION"),
        FEEDER("FEEDER"),
        ZIP("ZIP"),
        USER("USER"),
        SERIAL("SERIAL"),
        LOAD("LOAD"),
        PROGRAM("PROGRAM"),
        SPLINTER("SPLINTER");

        private final String addressUsage;

        AddressUsageExpresscom(String addressUsage) {
            this.addressUsage = addressUsage;
        }

        public String getAddressLevelMCT() {
            return this.addressUsage;
        }

        public static AddressUsageExpresscom getRandomAddressUsage() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }    

    public enum AddressUsageVersacom {
        UTILITY("UTILITY"),
        SECTION("SECTION"),
        CLASS("CLASS"),
        DIVISION("DIVISION"),
        SERIAL("SERIAL");

        private final String addressUsage;

        AddressUsageVersacom(String addressUsage) {
            this.addressUsage = addressUsage;
        }

        public String getAddressLevelMCT() {
            return this.addressUsage;
        }

        public static AddressUsageVersacom getRandomAddressUsage() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }

    public enum RippleAreaCode {
        UNIVERSAL("UNIVERSAL"),
        MINNKOTA("MINNKOTA"),
        BELTRAMI("BELTRAMI"),
        CASS_COUNTY("CASS_COUNTY"),
        CAVALIER_RURAL("CAVALIER_RURAL"),
        CLEARWATER_POLK("CLEARWATER_POLK"),
        NODAK_RURAL("NODAK_RURAL"),
        NORTH_STAR("NORTH_STAR"),
        PKM_ELECTRIC("PKM_ELECTRIC"),
        RED_LAKE("RED_LAKE"),
        RED_RIVER_VALLEY("RED_RIVER_VALLEY"),
        ROSEAU_ELECTRIC("ROSEAU_ELECTRIC"),
        SHEYENNE_VALLEY("SHEYENNE_VALLEY"),
        WILD_RICE("WILD_RICE"),
        NMPA("NMPA");

        private final String areaCode;

        RippleAreaCode(String areaCode) {
            this.areaCode = areaCode;
        }

        public String getAreaCode() {
            return this.areaCode;
        }

        public static RippleAreaCode getRandomAreaCode() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }

    public enum RippleGroup {
        TST("TST"),
        ONE_00("ONE_00"),
        ONE_01("ONE_01"),
        ONE_02("ONE_02"),
        TWO_00("TWO_00"),
        TWO_01("TWO_01"),
        TWO_02("TWO_02"),
        TWO_03("TWO_03"),
        TWO_04("TWO_04"),
        THREE_00("THREE_00"),
        THREE_01("THREE_01"),
        THREE_06("THREE_06"),
        THREE_07("THREE_07"),
        THREE_09("THREE_09"),
        THREE_01_AND_THREE_09("THREE_01_AND_THREE_09"),
        FOUR_00("FOUR_00"),
        FOUR_01("FOUR_01"),
        FOUR_02("FOUR_02"),
        SIX_00("SIX_00"),
        SIX_01("SIX_01"),
        SIX_06("SIX_06");

        private final String group;

        RippleGroup(String group) {
            this.group = group;
        }

        public String getGroup() {
            return this.group;
        }

        public static RippleGroup getRandomGroup() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }   
    
    public enum RippleGroupUi {
        TST("TST"),
        ONE_00("LG  1.00"),
        ONE_01("LG  1.01"),
        ONE_02("LG  1.02"),
        TWO_00("LG  2.00"),
        TWO_01("LG  2.01"),
        TWO_02("LG  2.02"),
        TWO_03("LG  2.03"),
        TWO_04("LG  2.04"),
        THREE_00("LG  3.00"),
        THREE_01("LG  3.01"),
        THREE_06("LG  3.06"),
        THREE_07("LG  3.07"),
        THREE_09("LG  3.09"),
        THREE_01_AND_THREE_09("LG  3.01_3.09"),
        FOUR_00("LG  4.00"),
        FOUR_01("LG  4.01"),
        FOUR_02("LG  4.02"),
        SIX_00("LG  6.00"),
        SIX_01("LG  6.01"),
        SIX_06("LG  6.06");

        private final String group;

        RippleGroupUi(String group) {
            this.group = group;
        }

        public String getGroup() {
            return this.group;
        }        
    }       

    public enum RippleShedTime {
        CONTINUOUS_LATCH("0"),
        SEVEN_MINUTES_30SECOND("450"),
        FIFTEEN_MINUTES("900"),
        THIRTY_MINUTES("1800"),
        ONE_HOUR("3600");

        private final String shedTime;

        RippleShedTime(String shedTime) {
            this.shedTime = shedTime;
        }

        public String getShedTime() {
            return this.shedTime;
        }

        public static RippleShedTime getRandomShedTime() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }

    public enum ProtocolPriorityExpresscom {
        DEFAULT("DEFAULT"),
        MEDIUM("MEDIUM"),
        HIGH("HIGH"),
        HIGHEST("HIGHEST");

        private final String protocolPriority;

        ProtocolPriorityExpresscom(String protocolPriority) {
            this.protocolPriority = protocolPriority;
        }

        public String getProtocolPriority() {
            return this.protocolPriority;
        }

        public static ProtocolPriorityExpresscom getRandomProtocolPriority() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }

    public enum DigiSepDeviceClassEnum {

        HVAC_COMPRESSOR_FURNACE("HVAC_COMPRESSOR_FURNACE"),
        BASEBOARD_HEAT("BASEBOARD_HEAT"),
        WATER_HEATER("WATER_HEATER"),
        POOL_PUMP("POOL_PUMP"),
        SMART_APPLIANCE("SMART_APPLIANCE"),
        IRRIGATION_PUMP("IRRIGATION_PUMP"),
        MANAGED_COMMERCIAL_INDUSTRIAL("IRRIGATION_PUMP"),
        SIMPLE_RESIDENTIAL_ON_OFF("SIMPLE_RESIDENTIAL_ON_OFF"),
        EXTERIOR_LIGHTING("EXTERIOR_LIGHTING"),
        INTERIOR_LIGHTING("INTERIOR_LIGHTING"),
        ELECTRIC_VEHICLE("ELECTRIC_VEHICLE"),
        GENERATION_SYSTEMS("GENERATION_SYSTEMS");

        private final String deviceClassSet;

        DigiSepDeviceClassEnum(String deviceClassSet) {
            this.deviceClassSet = deviceClassSet;
        }

        public String getDeviceClassSet() {
            return this.deviceClassSet;
        }

        public static DigiSepDeviceClassEnum getRandomDeviceClassSet() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
    
    public enum DigiSepDeviceClassUIEnum {
        HVAC_COMPRESSOR_FURNACE("HVAC Compressor or Furnace"),
        BASEBOARD_HEAT("Strip Heaters/Baseboard Heaters"),
        WATER_HEATER("Water Heater"),
        POOL_PUMP("Pool Pump/Spa/Jacuzzi"),
        SMART_APPLIANCE("Smart Appliances"),
        IRRIGATION_PUMP("Irrigation Pump"),
        MANAGED_COMMERCIAL_INDUSTRIAL("Managed Commercial and Industrial Loads"),
        SIMPLE_RESIDENTIAL_ON_OFF("Simple Misc, Residential On/Off Loads"),
        EXTERIOR_LIGHTING("Exterior Lighting"),
        INTERIOR_LIGHTING("Interior Lighting"),
        ELECTRIC_VEHICLE("Electric Vehicle"),
        GENERATION_SYSTEMS("Generation Systems");

        private final String deviceClassSet;

        DigiSepDeviceClassUIEnum(String deviceClassSet) {
            this.deviceClassSet = deviceClassSet;
        }

        public String getDeviceClassSet() {
            return this.deviceClassSet;
        }          
    }

    public enum PointStartControlRawState {
        TRUE(1),
        FALSE(0);

        private final Integer startControlRawState;

        PointStartControlRawState(Integer startControlRawState) {
            this.startControlRawState = startControlRawState;
        }

        public Integer getPointStartControlRawState() {
            return this.startControlRawState;
        }

        public static PointStartControlRawState getRandomPointStartControlRawState() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
}
