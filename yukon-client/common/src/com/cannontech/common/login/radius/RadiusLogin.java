package com.cannontech.common.login.radius;

import net.sourceforge.jradiusclient.RadiusAttribute;
import net.sourceforge.jradiusclient.RadiusAttributeValues;
import net.sourceforge.jradiusclient.RadiusClient;
import net.sourceforge.jradiusclient.RadiusPacket;
import net.sourceforge.jradiusclient.exception.InvalidParameterException;
import net.sourceforge.jradiusclient.exception.RadiusException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.authentication.service.AuthenticationProvider;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

/**
 * @author snebben
 *
 * Handles Radius login and authentication
 *
 */
public class RadiusLogin implements AuthenticationProvider {
    private static Logger log = YukonLogManager.getLogger(RadiusLogin.class);
    @Autowired private GlobalSettingDao globalSettingDao;

	/**
	 * @param username
	 * @param password
	 * @return
	 */
    public boolean login(String username, String password) {
        // We need to decide if checking for role existance (checkRole) defines a YukonGroup
        // having Radius logins, or whether we should check the role property RADIUS_SERVER_ADDRESS 
        // has some value other than '(none)'. SN / Jon
        RadiusClient rc;
        try {
            String radiusAddr = globalSettingDao.getString(GlobalSettingType.SERVER_ADDRESS);
            int authPort = globalSettingDao.getInteger(GlobalSettingType.AUTH_PORT);
            int acctPort = globalSettingDao.getInteger(GlobalSettingType.ACCT_PORT);
            int authTimeout = globalSettingDao.getInteger(GlobalSettingType.AUTH_TIMEOUT) * 1000;
            String secret = globalSettingDao.getString(GlobalSettingType.SECRET_KEY);
            
            rc = new RadiusClient(radiusAddr, authPort, acctPort, secret, authTimeout );

            if( basicAuthenticate(rc, username, password)) {
                return true;			
            }

        }
        catch (RadiusException e) {
            log.error( "Radius Login Failed", e );
        }
        catch (InvalidParameterException e) {
            log.error( "Radius Login Failed", e );
        }
        return false;
    }
    
    public boolean login(LiteYukonUser user, String password) {
        return login(user.getUsername(), password);
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
			log.error( e.getMessage(), e );
		}
		catch (InvalidParameterException e)
		{
			log.error( e.getMessage(), e );
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
			RadiusPacket accessRequest = new RadiusPacket(RadiusPacket.ACCESS_REQUEST);
			accessRequest.setAttribute(new RadiusAttribute(RadiusAttributeValues.USER_NAME, userName.getBytes()));
			accessRequest.setAttribute(new RadiusAttribute(RadiusAttributeValues.USER_PASSWORD, userPass.getBytes()));
			
			RadiusPacket accessResponse = rc.authenticate(accessRequest);
			switch(accessResponse.getPacketType()){
				case RadiusPacket.ACCESS_ACCEPT:
					CTILogger.info("User " + userName + " authenticated");
					return true;
				case RadiusPacket.ACCESS_REJECT:
					CTILogger.info("User " + userName + " NOT authenticated");
					return false;
				case RadiusPacket.ACCESS_CHALLENGE:
					String reply = new String(accessResponse.getAttribute(RadiusAttributeValues.REPLY_MESSAGE).getValue());
					log.warn("User " + userName + " Challenged with " + reply);
					break;
				default:
					log.warn("Received an unknown RadiusPacket with the following type: " + accessResponse.getPacketType());
					break;
			}
		}
		catch(InvalidParameterException ivpex) {
			log.error("Radius basicAuthenticate failed for: " + userName, ivpex);
		}
		catch(RadiusException rex) {
			log.error("Radius basicAuthenticate failed for: " + userName, rex);
		}

		return false;
	}
}
