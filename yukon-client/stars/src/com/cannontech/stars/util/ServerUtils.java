package com.cannontech.stars.util;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Date;
import java.util.TimeZone;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.EnergyCompanyFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ServerUtils {
    
	public static final String AUTO_GEN_NUM_PREC = "##";
    
	// If date in database is earlier than this, than the date is actually empty
	public static long VERY_EARLY_TIME = 1000 * 3600 * 24;
    
	public static final java.text.SimpleDateFormat starsDateFormat =
			new java.text.SimpleDateFormat( "yyyyMMdd" );
	public static final java.text.SimpleDateFormat starsTimeFormat =
			new java.text.SimpleDateFormat( "HHmm" );

	// Increment this for every message
	private static long userMessageIDCounter = 1;
	
	private static final java.text.SimpleDateFormat dateTimeFormat =
			new java.text.SimpleDateFormat("MM/dd/yy HH:mm");
	
	// Directory for all the temporary files
    private static final String STARS_TEMP_DIR = "stars_temp";
    
    // Temporary data files/directories
	public static final String SWITCH_COMMAND_FILE = "switch_commands.txt";
	public static final String OPTOUT_EVENT_FILE = "optout_events.txt";
	public static final String UPLOAD_DIR = "upload";
	
	public static void sendSerialCommand(String command, int routeID)
	{
		com.cannontech.yc.gui.YC yc = SOAPServer.getYC();
		synchronized (yc) {
			yc.setRouteID( routeID );
			yc.setCommand( command );
			yc.handleSerialNumber();
		}
	}
	
	/**
	 * Return date in the format of MM/dd/yy HH:mm in the specified time zone
	 */
	public static String formatDate(Date date, TimeZone tz) {
		if (tz != null)
			dateTimeFormat.setTimeZone( tz );
		else
			dateTimeFormat.setTimeZone( TimeZone.getDefault() );
		return dateTimeFormat.format( date );
	}
	
	public static boolean isOperator(StarsYukonUser user) {
		return !isResidentialCustomer(user) &&
				EnergyCompanyFuncs.getEnergyCompany( user.getYukonUser() ) != null;
	}
	
	public static boolean isResidentialCustomer(StarsYukonUser user) {
		return AuthFuncs.checkRole(user.getYukonUser(), ResidentialCustomerRole.ROLEID) != null;
	}
	
	public static void handleDBChangeMsg(DBChangeMsg msg) {
		if (msg != null) {
			DefaultDatabaseCache.getInstance().handleDBChangeMessage( msg );
			
			com.cannontech.message.util.ClientConnection conn = SOAPServer.getInstance().getClientConnection();
			if (conn == null) {
				CTILogger.error( "Cannot get dispatch client connection" );
				return;
			}
			
			conn.write( msg );
		}
	}
	
	public static void handleDBChange(com.cannontech.database.data.lite.LiteBase lite, int typeOfChange) {
		DBChangeMsg msg = null;
		
		if (lite == null) {
			msg = new DBChangeMsg( 0, Integer.MAX_VALUE, "", "", typeOfChange );
		}
		else if (lite.getLiteType() == LiteTypes.STARS_CUST_ACCOUNT_INFO) {
			msg = new DBChangeMsg(
				lite.getLiteID(),
				DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB,
				DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
				DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
				typeOfChange
				);
		}
		else if (lite.getLiteType() == LiteTypes.YUKON_USER) {
			msg = new DBChangeMsg(
				lite.getLiteID(),
				DBChangeMsg.CHANGE_YUKON_USER_DB,
				DBChangeMsg.CAT_YUKON_USER,
				DBChangeMsg.CAT_YUKON_USER,
				typeOfChange
				);
		}
		else if (lite.getLiteType() == LiteTypes.CONTACT) {
			msg = new DBChangeMsg(
				lite.getLiteID(),
				DBChangeMsg.CHANGE_CONTACT_DB,
				DBChangeMsg.CAT_CUSTOMERCONTACT,
				DBChangeMsg.CAT_CUSTOMERCONTACT,
				typeOfChange
				);
		}
		else if (lite.getLiteType() == LiteTypes.YUKON_GROUP) {
			msg = new DBChangeMsg(
				lite.getLiteID(),
				DBChangeMsg.CHANGE_YUKON_USER_DB,
				DBChangeMsg.CAT_YUKON_USER_GROUP,
				DBChangeMsg.CAT_YUKON_USER_GROUP,
				typeOfChange
				);
		}
		else if (lite.getLiteType() == LiteTypes.ENERGY_COMPANY) {
			msg = new DBChangeMsg(
				lite.getLiteID(),
				DBChangeMsg.CHANGE_ENERGY_COMPANY_DB,
				DBChangeMsg.CAT_ENERGY_COMPANY,
				DBChangeMsg.CAT_ENERGY_COMPANY,
				typeOfChange
				);
		}
		else if (lite.getLiteType() == LiteTypes.CUSTOMER) {
			msg = new DBChangeMsg(
				lite.getLiteID(),
				DBChangeMsg.CHANGE_CUSTOMER_DB,
				DBChangeMsg.CAT_CUSTOMER,
				DBChangeMsg.CAT_CUSTOMER,
				typeOfChange
				);
		}
		else if (lite.getLiteType() == LiteTypes.CUSTOMER_CI) {
			msg = new DBChangeMsg(
				lite.getLiteID(),
				DBChangeMsg.CHANGE_CUSTOMER_DB,
				DBChangeMsg.CAT_CI_CUSTOMER,
				DBChangeMsg.CAT_CI_CUSTOMER,
				typeOfChange
				);
		}
		
		handleDBChangeMsg( msg );
	}
	
	public static Date translateDate(long time) {
		if (time < VERY_EARLY_TIME) return null;
		return new Date(time);
	}

	public static String forceNotNull(String str) {
		return (str == null) ? "" : str.trim();
	}

	public static String forceNotNone(String str) {
		String str1 = forceNotNull(str);
		return (str1.equalsIgnoreCase("(none)")) ? "" : str1;
	}
	
	public static String getNotification(LiteContactNotification liteNotif) {
		String notification = (liteNotif == null)? null : liteNotif.getNotification();
		return forceNotNull(notification);
	}
	
	public static String formatName(LiteContact liteContact) {
		StringBuffer name = new StringBuffer();
		
		String firstName = forceNotNone( liteContact.getContFirstName() ).trim();
		if (firstName.length() > 0)
			name.append( firstName );
		
		String lastName = forceNotNone( liteContact.getContLastName() ).trim();
		if (lastName.length() > 0)
			name.append(" ").append( lastName );
		
		return name.toString();
	}
	
	public static String[] readFile(File file, boolean returnEmpty) {
		if (file.exists()) {
			java.io.BufferedReader fr = null;
			try {
				fr = new java.io.BufferedReader( new java.io.FileReader(file) );
				
				ArrayList lines = new ArrayList();
				String line = null;
				
				while ((line = fr.readLine()) != null) {
					if (!returnEmpty && (line.trim().equals("") || line.charAt(0) == '#'))
						continue;
					lines.add(line);
				}
				
				String[] lns = new String[ lines.size() ];
				lines.toArray( lns );
				return lns;
			}
			catch (IOException e) {
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
				CTILogger.error("Failed to read file \"" + file.getPath() + "\"");
			}
			finally {
				try {
					fr.close();
				}
				catch (IOException e) {}
			}
		}
		else {
			CTILogger.error("Unable to find file \"" + file.getPath() + "\"");
		}
		
		return null;
	}
	
	public static String[] readFile(File file) {
		return readFile( file, true );
	}
	
	public static void writeFile(File file, String[] lines) throws IOException {
		java.io.PrintWriter fw = new java.io.PrintWriter(
				new java.io.BufferedWriter( new java.io.FileWriter(file) ));
		
		for (int i = 0; i < lines.length; i++)
			fw.println( lines[i] );
		
		fw.close();
	}
	
	public static String[] splitString(String str, String delim) {
		StreamTokenizer st = new StreamTokenizer( new StringReader(str) );
		st.resetSyntax();
		st.wordChars( 0, 255 );
		st.quoteChar( '"' );
		for (int i = 0; i < delim.length(); i++)
			st.ordinaryChar( delim.charAt(i) );
		
		ArrayList tokenList = new ArrayList();
		boolean isDelimLast = true;	// Whether the last token is a deliminator
		
		try {
			while (st.nextToken() != StreamTokenizer.TT_EOF) {
				if (isDelimLast) {
					if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"') {
						tokenList.add( st.sval );
						isDelimLast = false;
					}
					else if (st.ttype == ',') {
						tokenList.add( "" );
					}
				}
				else {
					if (st.ttype == ',') isDelimLast = true;
				}
			}
		}
		catch (IOException e) {
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			return null;
		}
		
		// If the line ends with a comma, add an empty string to the column list 
		if (isDelimLast) tokenList.add( "" );
		
		String[] tokens = new String[ tokenList.size() ];
		tokenList.toArray( tokens );
		return tokens;
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

}
