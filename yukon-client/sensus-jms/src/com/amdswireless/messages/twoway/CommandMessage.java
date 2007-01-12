package com.amdswireless.messages.twoway;

import java.io.Serializable;

public class CommandMessage implements Serializable {

	private transient static final long serialVersionUID = 1L;
	EngineParameters engineParameters;
	TxRequest txRequest;
	
	public CommandMessage() {
		txRequest = new TxRequest();
		engineParameters = new EngineParameters();
	}

	public EngineParameters getEngineParameters() {
		return engineParameters;
	}

	public void setEngineParameters(EngineParameters engineParameters) {
		this.engineParameters = engineParameters;
	}

	public TxRequest getTxRequest() {
		return txRequest;
	}

	public void setTxMsg(TxRequest txRequest) {
		this.txRequest = txRequest;
	}
}
