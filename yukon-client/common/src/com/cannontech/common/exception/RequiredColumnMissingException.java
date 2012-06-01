package com.cannontech.common.exception;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

/**
 * Exception thrown during .csv file validation when a required column is missing.
 */
public class RequiredColumnMissingException extends ImportFileFormatException {
    private Collection<String> names;
    
    public RequiredColumnMissingException(Collection<String> names) {
        this.names = names;
    }
    
    public Collection<String> getMissingColumnNames() {
        return names;
    }
    
    public String getJoinedMissingColumnNames() {
        return StringUtils.join(names, ", ");
    }
}
