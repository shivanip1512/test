package com.cannontech.capcontrol.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.cannontech.capcontrol.creation.CapControlImporterCbcCsvField;
import com.cannontech.capcontrol.creation.CapControlImporterHierarchyCsvField;
import com.cannontech.capcontrol.creation.model.CbcImportData;
import com.cannontech.capcontrol.creation.model.CbcImportResult;
import com.cannontech.capcontrol.creation.model.CbcImportResultType;
import com.cannontech.capcontrol.creation.model.HierarchyImportData;
import com.cannontech.capcontrol.creation.model.HierarchyImportResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportResultType;
import com.cannontech.capcontrol.creation.model.ImportAction;
import com.cannontech.capcontrol.dao.CapControlImporterFileDao;
import com.cannontech.capcontrol.exception.CapControlCbcFileImportException;
import com.cannontech.capcontrol.exception.CapControlFileImporterException;
import com.cannontech.capcontrol.exception.CapControlHierarchyFileImporterException;
import com.cannontech.capcontrol.exception.CapControlImportException;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.tools.csv.CSVReader;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class CapControlImporterFileDaoImpl implements CapControlImporterFileDao {
	
	private Map<CapControlImporterCbcCsvField, Integer> getCbcHeaderRowMap(final String[] headerRow) {
		Map<CapControlImporterCbcCsvField, Integer> headerColumnMap = Maps.newHashMap();
		
		for (int i = 0; i < headerRow.length; i++) {
			CapControlImporterCbcCsvField column = CapControlImporterCbcCsvField.getColumnByName(headerRow[i]);
			headerColumnMap.put(column, i);
		}
		
		return headerColumnMap;
	}
	
	private Map<CapControlImporterHierarchyCsvField, Integer> getHierarchyHeaderRowMap(final String[] headerRow) {
		Map<CapControlImporterHierarchyCsvField, Integer> headerColumnMap = Maps.newHashMap();
		
		for (int i = 0; i < headerRow.length; i++) {
			CapControlImporterHierarchyCsvField column = CapControlImporterHierarchyCsvField.getColumnByName(headerRow[i]);
			headerColumnMap.put(column, i);
		}
		
		return headerColumnMap;
	}
	
	private void validateCbcImportFileColumns(Map<CapControlImporterCbcCsvField, Integer> headerColumnMap) {
		// Make sure we have all required columns in the file!
		Set<CapControlImporterCbcCsvField> requiredFields = CapControlImporterCbcCsvField.getRequiredFields();
		Set<CapControlImporterCbcCsvField> fileColumns = headerColumnMap.keySet();
		
		if (!fileColumns.containsAll(requiredFields)) {
	        fileColumns.removeAll(CapControlImporterCbcCsvField.getNonRequiredFields());
	        Set<CapControlImporterCbcCsvField> missing = Sets.difference(fileColumns, requiredFields);
            throw new CapControlCbcFileImportException("Cbc Import File is missing required columns: ", missing);
		}
	}
	
	private void validateHierarchyImportFileColumns(Map<CapControlImporterHierarchyCsvField, Integer> headerColumnMap) {
		// Make sure we have all required columns in the file!
		Set<CapControlImporterHierarchyCsvField> requiredFields = CapControlImporterHierarchyCsvField.getRequiredFields();
		Set<CapControlImporterHierarchyCsvField> fileColumns = headerColumnMap.keySet();
		
		if (!fileColumns.containsAll(requiredFields)) {
		    fileColumns.removeAll(CapControlImporterHierarchyCsvField.getNonRequiredFields());
	        Set<CapControlImporterHierarchyCsvField> missingColumns = Sets.difference(fileColumns, requiredFields);
			throw new CapControlHierarchyFileImporterException("Hierarchy import file is missing required columns: ", missingColumns);
		}
	}
	
	private CbcImportData createCbcImportData(final String[] line, Map<CapControlImporterCbcCsvField, Integer> headerColumnMap) {
		PaoType paoType;
		
		String name = line[headerColumnMap.get(CapControlImporterCbcCsvField.CBC_NAME)];

		ImportAction importAction = ImportAction.getForDbString(line[headerColumnMap.get(CapControlImporterCbcCsvField.IMPORT_ACTION)]);

		if (importAction != ImportAction.REMOVE) {
			String type = line[headerColumnMap.get(CapControlImporterCbcCsvField.CBC_TYPE)];
			try {
				paoType = PaoType.getForDbString(type);
			} catch (IllegalArgumentException e) {
				throw new CapControlImportException("Import of " + name + " failed. Unknown Type: " + type);
			}
			
			if (PaoType.isCbc(paoType)) {
				// There are required fields we KNOW are here. Set them, then try the non-requireds.
				int cbcSerialNumber = Integer.decode(line[headerColumnMap.get(CapControlImporterCbcCsvField.CBC_SERIAL_NUMBER)]);
				int masterAddress = Integer.decode(line[headerColumnMap.get(CapControlImporterCbcCsvField.MASTER_ADDRESS)]);
				int slaveAddress = Integer.decode(line[headerColumnMap.get(CapControlImporterCbcCsvField.SLAVE_ADDRESS)]);
				String commChannel = line[headerColumnMap.get(CapControlImporterCbcCsvField.COMM_CHANNEL)];
				
				CbcImportData cbcData = new CbcImportData(name, 
				                                          importAction,
				                                          paoType, 
				                                          commChannel, 
				                                          cbcSerialNumber, 
				                                          masterAddress, 
				                                          slaveAddress);
		        
		        // Template name may or may not be there. Check.
		        if (headerColumnMap.containsKey(CapControlImporterCbcCsvField.TEMPLATE_NAME)) {
		            cbcData.setTemplateName(line[headerColumnMap.get(CapControlImporterCbcCsvField.TEMPLATE_NAME)]);
		        }
				
				Integer capBankColumn = headerColumnMap.get(CapControlImporterCbcCsvField.CAPBANK_NAME);
				if (capBankColumn != null && !StringUtils.isBlank(line[capBankColumn])) {
					cbcData.setCapBankName(line[capBankColumn]);
				}
				
				Integer scanColumn = headerColumnMap.get(CapControlImporterCbcCsvField.SCAN_INTERVAL);
				if (scanColumn != null && !StringUtils.isBlank(line[scanColumn])) {
					cbcData.setScanInterval(Integer.decode(line[scanColumn]));
				}
				
				Integer altColumn = headerColumnMap.get(CapControlImporterCbcCsvField.ALT_INTERVAL);
				if (altColumn != null && !StringUtils.isBlank(line[altColumn])) {
					cbcData.setAltInterval(Integer.decode(line[altColumn]));
				}
				
				Integer scanEnabledColumn = headerColumnMap.get(CapControlImporterCbcCsvField.SCAN_ENABLED);
				if (scanEnabledColumn != null && !StringUtils.isBlank(line[scanEnabledColumn])) {
					cbcData.setScanEnabled(line[scanEnabledColumn]);
				}
				
				return cbcData;
			} else {
			    // They gave us a valid PaoType, only it wasn't a valid CBC type. Yell!
			    throw new CapControlImportException("Import of " + name + " failed. Invalid CBC " +
			                                        "PaoType of " + paoType.getDbString() + " provided.");
			}
		} else {
		    /* This is a little icky, but we only needed the name and 
		     * import action of remove anyway. */
		    return CbcImportData.createRemovalImportData(name);
		}
	}
	
	private HierarchyImportData createHierarchyImportData(final String[] line, Map<CapControlImporterHierarchyCsvField, Integer> headerColumnMap) {
		PaoType paoType;
		
		String name = line[headerColumnMap.get(CapControlImporterHierarchyCsvField.NAME)];
		String ccType = line[headerColumnMap.get(CapControlImporterHierarchyCsvField.TYPE)];	
		
		try {
			paoType = PaoType.getForDbString(ccType);
		} catch (IllegalArgumentException i) {
			throw new CapControlImportException("Import of " + name + " failed. Unknown Type: " + ccType);
		}
		
		// There are required fields we KNOW are here. Set them, then try the non-requireds.
		String action = line[headerColumnMap.get(CapControlImporterHierarchyCsvField.IMPORT_ACTION)];
		ImportAction importAction = ImportAction.getForDbString(action);
		
		// We got everything we need, lets make the HierarchyImportData object.
		HierarchyImportData data = new HierarchyImportData(paoType, name, importAction);
		
		Integer descColumn = headerColumnMap.get(CapControlImporterHierarchyCsvField.DESCRIPTION);
		if (descColumn != null && !StringUtils.isBlank(line[descColumn])) {
			data.setDescription(line[descColumn]);
		}
		
		Integer disabledColumn = headerColumnMap.get(CapControlImporterHierarchyCsvField.DISABLED);
		if (disabledColumn != null && !StringUtils.isBlank(line[disabledColumn])) {
			String disabled = line[disabledColumn];
			if (disabled.equalsIgnoreCase("Y")) {
				data.setDisabled(true);
			} else if (disabled.equalsIgnoreCase("N")) {
				data.setDisabled(false);
			} else {
				throw new CapControlImportException("Disabled field contained invalid data. Please " +
													"change to 'Y' or 'N'");
			}
		}
		
		Integer mapColumn = headerColumnMap.get(CapControlImporterHierarchyCsvField.MAP_LOCATION_ID);
		if (mapColumn != null && !StringUtils.isBlank(line[mapColumn])) {
			data.setMapLocationId(line[mapColumn]);
		}
		
		Integer parentColumn = headerColumnMap.get(CapControlImporterHierarchyCsvField.PARENT);
		if (parentColumn != null && !StringUtils.isBlank(line[parentColumn])) {
			data.setParent(line[parentColumn]);
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
            
            Map<CapControlImporterCbcCsvField, Integer> headerColumnMap = getCbcHeaderRowMap(headerRow);
            validateCbcImportFileColumns(headerColumnMap);
            
            String[] line = csvReader.readNext();
        
	        while(line != null) {
	        	CbcImportData cbcData = null;
	        	List<CapControlImporterCbcCsvField> missingColumns = Lists.newArrayList();
	        	
	        	try {
	        		validateCbcImporterRow(headerColumnMap, line, missingColumns);
	        	
	        		// Create the data from the line.
	        		cbcData = createCbcImportData(line, headerColumnMap);
	        		
	        		cbcImportData.add(cbcData);
	        	} catch (CapControlFileImporterException e) {
	        		CTILogger.error(e.getMessage());
	        		results.add(new CbcImportResult(cbcData, CbcImportResultType.MISSING_DATA));
	        	} catch (CapControlImportException e) {
	        	    CTILogger.error(e.getMessage());
	        	    results.add(new CbcImportResult(cbcData, CbcImportResultType.INVALID_TYPE));
	        	}
	        	finally {
	        		line = csvReader.readNext();
	        	}
	        }
		        
		    csvReader.close();
		} catch (IOException e) {
			throw new CapControlImportException("Cap Control Importer encountered an error while reading the input file!");
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
            
            Map<CapControlImporterHierarchyCsvField, Integer> headerColumnMap = getHierarchyHeaderRowMap(headerRow);
            validateHierarchyImportFileColumns(headerColumnMap);
            
            String[] line = csvReader.readNext();
        
	        while(line != null) {
	        	HierarchyImportData data = null;
	        	List<CapControlImporterHierarchyCsvField> missingColumns = Lists.newArrayList();
	        	
	        	try {
	        		validateHierarchyImporterRow(headerColumnMap, line, missingColumns);
	        	
	        		// Create the data from the line.
	        		data = createHierarchyImportData(line, headerColumnMap);
	        		
	        		hierarchyImportData.add(data);
	        	} catch (CapControlHierarchyFileImporterException e) {
	        		CTILogger.error(e.getMessage());
	        		results.add(new HierarchyImportResult(data, HierarchyImportResultType.MISSING_DATA));
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
	
	private void validateCbcImporterRow(Map<CapControlImporterCbcCsvField, Integer> headerColumnMap,
			String[] line, List<CapControlImporterCbcCsvField> missingColumns) throws CapControlFileImporterException {
		Set<CapControlImporterCbcCsvField> columns = Sets.newHashSet(headerColumnMap.keySet());
		
		// Check to see if we're a template first.
		if (headerColumnMap.containsKey(CapControlImporterCbcCsvField.TEMPLATE_NAME)) {
			int templateColumnId = headerColumnMap.get(CapControlImporterCbcCsvField.TEMPLATE_NAME);
			
			if( line[templateColumnId].length() == 0 ) {
				missingColumns.add(CapControlImporterCbcCsvField.TEMPLATE_NAME);
			}
			
			// Remove the template name column from the set so the rest of the function doesn't see it and complain.
			columns.remove(CapControlImporterCbcCsvField.TEMPLATE_NAME);
		}
		
		ImportAction action = null;
		
		int actionColumnId = headerColumnMap.get(CapControlImporterCbcCsvField.IMPORT_ACTION);
		
		if (StringUtils.isBlank(line[actionColumnId])) {
			missingColumns.add(CapControlImporterCbcCsvField.IMPORT_ACTION);
		} else {
			action = ImportAction.getForDbString(line[actionColumnId]);
		
			if (action == ImportAction.REMOVE) {
				// In Remove cases, we only care about the name of the CBC, the rest is irrelevant.
				int cbcNameColumnId = headerColumnMap.get(CapControlImporterCbcCsvField.CBC_NAME);
				
				if (StringUtils.isBlank(line[cbcNameColumnId])) {
					missingColumns.add(CapControlImporterCbcCsvField.CBC_NAME);
				}
			} else {
				for (CapControlImporterCbcCsvField column : columns) {
					int columnId = headerColumnMap.get(column);
					
					if (column.isRequired() && StringUtils.isBlank(line[columnId])) {
						missingColumns.add(column);
					}
				}
			}
		}

		if(missingColumns.size() > 0) {
			throw new CapControlCbcFileImportException("CBC Template Import File was missing required data in the following column(s): ", missingColumns);
		}
	}
		
	private void validateHierarchyImporterRow(Map<CapControlImporterHierarchyCsvField, Integer> headerColumnMap,
			String[] line, List<CapControlImporterHierarchyCsvField> missingColumns) throws CapControlFileImporterException {
		// Ensure all required columns contain data.
		Set<CapControlImporterHierarchyCsvField> columns = headerColumnMap.keySet();
		
		for (CapControlImporterHierarchyCsvField column : columns) {
			int columnId = headerColumnMap.get(column);
			
			if (column.isRequired() && StringUtils.isBlank(line[columnId])) {
				missingColumns.add(column);
			}
		}
		
		if(missingColumns.size() > 0) {
			throw new CapControlHierarchyFileImporterException("Hierarchy Template Import File was missing required " +
															   "data in the following column(s): ", missingColumns);
		}
	}
}