package com.cannontech.stars.util;

import java.util.List;


import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.commander.YC;
import com.cannontech.clientutils.commander.YCDefaults;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ServerUtils {
    
	// Temporary data files/directories
	public static final String SWITCH_COMMAND_FILE = "switch_commands.txt";
	public static final String OPTOUT_EVENT_FILE = "optout_events.txt";
	public static final String UPLOAD_DIR = "upload";
	public static final String ARCHIVE_DIR = "archive";
	public static final String FAILED_IMPORT_DIR = "failedImport";
	public static final String IMPORT_INPUT_DIR = "importInput";
	
	public static final int COMMAND_PRIORITY_CONFIG = 6;
	public static final int COMMAND_PRIORITY_SERVICE = 7;

    public static final int ADDRESSING_GROUP_NOT_FOUND = -999999;
    
	// Directory for all the temporary files
    private static final String STARS_TEMP_DIR = "stars_temp";
    
    //Environment variable for directory location
    private static final String SWITCH_BATCH_PATH = "switch.batch.path";
    //data files for writing out switch commands
    public static final String SA205_FILE = "sa_205.txt";
    public static final String SA305_FILE = "sa_305.txt";
    public static final String EXPRESSCOM_FILE = "expresscom.txt";
    public static final String VERSACOM_FILE = "versacom.txt";
    public static final String PROBLEM_FILE = "problem.txt";
	
	// YC object used for sending command to porter
	private static YC yc = null;
	
	private synchronized static YC getYC() {
		if (yc == null) {
			yc = new YC();
			yc.addObserver( new java.util.Observer() {
				public void update(java.util.Observable o, Object arg) {
					if (arg instanceof String) {
						CTILogger.info( (String)arg );
					}
					else {
						CTILogger.info( ((YC)o).getResultText() );
						((YC)o).clearResultText();
					}
				}
			});
		}
		
		return yc;
	}
	
	public static void sendSerialCommand(String command, int routeID, LiteYukonUser user) throws WebClientException
	{
		if (routeID == 0)
			throw new WebClientException("The route to send the switch command is not specified.");
		
		int priority = (command.indexOf("putconfig service") >= 0)? COMMAND_PRIORITY_SERVICE : COMMAND_PRIORITY_CONFIG;
		YCDefaults ycDefaults = new YCDefaults( priority, false, true, false);
		
		YC yc = getYC();
		synchronized (yc) {
    			yc.setYCDefaults( ycDefaults );
    			yc.setRouteID( routeID );
                /*We now need to pass in a user for the permission checks to work properly*/
                yc.setLiteUser(user);
                yc.setCommandStringWithoutPaoAuth( command );
    			yc.handleSerialNumber();
		}
	}
	
	public static void handleDBChangeMsg(DBChangeMsg msg) {
		if (msg != null) {
			DefaultDatabaseCache.getInstance().handleDBChangeMessage( msg );
			
			IServerConnection conn = ConnPool.getInstance().getDefDispatchConn();
			if (conn == null || !conn.isValid()) {
				CTILogger.error( "Not connected to dispatch." );
				return;
			}
			
			conn.write( msg );
		}
	}
	
	public static void handleDBChange(com.cannontech.database.data.lite.LiteBase lite, DbChangeType dbChangeType) {
		DBChangeMsg msg = null;
		
		if (lite == null) {
			msg = new DBChangeMsg( 0, Integer.MAX_VALUE, "", "", dbChangeType);
		}
		else if (lite.getLiteType() == LiteTypes.STARS_CUST_ACCOUNT_INFO) {
			msg = new DBChangeMsg(
				lite.getLiteID(),
				DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB,
				DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
				DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
				dbChangeType
				);
		}
		else if (lite.getLiteType() == LiteTypes.YUKON_USER) {
			msg = new DBChangeMsg(
				lite.getLiteID(),
				DBChangeMsg.CHANGE_YUKON_USER_DB,
				DBChangeMsg.CAT_YUKON_USER,
				DBChangeMsg.CAT_YUKON_USER,
				dbChangeType
				);
		}
		else if (lite.getLiteType() == LiteTypes.CONTACT) {
			msg = new DBChangeMsg(
				lite.getLiteID(),
				DBChangeMsg.CHANGE_CONTACT_DB,
				DBChangeMsg.CAT_CUSTOMERCONTACT,
				DBChangeMsg.CAT_CUSTOMERCONTACT,
				dbChangeType
				);
		}
		else if (lite.getLiteType() == LiteTypes.YUKON_GROUP) {
			msg = new DBChangeMsg(
				lite.getLiteID(),
				DBChangeMsg.CHANGE_YUKON_USER_DB,
				DBChangeMsg.CAT_YUKON_USER_GROUP,
				DBChangeMsg.CAT_YUKON_USER_GROUP,
				dbChangeType
				);
		}
		else if (lite.getLiteType() == LiteTypes.ENERGY_COMPANY) {
			msg = new DBChangeMsg(
				lite.getLiteID(),
				DBChangeMsg.CHANGE_ENERGY_COMPANY_DB,
				DBChangeMsg.CAT_ENERGY_COMPANY,
				DBChangeMsg.CAT_ENERGY_COMPANY,
				dbChangeType
				);
		}
		else if (lite.getLiteType() == LiteTypes.CUSTOMER) {
			msg = new DBChangeMsg(
				lite.getLiteID(),
				DBChangeMsg.CHANGE_CUSTOMER_DB,
				DBChangeMsg.CAT_CUSTOMER,
				DBChangeMsg.CAT_CUSTOMER,
				dbChangeType
				);
		}
		else if (lite.getLiteType() == LiteTypes.CUSTOMER_CI) {
			msg = new DBChangeMsg(
				lite.getLiteID(),
				DBChangeMsg.CHANGE_CUSTOMER_DB,
				DBChangeMsg.CAT_CI_CUSTOMER,
				DBChangeMsg.CAT_CI_CUSTOMER,
				dbChangeType
				);
		}
		else if( lite.getLiteType() == LiteTypes.STARS_SERVICE_COMPANY_DESIGNATION_CODE){
			msg = new DBChangeMsg(
				lite.getLiteID(),
				DBChangeMsg.CHANGE_SERVICE_COMPANY_DESIGNATION_CODE_DB,
				DBChangeMsg.CAT_SERVICE_COMPANY_DESIGNATION_CODE,
				DBChangeMsg.CAT_SERVICE_COMPANY_DESIGNATION_CODE,
				dbChangeType
				);
		}

		handleDBChangeMsg( msg );
	}
	
	public static String getStarsTempDir() {
		final String fs = System.getProperty( "file.separator" );
		String serverBase = null;
		
		String catBase = System.getProperty( "catalina.base" );
		if (catBase != null) {
			serverBase = catBase;
		} 
		else {
			String yukonBase = System.getProperty( "yukon.base" );
			if (yukonBase != null) {
				serverBase = yukonBase;
			} 
			else {
				serverBase = "C:" + fs + "yukon";
			}
		}
		
		return serverBase + fs + STARS_TEMP_DIR;
	}
    
    public static String getFileWriteSwitchConfigDir()
    {
        String dirBase = null;
        
        String temp = System.getProperty( SWITCH_BATCH_PATH );
        if (temp != null) {
            dirBase = temp;
        } 
        else 
        {
            dirBase = getStarsTempDir();
        }
        
        return dirBase;
    }
    
    public static<T> T returnFirstRow(List<T> list, String errorString) {
    	if (list.size() > 1) {
    		CTILogger.warn(errorString + " got " + list.size() + " rows");
    	}
    	return list.get(0);
    }
}
