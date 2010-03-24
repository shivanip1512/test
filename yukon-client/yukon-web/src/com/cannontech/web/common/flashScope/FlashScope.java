package com.cannontech.web.common.flashScope;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.MessageSourceResolvable;

import com.google.common.collect.Lists;

public class FlashScope {

	private HttpServletRequest request;
	private static final String SESSION_ATTR_PREFIX = "com.cannontech.web.common.flashScope.FlashScope";
	
	public FlashScope(HttpServletRequest request) {
		this.request = request;
	}
	
	public void setMessage(List<MessageSourceResolvable> messages, FlashScopeMessageType flashScopeMessageType) {
		setMessage(flashScopeMessageType, messages);
	}
	public void setMessage(MessageSourceResolvable message, FlashScopeMessageType flashScopeMessageType) {
		setMessage(flashScopeMessageType, Collections.singletonList(message));
	}
	
	public void setConfirm(List<MessageSourceResolvable> messages) {
		setMessage(FlashScopeMessageType.CONFIRM, messages);
	}
	public void setConfirm(MessageSourceResolvable message) {
		setMessage(FlashScopeMessageType.CONFIRM, Collections.singletonList(message));
	}
	public void setWarning(List<MessageSourceResolvable> messages) {
		setMessage(FlashScopeMessageType.WARNING, messages);
	}
	public void setWarning(MessageSourceResolvable message) {
		setMessage(FlashScopeMessageType.WARNING, Collections.singletonList(message));
	}
	public void setError(List<MessageSourceResolvable> messages) {
		setMessage(FlashScopeMessageType.ERROR, messages);
	}
	public void setError(MessageSourceResolvable message) {
		setMessage(FlashScopeMessageType.ERROR, Collections.singletonList(message));
	}
	
	// PULL MESSAGES
	public List<FlashScopeMessage> pullMessages() {
		
		List<FlashScopeMessage> msgs = Lists.newArrayListWithCapacity(FlashScopeMessageType.values().length);
		
		HttpSession session = request.getSession();
		for (FlashScopeMessageType type : FlashScopeMessageType.values()) {
			String attributeName = getAttributeNameForType(type);
			FlashScopeMessage msg = (FlashScopeMessage)session.getAttribute(attributeName);
			session.removeAttribute(attributeName);
			if (msg != null) {
				msgs.add(msg);
			}
		}
		
		return msgs;
	}
	
	// HELPERS
	private void setMessage(FlashScopeMessageType type, List<? extends MessageSourceResolvable> messages) {
		
		FlashScopeMessage msg = new FlashScopeMessage(messages, type);
		
		HttpSession session = request.getSession();
		String attributeName = getAttributeNameForType(type);
		session.setAttribute(attributeName, msg);
	}
	
	private String getAttributeNameForType(FlashScopeMessageType type) {
		return SESSION_ATTR_PREFIX + type.name();
	}
}
