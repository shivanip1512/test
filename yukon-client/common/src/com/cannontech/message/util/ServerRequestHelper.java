package com.cannontech.message.util;

import com.cannontech.message.server.ServerRequestMsg;
import com.cannontech.message.server.ServerResponseMsg;

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
                                           Message msg, 
                                           int timeoutMilliSeconds) throws BadServerResponseException {
        Object result = null;
        
        ServerRequest req = new ServerRequestImpl();
        ServerResponseMsg responseMsg = req.makeServerRequest(conn, msg, timeoutMilliSeconds);// could block up to 60 seconds
        if(responseMsg.getStatus() == ServerResponseMsg.STATUS_OK) {
            // good response
            result = responseMsg.getPayload();
        }

        if (result == null) {
            throw new BadServerResponseException();
        }
        return result;
    }
    
    public static void makeServerResponse(ClientConnection conn, 
                                          ServerRequestMsg req,
                                          Object payload) {
        ServerResponseMsg responseMsg = req.createResponseMsg();
        responseMsg.setPayload(payload);
        responseMsg.setStatus(ServerResponseMsg.STATUS_OK);
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
                                           Message msg) throws BadServerResponseException {
        return makeServerRequest(conn, msg, 10000);
    }

    
    public static boolean isPayloadInstanceOf(Message msg, Class clazz) {
        if (msg instanceof ServerRequestMsg) {
            ServerRequestMsg reqMsg = (ServerRequestMsg) msg;
            Object payload = reqMsg.getPayload();
            return clazz.isInstance(payload);
        }
        return false;
    }
    
}
