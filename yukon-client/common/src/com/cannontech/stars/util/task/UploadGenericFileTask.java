/*
 * Created on Jan 29, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UploadGenericFileTask extends TimeConsumingTask {

	MultipartFile genericFile = null;
	LiteStarsEnergyCompany inc = null;
	
	public UploadGenericFileTask (LiteStarsEnergyCompany company, MultipartFile uploadFile) 
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
	        String fName = genericFile.getOriginalFilename();
			
			//make sure this is actually a file	
			if (genericFile == null) {
				String value = genericFile.getName();

				CTILogger.error( value + " is not a valid file.");
				status = STATUS_ERROR;
				throw new WebClientException( value + " is not a valid file." );
			} else {
				// save to file
				String genericFilename; 
				File uploadFile;
				final String fs = System.getProperty( "file.separator" );
				
				String dirPath = StarsUtils.getStarsTempDir() + fs + "fileholder"
					+ fs + inc.getName();
				genericFilename = dirPath + fs + fName;
				
				uploadFile = new File(dirPath);
				uploadFile.mkdirs();
				uploadFile = new File(genericFilename);

				try	{
				    genericFile.transferTo(uploadFile);
					
					status = STATUS_FINISHED;
					return;
				} catch(Exception e) {
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
