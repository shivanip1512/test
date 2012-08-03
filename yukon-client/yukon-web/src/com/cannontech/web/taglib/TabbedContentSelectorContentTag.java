package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.ObjectFormattingService;

public class TabbedContentSelectorContentTag extends YukonTagSupport {
    private final static Logger log = YukonLogManager.getLogger(TabbedContentSelectorContentTag.class);

    private ObjectFormattingService objectFormattingService;
    
	private String selectorName = "";
    private Object key;
	private String cssClass;
	private boolean initiallySelected = false;
	
	@Override
    public void doTag() throws JspException, IOException {
        MessageSourceResolvable resolvable = null;
        if (key instanceof String) {
            String baseCode = (String) key;
            resolvable = MessageScopeHelper.forRequest(getRequest()).generateResolvable(baseCode);
        } else if (key != null){
            resolvable = objectFormattingService.formatObjectAsResolvable(key, getUserContext());
        }

        String message = selectorName;
        if (resolvable != null) {
            try {
                message = getMessageSource().getMessage(resolvable);
            } catch (NoSuchMessageException e) {
                if (key instanceof String) {
                    log.error("unable to resolve message for key [" + key + "] using scope " + MessageScopeHelper.forRequest(getRequest()));
                }
                throw e;
            }
        }

		String id = UniqueIdentifierTag.generateIdentifier(getJspContext(), "tabbedContentSelectorContent_");
		
		// tell container tag about ourself
        TabbedContentSelectorTag parent = getParent(TabbedContentSelectorTag.class);
        parent.addTab(message, id, initiallySelected);
        
        // tab content
		getJspContext().getOut().println("<div id=\"" + id + "\" class=\"" + cssClass + "\">");
        getJspBody().invoke(getJspContext().getOut());
		getJspContext().getOut().println("</div>");
        
    }
	
	public void setSelectorName(String selectorName) {
		this.selectorName = selectorName;
	}

	public void setKey(Object key) {
        this.key = key;
    }
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }
 
	public void setInitiallySelected(boolean initiallySelected) {
		this.initiallySelected = initiallySelected;
	}
    
    public void setObjectFormattingService(ObjectFormattingService objectFormattingService) {
        this.objectFormattingService = objectFormattingService;
    }
}