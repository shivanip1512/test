package com.cannontech.web.capcontrol;

import java.util.List;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("serial")
public class CapControlImporterException extends Exception {
	
	private CapControlImporterException(String message) {
		super(message);
	}
	
	public CapControlImporterException(String message, List<CapBankControllerImporterEnum> columns) {
		this(message + StringUtils.join(columns, ", "));
	}

}