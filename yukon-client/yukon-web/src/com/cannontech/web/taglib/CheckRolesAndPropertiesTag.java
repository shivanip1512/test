package com.cannontech.web.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.ServletUtil;

/**
 * If at least one property is true then the body of the tag is evaluated, otherwise it is skipped.
 */
public class CheckRolesAndPropertiesTag extends TagSupport {
    private String value;

    @Override
    public int doStartTag() throws JspException {
        LiteYukonUser user = ServletUtil.getYukonUser(pageContext.getRequest());

        RoleAndPropertyDescriptionService descriptionService =
            YukonSpringHook.getBean(RoleAndPropertyDescriptionService.class);

        return descriptionService.checkIfAtLeaseOneExists(value, user) ? EVAL_BODY_INCLUDE : SKIP_BODY;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
