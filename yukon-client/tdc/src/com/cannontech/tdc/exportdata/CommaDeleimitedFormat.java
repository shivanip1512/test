package com.cannontech.tdc.exportdata;

/**
 * Insert the type's description here.
 * Creation date: (2/29/00 10:16:47 AM)
 * @author: 
 */
public class CommaDeleimitedFormat {
/**
 * CommaDeleimited constructor comment.
 */
public CommaDeleimitedFormat() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (2/29/00 12:23:04 PM)
 * @return java.lang.String
 * @param data java.lang.String[]
 */
public static String[] createLines(String[] data, int columnCount ) 
{
	String[] buffer = new String[ data.length ];
	int length = buffer.length;

	for( int i = 0; i < length; i++ )
	{		
		if( i == (length - 1) )
		{			
			buffer[i] = data[i];
			break;
		}
		else
			buffer[i] = data[i] + ",";

		if( (i + 1) % columnCount == 0 )
			buffer[i] += "\r\n";		
	}

	return buffer;
}
}
