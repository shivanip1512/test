package com.cannontech.capcontrol.creation.model;

public class CbcImportResult {
	private CbcImportData cbcImportData;
	private CbcImportResultType resultType;

	public CbcImportResult(CbcImportData cbcImportData, CbcImportResultType resultType) {
		this.cbcImportData = cbcImportData;
		this.resultType = resultType;
	}
	
	public CbcImportData getCbcImportData() {
		return cbcImportData;
	}
	
	public CbcImportResultType getResultType() {
		return resultType;
	}
}
