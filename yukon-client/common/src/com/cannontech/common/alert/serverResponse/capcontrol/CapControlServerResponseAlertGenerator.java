package com.cannontech.common.alert.serverResponse.capcontrol;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.message.capcontrol.CapControlResponseType;
import com.cannontech.message.capcontrol.CapControlServerResponse;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.roles.application.WebClientRole;
import com.cannontech.user.checker.RolePropertyUserCheckerFactory;
import com.cannontech.user.checker.UserChecker;
import com.cannontech.yukon.IServerConnection;

public class CapControlServerResponseAlertGenerator implements MessageListener {
    private AlertService alertService;
    private RolePropertyUserCheckerFactory userCheckerFactory;
    private IServerConnection defCapControlConn;
    
    
    public void initialize() {
        defCapControlConn.addMessageListener( this );  
    }

    @Override
    public void messageReceived(MessageEvent e) {
        Message in = e.getMessage();
        if (in instanceof CapControlServerResponse) {
            CapControlServerResponse response = (CapControlServerResponse)in;
            if(response.getResponseType() == CapControlResponseType.CommandRefused) {
                ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.common.alerts.serverResponse.CapControlServerResponseAlert");
                resolvableTemplate.addData("responseText", response.getResponse());
                CapControlServerResonseAlert alert = new CapControlServerResonseAlert(in.getTimeStamp(), resolvableTemplate);
                UserChecker userChecker = userCheckerFactory.createPropertyChecker(WebClientRole.VIEW_ALARMS_AS_ALERTS);
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
    
    @Required
    public void setUserCheckerFactory(RolePropertyUserCheckerFactory userCheckerFactory) {
        this.userCheckerFactory = userCheckerFactory;
    }
}
