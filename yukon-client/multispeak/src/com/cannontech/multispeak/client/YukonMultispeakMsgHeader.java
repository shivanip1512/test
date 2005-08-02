/*
 * Created on Jul 12, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.client;

import java.math.BigInteger;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.version.VersionTools;
import com.cannontech.multispeak.MessageHeaderCSUnits;
import com.cannontech.multispeak.MultiSpeakMsgHeader;

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
//		setAppName("Yukon Multispeak");
		setAppName("Cannon Test App");
		setAppVersion(VersionTools.getYUKON_VERSION());
//		setCompany("Cannon Technologies, Inc.");
		setCompany("Cannon Test");
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
	 */
	public YukonMultispeakMsgHeader(
		String version,
		String userID,
		String pwd,
		String appName,
		String appVersion,
		String company,
		MessageHeaderCSUnits CSUnits,
		String coordinateSystem,
		String datum,
		String sessionID,
		String previousSessionID,
		BigInteger objectsRemaining) {
		super(
			version,
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
			objectsRemaining);
		// TODO Auto-generated constructor stub
	}

}
