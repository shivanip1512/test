package com.cannontech.dbtools.DBCompare;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.commandlineparameters.CommandLineParser;
import com.cannontech.common.exception.StarsNotCreatedException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.version.VersionTools;
import com.cannontech.database.db.version.CTIDatabase;
import com.cannontech.dbtools.updater.MessageFrameAdaptor;
import com.cannontech.tools.gui.IMessageFrameWriter;
import com.cannontech.tools.gui.IRunnableDBTool;
import com.cariboulake.db.schemacompare.app.SchemaCompare;
import com.cariboulake.db.schemacompare.app.SchemaCompareConnection;
import com.cariboulake.db.schemacompare.app.SchemaCompareParameters;
import com.cariboulake.db.schemacompare.business.domain.dbcompare.DbDifferences;

/*
The following minimum versions of software are needed:
 JRE 1.4.2
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
    name of the file is left unchanged. The next time this application is run, it
    will pick up the intermediate file instead of the DBupdate file.

Embedded descriptive tags:
All tags apply to the line below the tag.
@error [ignore | ignore-remaining | ignore-begin | ignore-end ] - what action to take in case of an error 
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
*/

/**
 * @author rneuharth
 * Allows the user to update and change the DB. Here is a quick summary of what
 * format the files read should be in:
 * 
 * 1) All comments should begin and end on 1 line.
 * 2) a ; (semi colon) should follow every command
 * 3) File name must have the version number as the first 4 chars (ex: 2.40, 2.41)
 * 4) All meta data tags (the @ sign) should be nested in a comment
 */
public class DBCompare extends MessageFrameAdaptor
{
    private static final String FS = System.getProperty("file.separator");

    public final static String[] CMD_LINE_PARAM_NAMES = 
    {
        IRunnableDBTool.PROP_VALUE,
        "verbose"
    };

    /**
     * 
     */
    public DBCompare()
    {
        super();
    }

    public String getName()
    {
        return "DBCompare";
    }

    public String getParamText()
    {
        return "Database Schema File:";
    }

    public String getDefaultValue()
    {
        return CtiUtilities.CURRENT_DIR;
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
    }

    public void run()
    {
        try
        {
            CTIDatabase db = VersionTools.getDBVersionRefresh();
            getIMessageFrame().addOutput("CONNECTING TO THE FOLLOWING DATABASE:");
            getIMessageFrame().addOutput("   DB Version   : " + db.getVersion() + "  Build:  " + db.getBuild() );
            getIMessageFrame().addOutput("   DB Alias     : " + CtiUtilities.getDatabaseAlias() );          
            getIMessageFrame().addOutput("  ");          
            
            final String srcPath = System.getProperty(CMD_LINE_PARAM_NAMES[0]);
            String[] split = srcPath.split("\\\\");
            String fileName = split[split.length-1]; 
            
            SchemaCompare schemaCompare = new SchemaCompare();
            
            // Creates the two instances of the database and compares them to each other
            SchemaCompareConnection schCompConn1 = 
                new SchemaCompareConnection("Local Database");
            SchemaCompareConnection schCompConn2 = 
                new SchemaCompareConnection(fileName, srcPath);
            SchemaCompareParameters schCompParams = new SchemaCompareParameters(schCompConn1, schCompConn2);
            DbDifferences schemaDifferences = schemaCompare.checkDifferences(schCompParams);

            // Writes the comparison information to the dbtools output panel
            IMessageFrameWriter messageFrameWriter = new IMessageFrameWriter(getIMessageFrame());
            schemaDifferences.printDifferences(messageFrameWriter);
            
        } catch (StarsNotCreatedException e) {
            String errorString = "WARNING!!!  STARS database tables are missing and cannot be automatically created." + 
                            "\r\nContact Technical Support or TSSL immediately to get the STARS Database Creation script\r\n before continuing with this tool.";
            getIMessageFrame().addOutput(errorString);
            CTILogger.error(errorString, e );
        
        } catch( Exception e ) {
            getIMessageFrame().addOutput( "DBUpdate was Unsuccessfully executed" );
            CTILogger.warn( "A problem occurred in the execution", e );
        }

    }
  
    public static void main(String[] args) throws java.io.IOException 
    {
        System.setProperty("cti.app.name", "DBCompare");

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

    }
}
