package com.cannontech.capcontrol.creation.service;

import java.util.List;

import com.cannontech.capcontrol.creation.model.CbcImportData;
import com.cannontech.capcontrol.creation.model.CbcImportResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportData;
import com.cannontech.capcontrol.creation.model.HierarchyImportResult;

public interface CapControlImportService {

	public void createCbc(CbcImportData cbcImportData, List<CbcImportResult> results);
	
	public void createCbcFromTemplate(CbcImportData cbcImportData, List<CbcImportResult> results);
	
	public void updateCbc(CbcImportData cbcImportData, List<CbcImportResult> results);
	
	public void removeCbc(CbcImportData cbcImportData, List<CbcImportResult> results);
	
	public void createHierarchyObject(HierarchyImportData hierarchyImportData, List<HierarchyImportResult> results);
	
	public void updateHierarchyObject(HierarchyImportData hierarchyImportData, List<HierarchyImportResult> results);
	
	public void removeHierarchyObject(HierarchyImportData hierarchyImportData, List<HierarchyImportResult> results);
}
