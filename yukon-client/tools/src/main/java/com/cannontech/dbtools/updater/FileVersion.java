package com.cannontech.dbtools.updater;

import java.io.File;
import java.util.Comparator;

import com.cannontech.clientutils.CTILogger;

/**
 * @author ryan
 *
 * Deals with a file name in following format:
 * Format examples:
 *   3.1_1.sql
 *   11.22_33.sql
 */
public class FileVersion
{
	private File file = null;

	public static Comparator<FileVersion> FILE_COMP = new Comparator<FileVersion>()
	{
		/**
		 * Compares version numbers to each other. Some truths are:
			3.1 < 3.11
			3.2 < 3.11  (interesting!)
			3.2 > 3.1
			3.99 > 3.2
			3.9 > 3.11  (interesting!)
		 */
		public int compare(FileVersion o1, FileVersion o2)
		{
			// 11.22
			// 9.1
			String aMinor = String.valueOf(o1.getVersion());
			aMinor = aMinor.substring(
						aMinor.indexOf(".")+1,
						aMinor.length() );
			if( aMinor.length() <= 1 )
				aMinor = "0" + aMinor;
			
			String bMinor = String.valueOf(o2.getVersion());
			bMinor = bMinor.substring(
						bMinor.indexOf(".")+1,
						bMinor.length() );
			if( bMinor.length() <= 1 )
				bMinor = "0" + bMinor;
			
			double aVersion = Double.parseDouble(
				((int)o1.getVersion()) + "." + aMinor );
			double bVersion = Double.parseDouble(
				((int)o2.getVersion()) + "." + bMinor );
				
			//CTILogger.debug("  aVers = " + aVersion + ",  given=" + aVal.getVersion());			
			//CTILogger.debug("  bVers = " + bVersion + ",  given=" + bVal.getVersion());

			if( aVersion < bVersion )
				return -1;
			else if( aVersion == bVersion )
			{
				//version is not enough, use the build #
				return (o1.getBuild() < o2.getBuild() ? -1
						: (o1.getBuild() == o2.getBuild() ? 0 : 1) );
			}
			else
				return 1;
		}
			
		public boolean equals(Object obj)
		{
			return false;
		}
	};
	
	public FileVersion( File file_ )
	{
		super();
		if( file_ == null )
			throw new IllegalArgumentException("The file name can not be null");

		setFile( file_ );
	}

	/**
	 * @return Returns the build.
	 */
	public int getBuild()
	{
		try
		{
			int startIndx = getFile().getName().indexOf('_') + 1;
			int endIndx =
				(getFile().getName().endsWith(DBMSDefines.NAME_VALID)
					? getFile().getName().length() - DBMSDefines.NAME_VALID.length()
				    : getFile().getName().length() - DBMSDefines.SQL_EXT.length() );
			
			return Integer.parseInt( getFile().getName().substring(
						startIndx, endIndx) );
		}
		catch( Exception e )
		{
			CTILogger.info("Build Number not found in file name, name = " + getFile().getName() );
		}
		
		return 0;
	}

	/**
	 * @return Returns the file.
	 */
	public File getFile() {
		return file;
	}
	/**
	 * @param file The file to set.
	 */
	public void setFile(File file_) {
		this.file = file_;
	}
	/**
	 * @return Returns the version.
	 */
	public double getVersion()
	{
		try
		{
			int indx = getFile().getName().indexOf('_');

			if( indx < 0 )
				indx = getFile().getName().length() - DBMSDefines.SQL_EXT.length();

			return Double.parseDouble(
					getFile().getName().substring(0, indx) );			
		}
		catch( Exception e )
		{
			CTILogger.info("Version Number not found in file name, name = " + getFile().getName() );
		}
		
		return -1.0;
	}
		
	public boolean equals(Object obj)
	{
		return
			obj instanceof FileVersion
			&& FILE_COMP.compare( this, (FileVersion)obj ) == 0;
	}
}
