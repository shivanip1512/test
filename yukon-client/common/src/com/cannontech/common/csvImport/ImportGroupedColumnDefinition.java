package com.cannontech.common.csvImport;

/**
 * Represents a special type of optional import file column. If any of the columns in a given
 * group are present, they must all be present.
 */
public class ImportGroupedColumnDefinition extends ImportColumnDefinition{
    private final String groupName;
    
    public ImportGroupedColumnDefinition(String name, Class<?> typeClass, String groupName, boolean nullable, boolean uppercaseValue) {
        super(name, typeClass, false, nullable, uppercaseValue);
        this.groupName = groupName;
    }
    
    public String getGroupName() {
        return groupName;
    }
}
