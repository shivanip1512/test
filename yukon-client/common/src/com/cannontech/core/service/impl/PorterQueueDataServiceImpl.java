package com.cannontech.core.service.impl;

import java.util.Random;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.util.Checker;
import com.cannontech.core.service.PorterQueueDataService;
import com.cannontech.message.porter.message.QueueData;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.util.ServerRequestBlocker;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.yukon.BasicServerConnection;

public class PorterQueueDataServiceImpl implements PorterQueueDataService {
    private final static Random random = new Random();

    private BasicServerConnection porterConnection;

    private final String commandString = "system message request count";
    private final long timeout = 30000;

    @Override
    public long getMessageCountForRequest(long requestId) {
        Request req = new Request();
        final int randomId = random.nextInt();
        req.setCommandString(commandString);
        req.setUserMessageID(randomId);
        req.setGroupMessageID(requestId);
        
        ServerRequestBlocker<QueueData> blocker = 
            new ServerRequestBlocker<QueueData>(porterConnection, QueueData.class, new Checker<QueueData>() {
            @Override
            public boolean check(QueueData msg) {
                return msg.getUserMessageId() == randomId;
            }
        });
        
        try {
            QueueData response = blocker.execute(req, timeout);
            return response.getRequestIdCount();
        } catch (TimeoutException e) {
            throw new RuntimeException("Unable to get message count for request", e);
        }
    }

    @Required
    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }
}
