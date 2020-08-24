package com.eaton.builders.drsetup.loadgroup;

import java.util.Random;

public class LoadGroupEnums {

    public enum AddressUsage {
        UTILITY("UTILITY"),
        SECTION("SECTION"),
        CLASS("CLASS"),
        DIVISION("DIVISION"),
        SERIAL("SERIAL");

        private final String ADDRESS_USAGE;

        AddressUsage(String addressUsage) {
            this.ADDRESS_USAGE = addressUsage;
        }

        public String getAddressUsage() {
            return this.ADDRESS_USAGE;
        }
    }

    public enum RelayUsage {
        RELAY_1("RELAY_1"),
        RELAY_2("RELAY_2"),
        RELAY_3("RELAY_3"),
        RELAY_4("RELAY_4");

        private final String RELAY_USAGE;

        RelayUsage(String relayUsage) {
            this.RELAY_USAGE = relayUsage;
        }

        public String getRelayUsage() {
            return this.RELAY_USAGE;
        }
    }

    public enum RouteId {
        firstRouteId(84),
        secondRouteId(28),
        thirdRouteId(32),
        fourthRouteId(36),
        fifthRouteId(40),
        sixthRouteId(43),
        seventhRouteId(46),
        eighthRouteId(49),
        ninthRouteId(52),
        tenthRouteId(55),
        eleventhRouteId(58),
        twelfthRouteId(62),
        thirteenthRouteId(66),
        fourteenthRouteId(69),
        fifteenthRouteId(73),
        sixteenthRouteId(77),
        seventeenthRouteId(80);

        private final int ROUTE_ID;

        RouteId(Integer route_Id) {
            this.ROUTE_ID = route_Id;
        }

        public Integer getRouteId() {
            return this.ROUTE_ID;
        }

        public static RouteId getRandomRouteId() {
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

}
