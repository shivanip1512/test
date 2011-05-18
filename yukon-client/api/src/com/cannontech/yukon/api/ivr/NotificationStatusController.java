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
     */
    @RequestMapping(method = RequestMethod.POST)
    public void callComplete(String callToken, boolean success, Writer response) throws IOException {
        log.debug("received callComplete for " + callToken + " with success=" + success);
        try {
            Validate.notEmpty(callToken, "callToken must not be null");
            notifClientConnection.sendCallEvent(callToken, success ? NotifCallEvent.CONFIRMED : NotifCallEvent.UNCONFIRMED);
        } catch (Exception e) {
            response.write("failure: " + e.toString());
            log.warn("unable to handle callComplete", e);
            return;
        }
        response.write("success");
    }
    
    /**
     * This method should be invoked once the line is clear and a Yukon can initiate a new
     * call.
     */
    @RequestMapping(method = RequestMethod.POST)
    public void callEnd(String callToken, boolean failure, Writer response) throws IOException {
        log.debug("received callEnd for " + callToken + " with failure=" + failure);
        try {
            Validate.notEmpty(callToken, "callToken must not be null");
            notifClientConnection.sendCallEvent(callToken, failure ? NotifCallEvent.FAILURE : NotifCallEvent.DISCONNECT);
        } catch (Exception e) {
            response.write("failure: " + e.toString());
            log.warn("unable to handle callEnd", e);
            return;
        }
        response.write("success");
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public void callDisconnect(String yukonCallToken, Writer response) throws IOException {
        log.debug("received callComplete for " + yukonCallToken);
        Validate.notNull(yukonCallToken);
        notifClientConnection.sendCallEvent(yukonCallToken, NotifCallEvent.DISCONNECT);
        response.write("exitEvent\n");
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public void callFailed(String yukonCallToken, Writer response) throws IOException {
        log.debug("received callComplete for " + yukonCallToken);
        Validate.notNull(yukonCallToken);
        notifClientConnection.sendCallEvent(yukonCallToken, NotifCallEvent.FAILURE);
        response.write("exitEvent\n");
    }
    
    @Autowired
    public void setNotifClientConnection(INotifConnection notifClientConnection) {
        this.notifClientConnection = notifClientConnection;
    }
}