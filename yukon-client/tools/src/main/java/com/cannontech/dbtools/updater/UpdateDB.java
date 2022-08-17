/*
 * Created on Jun 2, 2003
 *
 */
package com.cannontech.dbtools.updater;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigHelper;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
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

    private IMessageFrame output = null;
    private Double version = null;
    private boolean starsToBeCreated = false;

    private static final Pattern cparmPattern = Pattern.compile(DBMSDefines.START_CPARM_REGEX);
    private static final Pattern startIfPattern = Pattern.compile(DBMSDefines.START_IF_REGEX, Pattern.CASE_INSENSITIVE);
    private static final Pattern startPattern = Pattern.compile(DBMSDefines.START_REGEX, Pattern.CASE_INSENSITIVE);
    private static final Pattern endPattern = Pattern.compile(DBMSDefines.END_REGEX, Pattern.CASE_INSENSITIVE);
    private static final Pattern SQLServerVarPattern = 
            Pattern.compile("SELECT @[A-Za-z_]+ = \\'?([A-Za-z0-9\\.\\-\\s,]+)\\'?;");
    private static final Pattern OracleVarPattern = 
            Pattern.compile("v_[A-Za-z_]+ := \\'?([A-Za-z0-9\\.\\-\\s,]+)\\'?;");
    /**
     * 
     */
    public UpdateDB( IMessageFrame output_ )
    {
        super();
        output = output_;       
    }


	public static boolean isValidString( String str_ )
	{
		if( str_ == null || str_.length() <= 0 )
			return false;


		for( int i = 0; i < DBMSDefines.IGNORE_STRINGS.length; i++ )
			if( DBMSDefines.IGNORE_STRINGS[i].equalsIgnoreCase(str_.trim()) )
				return false;
		
		return true;
	}

	public static boolean isValidUpdateFile( File file_ )
	{
		if( file_ == null )
			return false;

		if( getFileVersion(file_).getVersion() > 0.0
			 && file_.getAbsolutePath().toLowerCase().endsWith(DBMSDefines.SQL_EXT)
			 && (file_.getAbsolutePath().toLowerCase().indexOf(DBMSDefines.NAME_VALID) > -1) )
		{
			return true;
		}
		
		return false;
	}
    
    /**
     * Helper method to determine if stars tables exist
     * @return True if tables exist
     */
    public boolean starsExistsOrWillExist() {
        
        Connection conn = null;
        PreparedStatement stmt = null;

        try {

            String sql = "select count(*) from APPLIANCECATEGORY";

            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            stmt = conn.prepareStatement(sql);
            stmt.executeQuery();

            return true;

        } catch (SQLException e) {
            // do nothing - stars table does not exist
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // Double version = null;
            }
        }

        return starsToBeCreated;
    }

	/**
     * Helper method to get the current database version
     * @return Database version
     */
    private double getDBVersion() {

        if(version != null){
            return version;
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {

            String sql = "select max(version) as version from CTIDatabase";

            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            rs.next();

            version = Double.valueOf(rs.getString("version"));

        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // do nothing - tried to close
            }
        }

        return version;
    }

    /**
     * Helper method to get the current database version build
     * @return Database version build
     */
	public int getDBBuild() {
        
        int build = 0;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {

            String sql = "select max(build) as build from CTIDatabase"
                    + " where version = ?";

            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, this.getDBVersion());
            
            rs = stmt.executeQuery();
            rs.next();

            build = Integer.valueOf(rs.getString("build"));

        } catch (SQLException e) {
            // do nothing - build column may not exist on old databases
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // do nothing - tried to close
            }
        }

        return build;
	}

	/**
	 * Deals with a file name in following format:
	 *   2.42.sql
	 * 
	 * @param file_
	 * @return
	 */
	public static FileVersion getFileVersion( File file_ )
	{		
		return new FileVersion( file_ );
	}

	/**
	* @return FileVersion[]
	*
	* Gets a list of specific files from a directory.
	* First try to get the intermediate file generated.
	* If that fails, then get the original update script file
	* */
	public FileVersion[] getDBUpdateFiles( String rootDIR )
	{
		ArrayList<FileVersion> versions = new ArrayList<FileVersion>(32);

		//previously generated list of valid commands
		final File genDIR = new File( CtiUtilities.getClientLogDir() );
		if(genDIR.listFiles() == null) {
		    CTILogger.error("Could not find valid client log directory: " + CtiUtilities.getClientLogDir());
		} else {
    		for( int i = 0; i < genDIR.listFiles().length; i++ )
    		{
    			File sqlFile = genDIR.listFiles()[i];
    			if( sqlFile.isDirectory() )
    				continue;
    
    			FileVersion fVers = getFileVersion(sqlFile);
    			if( fVers.getVersion() < DBMSDefines.MIN_VERSION
    				 || fVers.getVersion() < getDBVersion()
    				 || (fVers.getVersion() == getDBVersion() && fVers.getBuild() <= getDBBuild()) )
    			{
    				//getIMessageFrame().addOutput("  IGNORING file (Version mismatch): " + sqlFile );
    				continue;
    			}
    
    			if( isValidUpdateFile(sqlFile) )
    			{
    				CTILogger.debug("  GenDir - Adding file : " + sqlFile );
                    getIMessageFrame().addOutput("  Found valid file : " + sqlFile );
    				versions.add( fVers );
    			}
    
    		}
		}
		

		//dir to look in for new updates
		final File userDIR = new File( rootDIR );
		if(userDIR.listFiles() == null) {
		    CTILogger.error("Could not find valid root log directory: " + rootDIR);
		} else {
    		for( int i = 0; i < userDIR.listFiles().length; i++ )
    		{
    			File sqlFile = userDIR.listFiles()[i];
    			if( sqlFile.isDirectory() )
    				continue;
    
    			FileVersion fVers = getFileVersion(sqlFile);			
    			if( fVers.getVersion() < DBMSDefines.MIN_VERSION
    				 || fVers.getVersion() < getDBVersion()
    				 || (fVers.getVersion() == getDBVersion() && fVers.getBuild() <= getDBBuild()) )
    			{
    				//getIMessageFrame().addOutput("  IGNORING file (Version mismatch): " + sqlFile );
    				continue;
    			}
    
    			if( sqlFile.getAbsolutePath().toLowerCase().endsWith(DBMSDefines.SQL_EXT) 
    				 && !versions.contains(fVers) )
    			{
    				CTILogger.debug("  UserDIR - Adding file : " + sqlFile );
                    getIMessageFrame().addOutput("  Found valid file : " + sqlFile );
                    versions.add( fVers );
    			}
    
    		}
		}


		FileVersion[] dbUpdateFiles = null;
		dbUpdateFiles = new FileVersion[ versions.size() ];
		dbUpdateFiles = (FileVersion[])versions.toArray( dbUpdateFiles );

		//be sure the returned list is in order by version number
		Arrays.sort( dbUpdateFiles, FileVersion.FILE_COMP );
		return dbUpdateFiles;
	}

    /**
     *
     * This comment may contain some meta data, process it
     * if so.
     **/
    private void handleComment(final String token_, final UpdateLine updLine_) {
        int metaIndx = token_.indexOf(DBMSDefines.META_TAG);
        int startIndx = token_.indexOf(DBMSDefines.META_START);
        if (startIndx >= 0) {
            StringTokenizer tokenizer = new StringTokenizer(token_.substring(metaIndx + 1), DBMSDefines.META_TOKEN);
            String key = tokenizer.nextToken();
            while (tokenizer.countTokens() >= 2) {
                String val = tokenizer.nextToken();
                if (!updLine_.getMetaProps().containsKey(key)) {
                    updLine_.getMetaProps().put(key, val);
                } else {
                    updLine_.getMetaProps().put(key, updLine_.getMetaProps().get(key) + " " + val);
                }
            }
        } else {
            if (metaIndx >= 0 && metaIndx < token_.length()) {
                StringTokenizer tokenizer = new StringTokenizer(token_.substring(metaIndx + 1), DBMSDefines.META_TOKEN);

                // we are only expecting the key,value pair
                if (tokenizer.countTokens() >= 2) {
                    String key = tokenizer.nextToken();
                    if (key.equals(DBMSDefines.META_ERROR)) {
                        updLine_.getMetaProps().remove(key);
                    }
                    String val = tokenizer.nextToken();
                    updLine_.getMetaProps().put(key, val);
                }

            }
        }
    }

	/**
	 * When the stars token is found, find a matching stars file and return that file
	 * @param token
	 * @param sqlFilesLocation The location of our database update files, must contain a /stars folder
	 * @param sqlFileName The name of the file being currently run, used to find the proper stars.sql file
	 * @return
	 */	
	private UpdateLine[] handleIncludeStarsMeta( final String token, final String sqlFilesLocation, final String sqlFileName )
	{
		UpdateLine[] starsLines = null;

		if( token.indexOf(DBMSDefines.STARS_INC) > 0 && starsExistsOrWillExist() )
		{
			getIMessageFrame().addOutput( "    <-=======================================->" );
			getIMessageFrame().addOutput( "            Loading STARS updates" );
			
			try
			{
				//d:/eclipse/head/yukon-database/dbupdates/sqlserver
				File starsFile = new File( sqlFilesLocation + "/stars/" +
				        sqlFileName.substring(0, sqlFileName.length()-4) + "-stars.sql" );					

				UpdateDB newUp = new UpdateDB( getIMessageFrame() );
				starsLines = newUp.readFile( starsFile );						
			}
			catch( Exception e )
			{
				getIMessageFrame().addOutput("--------- CAUGHT EXCEPTION with STARS Update ---------");
				getIMessageFrame().addOutput("    " + e.getMessage() );
			}
			
			getIMessageFrame().addOutput( "          Done with STARS updates" );
			getIMessageFrame().addOutput( "    <-=======================================->" );
		}
		
		return starsLines;
	}

	/**
     * When the stars token is found, find a matching stars file and return that file
     * @param token
     * @param sqlFilesLocation The location of our database update files, must contain a /stars folder
     * @param sqlFileName The name of the file being currently run, used to find the proper stars.sql file
     * @return
     */ 
	private UpdateLine[] handleCreateStarsMeta( final String token, final String sqlFilesLocation, final String sqlFileName )
	{
		UpdateLine[] starsLines = null;

		if( token.indexOf(DBMSDefines.STARS_CREATE) > 0 && !starsExistsOrWillExist())
		{
			getIMessageFrame().addOutput( "    <-=======================================->" );
			getIMessageFrame().addOutput( "            Loading STARS Creation" );
			
			try
			{
				//d:/eclipse/head/yukon-database/dbupdates/sqlserver
				File starsFile = new File( sqlFilesLocation + "/stars/" +
				        sqlFileName.substring(0, sqlFileName.length()-4) + "-createstars.sql" );					

				UpdateDB newUp = new UpdateDB( getIMessageFrame() );
				starsLines = newUp.readFile( starsFile );
				starsToBeCreated = true;
			}
			catch( Exception e )
			{
				getIMessageFrame().addOutput("--------- CAUGHT EXCEPTION with STARS Create---------");
				getIMessageFrame().addOutput("    " + e.getMessage() );
			}
			
			getIMessageFrame().addOutput( "          Done with STARS Create" );
			getIMessageFrame().addOutput( "    <-=======================================->" );
		}
		
		return starsLines;
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
			    // Convert the file to a list of strings
				List<String> fileStrings = new ArrayList<String>(512);
				java.io.RandomAccessFile fileReader = new java.io.RandomAccessFile(file, "r");
				while( fileReader.getFilePointer() < fileReader.length() )
                {
				    String token = "";
                    token = fileReader.readLine().trim();
                    fileStrings.add(token);
                }
				fileReader.close();
				
				List<UpdateLine> validLines = convertToUpdateLines(fileStrings, file.getParent(), file.getName());

				//file open/closed and read successfully
				UpdateLine[] cmds = new UpdateLine[ validLines.size() ];
				return (UpdateLine[])validLines.toArray( cmds );				
			} catch( Exception e) {
				getIMessageFrame().addOutput( e.getMessage() );
				CTILogger.error( e.getMessage(), e );
				throw new DBUpdateException( e );
			}
		} else {
			getIMessageFrame().addOutput( "Unable to find file '" + file +"'" );
			throw new DBUpdateException( "Unable to find file '" + file +"'" );
		}

	}


    // Given a file in string list format (fileStrings), output parsed UpdateLines
    // sqlFilesLocation and sqlFileName are used for special STARS annotations in Yukon 4.0.3 and previous
    public List<UpdateLine> convertToUpdateLines(List<String> fileStrings, final String sqlFilesLocation, final String sqlFileName) {
        List<UpdateLine> validLines = new ArrayList<UpdateLine>(512);
        UpdateLine updLine = new UpdateLine();
        boolean commentState = false;
        boolean blockState = false;
        boolean cparmState = false;
        boolean startIfState = false;
        String cparmToken = "";
        ConfigurationSource config = null;
        Matcher cparmMatcher = null;
        Matcher startIfMatcher = null;
        Matcher endMatcher = null;
        Matcher startMatcher = null;
        boolean startMatchState = false;
        for (String token : fileStrings) {
            cparmMatcher = cparmPattern.matcher(token);
            startIfMatcher = startIfPattern.matcher(token);
            endMatcher = endPattern.matcher(token);
            startMatcher = startPattern.matcher(token);
            if( isValidString(token) )
            {
                // Check to see if we're handling a cparm.
                // This can happen in a block, so check it first.
                if ( cparmState ) {
                    if (token.endsWith(DBMSDefines.END_CPARM)) {
                        // We are done processing this cparm. Grab the next line.
                        cparmState = false;
                        continue;
                    }
                    
                    if (config == null) {
                        config = MasterConfigHelper.getConfiguration();
                    }
                    
                    String configValue = config.getString(cparmToken);
                    if (configValue != null) {
                        Matcher sqlServerMatcher = SQLServerVarPattern.matcher(token);
                        Matcher oracleMatcher = OracleVarPattern.matcher(token);
                        if (sqlServerMatcher.find()) {
                            token = token.replace(sqlServerMatcher.group(1), configValue);
                        }
                        if (oracleMatcher.find()) {
                            token = token.replace(oracleMatcher.group(1), configValue);
                        }
                    }

                    // The line gets added regardless of whether we found the cparm
                    // in master.cfg.
                    updLine.getValue().append(token);
                    updLine.getValue().append(" ");
                            
                    // We're done with this line, move on.
                    continue;
                } else if( cparmMatcher.matches() ) {
                    cparmState = true;
                    cparmToken = cparmMatcher.group(1);
                    continue;
                }

                if (startIfState) {
                    startIfState = !endMatcher.find();
                    if (!startIfState) {
                        int metaIndx = token.indexOf(DBMSDefines.META_TAG);
                        StringTokenizer tokenizer =
                            new StringTokenizer(token.substring(metaIndx + 1), DBMSDefines.META_TOKEN);
                        if (metaIndx >= 0 && metaIndx < token.length() && tokenizer.countTokens() >= 2) {
                            String key = tokenizer.nextToken();
                            String value = tokenizer.nextToken();
                            updLine.getMetaProps().put(key, value);
                        }
                        continue;
                    }
                } else if (startIfMatcher.find()) {
                    startIfState = !endMatcher.find();
                }

                if (startMatchState) {
                    startMatchState = !endMatcher.find();
                    if (!startMatchState) {
                        int metaIndx = token.indexOf(DBMSDefines.META_TAG);
                        StringTokenizer tokenizer =
                            new StringTokenizer(token.substring(metaIndx + 1), DBMSDefines.META_TOKEN);
                        if (metaIndx >= 0 && metaIndx < token.length() && tokenizer.countTokens() >= 2) {
                            String key = tokenizer.nextToken();
                            String value = tokenizer.nextToken();
                            updLine.getMetaProps().put(key, value);
                        }
                        continue;
                    }
                } else if (startMatcher.find()) {
                    startMatchState = !endMatcher.find();
                }

                // Checks to see if its an end block
                if ( blockState ) {
                    // Checks to see if the file is using a slash and skips to the next line
                    if (token.equals(DBMSDefines.PROCESS_COMMAND_CHARACTER)) {
                        continue;
                    }
                    
                    blockState = !token.endsWith(DBMSDefines.END_BLOCK);
                    if(blockState) {
                        updLine.getValue().append( token );
                        updLine.getValue().append(" ");
                    }
                    //block is done, move on
                    else {
                        updLine.getValue().append(DBMSDefines.END_BLOCK);
                        validLines.add( updLine );
                        updLine = new UpdateLine();
                        continue;
                    }
                }
        		// Checks to see if its a start block
                else if( token.startsWith(DBMSDefines.START_BLOCK) ) {
                    //if we have a END_BLOCK, this comment is terminated
                    blockState = !token.endsWith(DBMSDefines.END_BLOCK);
                    if(blockState) {
                        // @error warn_once should work with blocks. Blocks otherwise would clear this setting and do not work with @error ignore-begin
                        UpdateLine oldUpdLine = updLine;
                        updLine = new UpdateLine();
                        if (startIfState || startMatchState) {
                            updLine.getMetaProps().putAll(oldUpdLine.getMetaProps());
                        }
                        if(oldUpdLine.isWarnOnce()) { 
                            
                            updLine.setWarnOnce();
                        } 
                        if (oldUpdLine.isIgnoreEnd()){
                            updLine.setIgnoreEnd();
                        }

                        if (token.startsWith(DBMSDefines.START_BLOCK)) {
                            updLine.getValue().append(DBMSDefines.START_BLOCK + " ");
                        }
                        continue;
                    }
                    //single line block, so must be from a *valids file.
                    else {
                        updLine.getValue().append(token);
                        validLines.add( updLine );
                        updLine = new UpdateLine();
                        continue;
                    }
                }
                //are we handling a comment
        	    else if( commentState )
        		{
        			commentState = !token.endsWith(DBMSDefines.COMMENT_END);
        			handleComment( token, updLine );
        		}
        		else if( token.startsWith(DBMSDefines.COMMENT_BEGIN) )
        		{
        			//if we have a COMMENT_END, this comment is terminated
        			commentState = !token.endsWith(DBMSDefines.COMMENT_END);
        				
        			if( token.indexOf(DBMSDefines.META_TAG + DBMSDefines.META_INCLUDE) > 0 )
        			{
        				UpdateLine[] extraLines = handleIncludeStarsMeta( token, sqlFilesLocation, sqlFileName );

        				//if we have lines from the include, add them to our VALIDs list
        				//Stars updates extra lines
        				if( extraLines != null )
        					for( int i = 0; i < extraLines.length; i++ ) {
        						validLines.add( extraLines[i] );
        					}
        				//Stars Creation extra lines
        				extraLines = handleCreateStarsMeta( token, sqlFilesLocation, sqlFileName );
        				if( extraLines != null )
        					for( int i = 0; i < extraLines.length; i++ ) {
        						validLines.add( extraLines[i] );
        					}
        			} else {
        				handleComment( token, updLine );
        			}
        		} else {
        			updLine.getValue().append( token );

        			if( updLine.getValue().toString().indexOf(DBMSDefines.LINE_TERM) >= 0) { //white space follows the LINE_TERM
        				updLine.setValue(new StringBuffer(updLine.getValue().toString().trim()));
        			}
        			if( updLine.getValue().toString().endsWith(DBMSDefines.LINE_TERM) ) {
        				validLines.add( updLine );
        				updLine = new UpdateLine();
        			} else {
        				updLine.getValue().append(" "); //add a blank to separate the lines (just in case)
        			}
        		}
        	}
        }
        return validLines;
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
    
    public static void main(String[] args) {
        UpdateDB udb = new UpdateDB(null);
        
        System.out.println(udb.getDBVersion());
        System.out.println(udb.getDBBuild());
    }

}