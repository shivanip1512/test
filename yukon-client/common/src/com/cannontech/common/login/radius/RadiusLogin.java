package com.cannontech.common.login.radius;

import java.util.Iterator;

import net.sourceforge.jradiusclient.RadiusAttribute;
import net.sourceforge.jradiusclient.RadiusAttributeValues;
import net.sourceforge.jradiusclient.RadiusClient;
import net.sourceforge.jradiusclient.RadiusPacket;
import net.sourceforge.jradiusclient.exception.InvalidParameterException;
import net.sourceforge.jradiusclient.exception.RadiusException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.yukon.AuthenticationRole;

/**
 * @author snebben
 *
 * Handles Radius login and authentication
 *
 */
public class RadiusLogin
{
	/**
	 * @param username
	 * @param password
	 * @return
	 */
	public static LiteYukonUser login(String username, String password)
	{
		// We need to decide if checking for role existance (checkRole) defines a YukonGroup
		// having Radius logins, or whether we should check the role property RADIUS_SERVER_ADDRESS 
		// has some value other than '(none)'. SN / Jon
		RadiusClient rc;
		try
		{
			String radiusAddr = RoleFuncs.getGlobalPropertyValue(AuthenticationRole.SERVER_ADDRESS);
			int authPort = Integer.valueOf(RoleFuncs.getGlobalPropertyValue(AuthenticationRole.AUTH_PORT)).intValue();
			int acctPort = Integer.valueOf(RoleFuncs.getGlobalPropertyValue(AuthenticationRole.ACCT_PORT)).intValue();
			String secret = RoleFuncs.getGlobalPropertyValue(AuthenticationRole.SECRET_KEY);
			//any other radius attributes we don't know about yet.
		
			rc = new RadiusClient(radiusAddr, authPort, acctPort, secret);
			if( basicAuthenticate(rc, username, password))
			{
				DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
				synchronized(cache) {
				Iterator i = cache.getAllYukonUsers().iterator();
				while(i.hasNext()) {
					LiteYukonUser u = (LiteYukonUser) i.next();
					if( CtiUtilities.isEnabled(u.getStatus()) &&
						u.getUsername().equalsIgnoreCase(username) ) {
						return u;  //success!
					   }
				}
				}			
			}
				
		}
		catch (RadiusException e)
		{
			//Auto-generated catch block
			CTILogger.error( e.getMessage(), e );
		}
		catch (InvalidParameterException e)
		{
			//Auto-generated catch block
			CTILogger.error( e.getMessage(), e );
		}
		return null;
	}

	/**
	 *  TESTING FUNCTION!!!!
	 * @param username
	 * @param password
	 * @return
	 */
	public static boolean loginTest(String radiusAddr, int authPort, int acctPort, String secret, String username, String password)
	{
		RadiusClient rc;
		try
		{
			//any other radius attributes we don't know about yet.
			rc = new RadiusClient(radiusAddr, authPort, acctPort, secret);
			return basicAuthenticate(rc, username, password);
		}
		catch (RadiusException e)
		{
			//Auto-generated catch block
			CTILogger.error( e.getMessage(), e );
		}
		catch (InvalidParameterException e)
		{
			//Auto-generated catch block
			CTILogger.error( e.getMessage(), e );
		}
		return false;
	}

	/**
	 * @param rc
	 * @param userName
	 * @param userPass
	 */
	private static boolean basicAuthenticate(final RadiusClient rc, String userName, String userPass)
	{
		try
		{
			boolean attributes = false, continueTest = true;
			String authMethod = null;

			attributes = false;
			RadiusPacket accessRequest = new RadiusPacket(RadiusPacket.ACCESS_REQUEST);
			accessRequest.setAttribute(new RadiusAttribute(RadiusAttributeValues.USER_NAME, userName.getBytes()));
			accessRequest.setAttribute(new RadiusAttribute(RadiusAttributeValues.USER_PASSWORD, userPass.getBytes()));
			
			RadiusPacket accessResponse = rc.authenticate(accessRequest);
			switch(accessResponse.getPacketType()){
				case RadiusPacket.ACCESS_ACCEPT:
					CTILogger.info("User " + userName + " authenticated");
					//printAttributes(accessResponse);
					//basicAccount(rc,userName);
					return true;
				case RadiusPacket.ACCESS_REJECT:
					CTILogger.info("User " + userName + " NOT authenticated");
					//printAttributes(accessResponse);
					return false;
				case RadiusPacket.ACCESS_CHALLENGE:
					String reply = new String(accessResponse.getAttribute(RadiusAttributeValues.REPLY_MESSAGE).getValue());
					System.out.println("User " + userName + " Challenged with " + reply);
					break;
				default:
					System.out.println("Received an unknown RadiusPacket with the following type: " + accessResponse.getPacketType());
					break;
			}
		}
		catch(InvalidParameterException ivpex)
		{
			CTILogger.error( ivpex.getMessage(), ivpex );
		}
		catch(RadiusException rex)
		{
			CTILogger.error( rex.getMessage(), rex );
		}

		return false;
	}	
	/**
	 * @param rp
	 */
	private static void printAttributes(RadiusPacket rp)
	{
		Iterator attributes = rp.getAttributes().iterator();
		RadiusAttribute tempRa;
		CTILogger.debug("Response Packet Attributes");
		CTILogger.debug("\tType\tValue");
		while(attributes.hasNext())
		{
			tempRa = (RadiusAttribute)attributes.next();
			CTILogger.debug("\t" + tempRa.getType() + "\t" + new String(tempRa.getValue()));
		}
	}

	/**
	 * Opens the Radius accounting port to send/log Radius accounting events to
	 * the server. This is optional on most systems.
	 * 
	 * @param rc
	 * @param userName
	 * @throws InvalidParameterException
	 * @throws RadiusException
	 */
	private static boolean basicAccount(final RadiusClient rc, final String userName)throws InvalidParameterException, RadiusException
	{
		RadiusPacket accountRequest = new RadiusPacket(RadiusPacket.ACCOUNTING_REQUEST);
		accountRequest.setAttribute(new RadiusAttribute(RadiusAttributeValues.USER_NAME,userName.getBytes()));
		accountRequest.setAttribute(new RadiusAttribute(RadiusAttributeValues.ACCT_STATUS_TYPE,new byte[]{0, 0, 0, 1}));

		//What exactly is the ACCT_SESSION_ID??? using username until we learn more!  SN
		accountRequest.setAttribute(new RadiusAttribute(RadiusAttributeValues.ACCT_SESSION_ID,(userName).getBytes()));
		accountRequest.setAttribute(new RadiusAttribute(RadiusAttributeValues.SERVICE_TYPE,new byte[]{0, 0, 0, 1}));
		RadiusPacket accountResponse = rc.account(accountRequest);
		printAttributes(accountResponse);
		switch(accountResponse.getPacketType()){
			case RadiusPacket.ACCOUNTING_MESSAGE:
				CTILogger.debug("User " + userName + " got ACCOUNTING_MESSAGE response");
				return true;
			case RadiusPacket.ACCOUNTING_RESPONSE:
				CTILogger.debug("User " + userName + " got ACCOUNTING_RESPONSE response");
				return true;
			case RadiusPacket.ACCOUNTING_STATUS:
				CTILogger.debug("User " + userName + " got ACCOUNTING_STATUS response");
				return true;
			default:
				CTILogger.debug("User " + userName + " got invalid response " + accountResponse.getPacketType() );
				return false;
		}
		
	}	
}
