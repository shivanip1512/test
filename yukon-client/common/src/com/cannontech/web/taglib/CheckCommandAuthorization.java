package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.ServletUtil;

public class CheckCommandAuthorization extends BodyTagSupport {

    private String command = null;
    private LiteYukonPAObject device = null;

    public void setCommand(String command) {
        this.command = command;
    }

    public void setDevice(LiteYukonPAObject device) {
        this.device = device;
    }

    public int doStartTag() throws JspException {

        LiteYukonUser user = ServletUtil.getYukonUser(pageContext.getSession());

        PaoCommandAuthorizationService service = (PaoCommandAuthorizationService) YukonSpringHook.getBean("commandAuthorizationService");

        return (service.isAuthorized(user, command, device)) ? EVAL_BODY_INCLUDE : SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        try {
            if (bodyContent != null) {
                pageContext.getOut().print(bodyContent.getString());
            }
            return EVAL_PAGE;
        } catch (IOException e) {
            throw new JspException(e);
        }
    }

}
