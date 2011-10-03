package com.cannontech.capcontrol.exception;

import com.cannontech.capcontrol.creation.model.HierarchyImportResultTypesEnum;

public class HierarchyImporterWebServiceException extends CapControlImportException{

	private HierarchyImportResultTypesEnum resultType;
	
	public HierarchyImporterWebServiceException(HierarchyImportResultTypesEnum resultType) {
		super("Cap Control web service failed a hierarchy object import for the following reason: " + 
				resultType.getDbString());
		this.resultType = resultType;
	}
	
	public HierarchyImportResultTypesEnum getResultType() {
		return resultType;
	}
}
