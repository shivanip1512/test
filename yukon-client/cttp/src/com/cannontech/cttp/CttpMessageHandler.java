/*
 * Created on Nov 5, 2003
 */
package com.cannontech.cttp;

/**
 * CttpMessageHandler handles a cttp operation request
 * and returns a cttp operation response.
 * @author aaron
 */
public interface CttpMessageHandler {
		
	public CttpResponse handleMessage(CttpRequest req) throws Exception;
}
