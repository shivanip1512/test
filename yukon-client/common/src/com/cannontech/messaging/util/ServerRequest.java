package com.cannontech.messaging.util;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.server.ServerResponseMessage;
import com.cannontech.yukon.IServerConnection;

public interface ServerRequest {

    // wait this many millis by default for a response message
    public final static long DEFAULT_TIMEOUT = 30L * 1000L;

    /**
     * Executes a server request, if we don't hear back from the server
     * a server response will be generated with an error status
     * @param conn - Connection to a Yukon server
     * @param msg - Some type of message that represents a request
     * @return
     */
    public abstract ServerResponseMessage makeServerRequest(IServerConnection conn,
            BaseMessage msg);

    /**
     * Executes a server request, if we don't hear back from the server
     * a server response will be generated with an error status
     * @param conn - Connection to a Yukon server
     * @param msg - Some type of message that represents a request
     * @param timout - Timeout in millis
     * @return
     */
    public abstract ServerResponseMessage makeServerRequest(IServerConnection conn,
            BaseMessage msg, long timeout);

}