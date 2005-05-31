package com.cannontech.billing;

/**
 * Insert the type's description here.
 * Creation date: (5/18/00 2:07:05 PM)
 * @author: 
 */
import com.cannontech.billing.mainprograms.BillingFileDefaults;
import com.cannontech.billing.record.BillingRecordBase;
import com.cannontech.billing.record.MVRSRecord;

public abstract class FileFormatBase
{
	//number of records
	private int count = 0;
	
	//used to store every line of output that will be written to the file
	// it holds Objects of type BillingRecordBase
	private java.util.Vector recordVector = null;
	public static BillingFileDefaults billingDefaults = null;
	public java.util.Hashtable pointIDMultiplierHashTable = null;
	
	public static final int validAnalogPtOffsets[] =
	{
		1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 21, 22, 23, 24, 25, 26, 27, 28, 30, 31
	};
	//Added offset 1 to AnalogPtOffsets for IED meters per David 3.0.30
	
	
	public static final int kwAnalogOffsets[] =
	{
		2, 4, 6, 8, 10
	};
	
	public static final int kwhAnalogOffsets[] =
	{
		1, 3, 5, 7, 9, 11
	};
	
	public static final int kvarAnalogOffsets[] =
	{
		22, 24, 26, 28, 30
	};

	public static final int kvarhAnalogOffsets[] =
	{
		21, 23, 25, 27, 29, 31
	};
	
	public static final int validAccPtOffsets[] =
	{
		1, 2, 3
	};
	
	public static final int validDemandAccOffsets[] =
	{
		101, 102, 103, 104
	};
	
	public static final int noOffsets_MASK = 0x0000;
	public static final int validAnalogPtOffsets_MASK = 0x0001;
	public static final int kwAnalogOffsets_MASK = 0x0002;
	public static final int kwhAnalogOffsets_MASK = 0x0004;
	public static final int kvarAnalogPtOffsets_MASK = 0x0008;
	public static final int validAccPtOffsets_MASK = 0x0016;
	public static final int kwDemandAccOffsets_MASK = 0x0032;
	public static final int kvarhAnalogOffsets_MASK = 0x0064;

	/**
	 * FileFormatBase constructor comment.
	 */
	public FileFormatBase()
	{
		super();
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (8/31/2001 5:03:58 PM)
	 */
	// Override me if you want to manually close the DBConnection
	public void closeDBConnection() 
	{
		com.cannontech.clientutils.CTILogger.info(this.getClass().getName() + ".closeDBConnection() must be overriden");
	}

	/**
	 * Returns the billingDefault endDate as a string (dd-MMM-yyyy).
	 * @return java.lang.String
	 */
	public String endDateString()
	{
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("dd-MMM-yyyy");
		return format.format(getBillingDefaults().getEndDate());
	}

	/**
	 * Returns the Billing File Defaults.
	 * @return BillingFileDefaults
	 */
	public BillingFileDefaults getBillingDefaults()
	{
		return billingDefaults;
	}
	
	/**
	 * Returns the billingDefault inputFileDir string.
	 * Creation date: (8/31/2001 2:34:47 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getInputFileName()
	{
		return getBillingDefaults().getInputFileDir();
	}

	/**
	 * Returns the record vector as a string buffer formatted
	 *  by each BillingRecordBase dataToString() format.
	 * Creation date: (11/29/00)
	 * @return java.lang.StringBuffer
	 */
	public StringBuffer getOutputAsStringBuffer()
	{
		StringBuffer returnBuffer = new StringBuffer();
		setRecordCount(0);	//reset the count
		
		if( getBillingDefaults().getFormatID() == FileFormatTypes.MVRS)//special case!!!
		{
		    // create an instance of the record and call the dataToString, this reads a file instead
		    // of being a preloaded vector of records from the database.
		    MVRSRecord mvrsRecord = new MVRSRecord();
		    mvrsRecord.setInputFile(getInputFileName());
		    returnBuffer.append(mvrsRecord.dataToString());
		    //set the record format's record count, based on the number of meter records in the file
		    setRecordCount(mvrsRecord.getNumberMeters());
		}
		else
		{
			for(int i = 0; i < getRecordVector().size(); i++)
			{
				String dataString = ((BillingRecordBase)getRecordVector().get(i)).dataToString();
				if( dataString != null)
					returnBuffer.append(dataString);
			}
		}
	
		return returnBuffer;
	}

	/**
	 * Returns the billingDefault outputFileDir string.
	 * Creation date: (8/31/2001 2:34:47 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getOutputFileName() 
	{
		return getBillingDefaults().getOutputFileDir();
	}

	/**
	 * Returns a hashtable of pointid as key and multiplier as value.
	 * @return java.util.Hashtable
	 */
	public java.util.Hashtable getPointIDMultiplierHashTable()
	{
		if( pointIDMultiplierHashTable == null)
		{
			pointIDMultiplierHashTable = retrievePointIDMultiplierHashTable();
		}
		return pointIDMultiplierHashTable;
	}

	/**
	 * Returns a vector of (billingRecordBase) records.
	 * Creation date: (11/29/00)
	 * @return java.util.Vector
	 */
	public java.util.Vector getRecordVector()
	{
		if( recordVector == null )
			recordVector = new java.util.Vector(150);
			
		return recordVector;
	}
	
	/**
	 * Returns the flag that determines if offset is valid for the static kvarAnalogOffsets values.
	 * Creation date: (3/11/2002 3:11:08 PM)
	 * @return boolean
	 * @param offset int
	 */
	public boolean isKVAR(int offset) 
	{
		for (int i = 0; i < kvarAnalogOffsets.length; i++)
		{
			if( offset == kvarAnalogOffsets[i])
				return true;
		}
		return false;
	}
	

	/**
	 * Returns the flag that determines if offset is valid for the static kvarhAnalogOffsets values.
	 * Creation date: (3/11/2002 3:11:08 PM)
	 * @return boolean
	 * @param offset int
	 */
	public boolean isKVARH(int offset) 
	{
		for (int i = 0; i < kvarhAnalogOffsets.length; i++)
		{
			if( offset == kvarhAnalogOffsets[i])
				return true;
		}
		return false;
	}
	
	/**
	 * Returns the flag that determines if offset is valid for the static kwAnalogOffsets values.
	 * Creation date: (3/11/2002 3:11:08 PM)
	 * @return boolean
	 * @param offset int
	 */
	public boolean isKW(int offset) 
	{
		for (int i = 0; i < kwAnalogOffsets.length; i++)
		{
			if( offset == kwAnalogOffsets[i])
				return true;
		}
		return false;
	}
	
	/**
	 * Returns the flag that determines if offset is valid for the static validDemandAccOffsets values.
	 * Creation date: (3/11/2002 3:11:08 PM)
	 * @return boolean
	 * @param offset int
	 */
	public boolean isKW_demand(int offset) 
	{
		for (int i = 0; i < validDemandAccOffsets.length; i++)
		{
			if( offset == validDemandAccOffsets[i])
				return true;
		}
		return false;
	}

	/**
	 * Returns the flag that determines if offset is valid for the static kwhAnalogOffsets values.
	 * Creation date: (3/11/2002 3:11:08 PM)
	 * @return boolean
	 * @param offset int
	 */
	public boolean isKWH(int offset) 
	{
		for (int i = 0; i < kwhAnalogOffsets.length; i++)
		{
			if( offset == kwhAnalogOffsets[i])
				return true;
		}
		return false;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (5/13/2002 2:35:25 PM)
	 * @param args java.lang.String[]
	 */
	public void main(String[] args)
	{
		try
		{
			FileFormatBase billingFile = null;
		} 
		catch (Throwable exception)
		{
			System.err.println("Exception occurred in main() of javax.swing.JFrame");
			exception.printStackTrace(System.out);
		}
	}
	
	/**
	 * Returns a hashtable of meternumber as key and account number as value.
	 * Collects the meterNumber/Account number set from the database unless the file 
	 *  meterAndAccountNumbers.txt exists containing this mapping.
	 * @return java.util.Hashtable
	 * @param dbAlias the database string name alias.
	 */
	public java.util.Hashtable retrieveAccountNumbers(String dbAlias)
	{
		java.util.Vector returnBillingAccountNumber = new java.util.Vector();
	
		java.util.Vector linesInFile = new java.util.Vector();
		java.util.Hashtable accountNumberHashTable = null;
		
		if (dbAlias == null)
			dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();
		try
		{
			java.io.FileReader meterAndAccountNumbersFileReader = new java.io.FileReader("../config/meterAndAccountNumbers.txt");
			//java.io.FileReader meterAndAccountNumbersFileReader = new java.io.FileReader("D:/yukon/client/config/meterAndAccountNumbers.txt");
			
			java.io.BufferedReader readBuffer = new java.io.BufferedReader(meterAndAccountNumbersFileReader);
	
			try
			{
				String tempLineString = readBuffer.readLine();
							
				while(tempLineString != null)
				{
					linesInFile.add(new String(tempLineString));
					tempLineString = readBuffer.readLine();	
				}
			}
			catch(java.io.IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
		catch(java.io.FileNotFoundException fnfe)
		{
			com.cannontech.clientutils.CTILogger.info("***********************************************************************************************");
			com.cannontech.clientutils.CTILogger.info("Cannot find meterAndAccountNumbers.txt attempting to get account numbers from the device names.");
			com.cannontech.clientutils.CTILogger.info("***********************************************************************************************");
			return null;	//with null return, meternumbers will be used in place of accountnumbers
		}
	
		if(linesInFile != null)
		{	
			java.util.Collections.sort(linesInFile);
			int hashCapacity = (linesInFile.size() + 1);
			accountNumberHashTable = new java.util.Hashtable(hashCapacity);
	
			for (int i = 0; i < linesInFile.size(); i++)
			{
				String line = (String)linesInFile.get(i);
				int commaIndex = line.indexOf(",");
				
				String keyMeterNumber = line.substring(0, commaIndex);
				String valueAccountNumber = line.substring(commaIndex + 1);
				accountNumberHashTable.put(keyMeterNumber, valueAccountNumber);
			}
		}
		return accountNumberHashTable;
	}
	
	/**
	 * Returns true if the billing data retrieval was successfull
	 * Retrieves values from the database and inserts them in a FileFormatBase object.
	 * @param dbAlias the database name string alias
	 * @return boolean
	 */
	abstract public boolean retrieveBillingData(String dbAlias);
	
	/**
	 * Returns the number of records collected.
	 * @return int
	 */
	public int getRecordCount()
	{
		if( count == 0 )
		{
			if( getRecordVector() != null)
			{
				for (int i = 0; i < getRecordVector().size(); i++)
				{
					if( !(getRecordVector().get(i) instanceof com.cannontech.billing.record.StringRecord))
						count++;
				}
			}
		}
		return count;
	}
	
	private void setRecordCount(int count_)
	{
		count = count_;
	}
	/**
	 * Returns a hashtable of pointid as key and multiplier as value.
	 * Collects the pointid/multiplier from the database.
	 * @return java.util.Hashtable
	 */
	public java.util.Hashtable retrievePointIDMultiplierHashTable()
	{

		java.util.Hashtable multiplierHashTable = new java.util.Hashtable();
		
		String sql = new String("SELECT ACC.POINTID, ACC.MULTIPLIER FROM POINTACCUMULATOR ACC");
	
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
	
			if( conn == null )
			{
				com.cannontech.clientutils.CTILogger.info(":  Error getting database connection.");
				return null;
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());
				rset = pstmt.executeQuery();
	
				while( rset.next() )
				{
					Integer pointID = new Integer(rset.getInt(1));
					Double multiplier = new Double(rset.getDouble(2));
					multiplierHashTable.put(pointID, multiplier);				
				}
				
				sql = new String("SELECT ANA.POINTID, ANA.MULTIPLIER FROM POINTANALOG ANA");
				pstmt = conn.prepareStatement(sql.toString());
				rset = pstmt.executeQuery();
				
				while( rset.next())
				{
					Integer pointID = new Integer( rset.getInt(1));
					Double multiplier = new Double( rset.getDouble(2));
					multiplierHashTable.put(pointID, multiplier);
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
				if( pstmt != null ) pstmt.close();
				if( conn != null ) conn.close();
			} 
			catch( java.sql.SQLException e2 )
			{
				e2.printStackTrace();//sometin is up
				return null;
			}	
		}
	
		return multiplierHashTable;
	}
	
	/**
	 * Set the billingFileDefaults field.
	 * @param newBillingFileDefaults com.cannontech.billing.mainprograms.BillingFileDefaults
	 */
	public void setBillingDefaults(BillingFileDefaults newBillingDefaults)
	{
		billingDefaults = newBillingDefaults;
	}
	
	/**
	 * Writes the record string to the output file.
	 */
	public void writeToFile() throws java.io.IOException
	{
		java.io.FileWriter outputFileWriter = new java.io.FileWriter( getOutputFileName(), getBillingDefaults().isAppendToFile() );
		outputFileWriter.write( getOutputAsStringBuffer().toString() );
		outputFileWriter.flush();
		outputFileWriter.close();
	}

	/**
	 * Writes the record string to the output stream.
	 * @param out java.io.OutputStream
	 */
	public void writeToFile(java.io.OutputStream out) throws java.io.IOException
	{
		out.write(getOutputAsStringBuffer().toString().getBytes());
	}
	
	public String toString()
	{
		return FileFormatTypes.getFormatType(billingDefaults.getFormatID());
	}
}
