package com.cannontech.web.i18n;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;

import com.cannontech.web.taglib.MessageScopeHelper;
import com.cannontech.web.taglib.YukonTagSupport;

public class PrintMsgScopeTag extends YukonTagSupport {
    @Override
    public void doTag() throws JspException, IOException {
        List<String> fullKeys = MessageScopeHelper.forRequest(getRequest()).getFullKeys(".xxx", "yukon.web.");
        getJspContext().getOut().println("<br>");
        getJspContext().getOut().print("<ul>");
        for (String fullKey : fullKeys) {
            getJspContext().getOut().print("<li>" + fullKey + "</li>");
            System.out.println(fullKey);
        }
        getJspContext().getOut().print("</ul>");
    }
}
