package com.cannontech.billing.record;

import com.cannontech.common.login.ClientSession;
import com.cannontech.roles.amr.BillingRole;

/**
 * Insert the type's description here.
 * Creation date: (6/28/00 11:55:04 AM)
 * @author: 
 */
public class CADPRecord implements BillingRecordBase
{
	private String coopId = null;
	private String pageCenter = null;
	private String tranNumber = null;
	private String batchDate = null;
	private String batchNumber = null;
	private java.util.Vector meterNumberVector = null;
	private java.util.Vector kwhReadingVector = null;
	private java.util.Vector kwReadingVector = null;
	private java.util.Vector kvarReadingVector = null;
	private String pageNumber = null;
	private String filler = null;

	private static java.text.SimpleDateFormat DATE_FORMAT = new java.text.SimpleDateFormat("MMddyy");
	//received actual date_format from Ben 5/29/02.  SN
//	private static java.text.SimpleDateFormat DATE_FORMAT = new java.text.SimpleDateFormat("yyddMM");

	java.text.DecimalFormat decimalFormat5v0 = new java.text.DecimalFormat("00000");
	java.text.DecimalFormat decimalFormat4v3 = new java.text.DecimalFormat("0000.000");
/**
 * CADPFormat constructor comment.
 */
public CADPRecord()
{
	super();
}
/**
 * CADPFormat constructor comment.
 */
public CADPRecord(String newCoopId, String newPageCenter, String newTranNumber, 
	String newbatchDate, String newBatchNumber, java.util.Vector newMeterNumbers, 
	java.util.Vector newKwhReadings, java.util.Vector newKwReadings, 
	java.util.Vector newKvarReadings, String newPageNumber)
{
	super();
	setCoopId(coopId);
	setPageCenter(newPageCenter);
	setTranNumber(newTranNumber);
	setBatchDate(newbatchDate);
	setBatchNumber(newBatchNumber);
	setMeterNumberVector(newMeterNumbers);
	setKwhReadingVector(newKwhReadings);
	setKwReadingVector(newKwReadings);
	setKvarReadingVector(newKvarReadings);
	setPageNumber(newPageNumber);
	setFiller("    ");
}
/**
 * CADPFormat constructor comment.
 */
public CADPRecord(java.util.Vector newMeterNumbers, java.util.Vector newKwhReadings, 
	java.util.Vector newKwReadings, java.util.Vector newKvarReadings)
{
	super();
	setMeterNumberVector(newMeterNumbers);
	setKwhReadingVector(newKwhReadings);
	setKwReadingVector(newKwReadings);
	setKvarReadingVector(newKvarReadings);
}
/**
 * dataToString method comment.
 */
public String dataToString()
{
	int pass = 0;
		
	StringBuffer writeToFile = new StringBuffer();
	
	writeToFile.append(getCoopId());
	
	writeToFile.append(getPageCenter());
	
	writeToFile.append(getTranNumber());
	
	writeToFile.append(getBatchDate());

	writeToFile.append(getBatchNumber())	;

	for (int i = 0; i < 8; i++)
	{
		String tempMeterNum = getMeterNumberVector().get(i).toString();
		if ( tempMeterNum.length() > 10)
			tempMeterNum = tempMeterNum.substring(0, 10);
		
		int meterNumLength = tempMeterNum.length();
		for ( int j = 0; j < (10 - meterNumLength); j++)
		{
			writeToFile.append("0");
		}
		writeToFile.append(tempMeterNum);


		writeToFile.append(decimalFormat5v0.format(((Double)getKwhReadingVector().get(i)).doubleValue()));

		if( ((Double)getKwReadingVector().get(i)).doubleValue() == 0)
			writeToFile.append("       ");
		else
		{
			String tempKwReadingString = decimalFormat4v3.format(((Double)getKwReadingVector().get(i)).doubleValue());

			for(int j=0;j<tempKwReadingString.length();j++)
			{
				if(tempKwReadingString.charAt(j) != '.')
				{
					writeToFile.append(tempKwReadingString.charAt(j));
				}
			}
		}
	
		if( ((Double)getKvarReadingVector().get(i)).doubleValue() == 0)
			writeToFile.append("     ");
		else
		{
			String tempKvarReadingString = decimalFormat5v0.format(((Double)getKvarReadingVector().get(i)).doubleValue());
			for(int j = 0; j < tempKvarReadingString.length();j++)
			{
				if(tempKvarReadingString.charAt(j) != '.')
				{
					writeToFile.append(tempKvarReadingString.charAt(j));
				}
			}
		}
	}

	Integer pgNum = new Integer( Integer.valueOf(getPageNumber()).intValue() + 1);
	String newPgNum = new String( pgNum.toString() );
	setPageNumber(newPgNum);
	
	int pgNumLength = getPageNumber().length();
	for ( int j = 0; j < (4 - pgNumLength); j++)
	{
		writeToFile.append("0");
	}

	writeToFile.append(getPageNumber());

	writeToFile.append(getFiller());
	
	writeToFile.append("\r\n");
	
	return writeToFile.toString();

}
/**
 * Insert the method's description here.
 * Creation date: (11/30/2000 4:19:40 PM)
 * @return java.lang.String
 */
public java.lang.String getBatchDate()
{
	if( batchDate == null)
	{
		java.util.Date today = new java.util.Date();
		batchDate = DATE_FORMAT.format(today);
	}
	return batchDate;
}
/**
 * Insert the method's description here.
 * Creation date: (11/30/2000 4:19:40 PM)
 * @return java.lang.String
 */
public java.lang.String getBatchNumber()
{
	if( batchNumber == null)
		batchNumber = "999";
	return batchNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (11/30/2000 4:19:40 PM)
 * @return java.lang.String
 */
public java.lang.String getCoopId()
{
	if( coopId == null)
	{
		coopId = ClientSession.getInstance().getRolePropertyValue( BillingRole.COOP_ID_CADP_ONLY );
		
		if( coopId == null )
		{
			com.cannontech.clientutils.CTILogger.info(" Missing 'coop_id' in config.properties.");

			String value = javax.swing.JOptionPane.showInputDialog(
				null, "Please enter your 5 digit coop-Id now: ", 
				"Add coop_id to config.properties", javax.swing.JOptionPane.WARNING_MESSAGE);

			if( value != null)
			{
				if( value.length() > 5)
					value = value.substring(0, 5);
				else if( value.length() < 5)
				{
					for (int i = value.length(); i < 5; i++)
					{
						value += value + " ";
					}
				}
				coopId = value;
			}
			else
				coopId = "00000";
		}
	}

	return coopId;
}
/**
 * Insert the method's description here.
 * Creation date: (11/30/2000 4:19:40 PM)
 * @return java.lang.String
 */
public java.lang.String getFiller()
{
	if( filler == null)
		filler = "    ";	//4 blanks
	return filler;
}
/**
 * Insert the method's description here.
 * Creation date: (11/30/2000 4:19:40 PM)
 * @return java.util.Vector
 */
public java.util.Vector getKvarReadingVector() {
	return kvarReadingVector;
}
/**
 * Insert the method's description here.
 * Creation date: (11/30/2000 4:19:40 PM)
 * @return java.util.Vector
 */
public java.util.Vector getKwhReadingVector() {
	return kwhReadingVector;
}
/**
 * Insert the method's description here.
 * Creation date: (11/30/2000 4:19:40 PM)
 * @return java.util.Vector
 */
public java.util.Vector getKwReadingVector() {
	return kwReadingVector;
}
/**
 * Insert the method's description here.
 * Creation date: (11/30/2000 4:19:40 PM)
 * @return java.util.Vector
 */
public java.util.Vector getMeterNumberVector() {
	return meterNumberVector;
}
/**
 * Insert the method's description here.
 * Creation date: (11/30/2000 4:19:40 PM)
 * @return java.lang.String
 */
public java.lang.String getPageCenter()
{
	if( pageCenter == null)
		pageCenter = "99";
	return pageCenter;
}
/**
 * Insert the method's description here.
 * Creation date: (11/30/2000 4:19:40 PM)
 * @return java.lang.String
 */
public java.lang.String getPageNumber()
{
	if( pageNumber == null)
		pageNumber = "0";
	return pageNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (11/30/2000 4:19:40 PM)
 * @return java.lang.String
 */
public java.lang.String getTranNumber()
{
	if( tranNumber == null)
		tranNumber = "062";
	return tranNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (11/30/2000 4:19:40 PM)
 * @param newBatchDate java.lang.String
 */
public void setBatchDate(java.lang.String newBatchDate)
{
	batchDate = newBatchDate;
}
/**
 * Insert the method's description here.
 * Creation date: (11/30/2000 4:19:40 PM)
 * @param newBatchNumber java.lang.String
 */
public void setBatchNumber(java.lang.String newBatchNumber)
{
	batchNumber = newBatchNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (11/30/2000 4:19:40 PM)
 * @param newCoopId java.lang.String
 */
public void setCoopId(java.lang.String newCoopId)
{
	coopId = newCoopId;
}
/**
 * Insert the method's description here.
 * Creation date: (11/30/2000 4:19:40 PM)
 * @param newFiller java.lang.String
 */
public void setFiller(java.lang.String newFiller) 
{
	filler = newFiller;
}
/**
 * Insert the method's description here.
 * Creation date: (11/30/2000 4:19:40 PM)
 * @param newKvReadingVector java.util.Vector
 */
public void setKvarReadingVector(java.util.Vector newKvarReadingVector)
{
	kvarReadingVector = newKvarReadingVector;
}
/**
 * Insert the method's description here.
 * Creation date: (11/30/2000 4:19:40 PM)
 * @param newKwhReadingVector java.util.Vector
 */
public void setKwhReadingVector(java.util.Vector newKwhReadingVector) {
	kwhReadingVector = newKwhReadingVector;
}
/**
 * Insert the method's description here.
 * Creation date: (11/30/2000 4:19:40 PM)
 * @param newKwReadingVector java.util.Vector
 */
public void setKwReadingVector(java.util.Vector newKwReadingVector) {
	kwReadingVector = newKwReadingVector;
}
/**
 * Insert the method's description here.
 * Creation date: (11/30/2000 4:19:40 PM)
 * @param newMeterNumberVector java.util.Vector
 */
public void setMeterNumberVector(java.util.Vector newMeterNumberVector)
{
	meterNumberVector = newMeterNumberVector;
}
/**
 * Insert the method's description here.
 * Creation date: (11/30/2000 4:19:40 PM)
 * @param newPageCounter java.lang.String
 */
public void setPageCenter(java.lang.String newPageCenter)
{
	pageCenter = newPageCenter;
}
/**
 * Insert the method's description here.
 * Creation date: (11/30/2000 4:19:40 PM)
 * @param newPageNumber java.lang.String
 */
public void setPageNumber(java.lang.String newPageNumber) 
{
	pageNumber = newPageNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (11/30/2000 4:19:40 PM)
 * @param newTranNumber java.lang.String
 */
public void setTranNumber(java.lang.String newTranNumber)
{
	tranNumber = newTranNumber;
}
}
