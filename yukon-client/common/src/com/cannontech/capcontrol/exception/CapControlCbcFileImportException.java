package com.cannontech.capcontrol.exception;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import com.cannontech.capcontrol.creation.CapControlImporterCbcCsvField;

public class CapControlCbcFileImportException extends CapControlFileImporterException {
	
	private Collection<CapControlImporterCbcCsvField> columns;

	private CapControlCbcFileImportException(String message) {
		super(message);
	}
	
	public CapControlCbcFileImportException(String message, Collection<CapControlImporterCbcCsvField> columns) {
		this(message + StringUtils.join(columns, ", "));
		this.columns = columns;
	}
	
	public Collection<CapControlImporterCbcCsvField> getColumns() {
		return columns;
	}
}
