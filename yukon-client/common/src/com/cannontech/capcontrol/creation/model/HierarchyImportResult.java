package com.cannontech.capcontrol.creation.model;

public class HierarchyImportResult {
	private HierarchyImportData hierarchyImportData;
	private HierarchyImportResultTypesEnum resultType;
	
	public HierarchyImportResult(HierarchyImportData hierarchyImportData, HierarchyImportResultTypesEnum resultType) {
		this.hierarchyImportData = hierarchyImportData;
		this.resultType = resultType;
	}
	
	public HierarchyImportData getHierarchyImportData() {
		return hierarchyImportData;
	}
	
	public HierarchyImportResultTypesEnum getResultType() {
		return resultType;
	}
}
