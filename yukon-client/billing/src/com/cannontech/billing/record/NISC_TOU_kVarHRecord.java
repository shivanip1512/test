/*
 * Created on May 31, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.cannontech.billing.record;

import java.sql.Timestamp;
import java.util.Vector;

/**
 * @author stacey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
Offset	Length	Type	Description	NISC Value
				
1		1		C	Record Type	‘H’ Header ‘M’ Meter ‘L’ End of Record
2		1		C	Comma	‘,’
3		10		C	Meter Number	Meter Number
13		1		C	Comma	‘,’
14		1		N	Time Of Use	Time Of Use
15		1		C	Comma	‘,’
16		5		N	KWH Reading	KWH RDG
21		1		C	Comma	‘,’
22		5		C	KWH Read Time	KWH RD Time
27		1		C	Comma	‘,’
28		10		C	KWH Read Date	KWH RD Date
38		1		C	Comma	‘,’
39		6		N	KW Reading	KW RDG
45		1		C	Comma	‘,’
46		5		C	Peak Time	KW RD Time
51		1		C	Comma	‘,’
52  	10		C	Peak Date	KW RD Date
62		1		C	Comma	‘,’
63		6		N	KVA Reading	KVA RDG (this can be ANY reading value, Sheryl Skiba - NISC)
68					Total Length	

 */
public class NISC_TOU_kVarHRecord extends TurtleRecordBase
{
    //The TurtleRecordBase demand reading and time/date fields are not used, but rather a vector of them are used in order to keep TOU
    private Vector peakDemandReadingsVector = new Vector(5);
    private Vector peakDemandTimestampVector = new Vector(5);
	private Double readingKvarH = null;	// 6digits (no decimal)

	private static java.text.DecimalFormat KVARH_FORMAT_NODECIMAL = new java.text.DecimalFormat("######");
	private static java.text.DecimalFormat KW_FORMAT_3v2 = new java.text.DecimalFormat("##0.00");
	private static java.text.SimpleDateFormat DATE_FORMAT = new java.text.SimpleDateFormat("MM/dd/yyyy");	
	/**
	 * NISC_TOU_KVARHRecord constructor comment.
	 */
	public NISC_TOU_kVarHRecord() {
		super();
	}
	/**
	 * NISC_TOU_KVARHRecord constructor comment.
	 */
	public NISC_TOU_kVarHRecord(String newMeterNumber)
	{
		super(newMeterNumber);
	}
	/**
	 * NISC_TOU_KVARHRecord constructor comment.
	 */
	public NISC_TOU_kVarHRecord(String newMeterNumber, double reading, java.sql.Timestamp newTimestamp)
	{
		super(newMeterNumber, reading, newTimestamp);
	}
	/**
	 * NISC_TOU_KVARHRecord constructor comment.
	 */
	public NISC_TOU_kVarHRecord(String newMeterNumber, java.sql.Timestamp newTimestamp)
	{
		super(newMeterNumber, newTimestamp);
	}
	/**
	 * Converts data in a SEDCFormat to a formatted StringBuffer for stream use.
	 * Creation date: (5/24/00 10:58:48 AM)
	 *@return java.lang.String
	*/
	 //r,mmmmmmmmmm,rrrrr,tt:tt,dd/dd/dd
	 //r - 1 meterReading/header record
	 //m - 10 meterNumber Left Justified
	 //x - 1 tou
	 //r - 5 reading
	 //t - 5 time (includes :)
	 //d - 8 date (includes /s)
	 //r - 6 (3.2) peak Reading
	 //t - 5 peak time (includes :)
	 //d - 8 peak date (includes /s)
	 //r - 6 kvarh Reading
	
	public String dataToString()
	{
		StringBuffer writeToFile = new StringBuffer();
	
		//Currently only set up for kw (peak) tou.
		int loopCount = getPeakDemandReadingsVector().size();
		if(loopCount < 1)	//loop at least once through this stuff.
		    loopCount = 1;
		for (int touIndex = 0; touIndex < loopCount; touIndex++)
		{
			writeToFile.append( getRecordIndicator() + ",");
		
			writeToFile.append( getMeterNumber());
			for ( int i = 0; i < (10 - getMeterNumber().length()); i++)
			{
				writeToFile.append(" ");
			}
			writeToFile.append(",");
		
			//TOU
			writeToFile.append(touIndex+1 + ",");	//TOU is the register number we are currently looping through (offset 1, not 0 of course).
			
			if( getReadingKWH() == null || touIndex > 0)	//don't write the value again if not the first tou
				writeToFile.append("     ,");
			else
			{
				for ( int i = 0; i < (5 - KWH_FORMAT_NODECIMAL.format(getReadingKWH()).length());i++)
				{
					writeToFile.append(" ");
				}
				writeToFile.append(KWH_FORMAT_NODECIMAL.format(getReadingKWH()) + ",");
			}
			
			writeToFile.append(getTime() + ",");
			writeToFile.append(getDate() + ",");
		
			//Peak
			if( getPeakDemandReadingsVector().size() < (touIndex+1) ) 
			{
				for (int i = 0; i < getKwFieldSize(); i++)
					writeToFile.append(" ");
				
				writeToFile.append(",");	//value
				writeToFile.append(",");	//date
				writeToFile.append(",");	//time
			}
			else
			{
				if( getPeakDemandReadingsVector().size() < (touIndex+1) || getPeakDemandReadingsVector().get(touIndex) == null)
				{
					for (int i = 0; i < getKwFieldSize(); i++)
						writeToFile.append(" ");
					
					writeToFile.append(",");
				}
				else
				{
					for ( int i = 0; i < (getKwFieldSize() - getKwFormat().format(getPeakDemandReadingsVector().get(touIndex)).length());i++)
					{
						writeToFile.append(" ");
					}
			
					writeToFile.append(getKwFormat().format(getPeakDemandReadingsVector().get(touIndex)) + ",");
				}
					
				//PeakT
				java.util.Date d = new java.util.Date(((Timestamp)getPeakDemandTimestampVector().get(touIndex)).getTime());
				writeToFile.append(TIME_FORMAT.format(d) + ',');
				//PeakD
				writeToFile.append(getDateFormat().format(d) + ',');
			}			
			//KvarH value 	(assumed its the same timestamp/date as the kwh reading in the file).
			if( getReadingKvarH() == null || touIndex > 0)	//don't write the value again if not the first tou
				writeToFile.append("      ");
			else
			{
				for ( int i = 0; i < (6 - KVARH_FORMAT_NODECIMAL.format(getReadingKvarH()).length());i++)
				{
					writeToFile.append(" ");
				}
				writeToFile.append(KVARH_FORMAT_NODECIMAL.format(getReadingKvarH()) + "");
			}
		
			writeToFile.append("\r\n");
		}
		return writeToFile.toString();
	}	
	/**
	 * Insert the method's description here.
	 * Creation date: (7/31/2001 2:34:50 PM)
	 * @return boolean
	 * @param o java.lang.Object
	 */
	public boolean equals(Object o) 
	{
		return ( (o != null) &&
				   (o instanceof NISC_TOU_kVarHRecord) &&
				   ((NISC_TOU_kVarHRecord)o).getMeterNumber().equalsIgnoreCase(getMeterNumber()) );
	}
	
	/**
	 * Returns the Date format.
	 * Creation date: (7/31/2001 2:34:50 PM)
	 * @return java.text.SimpleDateFormat
	 */
	public java.text.SimpleDateFormat getDateFormat()
	{
		return DATE_FORMAT;
	}
    /**
     * @return Returns the peakDemandReadingsVector.
     */
    public Vector getPeakDemandReadingsVector()
    {
        return peakDemandReadingsVector;
    }
    /**
     * @param peakDemandReadingsVector The peakDemandReadingsVector to set.
     */
    public void setPeakDemandReadingsVector(Vector peakDemandReadingsVector)
    {
        this.peakDemandReadingsVector = peakDemandReadingsVector;
    }
    /**
     * @return Returns the peakDemandTimestampVector.
     */
    public Vector getPeakDemandTimestampVector()
    {
        return peakDemandTimestampVector;
    }
    /**
     * @param peakDemandTimestampVector The peakDemandTimestampVector to set.
     */
    public void setPeakDemandTimestampVector(Vector peakDemandTimestampVector)
    {
        this.peakDemandTimestampVector = peakDemandTimestampVector;
    }
    /**
     * @return Returns the readingKvarH.
     */
    public Double getReadingKvarH()
    {
        return readingKvarH;
    }
    /**
     * @param readingKvarH The readingKvarH to set.
     */
    public void setReadingKvarH(double readingKvarH)
    {
        this.readingKvarH = new Double(readingKvarH);
    }
	public java.text.DecimalFormat getKwFormat()
	{
		return KW_FORMAT_3v2;
	}
}
