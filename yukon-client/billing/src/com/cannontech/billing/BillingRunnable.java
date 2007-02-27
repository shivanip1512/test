package com.cannontech.billing;

import com.cannontech.clientutils.CTILogger;

/**
 * Insert the type's description here.
 * Creation date: (8/31/2001 2:53:28 PM)
 * @author: 
 */
public class BillingRunnable extends java.util.Observable implements Runnable
{
	private FileFormatBase fileFormatBase = null;

/**
 * BillingThread constructor comment.
 */
public BillingRunnable() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (8/31/2001 5:15:32 PM)
 * @return com.cannontech.billing.FileFormatBase
 */
public FileFormatBase getFileFormatBase() {
	return fileFormatBase;
}
/**
 * Insert the method's description here.
 * Creation date: (8/31/2001 11:56:09 AM)
 */
public void run() 
{
	if( fileFormatBase != null )
	{
		boolean success = fileFormatBase.retrieveBillingData();

		try
		{
			if( success )
			{
				fileFormatBase.writeToFile();
			}
			else				
			{
				setChanged();
				this.notifyObservers("Unsuccessfull database query" );
			}

			setChanged();
			this.notifyObservers("Successfully created the file : " + fileFormatBase.getOutputFileName() );
		}
		catch(java.io.IOException ioe) {
			setChanged();
			this.notifyObservers("Unsuccessfull reading of file : " + fileFormatBase.getOutputFileName() );
			CTILogger.error(ioe);
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/31/2001 2:54:34 PM)
 * @param newFileFormatBase com.cannontech.billing.FileFormatBase
 */
public void setFileFormatBase(FileFormatBase newFileFormatBase) {
	fileFormatBase = newFileFormatBase;
}
}
