package com.cannontech.web.support.database.migration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.database.PoolManager;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.util.WebFileUtils;

@Controller
@RequestMapping("/database/migration/*")
public class DatabaseMigrationController {

	private PoolManager poolManager;
	private YukonUserContextMessageSourceResolver messageSourceResolver;
	
	private Map<String, FileSystemResource> fileStore = new HashMap<String, FileSystemResource>();
	
	// HOME
	@RequestMapping
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) {
        
        ModelAndView mav = new ModelAndView("database/migration/home.jsp");
        
        String errorMsg = ServletRequestUtils.getStringParameter(request, "errorMsg", null);
        boolean exportTab = ServletRequestUtils.getBooleanParameter(request, "export", false);
        boolean importTab = ServletRequestUtils.getBooleanParameter(request, "import", false);
        
        mav.addObject("errorMsg", errorMsg);
        mav.addObject("export", exportTab);
        mav.addObject("import", importTab);
        addDbInfoToMav(mav);
        
        return mav;
    }
	
	// EXPORT
	@RequestMapping
    public ModelAndView export(HttpServletRequest request, HttpServletResponse response) {
        
        ModelAndView mav = new ModelAndView("database/migration/exportProgress.jsp");
        
        addDbInfoToMav(mav);
        
        return mav;
    }
	
	// SELECT OBJECTS
	@RequestMapping
    public ModelAndView selectObjects(HttpServletRequest request, HttpServletResponse response) {
        
        ModelAndView mav = new ModelAndView("database/migration/popup/selectObjects.jsp");
        
        addDbInfoToMav(mav);
        
        return mav;
    }
	
	// DOWNLOAD EXPORT FILE
	@RequestMapping
    public void downloadExportFile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		String fileKey = ServletRequestUtils.getRequiredStringParameter(request, "fileKey");
		downloadFile(fileKey, response);
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
		File file = new File(dataFilePath);
		if (!file.exists()) {
			mav = new ModelAndView("redirect:home");
			mav.addObject("import", true);
			mav.addObject("errorMsg", messageSourceAccessor.getMessage("yukon.web.modules.support.databaseMigration.loadImport.loadFile.error.doesNotExist"));
			return mav;
		}
		
		// store
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
	
	// VIEW OBJECTS
	@RequestMapping
    public ModelAndView objectsViewPopup(HttpServletRequest request, HttpServletResponse response) throws ServletException {
	
		String someObjectKey = ServletRequestUtils.getRequiredStringParameter(request, "someObjectKey");
		
		ModelAndView mav = new ModelAndView("database/migration/popup/objectsViewPopup.jsp");
		
		return mav;
	}
	
	// VIEW WARNINGS
	@RequestMapping
    public ModelAndView warningsViewPopup(HttpServletRequest request, HttpServletResponse response) throws ServletException {
	
		String someObjectKey = ServletRequestUtils.getRequiredStringParameter(request, "someObjectKey");
		
		ModelAndView mav = new ModelAndView("database/migration/popup/warningsViewPopup.jsp");
		
		return mav;
	}
	
	// VIEW ERRORS
	@RequestMapping
    public ModelAndView errorsViewPopup(HttpServletRequest request, HttpServletResponse response) throws ServletException {
	
		String someObjectKey = ServletRequestUtils.getRequiredStringParameter(request, "someObjectKey");
		
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
	
	//TODO delete
	@PostConstruct
	private void makeDummyExportFile() throws IOException {
		
		String filePath = "c:\\dummyDatabaseMigrationExport.xml";
		FileWriter fileWriter = new FileWriter(filePath);
		BufferedWriter out = new BufferedWriter(fileWriter);
		out.write("<xml>Componenty Goodness</xml>");
		out.flush();
		out.close();
		
		String fileKey = "dummyFileKey";
		FileSystemResource resource = new FileSystemResource(filePath);
		fileStore.put(fileKey, resource);
	}
	
	@Autowired
	public void setPoolManager(PoolManager poolManager) {
		this.poolManager = poolManager;
	}
	
	@Autowired
	public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
		this.messageSourceResolver = messageSourceResolver;
	}
}
