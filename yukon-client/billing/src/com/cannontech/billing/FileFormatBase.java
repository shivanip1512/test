package com.cannontech.billing;

/**
 * Insert the type's description here.
 * Creation date: (5/18/00 2:07:05 PM)
 * @author: 
 */
import com.cannontech.billing.mainprograms.BillingFileDefaults;
import com.cannontech.billing.record.BillingRecordBase;

public abstract class FileFormatBase
{
	//used to store every line of output that will be written to the file
	// it holds Objects of type BillingRecordBase
	private java.util.Vector recordVector = null;
	private boolean appending = false;
	public static BillingFileDefaults billingDefaults = null;
	public java.util.Hashtable pointIDMultiplierHashTable = null;
	
	
	public static final int validAnalogPtOffsets[] =
	{
		2, 3, 4, 5, 6, 7, 8, 9, 22, 24, 26, 28
	};

	
	public static final int kwAnalogOffsets[] =
	{
		2, 4, 6, 8
	};
	
	public static final int kwhAnalogOffsets[] =
	{
		3, 5, 7, 9
	};
	
	public static final int kvarAnalogOffsets[] =
	{
		22, 24, 26, 28
	};
	
	public static final int validAccPtOffsets[] =
	{
		1
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
	System.out.println(this.getClass().getName() + ".closeDBConnection() must be overriden");
}
public String endDateString()
{
	java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("dd-MMM-yyyy");
	return format.format(getBillingDefaults().getEndDate());
}
public BillingFileDefaults getBillingDefaults()
{
	return billingDefaults;
}
/**
 * Insert the method's description here.
 * Creation date: (8/31/2001 2:34:47 PM)
 * @return java.lang.String
 */
public java.lang.String getInputFileName()
{
	return getBillingDefaults().getInputFileDir();
}
/**
 * Insert the method's description here.
 * Creation date: (11/29/00)
 */
public StringBuffer getOutputAsStringBuffer()
{
	StringBuffer returnBuffer = new StringBuffer();
	java.util.Vector records = getRecordVector();
	
	for(int i=0;i<records.size();i++)
	{
		String dataString = ((BillingRecordBase)records.get(i)).dataToString();
		if( dataString != null)
			returnBuffer.append(dataString);
	}

	return returnBuffer;
}
/**
 * Insert the method's description here.
 * Creation date: (8/31/2001 2:34:47 PM)
 * @return java.lang.String
 */
public java.lang.String getOutputFileName() 
{
	return getBillingDefaults().getOutputFileDir();
}

public java.util.Hashtable getPointIDMultiplierHashTable()
{
	if( pointIDMultiplierHashTable == null)
	{
		pointIDMultiplierHashTable = retrievePointIDMultiplierHashTable();
	}
	return pointIDMultiplierHashTable;
}
/**
 * Insert the method's description here.
 * Creation date: (11/29/00)
 */
public java.util.Vector getRecordVector()
{
	if( recordVector == null )
		recordVector = new java.util.Vector(150);
		
	return recordVector;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/2001 12:09:57 PM)
 * @return boolean
 */
public boolean isAppending() {
	return appending;
}
/**
 * Insert the method's description here.
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
 * Insert the method's description here.
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
 * Insert the method's description here.
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
// This method creates a hash table of MeterNumbers (keys) and AccountNumbers(values).
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
		//fnfe.printStackTrace();
		System.out.println("***********************************************************************************************");
		System.out.println("Cannot find meterAndAccountNumbers.txt attempting to get account numbers from the device names.");
		System.out.println("***********************************************************************************************");
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
 * Retrieves values from the database and inserts them in a FileFormatBase object
 * Creation date: (11/30/00)
 */
//returns true if the data retrieval was successfull
abstract public boolean retrieveBillingData(String dbAlias);

public java.util.Hashtable retrievePointIDMultiplierHashTable()
{
//	java.util.Vector returnMultipliers = new java.util.Vector();
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
			System.out.println(":  Error getting database connection.");
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

public void setBillingDefaults(BillingFileDefaults newBillingDefaults)
{
	billingDefaults = newBillingDefaults;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/2001 12:08:32 PM)
 * @param newAppending boolean
 */
public void setIsAppending(boolean newAppending) {
	appending = newAppending;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/2001 12:09:57 PM)
 */
public void writeToFile() throws java.io.IOException
{
	java.io.FileWriter outputFileWriter = new java.io.FileWriter( getOutputFileName(), isAppending() );
	outputFileWriter.write( getOutputAsStringBuffer().toString() );
	outputFileWriter.flush();
	outputFileWriter.close();
}
}
