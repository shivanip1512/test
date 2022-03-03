package com.eaton.builders.tools.points;

import java.util.Random;

public class PointEnums {

    public enum ArchiveType {
        NONE("NONE"),
        ON_CHANGE("ON_CHANGE");

        private final String archiveType;

        ArchiveType(String archiveType) {
            this.archiveType = archiveType;
        }

        public String getArchiveType() {
            return this.archiveType;
        }

        public static ArchiveType getRandomArchiveType() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
    
    public enum StateGroupId {
        METER_PROGRAMMING(-29),
        RELAY_STATE(-28),
        NO_YES(-27),
        SCADA_OVERRIDE_TYPE(-26),
        YES_NO(-25),
        SACADA_TRIP_CLOSE(-24),
        VAR_VOLTAGE_INPUT(-23),
        CBC8_HARDWARE_TYPE(-22),
        CBC_DOOR_STATUS(-21),
        IGNORED_CONTROL(-20),
        RF_DEMAND_RESET(-19),
        LCR_SERVICE_STATUS(-18),
        LAST_CONTROL(-17),
        EVENT_STATUS(-16),
        SIGNAL_STRENGTH(-15),
        OUTAGE_STATUS(-14),
        COMMISSIONED_STATE(-13),
        RFN_DISCONNECT_STATUS(-12),
        COMM_STATUS_STATE(-11),
        PHASE_STATUS(-10),
        THREE_STATE_STATUS(-9),
        TWO_STATE_ACTIVE(-8),
        THREAD_MONITOR(-7),
        FOUR_TEN_DISCONNECT(-6),
        EVENT_PRIORITY(-5),
        DEFAULT_CALCULATED(-3),
        DEFAULT_ACCUMULATOR(-2),
        DEFAULT_ANALOG(-1),
        SYSTEM_STATE(0),
        TWO_STATE_STATUS(1),
        CAP_BANK_STATUS(3),
        TRUE_FALSE(4);

        private final Integer stateGrpId;

        StateGroupId(Integer stateGrpId) {
            this.stateGrpId = stateGrpId;
        }

        public Integer getStateGroupId() {
            return this.stateGrpId;
        }

        public static StateGroupId getRandomStateGroupId() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
}
