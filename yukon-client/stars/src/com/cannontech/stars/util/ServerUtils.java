package com.cannontech.stars.util;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.message.dispatch.message.DBChangeMsg;
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
	
	// Default sender email address from Stars
	public static final String ADMIN_EMAIL_ADDRESS = "info@cannontech.com";
	
	public static final Comparator YUK_LIST_ENTRY_ALPHA_CMPTR = new Comparator() {
		public int compare(Object o1, Object o2) {
			YukonListEntry entry1 = (YukonListEntry) o1;
			YukonListEntry entry2 = (YukonListEntry) o2;
			int res = entry1.getEntryText().compareTo( entry2.getEntryText() );
			if (res == 0) res = entry1.getEntryID() - entry2.getEntryID();
			return res;
		}
	};
	
	
	public static void sendSerialCommand(String command, int routeID) throws WebClientException
	{
		if (routeID == 0)
			throw new WebClientException("The route to send the serial command on is not specified");
		
		com.cannontech.yc.gui.YC yc = SOAPServer.getYC();
		synchronized (yc) {
			yc.setRouteID( routeID );
			yc.setCommandString( command );
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
	
	private static String[] readLines(java.io.Reader reader, boolean returnEmpty) throws IOException {
		java.io.BufferedReader br = null;
		try {
			br = new java.io.BufferedReader( reader );
			
			ArrayList lines = new ArrayList();
			String line = null;
			
			while ((line = br.readLine()) != null) {
				if (!returnEmpty && (line.trim().equals("") || line.charAt(0) == '#'))
					continue;
				lines.add(line);
			}
			
			String[] lns = new String[ lines.size() ];
			lines.toArray( lns );
			return lns;
		}
		finally {
			if (br != null) br.close();
		}
	}
	
	public static String[] readInputStream(java.io.InputStream is, boolean returnEmpty) {
		try {
			return readLines( new java.io.InputStreamReader(is), returnEmpty );
		}
		catch (IOException e) {
			CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}
	
	public static String[] readFile(File file, boolean returnEmpty) {
		if (file.exists()) {
			try {
				return readLines( new java.io.FileReader(file), returnEmpty );
			}
			catch (IOException e) {
				CTILogger.error( e.getMessage(), e );
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
