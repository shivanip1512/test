package com.cannontech.web.common.flashScope;

import java.util.List;

import org.springframework.context.MessageSourceResolvable;

public class FlashScopeMessage {

	private List<? extends MessageSourceResolvable> messages;
	private FlashScopeMessageType type;
	
	public FlashScopeMessage(List<? extends MessageSourceResolvable> messages, FlashScopeMessageType type) {
		this.messages = messages;
		this.type = type;
	}
	
	public List<? extends MessageSourceResolvable> getMessages() {
		return messages;
	}
	
	public FlashScopeMessageType getType() {
		return type;
	}
}
