/**
 * OneResponse.java part of com.amdswireless.agents.twoway
 * AMDSJava
 * Author johng as of Nov 11, 2005
 * SVN Version $Id$
 */
package com.amdswireless.messages.twoway;

import java.io.Serializable;

/**
 * @author johng
 *
 */
public class OneResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	private int msgsRemaining;
	private boolean required;
	
	public OneResponse(int count, boolean b ) {
		this.msgsRemaining=count;
		this.required=b;
	}
	
	public int getMsgCount() {
		return this.msgsRemaining;
	}
	
	public void setMsgCount(int i) {
		msgsRemaining=i;
	}
	
	public boolean isRequired() {
		return this.required;
	}
}
