package com.cannontech.capcontrol.exception;

import com.cannontech.capcontrol.creation.model.HierarchyImportResultType;

/**
 * This exception is thrown in the Cap Control web service import for hierarchy imports
 * that have an unsuccessful outcome. It is thrown when the user submits a request
 * with invalid/unsupported data in the web service request (i.e. invalid cap control type
 * or import action.)
 */
public class HierarchyImporterWebServiceException extends CapControlImportException{

	private HierarchyImportResultType resultType;
	
	public HierarchyImporterWebServiceException(HierarchyImportResultType resultType) {
		super("Cap Control web service failed a hierarchy object import for the following reason: " + 
				resultType.getDbString());
		this.resultType = resultType;
	}
	
	public HierarchyImportResultType getResultType() {
		return resultType;
	}
}
