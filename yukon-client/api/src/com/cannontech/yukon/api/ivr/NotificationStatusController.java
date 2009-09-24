package com.cannontech.yukon.api.ivr;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.message.notif.NotifCallEvent;
import com.cannontech.yukon.INotifConnection;

@Controller
@RequestMapping("/notification/status/*")
public class NotificationStatusController {
    private Logger log = YukonLogManager.getLogger(NotificationStatusController.class);
    private INotifConnection notifClientConnection;

    /**
     * This method should be invoked after the call was dialed.
     * @param callToken
     * @param success
     */
    @RequestMapping(method = RequestMethod.POST)
    public void callComplete(String callToken, boolean success) {
        notifClientConnection.sendCallEvent(callToken, success ? NotifCallEvent.CONFIRMED : NotifCallEvent.UNCONFIRMED);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public void callDisconnect(String yukonCallToken, Writer response) throws IOException {
        Validate.notNull(yukonCallToken);
        notifClientConnection.sendCallEvent(yukonCallToken, NotifCallEvent.DISCONNECT);
        response.write("exitEvent\n");
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public void callFailed(String yukonCallToken, Writer response) throws IOException {
        Validate.notNull(yukonCallToken);
        notifClientConnection.sendCallEvent(yukonCallToken, NotifCallEvent.FAILURE);
        response.write("exitEvent\n");
    }
    
    @Autowired
    public void setNotifClientConnection(INotifConnection notifClientConnection) {
        this.notifClientConnection = notifClientConnection;
    }
}