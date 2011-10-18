package com.cannontech.capcontrol.exception;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import com.cannontech.capcontrol.creation.CapControlImporterHierarchyCsvField;

@SuppressWarnings("serial")
public class CapControlHierarchyFileImporterException extends CapControlFileImporterException {

	private Collection<CapControlImporterHierarchyCsvField> columns;
	
	private CapControlHierarchyFileImporterException(String message) {
		super(message);
	}
	
	public CapControlHierarchyFileImporterException(String message, Collection<CapControlImporterHierarchyCsvField> columns) {
		this(message + StringUtils.join(columns, ", "));
		this.columns = columns;
	}
	
	public Collection<CapControlImporterHierarchyCsvField> getColumns() {
		return columns;
	}
}
