package com.cannontech.report.mainprograms;

/**
 * Insert the type's description here.
 * Creation date: (5/23/00 10:13:54 AM)
 * @author: 
 */

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.report.ReportBase;
import com.cannontech.report.ReportFactory;
import com.cannontech.report.ReportTypes;
import com.cannontech.util.ServletUtil;
import com.klg.jclass.page.JCDocument;
import com.klg.jclass.page.JCFlow;
import com.klg.jclass.page.awt.JCAWTPrinter;

public class ReportCommandLine
{
/**
 * FileFormatCommandLine constructor comment.
 */
public ReportCommandLine() {
	super();
}
/**
 * Billing for Text File Formats
 * Written by Josh Wolberg, starting 4/1/02
 *
 * Supports : Loads_Shed, Daily_Peaks
 *
 * @param args java.lang.String[]
 */
public static void main(String[] args)
{
	String reportString = null;
	int reportType = ReportTypes.INVALID;
	String outputFileName = null;
	java.util.Date tempStartDate = null;
	java.util.Date tempStopDate = null;
	java.util.GregorianCalendar startDate = null;
	java.util.GregorianCalendar stopDate = null;

	boolean printFlag = false;	//default to false, the argument passed in sets it to true, there is not T/F value on the command line argument.
	boolean validArguments = true;


	for(int i=0;i<args.length;i++) 
	{
		if( args[i].length() >= 5 &&
			  args[i].substring(0,5).equalsIgnoreCase("type=") && reportType == ReportTypes.INVALID )
		{
			reportString = args[i].substring(5);
			reportType = ReportTypes.getType(reportString);
		}

		if( args[i].length() >= 11 &&
			  args[i].substring(0,11).equalsIgnoreCase("outputfile=") )
		{// Name of output file
			outputFileName = new String(args[i].substring(11));
		}

		if( args[i].length() >= 10 &&
			  args[i].substring(0,10).equalsIgnoreCase("startdate=") )
		{// date where the data starts to be used to generate the billing file
			
			tempStartDate = ServletUtil.parseDateStringLiberally(args[i].substring(10));
		}

		if( args[i].length() >= 9 &&
			  args[i].substring(0,9).equalsIgnoreCase("stopdate=") )
		{// date where the data stops to be used to generate the billing file
			tempStopDate = ServletUtil.parseDateStringLiberally(args[i].substring(9));
		}

		if( args[i].length() >= 5 && args[i].equalsIgnoreCase("print") )
		{
			printFlag = true; // Sets file to overwrite previous data
		}
	}
	
	// Error messages for missing arguments
	if( reportType == ReportTypes.INVALID )
	{
		CTILogger.info("ERROR: Missing File Format Type");
		validArguments = false;
	}

	if( validArguments )
	{
		ReportBase reportBase = ReportFactory.createReport(reportType);
		
		if( reportBase != null )
		{
			startDate = new GregorianCalendar();
			if( tempStartDate != null)
				startDate.setTime(tempStartDate);
			else
			{
				if( tempStopDate != null)	//if we have a stop date and no start date, set startdate to one day before the stop date.
				{
					startDate.setTime(tempStopDate);
				}
			}
			//Resolve date to beginning of day.
			startDate.set(Calendar.HOUR_OF_DAY,0);
			startDate.set(Calendar.MINUTE,0);
			startDate.set(Calendar.SECOND,0);
			startDate.set(Calendar.MILLISECOND,0);

			stopDate = new GregorianCalendar();
			if (tempStopDate != null)
				stopDate.setTime(tempStopDate);
			else
			{
				if( tempStartDate != null)//if we have a start date and no stop date, set stopdate to one day after the start date.
				{
					stopDate.setTime(tempStartDate);	//we actually increment it below
				}
			}
            
			//increment the day to get the Full day!
			stopDate.add(Calendar.DATE,1);
			stopDate.set(Calendar.HOUR_OF_DAY,0);
			stopDate.set(Calendar.MINUTE,0);
			stopDate.set(Calendar.SECOND,0);
			stopDate.set(Calendar.MILLISECOND,0);

			reportBase.setStartDate(startDate);
			reportBase.setStopDate(stopDate);

			reportBase.setOutputFileName(outputFileName);
			
			CTILogger.info("Generating Report " + ReportTypes.getType(reportType) + " : Time Period > " + startDate.getTime() +" And < " + stopDate.getTime());
			boolean success = reportBase.retrieveReportData(CtiUtilities.getDatabaseAlias());

			try
			{
				if( success )
				{
					if( printFlag )
					{
						try
						{
							java.awt.print.PrinterJob pj = java.awt.print.PrinterJob.getPrinterJob();
							pj.setJobName(ReportTypes.getType(reportType));
							JCAWTPrinter printer = new JCAWTPrinter( pj, pj.defaultPage(), false );
							JCDocument document = new JCDocument( printer );

							JCFlow flow = reportBase.getFlow(document);
							document.setFlow( flow );

							while( printer.isDocumentOpen() )
							{
								try
								{
									Thread.sleep(1000);
								}
								catch( InterruptedException ex )
								{
									CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
									ex.printStackTrace(System.out);	
								}

								CTILogger.info("	Waiting for printer");		
							}	
							document.print( printer );
							flow.endFlow();
						}
						catch( JCAWTPrinter.PrinterJobCancelledException e)
						{
							CTILogger.info("USER CANCELED PRINT JOB");
						}
					}
					//Always write to a file!!!
				    reportBase.writeToFile();

					CTILogger.info("Report successfully created.");
				}
				else
					CTILogger.info("Unable to create report errors in retrieveReportData().");
				
			}
			catch(java.io.IOException ioe)
			{
				CTILogger.info("Report not created.");
				ioe.printStackTrace();
			}
			
		}
		else
		{
			CTILogger.info(reportString + " unrecognized report type");
		}
	}
	else
	{
		CTILogger.info("Supported Reports : ");
		for( int i = 0; i < ReportTypes.SUPPORTED_REPORTS.length; i++ )
		{
			CTILogger.info( ReportTypes.SUPPORTED_REPORTS[i] );
		}

		CTILogger.info("*** Sample argument structure: type=loads_shed outputfile=loads_shed.out startdate=10/01/2001 stopdate=10/01/2001 ***");
	}

	System.exit(0);
}
}
