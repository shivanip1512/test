package com.cannontech.capcontrol.exception;

import com.cannontech.capcontrol.creation.model.HierarchyImportResultType;

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
