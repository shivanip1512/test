package com.cannontech.dbtools.updater;

import java.io.File;
import java.util.Comparator;

import com.cannontech.clientutils.CTILogger;

/**
 * @author ryan
 *
 * Deals with a file name in following format:
 *   2.42_01.sql
 * 
 */
public class FileVersion
{
	private File file = null;

	public static Comparator FILE_COMP = new Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			if( !(o1 instanceof FileVersion) 
				|| !(o2 instanceof FileVersion) )
				return 0;

			FileVersion aVal = (FileVersion)o1;
			FileVersion bVal = (FileVersion)o2;

			if( aVal.getVersion() < bVal.getVersion() )
				return -1;
			else if( aVal.getVersion() == bVal.getVersion() )
			{
				//version is not enough, use the build #
				return (aVal.getBuild() < bVal.getBuild() ? -1
						: (aVal.getBuild() == bVal.getBuild() ? 0 : 1) );
				//return 0;
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
			int indx = getFile().getName().indexOf('_') + 1;			
			return Integer.parseInt( getFile().getName().substring(
					indx, indx+2) );			
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
			return Double.parseDouble( getFile().getName().substring(0, 4) );			
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
