package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.util.HtmlUtils;

import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessage;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class FlashScopeMessagesTag extends YukonTagSupport {
    private final static String REQUEST_ATTR_NAME = "com.cannontech.web.taglib.FlashScopeMessagesTag.flashScopeMessages";

	private boolean htmlEscape = true;
	
    @Override
    public void doTag() throws JspException, IOException {
    	
		PageContext pageContext = (PageContext) getJspContext();
		JspWriter out = getJspContext().getOut();
		HttpSession session = pageContext.getSession();
		ServletRequest request = pageContext.getRequest();

		FlashScope flashScope = new FlashScope(session);
		List<FlashScopeMessage> flashScopeMessages = flashScope.pullMessages();
		Iterable<FlashScopeMessage> requestMessages = null;
		if (request.getAttribute(REQUEST_ATTR_NAME) != null) {
		    requestMessages = Iterables.filter((Iterable<?>) request.getAttribute(REQUEST_ATTR_NAME),
		                                       FlashScopeMessage.class);
		}
		if (requestMessages == null) {
		    requestMessages = flashScopeMessages;
	        request.setAttribute(REQUEST_ATTR_NAME, requestMessages);
		} else if (!flashScopeMessages.isEmpty()) {
		    List<FlashScopeMessage> temp = Lists.newArrayList(requestMessages);
		    temp.addAll(flashScopeMessages);
		    requestMessages = temp;
	        request.setAttribute(REQUEST_ATTR_NAME, requestMessages);
		}
		for (FlashScopeMessage flashScopeMessage : requestMessages) {
			
			if (flashScopeMessage.getMessages().size() > 0) {
			
				out.println("<div class=\"userMessage " + flashScopeMessage.getType() + "\">");
				
				if (flashScopeMessage.getMessages().size() == 1) {
					String resolvedMessage = resolveMessage(flashScopeMessage.getMessages().get(0));
					out.println(resolvedMessage);
				} else {
					
					out.println("<ul>");
					for (MessageSourceResolvable messageSourceResolvable : flashScopeMessage.getMessages()) {
						String resolvedMessage = resolveMessage(messageSourceResolvable);
						out.println("<li>" + resolvedMessage + "</li>");
					}
					out.println("</ul>");
				}
				
				out.println("</div>");
			}
		}
    }
	
	private String resolveMessage(MessageSourceResolvable messageSourceResolvable) {
		
		String resolvedMessage = getMessageSource().getMessage(messageSourceResolvable);
		return htmlEscape ? HtmlUtils.htmlEscape(resolvedMessage) : resolvedMessage;
	}
	
	public void setHtmlEscape(boolean htmlEscape) throws JspException {
        this.htmlEscape = htmlEscape;
    }
}
