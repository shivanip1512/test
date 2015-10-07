/*
 * Created on Mar 8, 2004
 */
package com.cannontech.message.server;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.message.util.Message;

/**
 * CtiRequestMsg is used to send a request to a server
 * application.  
 * A client submitting a request msg should keep track
 * of the id used in order to determine whether any subsequent
 * requests are intended for them.  
 * @author aaron
 */
public class ServerRequestMsg extends Message {
	
	private int _id;
	private Object _payload; 
	
	/**
	 * @return
	 */
	public int getId() {
		return _id;
	}

	/**
	 * @return
	 */
	public Object getPayload() {
		return _payload;
	}

	/**
	 * @param i
	 */
	public void setId(int i) {
		_id = i;
	}
	
	@Override
	public String toString() {
	    ToStringCreator toStringCreator = new ToStringCreator(this);
	    toStringCreator.append("id", _id);
	    toStringCreator.append("payload", _payload);
	    return toStringCreator.toString();
	}

	/**
	 * @param object
	 */
	public void setPayload(Object object) {
		_payload = object;
	}
    
    public ServerResponseMsg createResponseMsg(int status, String message) {
        ServerResponseMsg responseMsg = new ServerResponseMsg(getId(), status, message);
        return responseMsg;
    }
}
