/*
 * Created on Feb 4, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.servlet;

import java.io.File;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.util.task.ImportCustAccountsTask;
import com.cannontech.stars.util.task.ImportDSMDataTask;
import com.cannontech.stars.util.task.ImportStarsDataTask;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.ImportManagerUtil;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.stars.xml.serialize.AddressingGroup;
import com.cannontech.stars.xml.serialize.StarsApplianceCategory;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.stars.xml.serialize.StarsEnrLMProgram;
import com.cannontech.stars.xml.serialize.StarsEnrollmentPrograms;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

interface ImportFileParser {
	String[] populateFields(String line) throws Exception;
}

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ImportManager extends HttpServlet {
    
	private static final String LINE_SEPARATOR = System.getProperty( "line.separator" );
	private static final Integer ZERO = new Integer(0);
    
	private static StreamTokenizer prepareStreamTokenzier(String line) {
		StreamTokenizer st = new StreamTokenizer( new StringReader(line) );
		st.resetSyntax();
		st.wordChars( 0, 255 );
		st.ordinaryChar( ',' );
		st.quoteChar( '"' );
		
		return st;
	}
    
	private static final Hashtable parsers = new Hashtable();
	static {
		parsers.put("Idaho", new ImportFileParser() {
			/** Idaho Power Customer Information
			 * COL_NUM:	COL_NAME
			 * 1		----
			 * 2		LAST_NAME
			 * 3		FIRST_NAME
			 * 4		----
			 * 5,6		STREET_ADDR1
			 * 7		CITY
			 * 8		STATE
			 * 9		ZIP_CODE
			 * 10		HOME_PHONE
			 * 11		ACCOUNT_NO
			 * 12		SERIAL_NO(THERMOSTAT)
			 * 13		INSTALL_DATE
			 * 14		SERVICE_COMPANY
			 */
			public String[] populateFields(String line) throws Exception {
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = ImportManagerUtil.prepareFields(ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.NUM_INV_FIELDS);
				
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_LAST_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_FIRST_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_STREET_ADDR1] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[ImportManagerUtil.IDX_STREET_ADDR1] += " " + st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_CITY] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_STATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_ZIP_CODE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_HOME_PHONE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_ACCOUNT_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERIAL_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_INSTALL_DATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERVICE_COMPANY] = st.sval;
				
				fields[ImportManagerUtil.IDX_USERNAME] = fields[ImportManagerUtil.IDX_FIRST_NAME].substring(0,1).toLowerCase()
									 + fields[ImportManagerUtil.IDX_LAST_NAME].toLowerCase();
				fields[ImportManagerUtil.IDX_PASSWORD] = fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERIAL_NO];
				
				fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_DEVICE_TYPE] = "ExpressStat";
				
				if (fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERVICE_COMPANY].equalsIgnoreCase("Western"))
					fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERVICE_COMPANY] = "\"Western Heating\"";
				else if (fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERVICE_COMPANY].equalsIgnoreCase("Ridgeway"))
					fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERVICE_COMPANY] = "\"Ridgeway Industrial\"";
				else if (fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERVICE_COMPANY].equalsIgnoreCase("Access"))
					fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERVICE_COMPANY] = "\"Access Heating\"";
				
				return fields;
			}
		});
    	
		parsers.put("Portland", new ImportFileParser() {
			/** Portland General Customer Information
			 * COL_NUM:	COL_NAME
			 * 1		ACCOUNT_NO
			 * 2		COUNTY
			 * 3		----
			 * 4		LAST_NAME
			 * 5		FIRST_NAME
			 * 6		HOME_PHONE
			 * 7,8		WORK_PHONE
			 * 9		STREET_ADDR1
			 * 10		CITY
			 * 11		STATE
			 * 12		ZIP_CODE
			 * 13		EMAIL
			 * 14		----
			 * 15		SERVICE_COMPANY
			 * 16		----
			 * 17		INSTALL_DATE
			 * 18		SERIAL_NO(LCR)
			 * 19		ADDR_GROUP
			 * 20		----
			 */
			public String[] populateFields(String line) throws Exception {
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = ImportManagerUtil.prepareFields(ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.NUM_INV_FIELDS);
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_ACCOUNT_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_COUNTY] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_LAST_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_FIRST_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_HOME_PHONE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_WORK_PHONE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_WORK_PHONE_EXT] = "(ext." + st.sval + ")";
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_STREET_ADDR1] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_CITY] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_STATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_ZIP_CODE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_EMAIL] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERVICE_COMPANY] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_INSTALL_DATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERIAL_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_ADDR_GROUP] = st.sval;
				if (st.ttype != ',') st.nextToken();
				
				fields[ImportManagerUtil.IDX_USERNAME] = fields[ImportManagerUtil.IDX_EMAIL];
				fields[ImportManagerUtil.IDX_PASSWORD] = fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERIAL_NO];
				
				fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_DEVICE_TYPE] = "LCR-5000";
				fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_ADDR_GROUP] = "PGE RIWH Group " + fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_ADDR_GROUP];
				
				return fields;
			}
		});
    	
		/** San Antonio Customer Information
		 * COL_NUM:	COL_NAME
		 * 1		ACCOUNT_NO
		 * 2		LAST_NAME
		 * 3		FIRST_NAME
		 * 4		HOME_PHONE
		 * 5		WORK_PHONE
		 * 6		EMAIL
		 * 7		STREET_ADDR1
		 * 8		STREET_ADDR2
		 * 9		CITY
		 * 10		STATE
		 * 11		ZIP_CODE
		 * 12		MAP_NO
		 * 13		SERIAL_NO(THERMOSTAT)
		 * 14		INSTALL_DATE
		 * 15		REMOVE_DATE
		 * ----		(Additional required fields)
		 * 16		USERNAME
		 * 17		PASSWORD
		 * 18		HARDWARE_ACTION
		 */
		parsers.put("San Antonio", new ImportFileParser() {
			public String[] populateFields(String line) throws Exception {
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = ImportManagerUtil.prepareFields(ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.NUM_INV_FIELDS);
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_ACCOUNT_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_LAST_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_FIRST_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_HOME_PHONE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_WORK_PHONE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_EMAIL] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_STREET_ADDR1] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_STREET_ADDR2] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_CITY] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_STATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_ZIP_CODE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_MAP_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERIAL_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_INSTALL_DATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_REMOVE_DATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_USERNAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_PASSWORD] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_HARDWARE_ACTION] = st.sval;
				
				fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_DEVICE_TYPE] = "ExpressStat";
				
				return fields;
			}
		});
	}
    
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
			preprocessStarsData( user, req, session );
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
			
			TimeConsumingTask task = new ImportCustAccountsTask( user, custFile, hwFile, email, preScan != null );
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
		
		try {
			File importDir = new File( req.getParameter("ImportDir") );
			if (importDir.isFile())
				importDir = importDir.getParentFile();
			
			if (!importDir.exists())
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
	
	private void importSelectionLists(File selListFile, LiteStarsEnergyCompany energyCompany) throws Exception {
		String[] lines = StarsUtils.readFile( selListFile );
		if (lines == null)
			throw new WebClientException("Unable to read file '" + selListFile.getPath() + "'");
		
		String listName = null;
		ArrayList listEntries = null;
		boolean isInList = false;
		
		for (int i = 0; i < lines.length; i++) {
			if (!isInList) {
				if (!lines[i].startsWith("[")) continue;
				
				for (int j = 0; j < ImportManagerUtil.LIST_NAMES.length; j++) {
					if (ImportManagerUtil.LIST_NAMES[j][2].equals( lines[i] )) {
						listName = ImportManagerUtil.LIST_NAMES[j][0];
						listEntries = new ArrayList();
						isInList = true;
						break;
					}
				}
			}
			else if (lines[i].trim().length() > 0) {
				if (lines[i].endsWith("="))
					lines[i] = lines[i].substring(0, lines[i].length() - 1);
				listEntries.add( lines[i] );
			}
			else {
				// Find the end of a list, update the list entries
				isInList = false;
				
				if (listName.equals("ServiceCompany")) {
					StarsAdminUtil.deleteAllServiceCompanies( energyCompany );
					
					for (int j = 0; j < listEntries.size(); j++) {
						String entry = (String) listEntries.get(j);
						StarsAdminUtil.createServiceCompany( entry, energyCompany );
					}
				}
				else if (listName.equals("Substation")) {
					StarsAdminUtil.deleteAllSubstations( energyCompany );
					
					for (int j = 0; j < listEntries.size(); j++) {
						String entry = (String) listEntries.get(j);
						String subName = null;
						int routeID = 0;
						
						int pos = entry.indexOf('=');
						if (pos >= 0) {
							subName = entry.substring(0, pos);
							String routeName = entry.substring(pos + 1);
							if (routeName.length() > 0) {
								LiteYukonPAObject[] routes = PAOFuncs.getAllLiteRoutes();
								for (int k = 0; k < routes.length; k++) {
									if (routes[k].getPaoName().equalsIgnoreCase( routeName )) {
										routeID = routes[k].getYukonID();
										break;
									}
								}
							}
						}
						else {
							subName = entry;
						}
						
						StarsAdminUtil.createSubstation( subName, routeID, energyCompany );
					}
				}
				else if (listName.equals("LoadType")) {
					for (int j = 0; j < listEntries.size(); j++) {
						String entry = (String) listEntries.get(j);
						StarsAdminUtil.createApplianceCategory( entry, energyCompany );
					}
				}
				else {
					// Always add an empty entry at the beginning of the list
					listEntries.add(0, " ");
					
					Object[][] entryData = new Object[ listEntries.size() ][];
					for (int j = 0; j < listEntries.size(); j++) {
						entryData[j] = new Object[3];
						entryData[j][0] = ZERO;
						entryData[j][1] = listEntries.get(j);
						entryData[j][2] = ZERO;
					}
					
					YukonSelectionList cList = energyCompany.getYukonSelectionList(listName, false, false);
					if (cList == null)
						throw new WebClientException("Cannot import data into an inherited selection list.");
					
					StarsAdminUtil.updateYukonListEntries( cList, entryData, energyCompany );
				}
			}
		}
	}
	
	private void importAppSettings(File appSettingsFile, HttpSession session) throws WebClientException {
		String[] lines = StarsUtils.readFile( appSettingsFile );
		if (lines == null)
			throw new WebClientException( "Unable to read file '" + appSettingsFile.getPath() + "'" );
		
		String sectionName = null;
		Hashtable userLabels = new Hashtable();
		
		Hashtable deviceTypes = new Hashtable();
		deviceTypes.put( "201", "LCR-1000" );
		deviceTypes.put( "202", "LCR-2000" );
		deviceTypes.put( "203", "LCR-3000" );
		deviceTypes.put( "204", "LCR-4000" );
		deviceTypes.put( "10", "MCT-210" );
		deviceTypes.put( "11", "MCT-212" );
		deviceTypes.put( "12", "MCT-213" );
		deviceTypes.put( "20", "MCT-224" );
		deviceTypes.put( "21", "MCT-226" );
		deviceTypes.put( "30", "MCT-240" );
		deviceTypes.put( "31", "MCT-242" );
		deviceTypes.put( "32", "MCT-248" );
		deviceTypes.put( "40", "MCT-250" );
		deviceTypes.put( "50", "MCT-260" );
		deviceTypes.put( "60", "MCT-310" );
		deviceTypes.put( "64", "MCT-310ID" );
		deviceTypes.put( "68", "MCT-318" );
		deviceTypes.put( "69", "MCT-310IL" );
		deviceTypes.put( "70", "MCT-318L" );
		deviceTypes.put( "75", "MCT-360" );
		deviceTypes.put( "80", "MCT-370" );
		
		Hashtable woStatus = new Hashtable();
		woStatus.put( "0", ImportManagerUtil.WO_STATUS_CLOSED );
		woStatus.put( "1", ImportManagerUtil.WO_STATUS_OPEN );
		woStatus.put( "2", ImportManagerUtil.WO_STATUS_SCHEDULED );
		woStatus.put( "99", ImportManagerUtil.WO_STATUS_WAITING );
		woStatus.put( "65535", ImportManagerUtil.WO_STATUS_OPEN );
		
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].trim().length() == 0) continue;
			if (lines[i].charAt(0) == ';') continue;
			
			if (lines[i].charAt(0) == '[') {
				sectionName = lines[i];
				continue;
			}
			
			int pos = lines[i].indexOf('=');
			if (pos < 0) continue;
			String name = lines[i].substring( 0, pos );
			String value = lines[i].substring( pos+1 );
			
			if (sectionName.equalsIgnoreCase("[User Labels]")) {
				if (!value.startsWith("@"))
					userLabels.put( name, value );
			}
			else if (sectionName.equalsIgnoreCase("[Device Types]")) {
				deviceTypes.put( value, name );
			}
			else if (sectionName.equalsIgnoreCase("[Work Order Status]")) {
				woStatus.put( value, name );
			}
		}
		
		session.setAttribute( ImportManagerUtil.USER_LABELS, userLabels );
		session.setAttribute( ImportManagerUtil.DEVICE_TYPES, deviceTypes );
		session.setAttribute( ImportManagerUtil.WORK_ORDER_STATUS, woStatus );
	}
	
	private void importINIData(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			boolean selListImported = false;
			
			if (req.getParameter("S3DATA_INI").length() > 0) {
				File selListFile = new File( req.getParameter("S3DATA_INI") );
				importSelectionLists( selListFile, energyCompany );
				selListImported = true;
			}
			
			if (req.getParameter("STARS3_INI").length() > 0) {
				File appSettingsFile = new File( req.getParameter("STARS3_INI") );
				importAppSettings( appSettingsFile, session );
			}
			
			String msg = "INI file(s) imported successfully.";
			if (selListImported)
				msg += LINE_SEPARATOR + "Please go to the energy company settings page to update the appliance categories and device type list.";
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, msg);
		}
		catch (WebClientException e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to import INI file(s)");
		}
	}
	
	private Integer getTextEntryID(String text, String listName, LiteStarsEnergyCompany energyCompany) {
		if (listName.equals("ServiceCompany")) {
			if (text.equals("")) return null;
			
			ArrayList companies = energyCompany.getAllServiceCompanies();
			for (int i = 0; i < companies.size(); i++) {
				LiteServiceCompany liteCompany = (LiteServiceCompany) companies.get(i);
				if (text.equalsIgnoreCase( liteCompany.getCompanyName() ))
					return new Integer( liteCompany.getCompanyID() );
			}
		}
		else if (listName.equals("LoadType")) {
			if (text.equals("")) return null;
			
			ArrayList appCats = energyCompany.getAllApplianceCategories();
			for (int i = 0; i < appCats.size(); i++) {
				LiteApplianceCategory liteAppCat = (LiteApplianceCategory) appCats.get(i);
				if (text.equalsIgnoreCase( liteAppCat.getDescription() ))
					return new Integer( liteAppCat.getApplianceCategoryID() );
			}
		}
		else if (listName.equals("LoadGroup")) {
			if (text.equals("")) return null;
			
			StarsEnrollmentPrograms programs = energyCompany.getStarsEnrollmentPrograms();
			for (int i = 0; i < programs.getStarsApplianceCategoryCount(); i++) {
				StarsApplianceCategory category = programs.getStarsApplianceCategory(i);
				for (int j = 0; j < category.getStarsEnrLMProgramCount(); j++) {
					StarsEnrLMProgram program = category.getStarsEnrLMProgram(j);
					for (int k = 0; k < program.getAddressingGroupCount(); k++) {
						AddressingGroup group = program.getAddressingGroup(k);
						if (group.getContent().equalsIgnoreCase( text ))
							return new Integer( group.getEntryID() );
					}
				}
			}
		}
		else {
			YukonSelectionList list = energyCompany.getYukonSelectionList( listName );
			if (list != null) {
				for (int i = 0; i < list.getYukonListEntries().size(); i++) {
					YukonListEntry entry = (YukonListEntry) list.getYukonListEntries().get(i);
					if (text.equalsIgnoreCase( entry.getEntryText().trim() ))
						return new Integer( entry.getEntryID() );
				}
			}
		}
		
		return ZERO;
	}
	
	/**
	 * For some string including a label (e.g. "R2-GROUP:WHH"), get the text part of it (i.e. "WHH").
	 * If the text is empty or it represents the numerical value -1, return null.
	 * Otherwise, the text is considered "meaningful", and is returned.
	 */
	private String getMeaningfulText(String str) {
		if (str != null && str.length() > 0) {
			String text = str.substring( str.indexOf(':') + 1 );
			if (text.length() > 0) {
				try {
					if (Double.parseDouble(text) == -1.0)
						return null;
				}
				catch (NumberFormatException e) {}
				
				return text;
			}
		}
		
		return null;
	}
	
	/** Old STARS customer table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)	#used accross old STARS tables
	 * 2,3		ACCOUNT_NO
	 * 4		CUSTOMER_TYPE
	 * 5		LAST_NAME
	 * 6		FIRST_NAME
	 * 7		----
	 * 8		COMPANY_NAME
	 * 9		MAP_NO
	 * 10		STREET_ADDR1
	 * 11		STREET_ADDR2
	 * 12		CITY
	 * 13		STATE
	 * 14		ZIP_CODE
	 * 15		HOME_PHONE
	 * 16		WORK_PHONE
	 * 17		----
	 * 18		ACCOUNT_NOTES
	 */
	private String[] parseStarsCustomer(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_ACCOUNT_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 18)
			throw new WebClientException( "Incorrect number of fields in customer file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_ACCOUNT_NO] = columns[1];
		if (columns[2].length() > 0)
			fields[ImportManagerUtil.IDX_ACCOUNT_NO] += "-" + columns[2];
		fields[ImportManagerUtil.IDX_CUSTOMER_TYPE] = columns[3];
		fields[ImportManagerUtil.IDX_LAST_NAME] = columns[4];
		fields[ImportManagerUtil.IDX_FIRST_NAME] = columns[5];
		fields[ImportManagerUtil.IDX_COMPANY_NAME] = columns[7];
		fields[ImportManagerUtil.IDX_MAP_NO] = columns[8];
		fields[ImportManagerUtil.IDX_STREET_ADDR1] = columns[9];
		fields[ImportManagerUtil.IDX_STREET_ADDR2] = columns[10];
		fields[ImportManagerUtil.IDX_CITY] = columns[11];
		fields[ImportManagerUtil.IDX_STATE] = columns[12];
		fields[ImportManagerUtil.IDX_ZIP_CODE] = columns[13];
		fields[ImportManagerUtil.IDX_HOME_PHONE] = columns[14];
		fields[ImportManagerUtil.IDX_WORK_PHONE] = columns[15];
		fields[ImportManagerUtil.IDX_ACCOUNT_NOTES] = columns[17];
		
		return fields;
	}
	
	/** Old STARS service info table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		PROP_NOTES
	 * 3		SUBSTATION
	 * 4		FEEDER
	 * 5		POLE
	 * 6		TRFM_SIZE
	 * 7		SERV_VOLT
	 * 8,9		----
	 * 10-17	PROP_NOTES
	 */
	private String[] parseStarsServiceInfo(String line, Hashtable userLabels) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_ACCOUNT_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 17)
			throw new WebClientException( "Incorrect number of fields in service info file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		if (columns[1].length() > 0)
			fields[ImportManagerUtil.IDX_PROP_NOTES] += "MeterNumber: " + columns[1] + LINE_SEPARATOR;
		fields[ImportManagerUtil.IDX_SUBSTATION] = columns[2];
		fields[ImportManagerUtil.IDX_FEEDER] = columns[3];
		fields[ImportManagerUtil.IDX_POLE] = columns[4];
		fields[ImportManagerUtil.IDX_TRFM_SIZE] = columns[5];
		fields[ImportManagerUtil.IDX_SERV_VOLT] = columns[6];
		if (columns[9].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_SI_CHAR1) != null) {
			String text = getMeaningfulText( columns[9] );
			if (text != null)
				fields[ImportManagerUtil.IDX_PROP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_SI_CHAR1) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[10].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_SI_CHAR2) != null) {
			String text = getMeaningfulText( columns[10] );
			if (text != null)
				fields[ImportManagerUtil.IDX_PROP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_SI_CHAR2) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[11].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_SI_DROPBOX1) != null) {
			String text = getMeaningfulText( columns[11] );
			if (text != null)
				fields[ImportManagerUtil.IDX_PROP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_SI_DROPBOX1) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[12].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_SI_DROPBOX2) != null) {
			String text = getMeaningfulText( columns[12] );
			if (text != null)
				fields[ImportManagerUtil.IDX_PROP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_SI_DROPBOX2) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[13].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_SI_CHECKBOX1) != null) {
			String text = getMeaningfulText( columns[13] );
			if (text != null)
				fields[ImportManagerUtil.IDX_PROP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_SI_CHECKBOX1) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[14].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_SI_CHECKBOX2) != null) {
			String text = getMeaningfulText( columns[14] );
			if (text != null)
				fields[ImportManagerUtil.IDX_PROP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_SI_CHECKBOX2) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[15].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_SI_NUMERIC1) != null) {
			String text = getMeaningfulText( columns[15] );
			if (text != null)
				fields[ImportManagerUtil.IDX_PROP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_SI_NUMERIC1) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[16].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_SI_NUMERIC2) != null) {
			String text = getMeaningfulText( columns[16] );
			if (text != null)
				fields[ImportManagerUtil.IDX_PROP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_SI_NUMERIC2) + ": " + text + LINE_SEPARATOR;
		}
		
		return fields;
	}
    
	/** Old STARS inventory table
	 * COL_NUM:	COL_NAME
	 * 1		(INV_ID)
	 * 2		SERIAL_NO
	 * 3		(ACCOUNT_ID)
	 * 4		ALT_TRACK_NO
	 * 5		DEVICE_NAME
	 * 6-8		INV_NOTES		
	 * 9		DEVICE_TYPE
	 * 10-12	----
	 * 13		DEVICE_VOLT
	 * 14		RECEIVE_DATE
	 * 15		----
	 * 16		SERVICE_COMPANY
	 * 17-19	INV_NOTES
	 */
	private String[] parseStarsInventory(String line, Hashtable userLabels) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields(ImportManagerUtil.NUM_INV_FIELDS);
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 19)
			throw new WebClientException( "Incorrect number of fields in inventory file" );
		
		fields[ImportManagerUtil.IDX_INV_ID] = columns[0];
		fields[ImportManagerUtil.IDX_SERIAL_NO] = columns[1];
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[2];
		fields[ImportManagerUtil.IDX_ALT_TRACK_NO] = columns[3];
		fields[ImportManagerUtil.IDX_DEVICE_NAME] = columns[4];
		if (columns[5].length() > 0)
			fields[ImportManagerUtil.IDX_INV_NOTES] += "MapNumber: " + columns[5] + LINE_SEPARATOR;
		if (columns[6].length() > 0)
			fields[ImportManagerUtil.IDX_INV_NOTES] += "OriginAddr1: " + columns[6] + LINE_SEPARATOR;
		if (columns[7].length() > 0)
			fields[ImportManagerUtil.IDX_INV_NOTES] += "OriginAddr2: " + columns[7] + LINE_SEPARATOR;
		fields[ImportManagerUtil.IDX_DEVICE_TYPE] = columns[8];
		fields[ImportManagerUtil.IDX_DEVICE_VOLTAGE] = columns[12];
		fields[ImportManagerUtil.IDX_RECEIVE_DATE] = columns[13];
		fields[ImportManagerUtil.IDX_SERVICE_COMPANY] = columns[15];
		if (columns[16].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_DI_CHAR1) != null) {
			String text = getMeaningfulText( columns[16] );
			if (text != null)
				fields[ImportManagerUtil.IDX_INV_NOTES] += userLabels.get(ImportManagerUtil.LABEL_DI_CHAR1) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[17].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_DI_DROPBOX1) != null) {
			String text = getMeaningfulText( columns[17] );
			if (text != null)
				fields[ImportManagerUtil.IDX_INV_NOTES] += userLabels.get(ImportManagerUtil.LABEL_DI_DROPBOX1) + ": " + text + LINE_SEPARATOR;
		}
		
		return fields;
	}
	
	/** Old STARS receiver table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(INV_ID)
	 * 3		INSTALL_DATE
	 * 4-10		INV_NOTES
	 * 11		DEVICE_STATUS
	 * 12,15,18	R1_GROUP,R2_GROUP,R3_GROUP
	 * 13,16,19	INV_NOTES
	 * 14,17,20	R1_STATUS,R2_STATUS,R3_STATUS
	 */
	private String[] parseStarsReceiver(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_INV_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 20)
			throw new WebClientException( "Incorrect number of fields in receiver file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_INV_ID] = columns[1];
		fields[ImportManagerUtil.IDX_INSTALL_DATE] = columns[2];
		for (int i = 3; i <= 5; i++) {
			if (columns[i].indexOf(':') < columns[i].length() - 1)
				fields[ImportManagerUtil.IDX_INV_NOTES] += columns[i] + LINE_SEPARATOR;
		}
		if (columns[6].length() > 0)
			fields[ImportManagerUtil.IDX_INV_NOTES] += "Technician: " + columns[6] + LINE_SEPARATOR;
		if (columns[7].length() > 0)
			fields[ImportManagerUtil.IDX_INV_NOTES] += "Location: " + columns[7] + LINE_SEPARATOR;
		if (columns[8].length() > 0) {
			if (getMeaningfulText(columns[8]) != null)
				fields[ImportManagerUtil.IDX_INV_NOTES] += columns[8] + LINE_SEPARATOR;
		}
		if (columns[9].length() > 0)
			fields[ImportManagerUtil.IDX_INV_NOTES] += columns[9] + LINE_SEPARATOR;
		fields[ImportManagerUtil.IDX_DEVICE_STATUS] = columns[10];
		for (int i = 0; i < 3; i++) {
			fields[ImportManagerUtil.IDX_R1_GROUP + i] = columns[11+3*i].substring( "RX-GROUP:".length() );
			if (columns[12+3*i].length() > 0) {
				if (getMeaningfulText(columns[12+3*i]) != null)
					fields[ImportManagerUtil.IDX_INV_NOTES] += columns[12+3*i] + LINE_SEPARATOR;
			}
			fields[ImportManagerUtil.IDX_R1_STATUS + i] = columns[13+3*i].substring( "RX-STATUS:".length() );
		}
		
		return fields;
	}
	
	/** Old STARS meter table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(INV_ID)
	 * 3		DEVICE_NAME
	 * 4		INV_NOTES
	 * 5		INSTALL_DATE
	 * 6		INV_NOTES		
	 */
	private String[] parseStarsMeter(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_INV_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 6)
			throw new WebClientException( "Incorrect number of fields in meter file" );

		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_INV_ID] = columns[1];
		fields[ImportManagerUtil.IDX_DEVICE_NAME] = columns[2];
		if (columns[3].length() > 0)
			fields[ImportManagerUtil.IDX_INV_NOTES] += "Technician: " + columns[3] + LINE_SEPARATOR;
		fields[ImportManagerUtil.IDX_INSTALL_DATE] = columns[4];
		if (columns[5].length() > 0)
			fields[ImportManagerUtil.IDX_INV_NOTES] += columns[5];
		
		return fields;
	}
	
	/** Old STARS load info table
	 * COL_NUM:	COL_NAME
	 * 1		APP_ID
	 * 2		(ACCOUNT_ID)
	 * 3		(INV_ID)
	 * 4		RELAY_NUM
	 * 5		APP_DESC
	 * 6		APP_TYPE
	 * 7		APP_NOTES
	 * 8		MANUFACTURER
	 * 9-11		APP_NOTES
	 * 12		AVAIL_FOR_CTRL
	 * 13		YEAR_MADE
	 * 14-21	APP_NOTES
	 */
	private String[] parseStarsLoadInfo(String line, Hashtable userLabels) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 21)
			throw new WebClientException( "Incorrect number of fields in load info file" );
		
		fields[ImportManagerUtil.IDX_APP_ID] = columns[0];
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[1];
		fields[ImportManagerUtil.IDX_INV_ID] = columns[2];
		fields[ImportManagerUtil.IDX_RELAY_NUM] = columns[3];
		fields[ImportManagerUtil.IDX_APP_DESC] = columns[4];
		fields[ImportManagerUtil.IDX_APP_TYPE] = columns[5];
		if (columns[6].length() > 0)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "EquipCode: " + columns[6] + LINE_SEPARATOR;
		fields[ImportManagerUtil.IDX_MANUFACTURER] = columns[7];
		if (columns[8].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_CONTRACTOR1) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_CONTRACTOR1) + ": " + columns[8] + LINE_SEPARATOR;
		if (columns[9].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_CONTRACTOR2) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_CONTRACTOR2) + ": " + columns[9] + LINE_SEPARATOR;
		if (columns[10].length() > 0)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "WarrantyInfo: " + columns[10] + LINE_SEPARATOR;
		fields[ImportManagerUtil.IDX_AVAIL_FOR_CTRL] = columns[11];
		fields[ImportManagerUtil.IDX_YEAR_MADE] = columns[12];
		if (columns[13].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_LI_CHAR1) != null) {
			String text = getMeaningfulText( columns[13] );
			if (text != null)
				fields[ImportManagerUtil.IDX_APP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_LI_CHAR1) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[14].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_LI_DROPBOX1) != null) {
			String text = getMeaningfulText( columns[14] );
			if (text != null)
				fields[ImportManagerUtil.IDX_APP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_LI_DROPBOX1) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[15].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_LI_DROPBOX2) != null) {
			String text = getMeaningfulText( columns[15] );
			if (text != null)
				fields[ImportManagerUtil.IDX_APP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_LI_DROPBOX2) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[16].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_LI_DROPBOX3) != null) {
			String text = getMeaningfulText( columns[16] );
			if (text != null)
				fields[ImportManagerUtil.IDX_APP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_LI_DROPBOX3) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[17].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_LI_CHECKBOX1) != null) {
			String text = getMeaningfulText( columns[17] );
			if (text != null)
				fields[ImportManagerUtil.IDX_APP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_LI_CHECKBOX1) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[18].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_LI_CHECKBOX2) != null) {
			String text = getMeaningfulText( columns[18] );
			if (text != null)
				fields[ImportManagerUtil.IDX_APP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_LI_CHECKBOX2) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[19].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_LI_NUMERIC1) != null) {
			String text = getMeaningfulText( columns[19] );
			if (text != null)
				fields[ImportManagerUtil.IDX_APP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_LI_NUMERIC1) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[20].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_LI_NUMERIC2) != null) {
			String text = getMeaningfulText( columns[20] );
			if (text != null)
				fields[ImportManagerUtil.IDX_APP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_LI_NUMERIC2) + ": " + text + LINE_SEPARATOR;
		}
		
		return fields;
	}
	
	/** Old STARS work order table
	 * COL_NUM:	COL_NAME
	 * 1		ORDER_NO
	 * 2		(ACCOUNT_ID)
	 * 3		(INV_ID)
	 * 4-8		----
	 * 9		DATE_REPORTED
	 * 10		DATE_COMPLETED
	 * 11		ORDER_STATUS
	 * 12		ORDER_TYPE
	 * 13		ORDER_DESC
	 * 14		ACTION_TAKEN
	 * 15		TIME_SCHEDULED
	 * 16		DATE_SCHEDULED
	 * 17-19	ACTION_TAKEN
	 * 20		SERVICE_COMPANY
	 */
	private String[] parseStarsWorkOrder(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_ORDER_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 20)
			throw new WebClientException( "Incorrect number of fields in work order file" );
		
		fields[ImportManagerUtil.IDX_ORDER_NO] = columns[0];
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[1];
		fields[ImportManagerUtil.IDX_INV_ID] = columns[2];
		fields[ImportManagerUtil.IDX_DATE_REPORTED] = columns[8];
		fields[ImportManagerUtil.IDX_DATE_COMPLETED] = columns[9];
		fields[ImportManagerUtil.IDX_ORDER_STATUS] = columns[10];
		fields[ImportManagerUtil.IDX_ORDER_TYPE] = columns[11];
		fields[ImportManagerUtil.IDX_ORDER_DESC] = columns[12] + LINE_SEPARATOR;
		fields[ImportManagerUtil.IDX_ACTION_TAKEN] = columns[13] + LINE_SEPARATOR;
		fields[ImportManagerUtil.IDX_TIME_SCHEDULED] = columns[14];
		if (fields[ImportManagerUtil.IDX_TIME_SCHEDULED].length() > 0)
			fields[ImportManagerUtil.IDX_ORDER_DESC] += "Time Scheduled: " + fields[ImportManagerUtil.IDX_TIME_SCHEDULED];
		fields[ImportManagerUtil.IDX_DATE_SCHEDULED] = columns[15];
		if (columns[16].length() > 0) {
			String text = getMeaningfulText( columns[16] );
			if (text != null && text.equalsIgnoreCase("YES")) {
				fields[ImportManagerUtil.IDX_ACTION_TAKEN] += "Overtime: " + text + LINE_SEPARATOR;
				try {
					if (Double.parseDouble( columns[17] ) > 0)
						fields[ImportManagerUtil.IDX_ACTION_TAKEN] += "Overtime Hours: " + columns[17] + LINE_SEPARATOR;
				}
				catch (NumberFormatException e) {}
			}
		}
		if (columns[18].length() > 0)
			fields[ImportManagerUtil.IDX_ACTION_TAKEN] += "Technician: " + columns[18];
		fields[ImportManagerUtil.IDX_ORDER_CONTRACTOR] = columns[19];
		
		return fields;
	}
	
	/** Old STARS residence info table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		OWNERSHIP_TYPE
	 * 3		RES_TYPE
	 * 4		CONSTRUCTION_TYPE
	 * 5		DECADE_BUILT
	 * 6		SQUARE_FEET
	 * 7		NUM_OCCUPANTS
	 * 8		GENERAL_COND
	 * 9		INSULATION_DEPTH
	 * 10		MAIN_FUEL_TYPE
	 * 11		RES_NOTES
	 * 12		MAIN_COOLING_SYS
	 * 13,14	RES_NOTES
	 * 15		MAIN_HEATING_SYS
	 * 16-21	RES_NOTES
	 */
	private String[] parseStarsResidenceInfo(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_RES_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 21)
			throw new WebClientException( "Incorrect number of fields in residence info file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_OWNERSHIP_TYPE] = columns[1];
		fields[ImportManagerUtil.IDX_RES_TYPE] = columns[2];
		fields[ImportManagerUtil.IDX_CONSTRUCTION_TYPE] = columns[3];
		fields[ImportManagerUtil.IDX_DECADE_BUILT] = columns[4];
		fields[ImportManagerUtil.IDX_SQUARE_FEET] = columns[5];
		fields[ImportManagerUtil.IDX_NUM_OCCUPANTS] = columns[6];
		fields[ImportManagerUtil.IDX_GENERAL_COND] = columns[7];
		fields[ImportManagerUtil.IDX_INSULATION_DEPTH] = columns[8];
		fields[ImportManagerUtil.IDX_MAIN_FUEL_TYPE] = columns[9];
		//fields[IDX_RES_NOTES] += "SetBackThermostat: " + columns[10] + LINE_SEPARATOR;
		fields[ImportManagerUtil.IDX_MAIN_COOLING_SYS] = columns[11];
		//fields[IDX_RES_NOTES] += "CoolingSystemYearInstalled: " + columns[12] + LINE_SEPARATOR;
		//fields[IDX_RES_NOTES] += "CoolingSystemEff.: " + columns[13] + LINE_SEPARATOR;
		fields[ImportManagerUtil.IDX_MAIN_HEATING_SYS] = columns[14];
		//fields[IDX_RES_NOTES] += "HeatingSystemYearInstalled: " + columns[15] + LINE_SEPARATOR;
		//fields[IDX_RES_NOTES] += "HeatingSystemEfficiency: " + columns[16] + LINE_SEPARATOR;
		//fields[IDX_RES_NOTES] += "EnergyAudit: " + columns[17] + LINE_SEPARATOR;
		//fields[IDX_RES_NOTES] += "HeatLoss: " + columns[18] + LINE_SEPARATOR;
		//fields[IDX_RES_NOTES] += "HeatGain: " + columns[19] + LINE_SEPARATOR;
		//fields[IDX_RES_NOTES] += "EnergyManagementParticipant: " + columns[20];
		
		return fields;
	}
	
	/** Old STARS AC info table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(APP_ID)
	 * 3		APP_KW
	 * 4		APP_NOTES
	 * 5		AC_TONNAGE
	 * 6,7		NOTES
	 */
	private String[] parseStarsACInfo(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 7)
			throw new WebClientException( "Incorrect number of fields in AC info file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_APP_ID] = columns[1];
		fields[ImportManagerUtil.IDX_APP_KW] = columns[2];
		if (getMeaningfulText(columns[3]) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "Rebate: " + columns[3] + LINE_SEPARATOR;
		fields[ImportManagerUtil.IDX_AC_TONNAGE] = columns[4];
		if (getMeaningfulText(columns[5]) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "BTU_Hour: " + columns[5] + LINE_SEPARATOR;
		fields[ImportManagerUtil.IDX_APP_NOTES] += columns[6];
			
		return fields;
	}
	
	/** Old STARS WH info table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(APP_ID)
	 * 3		APP_KW
	 * 4		APP_NOTES
	 * 5		WH_NUM_GALLONS
	 * 6		WH_NUM_ELEMENTS
	 * 7		WH_ENERGY_SRC
	 * 8		WH_LOCATION
	 * 9		APP_NOTES
	 */
	private String[] parseStarsWHInfo(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 9)
			throw new WebClientException( "Incorrect number of fields in WH info file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_APP_ID] = columns[1];
		fields[ImportManagerUtil.IDX_APP_KW] = columns[2];
		if (getMeaningfulText(columns[3]) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "Rebate: " + columns[3] + LINE_SEPARATOR;
		fields[ImportManagerUtil.IDX_WH_NUM_GALLONS] = columns[4];
		fields[ImportManagerUtil.IDX_WH_NUM_ELEMENTS] = columns[5];
		fields[ImportManagerUtil.IDX_WH_ENERGY_SRC] = columns[6];
		if (columns[7].length() > 0)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "Location: " + columns[7] + LINE_SEPARATOR;
		fields[ImportManagerUtil.IDX_APP_NOTES] += columns[8];
		
		return fields;
	}
	
	/** Old STARS generator info table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(APP_ID)
	 * 3		APP_NOTES
	 * 4		GEN_FUEL_CAP
	 * 5		GEN_START_DELAY
	 * 6		GEN_CAPACITY
	 * 7		GEN_TRAN_SWITCH_MFC
	 * 8		GEN_TRAN_SWITCH_TYPE
	 */
	private String[] parseStarsGeneratorInfo(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 8)
			throw new WebClientException( "Incorrect number of fields in generator info file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_APP_ID] = columns[1];
		if (getMeaningfulText(columns[2]) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "StandbyKW: " + columns[2] + LINE_SEPARATOR;
		fields[ImportManagerUtil.IDX_GEN_FUEL_CAP] = columns[3];
		fields[ImportManagerUtil.IDX_GEN_START_DELAY] = columns[4];
		fields[ImportManagerUtil.IDX_GEN_CAPACITY] = columns[5];
		fields[ImportManagerUtil.IDX_GEN_TRAN_SWITCH_MFC] = columns[6];
		fields[ImportManagerUtil.IDX_GEN_TRAN_SWITCH_TYPE] = columns[7];
		
		return fields;
	}
	
	/** Old STARS irrigation info table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(APP_ID)
	 * 3		APP_KW
	 * 4		APP_NOTES
	 * 5		IRR_TYPE
	 * 6		IRR_ENERGY_SRC
	 * 7		IRR_HORSE_POWER
	 * 8		IRR_METER_VOLT
	 * 9		IRR_METER_LOC
	 * 10		IRR_SOIL_TYPE
	 */
	private String[] parseStarsIrrigationInfo(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 10)
			throw new WebClientException( "Incorrect number of fields in irrigation info file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_APP_ID] = columns[1];
		fields[ImportManagerUtil.IDX_APP_KW] = columns[2];
		if (getMeaningfulText(columns[3]) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "Rebate: " + columns[3] + LINE_SEPARATOR;
		fields[ImportManagerUtil.IDX_IRR_TYPE] = columns[4];
		fields[ImportManagerUtil.IDX_IRR_ENERGY_SRC] = columns[5];
		fields[ImportManagerUtil.IDX_IRR_HORSE_POWER] = columns[6];
		fields[ImportManagerUtil.IDX_IRR_METER_VOLT] = columns[7];
		fields[ImportManagerUtil.IDX_IRR_METER_LOC] += columns[8];
		fields[ImportManagerUtil.IDX_IRR_SOIL_TYPE] = columns[9];
		
		return fields;
	}
	
	/** Old STARS grain dryer info table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(APP_ID)
	 * 3		APP_KW
	 * 4		APP_NOTES
	 * 5		GDRY_TYPE
	 * 6		GDRY_ENERGY_SRC
	 * 7		GDRY_HORSE_POWER
	 * 8		GDRY_HEAT_SRC
	 * 9		GDRY_BIN_SIZE
	 */
	private String[] parseStarsGrainDryerInfo(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 9)
			throw new WebClientException( "Incorrect number of fields in grain dryer info file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_APP_ID] = columns[1];
		fields[ImportManagerUtil.IDX_APP_KW] = columns[2];
		if (getMeaningfulText(columns[3]) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "Rebate: " + columns[3] + LINE_SEPARATOR;
		fields[ImportManagerUtil.IDX_GDRY_TYPE] = columns[4];
		fields[ImportManagerUtil.IDX_GDRY_ENERGY_SRC] = columns[5];
		fields[ImportManagerUtil.IDX_GDRY_HORSE_POWER] = columns[6];
		fields[ImportManagerUtil.IDX_GDRY_HEAT_SRC] = columns[7];
		fields[ImportManagerUtil.IDX_GDRY_BIN_SIZE] = columns[8];
		
		return fields;
	}
	
	/** Old STARS heat pump info table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(APP_ID)
	 * 3		APP_KW
	 * 4		APP_NOTES
	 * 5		HP_TYPE
	 * 6		HP_SIZE
	 * 7		HP_STANDBY_SRC
	 * 8		HP_RESTART_DELAY
	 * 9		APP_NOTES
	 */
	private String[] parseStarsHeatPumpInfo(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 9)
			throw new WebClientException( "Incorrect number of fields in heat pump info file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_APP_ID] = columns[1];
		fields[ImportManagerUtil.IDX_APP_KW] = columns[2];
		if (getMeaningfulText(columns[3]) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "Rebate: " + columns[3] + LINE_SEPARATOR;
		fields[ImportManagerUtil.IDX_HP_TYPE] = columns[4];
		fields[ImportManagerUtil.IDX_HP_SIZE] = columns[5];
		fields[ImportManagerUtil.IDX_HP_STANDBY_SRC] = columns[6];
		fields[ImportManagerUtil.IDX_HP_RESTART_DELAY] = columns[7];
		fields[ImportManagerUtil.IDX_APP_NOTES] += columns[8];
		
		return fields;
	}
	
	/** Old STARS storage heat info table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(APP_ID)
	 * 3		APP_KW
	 * 4		APP_NOTES
	 * 5		SH_TYPE
	 * 6		SH_CAPACITY
	 * 7		SH_RECHARGE_TIME
	 * 8,9		APP_NOTES
	 */
	private String[] parseStarsStorageHeatInfo(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 9)
			throw new WebClientException( "Incorrect number of fields in storage heat info file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_APP_ID] = columns[1];
		fields[ImportManagerUtil.IDX_APP_KW] = columns[2];
		if (getMeaningfulText(columns[3]) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "Rebate: " + columns[3] + LINE_SEPARATOR;
		fields[ImportManagerUtil.IDX_SH_TYPE] = columns[4];
		fields[ImportManagerUtil.IDX_SH_CAPACITY] = columns[5];
		fields[ImportManagerUtil.IDX_SH_RECHARGE_TIME] = columns[6];
		if (getMeaningfulText(columns[7]) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "ContractHours: " + columns[7] + LINE_SEPARATOR;
		fields[ImportManagerUtil.IDX_APP_NOTES] += columns[8];
		
		return fields;
	}
	
	/** Old STARS dual fuel info table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(APP_ID)
	 * 3		APP_KW
	 * 4,5		APP_NOTES
	 * 6		DF_2ND_ENERGY_SRC
	 * 7		DF_2ND_CAPACITY
	 * 8		DF_SWITCH_OVER_TYPE
	 * 9		APP_NOTES
	 */
	private String[] parseStarsDualFuelInfo(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 9)
			throw new WebClientException( "Incorrect number of fields in dual fuel info file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_APP_ID] = columns[1];
		fields[ImportManagerUtil.IDX_APP_KW] = columns[2];
		if (getMeaningfulText(columns[3]) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "Rebate: " + columns[3] + LINE_SEPARATOR;
		if (getMeaningfulText(columns[4]) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "PrimarySize: " + columns[4] + LINE_SEPARATOR;
		fields[ImportManagerUtil.IDX_DF_2ND_ENERGY_SRC] = columns[5];
		fields[ImportManagerUtil.IDX_DF_2ND_CAPACITY] = columns[6];
		fields[ImportManagerUtil.IDX_DF_SWITCH_OVER_TYPE] = columns[7];
		fields[ImportManagerUtil.IDX_APP_NOTES] += columns[8];
		
		return fields;
	}
	
	/** Old STARS general info table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(APP_ID)
	 * 3		APP_KW
	 * 4,5		APP_NOTES
	 */
	private String[] parseStarsGeneralInfo(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 5)
			throw new WebClientException( "Incorrect number of fields in general info file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_APP_ID] = columns[1];
		fields[ImportManagerUtil.IDX_APP_KW] = columns[2];
		if (getMeaningfulText(columns[3]) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "Rebate: " + columns[3] + LINE_SEPARATOR;
		fields[ImportManagerUtil.IDX_APP_NOTES] += columns[4];
		
		return fields;
	}
	
	private void dumpSelectionLists(Hashtable preprocessedData, LiteStarsEnergyCompany energyCompany) {
		ArrayList lines = new ArrayList();
		
		for (int i = 0; i < ImportManagerUtil.LIST_NAMES.length; i++) {
			if (ImportManagerUtil.LIST_NAMES[i][1].length() == 0) continue;
			
			TreeMap valueIDMap = (TreeMap) preprocessedData.get( ImportManagerUtil.LIST_NAMES[i][0] );
			if (valueIDMap.size() == 0) continue;
			
			lines.add( "[" + ImportManagerUtil.LIST_NAMES[i][0] + "]" );
			
			Iterator it = valueIDMap.keySet().iterator();
			while (it.hasNext()) {
				String value = (String) it.next();
				Integer id = (Integer) valueIDMap.get( value );
				
				if (id == null) {
					if (value.trim().length() > 0)
						lines.add( "@" + value + "=" );
				}
				else {
					String line = value + "=";
					if (id.intValue() > 0) {
						if (ImportManagerUtil.LIST_NAMES[i][0].equals("ServiceCompany")) {
							ArrayList companies = energyCompany.getAllServiceCompanies();
							for (int j = 0; j < companies.size(); j++) {
								LiteServiceCompany liteCompany = (LiteServiceCompany) companies.get(j);
								if (liteCompany.getCompanyID() == id.intValue()) {
									line += "\"" + liteCompany.getCompanyName() + "\"";
									break;
								}
							}
						}
						else if (ImportManagerUtil.LIST_NAMES[i][0].equals("LoadType")) {
							ArrayList appCats = energyCompany.getAllApplianceCategories();
							for (int j = 0; j < appCats.size(); j++) {
								LiteApplianceCategory liteAppCat = (LiteApplianceCategory) appCats.get(j);
								if (liteAppCat.getApplianceCategoryID() == id.intValue()) {
									line += "\"" + liteAppCat.getDescription() + "\"";
									break;
								}
							}
						}
						else if (ImportManagerUtil.LIST_NAMES[i][0].equals("LoadGroup")) {
							line += "\"" + PAOFuncs.getYukonPAOName( id.intValue() ) + "\"";
						}
						else {
							YukonSelectionList list = energyCompany.getYukonSelectionList( ImportManagerUtil.LIST_NAMES[i][0] );
							for (int j = 0; j < list.getYukonListEntries().size(); j++) {
								YukonListEntry entry = (YukonListEntry) list.getYukonListEntries().get(j);
								if (entry.getEntryID() == id.intValue()) {
									line += "\"" + entry.getEntryText() + "\"";
									break;
								}
							}
						}
					}
					
					lines.add( line );
				}
			}
			
			lines.add( "" );
		}
		
		String[] lns = new String[ lines.size() ];
		lines.toArray( lns );
		
		String path = (String) preprocessedData.get( ImportManagerUtil.CUSTOMER_FILE_PATH );
		File custListFile = new File( path, "custlist.map" );
		try {
			StarsUtils.writeFile( custListFile, lns );
		}
		catch (IOException e) {
			CTILogger.error( e.getMessage(), e );
		}
	}
	
	private void preprocessStarsData(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		int lineNo = 0;
		
		try {
			File custFile = new File( req.getParameter("CustFile") );
			File servInfoFile = new File( req.getParameter("ServInfoFile") );
			File invFile = new File( req.getParameter("InvFile") );
			File recvrFile = new File( req.getParameter("RecvrFile") );
			File meterFile = new File( req.getParameter("MeterFile") );
			File loadInfoFile = new File( req.getParameter("LoadInfoFile") );
			File acInfoFile = new File( req.getParameter("ACInfoFile") );
			File whInfoFile = new File( req.getParameter("WHInfoFile") );
			File genInfoFile = new File( req.getParameter("GenInfoFile") );
			File irrInfoFile = new File( req.getParameter("IrrInfoFile") );
			File gdryInfoFile = new File( req.getParameter("GDryInfoFile") );
			File hpInfoFile = new File( req.getParameter("HPInfoFile") );
			File shInfoFile = new File( req.getParameter("SHInfoFile") );
			File dfInfoFile = new File( req.getParameter("DFInfoFile") );
			File genlInfoFile = new File( req.getParameter("GenlInfoFile") );
			File workOrderFile = new File( req.getParameter("WorkOrderFile") );
			File resInfoFile = new File( req.getParameter("ResInfoFile") );
			
			String[] custLines = StarsUtils.readFile( custFile );
			String[] servInfoLines = StarsUtils.readFile( servInfoFile, false );
			String[] invLines = StarsUtils.readFile( invFile );
			String[] recvrLines = StarsUtils.readFile( recvrFile, false );
			String[] meterLines = StarsUtils.readFile( meterFile, false );
			String[] loadInfoLines = StarsUtils.readFile( loadInfoFile );
			String[] acInfoLines = StarsUtils.readFile( acInfoFile, false );
			String[] whInfoLines = StarsUtils.readFile( whInfoFile, false );
			String[] genInfoLines = StarsUtils.readFile( genInfoFile, false );
			String[] irrInfoLines = StarsUtils.readFile( irrInfoFile, false );
			String[] gdryInfoLines = StarsUtils.readFile( gdryInfoFile, false );
			String[] hpInfoLines = StarsUtils.readFile( hpInfoFile, false );
			String[] shInfoLines = StarsUtils.readFile( shInfoFile, false );
			String[] dfInfoLines = StarsUtils.readFile( dfInfoFile, false );
			String[] genlInfoLines = StarsUtils.readFile( genlInfoFile, false );
			String[] workOrderLines = StarsUtils.readFile( workOrderFile );
			String[] resInfoLines = StarsUtils.readFile( resInfoFile );
			
			Hashtable preprocessedData = (Hashtable) session.getAttribute(ImportManagerUtil.PREPROCESSED_DATA);
			if (preprocessedData != null) {
				// Clear up the old data from memory
				ArrayList acctFieldsList = (ArrayList) preprocessedData.get("CustomerAccount");
				if (acctFieldsList != null) {
					acctFieldsList.clear();
					preprocessedData.remove( "CustomerAccount" );
				}
				
				ArrayList invFieldsList = (ArrayList) preprocessedData.get("Inventory");
				if (invFieldsList != null) {
					invFieldsList.clear();
					preprocessedData.remove( "Inventory" );
				}
				 
				ArrayList appFieldsList = (ArrayList) preprocessedData.get("Appliance");
				if (appFieldsList != null) {
					appFieldsList.clear();
					preprocessedData.remove( "Appliance" );
				}
				
				ArrayList orderFieldsList = (ArrayList) preprocessedData.get("WorkOrder");
				if (orderFieldsList != null) {
					orderFieldsList.clear();
					preprocessedData.remove( "WorkOrder" );
				}
				
				ArrayList resFieldsList = (ArrayList) preprocessedData.get("CustomerResidence");
				if (resFieldsList != null) {
					resFieldsList.clear();
					preprocessedData.remove( "CustomerResidence" );
				}
			}
			else
				preprocessedData = new Hashtable();
			
			Hashtable userLabels = (Hashtable) session.getAttribute( ImportManagerUtil.USER_LABELS );
			if (userLabels == null) userLabels = new Hashtable();
			Hashtable deviceTypes = (Hashtable) session.getAttribute( ImportManagerUtil.DEVICE_TYPES );
			if (deviceTypes == null) deviceTypes = new Hashtable();
			Hashtable woStatus = (Hashtable) session.getAttribute( ImportManagerUtil.WORK_ORDER_STATUS );
			if (woStatus == null) woStatus = new Hashtable();
			
			Hashtable acctIDFields = new Hashtable();	// Map from account id(Integer) to fields(String[])
			Hashtable invIDFields = new Hashtable();	// Map from inventory id(Integer) to fields(String[])
			Hashtable acctIDAppFields = new Hashtable();	// Map from account id(Integer) to (Map from appliance id (Integer) to fields(String[]))
			
			ArrayList acctFieldsList = new ArrayList();		// List of all account fields(String[])
			ArrayList invFieldsList = new ArrayList();		// List of all inventory fields(String[])
			ArrayList appFieldsList = new ArrayList();		// List of all appliance fields(String[])
			ArrayList orderFieldsList = new ArrayList();	// List of all work order fields(String[])
			ArrayList resFieldsList = new ArrayList();		// List of all residence fields(String[])
			
			// Sorted maps of import value(String) to id(Integer), filled in assignSelectionList()
			TreeMap[] valueIDMaps = new TreeMap[ ImportManagerUtil.LIST_NAMES.length ];
			for (int i = 0; i < ImportManagerUtil.LIST_NAMES.length; i++) {
				valueIDMaps[i] = (TreeMap) preprocessedData.get( ImportManagerUtil.LIST_NAMES[i][0] );
				if (valueIDMaps[i] == null) valueIDMaps[i] = new TreeMap();
			}
			
			if (custLines != null) {
				if (custFile.getName().equals("customer.map")) {
					// customer.map file format: import_account_id,db_account_id
					// Notice: the parsed lines have line# inserted at the beginning
					Hashtable acctIDMap = new Hashtable();
					
					for (int i = 0; i < custLines.length; i++) {
						lineNo = i + 1;
						String[] fields = custLines[i].split(",");
						if (fields.length != 2)
							throw new WebClientException("Invalid format of file '" + custFile.getPath() + "'");
						acctIDMap.put( Integer.valueOf(fields[0]), Integer.valueOf(fields[1]) );
					}
					
					preprocessedData.put(ImportManagerUtil.CUSTOMER_ACCOUNT_MAP, acctIDMap);
				}
				else {
					for (int i = 0; i < custLines.length; i++) {
						lineNo = i + 1;
						if (custLines[i].trim().equals("") || custLines[i].charAt(0) == '#')
							continue;
						
						String[] fields = parseStarsCustomer( custLines[i] );
						fields[ImportManagerUtil.IDX_LINE_NUM] = String.valueOf(i + 1);
						acctFieldsList.add( fields );
						acctIDFields.put( fields[ImportManagerUtil.IDX_ACCOUNT_ID], fields );
					}
				}
				
				preprocessedData.put(ImportManagerUtil.CUSTOMER_FILE_PATH, custFile.getParent());
			}
			else {
				if (preprocessedData.get(ImportManagerUtil.CUSTOMER_ACCOUNT_MAP) == null)
					throw new WebClientException("No customer information found. If you have already imported the customer file, select the generated 'customer.map' file in the 'Customer File' field");
			}
			
			if (servInfoLines != null) {
				lineNo = 0;
				for (int i = 0; i < servInfoLines.length; i++) {
					lineNo = i + 1;
					String[] fields = parseStarsServiceInfo( servInfoLines[i], userLabels );
					String[] custFields = (String[]) acctIDFields.get( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
					
					if (custFields != null) {
						for (int j = 0; j < custFields.length; j++)
							if (fields[j].length() > 0) custFields[j] = fields[j];
						
						for (int j = 0; j < ImportManagerUtil.SERVINFO_LIST_FIELDS.length; j++) {
							int listIdx = ImportManagerUtil.SERVINFO_LIST_FIELDS[j][0];
							int fieldIdx = ImportManagerUtil.SERVINFO_LIST_FIELDS[j][1];
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
								Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
								if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
							}
						}
					}
				}
			}
			
			if (invLines != null) {
				lineNo = 0;
				for (int i = 0; i < invLines.length; i++) {
					lineNo = i + 1;
					if (invLines[i].trim().equals("") || invLines[i].charAt(0) == '#')
						continue;
					
					String[] fields = parseStarsInventory( invLines[i], userLabels );
					fields[ImportManagerUtil.IDX_LINE_NUM] = String.valueOf(i + 1);
					
					if (fields[ImportManagerUtil.IDX_DEVICE_TYPE].equals("") || fields[ImportManagerUtil.IDX_DEVICE_TYPE].equals("-1"))
						continue;
					
					invFieldsList.add( fields );
					invIDFields.put( fields[ImportManagerUtil.IDX_INV_ID], fields );
					
					for (int j = 0; j < ImportManagerUtil.INV_LIST_FIELDS.length; j++) {
						int listIdx = ImportManagerUtil.INV_LIST_FIELDS[j][0];
						int fieldIdx = ImportManagerUtil.INV_LIST_FIELDS[j][1];
						
						if (fieldIdx == ImportManagerUtil.IDX_DEVICE_TYPE) {
							String text = (String) deviceTypes.get( fields[fieldIdx] );
							if (text == null)
								throw new WebClientException( "Inventory file line #" + lineNo + ": invalid device type " + fields[fieldIdx] );
							if (text.startsWith("MCT"))
								fields[fieldIdx] = "MCT";
							else
								fields[fieldIdx] = text;
						}
						
						if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
							Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
							if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
						}
					}
				}
			}
			
			if (recvrLines != null) {
				lineNo = 0;
				if (recvrFile.getName().equals("hwconfig.map")) {
					// hwconfig.map file format: import_inv_id,relay1_db_app_id,relay2_db_app_id,relay3_db_app_id
					Hashtable appIDMap = new Hashtable();
					
					for (int i = 0; i < recvrLines.length; i++) {
						lineNo = i + 1;
						String[] fields = recvrLines[i].split(",");
						if (fields.length != 4)
							throw new WebClientException("Invalid format of file '" + recvrFile.getPath() + "'");
						
						Integer invID = Integer.valueOf( fields[0] );
						int[] appIDs = new int[3];
						for (int j = 0; j < 3; j++)
							appIDs[j] = Integer.parseInt( fields[j+1] );
						
						appIDMap.put( invID, appIDs );
					}
					
					preprocessedData.put(ImportManagerUtil.HW_CONFIG_APP_MAP, appIDMap);
				}
				else {
					for (int i = 0; i < recvrLines.length; i++) {
						lineNo = i + 1;
						String[] fields = parseStarsReceiver( recvrLines[i] );
						String[] invFields = (String[]) invIDFields.get( fields[ImportManagerUtil.IDX_INV_ID] );
						
						if (invFields != null) {
							// Use hardware action field as a marker of whether an entry in the
							// inventory file has a corresponding entry in the receiver or meter file
							invFields[ImportManagerUtil.IDX_HARDWARE_ACTION] = "Receiver";
							
							for (int j = 0; j < invFields.length; j++) {
								if (fields[j].length() > 0) {
									if (j == ImportManagerUtil.IDX_INV_NOTES && invFields[j].length() > 0)
										invFields[j] += fields[j];
									else
										invFields[j] = fields[j];
								}
							}
							
							for (int j = 0; j < ImportManagerUtil.RECV_LIST_FIELDS.length; j++) {
								int listIdx = ImportManagerUtil.RECV_LIST_FIELDS[j][0];
								int fieldIdx = ImportManagerUtil.RECV_LIST_FIELDS[j][1];
								
								if (fieldIdx == ImportManagerUtil.IDX_DEVICE_STATUS) {
									if (fields[fieldIdx].equals("SwitchStatus:1"))
										fields[fieldIdx] = "Temp Unavail";
									else if (fields[fieldIdx].equals("SwitchStatus:4"))
										fields[fieldIdx] = "Unavailable";
									else if (fields[fieldIdx].equals("SwitchStatus:8"))
										fields[fieldIdx] = "Available";
								}
								
								if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
									Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
									if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
								}
							}
						}
					}
				}
			}
			
			if (meterLines != null) {
				lineNo = 0;
				for (int i = 0; i < meterLines.length; i++) {
					lineNo = i + 1;
					String[] fields = parseStarsMeter( meterLines[i] );
					String[] invFields = (String[]) invIDFields.get( fields[ImportManagerUtil.IDX_INV_ID] );
					
					if (invFields != null) {
						invFields[ImportManagerUtil.IDX_HARDWARE_ACTION] = "Meter";
						
						for (int j = 0; j < invFields.length; j++) {
							if (fields[j].length() > 0) {
								if (j == ImportManagerUtil.IDX_INV_NOTES && invFields[j].length() > 0)
									invFields[j] += fields[j];
								else
									invFields[j] = fields[j];
							}
						}
					}
				}
			}
			
			java.util.Iterator it = invFieldsList.iterator();
			while (it.hasNext()) {
				String[] fields = (String[]) it.next();
				if (fields[ImportManagerUtil.IDX_HARDWARE_ACTION].equals("")) {
					it.remove();
					invIDFields.remove( fields[ImportManagerUtil.IDX_INV_ID] );
				}
			}
			
			if (loadInfoLines != null) {
				lineNo = 0;
				if (recvrLines == null && preprocessedData.get(ImportManagerUtil.HW_CONFIG_APP_MAP) == null)
					throw new WebClientException("No hardware config information found. If you have already imported the receiver file, select the generated 'hwconfig.map' file in the 'Receiver File' field.");
				
				for (int i = 0; i < loadInfoLines.length; i++) {
					lineNo = i + 1;
					if (loadInfoLines[i].trim().equals("") || loadInfoLines[i].charAt(0) == '#')
						continue;
					
					String[] fields = parseStarsLoadInfo( loadInfoLines[i], userLabels );
					if (fields[ImportManagerUtil.IDX_APP_DESC].equals("")) continue;
					
					fields[ImportManagerUtil.IDX_LINE_NUM] = String.valueOf(i + 1);
					appFieldsList.add( fields );
					
					Hashtable appIDFields = (Hashtable) acctIDAppFields.get( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
					if (appIDFields == null) {
						appIDFields = new Hashtable();
						acctIDAppFields.put( fields[ImportManagerUtil.IDX_ACCOUNT_ID], appIDFields );
					}
					appIDFields.put( fields[ImportManagerUtil.IDX_APP_ID], fields );
					
					for (int j = 0; j < ImportManagerUtil.APP_LIST_FIELDS.length; j++) {
						int listIdx = ImportManagerUtil.APP_LIST_FIELDS[j][0];
						int fieldIdx = ImportManagerUtil.APP_LIST_FIELDS[j][1];
						if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
							Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
							if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
						}
					}
				}
			}
			
			if (acInfoLines != null) {
				lineNo = 0;
				for (int i = 0; i < acInfoLines.length; i++) {
					lineNo = i + 1;
					String[] fields = parseStarsACInfo( acInfoLines[i] );
					String[] appFields = null;
					
					Hashtable appIDFields = (Hashtable) acctIDAppFields.get( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
					if (appIDFields != null) 
						appFields = (String[]) appIDFields.get( fields[ImportManagerUtil.IDX_APP_ID] );
					
					if (appFields != null) {
						// Set the appliance category field (which will be used to decide the appliance type later)
						appFields[ImportManagerUtil.IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER);
						
						for (int j = 0; j < appFields.length; j++) {
							if (fields[j].length() > 0) {
								if (j == ImportManagerUtil.IDX_APP_NOTES && appFields[j].length() > 0)
									appFields[j] += fields[j];
								else
									appFields[j] = fields[j];
							}
						}
						
						for (int j = 0; j < ImportManagerUtil.AC_LIST_FIELDS.length; j++) {
							int listIdx = ImportManagerUtil.AC_LIST_FIELDS[j][0];
							int fieldIdx = ImportManagerUtil.AC_LIST_FIELDS[j][1];
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
								Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
								if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
							}
						}
					}
				}
			}
			
			if (whInfoLines != null) {
				lineNo = 0;
				for (int i = 0; i < whInfoLines.length; i++) {
					lineNo = i + 1;
					String[] fields = parseStarsWHInfo( whInfoLines[i] );
					String[] appFields = null;
					
					Hashtable appIDFields = (Hashtable) acctIDAppFields.get( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
					if (appIDFields != null) 
						appFields = (String[]) appIDFields.get( fields[ImportManagerUtil.IDX_APP_ID] );
					
					if (appFields != null) {
						appFields[ImportManagerUtil.IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER);
						
						for (int j = 0; j < appFields.length; j++) {
							if (fields[j].length() > 0) {
								if (j == ImportManagerUtil.IDX_APP_NOTES && appFields[j].length() > 0)
									appFields[j] += fields[j];
								else
									appFields[j] = fields[j];
							}
						}
						
						for (int j = 0; j < ImportManagerUtil.WH_LIST_FIELDS.length; j++) {
							int listIdx = ImportManagerUtil.WH_LIST_FIELDS[j][0];
							int fieldIdx = ImportManagerUtil.WH_LIST_FIELDS[j][1];
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
								Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
								if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
							}
						}
					}
				}
			}
			
			if (genInfoLines != null) {
				lineNo = 0;
				for (int i = 0; i < genInfoLines.length; i++) {
					lineNo = i + 1;
					String[] fields = parseStarsGeneratorInfo( genInfoLines[i] );
					String[] appFields = null;
					
					Hashtable appIDFields = (Hashtable) acctIDAppFields.get( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
					if (appIDFields != null) 
						appFields = (String[]) appIDFields.get( fields[ImportManagerUtil.IDX_APP_ID] );
					
					if (appFields != null) {
						appFields[ImportManagerUtil.IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GENERATOR);
						
						for (int j = 0; j < appFields.length; j++) {
							if (fields[j].length() > 0) {
								if (j == ImportManagerUtil.IDX_APP_NOTES && appFields[j].length() > 0)
									appFields[j] += fields[j];
								else
									appFields[j] = fields[j];
							}
						}
						
						for (int j = 0; j < ImportManagerUtil.GEN_LIST_FIELDS.length; j++) {
							int listIdx = ImportManagerUtil.GEN_LIST_FIELDS[j][0];
							int fieldIdx = ImportManagerUtil.GEN_LIST_FIELDS[j][1];
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
								Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
								if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
							}
						}
					}
				}
			}
			
			if (irrInfoLines != null) {
				lineNo = 0;
				for (int i = 0; i < irrInfoLines.length; i++) {
					lineNo = i + 1;
					String[] fields = parseStarsIrrigationInfo( irrInfoLines[i] );
					String[] appFields = null;
					
					Hashtable appIDFields = (Hashtable) acctIDAppFields.get( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
					if (appIDFields != null) 
						appFields = (String[]) appIDFields.get( fields[ImportManagerUtil.IDX_APP_ID] );
					
					if (appFields != null) {
						appFields[ImportManagerUtil.IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION);
						
						for (int j = 0; j < appFields.length; j++) {
							if (fields[j].length() > 0) {
								if (j == ImportManagerUtil.IDX_APP_NOTES && appFields[j].length() > 0)
									appFields[j] += fields[j];
								else
									appFields[j] = fields[j];
							}
						}
						
						for (int j = 0; j < ImportManagerUtil.IRR_LIST_FIELDS.length; j++) {
							int listIdx = ImportManagerUtil.IRR_LIST_FIELDS[j][0];
							int fieldIdx = ImportManagerUtil.IRR_LIST_FIELDS[j][1];
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
								Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
								if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
							}
						}
					}
				}
			}
			
			if (gdryInfoLines != null) {
				lineNo = 0;
				for (int i = 0; i < gdryInfoLines.length; i++) {
					lineNo = i + 1;
					String[] fields = parseStarsGrainDryerInfo( gdryInfoLines[i] );
					String[] appFields = null;
					
					Hashtable appIDFields = (Hashtable) acctIDAppFields.get( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
					if (appIDFields != null) 
						appFields = (String[]) appIDFields.get( fields[ImportManagerUtil.IDX_APP_ID] );
					
					if (appFields != null) {
						appFields[ImportManagerUtil.IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GRAIN_DRYER);
						
						for (int j = 0; j < appFields.length; j++) {
							if (fields[j].length() > 0) {
								if (j == ImportManagerUtil.IDX_APP_NOTES && appFields[j].length() > 0)
									appFields[j] += fields[j];
								else
									appFields[j] = fields[j];
							}
						}
						
						for (int j = 0; j < ImportManagerUtil.GDRY_LIST_FIELDS.length; j++) {
							int listIdx = ImportManagerUtil.GDRY_LIST_FIELDS[j][0];
							int fieldIdx = ImportManagerUtil.GDRY_LIST_FIELDS[j][1];
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
								Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
								if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
							}
						}
					}
				}
			}
			
			if (hpInfoLines != null) {
				lineNo = 0;
				for (int i = 0; i < hpInfoLines.length; i++) {
					lineNo = i + 1;
					String[] fields = parseStarsHeatPumpInfo( hpInfoLines[i] );
					String[] appFields = null;
					
					Hashtable appIDFields = (Hashtable) acctIDAppFields.get( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
					if (appIDFields != null) 
						appFields = (String[]) appIDFields.get( fields[ImportManagerUtil.IDX_APP_ID] );
					
					if (appFields != null) {
						appFields[ImportManagerUtil.IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_HEAT_PUMP);
						
						for (int j = 0; j < appFields.length; j++) {
							if (fields[j].length() > 0) {
								if (j == ImportManagerUtil.IDX_APP_NOTES && appFields[j].length() > 0)
									appFields[j] += fields[j];
								else
									appFields[j] = fields[j];
							}
						}
						
						for (int j = 0; j < ImportManagerUtil.HP_LIST_FIELDS.length; j++) {
							int listIdx = ImportManagerUtil.HP_LIST_FIELDS[j][0];
							int fieldIdx = ImportManagerUtil.HP_LIST_FIELDS[j][1];
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
								Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
								if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
							}
						}
					}
				}
			}
			
			if (shInfoLines != null) {
				lineNo = 0;
				for (int i = 0; i < shInfoLines.length; i++) {
					lineNo = i + 1;
					String[] fields = parseStarsStorageHeatInfo( shInfoLines[i] );
					String[] appFields = null;
					
					Hashtable appIDFields = (Hashtable) acctIDAppFields.get( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
					if (appIDFields != null) 
						appFields = (String[]) appIDFields.get( fields[ImportManagerUtil.IDX_APP_ID] );
					
					if (appFields != null) {
						appFields[ImportManagerUtil.IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT);
						
						for (int j = 0; j < appFields.length; j++) {
							if (fields[j].length() > 0) {
								if (j == ImportManagerUtil.IDX_APP_NOTES && appFields[j].length() > 0)
									appFields[j] += fields[j];
								else
									appFields[j] = fields[j];
							}
						}
						
						for (int j = 0; j < ImportManagerUtil.SH_LIST_FIELDS.length; j++) {
							int listIdx = ImportManagerUtil.SH_LIST_FIELDS[j][0];
							int fieldIdx = ImportManagerUtil.SH_LIST_FIELDS[j][1];
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
								Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
								if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
							}
						}
					}
				}
			}
			
			if (dfInfoLines != null) {
				lineNo = 0;
				for (int i = 0; i < dfInfoLines.length; i++) {
					lineNo = i + 1;
					String[] fields = parseStarsDualFuelInfo( dfInfoLines[i] );
					String[] appFields = null;
					
					Hashtable appIDFields = (Hashtable) acctIDAppFields.get( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
					if (appIDFields != null) 
						appFields = (String[]) appIDFields.get( fields[ImportManagerUtil.IDX_APP_ID] );
					
					if (appFields != null) {
						appFields[ImportManagerUtil.IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL);
						
						for (int j = 0; j < appFields.length; j++) {
							if (fields[j].length() > 0) {
								if (j == ImportManagerUtil.IDX_APP_NOTES && appFields[j].length() > 0)
									appFields[j] += fields[j];
								else
									appFields[j] = fields[j];
							}
						}
						
						for (int j = 0; j < ImportManagerUtil.DF_LIST_FIELDS.length; j++) {
							int listIdx = ImportManagerUtil.DF_LIST_FIELDS[j][0];
							int fieldIdx = ImportManagerUtil.DF_LIST_FIELDS[j][1];
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
								Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
								if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
							}
						}
					}
				}
			}
			
			if (genlInfoLines != null) {
				lineNo = 0;
				for (int i = 0; i < genlInfoLines.length; i++) {
					lineNo = i + 1;
					String[] fields = parseStarsGeneralInfo( genlInfoLines[i] );
					String[] appFields = null;
					
					Hashtable appIDFields = (Hashtable) acctIDAppFields.get( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
					if (appIDFields != null) 
						appFields = (String[]) appIDFields.get( fields[ImportManagerUtil.IDX_APP_ID] );
					
					if (appFields != null) {
						appFields[ImportManagerUtil.IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DEFAULT);
						
						for (int j = 0; j < appFields.length; j++) {
							if (fields[j].length() > 0) {
								if (j == ImportManagerUtil.IDX_APP_NOTES && appFields[j].length() > 0)
									appFields[j] += fields[j];
								else
									appFields[j] = fields[j];
							}
						}
					}
				}
			}
			
			if (workOrderLines != null) {
				lineNo = 0;
				for (int i = 0; i < workOrderLines.length; i++) {
					lineNo = i + 1;
					if (workOrderLines[i].trim().equals("") || workOrderLines[i].charAt(0) == '#')
						continue;
					
					String[] fields = parseStarsWorkOrder( workOrderLines[i] );
					fields[ImportManagerUtil.IDX_LINE_NUM] = String.valueOf(i + 1);
					orderFieldsList.add( fields );
					
					for (int j = 0; j < ImportManagerUtil.ORDER_LIST_FIELDS.length; j++) {
						int listIdx = ImportManagerUtil.ORDER_LIST_FIELDS[j][0];
						int fieldIdx = ImportManagerUtil.ORDER_LIST_FIELDS[j][1];
						
						if (fieldIdx == ImportManagerUtil.IDX_ORDER_STATUS) {
							String text = (String) woStatus.get( fields[fieldIdx] );
							if (text == null)
								throw new WebClientException( "Work order file line #" + lineNo + ": invalid work order status " + fields[fieldIdx] );
							if (text.equalsIgnoreCase(ImportManagerUtil.WO_STATUS_CLOSED))
								fields[fieldIdx] = "Completed";
							else if (text.equalsIgnoreCase(ImportManagerUtil.WO_STATUS_SCHEDULED))
								fields[fieldIdx] = "Scheduled";
							else if (text.equalsIgnoreCase(ImportManagerUtil.WO_STATUS_OPEN) || text.equalsIgnoreCase(ImportManagerUtil.WO_STATUS_WAITING))
								fields[fieldIdx] = "Pending";
						}
						
						if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
							Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
							if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
						}
					}
				}
			}
			
			if (resInfoLines != null) {
				lineNo = 0;
				for (int i = 0; i < resInfoLines.length; i++) {
					lineNo = i + 1;
					if (resInfoLines[i].trim().equals("") || resInfoLines[i].charAt(0) == '#')
						continue;
					
					String[] fields = parseStarsResidenceInfo( resInfoLines[i] );
					fields[ImportManagerUtil.IDX_LINE_NUM] = String.valueOf(i + 1);
					resFieldsList.add( fields );
					
					for (int j = 0; j < ImportManagerUtil.RES_LIST_FIELDS.length; j++) {
						int listIdx = ImportManagerUtil.RES_LIST_FIELDS[j][0];
						int fieldIdx = ImportManagerUtil.RES_LIST_FIELDS[j][1];
						if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
							Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
							if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
						}
					}
				}
			}
			
			preprocessedData.put( "CustomerAccount", acctFieldsList );
			preprocessedData.put( "Inventory", invFieldsList );
			preprocessedData.put( "Appliance", appFieldsList );
			preprocessedData.put( "WorkOrder", orderFieldsList );
			preprocessedData.put( "CustomerResidence", resFieldsList );
			for (int i = 0; i < ImportManagerUtil.LIST_NAMES.length; i++)
				preprocessedData.put( ImportManagerUtil.LIST_NAMES[i][0], valueIDMaps[i] );
			session.setAttribute(ImportManagerUtil.PREPROCESSED_DATA, preprocessedData);
			
			Hashtable unassignedLists = (Hashtable) session.getAttribute(ImportManagerUtil.UNASSIGNED_LISTS);
			if (unassignedLists == null) {
				unassignedLists = new Hashtable();
				session.setAttribute(ImportManagerUtil.UNASSIGNED_LISTS, unassignedLists);
			}
			
			for (int i = 0; i < ImportManagerUtil.LIST_NAMES.length; i++) {
				if (valueIDMaps[i].containsValue(ZERO) && ImportManagerUtil.LIST_NAMES[i][1].length() > 0)
					unassignedLists.put( ImportManagerUtil.LIST_NAMES[i][0], new Boolean(true) );
			}
			
			dumpSelectionLists( preprocessedData, energyCompany );
		}
		catch (WebClientException e) {
			CTILogger.error( e.getMessage(), e );
			String errorMsg = e.getMessage();
			if (lineNo > 0) errorMsg += ": line #" + lineNo;
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, errorMsg);
			redirect = referer;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to preprocess old STARS data");
			redirect = referer;
		}
	}
	
	private void assignSelectionList(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		StarsEnergyCompanySettings ecSettings =
				(StarsEnergyCompanySettings) session.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
		Hashtable selectionListTable = (Hashtable) session.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
		
		String listName = req.getParameter("ListName");
		String[] values = req.getParameterValues("ImportValue");
		String[] enabled = req.getParameterValues("Enabled");
		String[] entryIDs = req.getParameterValues("EntryID");
		String[] entryTexts = req.getParameterValues("EntryText");
		
		Hashtable preprocessedData = (Hashtable) session.getAttribute(ImportManagerUtil.PREPROCESSED_DATA);
		TreeMap valueIDMap = (TreeMap) preprocessedData.get(listName);
		
		for (int i = 0; i < values.length; i++)
			valueIDMap.put( values[i], null );
		
		ArrayList newEntries = new ArrayList();
		if (enabled != null) {
			int entryIDIdx = 0;
			for (int i = 0; i < enabled.length; i++) {
				int idx = Integer.parseInt( enabled[i] );
				
				String listEntry = req.getParameter("ListEntry" + idx);
				if (listEntry != null && listEntry.equals("New"))
					newEntries.add( values[idx] );
				else
					valueIDMap.put( values[idx], Integer.valueOf(entryIDs[entryIDIdx++]) );
			}
		}
		
		if (entryTexts != null) {
			// Create new list entries
			try {
				if (listName.equals("ServiceCompany")) {
					for (int i = 0; i < entryTexts.length; i++) {
						LiteServiceCompany liteCompany = StarsAdminUtil.createServiceCompany( entryTexts[i], energyCompany );
						valueIDMap.put( newEntries.get(i), new Integer(liteCompany.getCompanyID()) );
					}
				}
				else {
					YukonSelectionList cList = energyCompany.getYukonSelectionList(listName, false, false);
					if (cList == null)
						throw new WebClientException("Cannot update an inherited selection list.");
					
					ArrayList entries = cList.getYukonListEntries();
					
					Object[][] entryData = new Object[entries.size() + entryTexts.length][];
					for (int i = 0; i < entries.size(); i++) {
						YukonListEntry cEntry = (YukonListEntry) entries.get(i);
						entryData[i] = new Object[3];
						entryData[i][0] = new Integer( cEntry.getEntryID() );
						entryData[i][1] = cEntry.getEntryText();
						entryData[i][2] = new Integer( cEntry.getYukonDefID() );
					}
					
					for (int i = 0; i < entryTexts.length; i++) {
						entryData[entries.size()+i] = new Object[3];
						entryData[entries.size()+i][0] = ZERO;
						entryData[entries.size()+i][1] = entryTexts[i];
						entryData[entries.size()+i][2] = ZERO;
					}
					
					StarsAdminUtil.updateYukonListEntries( cList, entryData, energyCompany );
					
					StarsCustSelectionList starsList = StarsLiteFactory.createStarsCustSelectionList( cList );
					selectionListTable.put( starsList.getListName(), starsList );
					
					for (int i = 0; i < newEntries.size(); i++) {
						for (int j = 0; j < cList.getYukonListEntries().size(); j++) {
							YukonListEntry cEntry = (YukonListEntry) cList.getYukonListEntries().get(j);
							if (cEntry.getEntryText().equals(entryTexts[i]) && cEntry.getYukonDefID() == 0) {
								valueIDMap.put( newEntries.get(i), new Integer(cEntry.getEntryID()) );
								break;
							}
						}
					}
				}
			}
			catch (WebClientException e) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
				redirect = referer;
			}
			catch (Exception e) {
				CTILogger.error( e.getMessage(), e );
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to assign import values to selection list");
				redirect = referer;
			}
		}
		
		// Change the unassigned flag to false
		Hashtable unassignedLists = (Hashtable) session.getAttribute(ImportManagerUtil.UNASSIGNED_LISTS);
		unassignedLists.put(listName, new Boolean(false));
	}
	
	private void importStarsData(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		Hashtable preprocessedData = (Hashtable) session.getAttribute(ImportManagerUtil.PREPROCESSED_DATA);
		ArrayList acctFieldsList = (ArrayList) preprocessedData.get("CustomerAccount");
		ArrayList invFieldsList = (ArrayList) preprocessedData.get("Inventory");
		ArrayList appFieldsList = (ArrayList) preprocessedData.get("Appliance");
		ArrayList orderFieldsList = (ArrayList) preprocessedData.get("WorkOrder");
		ArrayList resFieldsList = (ArrayList) preprocessedData.get("CustomerResidence");
		
		dumpSelectionLists( preprocessedData, energyCompany );
		
		TreeMap[] valueIDMaps = new TreeMap[ ImportManagerUtil.LIST_NAMES.length ];
		for (int i = 0; i < ImportManagerUtil.LIST_NAMES.length; i++)
			valueIDMaps[i] = (TreeMap) preprocessedData.get( ImportManagerUtil.LIST_NAMES[i][0] );
		
		// Replace import values with ids assigned to them
		for (int i = 0; i < acctFieldsList.size(); i++) {
			String[] fields = (String[]) acctFieldsList.get(i);
			
			for (int j = 0; j < ImportManagerUtil.SERVINFO_LIST_FIELDS.length; j++) {
				int listIdx = ImportManagerUtil.SERVINFO_LIST_FIELDS[j][0];
				int fieldIdx = ImportManagerUtil.SERVINFO_LIST_FIELDS[j][1];
				Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
				fields[fieldIdx] = (id != null)? id.toString() : "0";
			}
		}
		
		for (int i = 0; i < invFieldsList.size(); i++) {
			String[] fields = (String[]) invFieldsList.get(i);
			
			for (int j = 0; j < ImportManagerUtil.INV_LIST_FIELDS.length; j++) {
				int listIdx = ImportManagerUtil.INV_LIST_FIELDS[j][0];
				int fieldIdx = ImportManagerUtil.INV_LIST_FIELDS[j][1];
				Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
				fields[fieldIdx] = (id != null)? id.toString() : "0";
			}
			
			int categoryID = InventoryUtils.getInventoryCategoryID( Integer.parseInt(fields[ImportManagerUtil.IDX_DEVICE_TYPE]), energyCompany );
			if (InventoryUtils.isLMHardware( categoryID )) {
				for (int j = 0; j < ImportManagerUtil.RECV_LIST_FIELDS.length; j++) {
					int listIdx = ImportManagerUtil.RECV_LIST_FIELDS[j][0];
					int fieldIdx = ImportManagerUtil.RECV_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
		}
		
		for (int i = 0; i < appFieldsList.size(); i++) {
			String[] fields = (String[]) appFieldsList.get(i);
			
			for (int j = 0; j < ImportManagerUtil.APP_LIST_FIELDS.length; j++) {
				int listIdx = ImportManagerUtil.APP_LIST_FIELDS[j][0];
				int fieldIdx = ImportManagerUtil.APP_LIST_FIELDS[j][1];
				Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
				fields[fieldIdx] = (id != null)? id.toString() : "0";
			}
			
			if (fields[ImportManagerUtil.IDX_APP_CAT_DEF_ID].equals("")) continue;
			int catDefID = Integer.parseInt( fields[ImportManagerUtil.IDX_APP_CAT_DEF_ID] );
			
			if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER) {
				for (int j = 0; j < ImportManagerUtil.AC_LIST_FIELDS.length; j++) {
					int listIdx = ImportManagerUtil.AC_LIST_FIELDS[j][0];
					int fieldIdx = ImportManagerUtil.AC_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
			else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER) {
				for (int j = 0; j < ImportManagerUtil.WH_LIST_FIELDS.length; j++) {
					int listIdx = ImportManagerUtil.WH_LIST_FIELDS[j][0];
					int fieldIdx = ImportManagerUtil.WH_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
			else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GENERATOR) {
				for (int j = 0; j < ImportManagerUtil.GEN_LIST_FIELDS.length; j++) {
					int listIdx = ImportManagerUtil.GEN_LIST_FIELDS[j][0];
					int fieldIdx = ImportManagerUtil.GEN_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
			else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION) {
				for (int j = 0; j < ImportManagerUtil.IRR_LIST_FIELDS.length; j++) {
					int listIdx = ImportManagerUtil.IRR_LIST_FIELDS[j][0];
					int fieldIdx = ImportManagerUtil.IRR_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
			else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GRAIN_DRYER) {
				for (int j = 0; j < ImportManagerUtil.GDRY_LIST_FIELDS.length; j++) {
					int listIdx = ImportManagerUtil.GDRY_LIST_FIELDS[j][0];
					int fieldIdx = ImportManagerUtil.GDRY_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
			else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_HEAT_PUMP) {
				for (int j = 0; j < ImportManagerUtil.HP_LIST_FIELDS.length; j++) {
					int listIdx = ImportManagerUtil.HP_LIST_FIELDS[j][0];
					int fieldIdx = ImportManagerUtil.HP_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
			else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT) {
				for (int j = 0; j < ImportManagerUtil.SH_LIST_FIELDS.length; j++) {
					int listIdx = ImportManagerUtil.SH_LIST_FIELDS[j][0];
					int fieldIdx = ImportManagerUtil.SH_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
			else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL) {
				for (int j = 0; j < ImportManagerUtil.DF_LIST_FIELDS.length; j++) {
					int listIdx = ImportManagerUtil.DF_LIST_FIELDS[j][0];
					int fieldIdx = ImportManagerUtil.DF_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
		}
		
		for (int i = 0; i < orderFieldsList.size(); i++) {
			String[] fields = (String[]) orderFieldsList.get(i);
			
			for (int j = 0; j < ImportManagerUtil.ORDER_LIST_FIELDS.length; j++) {
				int listIdx = ImportManagerUtil.ORDER_LIST_FIELDS[j][0];
				int fieldIdx = ImportManagerUtil.ORDER_LIST_FIELDS[j][1];
				Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
				fields[fieldIdx] = (id != null)? id.toString() : "0";
			}
		}
		
		for (int i = 0; i < resFieldsList.size(); i++) {
			String[] fields = (String[]) resFieldsList.get(i);
			
			for (int j = 0; j < ImportManagerUtil.RES_LIST_FIELDS.length; j++) {
				int listIdx = ImportManagerUtil.RES_LIST_FIELDS[j][0];
				int fieldIdx = ImportManagerUtil.RES_LIST_FIELDS[j][1];
				Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
				fields[fieldIdx] = (id != null)? id.toString() : "0";
			}
		}
		
		ImportStarsDataTask task = new ImportStarsDataTask(user, preprocessedData);
		long id = ProgressChecker.addTask( task );
		
		session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
		session.setAttribute(ServletUtils.ATT_REFERRER, redirect);
		redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
	}

}
