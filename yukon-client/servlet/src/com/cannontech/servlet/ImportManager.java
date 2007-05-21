/*
 * Created on Feb 4, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.servlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.util.task.ImportCustAccountsTask;
import com.cannontech.stars.util.task.ImportDSMDataTask;
import com.cannontech.stars.util.task.ImportStarsDataTask;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.util.task.UploadGenericFileTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.ImportManagerUtil;
import com.cannontech.web.navigation.CtiNavObject;

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
		if (referer == null) referer = ((CtiNavObject)session.getAttribute(ServletUtils.NAVIGATE)).getPreviousPage();
		if (redirect == null) redirect = referer;
		
		if (action.equalsIgnoreCase("ImportCustAccounts"))
			importCustomerAccounts( items, user, req, session );
		else if (action.equalsIgnoreCase("UploadGeneric"))
			uploadGenericFile( items, user, req, session );
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
	
	private void importCustomerAccounts(final List items, final StarsYukonUser user, final HttpServletRequest req, final HttpSession session) {
		final LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			final FileItem custFile = getUploadFile( items, "CustFile" );
			final FileItem hwFile = getUploadFile( items, "HwFile" );
			final String email = getFormField( items, "Email" );
			
			if (custFile == null && hwFile == null)
				throw new WebClientException( "No import file is provided" );
                 
            String logMsg = "Customer account import process started.";
            ActivityLogger.logEvent( user.getUserID(), ActivityLogActions.IMPORT_CUSTOMER_ACCOUNT_ACTION, logMsg );

            final Integer userId = new Integer(user.getUserID());

			final TimeConsumingTask task = new ImportCustAccountsTask() {
				private TimeConsumingTask preScanTask;
				private TimeConsumingTask realTask;
				
				public void run() {
					this.setStatus(ImportCustAccountsTask.STATUS_RUNNING);
					preScanTask = new ImportCustAccountsTask(energyCompany, custFile, hwFile, email, true, userId);
					long preScanId = ProgressChecker.addTask(preScanTask);
					
					for (preScanTask = ProgressChecker.getTask(preScanId); ((preScanTask.getStatus() != ImportCustAccountsTask.STATUS_FINISHED) ||
							(preScanTask.getStatus() != ImportCustAccountsTask.STATUS_ERROR));) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException ignore) {}
						
						if (preScanTask.getStatus() == ImportCustAccountsTask.STATUS_FINISHED) {
							ProgressChecker.removeTask(preScanId);
							break;
						}
						
						if (preScanTask.getStatus() == ImportCustAccountsTask.STATUS_ERROR) {
							ProgressChecker.removeTask(preScanId);
							this.setStatus(ImportCustAccountsTask.STATUS_ERROR);
							return;
						}
					}
					
					realTask = new ImportCustAccountsTask(energyCompany, custFile, hwFile, email, false, userId);
					long realTaskId = ProgressChecker.addTask(realTask);
					
					for (realTask = ProgressChecker.getTask(realTaskId); ((realTask.getStatus() != ImportCustAccountsTask.STATUS_FINISHED) ||
							(realTask.getStatus() != ImportCustAccountsTask.STATUS_ERROR));) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException ignore) {}
						
						if (realTask.getStatus() == ImportCustAccountsTask.STATUS_FINISHED) {
							ProgressChecker.removeTask(realTaskId);
							this.setStatus(ImportCustAccountsTask.STATUS_FINISHED);
							break;
						}
						
						if (realTask.getStatus() == ImportCustAccountsTask.STATUS_ERROR) {
							ProgressChecker.removeTask(realTaskId);
							this.setStatus(ImportCustAccountsTask.STATUS_ERROR);
							return;
						}
					}
					
					this.setStatus(ImportCustAccountsTask.STATUS_FINISHED);
				}
				
				public String getProgressMsg() {
					return (realTask != null) ? realTask.getProgressMsg() : (preScanTask != null) ? preScanTask.getProgressMsg() : "Pre-scanning import file(s)"; 
				}
				
				public Collection getErrorList() {
					if (preScanTask != null && preScanTask.getStatus() == ImportCustAccountsTask.STATUS_ERROR) {
						return ((ImportCustAccountsTask) preScanTask).getErrorList();
					}
					if (realTask != null && realTask.getStatus() == ImportCustAccountsTask.STATUS_ERROR) {
						return ((ImportCustAccountsTask) realTask).getErrorList();
					}
					return new java.util.HashSet();
				}
				
				public void cancel() {
					if (this.getStatus() == ImportCustAccountsTask.STATUS_RUNNING) {
						this.setStatus(ImportCustAccountsTask.STATUS_CANCELING);
						if (preScanTask != null) {
							preScanTask.cancel();
							while (preScanTask.getStatus() != ImportCustAccountsTask.STATUS_CANCELED||
								   preScanTask.getStatus() != ImportCustAccountsTask.STATUS_ERROR ||
								   preScanTask.getStatus() != ImportCustAccountsTask.STATUS_FINISHED) {
								try {
									Thread.sleep(1000);
								} catch (InterruptedException ignore) {}
								
								if (preScanTask.getStatus() == ImportCustAccountsTask.STATUS_CANCELED) {
									break;
								}
							}
						}
						if (realTask != null) {
							realTask.cancel();
							while (realTask.getStatus() != ImportCustAccountsTask.STATUS_CANCELED ||
								   realTask.getStatus() != ImportCustAccountsTask.STATUS_ERROR ||
								   realTask.getStatus() != ImportCustAccountsTask.STATUS_FINISHED) {
								try {
									Thread.sleep(1000);
								} catch (InterruptedException ignore) {}
								
								if (realTask.getStatus() == ImportCustAccountsTask.STATUS_CANCELED) {
									break;
								}
							}
						}
						this.setStatus(ImportCustAccountsTask.STATUS_CANCELED);
					}
				}
			};
			
			
			long id = ProgressChecker.addTask(task);
			int x;
			TimeConsumingTask t;
			for (t = ProgressChecker.getTask(id), x = 0; x < 5; x++ ) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ignore) {}
				
				if (t.getStatus() == ImportCustAccountsTask.STATUS_FINISHED) {
					session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, t.getProgressMsg());
					ProgressChecker.removeTask(id);
					return;
				}
				
				if (t.getStatus() == ImportCustAccountsTask.STATUS_ERROR) {
					session.setAttribute("errorList", ((ImportCustAccountsTask) t).getErrorList());
					ProgressChecker.removeTask(id);
					redirect = req.getContextPath() + "/operator/Consumer/ImportManagerView.jsp";
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
	
	private void uploadGenericFile(List items, StarsYukonUser user, HttpServletRequest req, HttpSession session) 
	{
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		try 
		{
			FileItem genericFile = getUploadFile( items, "GenericFile" );
			
			if (genericFile == null)
				throw new WebClientException( "No file is provided" );
			
			TimeConsumingTask task = new UploadGenericFileTask( energyCompany, genericFile );
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
