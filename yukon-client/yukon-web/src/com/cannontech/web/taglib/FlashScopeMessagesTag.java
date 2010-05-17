package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.util.HtmlUtils;

import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessage;

public class FlashScopeMessagesTag extends YukonTagSupport {

	private boolean htmlEscape = true;
	
	@Override
    public void doTag() throws JspException, IOException {
    	
		PageContext pageContext = (PageContext) getJspContext();
		JspWriter out = getJspContext().getOut();
		HttpSession session = pageContext.getSession();
		
		FlashScope flashScope = new FlashScope(session);
		List<FlashScopeMessage> flashScopeMessages = flashScope.pullMessages();
		for (FlashScopeMessage flashScopeMessage : flashScopeMessages) {
			
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
