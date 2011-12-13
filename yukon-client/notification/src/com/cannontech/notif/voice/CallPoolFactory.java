package com.cannontech.notif.voice;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;

public class CallPoolFactory {
    private @Autowired RolePropertyDao rolePropertyDao;
    private @Autowired YukonUserDao yukonUserDao;
    private @Autowired UrlDialerFactory urlDialerFactory;
    
    public CallPool createCallPool(LiteEnergyCompany energyCompany) {
        CallPool callPool = new CallPool();
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(energyCompany.getUserID());

        int numberOfChannels = rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.NUMBER_OF_CHANNELS, user);
        callPool.setNumberOfChannels(numberOfChannels);

        // we only have one factory, so hard code it
        DialerFactory dialerFactory = urlDialerFactory;
        
        Dialer dialer = dialerFactory.createDialer(energyCompany);
        callPool.setDialer(dialer);
        
        callPool.initialize();
        
        return callPool;
    }

}
