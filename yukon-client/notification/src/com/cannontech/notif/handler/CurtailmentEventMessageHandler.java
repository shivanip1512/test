package com.cannontech.notif.handler;

import com.cannontech.cc.dao.*;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.message.notif.CurtailmentEventDeleteMsg;
import com.cannontech.message.notif.CurtailmentEventMsg;
import com.cannontech.message.server.ServerRequestMsg;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.*;
import com.cannontech.notif.server.NotifServerConnection;

public class CurtailmentEventMessageHandler extends MessageHandler {
    private CurtailmentEventScheduler _scheduler;
    private CurtailmentEventDao curtailmentEventDao;
    
    public CurtailmentEventMessageHandler(CurtailmentEventScheduler scheduler) {
        _scheduler = scheduler;
    }

    public boolean handleMessage(NotifServerConnection connection, Message msg_) {
        if (msg_ instanceof CurtailmentEventMsg) {
            CurtailmentEventMsg msg = (CurtailmentEventMsg) msg_;
            _scheduler.handleCurtailmentMessage(msg);
            return true;
        } else if (ServerRequestHelper.isPayloadInstanceOf(msg_, CurtailmentEventDeleteMsg.class)) {
            ServerRequestMsg reqMsg = (ServerRequestMsg) msg_;
            CurtailmentEventDeleteMsg reqPayload = (CurtailmentEventDeleteMsg) reqMsg.getPayload();
            Integer curtailmentEventId = reqPayload.curtailmentEventId;
            CurtailmentEvent event = curtailmentEventDao.getForId(curtailmentEventId);
            boolean success = _scheduler.deleteEventNotification(event);
            CollectableBoolean respPayload = new CollectableBoolean(success);
            ServerResponseMsg responseMsg = reqMsg.createResponseMsg();
            responseMsg.setPayload(respPayload);
            responseMsg.setStatus(ServerResponseMsg.STATUS_OK);
            connection.write(responseMsg);
            return true;
        } else {
            return false;
        }
    }
    
    public void setCurtailmentEventDao(CurtailmentEventDao curtailmentEventDao) {
        this.curtailmentEventDao = curtailmentEventDao;
    }

}
