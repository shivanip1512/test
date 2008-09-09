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

    public long cancelRequests(int groupMessageId) {
        
        final int randomId = RandomUtils.nextInt();
        
        Request req = new Request();
        req.setCommandString("system message request cancel");
        req.setUserMessageID(randomId);
        req.setGroupMessageID(groupMessageId);
        
        ServerRequestBlocker<RequestCancel> blocker = 
            new ServerRequestBlocker<RequestCancel>(porterConnection, RequestCancel.class, new Checker<RequestCancel>() {
            public boolean check(RequestCancel msg) {
                return msg.getUserMessageId() == randomId;
            }
        });
        
        try {
            
            log.debug("Executing 'system message request cancel' for groupMessageId " + groupMessageId + ", user id " + randomId);
            RequestCancel response = blocker.execute(req, 30000);
            
            long itemsCanceled = response.getRequestIdCount();
            
            log.debug("Cancel command canceled " + itemsCanceled + " commands with groupMessageId " + groupMessageId + ", user id " + randomId);
            return itemsCanceled;
            
        } catch (TimeoutException e) {
            throw new RuntimeException("Unable to cancel commands due to timeout.", e);
        }
    }

    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }

}
