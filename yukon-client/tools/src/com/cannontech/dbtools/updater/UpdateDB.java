/*
 * Created on Jun 2, 2003
 *
 */
package com.cannontech.dbtools.updater;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;

/**
 * @author rneuharth
 *
 * Handled the File IO and the checking of validity for files and lines.
 * Decides which files to load in the given DIR.
 */
public class UpdateDB
{
	/**
	 * 
	 */
	public UpdateDB()
	{
		super();
	}



	/**
	* @return File[]
	*
	* Gets a list of specific files from a directory.
	* First try to get the intermediate file generated.
	* If that fails, then get the original update script file
	* */
	public File[] getDBUpdateFiles( String rootDIR )
	{
		File[] dbUpdateFiles = null;
		
		ArrayList files = new ArrayList(32);
		ArrayList versions = new ArrayList(32);

		//previously generated list of valid commands
		File genDIR = new File( CtiUtilities.getLogDirPath() );
		for( int i = 0; i < genDIR.listFiles().length; i++ )
		{
			File sqlFile = genDIR.listFiles()[i];
			double fVers = DBUpdater.getFileVersion(sqlFile);


			if( DBUpdater.getFileVersion(sqlFile) < DBMSDefines.MIN_VERSION
				 || fVers <= DBUpdater.getDBVersion() )
			{
				CTILogger.info("IGNORING file (Version mismatch): " + sqlFile );
				continue;
			}

			if( isValidUpdateFile(sqlFile) )
			{
				CTILogger.debug("  GenDir - Adding file : " + sqlFile );
				files.add( sqlFile ); //get all the .sql files in the directory
				versions.add( new Double(fVers) );
			}

		}
		

		//dir to look in for new updates
		File userDIR = new File( rootDIR );
		for( int i = 0; i < userDIR.listFiles().length; i++ )
		{
			File sqlFile = userDIR.listFiles()[i];
			double fVers = DBUpdater.getFileVersion(sqlFile);
			
			if( DBUpdater.getFileVersion(sqlFile) < DBMSDefines.MIN_VERSION
				 || fVers <= DBUpdater.getDBVersion() )
			{
				CTILogger.info("IGNORING file (Version mismatch): " + sqlFile );
				continue;
			}

			if( sqlFile.getAbsolutePath().toLowerCase().endsWith(DBMSDefines.SQL_EXT) 
				 && !versions.contains(new Double(fVers)) )
			{
				CTILogger.debug("  UserDIR - Adding file : " + sqlFile );
				files.add( sqlFile ); //get all the .sql files in the directory
			}

		}


		dbUpdateFiles = new File[ files.size() ];
		dbUpdateFiles = (File[])files.toArray( dbUpdateFiles );			
		
		return dbUpdateFiles;
	}


	/**
	*
	* This comment may contain some meta data, process it
	* if so.
	**/
	private void handleComment( final String token_, final UpdateLine updLine_ )
	{
		int metaIndx = token_.indexOf(DBMSDefines.META_TAG);
		
		if( metaIndx >= 0 && metaIndx < token_.length() )
		{
			StringTokenizer tokenizer = new StringTokenizer( 
					token_.substring(metaIndx+1), DBMSDefines.META_TOKEN );
	
			//we are only expecting the key,value pair
			if( tokenizer.countTokens() >= 2 )
			{
				String key = tokenizer.nextToken();
				String val = tokenizer.nextToken();
				
				updLine_.getMetaProps().put( key, val );
			}

		}
				
	}

	/**
	* Insert the method's description here.
	* Creation date: (3/31/2001 11:31:42 AM)
	* @return 
	*
	* Retrieves a list of SQL lines from a given
	* file. If a problem occurs, null is returned
	* Will throw a DBUpdateException if a problem occurs.
	* */
	public UpdateLine[] readFile( final File file ) throws DBUpdateException
	{
		if( file.exists() )
		{
			try
			{
				ArrayList validLines = new ArrayList(512);
				java.io.RandomAccessFile fileReader = new java.io.RandomAccessFile(file, "r");
				String token = "";
				UpdateLine updLine = new UpdateLine();


				while( fileReader.getFilePointer() < fileReader.length() )
				{
					token = fileReader.readLine();

					if( isValidString(token) )
					{
						if( token.startsWith(DBMSDefines.COMMENT_BEGIN) )
						{
							handleComment( token, updLine );
						}
						else
						{
							updLine.getValue().append( token );
	
							if( updLine.getValue().toString().endsWith(DBMSDefines.LINE_TERM) )
							{
								validLines.add( updLine );
	
								updLine = new UpdateLine();
							}
							else
								updLine.getValue().append(" "); //add a blank to seperate the lines (just in case)
						}

					}

				}	

				fileReader.close();


				//file open/closed and read successfully
				UpdateLine[] cmds = new UpdateLine[ validLines.size() ];
				return (UpdateLine[])validLines.toArray( cmds );				
			}
			catch( Exception e)
			{
				CTILogger.error( e.getMessage(), e );
				throw new DBUpdateException( e );
			}
		}
		else
		{
			CTILogger.info( "Unable to find file '" + file +"'" );
			throw new DBUpdateException( "Unable to find file '" + file +"'" );
		}

	}
	
	


	public static boolean isValidString( String str_ )
	{
		if( str_ == null || str_.length() <= 0 )
			return false;


		for( int i = 0; i < DBMSDefines.IGNORE_STRINGS.length; i++ )
			if( DBMSDefines.IGNORE_STRINGS[i].equalsIgnoreCase(str_) )
				return false;
		
		return true;
	}

	public static boolean isValidUpdateFile( File file_ )
	{
		if( file_ == null )
			return false;

		if( DBUpdater.getFileVersion(file_) > 0.0
			 && file_.getAbsolutePath().toLowerCase().endsWith(DBMSDefines.SQL_EXT)
			 && (file_.getAbsolutePath().toLowerCase().indexOf(DBMSDefines.NAME_VALID) > -1) )
		{
			return true;
		}
		
		return false;
	}


}