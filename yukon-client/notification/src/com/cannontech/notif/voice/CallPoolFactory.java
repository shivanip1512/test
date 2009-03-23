package com.cannontech.notif.voice;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;

public class CallPoolFactory {
    private RolePropertyDao rolePropertyDao;
    private YukonUserDao yukonUserDao;
    private UrlDialerFactory urlDialerFactory;
    
    public CallPool createCallPool(LiteEnergyCompany energyCompany) {
        CallPool callPool = new CallPool();
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(energyCompany.getUserID());

        int callTimeoutSeconds = rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.CALL_RESPONSE_TIMEOUT, null);
        callPool.setCallTimeoutSeconds(callTimeoutSeconds);
        
        int numberOfChannels = rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.NUMBER_OF_CHANNELS, user);
        callPool.setNumberOfChannels(numberOfChannels);

        // we only have one factory, so hard code it
        DialerFactory dialerFactory = urlDialerFactory;
        
        Dialer dialer = dialerFactory.createDialer(energyCompany);
        callPool.setDialer(dialer);
        
        callPool.initialize();
        
        return callPool;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
    
    @Autowired
    public void setUrlDialerFactory(UrlDialerFactory urlDialerFactory) {
        this.urlDialerFactory = urlDialerFactory;
    }

}
