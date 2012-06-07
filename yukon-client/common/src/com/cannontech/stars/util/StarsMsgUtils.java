/*
 * Created on Jan 3, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util;

import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.stars.xml.serialize.types.StarsLoginStatus;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class StarsMsgUtils {

    public static StarsLoginStatus getLoginStatus(LoginStatusEnum loginStatus) {
        if (LoginStatusEnum.ENABLED.equals(loginStatus)) {
            return StarsLoginStatus.ENABLED;
        } else if (LoginStatusEnum.DISABLED.equals(loginStatus)) {
            return StarsLoginStatus.DISABLED;
        } else {
            return null;
        }
    }
	
    public static LoginStatusEnum getUserStatus(StarsLoginStatus status) {
        if (status.getType() == StarsLoginStatus.ENABLED_TYPE) {
            return LoginStatusEnum.ENABLED;
        } else if (status.getType() == StarsLoginStatus.DISABLED_TYPE) {
            return LoginStatusEnum.DISABLED;
        } else {
            return null;
        }
    }
}
