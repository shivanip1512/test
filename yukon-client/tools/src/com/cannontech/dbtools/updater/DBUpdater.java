package com.cannontech.dbtools.updater;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;


import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.commandlineparameters.CommandLineParser;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.tools.gui.IRunnableDBTool;


/**
 * @author rneuharth
 * 

For 2.40 Yukon client updates, 
the following minimum versions of software are needed:
 JRE 1.4.0
 Oracle 9.2
 SQLServer 7 (prefer 2000)

Here is what the DBUpdater tool does:
1) Searches the directory that you specify for all valid .sql files (ex: 2.41.sql | 2.40-2.1.2003.sql)
2) Reads in all the lines of the files found
    - only reads the .sql files that have a version number greater than the database version
3) Attempts to write the lines out to an intermediate file in the \Yukon\Client\Log\ directory 
    called X.XXvalids.sql where X.XX is the version (ex: 2.41valids.sql). If an intermediate
    file already exists, not file is written.
4) Commands in the intermediate files are then executed, commiting each successful command.
    - If all the commands in an entire file are executed successfully, 
    the file is written with embedded descriptive tags and renamed by adding the following text
    to the extension _MM-dd-yyyy_HH-mm-ss (ex: 2.40valids.sql_06-24-2003_10-40-08 )
    - If a command fails, the file is written with embedded descriptive tags and the
    name of the file is left unchanged.

Embedded descriptive tags:
All tags apply to the line below the tag.
@error [ignore | autofix | verbose] - what action to take in case of an error 
@success [true | false]  - did the line commit successfully to the DB
  
NOTES: 
-Try to fix any unexpected errors on your own or find someone else who
 can fix it. Do NOT ignore errors.
-Any changes made to fix script errors should be made in the generated
 intermediate file. 
-During an error, the application can be rerun and and it will pick up
 command execution on the last command that did not execute successfully (only commands that 
 do not have a @success true tag will run).  
-Output can be logged to a file by changing :
 client_log_file=false to true in config.properties.

Suggested DBUpdate Process Steps
1) Install any new JRE (optional)
2) Update DBMS version (example: oracle 8 to oracle 9) (optional)
3) Install new Yukon software (clients, server, etc)
4) Execute DBUpdate application (DBToolsFrame.bat file in \yukon\client\bin\ )

 *
 * Allows the user to update and change the DB. Here is a quick summary of what
 * format the files read should be in:
 * 
 * 1) All comments should begin and end on 1 line.
 * 2) a ; (semi colon) should follow every command
 * 3) File name must have the version number as the first 4 chars (ex: 2.40, 2.41)
 * 4) All meta data tags (the @ sign) should be nested in a comment
 * 5)
 * 
 * 
 */
public class DBUpdater extends MessageFrameAdaptor
{
	//local class to execute update functions
	private UpdateDB updateDB = null;
    
	private boolean isIgnoreAllErrors = false;
    private boolean isIgnoreBlockErrors = false;

	
	private static final SimpleDateFormat frmt = new SimpleDateFormat("_MM-dd-yyyy_HH-mm-ss");
	
	//less typing for these
	private static final String FS = System.getProperty("file.separator");
	private static final String LF = System.getProperty("line.separator");

	public final static String[] CMD_LINE_PARAM_NAMES = 
	{
		IRunnableDBTool.PROP_VALUE,
		"verbose"
	};

	/**
	 * 
	 */
	public DBUpdater()
	{
		super();
	}

	public String getName()
	{
		return "DBUpdater";
	}

	public String getParamText()
	{
		return "Update Files Directory:";
	}

	public String getDefaultValue()
	{
		return CtiUtilities.USER_DIR;
	}

	public static void initApp( String[] args )
	{
		final CommandLineParser parser;
		parser = new CommandLineParser( CMD_LINE_PARAM_NAMES );
		String[] values = parser.parseArgs( args );
			
		//store all the command line args we want
		for( int i = 0; i < values.length; i++ )
			if( values[i] != null )
				System.setProperty(CMD_LINE_PARAM_NAMES[i], values[i]);
		

		String verb = System.getProperty(CMD_LINE_PARAM_NAMES[1]);
		if( verb != null )
			CTILogger.setLogLevel( 
					(Boolean.valueOf(verb).booleanValue() ? "DEBUG" : "INFO") );
		else
			CTILogger.setLogLevel( "DEBUG" );

	}

	public void run()
	{
		updateDB = new UpdateDB( getIMessageFrame() );

		try
		{
			getUpdateCommands();
			
			getIMessageFrame().addOutput("");
			getIMessageFrame().addOutput("		Lines read from files successfully, starting DB transactions..." );
			getIMessageFrame().addOutput("");			

			if( executeCommands() )
				getIMessageFrame().finish( "DBUpdate Completed Successfully" );
			else
				getIMessageFrame().addOutput( "DBUpdate was Unsuccessfully executed" );
		}
		catch( Exception e )
		{
			getIMessageFrame().addOutput( "DBUpdate was Unsuccessfully executed" );
			CTILogger.warn( "A problem occurred in the main execution", e );
		}

	}
	
	
	/**
	 * Main entry point
	 * @param args java.lang.String[]
	 */
	public static void main(String[] args) throws java.io.IOException 
	{
		System.setProperty("cti.app.name", "DBUpdater");
		DBUpdater updater = new DBUpdater();

		if( args.length < 1 )  // the user did not enter any params
		{
			System.out.println("Updates the database with the DBupdate script files given directory.");
			System.out.println("An intermediate file is generated in the " + CtiUtilities.getLogDirPath() );
			System.out.println("directory for each DBUpdate file found.");
			System.out.println("");
			System.out.println(" DBUpdater " + IRunnableDBTool.PROP_VALUE + "=<SRC_PATH> [verbose= true | false]");
			System.out.println("");
			System.out.println("   " + IRunnableDBTool.PROP_VALUE + "   : directory that contains the script files for updating the DB");
			System.out.println("   verbose  : should we show the most output possible (default true)");
			System.out.println("");
			System.out.println(" example: DBUpdater " + IRunnableDBTool.PROP_VALUE + "=d:" +
						FS + "YukonMiscInstall" + FS + "YukonDatabase" + FS + "DatabaseUpdates" +
						FS + "SqlServer");
			
			System.exit(1);
		}
		

		initApp( args );

		updater.run();
	}


	private synchronized boolean executeCommands()
	{
		//get all the files in the log DIR
		FileVersion[] fileVers = updateDB.getDBUpdateFiles( CtiUtilities.getLogDirPath() );

		Connection conn = 
			PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );

		String cmd = null;
		File sqlFile = null;
		UpdateLine[] validLines = null;

		try
		{
			//be sure every trx commits after its execution automatically
			conn.setAutoCommit( true );

			for( int i = 0; i < fileVers.length; i++ )
			{
				sqlFile = fileVers[i].getFile();
				validLines = updateDB.readFile( sqlFile );
				isIgnoreAllErrors = false;
                isIgnoreBlockErrors = false;

				for( int j = 0; j < validLines.length; j++ )
				{
					cmd = validLines[j].getValue().toString(); 
					processLine( validLines[j], conn );					
				}

				//print a new version of this file
				printUpdateLines( validLines, sqlFile );

				//we will rename the file since it was a success
				renameFile( sqlFile );
			}
			
			

			getIMessageFrame().addOutput("   SUCCESS for ALL_COMMANDS");
			getIMessageFrame().addOutput("----------------------------------------------------------------------------------------------");

			
			return true;
		}
		catch( Exception e )
		{
			getIMessageFrame().addOutput( "   **FAILURE : " + cmd );
			getIMessageFrame().addOutput( "     Please fix in file : " + 
					sqlFile.getAbsolutePath() );

			handleException( e );  //array-index, SQLException, .....
			
			//write the last file being processed with errors
			printUpdateLines( validLines, sqlFile );

			return false;
		}
		finally
		{	//no mattter what, close the conn
			try
			{
				conn.close();
			}
			catch( Exception e )
			{
				handleException( e );
			}
		}
		
	}
    
    private void setIgnoreState( final UpdateLine line_ )
    {
        //once True, always True
        isIgnoreAllErrors |= line_.isIgnoreRemainingErrors();
        
        if( line_.isIgnoreBegin() )
            isIgnoreBlockErrors = true; 

        if( line_.isIgnoreEnd() )
            isIgnoreBlockErrors = false;
    }

	private void processLine( UpdateLine line_, Connection conn ) throws SQLException
	{
		if( line_ == null || conn == null )
			return;

		String cmd = null;

		if( !line_.isSuccess() )
		{
			Statement stat = conn.createStatement();
			
			cmd = line_.getValue().toString().substring( 
						0,
						line_.getValue().toString().indexOf(DBMSDefines.LINE_TERM) );

            //set any error ignore flags
            setIgnoreState( line_ );


			if( line_.isIgnoreError() 
                || isIgnoreAllErrors
                || isIgnoreBlockErrors )
			{
				try
				{
					stat.execute( cmd );
					line_.setSuccess( true );
					getIMessageFrame().addOutput( "   SUCCESS : " + cmd );				
				}
				catch( Exception ex ) //SQLException ex )
				{
					//since we are ignoring errors, do not let the SQL error force use to exit
					line_.setSuccess( false );
					getIMessageFrame().addOutput( "   UNSUCCESSFUL : " + cmd );
					getIMessageFrame().addOutput( "     (IGNORING ERROR) : " + ex.getMessage() );
				}

			}
			else
			{
				stat.execute( cmd );
				line_.setSuccess( true );
				getIMessageFrame().addOutput( "   SUCCESS : " + cmd );
			}

			try
			{
				if( stat != null )
					stat.close(); //free up any open cursors please
			}
			catch( Exception e ) {} //ain't no thang
		}
	
	}


	private void renameFile( File sqlFile ) throws IOException
	{
		//upon completion of this file, rename it
		if( !sqlFile.renameTo(
				new File(
						sqlFile.getParentFile().getCanonicalPath()
						+ FS
						+ sqlFile.getName()
						+ frmt.format(new java.util.Date()) ) ) )
			throw new IOException(
					"Unable to rename " + sqlFile.getName() + " file"); 		
	}


	private void getUpdateCommands()
	{		
		final String srcPath = System.getProperty(CMD_LINE_PARAM_NAMES[0]);
		//"d:/eclipse/head/yukon-database/DBUpdates/oracle" );


		UpdateLine[] validLines = new UpdateLine[0];		

		//get all the files in the DIR
		FileVersion[] filesVers = updateDB.getDBUpdateFiles( srcPath );



		for( int i = 0; i < filesVers.length; i++ )
		{
			File sqlFile = filesVers[i].getFile();


			getIMessageFrame().addOutput("----------------------------------------------------------------------------------------------");
			getIMessageFrame().addOutput("Start reading file : " + sqlFile );

			try
			{
				validLines = updateDB.readFile( sqlFile );
			}
			catch( DBUpdateException e )
			{
				handleException( e );
			}
			
			getIMessageFrame().addOutput("Done reading file");
			
			//find out if we need to right the valid file for this version			
			if( !UpdateDB.isValidUpdateFile(sqlFile) )
				printUpdateLines( validLines, 
						new File( CtiUtilities.getLogDirPath() + 
							filesVers[i].getVersion() +
							"_" + filesVers[i].getBuild() +
							DBMSDefines.NAME_VALID) );
			
			getIMessageFrame().addOutput("----------------------------------------------------------------------------------------------");
		}

		getIMessageFrame().addOutput("");
		getIMessageFrame().addOutput("----------------------------------------------------------------------------------------------");
		getIMessageFrame().addOutput("Valid SQL strings LOADED (" + validLines.length + ")" );
		getIMessageFrame().addOutput("----------------------------------------------------------------------------------------------");
	}



	private void printObjects( final Object[] values_, final File file_ )
	{ 	
		try
		{			
			FileWriter fileWriter = new FileWriter(file_);
			
			for( int i = 0; i < values_.length; i++ )
			{
				fileWriter.write( values_[i].toString() + LF );
			}

			fileWriter.close();						
		}		
		catch( Exception e)
		{
			CTILogger.error( e.getMessage(), e );
		}

	}
	

	private void printUpdateLines( final UpdateLine[] values_, final File file_ )
	{ 	
		if( values_ == null || file_ == null )
			return;

		try
		{			
			FileWriter fileWriter = new FileWriter(file_);

			for( int i = 0; i < values_.length; i++ )
			{
				String[] lines = values_[i].getPrintableText();
				for( int j = 0; j < lines.length; j++ )
				{
					fileWriter.write( lines[j] + LF );
				}

			}

			fileWriter.close();						
		}		
		catch( Exception e)
		{
			CTILogger.error( e.getMessage(), e );
		}

	}

	
	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) 
	{
		getIMessageFrame().addOutput("--------- CAUGHT EXCEPTION ---------");
		getIMessageFrame().addOutput("    " + exception.getMessage() );

		CTILogger.error("--------- CAUGHT EXCEPTION ---------");
		CTILogger.error( exception.getMessage(), exception );
	}
	

}
