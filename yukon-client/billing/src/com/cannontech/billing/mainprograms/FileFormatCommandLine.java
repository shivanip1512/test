package com.cannontech.billing.mainprograms;

/**
 * Insert the type's description here.
 * Creation date: (5/23/00 10:13:54 AM)
 * @author: 
 */

import com.cannontech.billing.*;

public class FileFormatCommandLine
{
/**
 * FileFormatCommandLine constructor comment.
 */
public FileFormatCommandLine() {
	super();
}
/**
 * Billing for Text File Formats
 * Written by Jarid Kvale, starting 5/18/00
 * Improved by Josh Wolberg, starting 11/29/00
 *
 * Supports : CADPXL2 
 *
 * Not Supported : SEDC, MVRS, NCDC, CTI Standard 2, and CADP file formats
 *
 * @param args java.lang.String[]
 */
public static void main(String[] args)
{
	String fileFormatString = null;
	int fileFormatType = com.cannontech.billing.FileFormatTypes.INVALID;
	String outputFileName = null;
	String inputFileName = null;
	java.util.Vector collectionGroupVector = new java.util.Vector();
	java.util.Date startDate = null;
	java.util.Date stopDate = null;
	boolean appendFlag = true;
	boolean validArguments = true;
	java.text.SimpleDateFormat[] dateFormatArray =
	{
		new java.text.SimpleDateFormat("MM/dd/yy"),
		new java.text.SimpleDateFormat("MM-dd-yy"),
		new java.text.SimpleDateFormat("MM.dd.yy"),
		new java.text.SimpleDateFormat("MM/dd/yyyy"),
		new java.text.SimpleDateFormat("MM-dd-yyyy"),
		new java.text.SimpleDateFormat("MM.dd-yyyy"),
		new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss")
	};

	for(int i=0;i<args.length;i++) 
	{
		if( args[i].substring(0,5).equalsIgnoreCase("type=") && fileFormatType == FileFormatTypes.INVALID )
		{
			fileFormatString = args[i].substring(5);
			fileFormatType = FileFormatTypes.getFormatID(fileFormatString);
		}

		if( args[i].substring(0,11).equalsIgnoreCase("outputfile=") )
		{// Name of output file
			outputFileName = new String(args[i].substring(11));
		}

		if( args[i].substring(0,10).equalsIgnoreCase("inputfile=") )
		{ // Name of output file
			inputFileName = new String(args[i].substring(10));
		}

		if( args[i].substring(0,16).equalsIgnoreCase("collectiongroup=") )
		{// Add collection groups to Vector
			collectionGroupVector.addElement( args[i].substring(16) );
		}

		if( args[i].substring(0,10).equalsIgnoreCase("startdate=") )
		{// date where the data starts to be used to generate the billing file
			
			for(int j=0;j<dateFormatArray.length;j++)
			{
				try
				{
					startDate = dateFormatArray[j].parse( args[i].substring(10) );
					break;
				}
				catch( java.text.ParseException pe )
				{
				}
			}
		}

		if( args[i].substring(0,9).equalsIgnoreCase("stopdate=") )
		{// date where the data stops to be used to generate the billing file
			
			for(int j=0;j<dateFormatArray.length;j++)
			{
				try
				{
					stopDate = dateFormatArray[j].parse( args[i].substring(9) );
					break;
				}
				catch( java.text.ParseException pe )
				{
				}
			}
		}

		if( args[i].equalsIgnoreCase("newfile") )
		{
			appendFlag = false; // Sets file to overwrite previous data
		}
	}
	
	// Error messages for missing arguments
	if( fileFormatType == FileFormatTypes.INVALID )
	{
		System.out.println("ERROR: Missing File Format Type");
		validArguments = false;
	}
	if( collectionGroupVector.size() == 0 )
	{
		System.out.println("ERROR: Missing Collection Group");
		validArguments = false;
	}
	if( outputFileName == null )
	{
		System.out.println("ERROR: Missing Output Filename");
		validArguments = false;
	}
	if( (inputFileName == null) &&
		  (fileFormatType == FileFormatTypes.MVRS || fileFormatType == FileFormatTypes.WLT_40) )
	{
		System.out.println("ERROR: Missing Input Filename");
		validArguments = false;
	}

	if( validArguments )
	{
		FileFormatBase fileFormatBase = FileFormatFactory.createFileFormat(fileFormatType);
		
		if( fileFormatBase != null )
		{
			if( fileFormatBase instanceof MVRSFormat ||
					fileFormatBase instanceof WLT_40Format )
			{
				//fileFormatBase.getBillingDefaults().setInputFile(inputFileName);
				//start date is auto set with end date and previous days values.
				//fileFormatBase.setStartDate(startDate);
				fileFormatBase.getBillingDefaults().setEndDate(stopDate);
			}

			//tell our formatter about needing to append or not
			fileFormatBase.setIsAppending( appendFlag );

			boolean success = fileFormatBase.retrieveBillingData(collectionGroupVector, null);

			try
			{
				if( success )
				{
					java.io.FileWriter outputFileWriter = new java.io.FileWriter(outputFileName,appendFlag);
					outputFileWriter.write(fileFormatBase.getOutputAsStringBuffer().toString());
					outputFileWriter.flush();
					outputFileWriter.close();

					System.out.println("Billing File successfully created.");
				}
				else
					System.out.println("Unable to create billing file not due to erros with the FileFormatter.");
				
			}
			catch(java.io.IOException ioe)
			{
				System.out.println("Billing File not created.");
				ioe.printStackTrace();
			}
			
		}
		else
		{
			System.out.println(fileFormatString + " unrecognized file format type");
		}
	}
	else
	{
		System.out.print("Supported Formats : ");
		for( int i = 0; i < FileFormatTypes.getValidFormatTypes().length; i++ )
		{
			// do a little formatting for the screen
			if( i > 0 && (i%5) == 0 )
			{
				System.out.println();
				System.out.print("                    ");
			}
			
			System.out.print( FileFormatTypes.getValidFormatTypes()[i] );
		}

			
		System.out.println("*** Sample argument structure: type=mvrs outputfile=billing.out collectiongroup=north collectiongroup=south startdate=10/01/2001 stopdate=10/31/2001 newfile ***");
	}

	return;
}
}
