package com.cannontech.common.csvImport;

import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Sets;

/**
 * Represents a special type of import file column. A value-dependent column must be present if 
 * the depended-upon column contains the depended-upon value. Otherwise, it is optional.
 */
public class ImportValueDependentColumnDefinition extends ImportColumnDefinition {
    private final ImportColumnDefinition dependedUponColumn;
    private final Set<String> dependedUponValues;
    
    public ImportValueDependentColumnDefinition(String name, Class<?> typeClass, boolean nullable, ImportColumnDefinition dependedUponColumn, Collection<String> dependedUponValues) {
        super(name, typeClass, nullable, false);
        this.dependedUponColumn = dependedUponColumn;
        this.dependedUponValues = Sets.newHashSet(dependedUponValues);
    }
    
    public ImportColumnDefinition getDependedUponColumn() {
        return dependedUponColumn;
    }
    
    public Set<String> getDependedUponValue() {
        return dependedUponValues;
    }
    
    public String getDependedUponValueString() {
        return StringUtils.join(dependedUponValues.toArray(), ", ");
    }
}
