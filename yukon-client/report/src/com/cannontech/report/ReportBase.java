package com.cannontech.report;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.klg.jclass.page.JCDocument;
import com.klg.jclass.page.JCFlow;
import com.klg.jclass.page.JCTextStyle;

/**
 * Insert the type's description here. Creation date: (5/18/00 2:07:05 PM)
 * 
 * @author:
 */
public abstract class ReportBase
{
    //current count of groupHeader lines
    protected int groupLineCount = 0;
    //current count of pageHEader lines
    protected int pageLineCount = 0;
    
    //used to store every line of output that will be written to the file
    // it holds Objects of type ReportRecordBase
    private Vector recordVector = null;

    //Holds values of String, the output lines.
    protected Vector outputStringsVector = null;

    private java.util.GregorianCalendar startDate = null;

    private java.util.GregorianCalendar stopDate = null;

    private String outputFileName = null;
    
    public static int MAX_LINES_PER_PAGE = 53;

    /**
     * FileFormatBase constructor comment.
     */
    public ReportBase()
    {
        super();
    }

    /**
     * Insert the method's description here. Creation date: (8/31/2001 5:03:58
     * PM)
     */
    // Override me if you want to manually close the DBConnection
    public void closeDBConnection()
    {
        CTILogger.info(this.getClass().getName()
                + ".closeDBConnection() must be overriden");
    }

    /**
     * Insert the method's description here. Creation date: (11/29/00)
     */
    abstract public Vector getOutputStringsVector();

    /**
     * Insert the method's description here. Creation date: (8/31/2001 2:34:47
     * PM)
     * 
     * @return java.lang.String
     */
    public java.lang.String getOutputFileName()
    {
        if( outputFileName == null)
        {
            GregorianCalendar now = new GregorianCalendar();
            int date = now.get(Calendar.DATE);
            outputFileName = CtiUtilities.getExportDirPath()+"/" +getReportName() + "_" +date + ".txt";
        }
        return outputFileName;
    }

    /**
     * Insert the method's description here. Creation date: (11/29/00)
     */
    public java.util.Vector getRecordVector()
    {
        if (recordVector == null)
            recordVector = new java.util.Vector(150);

        return recordVector;
    }

    /**
     * Insert the method's description here. Creation date: (3/27/2002 3:33:14
     * PM)
     * 
     * @return java.util.GregorianCalendar
     */
    public java.util.GregorianCalendar getStartDate()
    {
        return startDate;
    }

    /**
     * Insert the method's description here. Creation date: (3/27/2002 3:33:14
     * PM)
     * 
     * @return java.util.GregorianCalendar
     */
    public java.util.GregorianCalendar getStopDate()
    {
        return stopDate;
    }

    /**
     * Retrieves values from the database and inserts them in a FileFormatBase
     * object Creation date: (11/30/00)
     */
    //returns true if the data retrieval was successfull
    abstract public boolean retrieveReportData(String databaseAlias);

    /**
     * Insert the method's description here. Creation date: (8/31/2001 2:34:47
     * PM)
     * 
     * @param newOutputFileName
     *            java.lang.String
     */
    public void setOutputFileName(java.lang.String newOutputFileName)
    {
        outputFileName = newOutputFileName;
    }

    /**
     * Insert the method's description here. Creation date: (3/27/2002 3:33:14
     * PM)
     * 
     * @param newStartDate
     *            java.util.GregorianCalendar
     */
    public void setStartDate(java.util.GregorianCalendar newStartDate)
    {
        startDate = newStartDate;
    }

    /**
     * Insert the method's description here. Creation date: (3/27/2002 3:33:14
     * PM)
     * 
     * @param newStopDate
     *            java.util.GregorianCalendar
     */
    public void setStopDate(java.util.GregorianCalendar newStopDate)
    {
        stopDate = newStopDate;
    }

    /**
     * Insert the method's description here. Creation date: (8/29/2001 12:09:57
     * PM)
     */
    public void writeToFile() throws java.io.IOException
    {
        java.io.FileWriter outputFileWriter = new java.io.FileWriter( getOutputFileName());
        for (int i = 0; i < getOutputStringsVector().size(); i++)
        {
            outputFileWriter.write((String) getOutputStringsVector().get(i));
            outputFileWriter.write("\r\n");
        }
        outputFileWriter.flush();
        outputFileWriter.close();
    }
    
    abstract public boolean isPageHeaderLine(String line);
    abstract public boolean isGroup1HeaderLine(String line);
    
    
    public JCFlow getFlow(JCDocument doc)
    {
    	JCFlow returnFlow = new JCFlow(doc);

    	java.awt.Font controlAreaHeaderFont = new java.awt.Font( "Monospaced", java.awt.Font.BOLD, 12 );
    	java.awt.Font group1HeaderFont = new java.awt.Font( "Monospaced", java.awt.Font.BOLD, 8 );
    	java.awt.Font normalFont = new java.awt.Font( "Monospaced", java.awt.Font.PLAIN, 8 );

    	JCTextStyle currentTextStyle = new JCTextStyle("Current");
    	currentTextStyle.setParagraphSpacing(1);
    	returnFlow.setCurrentTextStyle( currentTextStyle );

    	int linesOnCurrentPage = 0;
    	for(int i = 0; i < getOutputStringsVector().size(); i++)
    	{
    		String line = (String)getOutputStringsVector().get(i);

    		if( line == "\f" )	//new page occurance
    		{
    			returnFlow.newPage();
    			linesOnCurrentPage = 0;
    		}
    		else if (line == "\r\n")	//new line occurance
    		{
    			returnFlow.newLine();
    			linesOnCurrentPage++;
    		}

    		else if( isPageHeaderLine(line))
    		{
    			currentTextStyle.setFont(controlAreaHeaderFont);
    			returnFlow.print(line);
    			returnFlow.newLine();
    			linesOnCurrentPage++;
    		}
    		else if ( isGroup1HeaderLine(line) )
    		{
    			currentTextStyle.setFont(group1HeaderFont);
    			returnFlow.print(line);
    			returnFlow.newLine();
    			linesOnCurrentPage++;
    		}
    		else	//detail info
    		{
    			currentTextStyle.setFont(normalFont);
    			returnFlow.print(line);
    			returnFlow.newLine();
    			linesOnCurrentPage++;
    		}
    	}

    	return returnFlow;
    }    
    
    //Return a name for the report
    abstract public String getReportName();
}