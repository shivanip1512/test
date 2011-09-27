package com.cannontech.capcontrol.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.capcontrol.creation.CapControlCbcImporterEnum;
import com.cannontech.capcontrol.creation.CapControlHierarchyImporterEnum;
import com.cannontech.capcontrol.creation.model.CbcImportData;
import com.cannontech.capcontrol.creation.model.CbcImportResult;
import com.cannontech.capcontrol.creation.model.CbcImportResultTypesEnum;
import com.cannontech.capcontrol.creation.model.HierarchyImportData;
import com.cannontech.capcontrol.creation.model.HierarchyImportResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportResultTypesEnum;
import com.cannontech.capcontrol.creation.model.ImportActionsEnum;
import com.cannontech.capcontrol.dao.CapControlImporterFileDao;
import com.cannontech.capcontrol.exception.CapControlCbcFileImportException;
import com.cannontech.capcontrol.exception.CapControlFileImporterException;
import com.cannontech.capcontrol.exception.CapControlHierarchyFileImporterException;
import com.cannontech.capcontrol.exception.CapControlImportException;
import com.cannontech.capcontrol.model.CapbankController;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.tools.csv.CSVReader;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class CapControlImporterFileDaoImpl implements CapControlImporterFileDao {
	
	private Map<CapControlCbcImporterEnum, Integer> getCbcHeaderRowMap(final String[] headerRow) {
		Map<CapControlCbcImporterEnum, Integer> headerColumnMap = Maps.newHashMap();
		
		for (int i = 0; i < headerRow.length; i++) {
			CapControlCbcImporterEnum column = CapControlCbcImporterEnum.getColumnByName(headerRow[i]);
			headerColumnMap.put(column, i);
		}
		
		return headerColumnMap;
	}
	
	private Map<CapControlHierarchyImporterEnum, Integer> getHierarchyHeaderRowMap(final String[] headerRow) {
		Map<CapControlHierarchyImporterEnum, Integer> headerColumnMap = Maps.newHashMap();
		
		for (int i = 0; i < headerRow.length; i++) {
			CapControlHierarchyImporterEnum column = CapControlHierarchyImporterEnum.getColumnByName(headerRow[i]);
			headerColumnMap.put(column, i);
		}
		
		return headerColumnMap;
	}
	
	private void validateCbcImportFileColumns(Map<CapControlCbcImporterEnum, Integer> headerColumnMap) {
		// Make sure we have all required columns in the file!
		Set<CapControlCbcImporterEnum> requiredFields = CapControlCbcImporterEnum.getRequiredFields();
		Set<CapControlCbcImporterEnum> fileColumns = headerColumnMap.keySet();
		
		SetView<CapControlCbcImporterEnum> missing = Sets.difference(fileColumns, requiredFields);
		
		if (missing.size() > 0) {
			throw new CapControlCbcFileImportException("Cbc Import File is missing required columns: ", missing);
		}
	}
	
	private void validateHierarchyImportFileColumns(Map<CapControlHierarchyImporterEnum, Integer> headerColumnMap) {
		// Make sure we have all required columns in the file!
		Set<CapControlHierarchyImporterEnum> requiredFields = CapControlHierarchyImporterEnum.getRequiredFields();
		
		Set<CapControlHierarchyImporterEnum> fileColumns = headerColumnMap.keySet();
		SetView<CapControlHierarchyImporterEnum> missingColumns = Sets.difference(fileColumns, requiredFields);
		
		if (missingColumns.size() > 0) {
			throw new CapControlHierarchyFileImporterException("Hierarchy import file is missing required columns: ", missingColumns);
		}
	}
	
	private CbcImportData createCbcImportData(final String[] line, Map<CapControlCbcImporterEnum, Integer> headerColumnMap) {
		CbcImportData cbcData = new CbcImportData();
		
		// Template name may or may not be there. Check.
		if (headerColumnMap.containsKey(CapControlCbcImporterEnum.TEMPLATE_NAME)) {
			cbcData.setTemplateName(line[headerColumnMap.get(CapControlCbcImporterEnum.TEMPLATE_NAME)]);
		}
		
		cbcData.setCbcName(line[headerColumnMap.get(CapControlCbcImporterEnum.CBC_NAME)]);

		cbcData.setImportAction(ImportActionsEnum.getForDbString(line[headerColumnMap.get(CapControlCbcImporterEnum.IMPORT_ACTION)]));

		if (cbcData.getImportAction() != ImportActionsEnum.REMOVE) {
			String type = line[headerColumnMap.get(CapControlCbcImporterEnum.CBC_TYPE)];
			try {
				PaoType paoType = PaoType.getForDbString(type);
				cbcData.setCbcType(paoType);
			} catch (IllegalArgumentException e) {
				throw new CapControlImportException("Import of " + cbcData.getCbcName() + " failed. Unknown Type: " + type);
			}
			
			if (CapbankController.isValidCbc(cbcData.getCbcType())) {
				// There are required fields we KNOW are here. Set them, then try the non-requireds.
				cbcData.setCbcSerialNumber(Integer.decode(line[headerColumnMap.get(CapControlCbcImporterEnum.CBC_SERIAL_NUMBER)]));
				cbcData.setMasterAddress(Integer.decode(line[headerColumnMap.get(CapControlCbcImporterEnum.MASTER_ADDRESS)]));
				cbcData.setSlaveAddress(Integer.decode(line[headerColumnMap.get(CapControlCbcImporterEnum.SLAVE_ADDRESS)]));
				cbcData.setCommChannel(line[headerColumnMap.get(CapControlCbcImporterEnum.COMM_CHANNEL)]);
				
				Integer capBankColumn = headerColumnMap.get(CapControlCbcImporterEnum.CAPBANK_NAME);
				if (capBankColumn != null && !line[capBankColumn].isEmpty()) {
					cbcData.setCapBankName(line[capBankColumn]);
				}
				
				Integer scanColumn = headerColumnMap.get(CapControlCbcImporterEnum.SCAN_INTERVAL);
				if (scanColumn != null && !line[scanColumn].isEmpty()) {
					cbcData.setScanInterval(Integer.decode(line[scanColumn]));
				}
				
				Integer altColumn = headerColumnMap.get(CapControlCbcImporterEnum.ALT_INTERVAL);
				if (altColumn != null && !line[altColumn].isEmpty()) {
					cbcData.setAltInterval(Integer.decode(line[altColumn]));
				}
				
				Integer scanEnabledColumn = headerColumnMap.get(CapControlCbcImporterEnum.SCAN_ENABLED);
				if (scanEnabledColumn != null && !line[scanEnabledColumn].isEmpty()) {
					cbcData.setScanEnabled(line[scanEnabledColumn]);
				}
			}
		}
		
		return cbcData;
	}
	
	private HierarchyImportData createHierarchyImportData(final String[] line, Map<CapControlHierarchyImporterEnum, Integer> headerColumnMap) {
		HierarchyImportData data = new HierarchyImportData();
		
		data.setName(line[headerColumnMap.get(CapControlHierarchyImporterEnum.NAME)]);
		
		String ccType = line[headerColumnMap.get(CapControlHierarchyImporterEnum.TYPE)];	
		
		PaoType paoType = null;
		try {
			paoType = PaoType.getForDbString(ccType);
			data.setPaoType(paoType);
		} catch (IllegalArgumentException i) {
			throw new CapControlImportException("Import of " + data.getName() + " failed. Unknown Type: " + ccType);
		}
		
		Set<CapControlHierarchyImporterEnum> columns = Sets.newHashSet(headerColumnMap.keySet());
		
		// Remove the requireds from the list, we only need to check the non-requireds.
		columns.removeAll(CapControlHierarchyImporterEnum.getRequiredFields());
		
		// There are required fields we KNOW are here. Set them, then try the non-requireds.
		String action = line[headerColumnMap.get(CapControlHierarchyImporterEnum.IMPORT_ACTION)];
		data.setImportAction(ImportActionsEnum.getForDbString(action));
		
		for (CapControlHierarchyImporterEnum column : columns) {
			int columnId = headerColumnMap.get(column);
			
			switch(column) {
			case DESCRIPTION:
				data.setDescription(line[columnId]);
				break;
			case DISABLED:
				String disabled = line[columnId];
				if (disabled != null) {
					if (disabled.equalsIgnoreCase("Y")) {
						data.setDisabled(true);
					} else if (disabled.equalsIgnoreCase("N")) {
						data.setDisabled(false);
					} else {
						throw new CapControlImportException("Disabled field contained invalid data. Please " +
															"change to 'Y' or 'N'");
					}
				}
				break;
			case MAP_LOCATION_ID:
				data.setMapLocationId(line[columnId]);
				break;
			case PARENT:
				data.setParent(line[columnId]);
				break;
			}
		}
		
		return data;
	}
	
	@Override
	public List<CbcImportData> getCbcImportData(InputStream inputStream, List<CbcImportResult> results) throws CapControlImportException {
		List<CbcImportData> cbcImportData = Lists.newArrayList();
		
		try {
        	// Open up the file and remove the header row.
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            CSVReader csvReader = new CSVReader(inputStreamReader);
            final String[] headerRow = csvReader.readNext();
            
            Map<CapControlCbcImporterEnum, Integer> headerColumnMap = getCbcHeaderRowMap(headerRow);
            validateCbcImportFileColumns(headerColumnMap);
            
            String[] line = csvReader.readNext();
        
	        while(line != null) {
	        	CbcImportData cbcData = new CbcImportData();
	        	List<CapControlCbcImporterEnum> missingColumns = Lists.newArrayList();
	        	
	        	try {
	        		validateCbcImporterRow(headerColumnMap, line, missingColumns);
	        	
	        		// Create the data from the line.
	        		cbcData = createCbcImportData(line, headerColumnMap);
	        		
	        		cbcImportData.add(cbcData);
	        	} catch (CapControlFileImporterException e) {
	        		CTILogger.error(e.getMessage());
	        		results.add(new CbcImportResult(cbcData, CbcImportResultTypesEnum.MISSING_DATA));
	        	} finally {
	        		line = csvReader.readNext();
	        	}
	        }
		        
		    csvReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

        return cbcImportData;
	}
	
	@Override
	public List<HierarchyImportData> getHierarchyImportData(InputStream inputStream, List<HierarchyImportResult> results) {
		List<HierarchyImportData> hierarchyImportData = Lists.newArrayList();
		
		try {
        	// Open up the file and remove the header row.
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            CSVReader csvReader = new CSVReader(inputStreamReader);
            final String[] headerRow = csvReader.readNext();
            
            Map<CapControlHierarchyImporterEnum, Integer> headerColumnMap = getHierarchyHeaderRowMap(headerRow);
            validateHierarchyImportFileColumns(headerColumnMap);
            
            String[] line = csvReader.readNext();
        
	        while(line != null) {
	        	HierarchyImportData data = new HierarchyImportData();
	        	List<CapControlHierarchyImporterEnum> missingColumns = Lists.newArrayList();
	        	
	        	try {
	        		validateHierarchyImporterRow(headerColumnMap, line, missingColumns);
	        	
	        		// Create the data from the line.
	        		data = createHierarchyImportData(line, headerColumnMap);
	        		
	        		hierarchyImportData.add(data);
	        	} catch (CapControlHierarchyFileImporterException e) {
	        		CTILogger.error(e.getMessage());
	        		results.add(new HierarchyImportResult(data, HierarchyImportResultTypesEnum.MISSING_DATA));
	        	} finally {
	        		line = csvReader.readNext();
	        	}
	        }
		        
		    csvReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

        return hierarchyImportData;
	}
	
	private void validateCbcImporterRow(Map<CapControlCbcImporterEnum, Integer> headerColumnMap,
			String[] line, List<CapControlCbcImporterEnum> missingColumns) throws CapControlFileImporterException {
		Set<CapControlCbcImporterEnum> columns = Sets.newHashSet(headerColumnMap.keySet());
		
		// Check to see if we're a template first.
		if (headerColumnMap.containsKey(CapControlCbcImporterEnum.TEMPLATE_NAME)) {
			int templateColumnId = headerColumnMap.get(CapControlCbcImporterEnum.TEMPLATE_NAME);
			
			if( line[templateColumnId].length() == 0 ) {
				missingColumns.add(CapControlCbcImporterEnum.TEMPLATE_NAME);
			}
			
			// Remove the template name column from the set so the rest of the function doesn't see it and complain.
			columns.remove(CapControlCbcImporterEnum.TEMPLATE_NAME);
		}
		
		ImportActionsEnum action = null;
		
		int actionColumnId = headerColumnMap.get(CapControlCbcImporterEnum.IMPORT_ACTION);
		
		if (line[actionColumnId].length() == 0) {
			missingColumns.add(CapControlCbcImporterEnum.IMPORT_ACTION);
		} else {
			action = ImportActionsEnum.getForDbString(line[actionColumnId]);
		
			if (action == ImportActionsEnum.REMOVE) {
				// In Remove cases, we only care about the name of the CBC, the rest is irrelevant.
				int cbcNameColumnId = headerColumnMap.get(CapControlCbcImporterEnum.CBC_NAME);
				
				if (line[cbcNameColumnId].length() == 0) {
					missingColumns.add(CapControlCbcImporterEnum.CBC_NAME);
				}
			} else {
				for (CapControlCbcImporterEnum column : columns) {
					int columnId = headerColumnMap.get(column);
					
					if (column.isRequired() && line[columnId].length() == 0) {
						missingColumns.add(column);
					}
				}
			}
		}

		if(missingColumns.size() > 0) {
			throw new CapControlCbcFileImportException("CBC Template Import File was missing required data in the following column(s): ", missingColumns);
		}
	}
		
	private void validateHierarchyImporterRow(Map<CapControlHierarchyImporterEnum, Integer> headerColumnMap,
			String[] line, List<CapControlHierarchyImporterEnum> missingColumns) throws CapControlFileImporterException {
		// Ensure all required columns contain data.
		Set<CapControlHierarchyImporterEnum> columns = headerColumnMap.keySet();
		
		for (CapControlHierarchyImporterEnum column : columns) {
			int columnId = headerColumnMap.get(column);
			
			if (column.isRequired() && line[columnId].length() == 0) {
				missingColumns.add(column);
			}
		}
		
		if(missingColumns.size() > 0) {
			throw new CapControlHierarchyFileImporterException("Hierarchy Template Import File was missing required " +
															   "data in the following column(s): ", missingColumns);
		}
	}
}