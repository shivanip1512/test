/*
 * Created on Jan 29, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import org.apache.commons.fileupload.FileItem;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMProgramWebPublishing;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.util.ImportProblem;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.action.DeleteCustAccountAction;
import com.cannontech.stars.web.util.ImportManagerUtil;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.user.UserUtils;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UploadGenericFileTask extends TimeConsumingTask {

	FileItem genericFile = null;
	LiteStarsEnergyCompany inc = null;
	
	public UploadGenericFileTask (LiteStarsEnergyCompany company, FileItem uploadFile) 
	{
		this.genericFile = uploadFile;
		this.inc = company;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
	public String getProgressMsg() {
		return getImportProgress();
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() 
	{
		if (genericFile == null) {
			status = STATUS_ERROR;
			errorMsg = "Upload file is null.";
			return;
		}
        
        status = STATUS_RUNNING;
		
		try 
		{
			DiskFileUpload upload = new DiskFileUpload();
			String fName = genericFile.getName();
			String fieldName = genericFile.getFieldName();
			boolean isFormField = genericFile.isFormField(); 
			
			//make sure this is actually a file	
			if (isFormField == true)
			{
				String value = genericFile.getString();

				CTILogger.error( value + " is not a valid file.");
				status = STATUS_ERROR;
				throw new WebClientException( value + " is not a valid file." );
			}  
			else
			{
				// save to file
				String genericFilename; 
				File uploadFile;
				final String fs = System.getProperty( "file.separator" );
				//need to alter the filename a bit or we get the whole path
				fName = fName.substring(fName.lastIndexOf("\\"));
				
				String dirPath = ServerUtils.getStarsTempDir() + fs + "fileholder"
					+ fs + inc.getName();
				genericFilename = dirPath + fs + fName;
				
				uploadFile = new File(dirPath);
				uploadFile.mkdirs();
				uploadFile = new File(genericFilename);

				try
				{	
					genericFile.write(uploadFile);
					
					status = STATUS_FINISHED;
					return;
				}
				catch(Exception e)
				{
					status = STATUS_ERROR;
					throw e;
				}
			}
		}
		
		catch (Exception e) {
			if (status == STATUS_CANCELED) {
				errorMsg = "Operation is canceled by user.";
			}
			else {
				CTILogger.error( e.getMessage(), e );
				status = STATUS_ERROR;
				
				errorMsg = "File did not upload.  Verify file exists and is uncorrupted.";
				if (e instanceof WebClientException)
					errorMsg += ": " + e.getMessage();
			}
		}
				
	}
	
	private String getImportProgress() 
	{
		String msg = "";
		if (status == STATUS_FINISHED) 
		{
			msg = "File uploaded to server successfully.";
		}
		return msg;
	}
	
}
