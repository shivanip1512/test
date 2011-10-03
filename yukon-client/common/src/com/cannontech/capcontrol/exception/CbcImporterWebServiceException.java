package com.cannontech.capcontrol.exception;

import com.cannontech.capcontrol.creation.model.CbcImportResultTypesEnum;

public class CbcImporterWebServiceException extends CapControlImportException {

	private CbcImportResultTypesEnum resultType;
	
	public CbcImporterWebServiceException(CbcImportResultTypesEnum resultType) {
		super("Cap Control web service failed a CBC import for the following reason: " + resultType.getDbString());
		this.resultType = resultType;
	}
	
	public CbcImportResultTypesEnum getResultType() {
		return resultType;
	}
}
