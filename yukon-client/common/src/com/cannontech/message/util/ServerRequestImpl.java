/*
 * Created on Apr 26, 2004
 */
package com.cannontech.message.util;

import java.util.Random;

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
 * 	    // some type of error occured
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
public class ServerRequestImpl implements MessageListener, ServerRequest 
{
	private static int MIN_RESERVED_ID = 0;
    private static int MAX_RESERVED_ID = 10;
	
	// used to generate a request id
	private static int _currentRequestID = Integer.MIN_VALUE;

	static { 
		Random r = new Random(System.currentTimeMillis());
        
        //values MIN_RESERVED_ID to MAX_RESERVED_ID inclusive are reserved
        while( _currentRequestID >= MIN_RESERVED_ID && _currentRequestID <= MAX_RESERVED_ID )
            _currentRequestID = r.nextInt();
	}

	private IServerConnection _connection;
	private ServerRequestMsg _requestMsg;
	private ServerResponseMsg _responseMsg;
	private int _requestID;

	
    /* (non-Javadoc)
     * @see com.cannontech.message.util.ServerRequest#makeServerRequest(com.cannontech.yukon.IServerConnection, com.cannontech.message.util.Message)
     */
    public ServerResponseMsg makeServerRequest(IServerConnection conn, Message msg) {
        return makeServerRequest(conn,msg,DEFAULT_TIMEOUT);
    }
    
	/* (non-Javadoc)
     * @see com.cannontech.message.util.ServerRequest#makeServerRequest(com.cannontech.yukon.IServerConnection, com.cannontech.message.util.Message, long)
     */
    public ServerResponseMsg makeServerRequest(IServerConnection conn, Message msg, long timeout) {
        _connection = conn;        
        _requestMsg = new ServerRequestMsg();
        _requestMsg.setPayload(msg);
        
        _requestID = nextClientMessageID();
        _requestMsg.setId(_requestID);
        
        return execute(timeout);
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
			wait(timeout);
		}
        catch(InterruptedException ie) {
        }
		finally {
			//Make sure to remove us or else there will be a leak!
			_connection.removeMessageListener(this);
		}
		
		// Did we get a response that matched our request id?
		if(_responseMsg == null) {
            _responseMsg = ServerResponseMsg.createTimeoutResp();
			//throw new TimeoutException("Timed out waiting for response message with id: " + _requestID);
		}
		
		return _responseMsg;
	}

   public ServerRequestImpl() {
   } 
	
	/* (non-Javadoc)
     * @see com.cannontech.message.util.ServerRequest#messageReceived(com.cannontech.message.util.MessageEvent)
     */
	public synchronized void messageReceived(MessageEvent e) {
		Message msg = e.getMessage();
		if(msg instanceof ServerResponseMsg) {
			ServerResponseMsg responseMsg = (ServerResponseMsg) msg;
			if(responseMsg.getId() == _requestID) {
				_responseMsg = responseMsg;
				notify(); //score! we found matching response, let the blocked thread know
			}
		}
	}
	
	/**
	 * Generate the next client message id
	 * @return
	 */
	private static synchronized int nextClientMessageID()
    {
        _currentRequestID +=
            (_currentRequestID >= MIN_RESERVED_ID && _currentRequestID <= MAX_RESERVED_ID
                ? MAX_RESERVED_ID + 1
                : 1 );
        
        return _currentRequestID;
	}
}
