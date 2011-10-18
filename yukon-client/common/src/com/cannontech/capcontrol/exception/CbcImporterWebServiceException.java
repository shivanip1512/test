package com.cannontech.capcontrol.exception;

import com.cannontech.capcontrol.creation.model.CbcImportResultType;

public class CbcImporterWebServiceException extends CapControlImportException {

	private CbcImportResultType resultType;
	
	public CbcImporterWebServiceException(CbcImportResultType resultType) {
		super("Cap Control web service failed a CBC import for the following reason: " + resultType.getDbString());
		this.resultType = resultType;
	}
	
	public CbcImportResultType getResultType() {
		return resultType;
	}
}
