/*
 * Created on May 7, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
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
import com.cannontech.roles.yukon.RadiusRole;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
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
		//	TODO We need to decide if checking for role existance (checkRole) defines a YukonGroup
		// having Radius logins, or whether we should check the role property RADIUS_SERVER_ADDRESS 
		// has some value other than '(none)'. SN / Jon
//		LiteYukonUser yukUser = YukonUserFuncs.getLiteYukonUser(UserUtils.USER_YUKON_ID);
//		if( AuthFuncs.getcheckRole(yukUser, RadiusRole.ROLEID) != null)
		RadiusClient rc;
		try
		{
			String radiusAddr = RoleFuncs.getGlobalPropertyValue(RadiusRole.RADIUS_SERVER_ADDRESS);
			int authPort = Integer.valueOf(RoleFuncs.getGlobalPropertyValue(RadiusRole.RADIUS_AUTH_PORT)).intValue();
			int acctPort = Integer.valueOf(RoleFuncs.getGlobalPropertyValue(RadiusRole.RADIUS_ACCT_PORT)).intValue();
			String secret = RoleFuncs.getGlobalPropertyValue(RadiusRole.RADIUS_SECRET_KEY);
			//TODO any other radius attributes we don't know about yet.
		
			rc = new RadiusClient(radiusAddr, authPort, acctPort, secret);
			if( basicAuthenticate(rc, username, password))
			{
				DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
				synchronized(cache) {
				Iterator i = cache.getAllYukonUsers().iterator();
				while(i.hasNext()) {
					LiteYukonUser u = (LiteYukonUser) i.next();
					if( CtiUtilities.isEnabled(u.getStatus()) &&
						u.getUsername().equals(username) ) {
						return u;  //success!
					   }
				}
				}			
			}
				
		}
		catch (RadiusException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvalidParameterException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			//TODO any other radius attributes we don't know about yet.
			rc = new RadiusClient(radiusAddr, authPort, acctPort, secret);
			return basicAuthenticate(rc, username, password);
		}
		catch (RadiusException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvalidParameterException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
					System.out.println("User " + userName + " authenticated");
					printAttributes(accessResponse);
					basicAccount(rc,userName);
					return true;
				case RadiusPacket.ACCESS_REJECT:
					System.out.println("User " + userName + " NOT authenticated");
					printAttributes(accessResponse);
					return false;
//				case RadiusPacket.ACCESS_CHALLENGE:
//					String reply = new String(accessResponse.getAttribute(RadiusAttributeValues.REPLY_MESSAGE).getValue());
//					System.out.println("User " + userName + " Challenged with " + reply);
//					break;
//				default:
//					System.out.println("Whoa, what kind of RadiusPacket is this " + accessResponse.getPacketType());
//					break;
			}
		}catch(InvalidParameterException ivpex)
		{
			System.out.println(ivpex.getMessage());
		}catch(RadiusException rex)
		{
			System.out.println(rex.getMessage());
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
		//TODO What exactly is the ACCT_SESSION_ID??? using username until we learn more!  SN
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
