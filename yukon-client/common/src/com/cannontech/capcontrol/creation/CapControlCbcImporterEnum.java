package com.cannontech.capcontrol.creation;

import org.apache.commons.lang.Validate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public enum CapControlCbcImporterEnum {
	TEMPLATE_NAME("Template Name", false),
	CBC_NAME("CBC Name", true),  
	CBC_TYPE("CBC Type", true),
	CBC_SERIAL_NUMBER("CBC Serial Number", true),
	CAPBANK_NAME("Cap Bank Name", false),
	MASTER_ADDRESS("Master Address", true),
	SLAVE_ADDRESS("Slave Address", true),
	COMM_CHANNEL("Comm Channel", true),
	SCAN_ENABLED("Scan Enabled", false),
	SCAN_INTERVAL("Scan Interval", false),
	ALT_INTERVAL("Alt Interval", false),
	IMPORT_ACTION("Import Action", true),
	;
	
	private final String columnName;
	private final boolean required;

	private final static ImmutableMap<String, CapControlCbcImporterEnum> lookupByString;
	
	private final static ImmutableSet<CapControlCbcImporterEnum> requiredFields;
	
	static {
        try {
            ImmutableMap.Builder<String, CapControlCbcImporterEnum> stringBuilder = ImmutableMap.builder();
            ImmutableSet.Builder<CapControlCbcImporterEnum> requiredFieldsBuilder = ImmutableSet.builder();
            for (CapControlCbcImporterEnum column : values()) {
                stringBuilder.put(column.getColumnName(), column);
                if(column.isRequired()) {
                	requiredFieldsBuilder.add(column);
                }
            }
            lookupByString = stringBuilder.build();
            requiredFields = requiredFieldsBuilder.build();
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }
	
	private CapControlCbcImporterEnum(String columnName, boolean required) {
		this.columnName = columnName;
		this.required = required;
	}
	
	public static CapControlCbcImporterEnum getColumnByName(String columnName) throws IllegalArgumentException {
		CapControlCbcImporterEnum column = lookupByString.get(columnName);
		Validate.notNull(column, columnName);
		return column;
	}
	
	public static ImmutableSet<CapControlCbcImporterEnum> getRequiredFields() {
		return requiredFields;
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