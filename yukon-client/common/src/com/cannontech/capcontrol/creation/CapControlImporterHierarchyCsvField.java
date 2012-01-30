package com.cannontech.capcontrol.creation;

import org.apache.commons.lang.Validate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public enum CapControlImporterHierarchyCsvField {
	TYPE("Type", true),
	NAME("Name", true),
	PARENT("Parent", false),
	DESCRIPTION("Description", false),
	DISABLED("Disabled", false),
	MAP_LOCATION_ID("MapLocationId", false),
	IMPORT_ACTION("Import Action", true),
	OPERATIONAL_STATE("Operational State", false),
	CAPBANK_SIZE("Capbank Size", false),
	;
	
	private final String columnName;
	private final boolean required;
	
	private final static ImmutableMap<String, CapControlImporterHierarchyCsvField> lookupByString;
	private final static ImmutableSet<CapControlImporterHierarchyCsvField> requiredFields;
	private final static ImmutableSet<CapControlImporterHierarchyCsvField> nonRequiredFields;
	
	static {
        try {
        	ImmutableMap.Builder<String, CapControlImporterHierarchyCsvField> stringBuilder = ImmutableMap.builder();
            ImmutableSet.Builder<CapControlImporterHierarchyCsvField> requiredFieldsBuilder = ImmutableSet.builder();
            ImmutableSet.Builder<CapControlImporterHierarchyCsvField> nonRequiredFieldsBuilder = ImmutableSet.builder();
        	for (CapControlImporterHierarchyCsvField column : values()) {
            	stringBuilder.put(column.getColumnName(), column);
            	if (column.isRequired()) {
            		requiredFieldsBuilder.add(column);
            	} else {
            	    nonRequiredFieldsBuilder.add(column);
            	}
            }
            lookupByString = stringBuilder.build();
            requiredFields = requiredFieldsBuilder.build();
            nonRequiredFields = nonRequiredFieldsBuilder.build();
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }
	
	private CapControlImporterHierarchyCsvField(String columnName, boolean required) {
		this.columnName = columnName;
		this.required = required;
	}
	
	public static CapControlImporterHierarchyCsvField getColumnByName(String columnName) throws IllegalArgumentException {
		CapControlImporterHierarchyCsvField column = lookupByString.get(columnName);
		Validate.notNull(column, columnName);
		return column;
	}
	
	public static ImmutableSet<CapControlImporterHierarchyCsvField> getRequiredFields() {
		return requiredFields;
	}
	
	public static ImmutableSet<CapControlImporterHierarchyCsvField> getNonRequiredFields() {
	    return nonRequiredFields;
	}
	
	public String getColumnName() {
		return columnName;
	}
	
	public boolean isRequired() {
		return required;
	}
	
	public static int numColumns() {
		return lookupByString.size();
	}
	
	@Override
	public String toString() {
		return columnName;
	}
}
