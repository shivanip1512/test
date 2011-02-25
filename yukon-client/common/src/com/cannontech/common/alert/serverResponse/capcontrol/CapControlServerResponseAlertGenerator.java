package com.cannontech.common.alert.serverResponse.capcontrol;

import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.capcontrol.CapControlResponseType;
import com.cannontech.message.capcontrol.CapControlServerResponse;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.user.checker.SingleUserChecker;
import com.cannontech.user.checker.UserChecker;
import com.cannontech.yukon.IServerConnection;

public class CapControlServerResponseAlertGenerator implements MessageListener {
    private AlertService alertService;
    private IServerConnection defCapControlConn;
    private YukonUserDao yukonUserDao;
    
    
    public void initialize() {
        defCapControlConn.addMessageListener( this );  
    }

    @Override
    public void messageReceived(MessageEvent e) {
        Message in = e.getMessage();
        String user = in.getUserName();
        if (in instanceof CapControlServerResponse) {
            CapControlServerResponse response = (CapControlServerResponse)in;
            if (response.getResponseType() == CapControlResponseType.CommandRefused) {
                ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.common.alerts.serverResponse");
                resolvableTemplate.addData("responseText", response.getResponse());
                CapControlServerResonseAlert alert = new CapControlServerResonseAlert(in.getTimeStamp(), resolvableTemplate);
                LiteYukonUser liteUser = yukonUserDao.findUserByUsername(user);
                UserChecker userChecker = new SingleUserChecker(liteUser);
                alert.setUserChecker(userChecker);
                
                alertService.add(alert);
            }
        }
    }
    
    public void setAlertService(AlertService alertService) {
        this.alertService = alertService;
    }
    
    public void setDefCapControlConn(IServerConnection defCapControlConn) {
        this.defCapControlConn = defCapControlConn;
    }
    
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
    
}
