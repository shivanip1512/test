package com.cannontech.tdc.bookmark;

/**
 * Insert the type's description here.
 * Creation date: (4/11/00 11:45:20 AM)
 * @author: 
 * @Version: <version>
 */
public class BookMarkBase 
{
	private static BookMarkBase bookMarkBase = null;

//	private boolean updateNeeded = true;
	private java.util.ArrayList list = null;

	public static final String BOOKMARK_TOKEN = "-";
/**
 * BookMarkBase constructor comment.
 */
private BookMarkBase() 
{
	super();

	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/00 11:56:49 AM)
 * Version: <version>
 * @param newValue java.lang.Object
 */
public void addBookmark(Object newValue) 
{	
	list.add( newValue );
	writeNewList();
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/00 11:52:01 AM)
 * Version: <version>
 * @return boolean
 */
public boolean contains( Object value ) 
{
	return list.contains( value );
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/00 11:56:49 AM)
 * Version: <version>
 */
public static synchronized BookMarkBase getInstance() 
{
	if( bookMarkBase == null )
		bookMarkBase = new BookMarkBase();

	return bookMarkBase;	
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/00 11:52:01 AM)
 * Version: <version>
 * @return java.lang.Object[]
 */
public Object[] getList() 
{
//	updateNeeded = false;
	return list.toArray();
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) 
{
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION in TDCMainPanel() ---------");
	exception.printStackTrace(System.out);	
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/00 11:48:50 AM)
 * Version: <version>
 */
private void initialize() 
{
	list = new java.util.ArrayList( 15 );

	parseInputFile();
}
/**
 * This method was created in VisualAge.
 */
private void parseInputFile() 
{

	java.io.RandomAccessFile file = null;
	java.io.File checkFile = new java.io.File( com.cannontech.tdc.utils.TDCDefines.BOOKMARK_FILE_NAME );

	try
	{
		if( checkFile.exists() )					
		{
			// open file		
			file = new java.io.RandomAccessFile( checkFile, "r" );
		
			long filePointer = 0;
			long length = file.length();

			while ( filePointer < length )  // loop until the end of the file
			{					
				String line = file.readLine();  // read a line in

				list.add( line );

				// set our pointer to the new position in the file
				filePointer = file.getFilePointer();
			}
			
			// Close file
			file.close();
		}
	}
	catch(java.io.IOException ex)
	{
		handleException( ex );
	}
	finally
	{
		try
		{
			if( checkFile.exists() )
				file.close();
		}
		catch( java.io.IOException ex )
		{}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/00 12:20:58 PM)
 * Version: <version>
 */
public void removeBookmark( Object value )
{
	if( list.contains( value ) )
	{		
		list.remove( value );
		writeNewList();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/00 12:20:58 PM)
 * Version: <version>
 */
public void removeBookmarkWithoutWrite( Object value )
{
	if( list.contains( value ) )
	{		
		list.remove( value );
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/00 12:52:41 PM)
 * Version: <version>
 */
public void writeNewList() 
{
	java.io.File checkFile = new java.io.File( com.cannontech.tdc.utils.TDCDefines.BOOKMARK_FILE_NAME );	
	java.io.DataOutputStream file = null;
		
	try
	{
		// open file		
		file = new java.io.DataOutputStream( new java.io.FileOutputStream( checkFile ) );
		
		for( int i = 0; i < list.size(); i++ )					
			file.writeBytes( list.get(i).toString() + "\r\n" );
		
		// Close file
		file.close();
	}
	catch(java.io.IOException ex)
	{
		handleException( ex );
	}
	finally
	{
		try
		{
			if( checkFile.exists() )
				file.close();
		}
		catch( java.io.IOException ex )
		{}	
		
		//updateNeeded = true;	
	}
}
}
