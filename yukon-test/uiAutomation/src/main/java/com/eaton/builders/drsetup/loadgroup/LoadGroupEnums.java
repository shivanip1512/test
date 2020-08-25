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
        RELAY_S("RELAY_S");

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
        LOAD_1("LOAD_1"),
        LOAD_2("LOAD_2"),
        LOAD_3("LOAD_3"),
        LOAD_4("LOAD_4"),
        LOAD_5("LOAD_5"),
        LOAD_6("LOAD_6"),
        LOAD_7("LOAD_7"),
        LOAD_8("LOAD_8");

        private final String relayUsage;

        RelayUsageExpresscom(String relayUsage) {
            this.relayUsage = relayUsage;
        }

        public String getRelayUsage() {
            return this.relayUsage;
        }

        public static RelayUsageExpresscom getRandomRelayUsage() {

            Random random = new Random();
            return values()[random.nextInt(values().length)];
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
        DIVISION("DIVISION");

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
}