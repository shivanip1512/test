package com.cannontech.core.service.impl;

import org.apache.commons.lang.math.RandomUtils;

import com.cannontech.common.util.Checker;
import com.cannontech.core.service.PorterQueueDataService;
import com.cannontech.message.porter.message.QueueData;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.util.ServerRequestBlocker;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.yukon.BasicServerConnection;

public class PorterQueueDataServiceImpl implements PorterQueueDataService {
    private BasicServerConnection porterConnection;

    public long getMessageCountForRequest(long requestId) {
        Request req = new Request();
        final int randomId = RandomUtils.nextInt();
        req.setCommandString("system message request count");
        req.setUserMessageID(randomId);
        req.setOptionsField((int) requestId);
        
        ServerRequestBlocker<QueueData> blocker = 
            new ServerRequestBlocker<QueueData>(porterConnection, QueueData.class, new Checker<QueueData>() {
            public boolean check(QueueData msg) {
                return msg.getUserMessageId() == randomId;
            }
        });
        
        try {
            QueueData response = blocker.execute(req, 30000);
            return response.getRequestIdCount();
        } catch (TimeoutException e) {
            throw new RuntimeException("Unable to get message count for request", e);
        }
    }

    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }

}
