/*
 * Created on Jul 12, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.client;

import java.math.BigInteger;

import com.cannontech.common.version.VersionTools;
import com.cannontech.multispeak.deploy.service.MessageHeaderCSUnits;
import com.cannontech.multispeak.deploy.service.MultiSpeakMsgHeader;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class YukonMultispeakMsgHeader extends MultiSpeakMsgHeader {

    /**
	 * 
	 */
	public YukonMultispeakMsgHeader() {
		super();
		setVersion("3.0");	//The Multispeak version?
		setUserID("");		//TODO change to Multispeak webservice username?
		setPwd("");		//TODO change to Multispeak webservice password?
		setAppName("Yukon");
		setAppVersion(VersionTools.getYUKON_VERSION());
		setCompany("Cannon");
		setCSUnits(MessageHeaderCSUnits.feet);
	}

	/**
	 * @param outUsername
	 * @param outPassword
	 */
	public YukonMultispeakMsgHeader(String outUsername, String outPassword) {
		super();
		setVersion("3.0");	//The Multispeak version?
		setUserID(outUsername);
		setPwd(outPassword);
		setAppName("Yukon");
		setAppVersion(VersionTools.getYUKON_VERSION());
		setCompany("Cannon");
		setCSUnits(MessageHeaderCSUnits.feet);
	}

	/**
	 * @param version
	 * @param userID
	 * @param pwd
	 * @param appName
	 * @param appVersion
	 * @param company
	 * @param CSUnits
	 * @param coordinateSystem
	 * @param datum
	 * @param sessionID
	 * @param previousSessionID
	 * @param objectsRemaining
     * @param lastSent
	 */
    public YukonMultispeakMsgHeader(String version, String userID, String pwd, String appName, String appVersion, String company, MessageHeaderCSUnits CSUnits, String coordinateSystem, String datum, String sessionID, String previousSessionID, BigInteger objectsRemaining, String lastSent) {
        super(version,
              userID,
              pwd,
              appName,
              appVersion,
              company,
              CSUnits,
              coordinateSystem,
              datum,
              sessionID,
              previousSessionID,
              objectsRemaining,
              lastSent,
              null,
              null,
              null,
              null,
              null);
    }
}
