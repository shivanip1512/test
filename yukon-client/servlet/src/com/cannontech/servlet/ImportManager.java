/*
 * Created on Feb 4, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.servlet;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.util.task.ImportCustAccountsTask;
import com.cannontech.stars.util.task.ImportDSMDataTask;
import com.cannontech.stars.util.task.ImportStarsDataTask;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.ImportManagerUtil;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ImportManager extends HttpServlet {
    
	private String referer = null;
	private String redirect = null;

	private String getFormField(List items, String fieldName) {
		for (int i = 0; i < items.size(); i++) {
			FileItem item = (FileItem) items.get(i);
			if (item.isFormField() && item.getFieldName().equals(fieldName))
				return item.getString();
		}
		
		return null;
	}
	
	private FileItem getUploadFile(List items, String fieldName)
		throws WebClientException
	{
		for (int i = 0; i < items.size(); i++) {
			FileItem item = (FileItem) items.get(i);
			if (!item.isFormField() && item.getFieldName().equals(fieldName))
				if (!item.getName().equals("")) return item;
		}
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException
	{
		HttpSession session = req.getSession(false);
		if (session == null) {
			resp.sendRedirect( req.getContextPath() + SOAPClient.LOGIN_URL );
			return;
		}
    	
		StarsYukonUser user = (StarsYukonUser)
				session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
		
		boolean isMultiPart = DiskFileUpload.isMultipartContent( req );
		List items = null;
		String action = null;
		
		if (isMultiPart) {
			try {
				DiskFileUpload upload = new DiskFileUpload();
				items = upload.parseRequest( req );
				action = getFormField( items, "action" );
				redirect = getFormField( items, ServletUtils.ATT_REDIRECT );
				referer = getFormField( items, ServletUtils.ATT_REFERRER );
			}
			catch (FileUploadException e) {
				CTILogger.error( e.getMessage(), e );
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to parse the form data");
			}
		}
		else {
			action = req.getParameter( "action" );
			redirect = req.getParameter( ServletUtils.ATT_REDIRECT );
			referer = req.getParameter( ServletUtils.ATT_REFERRER );
		}
		
		if (action == null) action = "";
		if (referer == null) referer = req.getHeader( "referer" );
		if (redirect == null) redirect = referer;
		
		if (action.equalsIgnoreCase("ImportCustAccounts"))
			importCustomerAccounts( items, user, req, session );
		else if (action.equalsIgnoreCase("ImportINIData"))
			importINIData( user, req, session );
		else if (action.equalsIgnoreCase("PreprocessStarsData"))
			preProcessStarsData( user, req, session );
		else if (action.equalsIgnoreCase("AssignSelectionList"))
			assignSelectionList( user, req, session );
		else if (action.equalsIgnoreCase("ImportStarsData"))
			importStarsData( user, req, session );
		else if (action.equalsIgnoreCase("GenerateConfigFiles"))
			generateConfigFiles( user, req, session );
		else if (action.equalsIgnoreCase("ImportDSM"))
			importDSMData( user, req, session );
        
		resp.sendRedirect( redirect );
	}
	
	private void importCustomerAccounts(List items, StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			FileItem custFile = getUploadFile( items, "CustFile" );
			FileItem hwFile = getUploadFile( items, "HwFile" );
			String email = getFormField( items, "Email" );
			String preScan = getFormField( items, "PreScan" );
			
			if (custFile == null && hwFile == null)
				throw new WebClientException( "No import file is provided" );
			
			TimeConsumingTask task = new ImportCustAccountsTask( energyCompany, custFile, hwFile, email, preScan != null );
			long id = ProgressChecker.addTask( task );
			
			// Wait 5 seconds for the task to finish (or error out), if not, then go to the progress page
			for (int i = 0; i < 5; i++) {
				try {
					Thread.sleep(1000);
				}
				catch (InterruptedException e) {}
				
				task = ProgressChecker.getTask(id);
				
				if (task.getStatus() == ImportCustAccountsTask.STATUS_FINISHED) {
					session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, task.getProgressMsg());
					ProgressChecker.removeTask( id );
					return;
				}
				
				if (task.getStatus() == ImportCustAccountsTask.STATUS_ERROR) {
					session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, task.getErrorMsg());
					ProgressChecker.removeTask( id );
					return;
				}
			}
			
			session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
			session.setAttribute(ServletUtils.ATT_REFERRER, redirect);
			redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
		}
		catch (WebClientException e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
			redirect = referer;
		}
	}
	
	private void generateConfigFiles(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		try {
			File importDir = new File( req.getParameter("ImportDir") );
			if (importDir.isFile())
				importDir = importDir.getParentFile();
			
			if (!importDir.exists())
				throw new WebClientException("The specified directory doesn't exist");
			
			Properties savedReq = new Properties();
			savedReq.put("ImportDir", importDir.getAbsolutePath());
			session.setAttribute(ServletUtils.ATT_LAST_SUBMITTED_REQUEST, savedReq);
			
			ImportDSMDataTask.generateConfigFiles( importDir );
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Configuration files have been generated. Please follow the instructions in the files to complete them before converting the database.");
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			if (e instanceof WebClientException)
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
			else
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to generate the configuration files");
			redirect = referer;
		}
	}
	
	private void importDSMData(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		ServletUtils.saveRequest(req, session, new String[] {"ImportDir"});
		
		try {
			File importDir = new File( req.getParameter("ImportDir") );
			if (!importDir.exists() || !importDir.isDirectory())
				throw new WebClientException("The specified directory doesn't exist");
			
			ImportDSMDataTask task = new ImportDSMDataTask( energyCompany, importDir );
			long id = ProgressChecker.addTask( task );
			
			session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
			session.setAttribute(ServletUtils.ATT_REFERRER, referer);
			redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
		}
		catch (WebClientException e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
			redirect = referer;
		}
	}
	
	private void importINIData(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			boolean selListImported = false;
			
			if (req.getParameter("S3DATA_INI").length() > 0) {
				File selListFile = new File( req.getParameter("S3DATA_INI") );
				ImportStarsDataTask.importSelectionLists( selListFile, energyCompany );
				selListImported = true;
			}
			
			if (req.getParameter("STARS3_INI").length() > 0) {
				File appSettingsFile = new File( req.getParameter("STARS3_INI") );
				ImportStarsDataTask.importAppSettings( appSettingsFile, session );
			}
			
			String msg = "INI file(s) imported successfully.";
			if (selListImported)
				msg += "<br>Please go to the energy company settings page to update appliance categories and device type list.";
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, msg);
		}
		catch (WebClientException e) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
			redirect = referer;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to import INI file(s)");
			redirect = referer;
		}
	}
	
	private void preProcessStarsData(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		ServletUtils.saveRequest(req, session, new String[] {"ImportDir"});
		
		try {
			File importDir = new File( req.getParameter("ImportDir") );
			if (!importDir.exists() || !importDir.isDirectory())
				throw new WebClientException("The specified directory doesn't exist");
			
			ImportStarsDataTask.preProcessStarsData( importDir, session, energyCompany );
		}
		catch (WebClientException e) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
			redirect = referer;
		}
	}
	
	private void assignSelectionList(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			ImportStarsDataTask.assignSelectionList( req, session, energyCompany );
		}
		catch (WebClientException e) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
			redirect = referer;
		}
	}
	
	private void importStarsData(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		Hashtable preprocessedData = (Hashtable) session.getAttribute(ImportManagerUtil.PREPROCESSED_DATA);
		File importDir = (File) session.getAttribute(ImportManagerUtil.CUSTOMER_FILE_PATH);
		
		Hashtable processedData = ImportStarsDataTask.postProcessStarsData( preprocessedData, importDir, energyCompany );
		
		ImportStarsDataTask task = new ImportStarsDataTask( energyCompany, processedData, importDir );
		long id = ProgressChecker.addTask( task );
		
		session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
		session.setAttribute(ServletUtils.ATT_REFERRER, referer);
		redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
	}

}
