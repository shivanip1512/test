package com.cannontech.capcontrol.creation;

import org.apache.commons.lang.Validate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public enum CapControlImporterCbcCsvField {
	TEMPLATE_NAME("Template Name", false),
	CBC_NAME("CBC Name", true),  
	CBC_TYPE("CBC Type", true),
	CBC_SERIAL_NUMBER("CBC Serial Number", true),
	CAPBANK_NAME("Cap Bank Name", false),
	MASTER_ADDRESS("Master Address", true),
	SLAVE_ADDRESS("Slave Address", true),
	COMM_CHANNEL("Comm Channel", true),
	SCAN_INTERVAL("Scan Interval", false),
	ALT_INTERVAL("Alt Interval", false),
	IMPORT_ACTION("Import Action", true),
	;
	
	private final String columnName;
	private final boolean required;

	private final static ImmutableMap<String, CapControlImporterCbcCsvField> lookupByString;
	
	private final static ImmutableSet<CapControlImporterCbcCsvField> requiredFields;
	private final static ImmutableSet<CapControlImporterCbcCsvField> nonRequiredFields;
	
	static {
        try {
            ImmutableMap.Builder<String, CapControlImporterCbcCsvField> stringBuilder = ImmutableMap.builder();
            ImmutableSet.Builder<CapControlImporterCbcCsvField> requiredFieldsBuilder = ImmutableSet.builder();
            ImmutableSet.Builder<CapControlImporterCbcCsvField> nonRequiredFieldsBuilder = ImmutableSet.builder();
            for (CapControlImporterCbcCsvField column : values()) {
                stringBuilder.put(column.getColumnName(), column);
                if(column.isRequired()) {
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
	
	private CapControlImporterCbcCsvField(String columnName, boolean required) {
		this.columnName = columnName;
		this.required = required;
	}
	
	public static CapControlImporterCbcCsvField getColumnByName(String columnName) throws IllegalArgumentException {
		CapControlImporterCbcCsvField column = lookupByString.get(columnName);
		Validate.notNull(column, columnName);
		return column;
	}
	
	public static ImmutableSet<CapControlImporterCbcCsvField> getRequiredFields() {
		return requiredFields;
	}
	
	public static ImmutableSet<CapControlImporterCbcCsvField> getNonRequiredFields() {
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