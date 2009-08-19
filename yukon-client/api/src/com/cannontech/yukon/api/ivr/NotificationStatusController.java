package com.cannontech.yukon.api.ivr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.yukon.INotifConnection;

@Controller
@RequestMapping("/notification/status/*")
public class NotificationStatusController {
    private INotifConnection notifClientConnection;

    @RequestMapping(method = RequestMethod.POST)
    public void callComplete(String callToken, boolean success) {
        notifClientConnection.sendConfirmation(callToken, success);
    }
    
    @Autowired
    public void setNotifClientConnection(INotifConnection notifClientConnection) {
        this.notifClientConnection = notifClientConnection;
    }
}