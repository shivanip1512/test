/*
 * Created on Jul 12, 2005 To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.client;

import com.cannontech.common.version.VersionTools;
import com.cannontech.msp.beans.v3.MessageHeaderCSUnits;
import com.cannontech.msp.beans.v3.MultiSpeakMsgHeader;


/**
 * @author stacey To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class YukonMultispeakMsgHeader extends MultiSpeakMsgHeader {
    /**
     * @param outUsername
     * @param outPassword
     */
    public YukonMultispeakMsgHeader(String outUsername, String outPassword, String version) {
        super();
        setVersion(version); // The Multispeak version?
        setUserID(outUsername);
        setPwd(outPassword);
        setAppName("Yukon");
        setAppVersion(VersionTools.getYUKON_VERSION());
        setCompany("Cannon");
        setCSUnits(MessageHeaderCSUnits.FEET);
    }
}
