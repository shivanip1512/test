package com.cannontech.stars.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.TimeZone;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiProperties;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.stars.LiteAddress;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
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

    // Increment this for every message
    private static long userMessageIDCounter = 1;
    
    // If date in database is earlier than this, than the date is actually empty
    private static long VERY_EARLY_TIME = 1000 * 3600 * 24;
	
	// Date/Time pattern strings that will be tried when attempting to interprate starting dates
	private static final java.text.SimpleDateFormat[] dateFormat =
	{
		new java.text.SimpleDateFormat("MM/dd/yy HH:mm"),
		new java.text.SimpleDateFormat("MM-dd-yy HH:mm"),
		new java.text.SimpleDateFormat("MM.dd.yy HH:mm"),
		new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm"),
		new java.text.SimpleDateFormat("MM-dd-yyyy HH:mm"),
		new java.text.SimpleDateFormat("MM.dd.yyyy HH:mm"),
	};

	//this static initializer sets all the simpledateformat to lenient
	static
	{
		for( int i = 0; i < dateFormat.length; i++ )
			dateFormat[i].setLenient(true);
	}
    
	
    public static void sendCommand(String command)
    {
    	com.cannontech.message.porter.ClientConnection conn = SOAPServer.getInstance().getPILConnection();
    	if (conn == null) {
			CTILogger.error( "Cannot get PIL client connection" );
			return;
		}
		
        com.cannontech.message.porter.message.Request req = // no need for deviceid so send 0
            new com.cannontech.message.porter.message.Request( 0, command, userMessageIDCounter++ );
        conn.write( req );
        
        CTILogger.debug( "Sent command to PIL: " + command );
    }
    
    public static void saveCommands(String fileName, String[] commands) throws IOException {
    	if (fileName == null) return;
    	
		File f = new File( fileName );
		if (!f.exists()) {
			File dir = new File( f.getParent() );
			if (!dir.exists()) dir.mkdirs();
			f.createNewFile();
		}
		
		PrintWriter fw = null;
		try {
			fw = new PrintWriter( new FileWriter(f, true) );
			for (int i = 0; i < commands.length; i++)
				fw.println( commands[i] );
		}
		finally {
			if (fw != null) fw.close();
		}
    }
	
	public static void sendEmailMsg(String from, String[] to, String[] cc, String subject, String text) throws Exception {
		CtiProperties props = CtiProperties.getInstance();
		Session session = Session.getDefaultInstance( props, null );
		
		Message emailMsg = new MimeMessage( session );
		emailMsg.setFrom( new InternetAddress(from) );
		
		Address[] toAddrs = new Address[ to.length ];
		for (int i = 0; i < to.length; i++)
			toAddrs[i] = new InternetAddress( to[i] );
		emailMsg.setRecipients( Message.RecipientType.TO, toAddrs );
		
		if (cc != null) {
			Address[] ccAddrs = new Address[ cc.length ];
			for (int i = 0; i < to.length; i++)
				ccAddrs[i] = new InternetAddress( cc[i] );
			emailMsg.setRecipients( Message.RecipientType.CC, ccAddrs );
		}
		
		emailMsg.setSubject( subject );
		emailMsg.setSentDate( new Date() );
		emailMsg.setText( text );
		
		Transport.send( emailMsg );
	}
	
	/**
	 * Return date in the format of MM/dd/yy HH:mm in the specified time zone
	 */
	public static String formatDate(Date date, TimeZone tz) {
		if (tz != null)
			dateFormat[0].setTimeZone( tz );
		else
			dateFormat[0].setTimeZone( TimeZone.getDefault() );
		return dateFormat[0].format( date );
	}
	
	/**
	 * Parse date with the format like MM/dd/yy HH:mm,
	 * MM-dd-yyyy HH:mm, etc., in the specified time zone
	 */
	public static Date parseDate(String dateStr, TimeZone tz) {
		Date retVal = null;
		
		for( int i = 0; i < dateFormat.length; i++ ) {
			try {
				dateFormat[i].setTimeZone(tz);
				retVal = dateFormat[i].parse(dateStr);
				break;
			}
			catch( java.text.ParseException pe ) {}
		}

		return retVal;	
	}
	
	public static boolean isOperator(StarsYukonUser user) {
		return (AuthFuncs.checkRole(user.getYukonUser(), ConsumerInfoRole.ROLEID) != null);
	}
	
	public static boolean isResidentialCustomer(StarsYukonUser user) {
		return (AuthFuncs.checkRole(user.getYukonUser(), ResidentialCustomerRole.ROLEID) != null);
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
		else if (lite.getLiteType() == LiteTypes.ENERGY_COMPANY || lite.getLiteType() == LiteTypes.ENERGY_COMPANY) {
			msg = new DBChangeMsg(
				lite.getLiteID(),
				DBChangeMsg.CHANGE_ENERGY_COMPANY_DB,
				DBChangeMsg.CAT_ENERGY_COMPANY,
				DBChangeMsg.CAT_ENERGY_COMPANY,
				typeOfChange
				);
		}
		
		DefaultDatabaseCache.getInstance().handleDBChangeMessage( msg );
		
		com.cannontech.message.util.ClientConnection conn = SOAPServer.getInstance().getClientConnection();
		if (conn == null) {
			CTILogger.error( "Cannot get dispatch client connection" );
			return;
		}
    	conn.write( msg );
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
	
	public static String getFormattedName(LiteContact liteContact) {
		StringBuffer name = new StringBuffer();
		
		String firstName = forceNotNone( liteContact.getContFirstName() ).trim();
		if (firstName.length() > 0)
			name.append( firstName );
		
		String lastName = forceNotNone( liteContact.getContLastName() ).trim();
		if (lastName.length() > 0)
			name.append(" ").append( lastName );
			
		if (name.length() == 0) name.append("(none)");
		return name.toString();
	}
	
	public static String getFormattedAddress(LiteAddress liteAddr) {
		StringBuffer addr = new StringBuffer();
    	
		String locationAddr1 = forceNotNone( liteAddr.getLocationAddress1() ).trim();
		if (locationAddr1.length() > 0)
			addr.append( locationAddr1 ).append( "<br>" );
			
		String locationAddr2 = forceNotNone( liteAddr.getLocationAddress2() ).trim();
		if (locationAddr2.length() > 0)
			addr.append( locationAddr2 ).append( "<br>" );
			
		String cityName = forceNotNone( liteAddr.getCityName() ).trim();
		if (cityName.length() > 0)
			addr.append( cityName ).append( ", " );
		
		String stateCode = forceNotNone( liteAddr.getStateCode() ).trim();
		if (stateCode.length() > 0) {
			addr.append( stateCode );
			String zipCode = forceNotNone( liteAddr.getZipCode() ).trim();
			if (zipCode.length() > 0)
				addr.append(" ").append( zipCode );
		}
    	
		if (addr.length() == 0) addr.append("Address N/A");
		return addr.toString();
	}
	
	public static String getOneLineAddress(LiteAddress liteAddr) {
		StringBuffer addr = new StringBuffer();
		
		String locationAddr1 = forceNotNone( liteAddr.getLocationAddress1() ).trim();
		if (locationAddr1.length() > 0)
			addr.append( locationAddr1 );
			
		String locationAddr2 = forceNotNone( liteAddr.getLocationAddress2() ).trim();
		if (locationAddr2.length() > 0) {
			if (addr.length() > 0) addr.append(", ");
			addr.append( locationAddr2 );
		}
		
		String cityName = forceNotNone( liteAddr.getCityName() ).trim();
		if (cityName.length() > 0) {
			if (addr.length() > 0) addr.append(", ");
			addr.append( cityName );
		}
		
		String stateCode = forceNotNone( liteAddr.getStateCode() ).trim();
		if (stateCode.length() > 0) {
			if (addr.length() > 0) addr.append(", ");
			addr.append( stateCode );
			
			String zipCode = forceNotNone( liteAddr.getZipCode() ).trim();
			if (zipCode.length() > 0)
				addr.append(" ").append( zipCode );
		}
		
		if (addr.length() == 0) addr.append("Address N/A");
		return addr.toString();
	}

}
