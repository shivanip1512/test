package com.cannontech.billing;

/**
 * Insert the type's description here.
 * Creation date: (5/18/00 2:07:05 PM)
 * @author: 
 */
import java.util.Hashtable;
import java.util.Vector;

import com.cannontech.billing.format.simple.SimpleBillingFormatBase;
import com.cannontech.billing.record.BillingRecordBase;
import com.cannontech.billing.record.MVRSRecord;
import com.cannontech.billing.record.MVRS_KetchikanRecord;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;

public abstract class FileFormatBase extends SimpleBillingFormatBase
{
	//number of records
	private int count = 0;
	
	//used to store every line of output that will be written to the file
	// it holds Objects of type BillingRecordBase
	private Vector<BillingRecordBase> recordVector = null;
	private Hashtable<Integer, Double> pointIDMultiplierHashTable = null;
	
	/**
	 * FileFormatBase constructor comment.
	 */
	public FileFormatBase()
	{
		super();
	}

	/**
	 * Returns the record vector as a string buffer formatted
	 *  by each BillingRecordBase dataToString() format.
	 * Creation date: (11/29/00)
	 * @return java.lang.StringBuffer
	 */
	private StringBuffer getOutputAsStringBuffer()
	{
		StringBuffer returnBuffer = new StringBuffer();
		this.count = 0;	//reset the count
		
		if( getBillingFileDefaults().getFormatID() == FileFormatTypes.MVRS ||
				getBillingFileDefaults().getFormatID() == FileFormatTypes.MVRS_KETCHIKAN)//special case!!!
		{
			MVRSRecord mvrsRecord;
			// create an instance of the record and call the dataToString, this reads a file instead
			// of being a preloaded vector of records from the database.
			if(getBillingFileDefaults().getFormatID() == FileFormatTypes.MVRS_KETCHIKAN)
				mvrsRecord = new MVRS_KetchikanRecord();
			else 
				mvrsRecord = new MVRSRecord();

			mvrsRecord.setInputFile(getBillingFileDefaults().getInputFileDir());
			returnBuffer.append(mvrsRecord.dataToString());
			//set the record format's record count, based on the number of meter records in the file
			this.count = mvrsRecord.getNumberMeters();
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
	 * Returns a hashtable of pointid as key and multiplier as value.
	 * @return java.util.Hashtable
	 */
	public Hashtable<Integer, Double> getPointIDMultiplierHashTable()
	{
		if( pointIDMultiplierHashTable == null) {
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
		if( recordVector == null ) {
			recordVector = new Vector<BillingRecordBase>(150);
		}			
		return recordVector;
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
	@Override
	public int getReadingCount()
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
	 * Writes the record string to the output stream.
	 * @param out java.io.OutputStream
	 */
	@Override
	public boolean writeToFile(java.io.OutputStream out) throws java.io.IOException {

		if (getBillingFileDefaults().getDeviceGroups().isEmpty()) {
			return false;
		}

		boolean success = retrieveBillingData( );
		if( success ) {
			out.write(getOutputAsStringBuffer().toString().getBytes());
		}
		return success;
	}
	
	public String toString() {
		return FileFormatTypes.getFormatType(billingFileDefaults.getFormatID());
	}
}
