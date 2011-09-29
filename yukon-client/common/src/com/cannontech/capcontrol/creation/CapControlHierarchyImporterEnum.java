package com.cannontech.capcontrol.creation;

import org.apache.commons.lang.Validate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public enum CapControlHierarchyImporterEnum {
	TYPE("Type", true),
	NAME("Name", true),
	PARENT("Parent", false),
	DESCRIPTION("Description", false),
	DISABLED("Disabled", false),
	MAP_LOCATION_ID("MapLocationId", false),
	IMPORT_ACTION("Import Action", true),
	;
	
	private final String columnName;
	private final boolean required;
	
	private final static ImmutableMap<String, CapControlHierarchyImporterEnum> lookupByString;
	private final static ImmutableSet<CapControlHierarchyImporterEnum> requiredFields;
	private final static ImmutableSet<CapControlHierarchyImporterEnum> nonRequiredFields;
	
	static {
        try {
        	ImmutableMap.Builder<String, CapControlHierarchyImporterEnum> stringBuilder = ImmutableMap.builder();
            ImmutableSet.Builder<CapControlHierarchyImporterEnum> requiredFieldsBuilder = ImmutableSet.builder();
            ImmutableSet.Builder<CapControlHierarchyImporterEnum> nonRequiredFieldsBuilder = ImmutableSet.builder();
        	for (CapControlHierarchyImporterEnum column : values()) {
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
	
	private CapControlHierarchyImporterEnum(String columnName, boolean required) {
		this.columnName = columnName;
		this.required = required;
	}
	
	public static CapControlHierarchyImporterEnum getColumnByName(String columnName) throws IllegalArgumentException {
		CapControlHierarchyImporterEnum column = lookupByString.get(columnName);
		Validate.notNull(column, columnName);
		return column;
	}
	
	public static ImmutableSet<CapControlHierarchyImporterEnum> getRequiredFields() {
		return requiredFields;
	}
	
	public static ImmutableSet<CapControlHierarchyImporterEnum> getNonRequiredFields() {
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
