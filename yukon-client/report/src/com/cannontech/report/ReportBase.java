package com.cannontech.report;

/**
 * Insert the type's description here.
 * Creation date: (5/18/00 2:07:05 PM)
 * @author: 
 */
public abstract class ReportBase
{
	//used to store every line of output that will be written to the file
	// it holds Objects of type ReportRecordBase
	private java.util.Vector recordVector = null;
	private java.util.GregorianCalendar startDate = null;
	private java.util.GregorianCalendar stopDate = null;
	private String outputFileName = null;
/**
 * FileFormatBase constructor comment.
 */
public ReportBase()
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
 * Retrieves values from the database and inserts them in a FileFormatBase object
 * Creation date: (11/30/00)
 */
//returns true if the data retrieval was successfull
abstract public com.klg.jclass.page.JCFlow getFlow(com.klg.jclass.page.JCDocument doc);
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
		String dataString = ((ReportRecordBase)records.get(i)).dataToString();
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
public java.lang.String getOutputFileName() {
	return outputFileName;
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
 * Creation date: (3/27/2002 3:33:14 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getStartDate() {
	return startDate;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:33:14 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getStopDate() {
	return stopDate;
}
/**
 * Retrieves values from the database and inserts them in a FileFormatBase object
 * Creation date: (11/30/00)
 */
//returns true if the data retrieval was successfull
abstract public boolean retrieveReportData(String databaseAlias);
/**
 * Insert the method's description here.
 * Creation date: (8/31/2001 2:34:47 PM)
 * @param newOutputFileName java.lang.String
 */
public void setOutputFileName(java.lang.String newOutputFileName) {
	outputFileName = newOutputFileName;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:33:14 PM)
 * @param newStartDate java.util.GregorianCalendar
 */
public void setStartDate(java.util.GregorianCalendar newStartDate) {
	startDate = newStartDate;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:33:14 PM)
 * @param newStopDate java.util.GregorianCalendar
 */
public void setStopDate(java.util.GregorianCalendar newStopDate) {
	stopDate = newStopDate;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/2001 12:09:57 PM)
 */
public void writeToFile() throws java.io.IOException
{
	java.io.FileWriter outputFileWriter = new java.io.FileWriter( getOutputFileName() );
	outputFileWriter.write( getOutputAsStringBuffer().toString() );
	outputFileWriter.flush();
	outputFileWriter.close();
}
}
