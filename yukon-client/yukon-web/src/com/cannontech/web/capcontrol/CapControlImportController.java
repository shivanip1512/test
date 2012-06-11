package com.cannontech.web.capcontrol;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
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
import com.cannontech.capcontrol.creation.service.RegulatorImportService;
import com.cannontech.capcontrol.creation.service.RegulatorPointMappingImportService;
import com.cannontech.capcontrol.dao.CapControlImporterFileDao;
import com.cannontech.capcontrol.exception.CapControlCbcFileImportException;
import com.cannontech.capcontrol.exception.CapControlHierarchyFileImporterException;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.csvImport.ImportData;
import com.cannontech.common.csvImport.ImportFileFormat;
import com.cannontech.common.csvImport.ImportFileValidator;
import com.cannontech.common.csvImport.ImportParser;
import com.cannontech.common.csvImport.ImportResult;
import com.cannontech.common.exception.DuplicateColumnNameException;
import com.cannontech.common.exception.InvalidColumnNameException;
import com.cannontech.common.exception.RequiredColumnMissingException;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.tools.csv.CSVReader;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.exceptions.EmptyImportFileException;
import com.cannontech.web.exceptions.NoImportFileException;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/import/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_IMPORTER)
public class CapControlImportController {
	private static Logger log = YukonLogManager.getLogger(CapControlImportController.class);

	@Autowired private CapControlImportService capControlImportService;
	@Autowired private CapControlImporterFileDao capControlFileImporterDao;
	@Autowired private RegulatorImportService regulatorImportService;
	@Autowired private RegulatorPointMappingImportService regulatorPointMappingImportService;
	
	private Cache<String, List<ImportResult>> resultsLookup = CacheBuilder.newBuilder().expireAfterWrite(12, TimeUnit.HOURS).build();
	
	private static Function<CapControlImporterCbcField, String> colNameOfField =
	        new Function<CapControlImporterCbcField, String>() {
        @Override
        public String apply(CapControlImporterCbcField input) {
            return input.getColumnName();
        }
    };
	
    private static enum ImportType implements DisplayableEnum {
        CBC,
        HIERARCHY,
        REGULATOR,
        POINT_MAPPING,
        ;
        
        public String getFormatKey() {
            return "yukon.web.modules.capcontrol.import.importTypes." + name();
        }
    }
    
	public static class CapControlImportResolvable implements ImportResult {
	    
	    private final YukonMessageSourceResolvable message;
	    private final boolean success;
	    
	    private CapControlImportResolvable(YukonMessageSourceResolvable message, boolean success) {
	        this.message = message;
	        this.success = success;
	    }
	    
	    public YukonMessageSourceResolvable getImportResultMessage() {
            return message;
        }
	    
	    public boolean isSuccess() {
            return success;
        }
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public String view(String cacheKey, LiteYukonUser user, ModelMap model) throws ExecutionException {
	    List<ImportResult> results = Lists.newArrayList();
		
		if (cacheKey != null) {
		    results = resultsLookup.getIfPresent(cacheKey);
		}
		
		model.addAttribute("results", results);
		model.addAttribute("importTypes", ImportType.values());
		
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
        	return "redirect:view";
        } else {
        	try {
	        	List<CbcImportData> cbcImportData = capControlFileImporterDao.getCbcImportData(inputStream, results);
	        	
	    		processCbcImport(cbcImportData, results);
        	} catch (CapControlCbcFileImportException e) {
        		log.error(e.getMessage());
        		Iterable<String> colNames = Iterables.transform(e.getColumns(), colNameOfField);
                String columnString = StringUtils.join(colNames.iterator(), ", ");
        		flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.missingRequiredColumn", columnString));
        		return "redirect:view";
        	} catch (IllegalArgumentException e) {
        		log.error("Invalid column name found in import file: " + e.getMessage());
        		flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.invalidColumns", e.getMessage()));
        		return "redirect:view";
        	} catch (RuntimeException e) { 
        	    flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.errorProcessingFile", e.getMessage()));
                log.error(e.getMessage());
                return "redirect:view";
            } finally {
        		inputStream.close();
        	}
        }
        
        List<ImportResult> cbcResults = getCbcResultResolvables(results);
        
        UUID randomUUID = UUID.randomUUID();
        resultsLookup.put(randomUUID.toString(), cbcResults);
        
        model.addAttribute("cacheKey", randomUUID.toString());
        
        for (CbcImportResult result : results) {
            if (!result.getResultType().isSuccess()) {
                flash.setWarning(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.processedWithErrors"));
                return "redirect:view";
            }
        }
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.importCbcFileSuccess"));
        
        return "redirect:view";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String regulatorFile(ModelMap model, HttpServletRequest request, FlashScope flashScope) throws IOException {
	    //Procure the import file
        if(!ServletFileUpload.isMultipartContent(request)) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.noImportFile"));
            return "redirect:view";
        }
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile dataFile = multipartRequest.getFile("dataFile");
	    
        //Set up a reader for the file
        CSVReader csvReader = null;
        try {
            csvReader = WebFileUtils.getTempBackedCsvReaderFromMultipartFile(dataFile);
        } catch(NoImportFileException e) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.noImportFile"));
            return "redirect:view";
        } catch(EmptyImportFileException e) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.errorProcessingFile"));
            return "redirect:view";
        }
        
        ImportFileFormat importFormat = regulatorImportService.getFormat();
        ImportParser parser = new ImportParser(importFormat);
        ImportData data = parser.parseFromCsvReader(csvReader);
        csvReader.close();
        
        try {
             ImportFileValidator.validateFileStructure(data);
	    } catch(DuplicateColumnNameException e) {
	         flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.duplicateColumns", e.getJoinedDuplicateColumnNames()));
	         return "redirect:view";
	    } catch(InvalidColumnNameException e) {
	         flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.invalidColumns", e.getJoinedInvalidColumnNames()));
             return "redirect:view";
	    } catch(RequiredColumnMissingException e) {
	         flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.missingRequiredColumn", e.getJoinedMissingColumnNames()));
             return "redirect:view";
	    }
	    
	    List<ImportResult> results = regulatorImportService.startImport(data);
	    
	    String resultId = UUID.randomUUID().toString();
	    resultsLookup.put(resultId, results);
	    
	    model.addAttribute("cacheKey", resultId);
	    
        return "redirect:view";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String pointmappingFile(ModelMap model, HttpServletRequest request, FlashScope flashScope) throws IOException {
	    //Procure the import file
        if(!ServletFileUpload.isMultipartContent(request)) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.import.error.noImportFile"));
            return "redirect:view";
        }
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile dataFile = multipartRequest.getFile("dataFile");
        
        //Set up a reader for the file
        CSVReader csvReader = null;
        try {
            csvReader = WebFileUtils.getTempBackedCsvReaderFromMultipartFile(dataFile);
        } catch(NoImportFileException e) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.import.error.noImportFile"));
            return "redirect:view";
        } catch(EmptyImportFileException e) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.errorProcessingFile"));
            return "redirect:view";
        }
        
        ImportFileFormat importFormat = regulatorPointMappingImportService.getFormat();
        ImportParser parser = new ImportParser(importFormat);
        ImportData data = parser.parseFromCsvReader(csvReader);
        csvReader.close();
        
        try {
            ImportFileValidator.validateFileStructure(data);
        } catch(DuplicateColumnNameException e) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.duplicateColumns", e.getJoinedDuplicateColumnNames()));
            return "redirect:view";
        } catch(InvalidColumnNameException e) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.invalidColumns", e.getJoinedInvalidColumnNames()));
            return "redirect:view";
        } catch(RequiredColumnMissingException e) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.missingRequiredColumn", e.getJoinedMissingColumnNames()));
            return "redirect:view";
        }
        
        List<ImportResult> results = regulatorPointMappingImportService.startImport(data);
        
        String resultId = UUID.randomUUID().toString();
        resultsLookup.put(resultId, results);
        
        model.addAttribute("cacheKey", resultId);
        
        return "redirect:view";
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
        	return "redirect:view";
        } else {
        	try {
        		List<HierarchyImportData> hierarchyImportData = capControlFileImporterDao.getHierarchyImportData(inputStream, results);
        	
        		processHierarchyImport(hierarchyImportData, results);
        	} catch (CapControlHierarchyFileImporterException e) {
        		log.error(e.getMessage());
        		flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.missingRequiredColumn", e.getColumns()));
        		return "redirect:view";
        	} catch (IllegalArgumentException e) {
        		log.error("Invalid column name found in import file: " + e.getMessage());
        		flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.invalidColumns", e.getMessage()));
        		return "redirect:view";
        	} finally {
                inputStream.close();
        	}
        }
        
        List<ImportResult> resolvables = getHierarchyResultResolvables(results);

        UUID randomUUID = UUID.randomUUID();
        resultsLookup.put(randomUUID.toString(), resolvables);
        
		model.addAttribute("cacheKey", randomUUID.toString());
        
		for (HierarchyImportResult result : results) {
		    if (!result.getResultType().isSuccess()) {
		        flash.setWarning(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.processedWithErrors"));
		        return "redirect:view";
		    }
		}

        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.import.importHierarchyFileSuccess"));
        return "redirect:view";
	}
	
	private List<ImportResult> getHierarchyResultResolvables(List<HierarchyImportResult> results) {
		List<ImportResult> resolvables = Lists.newArrayList();
		
		for (HierarchyImportResult result : results) {	
		    YukonMessageSourceResolvable message = result.getMessage();
		    
		    CapControlImportResolvable resolvable = 
		            new CapControlImportResolvable(message, result.getResultType().isSuccess());
			
			resolvables.add(resolvable);
		}
		
		return resolvables;
	}
	
	private List<ImportResult> getCbcResultResolvables(List<CbcImportResult> results) {
		List<ImportResult> resolvables = Lists.newArrayList();
		
		for (CbcImportResult result : results) {
		    YukonMessageSourceResolvable message = result.getMessage();
		    
		    CapControlImportResolvable resolvable = 
		            new CapControlImportResolvable(message, result.getResultType().isSuccess());
			
			resolvables.add(resolvable);
		}
		
		return resolvables;
	}

}