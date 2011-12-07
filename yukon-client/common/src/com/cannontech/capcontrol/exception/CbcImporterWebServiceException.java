package com.cannontech.capcontrol.exception;

import com.cannontech.capcontrol.creation.model.CbcImportResultType;

/**
 * This exception is thrown in the Cap Control web service import for CBC imports
 * that have an unsuccessful outcome. It is thrown when the user submits a request
 * with invalid/unsupported data in the web service request (i.e. invalid CBC type
 * or import action.)
 */
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
