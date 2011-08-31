package com.cannontech.capcontrol.creation.model;

public class CbcImportResult {
	private CbcImportData cbcImportData;
	private CbcImportResultTypesEnum resultType;

	public CbcImportResult(CbcImportData cbcImportData, CbcImportResultTypesEnum resultType) {
		this.cbcImportData = cbcImportData;
		this.resultType = resultType;
	}
	
	public CbcImportData getCbcImportData() {
		return cbcImportData;
	}
	
	public CbcImportResultTypesEnum getResultType() {
		return resultType;
	}
}
