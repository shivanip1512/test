package com.amdswireless.messages.twoway;

import java.io.Serializable;
import java.util.Vector;

import com.amdswireless.messages.rx.DataMessage;

public class CommandResponseMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private Vector<DataMessage> responses;
	
	public CommandResponseMessage(Vector<DataMessage> responses) {
		this.responses=responses;
	}
	
	public CommandResponseMessage() {
		responses = new Vector<DataMessage>();
	}
	
	public Vector<DataMessage> getResponses() {
		return this.responses;
	}
	
	public void setResponses(Vector<DataMessage> responses) {
		this.responses = responses;
	}
}
