package com.cannontech.web.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.ServletUtil;


/**
 * If at least one property is true then the body of the tag is evaluated, otherwise it is skipped.
 * @see CheckProperty
 */
public class CheckRolesAndPropertiesTag extends TagSupport {
    private String value;
    
    @Override
    public int doStartTag() throws JspException {
     
        LiteYukonUser yukonUser = ServletUtil.getYukonUser(pageContext.getRequest());
        
        RoleAndPropertyDescriptionService descriptionService = 
        	YukonSpringHook.getBean(
        			"roleAndPropertyDescriptionService", RoleAndPropertyDescriptionService.class);
        
    	boolean checkIfAtLeaseOneExists = 
        	descriptionService.checkIfAtLeaseOneExists(getValue(), yukonUser);
        if (checkIfAtLeaseOneExists) {
        	return EVAL_BODY_INCLUDE;
        } else {
        	return SKIP_BODY;
        }
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}