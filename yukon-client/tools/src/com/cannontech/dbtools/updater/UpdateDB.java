/*
 * Created on Jun 2, 2003
 *
 */
package com.cannontech.dbtools.updater;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.StringTokenizer;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.version.VersionTools;
import com.cannontech.database.db.version.CTIDatabase;
import com.cannontech.tools.gui.IMessageFrame;

/**
 * @author rneuharth
 *
 * Handled the File IO and the checking of validity for files and lines.
 * Decides which files to load in the given DIR.
 * 
 * D:\eclipse\head\yukon-database\DBUpdates\oracle\
 */
public class UpdateDB
{
	private static double dbVersion = 0.0;
	private IMessageFrame output = null;


	//A comparator to compare the DBupdate file versions 
	public static final Comparator COMP_FILE_VERS = new Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			double aVal = getFileVersion( (File)o1 );
			double bVal = getFileVersion( (File)o2 );

			return (aVal < bVal ? -1 : (aVal == bVal ? 0 : 1) );
		}
			
		public boolean equals(Object obj)
		{
			return false;
		}
	};

	/**
	 * 
	 */
	public UpdateDB( IMessageFrame output_ )
	{
		super();
		output = output_;
		
		getIMessageFrame().addOutput("");
		getIMessageFrame().addOutput("----------------------------------------------------------------------------------------------");
		getIMessageFrame().addOutput( "Current DB Version : " + getDBVersion() );
		getIMessageFrame().addOutput("----------------------------------------------------------------------------------------------");
		getIMessageFrame().addOutput("");
		
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
		final File genDIR = new File( CtiUtilities.getLogDirPath() );
		for( int i = 0; i < genDIR.listFiles().length; i++ )
		{
			File sqlFile = genDIR.listFiles()[i];
			double fVers = getFileVersion(sqlFile);


			if( getFileVersion(sqlFile) < DBMSDefines.MIN_VERSION
				 || fVers <= getDBVersion() )
			{
				getIMessageFrame().addOutput("  IGNORING file (Version mismatch): " + sqlFile );
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
		final File userDIR = new File( rootDIR );
		for( int i = 0; i < userDIR.listFiles().length; i++ )
		{
			File sqlFile = userDIR.listFiles()[i];
			double fVers = getFileVersion(sqlFile);
			
			if( getFileVersion(sqlFile) < DBMSDefines.MIN_VERSION
				 || fVers <= getDBVersion() )
			{
				getIMessageFrame().addOutput("  IGNORING file (Version mismatch): " + sqlFile );
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
		
		//be sure the returned list is in order by version number
		Arrays.sort( dbUpdateFiles, COMP_FILE_VERS );		 
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
				getIMessageFrame().addOutput( e.getMessage() );
				CTILogger.error( e.getMessage(), e );
				throw new DBUpdateException( e );
			}
		}
		else
		{
			getIMessageFrame().addOutput( "Unable to find file '" + file +"'" );
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

		if( getFileVersion(file_) > 0.0
			 && file_.getAbsolutePath().toLowerCase().endsWith(DBMSDefines.SQL_EXT)
			 && (file_.getAbsolutePath().toLowerCase().indexOf(DBMSDefines.NAME_VALID) > -1) )
		{
			return true;
		}
		
		return false;
	}


	public static double getDBVersion()
	{
		if( dbVersion <= 0.0 )
		{
			CTIDatabase db = VersionTools.getDatabaseVersion();			

			dbVersion = Double.parseDouble( db.getVersion() );
		}				

		return dbVersion;					
	}

	public static double getFileVersion( File file_ )
	{
		try
		{
			return Double.parseDouble( file_.getName().substring(0, 4) );
		}
		catch( Exception e )
		{
			CTILogger.info("Invalid file name, name = " + file_.getName() );
			return -1.0;
		}

	}
	

	/**
	 * This is where our output goes to
	 * 
	 * @return IMessageFrame
	 */
	public IMessageFrame getIMessageFrame() 
	{
		return output;
	}

}