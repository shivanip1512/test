package com.cannontech.billing.mainprograms;

/**
 * Insert the type's description here.
 * Creation date: (5/7/2002 2:16:34 PM)
 * @author: 
 */
import com.cannontech.billing.FileFormatBase;
import com.cannontech.billing.FileFormatTypes;
import com.cannontech.database.db.device.DeviceMeterGroup;

public class BillingFile extends java.util.Observable implements Runnable
{
	private BillingFileDefaults billingDefaults = null;
	private FileFormatBase fileFormatBase = null;
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

	/**
	 * Method initialize.
	 */
	public void initialize()
	{
		billingDefaults = new BillingFileDefaults();	
	}

	/**
	 * Method main.
	 * Takes args in format of key=value for batch running.
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			char argDel = '=';
			BillingFile billingFile = new BillingFile();
			
			for ( int i = 0; i < args.length; i++)
			{
				if( i == 0)	//first loop through, verify the char '=' is our delimiter, else try ':'
				{
					if( args[i].toLowerCase().indexOf(argDel) < 0)
						argDel = ':';
				}

				String argLowerCase = (String)args[i].toLowerCase();
				
				// Check the delimiter of '=', if not found check ':'
				int startIndex = argLowerCase.indexOf('=');
				if( startIndex < 0)
					startIndex = argLowerCase.indexOf(':');
				startIndex += 1;
				
				if( argLowerCase.startsWith("format") || argLowerCase.startsWith("type"))
				{//BillingFileDefaults.formatID
					String subString = argLowerCase.substring(startIndex);
					if( subString.length() > 2 )	//we accept int values of 0 - 12ish...so far.  Anything longer must be the string value.
						billingFile.getBillingDefaults().setFormatID(FileFormatTypes.getFormatID(subString));
					else
						billingFile.getBillingDefaults().setFormatID(Integer.valueOf(subString).intValue());
				}
				else if( argLowerCase.startsWith("dem"))
				{//BillingFileDefaults.demandDaysPrevious
					String subString = argLowerCase.substring(startIndex);
					billingFile.getBillingDefaults().setDemandDaysPrev(Integer.valueOf(subString).intValue());
				}
				else if( argLowerCase.startsWith("ener"))
				{//BillingFileDefaults.energyDaysPrevious
					String subString = argLowerCase.substring(startIndex);
					billingFile.getBillingDefaults().setEnergyDaysPrev(Integer.valueOf(subString).intValue());
				}
				else if( argLowerCase.startsWith("coll") )
				{//BillingFileDefaults.billGroupTypeString=COLLECTIONGROUP
				 //BillingFileDefaults.billGroup
					billingFile.getBillingDefaults().setBillGroupType(DeviceMeterGroup.COLLECTION_GROUP);
					String subString = args[i].substring(startIndex);
					billingFile.getBillingDefaults().setBillGroup(subString);
				}
				else if( argLowerCase.startsWith("test"))
				{//BillingFileDefaults.billGroupTypeString=TESTCOLLECTIONGROUP
				 //BillingFileDefaults.billGroup
					billingFile.getBillingDefaults().setBillGroupType(DeviceMeterGroup.TEST_COLLECTION_GROUP);
					String subString = args[i].substring(startIndex);
					billingFile.getBillingDefaults().setBillGroup(subString);
				}
				else if( argLowerCase.startsWith("bill") )
				{//BillingFileDefaults.billGroupTypeString.BILLINGGROUP
				 //BillingFileDefaults.billGroup
					billingFile.getBillingDefaults().setBillGroupType(DeviceMeterGroup.BILLING_GROUP);
					String subString = args[i].substring(startIndex);
					billingFile.getBillingDefaults().setBillGroup(subString);
				}
				else if( argLowerCase.startsWith("end"))
				{//BillingFileDefaults.endDate
					String subString = argLowerCase.substring(startIndex);
					com.cannontech.util.ServletUtil.parseDateStringLiberally(subString);
					billingFile.getBillingDefaults().setEndDate(com.cannontech.util.ServletUtil.parseDateStringLiberally(subString));
				}
				else if( argLowerCase.startsWith("file") || argLowerCase.startsWith("dir"))
				{//BillingFileDefaults.outputFileDir
					String subString = argLowerCase.substring(startIndex);
					if( subString.indexOf(':') > 0)	//they remembered the whole directory
						billingFile.getBillingDefaults().setOutputFileDir(subString);
					else	//try to help out and default the directory
						billingFile.getBillingDefaults().setOutputFileDir(com.cannontech.common.util.CtiUtilities.getExportDirPath() + subString);
				}
				else if( argLowerCase.startsWith("mult"))
				{//BillingFileDefuaults.removeMultiplier
					String subString = argLowerCase.substring(startIndex);
					if( subString.startsWith("t"))
						billingFile.getBillingDefaults().setRemoveMultiplier(true);
					else
						billingFile.getBillingDefaults().setRemoveMultiplier(false);
				}
				else if( argLowerCase.startsWith("app"))
				{
					String subString = argLowerCase.substring(startIndex);
					if( subString.startsWith("t"))
						billingFile.getBillingDefaults().setAppendToFile(true);
					else 
						billingFile.getBillingDefaults().setAppendToFile(false);
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
	 * Method retrieveAllBillGroupsVector.
	 * Retrieve all possible billGroups from database relative to getBillingFileDefaults().billGroupSQLString.
	 * @return java.util.Vector
	 */
	public java.util.Vector retrieveAllBillGroupsVector()
	{
		java.util.Vector billGroupVector = new java.util.Vector();
			
		java.sql.Statement stmt = null;
		java.sql.Connection conn = null;
		java.sql.ResultSet rset = null;
		
		String sql = new String( "SELECT DISTINCT " + getBillingDefaults().getBillGroupType() + " FROM "
						+ com.cannontech.database.db.device.DeviceMeterGroup.TABLE_NAME
						+ " ORDER BY " + getBillingDefaults().getBillGroupType());
		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( dbAlias);
			stmt = conn.createStatement();	
			rset = stmt.executeQuery(sql.toString());
	
			while (rset.next())
			{
				billGroupVector.addElement(rset.getString(1));
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
	 * Method retrieveFileFormats.
	 * Retrieve possible fileformats from yukon.BillingFileFormats db table.
	 */
	public void retrieveFileFormats() 
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
		return;
	}

	/**
	 * Retrieve all billingData and writeTofile for a formatBase.
	 * @see java.lang.Runnable#run()
	 */
	public void run() 
	{
		if( fileFormatBase != null )
		{
			fileFormatBase.setBillingDefaults(getBillingDefaults());
	
			com.cannontech.clientutils.CTILogger.info("Valid entries are for meter data where: ");
			com.cannontech.clientutils.CTILogger.info("  DEMAND readings > " + getBillingDefaults().getDemandStartDate() + " AND <= " + getBillingDefaults().getEndDate());
			com.cannontech.clientutils.CTILogger.info("  ENERGY readings > " + getBillingDefaults().getEnergyStartDate() + " AND <= " + getBillingDefaults().getEndDate());
	
			boolean success = false;
			
			if (getBillingDefaults().getBillGroup().isEmpty())
				success = false;
			else
				success = fileFormatBase.retrieveBillingData(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
	
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

	/**
	 * Method encodeOutput.
	 * Retrieve all billingData and write to outputStream for a formatBase.
	 * @param out
	 * @throws IOException
	 */
	public void encodeOutput(java.io.OutputStream out) throws java.io.IOException
	{
		if( fileFormatBase != null )
		{
			fileFormatBase.setBillingDefaults(getBillingDefaults());
	
			com.cannontech.clientutils.CTILogger.info("Valid entries are for meter data where: ");
			com.cannontech.clientutils.CTILogger.info("  DEMAND readings > " + getBillingDefaults().getDemandStartDate() + " AND <= " + getBillingDefaults().getEndDate());
			com.cannontech.clientutils.CTILogger.info("  ENERGY readings > " + getBillingDefaults().getEnergyStartDate() + " AND <= " + getBillingDefaults().getEndDate());
	
			boolean success = false;
			
			if (getBillingDefaults().getBillGroup().isEmpty())
				success = false;
			else
				success = fileFormatBase.retrieveBillingData(com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );	

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
	
	/**
	 * Notify observers and write to CTILogger the notifyString.
	 * @param notifyString
	 */
	private void notify(String notifyString)
	{
		this.notifyObservers(notifyString );
		com.cannontech.clientutils.CTILogger.info(notifyString);
	}
	/**
	 * Returns the allBillGroupsVector.
	 * If null, value is set by retrieveAllBillGroupsVector().
	 * @return java.util.Vector
	 */
	public java.util.Vector getAllBillGroupsVector()
	{
		if( allBillGroupsVector == null)
			allBillGroupsVector = retrieveAllBillGroupsVector();
		return allBillGroupsVector;
	}

	/**
	 * Returns the billingDefaults.
	 * If null, value is instantiated.
	 * @return BillingFileDefaults
	 */
	public BillingFileDefaults getBillingDefaults()
	{
		if( billingDefaults == null)
			billingDefaults = new BillingFileDefaults();
		return billingDefaults;
	}

	/**
	 * Returns the fileFormatBase.
	 * @return FileFormatBase
	 */
	public FileFormatBase getFileFormatBase()
	{
		return fileFormatBase;
	}

	/**
	 * Sets the allBillGroupsVector.
	 * @param allBillGroupsVector The allBillGroupsVector to set
	 */
	public void setAllBillGroupsVector(java.util.Vector allBillGroupsVector)
	{
		this.allBillGroupsVector = allBillGroupsVector;
	}

	/**
	 * Sets the billingDefaults.
	 * @param billingDefaults The billingDefaults to set
	 */
	public void setBillingDefaults(BillingFileDefaults billingDefaults)
	{
		this.billingDefaults = billingDefaults;
	}

	/**
	 * Sets the fileFormatBase.
	 * Sets the fileFormatBase.billingDefaults to this.billingDefaults.
	 * @param fileFormatBase The fileFormatBase to set
	 */
	public void setFileFormatBase(FileFormatBase fileFormatBase)
	{
		this.fileFormatBase = fileFormatBase;
		this.fileFormatBase.setBillingDefaults(getBillingDefaults());
	}
}
