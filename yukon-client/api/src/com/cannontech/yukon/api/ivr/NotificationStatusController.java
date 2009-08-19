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

    /**
     * This method should be invoked after the call was dialed.
     * @param callToken
     * @param success
     */
    @RequestMapping(method = RequestMethod.POST)
    public void callComplete(String callToken, boolean success) {
        notifClientConnection.sendConfirmation(callToken, success);
    }
    
    /**
     * This method should be invoked if the call failed to dial.
     * @param callToken
     * @param success
     */
    @RequestMapping(method = RequestMethod.POST)
    public void callFailure(String callToken, boolean transientError) {
        notifClientConnection.sendFailure(callToken, transientError);
    }
    
    @Autowired
    public void setNotifClientConnection(INotifConnection notifClientConnection) {
        this.notifClientConnection = notifClientConnection;
    }
}