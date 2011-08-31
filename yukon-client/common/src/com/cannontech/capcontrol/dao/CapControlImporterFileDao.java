package com.cannontech.capcontrol.dao;

import java.io.InputStream;
import java.util.List;

import com.cannontech.capcontrol.creation.model.CbcImportData;
import com.cannontech.capcontrol.creation.model.CbcImportResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportData;
import com.cannontech.capcontrol.creation.model.HierarchyImportResult;

public interface CapControlImporterFileDao {
		
	public List<CbcImportData> getCbcImportData(InputStream inputStream, List<CbcImportResult> results);
		
	public List<HierarchyImportData> getHierarchyImportData(InputStream inputStream, List<HierarchyImportResult> results);

}