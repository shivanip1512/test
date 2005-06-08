/*
 * Created on Mar 8, 2004
 */
package com.cannontech.message.server;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.ServerRequest;

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


    protected ServerResponseMsg( int id, int status, String message )
    {
        super();
        setId( id );
        setStatus( status );
        setMessage( message );
    }
    
    public ServerResponseMsg(int id)
    {
        _id = id;
    }
    
    public ServerResponseMsg() {
        
    }

    public static ServerResponseMsg createTimeoutResp()
    {
        return 
            new ServerResponseMsg(
                CtiUtilities.NONE_ZERO_ID,
                STATUS_ERROR,
                "Response from the server took too long and timed out (Timeout= " +
                    (ServerRequest.DEFAULT_TIMEOUT / 1000) + " seconds)");        
    }


	/**
	 * @return String
	 */
	public static String getStatusStr( int status ) 
	{	
		return STATUS_STRS[status];
	}


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
	protected void setId(int i) {
		_id = i;
	}

	/**
	 * @param string
	 */
	protected void setMessage(String string) {
		_message = string;
	}

	/**
	 * @param object
	 */
	protected void setPayload(Object object) {
		_payload = object;
	}

	/**
	 * @param i
	 */
	protected void setStatus(int i) {
		_status = i;
	}

}
