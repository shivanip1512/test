package com.cannontech.common.util;

/**
 * This type was created in VisualAge.
 */
public class LineCount {
/**
 * This method was created in VisualAge.
 * @return int
 * @param file java.io.File
 */
public final static int countLines(java.io.File file) {

	int count = 0;
	
	if( file.isDirectory() )
	{
		String[] files = file.list();
		String path = file.getPath();

		for( int i = 0; i < files.length; i++ )
		{
			count += countLines( new java.io.File( path + "/" + files[i] ) );
		}

		return count;
	}

	try
	{
		java.io.FileReader reader = new java.io.FileReader(file);

		java.io.LineNumberReader numReader = new java.io.LineNumberReader(reader);

		while( numReader.readLine() != null )
		{
			count++;
		}

		numReader.close();		
	}
	catch( Exception e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}

	return count;
	
}
}
