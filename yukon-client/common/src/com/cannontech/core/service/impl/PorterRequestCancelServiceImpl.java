package com.cannontech.core.service.impl;

import org.apache.commons.lang.math.RandomUtils;
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
    
    private BasicServerConnection porterConnection;
    private Logger log = YukonLogManager.getLogger(PorterRequestCancelServiceImpl.class);

    private final String commandString = "system message request cancel";
    private final long timeout = 30000;
    
    public long cancelRequests(int groupMessageId) {
        
        final int randomId = RandomUtils.nextInt();
        
        Request req = new Request();
        req.setCommandString(commandString);
        req.setUserMessageID(randomId);
        req.setGroupMessageID(groupMessageId);
        
        ServerRequestBlocker<RequestCancel> blocker = 
            new ServerRequestBlocker<RequestCancel>(porterConnection, RequestCancel.class, new Checker<RequestCancel>() {
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
