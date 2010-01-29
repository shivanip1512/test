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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.databaseMigration.bean.ExportDatabaseMigrationStatus;
import com.cannontech.common.databaseMigration.bean.ImportDatabaseMigrationStatus;
import com.cannontech.common.databaseMigration.bean.WarningProcessingEnum;
import com.cannontech.common.databaseMigration.model.DatabaseMigrationContainer;
import com.cannontech.common.databaseMigration.model.DisplayableExportType;
import com.cannontech.common.databaseMigration.model.ExportTypeEnum;
import com.cannontech.common.databaseMigration.service.DatabaseMigrationService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.PoolManager;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;

@CheckRoleProperty(YukonRoleProperty.ADMIN_DATABASE_MIGRATION)
@Controller
@RequestMapping("/database/migration/*")
public class DatabaseMigrationController {

    private DatabaseMigrationService databaseMigrationService;
	private PoolManager poolManager;
	private RolePropertyDao rolePropertyDao;
	private YukonUserContextMessageSourceResolver messageSourceResolver;

    private Map<String, FileSystemResource> fileStore = new HashMap<String, FileSystemResource>();
	
    private boolean ALLOW_EXPORT_ALL = false;
    
    
	// HOME
	@RequestMapping
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) {
        
        ModelAndView mav = new ModelAndView("database/migration/home.jsp");
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        String errorMsg = ServletRequestUtils.getStringParameter(request, "errorMsg", null);
        boolean exportTab = ServletRequestUtils.getBooleanParameter(request, "export", false);
        boolean importTab = ServletRequestUtils.getBooleanParameter(request, "import", false);
        
        Set<DisplayableExportType> exportTypeList = 
            databaseMigrationService.getAvailableExportTypes();
        
        List<String> exportFilePaths = getExportDirectoryFilePaths(userContext);
        
        mav.addObject("errorMsg", errorMsg);
        mav.addObject("export", exportTab);
        mav.addObject("import", importTab);
        mav.addObject("exportTypeList", exportTypeList);
        mav.addObject("serverFiles", exportFilePaths);
        addDbInfoToMav(mav);
        
        // recent validations
        List<ImportDatabaseMigrationStatus> allValidationStatuses = databaseMigrationService.getAllValidationStatuses();
        List<RecentImport> recentValidations = Lists.newArrayListWithCapacity(allValidationStatuses.size());
        for (ImportDatabaseMigrationStatus status : allValidationStatuses) {
        	recentValidations.add(new RecentImport(status));
        }
        mav.addObject("recentValidations", recentValidations);
        
        // recent imports
        List<ImportDatabaseMigrationStatus> allImportStatuses = databaseMigrationService.getAllImportStatuses();
        List<RecentImport> recentImports = Lists.newArrayListWithCapacity(allImportStatuses.size());
        for (ImportDatabaseMigrationStatus status : allImportStatuses) {
        	recentImports.add(new RecentImport(status));
        }
        mav.addObject("recentImports", recentImports);
        
        mav.addObject("allowExportAll", ALLOW_EXPORT_ALL);
        
        return mav;
    }
	
	
	
	// EXPORT
	@RequestMapping
    public ModelAndView export(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException, IOException {
		
	    ModelAndView mav = new ModelAndView("redirect:exportProgress");
        
        String exportTypeString = ServletRequestUtils.getStringParameter(request, "exportType");
        ExportTypeEnum exportType = ExportTypeEnum.valueOf(exportTypeString);
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        String exportIds = ServletRequestUtils.getRequiredStringParameter(request, "databaseMigrationIds");
        List<Integer> exportIdList = Lists.newArrayList();
        
        if (StringUtils.isBlank(exportIds)) {
        	
        	if (!ALLOW_EXPORT_ALL) {
        		throw new IllegalArgumentException("No Items Selected");
        	}
            
        	exportIdList = databaseMigrationService.getAllSearchIds(exportType);
        	
        } else {
        	
        	for (String exportId : exportIds.split(",")){
                exportIdList.add(Integer.valueOf(exportId));
            }
        }
        
        ExportDatabaseMigrationStatus status = 
            databaseMigrationService.processExportDatabaseMigration(exportType, exportIdList, userContext);

        File exportFile = status.getExportFile();
        FileSystemResource resource = new FileSystemResource(exportFile);
        fileStore.put(status.getId(), resource);

        mav.addObject("statusKey", status.getId());

        return mav;
    }
	
	@RequestMapping
    public ModelAndView exportProgress(HttpServletRequest request, HttpServletResponse response, String statusKey) throws ServletRequestBindingException {
	
		ModelAndView mav = new ModelAndView("database/migration/exportProgress.jsp");
		
		ExportDatabaseMigrationStatus migrationStatus = databaseMigrationService.getExportStatus(statusKey);
        mav.addObject("migrationStatus", migrationStatus);
        
        addDbInfoToMav(mav);
        
        return mav;
	}
	
	@RequestMapping
    public void downloadExportFile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		String fileKey = ServletRequestUtils.getRequiredStringParameter(request, "fileKey");
		downloadFile(fileKey, response);
	}
	
	// SELECT OBJECTS
	@RequestMapping
    public ModelAndView viewSelectedItems(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("database/migration/popup/objectsViewPopup.jsp");
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        String exportTypeString = ServletRequestUtils.getRequiredStringParameter(request, "exportType");
        ExportTypeEnum exportType = ExportTypeEnum.valueOf(exportTypeString);

        List<Integer> exportItemIdList = Lists.newArrayList();
        String exportItemIdsString = ServletRequestUtils.getRequiredStringParameter(request, "exportItemIds");
        String[] exportItemIdsArray = exportItemIdsString.split(",");
        for(String exportItemId : exportItemIdsArray) {
            exportItemIdList.add(Integer.valueOf(exportItemId));
        }
        List<DatabaseMigrationContainer> exportItems = 
            databaseMigrationService.getItemsByIds(exportType, exportItemIdList, userContext);
        
        List<String> itemLabelList = Lists.newArrayList();
        for(DatabaseMigrationContainer container : exportItems) {
            itemLabelList.add(container.getDatabaseMigrationDisplay());
        }
        
        mav.addObject("itemList", itemLabelList);
        mav.addObject("objectCount", itemLabelList.size());
        
        return mav;
    }
	
	
	
	// LOAD LOCAL FILE
	@RequestMapping
    public ModelAndView loadLocalFile(HttpServletRequest request, HttpServletResponse response) {
		
		ModelAndView mav = null;
		YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
		
		File importFile = null;
		String loadError = null;
		
		if(ServletFileUpload.isMultipartContent(request)) {

            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
            MultipartFile dataFile = mRequest.getFile("dataFile");

            try {

                if (dataFile == null || StringUtils.isBlank(dataFile.getOriginalFilename())) {
                	loadError = messageSourceAccessor.getMessage("yukon.web.modules.support.databaseMigration.loadImport.loadFile.error.noFile");
                } 
                else {
                    InputStream inputStream = dataFile.getInputStream();
                    if (inputStream.available() <= 0) {
                    	loadError = messageSourceAccessor.getMessage("yukon.web.modules.support.databaseMigration.loadImport.loadFile.error.emptyFile");
                    }
                }

                importFile = WebFileUtils.convertToTempFile(dataFile, dataFile.getOriginalFilename() + "_", "");

            } catch (IOException e) {
            	loadError = messageSourceAccessor.getMessage("yukon.web.modules.support.databaseMigration.loadImport.loadFile.error.noFile");
            }
        }
        else {
        	loadError = messageSourceAccessor.getMessage("yukon.web.modules.support.databaseMigration.loadImport.loadFile.error.noFile");
        }
        
		// error
		if (loadError != null) {
			mav = new ModelAndView("redirect:home");
			mav.addObject("import", true);
			mav.addObject("errorMsg", loadError);
			return mav;
		}

		if (importFile == null) {
			throw new IllegalStateException("No file set");
		}
		
		// run validation
		ImportDatabaseMigrationStatus importDatabaseMigrationStatus = databaseMigrationService.validateImportFile(importFile, userContext);
		
		// store
		FileSystemResource resource = new FileSystemResource(importFile);
		fileStore.put(importDatabaseMigrationStatus.getId(), resource);
		
		// import progress
		mav = new ModelAndView("redirect:validationProgress");
		mav.addObject("statusKey", importDatabaseMigrationStatus.getId());
		
        return mav;
    }
	
	// LOAD SERVER FILE
	@RequestMapping
    public ModelAndView loadServerFile(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = null;
		YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
		
		String dataFilePath = ServletRequestUtils.getRequiredStringParameter(request, "dataFilePath");
		
		// retrieve file
		File importFile = new File(dataFilePath);
		if (!importFile.exists()) {
			mav = new ModelAndView("redirect:home");
			mav.addObject("import", true);
			mav.addObject("errorMsg", messageSourceAccessor.getMessage("yukon.web.modules.support.databaseMigration.loadImport.loadFile.error.doesNotExist"));
			return mav;
		}
		
		// run validation
		ImportDatabaseMigrationStatus importDatabaseMigrationStatus = databaseMigrationService.validateImportFile(importFile, userContext);
		
		// store
		FileSystemResource resource = new FileSystemResource(importFile);
		fileStore.put(importDatabaseMigrationStatus.getId(), resource);
		
		// import progress
		mav = new ModelAndView("redirect:validationProgress");
		mav.addObject("statusKey", importDatabaseMigrationStatus.getId());
		
		return mav;
	}
	
	// VALIDATION PROGRESS
	@RequestMapping
    public ModelAndView validationProgress(HttpServletRequest request, HttpServletResponse response, String statusKey) throws ServletRequestBindingException {
	
		ModelAndView mav = new ModelAndView("database/migration/validationProgress.jsp");
		
		ImportDatabaseMigrationStatus status = databaseMigrationService.getValidationStatus(statusKey);
        mav.addObject("status", status);
        
        return mav;
	}
	
	// IMPORT VALIDATE
	@RequestMapping
    public ModelAndView importValidate(HttpServletRequest request, HttpServletResponse response, String statusKey) throws ServletRequestBindingException {
	
		ModelAndView mav = new ModelAndView("database/migration/importValidate.jsp");
		
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
		
        mav.addObject("status", status);
        mav.addObject("filePath", resource.getPath());
        mav.addObject("fileSize", CtiUtilities.formatFileSize(resource.getFile().length(), 1));
        mav.addObject("orgDbUrl", orgEnvironment);
        mav.addObject("orgDbUsername", orgSchemaUser);
        mav.addObject("exportType", status.getExportType());
        
        addDbInfoToMav(mav);
        
        return mav;
	}
	
	// IMPORT CONFIRM
	@RequestMapping
    public ModelAndView importConfirm(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
	    YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

	    String fileKey = ServletRequestUtils.getRequiredStringParameter(request, "fileKey");
        String warningProcessingValueStr = ServletRequestUtils.getRequiredStringParameter(request, "warningProcessingValue");
        WarningProcessingEnum warningProcessingValue = WarningProcessingEnum.valueOf(warningProcessingValueStr);
        
        // retrieve file
        FileSystemResource fileSystemResource = fileStore.get(fileKey);
        File importFile = fileSystemResource.getFile();
        ImportDatabaseMigrationStatus importDatabaseMigrationStatus = databaseMigrationService.processImportDatabaseMigration(importFile, warningProcessingValue, userContext);
        
		ModelAndView mav = new ModelAndView("redirect:importProgress");
		mav.addObject("statusKey", importDatabaseMigrationStatus.getId());
        
		return mav;
	}
	
	// IMPORT PROGRESS
	@RequestMapping
    public ModelAndView importProgress(HttpServletRequest request, HttpServletResponse response, String statusKey) throws ServletRequestBindingException {
	
		ModelAndView mav = new ModelAndView("database/migration/importProgress.jsp");
		
		ImportDatabaseMigrationStatus status = databaseMigrationService.getImportStatus(statusKey);
        mav.addObject("status", status);
        
        return mav;
	}
	
	
	
	// VIEW OBJECTS
	@RequestMapping
    public ModelAndView objectsViewPopup(HttpServletRequest request, HttpServletResponse response) throws ServletException {
	    ModelAndView mav = new ModelAndView("database/migration/popup/objectsViewPopup.jsp");
	
		String fileKey = ServletRequestUtils.getRequiredStringParameter(request, "fileKey");
		ImportDatabaseMigrationStatus importDatabaseMigrationStatus = databaseMigrationService.getValidationStatus(fileKey);
		List<String> labelList = importDatabaseMigrationStatus.getLabelList();
		
		mav.addObject("itemList", labelList);
		mav.addObject("objectCount", labelList.size());
		return mav;
	}
	
	// VIEW WARNINGS
	@RequestMapping
    public ModelAndView warningsViewPopup(HttpServletRequest request, HttpServletResponse response) throws ServletException {
	    ModelAndView mav = new ModelAndView("database/migration/popup/warningsViewPopup.jsp");
	
	    String fileKey = ServletRequestUtils.getRequiredStringParameter(request, "fileKey");
        ImportDatabaseMigrationStatus importDatabaseMigrationStatus = databaseMigrationService.getValidationStatus(fileKey);
        Map<String, Set<String>> warningListMap = importDatabaseMigrationStatus.getWarningsMap();

        mav.addObject("warningListMap", warningListMap);
        
        int objectCount = 0;
        for (String key : warningListMap.keySet()) {
        	objectCount += warningListMap.get(key).size();
        }
        mav.addObject("objectCount", objectCount);
        
		return mav;
	}
	
	// VIEW ERRORS
	@RequestMapping
    public ModelAndView errorsViewPopup(HttpServletRequest request, HttpServletResponse response) throws ServletException {
	    ModelAndView mav = new ModelAndView("database/migration/popup/errorsViewPopup.jsp");

	    String fileKey = ServletRequestUtils.getRequiredStringParameter(request, "fileKey");
	    ImportDatabaseMigrationStatus importDatabaseMigrationStatus = databaseMigrationService.getValidationStatus(fileKey);
	    Map<String, Set<String>> errorListMap = importDatabaseMigrationStatus.getErrorsMap();

	    mav.addObject("errorListMap", errorListMap);
	    
	    int objectCount = 0;
        for (String key : errorListMap.keySet()) {
        	objectCount += errorListMap.get(key).size();
        }
        mav.addObject("objectCount", objectCount);
        
	    return mav;
	}
	
	// OPEN FILE
	@RequestMapping
    public void openFile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		String fileKey = ServletRequestUtils.getRequiredStringParameter(request, "fileKey");
		downloadFile(fileKey, response);
	}
	
	// HELPERS
	private void downloadFile(String fileKey, HttpServletResponse response) throws IOException {
		
		FileSystemResource resource = fileStore.get(fileKey);
		
		response.setContentType("text/xml");
        response.setHeader("Content-Type", "application/force-download");
        response.setHeader("Content-Disposition","attachment; filename=\"" + ServletUtil.makeWindowsSafeFileName(resource.getFilename()) + "\"");
        
        FileCopyUtils.copy(resource.getInputStream(), response.getOutputStream());
	}
	
	private void addDbInfoToMav(ModelAndView mav) {
		mav.addObject("dbUrl", poolManager.getPrimaryUrl());
        mav.addObject("dbUsername", poolManager.getPrimaryUser());
	}

	/**
	 * gets all the exports on the server and returns all the paths of those files
	 * 
	 * @param userContext
	 * @return
	 */
	private List<String> getExportDirectoryFilePaths(YukonUserContext userContext) {
	    List<String> exportFilePaths = new ArrayList<String>();
	    
	    String rolePropertyPath = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.DATABASE_MIGRATION_FILE_LOCATION, userContext.getYukonUser());
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
	
	@Autowired
	public void setDatabaseMigrationService(DatabaseMigrationService databaseMigrationService) {
	    this.databaseMigrationService = databaseMigrationService;
	}

	@Autowired
	public void setPoolManager(PoolManager poolManager) {
		this.poolManager = poolManager;
	}
	
	@Autowired
	public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
		this.messageSourceResolver = messageSourceResolver;
	}
	
	@Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
}
