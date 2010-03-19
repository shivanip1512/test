package com.cannontech.web.common.flashScope;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.BindingResult;

import com.cannontech.web.common.validation.YukonValidationUtils;
import com.google.common.collect.Lists;

public class FlashScope {

	private HttpServletRequest request;
	
	public FlashScope(HttpServletRequest request) {
		this.request = request;
	}
	
	public void setMessages(List<? extends MessageSourceResolvable> messages, FlashScopeMessageType flashScopeMessageType) {
		setMessage(flashScopeMessageType, messages);
	}
	public void setConfirm(List<? extends MessageSourceResolvable> messages) {
		setMessage(FlashScopeMessageType.CONFIRM, messages);
	}
	public void setWarning(List<? extends MessageSourceResolvable> messages) {
		setMessage(FlashScopeMessageType.WARNING, messages);
	}
	public void setError(List<? extends MessageSourceResolvable> messages) {
		setMessage(FlashScopeMessageType.ERROR, messages);
	}
	
	public void setBindingResult(BindingResult bindingResult) {

		List<MessageSourceResolvable> messages =
		    YukonValidationUtils.errorsForBindingResult(bindingResult);

		setMessage(FlashScopeMessageType.ERROR, messages);
	}
	
	// PULL MESSAGES
	public List<FlashScopeMessage> pullMessages() {
		
		List<FlashScopeMessage> msgs = Lists.newArrayListWithCapacity(FlashScopeMessageType.values().length);
		
		HttpSession session = request.getSession();
		for (FlashScopeMessageType type : FlashScopeMessageType.values()) {
			FlashScopeMessage msg = (FlashScopeMessage)session.getAttribute(type.name());
			session.removeAttribute(type.name());
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
		session.setAttribute(type.name(), msg);
	}
}
