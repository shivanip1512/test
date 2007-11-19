package com.cannontech.billing;

/**
 * Insert the type's description here.
 * Creation date: (5/18/00 2:07:05 PM)
 * @author: 
 */
import java.util.Hashtable;
import java.util.Vector;

import com.cannontech.billing.mainprograms.BillingFileDefaults;
import com.cannontech.billing.record.BillingRecordBase;
import com.cannontech.billing.record.MVRSRecord;
import com.cannontech.billing.record.MVRS_KetchikanRecord;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;

public abstract class FileFormatBase
{
	//number of records
	private int count = 0;
	
	//used to store every line of output that will be written to the file
	// it holds Objects of type BillingRecordBase
	private Vector<BillingRecordBase> recordVector = null;
	public static BillingFileDefaults billingDefaults = null;
	public Hashtable<Integer, Double> pointIDMultiplierHashTable = null;
	
	public static final int validAnalogPtOffsets[] =
	{
		1, 2, 3, 4, 5, 6, 7, 8, 9, 21, 22, 23, 24, 25, 26, 27, 28
	};
	//Added offset 1 to AnalogPtOffsets for IED meters per David 3.0.30
	
	
	public static final int kwAnalogOffsets[] =
	{
		2, 4, 6, 8
	};
	
	public static final int kwhAnalogOffsets[] =
	{
		1, 3, 5, 7, 9
	};
	
	public static final int kvarAnalogOffsets[] =
	{
		22, 24, 26, 28
	};

	public static final int kvarhAnalogOffsets[] =
	{
		21, 23, 25, 27
	};
	
	public static final int validAccPtOffsets[] =
	{
		1, 2, 3
	};

    public static final int validTOUAccPtOffsets[] =
	{
        1, 2, 3, 101, 121, 141, 161
	};
	
	public static final int validPeakDemandAccOffsets[] =
	{
		11 // 410, 470 (12, 13, 14 needs to be added for the other buckets at some time)
	};

    public static final int validTOUPeakDemandAccOffsets[] =
	{
	    11, 111, 131, 151, 171
	};
	
	public static final int validProfileDemandAccOffsets[] =
	{
		101, 102, 103, 104
	};
	
	// Added offset 21 for 470 frozen peak kw support
	
	
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
		
		if( getBillingDefaults().getFormatID() == FileFormatTypes.MVRS ||
				getBillingDefaults().getFormatID() == FileFormatTypes.MVRS_KETCHIKAN)//special case!!!
		{
			MVRSRecord mvrsRecord;
			// create an instance of the record and call the dataToString, this reads a file instead
			// of being a preloaded vector of records from the database.
			if(getBillingDefaults().getFormatID() == FileFormatTypes.MVRS_KETCHIKAN)
				mvrsRecord = new MVRS_KetchikanRecord();
			else 
				mvrsRecord = new MVRSRecord();

			mvrsRecord.setInputFile(getBillingDefaults().getInputFileDir());
			returnBuffer.append(mvrsRecord.dataToString());
			//set the record format's record count, based on the number of meter records in the file
			setRecordCount(mvrsRecord.getNumberMeters());
		}
		else
		{
			for(int i = 0; i < getRecordVector().size(); i++)
			{
				String dataString = getRecordVector().get(i).dataToString();
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
	public Hashtable<Integer, Double> getPointIDMultiplierHashTable()
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
	public Vector<BillingRecordBase> getRecordVector()
	{
		if( recordVector == null )
			recordVector = new Vector<BillingRecordBase>(150);
			
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
	 * Returns the flag that determines if offset is valid for the static validProfileDemandAccOffsets values.
	 * Creation date: (3/11/2002 3:11:08 PM)
	 * @return boolean
	 * @param offset int
	 */
	public boolean isKW_profileDemand(int offset) 
	{
		for (int i = 0; i < validProfileDemandAccOffsets.length; i++)
		{
			if( offset == validProfileDemandAccOffsets[i])
				return true;
		}
		return false;
	}
	/**
	 * Returns the flag that determines if offset is valid for the static validPeakDemandAccOffsets values.
	 * Creation date: (3/11/2002 3:11:08 PM)
	 * @return boolean
	 * @param offset int
	 */
	public boolean isKW_peakDemand(int offset) 
	{
		for (int i = 0; i < validPeakDemandAccOffsets.length; i++)
		{
			if( offset == validPeakDemandAccOffsets[i])
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
	 * Returns a hashtable of meternumber as key and account number as value.
	 * Collects the meterNumber/Account number set from the database unless the file 
	 *  meterAndAccountNumbers.txt exists containing this mapping.
	 * @return java.util.Hashtable
	 * @param dbAlias the database string name alias.
	 */
	public Hashtable<String, String> retrieveAccountNumbers()
	{
		Vector <String> linesInFile = new Vector<String>();
		Hashtable <String, String> accountNumberHashTable = null;
		
		try {
			java.io.FileReader meterAndAccountNumbersFileReader = new java.io.FileReader("../config/meterAndAccountNumbers.txt");
			java.io.BufferedReader readBuffer = new java.io.BufferedReader(meterAndAccountNumbersFileReader);
	
			try {
				String tempLineString = readBuffer.readLine();
							
				while(tempLineString != null) {
					linesInFile.add(new String(tempLineString));
					tempLineString = readBuffer.readLine();	
				}
			}
			catch(java.io.IOException ioe) {
				CTILogger.error(ioe);
			}
		}
		catch(java.io.FileNotFoundException fnfe) {
			CTILogger.info("***********************************************************************************************");
			CTILogger.info("Cannot find meterAndAccountNumbers.txt attempting to get account numbers from the device names.");
			CTILogger.info("***********************************************************************************************");
			return null;	//with null return, meternumbers will be used in place of accountnumbers
		}
	
		if(linesInFile != null)
		{	
			java.util.Collections.sort(linesInFile);
			int hashCapacity = (linesInFile.size() + 1);
			accountNumberHashTable = new Hashtable<String, String>(hashCapacity);
	
			for (int i = 0; i < linesInFile.size(); i++) {
				String line = linesInFile.get(i);
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
	abstract public boolean retrieveBillingData();
	
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
	public Hashtable<Integer, Double> retrievePointIDMultiplierHashTable()
	{
		Hashtable<Integer, Double> multiplierHashTable = new Hashtable<Integer, Double>();
		
		String sql = new String("SELECT ACC.POINTID, ACC.MULTIPLIER FROM POINTACCUMULATOR ACC");
	
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
		try
		{
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	
			if( conn == null ) {
				com.cannontech.clientutils.CTILogger.info(":  Error getting database connection.");
				return null;
			}
			else {
				pstmt = conn.prepareStatement(sql.toString());
				rset = pstmt.executeQuery();
	
				while( rset.next() ) {
					Integer pointID = new Integer(rset.getInt(1));
					Double multiplier = new Double(rset.getDouble(2));
					multiplierHashTable.put(pointID, multiplier);				
				}
				
				sql = new String("SELECT ANA.POINTID, ANA.MULTIPLIER FROM POINTANALOG ANA");
				pstmt = conn.prepareStatement(sql.toString());
				rset = pstmt.executeQuery();
				
				while( rset.next()) {
					Integer pointID = new Integer( rset.getInt(1));
					Double multiplier = new Double( rset.getDouble(2));
					multiplierHashTable.put(pointID, multiplier);
				}
			}
		}
		catch( java.sql.SQLException e ) {
			CTILogger.error(e);
		}
		finally
		{
			SqlUtils.close(rset, pstmt, conn );
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
