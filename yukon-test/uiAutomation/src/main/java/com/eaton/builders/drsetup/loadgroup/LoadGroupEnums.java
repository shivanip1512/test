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

        private final String AREA_CODE;

        RippleAreaCode(String areaCode) {
            this.AREA_CODE = areaCode;
        }

        public String getAreaCode() {
            return this.AREA_CODE;
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

        private final String GROUP;

        RippleGroup(String group) {
            this.GROUP = group;
        }

        public String getGroup() {
            return this.GROUP;
        }

        public static RippleGroup getRandomGroup() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }

    public enum RippleShedTime {
        Continuous_Latch("0"),
        Seven_minutes_30_seconds("450"),
        Fifteen_minutes("900"),
        Thirty_minutes("1800"),
        One_hour("3600");

        private final String SHED_TIME;

        RippleShedTime(String shedTime) {
            this.SHED_TIME = shedTime;
        }

        public String getShedTime() {
            return this.SHED_TIME;
        }

        public static RippleShedTime getRandomShedTime() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
}
