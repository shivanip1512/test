package com.cannontech.report.mainprograms;

/**
 * Insert the type's description here.
 * Creation date: (5/23/00 10:13:54 AM)
 * @author: 
 */

import com.cannontech.report.*;

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
	java.util.Date tempStartDate = new java.util.Date();
	java.util.Date tempStopDate = new java.util.Date();
	java.util.GregorianCalendar startDate = new java.util.GregorianCalendar();
	java.util.GregorianCalendar stopDate = new java.util.GregorianCalendar();
	boolean printFlag = true;
	boolean validArguments = true;
	java.text.SimpleDateFormat[] dateFormatArray =
	{
		new java.text.SimpleDateFormat("MM/dd/yy"),
		new java.text.SimpleDateFormat("MM-dd-yy"),
		new java.text.SimpleDateFormat("MM.dd.yy"),
		new java.text.SimpleDateFormat("MM/dd/yyyy"),
		new java.text.SimpleDateFormat("MM-dd-yyyy"),
		new java.text.SimpleDateFormat("MM.dd-yyyy"),
		new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss"),
		new java.text.SimpleDateFormat("MM:dd:yyyy HH:mm:ss")
	};

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
			
			for(int j=0;j<dateFormatArray.length;j++)
			{
				try
				{
					tempStartDate = dateFormatArray[j].parse( args[i].substring(10) );
					break;
				}
				catch( java.text.ParseException pe )
				{
				}
			}
		}

		if( args[i].length() >= 9 &&
			  args[i].substring(0,9).equalsIgnoreCase("stopdate=") )
		{// date where the data stops to be used to generate the billing file
			
			for(int j=0;j<dateFormatArray.length;j++)
			{
				try
				{
					tempStopDate = dateFormatArray[j].parse( args[i].substring(9) );
					break;
				}
				catch( java.text.ParseException pe )
				{
				}
			}
		}

		if( args[i].length() >= 5 &&
			  args[i].equalsIgnoreCase("print") )
		{
			printFlag = true; // Sets file to overwrite previous data
		}
	}
	
	// Error messages for missing arguments
	if( reportType == ReportTypes.INVALID )
	{
		System.out.println("ERROR: Missing File Format Type");
		validArguments = false;
	}
	if( outputFileName == null && !printFlag )
	{
		System.out.println("ERROR: Missing Output Filename or not to be printed");
		validArguments = false;
	}

	if( validArguments )
	{
		ReportBase reportBase = ReportFactory.createReport(reportType);
		
		if( reportBase != null )
		{
			startDate = new java.util.GregorianCalendar();
			startDate.setTime(tempStartDate);
			startDate.add(java.util.Calendar.DAY_OF_YEAR,-1);
			startDate.set(java.util.Calendar.HOUR_OF_DAY,0);
			startDate.set(java.util.Calendar.MINUTE,0);
			startDate.set(java.util.Calendar.SECOND,0);
			startDate.set(java.util.Calendar.MILLISECOND,0);

			stopDate = new java.util.GregorianCalendar();
			stopDate.setTime(tempStartDate);
			stopDate.set(java.util.Calendar.HOUR_OF_DAY,0);
			stopDate.set(java.util.Calendar.MINUTE,0);
			stopDate.set(java.util.Calendar.SECOND,0);
			stopDate.set(java.util.Calendar.MILLISECOND,0);

			reportBase.setStartDate(startDate);
			reportBase.setStopDate(stopDate);

			boolean success = reportBase.retrieveReportData(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());

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
							com.klg.jclass.page.awt.JCAWTPrinter printer = new com.klg.jclass.page.awt.JCAWTPrinter( pj, pj.defaultPage(), false );

							com.klg.jclass.page.JCDocument document = new com.klg.jclass.page.JCDocument( printer );

							com.klg.jclass.page.JCFlow flow = reportBase.getFlow(document);
							document.setFlow( flow );

							while( printer.isDocumentOpen() )
							{
								try
								{
									Thread.sleep(1000);
								}
								catch( InterruptedException ex )
								{
									System.out.println("--------- UNCAUGHT EXCEPTION ---------");
									ex.printStackTrace(System.out);	
								}

								System.out.println("	Waiting for printer");		
							}	

							document.print( printer );
							flow.endFlow();
							/*java.io.StringWriter stringWriter = new java.io.StringWriter();
							java.io.PrintWriter printWriter = new java.io.PrintWriter(stringWriter,true);
							stringWriter.write( reportBase.getOutputAsStringBuffer().toString() );
							printWriter.flush();*/
						}
						catch( com.klg.jclass.page.awt.JCAWTPrinter.PrinterJobCancelledException e)
						{
							System.out.println("USER CANCELED PRINT JOB");
						}
					}
					else
					{
						java.io.FileWriter outputFileWriter = new java.io.FileWriter(outputFileName);
						outputFileWriter.write(reportBase.getOutputAsStringBuffer().toString());
						outputFileWriter.flush();
						outputFileWriter.close();
					}

					System.out.println("Report successfully created.");
				}
				else
					System.out.println("Unable to create report errors in retrieveReportData().");
				
			}
			catch(java.io.IOException ioe)
			{
				System.out.println("Report not created.");
				ioe.printStackTrace();
			}
			
		}
		else
		{
			System.out.println(reportString + " unrecognized report type");
		}
	}
	else
	{
		System.out.print("Supported Reports : ");
		for( int i = 0; i < ReportTypes.SUPPORTED_REPORTS.length; i++ )
		{
			// do a little formatting for the screen
			if( i > 0 && (i%5) == 0 )
			{
				System.out.println();
				System.out.print("                    ");
			}
			
			System.out.print( ReportTypes.SUPPORTED_REPORTS[i] );
		}

		System.out.println("*** Sample argument structure: type=loads_shed outputfile=loads_shed.out startdate=10/01/2001 stopdate=10/01/2001 ***");
	}

	System.exit(0);
}
}
