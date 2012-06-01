package com.cannontech.common.exception;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

/**
 * Exception thrown during .csv file validation when an invalid column is present.
 */
public class InvalidColumnNameException extends ImportFileFormatException {
    private Collection<String> names;
    
    public InvalidColumnNameException(Collection<String> names) {
        this.names = names;
    }
    
    public Collection<String> getInvalidColumnNames() {
        return names;
    }
    
    public String getJoinedInvalidColumnNames() {
        return StringUtils.join(names, ", ");
    }
}
