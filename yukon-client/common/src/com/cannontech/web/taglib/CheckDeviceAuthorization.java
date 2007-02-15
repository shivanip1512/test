package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.cannontech.core.authorization.support.AuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.ServletUtil;

public class CheckDeviceAuthorization extends BodyTagSupport {

    private Permission permission = null;
    private LiteYukonPAObject device = null;

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public void setDevice(LiteYukonPAObject device) {
        this.device = device;
    }

    public int doStartTag() throws JspException {

        LiteYukonUser user = ServletUtil.getYukonUser(pageContext.getSession());

        AuthorizationService service = (AuthorizationService) YukonSpringHook.getBean("deviceAthorizationService");

        return (service.isAuthorized(user, permission, device)) ? SKIP_BODY : EVAL_BODY_INCLUDE;
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
