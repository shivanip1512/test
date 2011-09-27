package com.cannontech.capcontrol.exception;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import com.cannontech.capcontrol.creation.CapControlCbcImporterEnum;

@SuppressWarnings("serial")
public class CapControlCbcFileImportException extends CapControlFileImporterException {
	
	private Collection<CapControlCbcImporterEnum> columns;

	private CapControlCbcFileImportException(String message) {
		super(message);
	}
	
	public CapControlCbcFileImportException(String message, Collection<CapControlCbcImporterEnum> columns) {
		this(message + StringUtils.join(columns, ", "));
		this.columns = columns;
	}
	
	public Collection<CapControlCbcImporterEnum> getColumns() {
		return columns;
	}
}
