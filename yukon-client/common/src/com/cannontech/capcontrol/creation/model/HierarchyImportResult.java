package com.cannontech.capcontrol.creation.model;

import java.util.Comparator;

public class HierarchyImportResult {
	private HierarchyImportData hierarchyImportData;
	private HierarchyImportResultTypesEnum resultType;
	
	public static final Comparator<HierarchyImportResult> HIERARCHY_SUCCESS_COMPARATOR = new Comparator<HierarchyImportResult>() {
		@Override
		public int compare(HierarchyImportResult o1, HierarchyImportResult o2) {
			HierarchyImportResultTypesEnum e1 = o1.getResultType();
			HierarchyImportResultTypesEnum e2 = o2.getResultType();
			if (e1 == HierarchyImportResultTypesEnum.SUCCESS && e2 != HierarchyImportResultTypesEnum.SUCCESS) {
				return -1;
			} else if (e1 != HierarchyImportResultTypesEnum.SUCCESS && e2 == HierarchyImportResultTypesEnum.SUCCESS) {
				return 1;
			} else {
				return 0;
			}
		}
	};
	
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
