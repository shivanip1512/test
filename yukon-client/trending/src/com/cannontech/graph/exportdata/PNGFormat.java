package com.cannontech.graph.exportdata;

/**
 * Insert the type's description here.
 * Creation date: (2/15/2002 1:27:47 PM)
 * @author: 
 */
public class PNGFormat 
{

	//	Portable Network Graphics (PNG) format. 

	java.io.File file = null;
/**
 * JPEGFormat constructor comment.
 */
public PNGFormat() {
	super();
}
/**
 * JPEGFormat constructor comment.
 */
public PNGFormat(java.io.File newFile)
{
	super();
	file = newFile;
}
/**
 * Insert the method's description here.
 * Creation date: (4/17/2001 8:54:10 AM)
 */
public void createPNGFormat( com.klg.jclass.chart.JCChart chart )
{
	try
	{
		if( file == null)
		{
			file = new java.io.File("C:/yukon/client/export/Chart.png");
		}

		chart.rotate();
		com.klg.jclass.util.swing.encode.JCEncodeComponent testEncode = new com.klg.jclass.util.swing.encode.JCEncodeComponent();
		testEncode.encode(com.klg.jclass.util.swing.encode.JCEncodeComponent.PNG, chart, file);
	}
	catch (java.io.IOException ioe)
	{
		ioe.printStackTrace();
	}
	catch (com.klg.jclass.util.swing.encode.EncoderException ee)
	{
		ee.printStackTrace();
	}
}//end encodeJPEGFormat()
}
