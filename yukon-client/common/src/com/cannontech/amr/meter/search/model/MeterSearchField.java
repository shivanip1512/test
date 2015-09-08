package com.cannontech.amr.meter.search.model;

/**
 * Enumeration of the fields for meter search
 */
public enum MeterSearchField {

    METERNUMBER("Meter Number"), 
    PAONAME("Device Name") {
        @Override
        public String getSearchQueryField() {
            return "ypo.paoname";
        }

    },
    TYPE("Device Type") {
        @Override
        public String getSearchQueryField() {
            return "ypo.type";
        }

    },
    ADDRESS("Address"), 
    SERIALNUMBER("SerialNumber"),
    ROUTE(
            "Route") {
        @Override
        public String getSearchQueryField() {
            return "rypo.paoname";
        }
    };

    private final String meterSearchString;

    // Constructor
    private MeterSearchField(String meterSearchString) {
        this.meterSearchString = meterSearchString;
    }

    /**
     * Method to get the name of the query field for this MeterSearchField
     * @return The name of the table column
     */
    public String getSearchQueryField() {
        return toString();
    }

    /**
     * Method to get the meter search friendly name for this MeterSearchField 
     * @return The name
     */
    public String getMeterSearchString() {
        return meterSearchString;
    }

    
    /**
     * Method to get the toString() value for this MeterSearchField (neccessary for JSP EL) 
     * @return toString() value
     */
    public String getName() {
        return toString();
    }

}
