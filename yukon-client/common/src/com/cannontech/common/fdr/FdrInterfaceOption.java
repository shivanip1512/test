package com.cannontech.common.fdr;

/**
 * Class to represent the values for each interface
 */
public enum FdrInterfaceOption {
    //Note: we may want to add a parameter here indicating which FdrInterfaceType these belong to.
    //Note2: this was quickly done for the Destination/Source Options, it should be done for every option.
    INET_DESTINATION_SOURCE("Destination/Source"),
    RCCS_DESTINATION_SOURCE("Destination/Source"),
    RDEX_DESTINATION_SOURCE("Destination/Source"),
    ACSMULTI_DESTINATION_SOURCE("Destination/Source"),
    DNPSLAVE_DESTINATION_SOURCE("Destination/Source"),
    VALMETMULTI_DESTINATION_SOURCE("Destination/Source");
        
    private final String optionLabel;
    
    private FdrInterfaceOption(String optionLabel) {
        this.optionLabel = optionLabel;
    }
    
    public String getOptionLabel() {
        return this.optionLabel;
    }
}
