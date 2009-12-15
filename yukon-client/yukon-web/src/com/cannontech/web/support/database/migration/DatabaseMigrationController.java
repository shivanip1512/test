package com.cannontech.web.support.database.migration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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
import com.cannontech.web.util.WebFileUtils;

@Controller
@RequestMapping("/database/migration/*")
public class DatabaseMigrationController {

    private DatabaseMigrationService databaseMigrationService;
	private PoolManager poolManager;
	private RolePropertyDao rolePropertyDao;
	private YukonUserContextMessageSourceResolver messageSourceResolver;

    private Map<String, FileSystemResource> fileStore = new HashMap<String, FileSystemResource>();
	
	// HOME
	@RequestMapping
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) {
        
        ModelAndView mav = new ModelAndView("database/migration/home.jsp");
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        String errorMsg = ServletRequestUtils.getStringParameter(request, "errorMsg", null);
        boolean exportTab = ServletRequestUtils.getBooleanParameter(request, "export", false);
        boolean importTab = ServletRequestUtils.getBooleanParameter(request, "import", false);
        
        SortedMap<String, SortedSet<String>> configurationDBTablesMap = 
            databaseMigrationService.getAvailableConfigurationDatabaseTableMap();
        
        List<String> exportFilePaths = getExportDirectoryFilePaths(userContext);
        
        mav.addObject("errorMsg", errorMsg);
        mav.addObject("export", exportTab);
        mav.addObject("import", importTab);
        mav.addObject("configurationDBTablesMap", configurationDBTablesMap);
        mav.addObject("serverFiles", exportFilePaths);
        addDbInfoToMav(mav);
        
        return mav;
    }
	
	
	
	// EXPORT
	@RequestMapping
    public ModelAndView export(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException, IOException {
		
        
        String componentType = ServletRequestUtils.getStringParameter(request, "selectedComponent");
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        String databaseMigrationIds = ServletRequestUtils.getRequiredStringParameter(request, "databaseMigrationIds");
        List<Integer> primaryKeyList = new ArrayList<Integer>();
        for (String databaseMigrationId : databaseMigrationIds.split(",")){
            primaryKeyList.add(Integer.valueOf(databaseMigrationId));
        }
        
        Map<String, Resource> availableConfigurationMap = databaseMigrationService.getAvailableConfigurationMap();
        Resource configurationXMLFile = availableConfigurationMap.get(componentType);
        ExportDatabaseMigrationStatus status = null;
        if(configurationXMLFile != null) {
            status = databaseMigrationService.processExportDatabaseMigration(configurationXMLFile.getFile(), primaryKeyList, userContext);
        } else {
            throw new IllegalArgumentException("The configuration file supplied does not exist.");
        }
        
        File exportFile = status.getExportFile();
        FileSystemResource resource = new FileSystemResource(exportFile);
        fileStore.put(status.getId(), resource);
        
        ModelAndView mav = new ModelAndView("redirect:exportProgress");
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
    public ModelAndView selectObjects(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        String objectKey = ServletRequestUtils.getRequiredStringParameter(request, "objectKey");
	    
        List<Map<String, Object>> configurationItems = databaseMigrationService.getConfigurationItems(objectKey);

        Map<String, Object> map = configurationItems.get(0);
        Set<String> keySet = map.keySet();
        String primaryKeyId = (String)keySet.toArray()[0];
        
	    ModelAndView mav = new ModelAndView("database/migration/popup/selectObjects.jsp");
        mav.addObject("configurationItems", configurationItems);
        mav.addObject("primaryKeyId", primaryKeyId);
        mav.addObject("objectKey", objectKey);
        addDbInfoToMav(mav);
        
        return mav;
    }
	
	
	
	// LOAD LOCAL FILE
	@RequestMapping
    public ModelAndView loadLocalFile(HttpServletRequest request, HttpServletResponse response) {
		
		ModelAndView mav = null;
		YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
		
		File file = null;
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

                file = WebFileUtils.convertToTempFile(dataFile, "databaseMigration", "");

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
		
		// store
		if (file == null) {
			throw new IllegalStateException("No file set");
		}
		String fileKey = UUID.randomUUID().toString();
		FileSystemResource resource = new FileSystemResource(file);
		fileStore.put(fileKey, resource);
		
		// show validation
		mav = new ModelAndView("database/migration/importValidate.jsp");
		mav.addObject("fileKey", fileKey);
		mav.addObject("filePath", resource.getPath());
		mav.addObject("fileSize", resource.getFile().length() / 1024);
		addDbInfoToMav(mav);
		
        return mav;
    }
	
	// LOAD LOCAL FILE
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
		
		ImportDatabaseMigrationStatus validateImportFile = databaseMigrationService.validateImportFile(importFile);
		System.out.println(validateImportFile);
		// store
		String fileKey = UUID.randomUUID().toString();
		FileSystemResource resource = new FileSystemResource(importFile);
		fileStore.put(fileKey, resource);
		
		// show validation
		mav = new ModelAndView("database/migration/importValidate.jsp");
		mav.addObject("fileKey", fileKey);
		mav.addObject("filePath", resource.getPath());
		mav.addObject("fileSize", resource.getFile().length() / 1024);
		addDbInfoToMav(mav);
		
		return mav;
	}
	
	// VIEW OBJECTS
	@RequestMapping
    public ModelAndView objectsViewPopup(HttpServletRequest request, HttpServletResponse response) throws ServletException {
	
		String objectKey = ServletRequestUtils.getRequiredStringParameter(request, "objectKey");
		
		ModelAndView mav = new ModelAndView("database/migration/popup/objectsViewPopup.jsp");
		
		return mav;
	}
	
	// VIEW WARNINGS
	@RequestMapping
    public ModelAndView warningsViewPopup(HttpServletRequest request, HttpServletResponse response) throws ServletException {
	
		String objectKey = ServletRequestUtils.getRequiredStringParameter(request, "objectKey");
		
		ModelAndView mav = new ModelAndView("database/migration/popup/warningsViewPopup.jsp");
		
		return mav;
	}
	
	// VIEW ERRORS
	@RequestMapping
    public ModelAndView errorsViewPopup(HttpServletRequest request, HttpServletResponse response) throws ServletException {
	
		String objectKey = ServletRequestUtils.getRequiredStringParameter(request, "objectKey");
		
		ModelAndView mav = new ModelAndView("database/migration/popup/errorsViewPopup.jsp");
		
		return mav;
	}
	
	// IMPORT CONFIRM
	@RequestMapping
    public ModelAndView importConfirm(HttpServletRequest request, HttpServletResponse response) throws ServletException {
	
		ModelAndView mav = new ModelAndView("database/migration/importProgress.jsp");
		
		
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
