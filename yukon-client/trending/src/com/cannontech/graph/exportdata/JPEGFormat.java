package com.cannontech.graph.exportdata;

/**
 * Insert the type's description here.
 * Creation date: (2/15/2002 1:27:47 PM)
 * @author: 
 */
public class JPEGFormat 
{

	//	Joint Photographic Experts Group (JPEG) format. 

	java.io.File file = null;
/**
 * JPEGFormat constructor comment.
 */
public JPEGFormat() {
	super();
}
/**
 * JPEGFormat constructor comment.
 */
public JPEGFormat(java.io.File newFile)
{
	super();
	file = newFile;
}
/**
 * Insert the method's description here.
 * Creation date: (4/17/2001 8:54:10 AM)
 */
public void createJPEGFormat( com.klg.jclass.chart.JCChart chart )
{
	try
	{
		if( file == null)
		{
			file = new java.io.File("C:/yukon/client/export/Chart.jpeg");
		}

		com.klg.jclass.util.swing.encode.JCEncodeComponent testEncode = new com.klg.jclass.util.swing.encode.JCEncodeComponent();
		testEncode.encode(com.klg.jclass.util.swing.encode.JCEncodeComponent.JPEG, chart, file);
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
