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

    public enum RouteId {
        ACCU710A(28),
        ACCU711(32),
        ACCU721(36),
        ALCUEASTRIVER(40),
        APAGINGTAPTERMINAL(43),
        AREPEATER800(52),
        AREPEATER801(55),
        AREPEATER900(46),
        AREPEATER902(49),
        AREPEATER921(58),
        ARTC(62),
        ARTULMI(66),
        ASNPPTERMINAL(69),
        ATCU5000(73),
        ATCU5500(77),
        AWCTPTERMINAL(80),
        AXML(84);

        private final int routeId;

        RouteId(Integer routeId) {
            this.routeId = routeId;
        }

        public Integer getRouteId() {
            return this.routeId;
        }

        public static RouteId getRandomRouteId() {
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

    public enum PointId {
        SCADA_Override(4230),
        CVR_Mode(4231),
        Capacitor_Bank_State(4249),
        Remote_Control_Mode(4251),
        OVUV_Control(4255),
        Manual_Control_Mode(4265);

        private final Integer POINT_ID;

        PointId(Integer pointId) {
            this.POINT_ID = pointId;
        }

        public Integer getPointId() {
            return this.POINT_ID;
        }

        public static PointId getRandomPointId() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }

    public enum PointStartControlRawState {
        True(1),
        False(0);

        private final Integer START_CONTROL_RAW_STATE;

        PointStartControlRawState(Integer startControlRawState) {
            this.START_CONTROL_RAW_STATE = startControlRawState;
        }

        public Integer getPointStartControlRawState() {
            return this.START_CONTROL_RAW_STATE;
        }

        public static PointStartControlRawState getRandomPointStartControlRawState() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
}