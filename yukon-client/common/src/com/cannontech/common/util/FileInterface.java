package com.cannontech.common.util;

/**
 * Abstract class provides functionality to watch for a particular
 * file extension in a particular directory.
 *
 * Concrete implementations must implement the handleFile method.
 *
 * Creation date: (5/17/00 3:07:10 PM)
 * @author: Aaron Lauinger
 */
public abstract class FileInterface implements Runnable{
	
	private java.lang.String dirToWatch;
	private java.lang.String fileExt;
		
	// How often in milliseconds should we check for the file
	private long checkFrequency;
	
/**
 * FileInterface constructor comment.
 */
public FileInterface(String dirToWatch, String fileExt) {
	super();
	setDirToWatch(dirToWatch);
	setFileExt(fileExt);
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 3:10:52 PM)
 * @return int
 */
public long getCheckFrequency() {
	return checkFrequency;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 3:08:33 PM)
 * @return java.lang.String
 */
public java.lang.String getDirToWatch() {
	return dirToWatch;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 3:08:44 PM)
 * @return java.lang.String
 */
public java.lang.String getFileExt() {
	return fileExt;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 3:10:17 PM)
 * @param in java.io.InputStream
 */
protected abstract void handleFile(java.io.InputStream in);
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 3:09:20 PM)
 */
public void run()
{
	try
	{
		while (true)
		{
			java.io.File dir = new java.io.File(getDirToWatch());
		
			if (!dir.isDirectory())
			{
				com.cannontech.clientutils.CTILogger.info("com.cannontech.common.util.FileInterface - Directory does not exist: " + getDirToWatch());
			}
			else
			{
				java.io.File[] files = dir.listFiles();

				for( int i = 0; i < files.length; i++ )				
					if( files[i].getName().toLowerCase().endsWith( getFileExt().toLowerCase() ))
						
					{
						try
						{
							java.io.InputStream in = new java.io.FileInputStream(files[i]);
							handleFile(in);

							try
							{
								in.close();
							}
							catch( java.io.IOException ioe ) { com.cannontech.clientutils.CTILogger.error( ioe.getMessage(), ioe ); };
							
							files[i].delete();
						}
						catch( java.io.FileNotFoundException e )
						{
							//could happen if the file was moved or delete at _just_ the right time
							com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
						}
					}
					
			}
			
			Thread.sleep(getCheckFrequency());
		}
	}
	catch (InterruptedException ie)
	{
		com.cannontech.clientutils.CTILogger.info("com.cannontech.common.util.FileInterface - Interrupted");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 3:10:52 PM)
 * @param newCheckFrequency int
 */
public void setCheckFrequency(int newCheckFrequency) {
	checkFrequency = newCheckFrequency;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 3:10:52 PM)
 * @param newCheckFrequency int
 */
public void setCheckFrequency(long newCheckFrequency) {
	checkFrequency = newCheckFrequency;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 3:08:33 PM)
 * @param newPathToWatch java.lang.String
 */
public void setDirToWatch(java.lang.String newDirToWatch) {
	dirToWatch = newDirToWatch;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 3:08:44 PM)
 * @param newFileExt java.lang.String
 */
public void setFileExt(java.lang.String newFileExt) {
	fileExt = newFileExt;
}
}
