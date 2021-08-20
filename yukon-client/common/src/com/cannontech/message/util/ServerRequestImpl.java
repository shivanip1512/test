/*
 * Created on Apr 26, 2004
 */
package com.cannontech.message.util;

import java.util.Random;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.message.server.ServerRequestMsg;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.yukon.IServerConnection;

/**
 * ServerRequestImpl is used to make a synchronous request to a Yukon server.
 * When execute is called, ServerRequestImpl will send the given message to the server
 * using the given connection and wait until a response for the message is received.
 * 
 * If a response is received it will be returned.  Otherwise after a timeout number
 * of milliseconds have passed an exception will be thrown.
 * 
 * Example:
 * 
 * try { 
 *  LMManualControlRequest lmReq = new LMManualControlRequest();
 *  ...
 * 	ServerRequestImpl req = ServerRequestImpl.makeServerRequest(conn, lmReq);
 * 	ServerResponseMsg responseMsg = req.execute(60000); // could block up to 60 seconds
 *  if(responseMsg.getStatus() == ServerResponseMessage.OK) {
 *  	// good response
 *  }
 *  else { 
 * 	    // some type of error occurred
 *  }
 * 
 *  LMManualControlResponse lmResp = (LMManualControlResponse) responseMsg.getPayload();
 *  if(lmResp != null) {
 *    //do something interesting.
 *  }
 *  
 * }
 * catch(Exception e) {
 * 	// didn't get a response!
 * }
 * @author aaron
 * TODO: Fix exception handling
 */
public class ServerRequestImpl implements ServerRequest 
{
    private Logger log = YukonLogManager.getLogger(ServerRequestImpl.class);
    
	private static int MIN_RESERVED_ID = 0;
    private static int MAX_RESERVED_ID = 10;
	
	// used to generate a request id
	private static int _currentRequestID = Integer.MIN_VALUE;
	private static Random randomGen = new Random(System.currentTimeMillis());

    /**
     * InnerServerRequest is stateful and should only be used for a single request.
     * @author alauinger
     *
     */
    private class InnerServerRequest implements MessageListener {
        private IServerConnection _connection;
        private ServerRequestMsg _requestMsg;  
        private ServerResponseMsg _responseMsg;        
        InnerServerRequest(IServerConnection conn, ServerRequestMsg reqMsg) {
            _connection = conn;
            _requestMsg = reqMsg;
        }
 
        /**
         * Returns a ServerResponseMsg that matches the request.
         * If no matching response is received a Timeout response will be returned 
         * after the given timeout number of milliseconds.
         * @param timeout
         * @return
         * @throws Exception
         */
        private synchronized ServerResponseMsg execute(long timeout) {
            try { 
                //Add this as a listener so we can look for a response
                _connection.addMessageListener(this);
                _connection.write(_requestMsg);
                if (log.isDebugEnabled()) {
                    log.debug("Server Request execute; request=" + _requestMsg + ", this=" + this);
                }
                wait(timeout);
            }
            catch(InterruptedException ie) {
                log.warn("Server request wait was interrupted.", ie);
            }
            finally {
                //Make sure to remove us or else there will be a leak!
                _connection.removeMessageListener(this);
            }
            
            // Did we get a response that matched our request id?
            if(_responseMsg == null) {
                _responseMsg = ServerResponseMsg.createTimeoutResp();
                log.info("Server response was a timeout");
                //throw new TimeoutException("Timed out waiting for response message with id: " + _requestID);
            }
            
            log.debug("Server Request execute complete");
            return _responseMsg;
        }
        
        /* (non-Javadoc)
         * @see com.cannontech.message.util.ServerRequest#messageReceived(com.cannontech.message.util.MessageEvent)
         */
        @Override
        public synchronized void messageReceived(MessageEvent e) {
            Message msg = e.getMessage();
            if(msg instanceof ServerResponseMsg) {
                ServerResponseMsg responseMsg = (ServerResponseMsg) msg;
                if (log.isDebugEnabled()) {
                    log.debug("Received response; response=" + responseMsg + ", this=" + this);
                }
                if(responseMsg.getId() == _requestMsg.getId() ) {
                    _responseMsg = responseMsg;
                    log.debug("Received matching response");
                    notifyAll(); //score! we found matching response, let the blocked thread know
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Received unknown response; response=" + msg + ", this=" + this);
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see com.cannontech.message.util.ServerRequest#makeServerRequest(com.cannontech.yukon.IServerConnection, com.cannontech.message.util.Message)
     */
    @Override
    public ServerResponseMsg makeServerRequest(IServerConnection conn, Message msg) {
        return makeServerRequest(conn,msg,DEFAULT_TIMEOUT);
    }
    
	/* (non-Javadoc)
     * @see com.cannontech.message.util.ServerRequest#makeServerRequest(com.cannontech.yukon.IServerConnection, com.cannontech.message.util.Message, long)
     */
    @Override
    public ServerResponseMsg makeServerRequest(IServerConnection conn, Message msg, long timeout) {
        int reqId = nextClientMessageID();
        ServerRequestMsg reqMsg = new ServerRequestMsg();
        reqMsg.setPayload(msg);
        reqMsg.setId(reqId);
        
        InnerServerRequest innerReq = new InnerServerRequest(conn, reqMsg);
        
        return innerReq.execute(timeout);
	}
	
   public ServerRequestImpl() {
   } 
	
	/**
	 * Generate the next client message id
	 * @return
	 */
	private static synchronized int nextClientMessageID()
    {
	    do {
          _currentRequestID = randomGen.nextInt();  
        }
        while(_currentRequestID >= MIN_RESERVED_ID && 
              _currentRequestID <= MAX_RESERVED_ID);
     
        return _currentRequestID;
	}
}
