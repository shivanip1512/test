package com.cannontech.capcontrol.exception;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import com.cannontech.capcontrol.creation.CapControlHierarchyImporterEnum;

@SuppressWarnings("serial")
public class CapControlHierarchyFileImporterException extends CapControlFileImporterException {

	private Collection<CapControlHierarchyImporterEnum> columns;
	
	private CapControlHierarchyFileImporterException(String message) {
		super(message);
	}
	
	public CapControlHierarchyFileImporterException(String message, Collection<CapControlHierarchyImporterEnum> columns) {
		this(message + StringUtils.join(columns, ", "));
		this.columns = columns;
	}
	
	public Collection<CapControlHierarchyImporterEnum> getColumns() {
		return columns;
	}
}
