package com.eaton.builders.drsetup.loadgroup;

import java.util.Random;

public class LoadGroupEnums {

    public enum RelayUsageMCT {
        RELAY_1("RELAY_1"),
        RELAY_2("RELAY_2"),
        RELAY_3("RELAY_3"),
        RELAY_4("RELAY_4");

        private final String relayUsage;

        RelayUsageMCT(String relayUsage) {
            this.relayUsage = relayUsage;
        }

        public String getRelayUsage() {
            return this.relayUsage;
        }

        public static RelayUsageMCT getRandomRelayUsage() {

            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }

    public enum AddressLevelMCT {
        BRONZE("BRONZE"),
        LEAD("LEAD"),
        MCT_ADDRESS("MCT_ADDRESS");

        private final String addressLevelMCT;

        AddressLevelMCT(String addressLevelMCT) {
            this.addressLevelMCT = addressLevelMCT;
        }

        public String getAddressLevelMCT() {
            return this.addressLevelMCT;
        }

        public static AddressLevelMCT getRandomAddressLevelMCT() {
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
}
