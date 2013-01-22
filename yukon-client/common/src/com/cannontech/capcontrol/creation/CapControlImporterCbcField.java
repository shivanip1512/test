package com.cannontech.capcontrol.creation;

import java.util.List;

import org.apache.commons.lang.Validate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public enum CapControlImporterCbcField {
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

	private final static ImmutableMap<String, CapControlImporterCbcField> lookupByString;
	
	private final static ImmutableSet<CapControlImporterCbcField> requiredFields;
	private final static ImmutableSet<CapControlImporterCbcField> nonRequiredFields;
	
	static {
        try {
            ImmutableMap.Builder<String, CapControlImporterCbcField> stringBuilder = ImmutableMap.builder();
            ImmutableSet.Builder<CapControlImporterCbcField> requiredFieldsBuilder = ImmutableSet.builder();
            ImmutableSet.Builder<CapControlImporterCbcField> nonRequiredFieldsBuilder = ImmutableSet.builder();
            for (CapControlImporterCbcField column : values()) {
                stringBuilder.put(column.getColumnName().toLowerCase(), column);
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
	
	private CapControlImporterCbcField(String columnName, boolean required) {
		this.columnName = columnName;
		this.required = required;
	}
	
	public static CapControlImporterCbcField getColumnByName(String columnName) throws IllegalArgumentException {
		CapControlImporterCbcField column = lookupByString.get(columnName.toLowerCase());
		Validate.notNull(column, columnName);
		return column;
	}
	
	public static ImmutableSet<CapControlImporterCbcField> getRequiredFields() {
		return requiredFields;
	}
	
    public static List<String> getRequiredFieldNames() {
        List<String> requiredFieldNames = Lists.newArrayList();
        for(CapControlImporterCbcField field : requiredFields){
            requiredFieldNames.add(field.getColumnName());
        }
        return requiredFieldNames;
    }

	public static ImmutableSet<CapControlImporterCbcField> getNonRequiredFields() {
	    return nonRequiredFields;
	}
	
    public static List<String> getOptionalFieldNames() {
        List<String> optionalFieldNames = Lists.newArrayList();
        for(CapControlImporterCbcField field : nonRequiredFields){
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
	
	public static int numColumns() {
		return lookupByString.size();
	}
	
	@Override
	public String toString() {
		return columnName;
	}
}