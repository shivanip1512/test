package com.cannontech.messaging.util;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.server.ServerRequestMessage;
import com.cannontech.messaging.message.server.ServerResponseMessage;

public class ServerRequestHelper {
    
    private ServerRequestHelper() {};
    
    /**
     * @param conn the ClientConnection to send the request on
     * @param msg the message to embed in the ServerRequestMsg
     * @param timeoutMilliSeconds
     * @return payload object from response
     * @throws BadServerResponseException 
     */
    public static Object makeServerRequest(ClientConnection conn, 
                                           BaseMessage msg, 
                                           int timeoutMilliSeconds) throws BadServerResponseException {
        Object result = null;
        
        ServerRequest req = new ServerRequestImpl();
        ServerResponseMessage responseMsg = req.makeServerRequest(conn, msg, timeoutMilliSeconds);// could block up to 60 seconds
        if(responseMsg.getStatus() == ServerResponseMessage.STATUS_OK) {
            // good response
            result = responseMsg.getPayload();
        }

        if (result == null) {
            throw new BadServerResponseException("Invalid response received from " + conn.getName());
        }
        return result;
    }
    
    public static void makeServerResponse(ClientConnection conn, 
                                          ServerRequestMessage req,
                                          Object payload) {
        ServerResponseMessage responseMsg = req.createResponseMsg();
        responseMsg.setPayload(payload);
        responseMsg.setStatus(ServerResponseMessage.STATUS_OK);
        conn.write(responseMsg);
    }
    
    
    /**
     * Convenience method that calls makeServerRequest(conn, msg, 10000).
     * @param conn
     * @param msg
     * @return
     * @throws BadServerResponseException 
     */
    public static Object makeServerRequest(ClientConnection conn, 
                                           BaseMessage msg) throws BadServerResponseException {
        return makeServerRequest(conn, msg, 10000);
    }

    
    public static boolean isPayloadInstanceOf(BaseMessage msg, Class clazz) {
        if (msg instanceof ServerRequestMessage) {
            ServerRequestMessage reqMsg = (ServerRequestMessage) msg;
            Object payload = reqMsg.getPayload();
            return clazz.isInstance(payload);
        }
        return false;
    }
    
}
