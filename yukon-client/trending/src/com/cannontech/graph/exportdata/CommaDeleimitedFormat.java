package com.cannontech.graph.exportdata;

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
	int temp = data.length/columnCount;
	
	int length = buffer.length;
	int bufferIndex = 0;

	
	for (int i = 0; i < temp ;i++)
	{
		int j = 0;
		if (i == (temp - 1) )
		{
			for (j = 0; j < (columnCount - 1); j++)
			{
				buffer[bufferIndex] = data[i+(temp * j)] + ",";
				bufferIndex++;
			}
				buffer[bufferIndex] = data[i+(temp * j)] + "\r\n";		
			break;	//don't want a comma on last one
		}

		else
		{
			for (j = 0; j < columnCount; j++)
			{
				buffer[bufferIndex] = data[i+(temp * j)] + ",";
				bufferIndex++;
			}
				buffer[bufferIndex - 1] += "\r\n";
		}
	}
	
	return buffer;	
}
}
