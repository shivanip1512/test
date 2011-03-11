package com.cannontech.web.capcontrol;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.cbc.model.Area;
import com.cannontech.cbc.model.Capbank;
import com.cannontech.cbc.model.CapbankAdditional;
import com.cannontech.cbc.model.CapbankController;
import com.cannontech.cbc.model.Feeder;
import com.cannontech.cbc.model.Substation;
import com.cannontech.cbc.model.SubstationBus;
import com.cannontech.cbc.service.CapControlCreationService;
import com.cannontech.cbc.util.CBCUtils;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.tools.csv.CSVReader;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class CapControlImportController {

	private CapControlCreationService capControlCreationService;
	private PaoDao paoDao;
	private Map<String, List<String>> errorsLookup = Maps.newHashMap();
	
	private static String [] hierarchyRequiredColumns = { HierarchyColumns.TYPE.name(),
														  HierarchyColumns.NAME.name(),
														  HierarchyColumns.PARENT.name(),
														  HierarchyColumns.DESCRIPTION.name(),
														  HierarchyColumns.MAPLOCATIONID.name(),
														  HierarchyColumns.DISABLED.name() };
	
	private static String [] cbcNewDeviceRequiredColumns = { CbcColumns.CBC_NAME.name(),
															 CbcColumns.CBC_TYPE.name(),
															 CbcColumns.COMM_CHANNEL.name(),
															 CbcColumns.CBC_SERIAL_NUMBER.name(),
															 CbcColumns.CAPBANK_NAME.name(),
															 CbcColumns.MASTER_ADDRESS.name(),
															 CbcColumns.SLAVE_ADDRESS.name(),
															 CbcColumns.SCAN_ENABLED.name(),
															 CbcColumns.SCAN_INTERVAL.name(),
															 CbcColumns.ALT_INTERVAL.name() };
	
	private static String [] cbcTemplateRequiredColumns = { CbcColumns.TEMPLATE_NAME.name(),
															CbcColumns.CBC_NAME.name(),
															CbcColumns.CBC_TYPE.name(),
															CbcColumns.COMM_CHANNEL.name(),
															CbcColumns.CBC_SERIAL_NUMBER.name(),
															CbcColumns.CAPBANK_NAME.name(),
															CbcColumns.MASTER_ADDRESS.name(),
															CbcColumns.SLAVE_ADDRESS.name(),
															CbcColumns.SCAN_ENABLED.name(),
															CbcColumns.SCAN_INTERVAL.name(),
															CbcColumns.ALT_INTERVAL.name() };
	
	private enum HierarchyColumns {
		TYPE,
		NAME,
		PARENT,
		DESCRIPTION,
		MAPLOCATIONID,
		DISABLED
	}
	
	private enum CbcColumns {
		TEMPLATE_NAME,
		CBC_NAME,
		CBC_TYPE,
		CBC_SERIAL_NUMBER,
		CAPBANK_NAME,
		MASTER_ADDRESS,
		SLAVE_ADDRESS,
		COMM_CHANNEL,
		SCAN_ENABLED,
		SCAN_INTERVAL,
		ALT_INTERVAL
	}
	
	private enum CapControlImportType {
		Hierarchy,
		CbcNewDevice,
		CbcTemplate
	}
	
	@RequestMapping
	public String importer(HttpServletRequest request, LiteYukonUser user, ModelMap model) {
	
		String errorsKey = ServletRequestUtils.getStringParameter(request, "errorsKey", null);
		boolean success = ServletRequestUtils.getBooleanParameter(request, "success", false);
		
		List<String> errors = errorsLookup.get(errorsKey);
		
		model.addAttribute("errors", errors);
		model.addAttribute("success", success);
		
		return "tools/capcontrolImport.jsp";
	}
	
	@RequestMapping
	public String importFile(HttpServletRequest request, LiteYukonUser user, ModelMap model) throws ServletException, IOException {
		List<String> errors = new ArrayList<String>();

		MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest)request;
        MultipartFile dataFile = mRequest.getFile("dataFile");
        
        // get file from request
        if (dataFile == null || StringUtils.isBlank(dataFile.getOriginalFilename())) {
        	errors.add("No file selected.");
        } else {
        	
            InputStream inputStream = dataFile.getInputStream();
            
            if (inputStream.available() <= 0) {
            	errors.add("File is empty.");
            } else {
            	//Open up the file and grab the header row to determine the import type
	            InputStreamReader inputStreamReader = new InputStreamReader(dataFile.getInputStream());
	            CSVReader csvReader = new CSVReader(inputStreamReader);
	            String[] headerRow = csvReader.readNext();

	            CapControlImportType importType = determineImportMethod(headerRow);
	            
	            try {
		            switch(importType) {
			            case Hierarchy: {
		    	            capcontrolImportFile(csvReader,errors);
		    	            break;
		            	} 
			            case CbcNewDevice: {	    	            
	    	            	cbcImportFile(csvReader, errors);
	    	            	break;
		            	}
			            case CbcTemplate: {
			            	cbcTemplateImport(csvReader,errors);
			            	break;
			            }
		            }
	            } finally {
	            	csvReader.close();
	            	inputStream.close();
	            }
            }
        }
        
        UUID randomUUID = UUID.randomUUID();
        errorsLookup.put(randomUUID.toString(), errors);
        
        model.addAttribute("errorsKey", randomUUID.toString());
        model.addAttribute("success", errors.isEmpty());

        return "redirect:importer";
	}
	
	private void capcontrolImportFile(CSVReader csvReader, List<String> errors) throws IOException {

        String[] line = csvReader.readNext();
        
        while(line != null) {
        	CapControlType capcontrolType = null;
        	String name = line[1];
        	String parent = line[2];
        	String description = CtiUtilities.STRING_NONE;
        	String mapLocationId = CtiUtilities.STRING_NONE;
        	boolean disabled = false;
        	
        	if (StringUtils.isNotBlank(line[3])) {
        		description = line[3];
        	}

        	if (StringUtils.isNotBlank(line[4])) {
        		disabled = line[4].equalsIgnoreCase("Y")?true:false;
        	}

        	if (StringUtils.isNotBlank(line[5])) {
        		mapLocationId = line[5];
        	}
        	
        	try{
        		capcontrolType = CapControlType.getCapControlType(line[0].toUpperCase());
            	
        		switch (capcontrolType) {
	            	case AREA: {
	            		Area area = new Area();
	            		area.setName(name);
	            		area.setDesctiption(description);
	            		area.setDisabled(disabled);
	            		area.setMapLocationId(mapLocationId);
	            		
	            		try {
	            		    capControlCreationService.createArea(area);
	            		} catch (DataIntegrityViolationException e){
	            			int id = getPaoIdByName(name);
            				if (id == -1) {
            					throw new UnsupportedOperationException(capcontrolType.name() + " failed insert and does not exist. " + name);
            				}
	            		}
	            		break;
	            	}
	            	case SUBSTATION: {
	            		Substation substation = new Substation();
	            		substation.setName(name);
	            		substation.setDesctiption(description);
	            		substation.setDisabled(disabled);
	            		substation.setMapLocationId(mapLocationId);
	            		
	            		try {
	            		    capControlCreationService.createSubstation(substation);
	            		    
	            		    if (StringUtils.isNotBlank(parent)) {
	                            //Not blank, so attempt to assign.
	                            int parentId = getPaoIdByName(parent);
	                            if (parentId == -1) {
	                                throw new UnsupportedOperationException(capcontrolType.name() + " insert as orphan because Parent was not found. " + name + " to " + parent);
	                            }
	                            capControlCreationService.assignSubstation(substation.getId(), parentId);
	                        }
	            		} catch (DataIntegrityViolationException e){
            				//If creation failed, check if it already exists to update assignment.
            				int id = getPaoIdByName(name);
            				
            				if (id == -1) {
            					throw new UnsupportedOperationException(capcontrolType.name() + " failed insert and was not found for re-assigning. " + name + " to " + parent);
            				} else {
            					substation.setId(id);
            					int parentId = getPaoIdByName(parent);
            					if (parentId == -1) {
            						//unassign the substation and do not re-assign.
            						capControlCreationService.unassignSubstation(substation.getId());
            						break;
            					}
            					//Assign will unassign first.
            					capControlCreationService.assignSubstation(substation.getId(), parentId);
            				}
            			}
	            		
	            		break;
	            	}
	            	case SUBBUS: {
	            		SubstationBus subBus = new SubstationBus();
	            		subBus.setName(name);
	            		subBus.setDescription(description);
	            		subBus.setDisabled(disabled);
	            		subBus.setMapLocationId(mapLocationId);
	            		
	            		try {
	            		    capControlCreationService.createSubstationBus(subBus);
	            		    
	            		    if (StringUtils.isNotBlank(parent)) {
	                            //Not blank, so attempt to assign.
	                            int parentId = getPaoIdByName(parent);
	                            if (parentId == -1) {
	                                throw new UnsupportedOperationException(capcontrolType.name() + " insert as orphan because Parent was not found. " + name + " to " + parent);
	                            }
	                            capControlCreationService.assignSubstationBus(subBus.getId(), parentId);
	                        }
	            		} catch (DataIntegrityViolationException e) {
            				//If creation failed, check if it already exists to update assignment.
            				int id = getPaoIdByName(name);
            				if (id == -1) {
            					throw new UnsupportedOperationException(capcontrolType.name() + " failed insert and was not found for re-assigning. " + name + " to " + parent);
            				} else {
            					subBus.setId(id);
            					int parentId = getPaoIdByName(parent);
            					if (parentId == -1) {
            						//unassign the substation and do not re-assign.
            						capControlCreationService.unassignSubstationBus(subBus.getId());
            						break;
            					}
            					//Assign will unassign first.
            					capControlCreationService.assignSubstationBus(subBus.getId(), parentId);
            				}
            			}
	            		
	            		break;
	            	}
	            	case FEEDER: {
	            		Feeder feeder = new Feeder();
	            		feeder.setDescription(description);
	            		feeder.setName(name);
	            		feeder.setDisabled(disabled);
	            		feeder.setMapLocationId(mapLocationId);
	            		
	            		try {
	            		    capControlCreationService.createFeeder(feeder);
	            		    
	            		    if (StringUtils.isNotBlank(parent)) {
                                //Not blank, so attempt to assign.
                                int parentId = getPaoIdByName(parent);
                                if (parentId == -1) {
                                    throw new UnsupportedOperationException(capcontrolType.name() + " inserted as orphan because Parent was not found. " + name + " to " + parent);
                                }
                                capControlCreationService.assignFeeder(feeder.getId(), parentId);
                            }
	            		} catch (DataIntegrityViolationException e) {
            				//If creation failed, check if it already exists to update assignment.
            				int id = getPaoIdByName(name);
            				if (id == -1) {
            					throw new UnsupportedOperationException(capcontrolType.name() + " failed insert and was not found for re-assigning. " + name + " to " + parent);
            				} else {
            					feeder.setId(id);
            					int parentId = getPaoIdByName(parent);
            					if (parentId == -1) {
            						//unassign the substation and do not re-assign.
            						capControlCreationService.unassignFeeder(feeder.getId());
            						break;
            					}
            					//Assign will unassign first.
            					capControlCreationService.assignFeeder(feeder.getId(), parentId);
            				}
            			}
	            		
	            		break;
	            	}
	            	case CAPBANK: {
	            		Capbank bank = new Capbank();
	            		bank.setName(name);
	            		bank.setDescription(description);
	            		bank.setCapbankAdditional(new CapbankAdditional());
	            		bank.setDisabled(disabled);
	            		bank.setMapLocationId(mapLocationId);
	            		
	            		try {
	            		    capControlCreationService.createCapbank(bank);
	            		    
	            		    if (StringUtils.isNotBlank(parent)) {
                                //Not blank, so attempt to assign.
                                int parentId = getPaoIdByName(parent);
                                if (parentId == -1) {
                                    throw new UnsupportedOperationException(capcontrolType.name() + " inserted as orphan because Parent was not found. " + name + " to " + parent);
                                }
                                capControlCreationService.assignCapbank(bank.getId(), parentId);
                            }
	            		} catch (DataIntegrityViolationException e) {
            				//If creation failed, check if it already exists to update assignment.
            				int id = getPaoIdByName(name);
            				if (id == -1) {
            					throw new UnsupportedOperationException(capcontrolType.name() + " failed insert and was not found for re-assigning. " + name + " to " + parent);
            				} else {
            					bank.setId(id);
            					int parentId = getPaoIdByName(parent);
            					if (parentId == -1) {
            						//unassign the substation and do not re-assign.
            						capControlCreationService.unassignCapbank(bank.getId());
            						break;
            					}
            					//Assign will unassign first.
            					capControlCreationService.assignCapbank(bank.getId(), parentId);
            				}
            			}
	            		
	            		break;
	            	}
	            	default: {
	            		throw new UnsupportedOperationException("Unknown Capcontrol Object, insert failed for " + name + " with type: " +  capcontrolType.toString());
	            	}
            	}
        	} catch (UnsupportedOperationException e) {
        		CTILogger.error(e.getMessage());
        		errors.add(e.getMessage());
        	} catch (DataIntegrityViolationException e) { 
        		CTILogger.error("Bad Data for Cap Control object, insert failed for: " + line[0] + " " + name, e );
        		errors.add("Error inserting " + line[0] + "(" + name +  ") into the database. ");
        	} catch (IllegalArgumentException e) {
        		CTILogger.error("Type not a capcontrol type " + line[0], e );
        		errors.add("Type not a capcontrol type: " + line[0] + "(" + name +  "). ");
        	} finally {
        		line = csvReader.readNext();
        	}
        }
	}
	
	private void cbcImportFile(CSVReader csvReader, List<String> errors) throws IOException {

        String[] line = csvReader.readNext();
        
        while(line != null) {
        	try {
	        	String cbcName = line[0];
	        	String cbcType = line[1];
	
	    		int typeId = PAOGroups.getDeviceType(cbcType);
	    		if (typeId == -1) {
	    			throw new UnsupportedOperationException("Import of " + cbcName + " failed. Unknown Type: " + cbcType);
	    		}
	    		PaoType deviceType = PaoType.getForId(typeId);
	    		
	    		if (CapbankController.isValidCbc(deviceType)) {
	    		
    	    		int cbcSerialNumber = Integer.decode(line[2]);
    	    		String capBankName = line[3];
    	    		int masterAddress = Integer.decode(line[4]);
    	    		int slaveAddress = Integer.decode(line[5]);
    	    		String commChannel = line[6];
    	    		//String scanEnabled = line[7];
    	    		int scanInterval = Integer.decode(line[8]);
    	    		int altInterval = Integer.decode(line[9]);
    				
    				CapbankController controller = new CapbankController();
    	    		controller.setType(deviceType);
    	    		controller.setName(cbcName);
    	    		controller.setSerialNumber(cbcSerialNumber);
    	    		controller.setMasterAddress(masterAddress);
    	    		controller.setSlaveAddress(slaveAddress);
    	    		
                    if (CBCUtils.isTwoWay(typeId)) {
                        int commChannelId = getPaoIdByName(commChannel);
                        if (commChannelId == -1) {
                            throw new UnsupportedOperationException("Import of " + cbcName + " failed. Comm Channel not found. " + commChannel);
                        }
                        controller.setPortId(commChannelId);
                    } else {
                        int routeId = getRouteIdByPaoName(commChannel);
                        if (routeId == -1) {
                            throw new UnsupportedOperationException("Import of " + cbcName + " failed. Route not found. " + commChannel);
                        }
                        controller.setRouteId(routeId);
                    }
    	    		
    	    		//These two are default settings. Import them?
    	    		controller.setScanGroup(0);
    	    		controller.setScanType(DeviceScanRate.TYPE_GENERAL);
    	
    	    		controller.setIntervalRate(scanInterval);
    	    		controller.setAlternateRate(altInterval);
    	    		
    	    		try {
    	    		    capControlCreationService.createController(controller);
        	    		if (StringUtils.isNotBlank(capBankName)) {
        	    			capControlCreationService.assignController(controller, capBankName);
        	    		}
    	    		} catch (DataIntegrityViolationException e){
    	    		    // else its an orphan
    	    		    errors.add("Error inserting " + cbcName + " into the database.");
    	    		}
	    		} else {
	    		    throw new UnsupportedOperationException("Import of " + cbcName + " failed. Unknown CBC Type: " + cbcType);
	    		}
	    	} catch (UnsupportedOperationException e) {
	    		CTILogger.error(e.getMessage());
	    		errors.add("Error inserting " + line[0] + " into the database. " + e.getMessage());
	    	} catch (DataIntegrityViolationException e) { 
	    		CTILogger.error("Insert failed for: " + line[1] + " " + line[2], e );
	    		errors.add("Error inserting " + line[0] + " into the database. " + e.getMessage());
	    	} finally {
	    		line = csvReader.readNext();
	    	}
        }
    }
	
	private void cbcTemplateImport(CSVReader csvReader, List<String> errors) throws IOException {
        String[] line = csvReader.readNext();
        
        while(line != null) {
        	try {
	        	String templateName = line[0];
	        	String cbcName = line[1];
                String cbcType = line[2];
               
                int typeId = PAOGroups.getDeviceType(cbcType);
                if (typeId == -1) {
                    throw new UnsupportedOperationException("Import of " + cbcName + " failed. Unknown Type: " + cbcType);
                }
                
	    		int cbcSerialNumber = Integer.decode(line[3]);
	    		String capBankName = line[4];
	    		int masterAddress = Integer.decode(line[5]);
	    		int slaveAddress = Integer.decode(line[6]);
	    		String commChannel = line[7];
	    		//String scanEnabled = line[8];
	    		int scanInterval = Integer.decode(line[9]);
	    		int altInterval = Integer.decode(line[10]);
				
				CapbankController controller = new CapbankController();
	    		controller.setName(cbcName);
	    		controller.setSerialNumber(cbcSerialNumber);
	    		controller.setMasterAddress(masterAddress);
	    		controller.setSlaveAddress(slaveAddress);
	    		
                if (CBCUtils.isTwoWay(typeId)) {
                    int commChannelId = getPaoIdByName(commChannel);
                    if (commChannelId == -1) {
                        throw new UnsupportedOperationException("Import of " + cbcName + " failed. Comm Channel not found. " + commChannel);
                    }
                    controller.setPortId(commChannelId);
                } else {
                    int routeId = getRouteIdByPaoName(commChannel);
                    if (routeId == -1) {
                        throw new UnsupportedOperationException("Import of " + cbcName + " failed. Route not found. " + commChannel);
                    }
                    controller.setRouteId(routeId);
                }
	    		
	    		//controller.setS    scan Enabled?
	    		//These two are default settings. Import them?
	    		controller.setScanGroup(0);
	    		controller.setScanType(DeviceScanRate.TYPE_GENERAL);
	
	    		controller.setIntervalRate(scanInterval);
	    		controller.setAlternateRate(altInterval);
	    		
	    		boolean ret = capControlCreationService.createControllerFromTemplate(templateName,controller);
	    		if (ret && StringUtils.isNotBlank(capBankName)) {
	    			capControlCreationService.assignController(controller, capBankName);
	    		} else if (!ret) {
	    			errors.add("Error inserting " + cbcName + " into the database.");
	    		}//else its an orphan
	    		
	    	} catch (UnsupportedOperationException e) {
	    		CTILogger.error(e.getMessage());
	    		errors.add("Error inserting " + line[0] + " into the database. " + e.getMessage());
	    	} catch (DataIntegrityViolationException e) { 
	    		CTILogger.error("Insert failed for: " + line[1] + " " + line[2], e );
	    		errors.add("Error inserting " + line[0] + " into the database. " + e.getMessage());
	    	} finally {
	    		line = csvReader.readNext();
	    	}
        }
	}
	
	private int getPaoIdByName(String paoName) {
		List<LiteYukonPAObject> paos = paoDao.getLiteYukonPaoByName(paoName, false);
		
		if (paos.size() != 1) {
			return -1;
		}
		LiteYukonPAObject litePao = paos.get(0);
		
		return litePao.getYukonID();
	}
	
    private int getRouteIdByPaoName(String paoName) {
        Integer routeId = paoDao.getRouteIdForRouteName(paoName);
        if (routeId == null) {
            return -1;
        }

        return routeId;
    }

	private CapControlImportType determineImportMethod(String[] header) throws UnsupportedOperationException {
		
		//Figure out what this file is based on the number of header columns
		if(header.length == hierarchyRequiredColumns.length) {
			return CapControlImportType.Hierarchy;
		} else if (header.length == cbcNewDeviceRequiredColumns.length) {
			return CapControlImportType.CbcNewDevice;
		} else if (header.length == cbcTemplateRequiredColumns.length) {
			return CapControlImportType.CbcTemplate;
		} else {
			throw new UnsupportedOperationException("Import File does not match any known methods.");
		}
	}
	
	@Autowired
	public void setCapControlCreationService(
			CapControlCreationService capControlCreationService) {
		this.capControlCreationService = capControlCreationService;
	}
	
	@Autowired
	public void setPaoDao(PaoDao paoDao) {
		this.paoDao = paoDao;
	}
}
