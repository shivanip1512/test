package com.cannontech.core.service.impl;

import org.apache.commons.lang.math.RandomUtils;

import com.cannontech.common.util.Checker;
import com.cannontech.core.service.PorterQueueDataService;
import com.cannontech.messaging.message.porter.QueueDataMessage;
import com.cannontech.messaging.message.porter.RequestMessage;
import com.cannontech.messaging.util.ServerRequestBlocker;
import com.cannontech.messaging.util.TimeoutException;
import com.cannontech.yukon.BasicServerConnection;

public class PorterQueueDataServiceImpl implements PorterQueueDataService {
    private BasicServerConnection porterConnection;
    
    private final String commandString = "system message request count";
    private final long timeout = 30000;

    public long getMessageCountForRequest(long requestId) {
        RequestMessage req = new RequestMessage();
        final int randomId = RandomUtils.nextInt();
        req.setCommandString(commandString);
        req.setUserMessageId(randomId);
        req.setGroupMessageId(requestId);
        
        ServerRequestBlocker<QueueDataMessage> blocker = 
            new ServerRequestBlocker<QueueDataMessage>(porterConnection, QueueDataMessage.class, new Checker<QueueDataMessage>() {
            public boolean check(QueueDataMessage msg) {
                return msg.getUserMessageId() == randomId;
            }
        });
        
        try {
            QueueDataMessage response = blocker.execute(req, timeout);
            return response.getRequestIdCount();
        } catch (TimeoutException e) {
            throw new RuntimeException("Unable to get message count for request", e);
        }
    }

    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }

}
