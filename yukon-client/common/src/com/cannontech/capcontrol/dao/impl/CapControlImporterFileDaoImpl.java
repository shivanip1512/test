package com.cannontech.capcontrol.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.BankOpState;
import com.cannontech.capcontrol.creation.CapControlImporterCbcField;
import com.cannontech.capcontrol.creation.CapControlImporterHierarchyField;
import com.cannontech.capcontrol.creation.model.CbcImportData;
import com.cannontech.capcontrol.creation.model.CbcImportInvalidDataResult;
import com.cannontech.capcontrol.creation.model.CbcImportMissingDataResult;
import com.cannontech.capcontrol.creation.model.CbcImportResult;
import com.cannontech.capcontrol.creation.model.CbcImportResultType;
import com.cannontech.capcontrol.creation.model.HierarchyImportCompleteDataResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportData;
import com.cannontech.capcontrol.creation.model.HierarchyImportInvalidDataResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportMissingDataResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportResultType;
import com.cannontech.capcontrol.dao.CapControlImporterFileDao;
import com.cannontech.capcontrol.exception.CapControlCbcFileImportException;
import com.cannontech.capcontrol.exception.CapControlCbcImportException;
import com.cannontech.capcontrol.exception.CapControlFileImporterException;
import com.cannontech.capcontrol.exception.CapControlHierarchyFileImporterException;
import com.cannontech.capcontrol.exception.CapControlHierarchyImportException;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.csvImport.ImportAction;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.opencsv.CSVReader;

public class CapControlImporterFileDaoImpl implements CapControlImporterFileDao {
    @Autowired private PaoDefinitionDao paoDefinitionDao; 
    private static final Logger log = YukonLogManager.getLogger(CapControlImporterFileDaoImpl.class);
	
	private Map<CapControlImporterCbcField, Integer> getCbcHeaderRowMap(final String[] headerRow) {
		Map<CapControlImporterCbcField, Integer> headerColumnMap = Maps.newHashMap();
		
		for (int i = 0; i < headerRow.length; i++) {
			CapControlImporterCbcField column = CapControlImporterCbcField.getColumnByName(headerRow[i]);
			headerColumnMap.put(column, i);
		}
		
		return headerColumnMap;
	}
	
	private Map<CapControlImporterHierarchyField, Integer> getHierarchyHeaderRowMap(final String[] headerRow) {
		Map<CapControlImporterHierarchyField, Integer> headerColumnMap = Maps.newHashMap();
		
		for (int i = 0; i < headerRow.length; i++) {
			CapControlImporterHierarchyField column = CapControlImporterHierarchyField.getColumnByName(headerRow[i]);
			headerColumnMap.put(column, i);
		}
		
		return headerColumnMap;
	}
	
	private void validateCbcImportFileColumns(Map<CapControlImporterCbcField, Integer> headerColumnMap) {
		// Make sure we have all required columns in the file!
		Set<CapControlImporterCbcField> requiredFields = CapControlImporterCbcField.getRequiredFields();
		Set<CapControlImporterCbcField> fileColumns = headerColumnMap.keySet();
		
		if (!fileColumns.containsAll(requiredFields)) {
	        fileColumns.removeAll(CapControlImporterCbcField.getNonRequiredFields());
	        Set<CapControlImporterCbcField> missing = Sets.difference(fileColumns, requiredFields);
            throw new CapControlCbcFileImportException("Cbc Import File is missing required columns: ", missing);
		}
	}
	
	private void validateHierarchyImportFileColumns(Map<CapControlImporterHierarchyField, Integer> headerColumnMap) {
		// Make sure we have all required columns in the file!
		Set<CapControlImporterHierarchyField> requiredFields = CapControlImporterHierarchyField.getRequiredFields();
		Set<CapControlImporterHierarchyField> fileColumns = headerColumnMap.keySet();
		
		if (!fileColumns.containsAll(requiredFields)) {
		    fileColumns.removeAll(CapControlImporterHierarchyField.getNonRequiredFields());
	        Set<CapControlImporterHierarchyField> missingColumns = Sets.difference(fileColumns, requiredFields);
			throw new CapControlHierarchyFileImporterException("Hierarchy import file is missing required columns: ", missingColumns);
		}
	}
	
	private CbcImportData createCbcImportData(final String[] line, Map<CapControlImporterCbcField, Integer> headerColumnMap) {
		PaoType paoType;
		
		String name = line[headerColumnMap.get(CapControlImporterCbcField.CBC_NAME)];
        if (!(PaoUtils.isValidPaoName(name))) {
            throw new CapControlCbcImportException("Import of "
                                                           + name
                                                           + " failed. CBC name cannot include any of the following characters: / \\ , \" ' |",
                                                   CbcImportResultType.ILLEGAL_CHARS);
        }
		ImportAction importAction = ImportAction.getForDbString(line[headerColumnMap.get(CapControlImporterCbcField.IMPORT_ACTION)]);

		if (importAction != ImportAction.REMOVE) {
			String type = line[headerColumnMap.get(CapControlImporterCbcField.CBC_TYPE)];
			try {
				paoType = PaoType.getForDbString(type);
			} catch (IllegalArgumentException e) {
			    throw new CapControlCbcImportException("Import of " + name + " failed. Unknown Type: " + type,
			                                             CbcImportResultType.INVALID_TYPE, e);
			}
			
			if (paoType.isCbc()) {
			    CbcImportData cbcData = new CbcImportData(name, importAction, paoType);
			    
				int cbcSerialNumber = Integer.decode(line[headerColumnMap.get(CapControlImporterCbcField.CBC_SERIAL_NUMBER)]);
				cbcData.setCbcSerialNumber(cbcSerialNumber);
				
			    Integer masterAddressColumn = headerColumnMap.get(CapControlImporterCbcField.MASTER_ADDRESS);
			    if (masterAddressColumn != null) {
                    if (!StringUtils.isBlank(line[masterAddressColumn]))
                        cbcData.setMasterAddress(Integer.decode(line[masterAddressColumn]));
			    }
			    Integer slaveAddressColumn = headerColumnMap.get(CapControlImporterCbcField.SLAVE_ADDRESS);
			    if (slaveAddressColumn != null) {
                    if (!StringUtils.isBlank(line[slaveAddressColumn]))
                        cbcData.setSlaveAddress(Integer.decode(line[slaveAddressColumn]));
			    }
			    Integer commChannelColumn = headerColumnMap.get(CapControlImporterCbcField.COMM_CHANNEL);
			    if (commChannelColumn != null) {
    				String commChannel = line[commChannelColumn];
    			    cbcData.setCommChannel(commChannel);
			    }
		        if (headerColumnMap.containsKey(CapControlImporterCbcField.TEMPLATE_NAME)) {
		            cbcData.setTemplateName(line[headerColumnMap.get(CapControlImporterCbcField.TEMPLATE_NAME)]);
		        }
				
				Integer capBankColumn = headerColumnMap.get(CapControlImporterCbcField.CAPBANK_NAME);
				if (capBankColumn != null) {
				    if (!StringUtils.isBlank(line[capBankColumn])) {
				        cbcData.setCapBankName(line[capBankColumn]);
				    } else {
				        cbcData.setCapBankName(new String()); // This will unassign the Cbc.
				    }
				}
				
				Integer parentRtuColumn = headerColumnMap.get(CapControlImporterCbcField.PARENT_RTU_NAME);
				if (parentRtuColumn != null) {
					if (!StringUtils.isBlank(line[parentRtuColumn])) {
						cbcData.setParentRtuName(line[parentRtuColumn]);
					}
				}
				
				Integer scanEnabledColumn = headerColumnMap.get(CapControlImporterCbcField.SCAN_ENABLED);
				if (scanEnabledColumn != null) {
				    boolean scanEnabled = "Y".equals(line[headerColumnMap.get(CapControlImporterCbcField.SCAN_ENABLED)]);
				    
				    cbcData.setScanEnabled(scanEnabled);
				    
				    if (scanEnabled) {
		                Integer scanColumn = headerColumnMap.get(CapControlImporterCbcField.SCAN_INTERVAL);
		                if (scanColumn != null && !StringUtils.isBlank(line[scanColumn])) {
		                    cbcData.setScanInterval(Integer.decode(line[scanColumn]));
		                }
		                
		                Integer altColumn = headerColumnMap.get(CapControlImporterCbcField.ALT_INTERVAL);
		                if (altColumn != null && !StringUtils.isBlank(line[altColumn])) {
		                    cbcData.setAltInterval(Integer.decode(line[altColumn]));
		                }
				    } 
				}
				
				return cbcData;
			} else {
			    // They gave us a valid PaoType, only it wasn't a valid CBC type. Yell!
			    throw new CapControlCbcImportException("Import of " + name + " failed. Invalid CBC " +
                                                         "PaoType of " + paoType.getDbString() + " provided.", 
                                                         CbcImportResultType.INVALID_TYPE);
			}
		} else {
		    return new CbcImportData(name, importAction, null); // PaoType is irrelevant!
		}
	}
	
	/**
	 * Creates a {@link HierarchyImportData} object from the required information provided by
	 * the user. 
	 * @param line the line of input
	 * @param headerColumnMap the line of header columns.
	 * @return a {@link HierarchyImportData} object populated with the minimal required data.
	 * @throws CapControlHierarchyImportException if the input is missing or has invalid required
	 *     data.
	 */
	private HierarchyImportData createHierarchyImportData(final String[] line, Map<CapControlImporterHierarchyField, Integer> headerColumnMap) 
	                        throws CapControlHierarchyImportException {
	    PaoType paoType;
	    ImportAction importAction;
        
        String name = line[headerColumnMap.get(CapControlImporterHierarchyField.NAME)];
        if (!(PaoUtils.isValidPaoName(name))) {
            throw new CapControlHierarchyImportException("Import of "
                                                                 + name
                                                                 + " failed. Hierarchy name cannot include any of the following characters: / \\ , \" ' |",
                                                         HierarchyImportResultType.ILLEGAL_CHARS);
        }
        String ccType = line[headerColumnMap.get(CapControlImporterHierarchyField.TYPE)];   
        
        try {
            paoType = PaoType.getForDbString(ccType);
        } catch (IllegalArgumentException i) {
            throw new CapControlHierarchyImportException("Import of " + name + " failed. Unknown Type: " + ccType, 
                                                         HierarchyImportResultType.INVALID_TYPE, i);
        }
        
        // There are required fields we KNOW are here. Set them, then try the non-requireds.
        String action = line[headerColumnMap.get(CapControlImporterHierarchyField.IMPORT_ACTION)];
        try {
            importAction = ImportAction.getForDbString(action);
        } catch (IllegalArgumentException e) {
            throw new CapControlHierarchyImportException("Import of " + name + " failed. Unknown Import Action" +
                                                         action, HierarchyImportResultType.INVALID_IMPORT_ACTION, e);
        }
        
        // We got everything we need, lets make the HierarchyImportData object.
        return new HierarchyImportData(paoType, name, importAction);
	}
	
	private void populateHierarchyImportData(final String[] line, Map<CapControlImporterHierarchyField, Integer> headerColumnMap,
                                           HierarchyImportData data) {
		Integer descColumn = headerColumnMap.get(CapControlImporterHierarchyField.DESCRIPTION);
		if (descColumn != null && !StringUtils.isBlank(line[descColumn])) {
		    data.setDescription(line[descColumn]);
		}
		
		Integer disabledColumn = headerColumnMap.get(CapControlImporterHierarchyField.DISABLED);
		if (disabledColumn != null && !StringUtils.isBlank(line[disabledColumn])) {
		    String disabled = line[disabledColumn];
		    if (disabled.equalsIgnoreCase("Y")) {
		        data.setDisabled(true);
		    } else if (disabled.equalsIgnoreCase("N")) {
		        data.setDisabled(false);
		    } else {
		        throw new CapControlHierarchyImportException("Disabled field contained invalid data. Please " +
		                "change to 'Y' or 'N'", 
		                HierarchyImportResultType.INVALID_DISABLED_VALUE);
		    }
		}
		
		if (data.getPaoType() != PaoType.CAP_CONTROL_AREA && 
		    data.getPaoType() != PaoType.CAP_CONTROL_SPECIAL_AREA) {
		    // We don't care about the parent or location if it's an Area or Special area, ignore them.
	        Integer mapColumn = headerColumnMap.get(CapControlImporterHierarchyField.MAP_LOCATION_ID);
	        if (mapColumn != null && !StringUtils.isBlank(line[mapColumn])) {
	            data.setMapLocationId(line[mapColumn]);
	        }
	        
    		Integer parentColumn = headerColumnMap.get(CapControlImporterHierarchyField.PARENT);
    		if (parentColumn != null) {
    		    if (!StringUtils.isBlank(line[parentColumn])) {
    		        data.setParent(line[parentColumn]);
    		    } else {
    		        data.setParent(new String());
    		    }
    		}
    		
    		if (PaoType.CAPBANK == data.getPaoType()) {
    		    // These columns only matter for cap banks, no need to validate or even look at them otherwise.
    		    Integer opStateColumn = headerColumnMap.get(CapControlImporterHierarchyField.OPERATIONAL_STATE);
    		    if (opStateColumn != null && !StringUtils.isBlank(line[opStateColumn])) {
    		        try {
    		            BankOpState bankOpState = BankOpState.getStateByName(line[opStateColumn]);
    		            data.setBankOpState(bankOpState);
    		        } catch (IllegalArgumentException e) {
    		            throw new CapControlHierarchyImportException("Operational state field contained invalid data.", 
    		                                                         HierarchyImportResultType.INVALID_OPERATIONAL_STATE, e);
    		        }
    		    }
    		    
    		    Integer capBankSizeColumn = headerColumnMap.get(CapControlImporterHierarchyField.CAPBANK_SIZE);
    		    if (capBankSizeColumn != null && !StringUtils.isBlank(line[capBankSizeColumn])) {
    		        data.setCapBankSize(Integer.valueOf(line[capBankSizeColumn]));
    		    }
    		}
		}
	}
	
	@Override
	public List<CbcImportData> getCbcImportData(InputStream inputStream, List<CbcImportResult> results) throws RuntimeException {
		List<CbcImportData> cbcImportData = Lists.newArrayList();
		
		try {
            // Open up the file and remove the header row.
            BOMInputStream bomInputStream = new BOMInputStream(inputStream, ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE,
                ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_32LE, ByteOrderMark.UTF_32BE);
            InputStreamReader inputStreamReader = new InputStreamReader(bomInputStream);
            CSVReader csvReader = new CSVReader(inputStreamReader);
            final String[] headerRow = csvReader.readNext();
            
            Map<CapControlImporterCbcField, Integer> headerColumnMap = getCbcHeaderRowMap(headerRow);
            validateCbcImportFileColumns(headerColumnMap);
            
            String[] line = csvReader.readNext();
        
	        while(line != null) {
	        	CbcImportData cbcData = null;
	        	List<CapControlImporterCbcField> missingColumns = Lists.newArrayList();
	        	
	        	try {
	        		validateCbcImporterRow(headerColumnMap, line, missingColumns);
	        	
	        		// Create the data from the line.
	        		cbcData = createCbcImportData(line, headerColumnMap);
	        		
	        		cbcImportData.add(cbcData);
	        	} catch (CapControlCbcFileImportException e) {
	        		log.error(e.getMessage());
	        		results.add(new CbcImportMissingDataResult(e.getColumns()));
	        	} catch (CapControlCbcImportException e) {
	        	    log.error(e.getMessage());
	        	    results.add(new CbcImportInvalidDataResult(e.getImportResultType()));
	        	} finally {
	        		line = csvReader.readNext();
	        	}
	        }
		        
		    csvReader.close();
		} catch (IOException e) {
			throw new RuntimeException("Cap Control Importer encountered an error while reading the input file!", e);
		}

        return cbcImportData;
	}
	
	@Override
	public List<HierarchyImportData> getHierarchyImportData(InputStream inputStream, List<HierarchyImportResult> results) {
		List<HierarchyImportData> hierarchyImportData = Lists.newArrayList();
		
		try {
            // Open up the file and remove the header row.
            BOMInputStream bomInputStream = new BOMInputStream(inputStream, ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE,
                    ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_32LE, ByteOrderMark.UTF_32BE);
            InputStreamReader inputStreamReader = new InputStreamReader(bomInputStream);
            CSVReader csvReader = new CSVReader(inputStreamReader);
            final String[] headerRow = csvReader.readNext();
            
            Map<CapControlImporterHierarchyField, Integer> headerColumnMap = getHierarchyHeaderRowMap(headerRow);
            validateHierarchyImportFileColumns(headerColumnMap);
            
            String[] line = csvReader.readNext();
        
	        while(line != null) {
	        	HierarchyImportData data = null;
	        	List<CapControlImporterHierarchyField> missingColumns = Lists.newArrayList();
	        	
	        	try {
	        		validateHierarchyImporterRow(headerColumnMap, line, missingColumns);
	        	} catch (CapControlHierarchyFileImporterException e) {
                    log.error(e.getMessage());
                    results.add(new HierarchyImportMissingDataResult(e.getColumns()));
                    line = csvReader.readNext();
                    continue;
                }
	        	
	        	try {
	        		data = createHierarchyImportData(line, headerColumnMap);
	        	} catch (CapControlHierarchyImportException e) {
                    log.error(e.getMessage());
                    results.add(new HierarchyImportInvalidDataResult(e.getImportResultType()));
                    line = csvReader.readNext();
                    continue;
                }
	        	
	        	try {
	        		// Create the data from the line.
	        		populateHierarchyImportData(line, headerColumnMap, data);
	        		
	        		hierarchyImportData.add(data);
	        	} catch (CapControlHierarchyImportException e) {
                    log.error(e.getMessage());
                    results.add(new HierarchyImportCompleteDataResult(data, e.getImportResultType()));
                    line = csvReader.readNext();
                    continue;
                }

	        	line = csvReader.readNext();
	        }
		        
		    csvReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

        return hierarchyImportData;
	}
	
	private void validateCbcImporterRow(Map<CapControlImporterCbcField, Integer> headerColumnMap,
			String[] line, List<CapControlImporterCbcField> missingColumns) throws CapControlCbcFileImportException {
		Set<CapControlImporterCbcField> columns = Sets.newHashSet(headerColumnMap.keySet());
        String paoTypeDbString = line[headerColumnMap.get(CapControlImporterCbcField.CBC_TYPE)];
        PaoType paoType = null;
        try {
            paoType = PaoType.getForDbString(paoTypeDbString);
        } catch (IllegalArgumentException e) {
            throw new CapControlCbcImportException("Import failed. Unknown CBC PaoType: " + paoTypeDbString,
                CbcImportResultType.INVALID_TYPE, e);
        }
		// Check to see if we're a template first.
		if (headerColumnMap.containsKey(CapControlImporterCbcField.TEMPLATE_NAME)) {
			int templateColumnId = headerColumnMap.get(CapControlImporterCbcField.TEMPLATE_NAME);
			
			if( line[templateColumnId].length() == 0 ) {
				missingColumns.add(CapControlImporterCbcField.TEMPLATE_NAME);
			}
			
			// Remove the template name column from the set so the rest of the function doesn't see it and complain.
			columns.remove(CapControlImporterCbcField.TEMPLATE_NAME);
		}
		
		ImportAction action = null;
		
		int actionColumnId = headerColumnMap.get(CapControlImporterCbcField.IMPORT_ACTION);
		
		if (StringUtils.isBlank(line[actionColumnId])) {
			missingColumns.add(CapControlImporterCbcField.IMPORT_ACTION);
		} else {
		    try {
		        action = ImportAction.getForDbString(line[actionColumnId]);
	            
	            if (action == ImportAction.REMOVE) {
	                // In Remove cases, we only care about the name of the CBC, the rest is irrelevant.
	                int cbcNameColumnId = headerColumnMap.get(CapControlImporterCbcField.CBC_NAME);
	                
	                if (StringUtils.isBlank(line[cbcNameColumnId])) {
	                    missingColumns.add(CapControlImporterCbcField.CBC_NAME);
	                }
	            } else {
	                for (CapControlImporterCbcField column : columns) {
	                    int columnId = headerColumnMap.get(column);
                        boolean isOneWay = paoDefinitionDao.isTagSupported(paoType, PaoTag.ONE_WAY_DEVICE);
                        
                        if (isOneWay) {
                            if (column == CapControlImporterCbcField.SLAVE_ADDRESS 
                             || column == CapControlImporterCbcField.MASTER_ADDRESS) {
                                continue;
                            }
                        } 
                        if (paoType.isLogicalCBC()) {
                            if (column == CapControlImporterCbcField.SLAVE_ADDRESS
                             || column == CapControlImporterCbcField.MASTER_ADDRESS
                             || column == CapControlImporterCbcField.COMM_CHANNEL) {
                               continue;
                           }
                        } 
                        if (column.isRequired() && StringUtils.isBlank(line[columnId])) {
                            missingColumns.add(column);
                        }
	                }
	            }
		    } catch (IllegalArgumentException e) {
		        throw new CapControlCbcImportException("Invalid import action entered.", 
		                                               CbcImportResultType.INVALID_IMPORT_ACTION, e);
		    }
		}

		if(missingColumns.size() > 0) {
			throw new CapControlCbcFileImportException("CBC Template Import File was missing required data in the following column(s): ", missingColumns);
		}
	}
		
	private void validateHierarchyImporterRow(Map<CapControlImporterHierarchyField, Integer> headerColumnMap,
			String[] line, List<CapControlImporterHierarchyField> missingColumns) throws CapControlFileImporterException {
		// Ensure all required columns contain data.
		Set<CapControlImporterHierarchyField> columns = headerColumnMap.keySet();
		
		for (CapControlImporterHierarchyField column : columns) {
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