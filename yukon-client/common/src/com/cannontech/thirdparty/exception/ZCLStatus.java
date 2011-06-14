package com.cannontech.thirdparty.exception;

//http://read.pudn.com/downloads145/doc/comm/632402/%5BAFG-ZigBee_Cluster_Library_Public_download_version%5D/%5BAFG-ZigBee_Cluster_Library_Public_download_version%5D.pdf
// Section 2.5.3 Status Enumerations

public enum ZCLStatus {
    SUCCESS(0,"Operation was successful"),
    FAILURE(1,"Operation was not successful"),
    
    INVALID_FIELD(133,"At least one field of the command contains an incorrect value"),//0x85
    UNSUPPORTED_ATTRIBUTE(134,"The specified attribute does not exist on the device."),//0x86
    INVALID_VALUE(135,"Out of range error, or set to a reserved value. Attribute keeps its old value."),//0x87
    READ_ONLY(136,"Attempt to write a read only attribute."),//0x88
    
    DUPLICATE_EXISTS(138,"An attempt to create an entry in a table failed due to a duplicate entry already being present in the table.");
   
    private int value;
    private String description;
    
    private ZCLStatus(int value, String description) {
        this.value = value;
        this.description = description;
    }
    
    public static ZCLStatus getByValue(int value) {
        for (ZCLStatus status : values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("ZCLStatus of " + value + " is unknown.");
    }
    
    public int getValue() {
        return value;
    }
    
    public String getDescription() {
        return description;
    }
}
