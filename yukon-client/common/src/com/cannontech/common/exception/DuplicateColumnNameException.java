package com.cannontech.common.exception;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

/**
 * Exception thrown during file validation when a duplicate column is present.
 */
public class DuplicateColumnNameException extends ImportFileFormatException {
    private Collection<String> names;
    
    public DuplicateColumnNameException(Collection<String> names) {
        this.names = names;
    }
    
    public Collection<String> getDuplicateColumnNames() {
        return names;
    }
    
    public String getJoinedDuplicateColumnNames() {
        return StringUtils.join(names, ", ");
    }
}
