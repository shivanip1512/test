package com.cannontech.common.fdr;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.util.Range;

/**
 * Class to represent the option values for each FDR interface.
 */
public enum FdrInterfaceOption {
    ACS_CATEGORY("Category", FdrOptionType.COMBO, false, new String[] { "PSEUDO", "REAL", "CALCULATED" }) {
        @Override
        public boolean validate(String value) {
            return this.isOptionValue(value);
        }
    },
    ACS_REMOTE("Remote", FdrOptionType.TEXT, false, null),
    ACS_POINT("Point", FdrOptionType.TEXT, false, null),
    ACSMULTI_CATEGORY("Category", FdrOptionType.COMBO, false, new String[] { "PSEUDO", "REAL", "CALCULATED" }) {
        @Override
        public boolean validate(String value) {
            return this.isOptionValue(value);
        }
    },
    ACSMULTI_REMOTE("Remote", FdrOptionType.TEXT, false, null),
    ACSMULTI_POINT("Point", FdrOptionType.TEXT, false, null),
    ACSMULTI_DESTINATION_SOURCE("Destination/Source", FdrOptionType.TEXT, true, null),
    BEPC_COOP_ID("Coop Id", FdrOptionType.TEXT, false, null),
    BEPC_FILENAME("Filename", FdrOptionType.TEXT, false, null),
    CYGNET_POINTID("PointID", FdrOptionType.TEXT, false, null),
    DNPSLAVE_MASTERID("MasterId", FdrOptionType.TEXT, false, null) {
        @Override
        public boolean validate(String value) {
            return isIntegerInRange(value, 0, Integer.MAX_VALUE);
        }
    },
    DNPSLAVE_SLAVEID("SlaveId", FdrOptionType.TEXT, false, null) {
        @Override
        public boolean validate(String value) {
            return isIntegerInRange(value, 0, Integer.MAX_VALUE);
        }
    },
    DNPSLAVE_OFFSET("Offset", FdrOptionType.TEXT, false, null) {
        @Override
        public boolean validate(String value) {
            return isIntegerInRange(value, 0, Integer.MAX_VALUE);
        }
    },
    DNPSLAVE_DESTINATION_SOURCE("Destination/Source", FdrOptionType.TEXT, true, null),
    DNPSLAVE_MULTIPLIER("Multiplier", FdrOptionType.TEXT, false, new String[] { "1.0" }) { // default value, no list validation
        @Override
        public boolean validate(String value) {
            return isDoubleInRange(value, 0d, Double.MAX_VALUE);
        }
    },
    DSM2FILEIN_OPTION_NUMBER("Option Number", FdrOptionType.COMBO, false, new String[] { "1" }),
    DSM2FILEIN_POINT_ID("Point ID", FdrOptionType.TEXT, false, null),
    DSM2IMPORT_POINT("Point", FdrOptionType.TEXT, false, null),
    INET_DEVICE("Device", FdrOptionType.TEXT, false, null),
    INET_POINT("Point", FdrOptionType.TEXT, false, null),
    INET_DESTINATION_SOURCE("Destination/Source", FdrOptionType.TEXT, true, null),
    LIVEDATA_ADDRESS("Address", FdrOptionType.TEXT, false, null) {
        @Override
        public boolean validate(String value) {
            return isIntegerInRange(value, 0, Integer.MAX_VALUE);
        }
    },
    LIVEDATA_DATA_TYPE("Data Type", FdrOptionType.COMBO, false,
            new String[] { "Data_RealExtended", "Data_DiscreteExtended", "Data_StateExtended", "Data_RealQ", "Data_DiscreteQ",
                    "Data_State", "Data_Discrete", "Data_Real", "Data_RealQTimeTag", "Data_StateQTimeTag",
                    "Data_DiscreteQTimeTag" }) {
        @Override
        public boolean validate(String value) {
            return this.isOptionValue(value);
        }
    },
    LODESTAR_ENH_CUSTOMER("Customer", FdrOptionType.TEXT, false, null),
    LODESTAR_ENH_CHANNEL("Channel", FdrOptionType.TEXT, false, null),
    LODESTAR_ENH_DRIVEPATH("DrivePath", FdrOptionType.TEXT, false, null),
    LODESTAR_ENH_FILENAME("Filename", FdrOptionType.TEXT, false, null),
    LODESTAR_STD_CUSTOMER("Customer", FdrOptionType.TEXT, false, null),
    LODESTAR_STD_CHANNEL("Channel", FdrOptionType.TEXT, false, null),
    LODESTAR_STD_DRIVEPATH("DrivePath", FdrOptionType.TEXT, false, null),
    LODESTAR_STD_FILENAME("Filename", FdrOptionType.TEXT, false, null),
    MULTISPEAK_LM_OBJECTID("ObjectId", FdrOptionType.TEXT, false, null),
    OPC_SERVER_NAME("Server Name", FdrOptionType.TEXT, false, null),
    OPC_OPC_GROUP("OPC Group", FdrOptionType.TEXT, false, null),
    OPC_OPC_ITEM("OPC Item", FdrOptionType.TEXT, false, null),
    PI_TAG_NAME("Tag Name", FdrOptionType.TEXT, false, null),
    PI_PERIOD_SEC("Period (sec)", FdrOptionType.TEXT, false, null) {
        @Override
        public boolean validate(String value) {
            return isIntegerInRange(value, 0, Integer.MAX_VALUE);
        }
    },
    RCCS_DEVICE("Device", FdrOptionType.TEXT, false, null),
    RCCS_POINT("Point", FdrOptionType.TEXT, false, null),
    RCCS_DESTINATION_SOURCE("Destination/Source", FdrOptionType.TEXT, true, null),
    RDEX_TRANSLATION("Translation", FdrOptionType.TEXT, false, null),
    RDEX_DESTINATION_SOURCE("Destination/Source", FdrOptionType.TEXT, true, null),
    STEC_POINT("Point", FdrOptionType.COMBO, false, new String[] { "SYSTEM LOAD", "STEC LOAD" }) {
        @Override
        public boolean validate(String value) {
            return this.isOptionValue(value);
        }
    },
    SYSTEM_CLIENT("Client", FdrOptionType.TEXT, false, null),
    TELEGYR_POINT("Point", FdrOptionType.TEXT, false, null),
    TELEGYR_INTERVAL_SEC("Interval (sec)", FdrOptionType.TEXT, false, null) {
        @Override
        public boolean validate(String value) {
            return isIntegerInRange(value, 0, Integer.MAX_VALUE);
        }
    },
    TEXTIMPORT_POINT_ID("Point ID", FdrOptionType.TEXT, false, null),
    TEXTIMPORT_DRIVEPATH("DrivePath", FdrOptionType.TEXT, false, null),
    TEXTIMPORT_FILENAME("Filename", FdrOptionType.TEXT, false, null),
    TEXTEXPORT_POINT_ID("Point ID", FdrOptionType.TEXT, false, null),
    TRISTATE_POINT("Point", FdrOptionType.COMBO, false, new String[] { "SYSTEM LOAD", "30 Minute AVG" }) {
        @Override
        public boolean validate(String value) {
            return this.isOptionValue(value);
        }
    },
    TRISTATESUB_POINT("Point", FdrOptionType.COMBO, false, new String[] { "Nucla 115/69 Xfmr.", "Happy Canyon 661Idarado",
            "Cascade 115/69 (T2)", "Ames Generation", "Dallas Creek MW", "Dallas Creek MV" }) {
        @Override
        public boolean validate(String value) {
            return this.isOptionValue(value);
        }
    },
    VALMET_POINT("Point", FdrOptionType.TEXT, false, null) {
        @Override
        public boolean validate(String value) {
            return this.isValidLength(value, 16);
        }
    },
    VALMETMULTI_POINT("Point", FdrOptionType.TEXT, false, null) {
        @Override
        public boolean validate(String value) {
            return this.isValidLength(value, 32);
        }
    },
    VALMETMULTI_PORT("Port", FdrOptionType.TEXT, false, null) {
        @Override
        public boolean validate(String value) {
            return isIntegerInRange(value, 0, 65535);
        }
    },
    WABASH_SCHEDNAME("SchedName", FdrOptionType.TEXT, false, null),
    WABASH_PATH("Path", FdrOptionType.TEXT, false, new String[] { "c:\\yukon\\server\\export" }), // default value, no list
                                                                                                  // validation
    WABASH_FILENAME("Filename", FdrOptionType.TEXT, false, new String[] { "control.txt" }), // default value, no list validation
    XA21LM_TRANSLATION("Translation", FdrOptionType.TEXT, false, null),
    ;

    private final String optionLabel;
    private final FdrOptionType optionType;
    private final String[] optionValues;
    private final boolean isDestinationOption;

    private FdrInterfaceOption(String optionLabel, FdrOptionType optionType, boolean isDestinationOption, String[] optionValues) {
        this.optionLabel = optionLabel;
        this.optionType = optionType;
        this.isDestinationOption = isDestinationOption;
        this.optionValues = optionValues;
    }

    public String getOptionLabel() {
        return this.optionLabel;
    }

    public FdrOptionType getOptionType() {
        return this.optionType;
    }

    public String[] getOptionValues() {
        return optionValues;
    }

    /**
     * Concatenates all option values into a single comma-delimited
     * String value.
     */
    public String getOptionValuesString() {
        return StringUtils.join(optionValues, ", ");
    }

    public boolean isDestinationOption() {
        return isDestinationOption;
    }

    public boolean isValid(String value) {
        return validate(value);
    }

    /**
     * Returns true when value is valid for FdrInterfaceOption
     * Override this method to change validation check for each enum.
     */
    protected boolean validate(String value) {
        return true;
    }

    /**
     * Returns true if value is contained in optionValues.
     * Insensitive case compare.
     */
    protected boolean isOptionValue(String value) {
        for (String optionValue : getOptionValues()) {
            if (optionValue.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if value is Integer and within range min/max.
     */
    protected boolean isIntegerInRange(String value, Integer min, Integer max) {
        try {
            Integer valueInt = Integer.valueOf(value);
            Range<Integer> range = Range.inclusive(min, max);
            return (valueInt != null && range.intersects(valueInt));
        } catch (NumberFormatException e) {
            // unable to parse as Int
            return false;
        }
    }

    /**
     * Returns true if value is Double and within range min/max.
     */
    protected boolean isDoubleInRange(String value, Double min, Double max) {
        try {
            Double valueInt = Double.valueOf(value);
            Range<Double> range = Range.inclusive(min, max);
            return (valueInt != null && range.intersects(valueInt));
        } catch (NumberFormatException e) {
            // unable to parse as Double
            return false;
        }
    }

    /**
     * Returns true if length of value string does not exceed maxLength.
     */
    protected boolean isValidLength(String value, int maxLength) {
        try {
            return StringUtils.length(value) <= maxLength;
        } catch (NumberFormatException e) {
            // unable to parse as Double
            return false;
        }
    }
}