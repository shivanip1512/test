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
import java.util.StringTokenizer;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
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

	private IMessageFrame output = null;
    private Double version = null;
    private boolean starsToBeCreated = false;

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

            String sql = "select max(version) as version from " + CTIDatabase.TABLE_NAME;

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

            String sql = "select max(build) as build from " + CTIDatabase.TABLE_NAME
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
		ArrayList versions = new ArrayList(32);

		//previously generated list of valid commands
		final File genDIR = new File( CtiUtilities.getLogDirPath() );
		if(genDIR.listFiles() == null) {
		    CTILogger.error("Could not find valid client log directory: " + CtiUtilities.getLogDirPath());
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
	 * Handles any include statements by reading in the included file
	 * @param token
	 * @param file
	 * @return
	 */	
	private UpdateLine[] handleIncludeMeta( final String token, final File file )
	{
		UpdateLine[] starsLines = null;

		if( token.indexOf(DBMSDefines.STARS_INC) > 0 && starsExistsOrWillExist() )
		{
			getIMessageFrame().addOutput( "    <-=======================================->" );
			getIMessageFrame().addOutput( "            Loading STARS updates" );
			
			try
			{
				//d:/eclipse/head/yukon-database/dbupdates/sqlserver
				File starsFile = new File( file.getParent() + "/stars/" +
					file.getName().substring(0, file.getName().length()-4) + "-stars.sql" );					

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

	private UpdateLine[] handleCreateStarsMeta( final String token, final File file )
	{
		UpdateLine[] starsLines = null;

		if( token.indexOf(DBMSDefines.STARS_CREATE) > 0 && !starsExistsOrWillExist())
		{
			getIMessageFrame().addOutput( "    <-=======================================->" );
			getIMessageFrame().addOutput( "            Loading STARS Creation" );
			
			try
			{
				//d:/eclipse/head/yukon-database/dbupdates/sqlserver
				File starsFile = new File( file.getParent() + "/stars/" +
					file.getName().substring(0, file.getName().length()-4) + "-createstars.sql" );					

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
				ArrayList validLines = new ArrayList(512);
				java.io.RandomAccessFile fileReader = new java.io.RandomAccessFile(file, "r");
				String token = "";
				UpdateLine updLine = new UpdateLine();
				boolean commentState = false;
				boolean blockState = false;

				while( fileReader.getFilePointer() < fileReader.length() )
				{
					token = fileReader.readLine().trim();

					if( isValidString(token) )
					{

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
                                updLine = new UpdateLine();
                                updLine.getValue().append(DBMSDefines.START_BLOCK + " ");
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
								UpdateLine[] extraLines = handleIncludeMeta( token, file );

								//if we have lines from the include, add them to our VALIDs list
								//Stars updates extra lines
								if( extraLines != null )
									for( int i = 0; i < extraLines.length; i++ ) {
										validLines.add( extraLines[i] );
									}
								//Stars Creation extra lines
								extraLines = handleCreateStarsMeta( token, file );
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

				fileReader.close();

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