package com.cannontech.capcontrol.exception;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cannontech.capcontrol.creation.CapControlCbcImporterEnum;

@SuppressWarnings("serial")
public class CapControlCbcFileImportException extends CapControlFileImporterException {
	
	private List<CapControlCbcImporterEnum> columns;

	private CapControlCbcFileImportException(String message) {
		super(message);
	}
	
	public CapControlCbcFileImportException(String message, List<CapControlCbcImporterEnum> columns) {
		this(message + StringUtils.join(columns, ", "));
		this.columns = columns;
	}
	
	public List<CapControlCbcImporterEnum> getColumns() {
		return columns;
	}
}
