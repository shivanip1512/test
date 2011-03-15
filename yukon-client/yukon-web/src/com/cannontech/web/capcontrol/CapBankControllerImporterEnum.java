package com.cannontech.web.capcontrol;

import org.apache.commons.lang.Validate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum CapBankControllerImporterEnum {
	TEMPLATE_NAME(0, "Template Name", true),
	CBC_NAME(1, "CBC Name", true),  
	CBC_TYPE(2, "CBC Type", true),
	CBC_SERIAL_NUMBER(3, "CBC Serial Number", true),
	CAPBANK_NAME(4, "Cap Bank Name", false),
	MASTER_ADDRESS(5, "Master Address", true),
	SLAVE_ADDRESS(6, "Slave Address", true),
	COMM_CHANNEL(7, "Comm Channel", true),
	SCAN_ENABLED(8, "Scan Enabled", false),
	SCAN_INTERVAL(9, "Scan Interval", false),
	ALT_INTERVAL(10, "Alt Interval", false);
	
	private final int columnId;
	private final String columnName;
	private final boolean required;
	
	private final static ImmutableMap<Integer, CapBankControllerImporterEnum> lookupById;
	
	static {
        try {
            Builder<Integer, CapBankControllerImporterEnum> idBuilder = ImmutableMap.builder();
            for (CapBankControllerImporterEnum column : values()) {
                idBuilder.put(column.getColumnId(), column);
            }
            lookupById = idBuilder.build();
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }
	
	private CapBankControllerImporterEnum(int columnId, String columnName, boolean required) {
		this.columnId = columnId;
		this.columnName = columnName;
		this.required = required;
	}
	
	public static CapBankControllerImporterEnum getColumnById(int columnId) throws IllegalArgumentException {
		CapBankControllerImporterEnum column = lookupById.get(columnId);
		Validate.notNull(column, Integer.toString(columnId));
		return column;
	}
	
	public int getColumnId() {
		return columnId;
	}
	
	public String getColumnName() {
		return columnName;
	}
	
	public boolean isRequired() {
		return required;
	}
	
	public static int numColumns() {
		return lookupById.size();
	}
	
	@Override
	public String toString() {
		return columnName;
	}
}