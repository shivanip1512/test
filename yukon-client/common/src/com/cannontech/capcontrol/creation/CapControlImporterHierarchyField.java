package com.cannontech.capcontrol.creation;

import java.util.List;

import org.apache.commons.lang.Validate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public enum CapControlImporterHierarchyField {
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
	
	private final static ImmutableMap<String, CapControlImporterHierarchyField> lookupByString;
	private final static ImmutableSet<CapControlImporterHierarchyField> requiredFields;
	private final static ImmutableSet<CapControlImporterHierarchyField> nonRequiredFields;
	
	static {
        try {
        	ImmutableMap.Builder<String, CapControlImporterHierarchyField> stringBuilder = ImmutableMap.builder();
            ImmutableSet.Builder<CapControlImporterHierarchyField> requiredFieldsBuilder = ImmutableSet.builder();
            ImmutableSet.Builder<CapControlImporterHierarchyField> nonRequiredFieldsBuilder = ImmutableSet.builder();
        	for (CapControlImporterHierarchyField column : values()) {
            	stringBuilder.put(column.getColumnName().toLowerCase(), column);
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
	
	private CapControlImporterHierarchyField(String columnName, boolean required) {
		this.columnName = columnName;
		this.required = required;
	}
	
	public static CapControlImporterHierarchyField getColumnByName(String columnName) throws IllegalArgumentException {
		CapControlImporterHierarchyField column = lookupByString.get(columnName.toLowerCase());
		Validate.notNull(column, columnName);
		return column;
	}
	
	public static ImmutableSet<CapControlImporterHierarchyField> getRequiredFields() {
		return requiredFields;
	}
	
    public static List<String> getRequiredFieldNames() {
        List<String> requiredFieldNames = Lists.newArrayList();
        for(CapControlImporterHierarchyField field : requiredFields){
            requiredFieldNames.add(field.getColumnName());
        }
        return requiredFieldNames;
    }
	
	public static ImmutableSet<CapControlImporterHierarchyField> getNonRequiredFields() {
	    return nonRequiredFields;
	}
	
    public static List<String> getOptionalFieldNames() {
        List<String> optionalFieldNames = Lists.newArrayList();
        for(CapControlImporterHierarchyField field : nonRequiredFields){
            optionalFieldNames.add(field.getColumnName());
        }
        return optionalFieldNames;
    }
	
	public String getColumnName() {
		return columnName;
	}
	
	public boolean isRequired() {
		return required;
	}
	
	@Override
	public String toString() {
		return columnName;
	}
}
