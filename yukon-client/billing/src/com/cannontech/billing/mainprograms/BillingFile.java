package com.cannontech.billing.mainprograms;

/**
 * Insert the type's description here.
 * Creation date: (5/7/2002 2:16:34 PM)
 * @author: 
 */

import com.cannontech.billing.FileFormatBase;
import com.cannontech.billing.FileFormatTypes;
public class BillingFile extends java.util.Observable implements Runnable
{
	private BillingFileDefaults billingDefaults = null;
	private FileFormatBase fileFormatBase = null;
	//private java.util.Date billingEndDate = null;
	private java.util.Vector allBillGroupsVector = null;
	
	private String dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();
/**
 * BillingFile constructor comment.
 */
public BillingFile()
{
	super();
	initialize();
}
public java.util.Vector getAllBillGroupsVector ()
{
	if (allBillGroupsVector == null)
		allBillGroupsVector = retreiveAllBillGroupsVector();
	return allBillGroupsVector;
}
public BillingFileDefaults getBillingDefaults()
{
	if( billingDefaults == null)
		billingDefaults = new BillingFileDefaults();
	return billingDefaults;
	
}
public FileFormatBase getFileFormatBase()
{
	return fileFormatBase;
}
/**
 * Insert the method's description here.
 * Creation date: (5/8/2002 3:03:15 PM)
 */
public void initialize()
{
	billingDefaults = new BillingFileDefaults();	
}
/**
 * Insert the method's description here.
 * Creation date: (3/28/2002 12:50:01 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args)
{
	try
	{
		BillingFile billingFile = new BillingFile();
		for ( int i = 0; i < args.length; i++)
		{
			String argLowerCase = (String)args[i].toLowerCase();
	
			if( argLowerCase.startsWith("format") || argLowerCase.startsWith("type"))
			{
				int startIndex = argLowerCase.indexOf("=") + 1;
				String subString = argLowerCase.substring(startIndex);
				billingFile.getBillingDefaults().setFormatID(Integer.valueOf(subString).intValue());
			}
			else if( argLowerCase.startsWith("demand"))
			{
				int startIndex = argLowerCase.indexOf("=") + 1;
				String subString = argLowerCase.substring(startIndex);
				billingFile.getBillingDefaults().setDemandDaysPrev(Integer.valueOf(subString).intValue());
			}
			else if( argLowerCase.startsWith("energy"))
			{
				int startIndex = argLowerCase.indexOf("=") + 1;
				String subString = argLowerCase.substring(startIndex);
				billingFile.getBillingDefaults().setEnergyDaysPrev(Integer.valueOf(subString).intValue());
			}
			else if( argLowerCase.startsWith("billgr"))
			{
				int startIndex = argLowerCase.indexOf("=") + 1;
				String subString = argLowerCase.substring(startIndex);
				billingFile.getBillingDefaults().setBillGroup(subString);
			}
			else if( argLowerCase.startsWith("sqlcol"))
			{
				int startIndex = argLowerCase.indexOf("=") + 1;
				String subString = argLowerCase.substring(startIndex);
				if( subString.length() == 1)	//we have the integer value of the sqlcolumn
					billingFile.getBillingDefaults().setBillGroupSQLString(Integer.valueOf(subString).intValue());
				else
					billingFile.getBillingDefaults().setBillGroupSQLString(subString);
			}
			else if( argLowerCase.startsWith("end"))
			{
				int startIndex = argLowerCase.indexOf("=") + 1;
				String subString = argLowerCase.substring(startIndex);
				com.cannontech.util.ServletUtil.parseDateStringLiberally(subString);
				billingFile.getBillingDefaults().setEndDate(com.cannontech.util.ServletUtil.parseDateStringLiberally(subString));
			}
			else if( argLowerCase.startsWith("file"))
			{
				int startIndex = argLowerCase.indexOf("=") + 1;
				String subString = argLowerCase.substring(startIndex);
				billingFile.getBillingDefaults().setOutputFile(subString);
			}
		}
		
		billingFile.setFileFormatBase( com.cannontech.billing.FileFormatFactory.createFileFormat(billingFile.getBillingDefaults().getFormatID() ));
		billingFile.run();
		billingFile.getBillingDefaults().writeDefaultsFile();
	} 
	catch (Throwable exception)
	{
		com.cannontech.clientutils.CTILogger.error("Exception occurred in main() of BillingFile");
		exception.printStackTrace(System.out);
	}
}
/**
 */
public java.util.Vector retreiveAllBillGroupsVector()
{
	java.util.Vector billGroupVector = new java.util.Vector();
		
	java.sql.Statement stmt = null;
	java.sql.Connection conn = null;
	java.sql.ResultSet rset = null;
	
	String sql = new String( "SELECT DISTINCT " + getBillingDefaults().getBillGroupSQLString() + " FROM "
					+ com.cannontech.database.db.device.DeviceMeterGroup.TABLE_NAME
					+ " ORDER BY " + getBillingDefaults().getBillGroupSQLString());
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection( dbAlias);

		stmt = conn.createStatement();	
		rset = stmt.executeQuery(sql.toString());

		while (rset.next())
		{
			billGroupVector.addElement(rset.getString(1));
		}

		//getGroupList().setListData(collectionGroupVector);
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
	}
	finally
	{
		try
		{
			if( rset != null )
				rset.close();
			if( stmt != null )
				stmt.close();
			if( conn != null )
				conn.close();
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
	}
	return billGroupVector;
}
/**
 * Insert the method's description here.
 * Creation date: (5/8/2002 9:08:12 AM)
 */
public void retreiveFileFormats() 
{
	long timer = System.currentTimeMillis();
		
	String sql = "SELECT FORMATID, FORMATTYPE FROM BILLINGFILEFORMATS" +
				" WHERE FORMATID >= 0";
	
	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;

	try
	{
		java.util.Vector formatIDVector = new java.util.Vector();
		java.util.Vector formatTypeVector = new java.util.Vector();
		
		conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
		if( conn == null )
		{
			com.cannontech.clientutils.CTILogger.info( getClass() + ":  Error getting database connection.");
			return;
		}
		else
		{
			stmt = conn.createStatement();	
			rset = stmt.executeQuery(sql.toString());
			int rowCount = 0;
			while( rset.next())
			{
				formatIDVector.add(new Integer(rset.getInt(1)));
				formatTypeVector.add( rset.getString(2));
				rowCount++;
			}

			if( rowCount > 0)
			{
				//(Re-)Initialize the validFormat..Arrays.
				int[] formatIDs = new int[rowCount];
				String[] formatTypes = new String[rowCount];
				
				for (int i = 0; i < rowCount; i++)
				{
					formatIDs[i] = ((Integer)formatIDVector.get(i)).intValue();
					formatTypes[i] = (String)formatTypeVector.get(i);
				}	
				//Copy into class arrays.
				FileFormatTypes.setValidFormatIDs(formatIDs);
				FileFormatTypes.setValidFormatTypes(formatTypes);
			}
		}
	}
			
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
	}
	finally
	{
		try
		{
			if( stmt != null )
				stmt.close();
			if( conn != null )
				conn.close();
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
	}
	//logEvent("...BASELINE DATA RETRIEVED: Took " + (System.currentTimeMillis() - timer) + 
					//" millis.", com.cannontech.common.util.LogWriter.INFO);
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (8/31/2001 11:56:09 AM)
 */
public void run() 
{
	if( fileFormatBase != null )
	{
		//tell our formatter about needing to append or not
		//fileFormatBase.setIsAppending( getAppendCheckBox().isSelected() );
		fileFormatBase.setBillingDefaults(getBillingDefaults());

		com.cannontech.clientutils.CTILogger.info(" ** FYI ** Valid entries are for meters with: ");
		com.cannontech.clientutils.CTILogger.info(" ** DEMAND readings > " + getBillingDefaults().getDemandStartDate() + " AND <= " + getBillingDefaults().getEndDate());
		com.cannontech.clientutils.CTILogger.info(" ** ENERGY readings > " + getBillingDefaults().getEnergyStartDate() + " AND <= " + getBillingDefaults().getEndDate());

		boolean success = false;
		
		if (getBillingDefaults().getBillGroup().isEmpty())
			success = false;
		else
			success = fileFormatBase.retrieveBillingData( null );	// null is the dbalias.  It's set in
																	// every method but should be updated sometime.

		try
		{
			if( success )
			{
				fileFormatBase.writeToFile();
			}
			else				
			{
				setChanged();
				notify("Unsuccessfull database query" );
			}

			setChanged();
			notify("Successfully created the file : " + fileFormatBase.getOutputFileName() + "\n" + fileFormatBase.getRecordCount() + " Valid Readings Reported.");
		}
		catch(java.io.IOException ioe)
		{
			setChanged();
			notify("Unsuccessfull reading of file : " + fileFormatBase.getOutputFileName() );
			ioe.printStackTrace();
		}
	}
}
public void setAllBillGroupsVector(java.util.Vector newBillGrpsVector)
{
	allBillGroupsVector = newBillGrpsVector;
}
public void setBillingDefaults(BillingFileDefaults newBillingFileDefaults)
{
	billingDefaults = newBillingFileDefaults ;
}
public void setFileFormatBase( FileFormatBase newFormatBase)
{
	fileFormatBase = newFormatBase;
	fileFormatBase.setBillingDefaults(getBillingDefaults());

}
public void encodeOutput(java.io.OutputStream out) throws java.io.IOException
{
	if( fileFormatBase != null )
	{
		//tell our formatter about needing to append or not
		//fileFormatBase.setIsAppending( getAppendCheckBox().isSelected() );
		fileFormatBase.setBillingDefaults(getBillingDefaults());

		com.cannontech.clientutils.CTILogger.info(" ** FYI ** Valid entries are for meters with: ");
		com.cannontech.clientutils.CTILogger.info(" ** DEMAND readings > " + getBillingDefaults().getDemandStartDate() + " AND <= " + getBillingDefaults().getEndDate());
		com.cannontech.clientutils.CTILogger.info(" ** ENERGY readings > " + getBillingDefaults().getEnergyStartDate() + " AND <= " + getBillingDefaults().getEndDate());

		boolean success = false;
		
		if (getBillingDefaults().getBillGroup().isEmpty())
			success = false;
		else
			success = fileFormatBase.retrieveBillingData( null );	// null is the dbalias.  It's set in
																	// every method but should be updated sometime.

		try
		{
			if( success )
			{
				fileFormatBase.writeToFile(out);
			}
			else				
			{
				notify("Unsuccessfull database query" );
			}
		}
		catch(java.io.IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}
private void notify(String notifyString)
{
	this.notifyObservers(notifyString );
	com.cannontech.clientutils.CTILogger.info(notifyString);
}
}
