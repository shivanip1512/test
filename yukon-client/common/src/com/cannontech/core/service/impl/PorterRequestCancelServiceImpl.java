package com.cannontech.core.service.impl;

import java.util.Random;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.Checker;
import com.cannontech.core.service.PorterRequestCancelService;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.RequestCancel;
import com.cannontech.message.util.ServerRequestBlocker;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.yukon.BasicServerConnection;

public class PorterRequestCancelServiceImpl implements PorterRequestCancelService {
    private final static Logger log = YukonLogManager.getLogger(PorterRequestCancelServiceImpl.class);
    private final static Random random = new Random();

    private BasicServerConnection porterConnection;

    private final static String commandString = "system message request cancel";
    private final static long timeout = 60000;

    @Override
    public long cancelRequests(int groupMessageId, int priority) {
        final int randomId = random.nextInt();

        Request req = new Request();
        req.setCommandString(commandString);
        req.setUserMessageID(randomId);
        req.setGroupMessageID(groupMessageId);
        req.setPriority(priority);
        
        ServerRequestBlocker<RequestCancel> blocker = 
            new ServerRequestBlocker<RequestCancel>(porterConnection, RequestCancel.class, new Checker<RequestCancel>() {
            @Override
            public boolean check(RequestCancel msg) {
                return msg.getUserMessageId() == randomId;
            }
        });
        
        try {
            RequestCancel response = blocker.execute(req, timeout);
            long itemsCanceled = response.getRequestIdCount();
            
            log.debug("Canceled " + itemsCanceled + " commands with groupMessageId " + groupMessageId);
            return itemsCanceled;
            
        } catch (TimeoutException e) {
            throw new RuntimeException("Unable to cancel commands due to timeout.", e);
        }
    }

    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }
}
