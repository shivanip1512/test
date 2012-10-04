package com.cannontech.web.support.database.migration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.cannontech.common.databaseMigration.bean.ExportDatabaseMigrationStatus;
import com.cannontech.common.databaseMigration.bean.ImportDatabaseMigrationStatus;
import com.cannontech.common.databaseMigration.bean.WarningProcessingEnum;
import com.cannontech.common.databaseMigration.model.DatabaseMigrationContainer;
import com.cannontech.common.databaseMigration.model.DisplayableExportType;
import com.cannontech.common.databaseMigration.model.ExportTypeEnum;
import com.cannontech.common.databaseMigration.service.DatabaseMigrationService;
import com.cannontech.common.util.BinaryPrefix;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.PoolManager;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.system.GlobalSetting;
import com.cannontech.system.dao.GlobalSettingsDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;

@CheckRoleProperty(YukonRoleProperty.ADMIN_DATABASE_MIGRATION)
@Controller
@RequestMapping("/database/migration/*")
public class DatabaseMigrationController {

    @Autowired private DatabaseMigrationService databaseMigrationService;
    @Autowired private PoolManager poolManager;
    @Autowired private GlobalSettingsDao globalSettingsDao;

    private Map<String, FileSystemResource> fileStore = new HashMap<String, FileSystemResource>();
	
    private boolean ALLOW_EXPORT_ALL = false;
    
	@RequestMapping
	public String home(HttpServletRequest request, ModelMap map,
    		YukonUserContext userContext, FlashScope flashScope) {
        
        boolean exportTab = ServletRequestUtils.getBooleanParameter(request, "export", false);
        boolean importTab = ServletRequestUtils.getBooleanParameter(request, "import", false);
        
        Set<DisplayableExportType> exportTypeList = 
        	databaseMigrationService.getAvailableExportTypes();
        
        List<String> exportFilePaths = getExportDirectoryFilePaths(userContext);
        
        map.addAttribute("exportTab", exportTab);
        map.addAttribute("importTab", importTab);
        map.addAttribute("exportTypeList", exportTypeList);
        map.addAttribute("serverFiles", exportFilePaths);
        addDbInfoToMav(map);
        
        // recent validations
        List<ImportDatabaseMigrationStatus> allValidationStatuses = 
        	databaseMigrationService.getAllValidationStatuses();
        List<RecentImport> recentValidations = 
        	Lists.newArrayListWithCapacity(allValidationStatuses.size());
        for (ImportDatabaseMigrationStatus status : allValidationStatuses) {
        	recentValidations.add(new RecentImport(status));
        }
        map.addAttribute("recentValidations", recentValidations);
        
        // recent imports
        List<ImportDatabaseMigrationStatus> allImportStatuses = 
        	databaseMigrationService.getAllImportStatuses();
        List<RecentImport> recentImports = Lists.newArrayListWithCapacity(allImportStatuses.size());
        for (ImportDatabaseMigrationStatus status : allImportStatuses) {
        	recentImports.add(new RecentImport(status));
        }
        map.addAttribute("recentImports", recentImports);
        
        map.addAttribute("allowExportAll", ALLOW_EXPORT_ALL);
        
        return "database/migration/home.jsp";
    }
	
	@RequestMapping
    public String export(ModelMap map, String exportType, String exportIds, 
    		YukonUserContext userContext) throws ServletRequestBindingException, IOException {
		
        ExportTypeEnum exportTypeValue = ExportTypeEnum.valueOf(exportType);
        
        List<Integer> exportIdList = Lists.newArrayList();
        
        if (StringUtils.isBlank(exportIds)) {
        	if (!ALLOW_EXPORT_ALL) {
        		throw new IllegalArgumentException("No Items Selected");
        	}
            
        	exportIdList = databaseMigrationService.getAllSearchIds(exportTypeValue);
        	
        } else {
        	exportIdList = ServletUtil.getIntegerListFromString(exportIds);
        }
        
        ExportDatabaseMigrationStatus status = 
            databaseMigrationService.processExportDatabaseMigration(exportTypeValue, exportIdList, userContext);

        File exportFile = status.getExportFile();
        FileSystemResource resource = new FileSystemResource(exportFile);
        fileStore.put(status.getId(), resource);

        map.addAttribute("statusKey", status.getId());

        return "redirect:exportProgress";
    }
	
	@RequestMapping
    public String exportProgress(ModelMap map, String statusKey) 
		throws ServletRequestBindingException {
	
		ExportDatabaseMigrationStatus migrationStatus = databaseMigrationService.getExportStatus(statusKey);
        map.addAttribute("migrationStatus", migrationStatus);
        
        addDbInfoToMav(map);
        
        return "database/migration/exportProgress.jsp";
	}
	
	@RequestMapping
    public void downloadExportFile(HttpServletResponse response, String fileKey) 
		throws ServletException, IOException {

		FileSystemResource resource = fileStore.get(fileKey);
		
		response.setContentType("text/xml");
        String safeName = ServletUtil.makeWindowsSafeFileName(resource.getFilename());
		response.setHeader("Content-Disposition","attachment; filename=\"" + safeName + "\"");
        
        FileCopyUtils.copy(resource.getInputStream(), response.getOutputStream());
	}
	
	@RequestMapping
    public String viewSelectedItems(ModelMap map, String exportType, String exportItemIds, 
    		YukonUserContext userContext) throws ServletException {

        ExportTypeEnum exportTypeValue = ExportTypeEnum.valueOf(exportType);

        List<Integer> exportItemIdList = ServletUtil.getIntegerListFromString(exportItemIds);
        List<DatabaseMigrationContainer> exportItems = 
            databaseMigrationService.getItemsByIds(exportTypeValue, exportItemIdList, userContext);
        
        List<String> itemLabelList = Lists.newArrayList();
        for(DatabaseMigrationContainer container : exportItems) {
            itemLabelList.add(container.getDatabaseMigrationDisplay());
        }
        
        map.addAttribute("itemList", itemLabelList);
        map.addAttribute("objectCount", itemLabelList.size());
        
        return "database/migration/popup/objectsViewPopup.jsp";
    }
	
	@RequestMapping
    public String loadLocalFile(HttpServletRequest request, ModelMap map, 
    		@RequestParam MultipartFile dataFile, YukonUserContext userContext, FlashScope flashScope) {
		
		File importFile = null;
		String errorPrefix = "yukon.web.modules.support.databaseMigration.loadImport.loadFile.error"; 
		String loadErrorSuffix = null;
		
		if(ServletFileUpload.isMultipartContent(request)) {
            try {
                if (dataFile == null || StringUtils.isBlank(dataFile.getOriginalFilename())) {
                	loadErrorSuffix = ".noFile";
                } 
                else {
                    InputStream inputStream = dataFile.getInputStream();
                    if (inputStream.available() <= 0) {
                    	loadErrorSuffix = ".emptyFile";
                    } else {	// No loadErrors so far, get importFile
                    	importFile = WebFileUtils.convertToTempFile(dataFile, dataFile.getOriginalFilename() + "_", "");
                    }
                }
            } catch (IOException e) {
            	loadErrorSuffix = ".noFile";
            }
        }
        else {
        	loadErrorSuffix = ".noFile";
        }
        
		// error
		if (loadErrorSuffix != null) {
			map.addAttribute("import", true);
			flashScope.setError(new YukonMessageSourceResolvable(errorPrefix + loadErrorSuffix));
			return "redirect:home";
		}

		if (importFile == null) {
			throw new IllegalStateException("No file set");
		}
		
		// run validation
		ImportDatabaseMigrationStatus importDatabaseMigrationStatus = 
			databaseMigrationService.validateImportFile(importFile, userContext);
		
		// store
		FileSystemResource resource = new FileSystemResource(importFile);
		fileStore.put(importDatabaseMigrationStatus.getId(), resource);
		
		// import progress
		map.addAttribute("statusKey", importDatabaseMigrationStatus.getId());
		
        return "redirect:validationProgress";
    }
	
	@RequestMapping
    public String loadServerFile(ModelMap map, String dataFilePath, YukonUserContext userContext, FlashScope flashScope) 
		throws ServletException {
		
		// Was a valid file selected?
	    if(dataFilePath == null) {
	        map.addAttribute("import", true);  
            flashScope.setError(new YukonMessageSourceResolvable(
                    "yukon.web.modules.support.databaseMigration.loadImport.loadFile.error.noServerFile"));
            return "redirect:home";
	    }
	    // retrieve file
		File importFile = new File(dataFilePath);
		if (!importFile.exists()) {
			map.addAttribute("import", true);
			flashScope.setError(new YukonMessageSourceResolvable(
			        "yukon.web.modules.support.databaseMigration.loadImport.loadFile.error.doesNotExist"));
			return "redirect:home";
		}
		
		// run validation
		ImportDatabaseMigrationStatus importDatabaseMigrationStatus = 
			databaseMigrationService.validateImportFile(importFile, userContext);
		
		// store
		FileSystemResource resource = new FileSystemResource(importFile);
		fileStore.put(importDatabaseMigrationStatus.getId(), resource);
		
		// import progress
		map.addAttribute("statusKey", importDatabaseMigrationStatus.getId());
		
		return "redirect:validationProgress";
	}
	
	@RequestMapping
    public String validationProgress(ModelMap map, String statusKey) 
		throws ServletRequestBindingException {
	
		ImportDatabaseMigrationStatus status = databaseMigrationService.getValidationStatus(statusKey);
        map.addAttribute("status", status);
        
        return "database/migration/validationProgress.jsp";
	}
	
	@RequestMapping
    public String importValidate(ModelMap map, String statusKey) throws ServletRequestBindingException {
	
		ImportDatabaseMigrationStatus status = databaseMigrationService.getValidationStatus(statusKey);
		FileSystemResource resource = fileStore.get(statusKey);
		
		String fileName = resource.getFilename();
		String orgEnvironment = "Not Available";
		String orgSchemaUser = "Not Available";
		try {
			String[] split = StringUtils.split(fileName, "_");
			orgEnvironment = split[0];
			orgSchemaUser = split[1];
		} catch (IndexOutOfBoundsException e) {
			// pass
		}
		
		map.addAttribute("status", status);
		map.addAttribute("filePath", resource.getPath());
		map.addAttribute("fileSize", BinaryPrefix.getCompactRepresentation(resource.getFile().length()));
		map.addAttribute("orgDbUrl", orgEnvironment);
		map.addAttribute("orgDbUsername", orgSchemaUser);
		map.addAttribute("exportType", status.getExportType());
        
        addDbInfoToMav(map);
        
        return "database/migration/importValidate.jsp";
	}
	
	@RequestMapping
	public String importConfirm(ModelMap map,
								String fileKey,
								String warningProcessingValue,
								YukonUserContext userContext) throws ServletException {
		
        WarningProcessingEnum warningProcessing = 
        	WarningProcessingEnum.valueOf(warningProcessingValue);
        
        // retrieve file
        FileSystemResource fileSystemResource = fileStore.get(fileKey);
        File importFile = fileSystemResource.getFile();
        ImportDatabaseMigrationStatus importDatabaseMigrationStatus = 
        	databaseMigrationService.processImportDatabaseMigration(importFile, 
        															warningProcessing, 
        															userContext);
        
		map.addAttribute("statusKey", importDatabaseMigrationStatus.getId());
        
		return "redirect:importProgress";
	}
	
	@RequestMapping
    public String importProgress(ModelMap map, String statusKey) throws ServletRequestBindingException {
	
		ImportDatabaseMigrationStatus status = databaseMigrationService.getImportStatus(statusKey);
        map.addAttribute("status", status);
        
        return "database/migration/importProgress.jsp";
	}
	
	@RequestMapping
    public String objectsViewPopup(ModelMap map, String fileKey) throws ServletException {
		ImportDatabaseMigrationStatus importDatabaseMigrationStatus = 
			databaseMigrationService.getValidationStatus(fileKey);
		List<String> labelList = importDatabaseMigrationStatus.getLabelList();
		
		map.addAttribute("itemList", labelList);
		map.addAttribute("objectCount", labelList.size());
		return "database/migration/popup/objectsViewPopup.jsp";
	}
	
	@RequestMapping
    public String warningsViewPopup(ModelMap map, String fileKey) throws ServletException {
        ImportDatabaseMigrationStatus importDatabaseMigrationStatus = 
        	databaseMigrationService.getValidationStatus(fileKey);
        Map<String, Set<String>> warningListMap = importDatabaseMigrationStatus.getWarningsMap();

        map.addAttribute("warningListMap", warningListMap);
        
        int objectCount = 0;
        for (String key : warningListMap.keySet()) {
        	objectCount += warningListMap.get(key).size();
        }
        map.addAttribute("objectCount", objectCount);
        
		return "database/migration/popup/warningsViewPopup.jsp";
	}
	
	@RequestMapping
    public String errorsViewPopup(ModelMap map, String fileKey) throws ServletException {
	    ImportDatabaseMigrationStatus importDatabaseMigrationStatus = 
	    	databaseMigrationService.getValidationStatus(fileKey);
	    Map<String, Set<String>> errorListMap = importDatabaseMigrationStatus.getErrorsMap();

	    map.addAttribute("errorListMap", errorListMap);
	    
	    int objectCount = 0;
        for (String key : errorListMap.keySet()) {
        	objectCount += errorListMap.get(key).size();
        }
        map.addAttribute("objectCount", objectCount);
        
	    return "database/migration/popup/errorsViewPopup.jsp";
	}
	
	// HELPERS
	private void addDbInfoToMav(ModelMap map) {
		map.addAttribute("dbUrl", poolManager.getPrimaryUrl());
		map.addAttribute("dbUsername", poolManager.getPrimaryUser());
	}

	/**
	 * Helper method to find all of the file exports on the server and return the paths of those files
	 */
	private List<String> getExportDirectoryFilePaths(YukonUserContext userContext) {
	    List<String> exportFilePaths = new ArrayList<String>();
	    
	    String rolePropertyPath = 
	            globalSettingsDao.getString(GlobalSetting.DATABASE_MIGRATION_FILE_LOCATION);
	    File exportDir = new File(CtiUtilities.getYukonBase(), rolePropertyPath);
	    
	    if (exportDir == null ||
	        !exportDir.isDirectory()) {
	        return null;
	    }

	    for (File file : exportDir.listFiles()) {
            if(file.getAbsolutePath().endsWith(".xml")){
                exportFilePaths.add(file.getAbsolutePath());
            }
        }
	    
	    return exportFilePaths;
	}
	
	public class RecentImport {
		
		public RecentImport(ImportDatabaseMigrationStatus status) {
		
			this.status = status;
			FileSystemResource resource = new FileSystemResource(status.getImportFile());
			this.filePath = resource.getPath();
		}
		
		private ImportDatabaseMigrationStatus status;
		private String filePath;
		
		public ImportDatabaseMigrationStatus getStatus() {
			return status;
		}
		public String getFilePath() {
			return filePath;
		}
	}
	
}
