package com.cannontech.graph.exportdata;

/**
 * Insert the type's description here.
 * Creation date: (4/16/2001 5:12:59 PM)
 * @author: 
 */

import com.klg.jclass.chart.JCChart; 
import com.cannontech.graph.Graph;

public class PDFformat
{
	private static String[] pdfString = null;
	private static java.io.File file = null;
/**
 * PDFFormat constructor comment.
 */
public PDFformat() {
	super();
}
/**
 * PDFFormat constructor comment.
 */
public PDFformat(java.io.File newFile)
{
	super();

	file = newFile;
}
/**
 * Insert the method's description here.
 * Creation date: (4/17/2001 8:54:10 AM)
 */
public static String[] createPDFFormat( com.klg.jclass.chart.JCChart chart )
{
		java.io.BufferedOutputStream out = null;
		String [] outTemp = null;

		if( chart == null)
		{

		}
		try
		{
			if (file == null)
			{
				file = new java.io.File("Download.pdf" );
			}
			
			out = new java.io.BufferedOutputStream(new java.io.FileOutputStream(file));
			encodePDF(out, chart);
			out.flush();				
		}		
		catch( java.io.IOException io )
		{
			io.printStackTrace();
		}
		finally
		{
			try
			{
				if( out != null )
					out.close();
			} 
			catch( java.io.IOException ioe )
			{
				ioe.printStackTrace();
			}
		}//end finally
	return pdfString;
}//end encodePDFFormat()
/**
 * Encodes a PDF on the given stream.
 * Creation date: (10/18/00 3:38:21 PM)
 * @param out java.io.OutputStream
 */
public static void encodePDF(java.io.OutputStream out, com.klg.jclass.chart.JCChart chart ) 
{
	//FIXFIX
	//Need to figure out how to size the pdf correctly
	//01/01 -acl
	com.klg.jclass.page.JCPrinter printer = new com.klg.jclass.page.adobe.pdf.JCPDFPrinter(out);

	com.klg.jclass.page.JCDocument doc = new com.klg.jclass.page.JCDocument(printer, com.klg.jclass.page.JCDocument.BLANK_8p5X11);
	doc.getPages();
	com.klg.jclass.page.JCFlow flow = new com.klg.jclass.page.JCFlow(doc);

	flow.embedComponent(chart);

	doc.print();
}
}
