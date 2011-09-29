package com.cannontech.capcontrol.creation.service;

import java.util.List;

import com.cannontech.capcontrol.creation.model.CbcImportData;
import com.cannontech.capcontrol.creation.model.CbcImportResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportData;
import com.cannontech.capcontrol.creation.model.HierarchyImportResult;

public interface CapControlImportService {

    /**
     * Uses the PaoCreationService to create a CBC object. 
     * @param cbcImportData the import data specified by the user that will be used
     * to create the CBC.
     * @param results a list of results for which the outcome of the import of the 
     * current CBC will be added.
     */
	public void createCbc(CbcImportData cbcImportData, List<CbcImportResult> results);
	
	public void createCbcFromTemplate(CbcImportData cbcImportData, List<CbcImportResult> results);
	
	public void updateCbc(CbcImportData cbcImportData, List<CbcImportResult> results);
	
	public void removeCbc(CbcImportData cbcImportData, List<CbcImportResult> results);
	
	public void createHierarchyObject(HierarchyImportData hierarchyImportData, List<HierarchyImportResult> results);
	
	public void updateHierarchyObject(HierarchyImportData hierarchyImportData, List<HierarchyImportResult> results);
	
	public void removeHierarchyObject(HierarchyImportData hierarchyImportData, List<HierarchyImportResult> results);
}
