package com.cannontech.amr.csr.model;

/**
 * Enumeration of the fields for csr pao search
 */
public enum CsrSearchField {

    METERNUMBER("Meter Number"), 
    PAONAME("Device Name") {
        public String getSearchQueryField() {
            return "ypo.paoname";
        }

    },
    TYPE("Device Type") {
        public String getSearchQueryField() {
            return "ypo.type";
        }

    },
    ADDRESS("Address"), 
    ROUTE(
            "Route") {
        public String getSearchQueryField() {
            return "rypo.paoname";
        }
    };

    private String csrString = null;

    // Constructor
    private CsrSearchField(String csrString) {
        this.csrString = csrString;
    }

    /**
     * Method to get the name of the query field for this CsrSearchField
     * @return The name of the table column
     */
    public String getSearchQueryField() {
        return toString();
    }

    /**
     * Method to get the csr friendly name for this CsrSearchField 
     * @return The name
     */
    public String getCsrString() {
        return csrString;
    }

    
    /**
     * Method to get the toString() value for this CsrSearchField (neccessary for JSP EL) 
     * @return toString() value
     */
    public String getName() {
        return toString();
    }

}
