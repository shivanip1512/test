/*
 * Created on Apr 26, 2004
 */
package com.cannontech.messaging.util;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.Checker;
import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.porter.RequestMessage;
import com.cannontech.yukon.BasicServerConnection;

public class ServerRequestBlocker<T extends BaseMessage> implements MessageListener {
    private BasicServerConnection _connection;
    private T _responseMsg = null;
    private final Checker<T> checker;
    private final Class<T> responseType;  
    
    private Logger log = YukonLogManager.getLogger(ServerRequestBlocker.class);
    
    public ServerRequestBlocker(BasicServerConnection conn, Class<T> responseType, Checker<T> checker) {
        _connection = conn;
        this.responseType = responseType;
        this.checker = checker;
    }

    public synchronized T execute(RequestMessage request, long timeoutMilliSec) throws TimeoutException {
        try { 
            //Add this as a listener so we can look for a response
            _connection.addMessageListener(this);
            
            log.debug("Executing '" + request.getCommandString() 
                      + "', userMessageId " + request.getUserMessageId() 
                      + ", groupMessageId " + request.getGroupMessageId());
            _connection.write(request);
            wait(timeoutMilliSec);
        }
        catch(InterruptedException ie) {
        }
        finally {
            //Make sure to remove us or else there will be a leak!
            _connection.removeMessageListener(this);
        }

        // Did we get a response that matched our request id?
        if(_responseMsg == null) {
            throw new TimeoutException();
        }

        return _responseMsg;
    }

    @SuppressWarnings("unchecked")
    public synchronized void messageReceived(MessageEvent e) {
        BaseMessage msg = e.getMessage();
        if (responseType.isInstance(msg)) {
            // Java doesn't like the following, but it will make the calling code nicer.
            T t = (T) msg;
            if(checker.check(t)) {
                _responseMsg = t;
                notifyAll(); //score! we found matching response, let the blocked thread know
            }
        }
    }
}
