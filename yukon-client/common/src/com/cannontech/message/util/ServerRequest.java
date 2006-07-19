/*
 * Created on Apr 26, 2004
 */
package com.cannontech.message.util;

import java.util.Random;

import com.cannontech.message.server.ServerRequestMsg;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.yukon.IServerConnection;

/**
 * ServerRequest is used to make a synchronous request to a Yukon server.
 * When execute is called, ServerRequest will send the given message to the server
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
 * 	ServerRequest req = ServerRequest.makeServerRequest(conn, lmReq);
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
public class ServerRequest implements MessageListener 
{
	// wait this many millis by default for a response message
	public final static long DEFAULT_TIMEOUT = 30L*1000L;
    
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

	
	/**
	 * Factory method to create a ServerRequest
	 * @param conn - Connection to a Yukon server
	 * @param msg - Some type of message that represents a request
	 * @return
	 */
	public static ServerRequest makeServerRequest(IServerConnection conn, Message msg) {
		ServerRequestMsg requestMsg = new ServerRequestMsg();
		requestMsg.setPayload(msg);
		return new ServerRequest(conn, requestMsg);
	}

	/**
	 * Returns a ServerResponseMsg that matches the request.
	 * If no matching response is received a Timeout response will be returned
	 * ater DEFAULT_TIMEOUT.
     * 
	 * @return
	 * @throws Exception
	 */
	public synchronized ServerResponseMsg execute() {
		return execute(DEFAULT_TIMEOUT);
	}
	
	/**
	 * Returns a ServerResponseMsg that matches the request.
	 * If no matching response is received a Timeout response will be returned 
     * after the given timeout number of milliseconds.
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	public synchronized ServerResponseMsg execute(long timeout) {
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

	/**
	 * @param conn
	 * @param requestMsg
	 */		
	public ServerRequest(IServerConnection conn, ServerRequestMsg requestMsg) {
		_connection = conn;
		_requestMsg = requestMsg;
		
		_requestID = nextClientMessageID();
		_requestMsg.setId(_requestID);
	}
	
	/**
	 * Called by the connection.
	 * If we receive a response with the id we expect then notify the thread
	 * blocked in execute.
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
