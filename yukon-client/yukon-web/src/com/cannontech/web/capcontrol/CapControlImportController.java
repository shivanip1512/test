package com.cannontech.web.capcontrol;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.capcontrol.creation.CapControlImporterCbcField;
import com.cannontech.capcontrol.creation.model.CbcImportCompleteDataResult;
import com.cannontech.capcontrol.creation.model.CbcImportData;
import com.cannontech.capcontrol.creation.model.CbcImportResult;
import com.cannontech.capcontrol.creation.model.CbcImportResultType;
import com.cannontech.capcontrol.creation.model.HierarchyImportCompleteDataResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportData;
import com.cannontech.capcontrol.creation.model.HierarchyImportResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportResultType;
import com.cannontech.capcontrol.creation.service.CapControlImportService;
import com.cannontech.capcontrol.dao.CapControlImporterFileDao;
import com.cannontech.capcontrol.exception.CapControlCbcFileImportException;
import com.cannontech.capcontrol.exception.CapControlHierarchyFileImporterException;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/import/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class CapControlImportController {
	private static Logger log = YukonLogManager.getLogger(CapControlImportController.class);

	@Autowired private CapControlImportService capControlImportService;
	@Autowired private CapControlImporterFileDao capControlFileImporterDao;
	
	private Cache<String, List<CapControlImportResolvable>> resultsLookup = CacheBuilder.newBuilder().expireAfterWrite(12, TimeUnit.HOURS).build();
	
	private static Function<CapControlImporterCbcField, String> colNameOfField =
	        new Function<CapControlImporterCbcField, String>() {
        @Override
        public String apply(CapControlImporterCbcField input) {
            return input.getColumnName();
        }
    };
	
	public static class CapControlImportResolvable {
	    
	    private final YukonMessageSourceResolvable message;
	    private final boolean success;
	    
	    private CapControlImportResolvable(YukonMessageSourceResolvable message, boolean success) {
	        this.message = message;
	        this.success = success;
	    }
	    
	    public YukonMessageSourceResolvable getMessage() {
            return message;
        }
	    
	    public boolean isSuccess() {
            return success;
        }
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public String view(String hierarchyKey, String cbcKey, LiteYukonUser user, ModelMap model) throws ExecutionException {
		List<CapControlImportResolvable> results = Lists.newArrayList();
		
		List<CapControlImportResolvable> hierarchyResults = null;
		if (hierarchyKey != null) {
		    hierarchyResults = resultsLookup.getIfPresent(hierarchyKey);
		}
		List<CapControlImportResolvable> cbcResults = null;
		if (cbcKey != null) {
		    cbcResults = resultsLookup.getIfPresent(cbcKey);
		}
		
		if (hierarchyResults != null) {
			results.addAll(hierarchyResults);
		}
		
		if (cbcResults != null) {
			results.addAll(cbcResults);
		}
		
		model.addAttribute("results", results);
		
		return "import/capcontrolImport.jsp";
	}
	
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
    					cbcResults.add(new CbcImportCompleteDataResult(data, CbcImportResultType.INVALID_IMPORT_ACTION));
    					break;
    			}
		    } catch (NotFoundException e) {
		        log.warn(e);
	            cbcResults.add(new CbcImportCompleteDataResult(data, CbcImportResultType.INVALID_PARENT));
		    }
		}
	}
	
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
						hierarchyResults.add(new HierarchyImportCompleteDataResult(data, HierarchyImportResultType.INVALID_IMPORT_ACTION));
						break;
				}
			} catch (NotFoundException e) {
				log.debug("Parent " + data.getParent() + " was not found for hierarchy object " + data.getName() +
						  ". No import occured for this object.");
				hierarchyResults.add(new HierarchyImportCompleteDataResult(data, HierarchyImportResultType.INVALID_PARENT));
			}
		}
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String cbcFile(HttpServletRequest request, LiteYukonUser user, ModelMap model, FlashScope flash) throws IOException {
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
        		Iterable<String> colNames = Iterables.transform(e.getColumns(), colNameOfField);
                String columnString = StringUtils.join(colNames.iterator(), ", ");
        		flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.missingRequiredColumn", columnString));
        		return "import/capcontrolImport.jsp";
        	} catch (IllegalArgumentException e) {
        		log.error("Invalid column name found in import file: " + e.getMessage());
        		flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.invalidColumns", e.getMessage()));
        		return "import/capcontrolImport.jsp";
        	} catch (RuntimeException e) { 
        	    flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.errorProcessingFile", e.getMessage()));
                log.error(e.getMessage());
                return "import/capcontrolImport.jsp";
            } finally {
        		inputStream.close();
        	}
        }
        
        List<CapControlImportResolvable> cbcResults = getCbcResultResolvables(results);
        
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
	public String hierarchyFile(HttpServletRequest request, LiteYukonUser user, ModelMap model, FlashScope flash) throws IOException {
		List<HierarchyImportResult> results = Lists.newArrayList();
		
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
        		return "import/capcontrolImport.jsp";
        	} catch (IllegalArgumentException e) {
        		log.error("Invalid column name found in import file: " + e.getMessage());
        		flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.invalidColumns", e.getMessage()));
        		return "import/capcontrolImport.jsp";
        	} finally {
                inputStream.close();
        	}
        }
        
        List<CapControlImportResolvable> resolvables = getHierarchyResultResolvables(results);

        UUID randomUUID = UUID.randomUUID();
        resultsLookup.put(randomUUID.toString(), resolvables);
        
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
	
	private List<CapControlImportResolvable> getHierarchyResultResolvables(List<HierarchyImportResult> results) {
		List<CapControlImportResolvable> resolvables = Lists.newArrayList();
		
		for (HierarchyImportResult result : results) {	
		    YukonMessageSourceResolvable message = result.getMessage();
		    
		    CapControlImportResolvable resolvable = 
		            new CapControlImportResolvable(message, result.getResultType().isSuccess());
			
			resolvables.add(resolvable);
		}
		
		return resolvables;
	}
	
	private List<CapControlImportResolvable> getCbcResultResolvables(List<CbcImportResult> results) {
		List<CapControlImportResolvable> resolvables = Lists.newArrayList();
		
		for (CbcImportResult result : results) {
		    YukonMessageSourceResolvable message = result.getMessage();
		    
		    CapControlImportResolvable resolvable = 
		            new CapControlImportResolvable(message, result.getResultType().isSuccess());
			
			resolvables.add(resolvable);
		}
		
		return resolvables;
	}

}