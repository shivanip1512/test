package com.cannontech.capcontrol.exception;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cannontech.capcontrol.creation.CapControlHierarchyImporterEnum;

@SuppressWarnings("serial")
public class CapControlHierarchyFileImporterException extends CapControlFileImporterException {

	private List<CapControlHierarchyImporterEnum> columns;
	
	private CapControlHierarchyFileImporterException(String message) {
		super(message);
	}
	
	public CapControlHierarchyFileImporterException(String message, List<CapControlHierarchyImporterEnum> columns) {
		this(message + StringUtils.join(columns, ", "));
		this.columns = columns;
	}
	
	public List<CapControlHierarchyImporterEnum> getColumns() {
		return columns;
	}
}
