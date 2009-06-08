package com.cannontech.web.capcontrol;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

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
import com.cannontech.cbc.model.Feeder;
import com.cannontech.cbc.model.Substation;
import com.cannontech.cbc.model.SubstationBus;
import com.cannontech.cbc.service.CapControlCreationService;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.tools.csv.CSVReader;
import com.cannontech.web.security.annotation.CheckRoleProperty;

import com.cannontech.database.data.pao.CapControlType;

@Controller
@RequestMapping("/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class CapControlImportController {

	private CapControlCreationService capControlCreationService;
	
	@RequestMapping
	public String importer(HttpServletRequest request, LiteYukonUser user, ModelMap model) {
	
		String error = ServletRequestUtils.getStringParameter(request, "error", null);
		boolean success = ServletRequestUtils.getBooleanParameter(request, "success", false);
		
		model.addAttribute("error", error);
		model.addAttribute("success", success);
		
		return "tools/capcontrolImport.jsp";
	}
	
	@RequestMapping
	public String importFile(HttpServletRequest request, LiteYukonUser user, ModelMap model) throws ServletException, IOException {
		String error = null;
		
		MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest)request;
        MultipartFile dataFile = mRequest.getFile("dataFile");
        
        // get file from request
        if (dataFile == null || StringUtils.isBlank(dataFile.getOriginalFilename())) {
        	error = "No file selected.";
        } else {
        	
            InputStream inputStream = dataFile.getInputStream();
            if (inputStream.available() <= 0) {
            	error = "File is empty.";
            } else {
	            InputStreamReader inputStreamReader = new InputStreamReader(dataFile.getInputStream());
	            CSVReader csvReader = new CSVReader(inputStreamReader);
	            try{
		            String[] headerRow = csvReader.readNext();
	
		            //Error Checking make sure the columns are there.
		            error = checkHeaderRow(headerRow);
		            if (error == null) {
			            String[] line = csvReader.readNext();
			            
			            while(line != null) {
			            	CapControlType type = CapControlType.getCapControlType(line[0]);
			            	String name = line[1];
			            	String parent = line[2];
			            	
			            	//validate input
			            	
			            	try{
				            	switch(type) {
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
					            		if (ret && !"".equalsIgnoreCase(parent)) {
					            			capControlCreationService.assignSubstation(substation.getId(), parent);
					            		}
					            		break;
					            	}
					            	case SUBBUS: {
					            		SubstationBus subBus = new SubstationBus();
					            		subBus.setName(name);
					            		
					            		boolean ret = capControlCreationService.createSubstationBus(subBus);
					            		if (ret && !"".equalsIgnoreCase(parent)) {
					            			capControlCreationService.assignSubstationBus(subBus.getId(), parent);
					            		}
					            		break;
					            	}
					            	case FEEDER: {
					            		Feeder feeder = new Feeder();
					            		feeder.setName(name);
					            		
					            		boolean ret = capControlCreationService.createFeeder(feeder);
					            		if (ret && !"".equalsIgnoreCase(parent)) {
					            			capControlCreationService.assignFeeder(feeder.getId(), parent);
					            		}
					            		break;
					            	}
					            	case CAPBANK: {
					            		throw new UnsupportedOperationException();
					            		/*
					            		Capbank bank = new Capbank();
					            		bank.setName(name);
					            		
					            		boolean ret = capControlCreationService.createCapbank(bank);
					            		if (ret && !"".equalsIgnoreCase(parent)) {
					            			capControlCreationService.assignCapbank(bank.getId(), parent);
					            		}
					            		break;
					            		*/
					            	}
					            	case CBC: {
					            		throw new UnsupportedOperationException();
					            	}
					            	default: {
					            		throw new UnsupportedOperationException();
					            	}
				            	}
				            	CTILogger.info("Successfully added or updated Cap Control Object type: " + type + " Name: " + name);
				            	
			            	} catch(UnsupportedOperationException e) {
			            		CTILogger.info("Unkown Cap Control object, insert failed for: " + type + " " + name );
			            	} catch(DataIntegrityViolationException e) { 
			            		CTILogger.info("Bad Data for Cap Control object, insert failed for: " + type + " " + name, e );
			            	} finally {
			            		line = csvReader.readNext();
			            	}
			            }
		            }
	            } finally {
	            	csvReader.close();
	            }
            }
        }
        
        model.addAttribute("error", error);
        model.addAttribute("success", error == null);

        return "redirect:importer";
	}
	
	private String checkHeaderRow(String[] rows) {
		
		if (rows.length != 3) {
			return "Header must have all three rows. TYPE, NAME, and PARENT";
		}
		
		List<String> rowList = Arrays.asList(rows);
		List<String> requiredList = Arrays.asList(new String []{"TYPE","NAME","PARENT"});
		
		if (!rowList.containsAll(requiredList)) {
			return "Header must have all three rows. TYPE, NAME, and PARENT";
		}
		
		return null;
	}

	@Autowired
	public void setCapControlCreationService(
			CapControlCreationService capControlCreationService) {
		this.capControlCreationService = capControlCreationService;
	}
}
