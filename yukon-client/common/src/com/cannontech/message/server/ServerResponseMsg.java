/*
 * Created on Mar 8, 2004
 */
package com.cannontech.message.server;

import com.cannontech.message.util.Message;

/**
 * CtiServerResponseMsg is used to respond back to a particular
 * client request with a status and a textual message as
 * well as a collectable payload.  That might be some updated
 * state info or whatever is relevant to the response to a request.
 * 
 * @author aaron
 */
public class ServerResponseMsg extends Message {
	//Possible values for status
	public static final int STATUS_OK = 0;
	public static final int STATUS_ERROR = 1;
	public static final int STATUS_UNINIT = 2;
	
	public static final String[] STATUS_STRS =
	{
		"Success",
		"Error",
		"Uninitialized"
	};


	private int _id;
	private int _status;
	private String _message;
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
	public String getMessage() {
		return _message;
	}

	/**
	 * @return
	 */
	public Object getPayload() {
		return _payload;
	}

	/**
	 * @return
	 */
	public int getStatus() {
		return _status;
	}

	/**
	 * @return
	 */
	public String getStatusStr() 
	{	
		return STATUS_STRS[getStatus()];
	}

	/**
	 * @param i
	 */
	public void setId(int i) {
		_id = i;
	}

	/**
	 * @param string
	 */
	public void setMessage(String string) {
		_message = string;
	}

	/**
	 * @param object
	 */
	public void setPayload(Object object) {
		_payload = object;
	}

	/**
	 * @param i
	 */
	public void setStatus(int i) {
		_status = i;
	}

}
