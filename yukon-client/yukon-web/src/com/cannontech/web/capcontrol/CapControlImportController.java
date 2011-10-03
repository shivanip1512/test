package com.cannontech.web.capcontrol;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.capcontrol.creation.model.CbcImportData;
import com.cannontech.capcontrol.creation.model.CbcImportResult;
import com.cannontech.capcontrol.creation.model.CbcImportResultTypesEnum;
import com.cannontech.capcontrol.creation.model.HierarchyImportData;
import com.cannontech.capcontrol.creation.model.HierarchyImportResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportResultTypesEnum;
import com.cannontech.capcontrol.creation.model.ImportActionsEnum;
import com.cannontech.capcontrol.creation.service.CapControlImportService;
import com.cannontech.capcontrol.dao.CapControlImporterFileDao;
import com.cannontech.capcontrol.dao.impl.CapControlImporterFileDaoImpl;
import com.cannontech.capcontrol.exception.CapControlCbcFileImportException;
import com.cannontech.capcontrol.exception.CapControlHierarchyFileImporterException;
import com.cannontech.capcontrol.exception.CapControlImportException;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;

@Controller
@RequestMapping("/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class CapControlImportController {
	private static Logger log = YukonLogManager.getLogger(CapControlImportController.class);

	private CapControlImportService capControlImportService;
	private CapControlImporterFileDao capControlFileImporterDao;
	
	private ConcurrentMap<String, List<String>> resultsLookup = new MapMaker().expiration(12, TimeUnit.HOURS).makeMap();
	
	@RequestMapping(method=RequestMethod.GET)
	public String importer(String hierarchyKey, String cbcKey, LiteYukonUser user, ModelMap model) {
		List<String> results = Lists.newArrayList();
		
		List<String> hierarchyResults = null;
		if (hierarchyKey != null) {
		    hierarchyResults = resultsLookup.get(hierarchyKey);
		}
		List<String> cbcResults = null;
		if (cbcKey != null) {
		    cbcResults = resultsLookup.get(cbcKey);
		}
		
		if (hierarchyResults != null) {
			results.addAll(hierarchyResults);
		}
		
		if (cbcResults != null) {
			results.addAll(cbcResults);
		}
		
		model.addAttribute("results", results);
		
		return "tools/capcontrolImport.jsp";
	}
	
	@Transactional
	private void processCbcImport(List<CbcImportData> cbcImportData, List<CbcImportResult> cbcResults) {
		for (CbcImportData data : cbcImportData) {
		    try{
    			switch(data.getImportAction()) {
    				case ADD:
    					if (data.isTemplate()) {
    						capControlImportService.createCbcFromTemplate(data, cbcResults);
    					} else {
    						capControlImportService.createCbc(data, cbcResults);
    					}
    					break;
    					
    				case UPDATE:
    					capControlImportService.updateCbc(data, cbcResults);
    					break;
    					
    				case REMOVE:
    					capControlImportService.removeCbc(data, cbcResults);
    					break;
    					
    				default:
    					cbcResults.add(new CbcImportResult(data, CbcImportResultTypesEnum.INVALID_IMPORT_ACTION));
    					break;
    			}
		    } catch (NotFoundException e) {
		        log.debug(e);
	            cbcResults.add(new CbcImportResult(data, CbcImportResultTypesEnum.INVALID_PARENT));
		    }
		}
	}
	
	@Transactional
	private void processHierarchyImport(List<HierarchyImportData> hierarchyImportData, 
										List<HierarchyImportResult> hierarchyResults) {
		for (HierarchyImportData data : hierarchyImportData) {
			try {
				switch(data.getImportAction()) {
					case ADD:
						capControlImportService.createHierarchyObject(data, hierarchyResults);
						break;
						
					case UPDATE:
						capControlImportService.updateHierarchyObject(data, hierarchyResults);
						break;
						
					case REMOVE:
						capControlImportService.removeHierarchyObject(data, hierarchyResults);
						break;
					
					default:
						hierarchyResults.add(new HierarchyImportResult(data, HierarchyImportResultTypesEnum.INVALID_IMPORT_ACTION));
						break;
				}
			} catch (NotFoundException e) {
				log.debug("Parent " + data.getParent() + " was not found for hierarchy object " + data.getName() +
						  ". No import occured for this object.");
				hierarchyResults.add(new HierarchyImportResult(data, HierarchyImportResultTypesEnum.INVALID_PARENT));
			}
		}
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String importCbcFile(HttpServletRequest request, LiteYukonUser user, ModelMap model, FlashScope flash) throws IOException {
		List<CbcImportResult> results = new ArrayList<CbcImportResult>();
		
		MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest)request;
        MultipartFile dataFile = mRequest.getFile("dataFile");
        InputStream inputStream = dataFile.getInputStream();
        
        if (inputStream.available() <= 0) {
            log.error("Cap Control CBC Import File is empty.");
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.cbcFileEmpty"));
        	return "redirect:importer";
        } else {
        	try {
	        	List<CbcImportData> cbcImportData = capControlFileImporterDao.getCbcImportData(inputStream, results);
	        	
	    		processCbcImport(cbcImportData, results);
        	} catch (CapControlCbcFileImportException e) {
        		log.error(e.getMessage());
        		flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.missingRequiredColumn", e.getColumns()));
        		return "tools/capcontrolImport.jsp";
        	} catch (CapControlImportException e) { 
        		log.error(e.getMessage());
        	} catch (IllegalArgumentException e) {
        		log.error("Invalid column name found in import file: " + e.getMessage());
        		flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.invalidColumns", e.getMessage()));
        		return "tools/capcontrolImport.jsp";
        	} finally {
        		inputStream.close();
        	}
        }
        
        List<String> cbcResults = getCbcResultStrings(results);
        
        UUID randomUUID = UUID.randomUUID();
        resultsLookup.put(randomUUID.toString(), cbcResults);
        
        model.addAttribute("cbcKey", randomUUID.toString());
        
        for (CbcImportResult result : results) {
            if (!result.getResultType().isSuccess()) {
                flash.setWarning(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.processedWithErrors"));
                return "redirect:importer";
            }
        }
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.importCbcFileSuccess"));
        
        return "redirect:importer";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String importHierarchyFile(HttpServletRequest request, LiteYukonUser user, ModelMap model, FlashScope flash) throws IOException {
		List<HierarchyImportResult> results = new ArrayList<HierarchyImportResult>();
		
		MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest)request;
        MultipartFile dataFile = mRequest.getFile("dataFile");
        InputStream inputStream = dataFile.getInputStream();
        
        if (inputStream.available() <= 0) {
        	log.error("Cap Control Hierarchy Import File is empty.");
        	flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.hierarchyFileEmpty"));
        	return "redirect:importer";
        } else {
        	try {
        		List<HierarchyImportData> hierarchyImportData = capControlFileImporterDao.getHierarchyImportData(inputStream, results);
        	
        		processHierarchyImport(hierarchyImportData, results);
        	} catch (CapControlHierarchyFileImporterException e) {
        		log.error(e.getMessage());
        		flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.missingRequiredColumn", e.getColumns()));
        		return "tools/capcontrolImport.jsp";
        	} catch (IllegalArgumentException e) {
        		log.error("Invalid column name found in import file: " + e.getMessage());
        		flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.invalidColumns", e.getMessage()));
        		return "tools/capcontrolImport.jsp";
        	} finally {
                inputStream.close();
        	}
        }
        
        List<String> hierarchyResults = getHierarchyResultStrings(results);

        UUID randomUUID = UUID.randomUUID();
        resultsLookup.put(randomUUID.toString(), hierarchyResults);
        
		model.addAttribute("hierarchyKey", randomUUID.toString());

        
		for (HierarchyImportResult result : results) {
		    if (!result.getResultType().isSuccess()) {
		        flash.setWarning(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.processedWithErrors"));
		        return "redirect:importer";
		    }
		}

        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.importHierarchyFileSuccess"));
        return "redirect:importer";
	}
	
	private List<String> getHierarchyResultStrings(List<HierarchyImportResult> results) {
		List<String> resultStrings = Lists.newArrayList();
		
		for (HierarchyImportResult result : results) {
			HierarchyImportResultTypesEnum resultType = result.getResultType();
			String resultString = resultType.getDbString();
			
			if (result.getHierarchyImportData() != null) {
				String detail = getHierarchyResultDetail(result);
				resultString += " - " + detail;
			}
			
			resultStrings.add(resultString);
		}
		
		return resultStrings;
	}
	
	private String getHierarchyResultDetail(HierarchyImportResult result) {
		ImportActionsEnum action = result.getHierarchyImportData().getImportAction();
		String name = result.getHierarchyImportData().getName();
		String outcome = result.getResultType().isSuccess() ? "succeeded" : "failed";
		String actionString = (action != null) ? action.getDbString() : "Import"; 
		
		String resultString = actionString + " " + outcome + " for object named \"" + name + "\".";
		
		return resultString;
	}
	
	private List<String> getCbcResultStrings(List<CbcImportResult> results) {
		List<String> resultStrings = Lists.newArrayList();
		
		for (CbcImportResult result : results) {
			CbcImportResultTypesEnum resultType = result.getResultType();
			String resultString = resultType.getDbString();
			
			if (result.getCbcImportData() != null) {
				String detail = getCbcResultDetail(result);
				resultString += " - " + detail;
			}
			
			resultStrings.add(resultString);
		}
		
		return resultStrings;
	}
	
	private String getCbcResultDetail(CbcImportResult result) {
		ImportActionsEnum action = result.getCbcImportData().getImportAction();
		String name = result.getCbcImportData().getCbcName();
		String outcome = result.getResultType().isSuccess() ? "succeeded" : "failed";
		String actionString = (action != null) ? action.getDbString() : "Import"; 
		
		String resultString = actionString + " " + outcome + " for CBC named \"" + name + "\".";
	
		return resultString;
	}
	
	@Autowired
	public void setCapControlImportService(CapControlImportService capControlImportService) {
		this.capControlImportService = capControlImportService;
	}
	
	@Autowired
	public void setCapControlFileImporterDao(CapControlImporterFileDaoImpl capControlFileImporterDao) {
		this.capControlFileImporterDao = capControlFileImporterDao;
	}
}
