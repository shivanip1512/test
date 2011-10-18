package com.cannontech.capcontrol.creation.model;

public class HierarchyImportResult {
	private HierarchyImportData hierarchyImportData;
	private HierarchyImportResultType resultType;
	
	public HierarchyImportResult(HierarchyImportData hierarchyImportData, HierarchyImportResultType resultType) {
		this.hierarchyImportData = hierarchyImportData;
		this.resultType = resultType;
	}
	
	public HierarchyImportData getHierarchyImportData() {
		return hierarchyImportData;
	}
	
	public HierarchyImportResultType getResultType() {
		return resultType;
	}
}
