package com.cannontech.dbtools.DBCompare;

import java.sql.Connection;
import java.sql.SQLException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.clientutils.commandlineparameters.CommandLineParser;
import com.cannontech.common.exception.StarsNotCreatedException;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.version.VersionTools;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.version.CTIDatabase;
import com.cannontech.dbtools.updater.MessageFrameAdaptor;
import com.cannontech.tools.gui.IMessageFrameWriter;
import com.cannontech.tools.gui.IRunnableDBTool;
import com.cariboulake.db.schemacompare.app.SchemaCompare;
import com.cariboulake.db.schemacompare.app.SchemaCompareConnection;
import com.cariboulake.db.schemacompare.app.SchemaCompareParameters;
import com.cariboulake.db.schemacompare.business.domain.dbcompare.DbDifferences;



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
        Connection connection = null;
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
            connection = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            SchemaCompareConnection schCompConn1 = 
                new SchemaCompareConnection("Local Database", connection);
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
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                CTILogger.info("Could not close database connection", e);
            }
        }

    }
  
    public static void main(String[] args) throws java.io.IOException 
    {
        CtiUtilities.setClientAppName(ApplicationId.DB_COMPARE);
        YukonLogManager.initialize();

        if( args.length < 1 )  // the user did not enter any params
        {
            System.out.println("Updates the database with the DBupdate script files given directory.");
            System.out.println("An intermediate file is generated in the " + CtiUtilities.getClientLogDir() );
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
