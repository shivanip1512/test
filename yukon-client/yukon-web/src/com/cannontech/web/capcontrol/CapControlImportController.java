package com.cannontech.web.capcontrol;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.DeviceType;
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
	
	private static String [] capcontrolImportHeader = { "TYPE","NAME","PARENT" };
	private static String [] controllerImportHeader = {	"TEMPLATE NAME","CBC NAME",
													   	"CBC TYPE","CBC SERIAL NUMBER",
													   	"CAPBANK NAME","MASTER ADDRESS",
													   	"SLAVE ADDRESS","COMM CHANNEL",
													   	"SCAN ENABLED","SCAN INTERVAL",
													   	"ALT INTERVAL" };
	
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
	            InputStreamReader inputStreamReader = new InputStreamReader(dataFile.getInputStream());
	            //Decide which private to call.
	            CSVReader csvReader = new CSVReader(inputStreamReader);
	            String[] headerRow = csvReader.readNext();

            	if (headerRow.length == capcontrolImportHeader.length) {
    	            //Error Checking make sure the columns are there.
    	            String error = checkHeader(headerRow,capcontrolImportHeader);
    	            if (error == null) {
    	            	capcontrolImportFile(csvReader,errors);
    	            } else {
    	            	errors.add(error);
    	            }
            	} else if (headerRow.length == controllerImportHeader.length) {
    	            //Error Checking make sure the columns are there.
    	            String error = checkHeader(headerRow,controllerImportHeader);
    	            if (error == null) {
    	            	cbcImportFile(csvReader, errors);
    	            } else {
    	            	errors.add(error);
    	            }
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
        try{
            String[] line = csvReader.readNext();
            
            while(line != null) {
            	CapControlType capcontrolType = null;
            	String name = line[1];
            	String parent = line[2];
            	
            	try{
            		capcontrolType = CapControlType.getCapControlType(line[0].toUpperCase());
	            	
            		switch (capcontrolType) {
		            	case AREA: {
		            		Area area = new Area();
		            		area.setName(name);
		            		
		            		capControlCreationService.createArea(area);
		            		break;
		            	}
		            	case SUBSTATION: {
		            		Substation substation = new Substation();
		            		substation.setName(name);
		            		
		            		boolean ret = capControlCreationService.createSubstation(substation);
		            		if (ret && StringUtils.isNotBlank(parent)) {
		            			capControlCreationService.assignSubstation(substation.getId(), parent);
		            		}
		            		break;
		            	}
		            	case SUBBUS: {
		            		SubstationBus subBus = new SubstationBus();
		            		subBus.setName(name);
		            		
		            		boolean ret = capControlCreationService.createSubstationBus(subBus);
		            		if (ret && StringUtils.isNotBlank(parent)) {
		            			capControlCreationService.assignSubstationBus(subBus.getId(), parent);
		            		}
		            		break;
		            	}
		            	case FEEDER: {
		            		Feeder feeder = new Feeder();
		            		feeder.setName(name);
		            		
		            		boolean ret = capControlCreationService.createFeeder(feeder);
		            		if (ret && StringUtils.isNotBlank(parent)) {
		            			capControlCreationService.assignFeeder(feeder.getId(), parent);
		            		}
		            		break;
		            	}
		            	case CAPBANK: {
		            		Capbank bank = new Capbank();
		            		bank.setName(name);
		            		bank.setCapbankAdditional(new CapbankAdditional());
		            		
		            		boolean ret = capControlCreationService.createCapbank(bank);
		            		if (ret && StringUtils.isNotBlank(parent)) {
		            			capControlCreationService.assignCapbank(bank.getId(), parent);
		            		}
		            		break;
		            	}
		            	default: {
		            		throw new UnsupportedOperationException("Unkown Capcontrol Object, insert failed for " + name + " with type: " +  capcontrolType.toString());
		            	}
	            	}


            	} catch (UnsupportedOperationException e) {
            		CTILogger.error(e.getMessage());
            		errors.add(e.getMessage());
            	} catch (DataIntegrityViolationException e) { 
            		CTILogger.error("Bad Data for Cap Control object, insert failed for: " + line[0] + " " + name, e );
            		errors.add("Error inserting " + line[0] + "(" + name +  ") into the database. " + e.getMessage());
            	} catch (IllegalArgumentException e) {
            		CTILogger.error("Type not a capcontrol type " + line[0], e );
            		errors.add("Type not a capcontrol type: " + line[0] + "(" + name +  "). " + e.getMessage());
            	} finally {
            		line = csvReader.readNext();
            	}
            }

        } finally {
        	csvReader.close();
        }
	}
	
	private void cbcImportFile(CSVReader csvReader, List<String> errors) throws IOException {
        try {
            String[] line = csvReader.readNext();
            
            //TODO Implement Template method for importing
            while(line != null) {
            	try {
	            	DeviceType deviceType = null;
            		
	            	//String templateName = line[0];
	            	//If template name import by template and ignore the rest of the file?
	            	
	            	String cbcName = line[1];
	            	String cbcType = line[2];

            		int typeId = PAOGroups.getDeviceType(cbcType);
            		if (typeId == -1) {
            			throw new UnsupportedOperationException("Import of " + cbcName + " failed. Unknown Type: " + cbcType);
            		}			            		
            		deviceType = DeviceType.getForId(typeId);

            		int cbcSerialNumber = Integer.decode(line[3]);
            		String capBankName = line[4];
            		int masterAddress = Integer.decode(line[5]);
            		int slaveAddress = Integer.decode(line[6]);
            		String commChannel = line[7];
            		//String scanEnabled = line[8];
            		int scanInterval = Integer.decode(line[9]);
            		int altInterval = Integer.decode(line[10]);
        			
            		int commChannelId = getPaoIdByName(commChannel);
            		if (commChannelId == -1) {
            			throw new UnsupportedOperationException("Import of " + cbcName + " failed. Comm Channel was not found. " + commChannel);
            		}
            		
        			CapbankController controller = new CapbankController();
            		controller.setType(deviceType.getDeviceTypeId());
            		controller.setName(cbcName);
            		controller.setSerialNumber(cbcSerialNumber);
            		controller.setMasterAddress(masterAddress);
            		controller.setSlaveAddress(slaveAddress);
            		controller.setPortId(commChannelId);
            		
            		//controller.setS    scan Enabled?
            		//These two are default settings. Import them?
            		controller.setScanGroup(0);
            		controller.setScanType(DeviceScanRate.TYPE_GENERAL);

            		controller.setIntervalRate(scanInterval);
            		controller.setAlternateRate(altInterval);
            		
            		boolean ret = capControlCreationService.createController(controller);
            		if (ret && StringUtils.isNotBlank(capBankName)) {
            			capControlCreationService.assignController(controller, capBankName);
            		} else if (!ret) {
            			errors.add("Error inserting " + cbcName + " into the database.");
            		}//else its an orphan
            		
            	} catch (UnsupportedOperationException e) {
            		CTILogger.error(e.getMessage());
            		errors.add("Error inserting " + line[1] + " into the database. " + e.getMessage());
            	} catch (DataIntegrityViolationException e) { 
            		CTILogger.error("Insert failed for: " + line[1] + " " + line[2], e );
            		errors.add("Error inserting " + line[1] + " into the database. " + e.getMessage());
            	} finally {
            		line = csvReader.readNext();
            	}
            }
        } finally {
        	csvReader.close();
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
	
	private String checkHeader(String[] file, String[] defaultHeader) {
		
		if (file.length != defaultHeader.length) {
			return "Header count is off. File has " + file.length + "/" + defaultHeader.length + " columns.";
		}
		
		
		List<String> rowList = new ArrayList<String>();
		
		for (String column : file) {
			rowList.add(column.toUpperCase());
		}
		
		List<String> requiredList = Arrays.asList(defaultHeader);
		
		if (!rowList.containsAll(requiredList)) {
			return "Header in file is missing required rows.";
		}
		
		return null;
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
