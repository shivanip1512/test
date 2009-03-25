package com.cannontech.web.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.checker.UserChecker;
import com.cannontech.util.ServletUtil;

public class CheckUserCheckerTag extends TagSupport {

	private String var;
	private UserChecker userChecker;
	
	@Override
	public int doStartTag() throws JspException {
        
		boolean isAuthorized = true;
		if (userChecker != null) {
			LiteYukonUser yukonUser = ServletUtil.getYukonUser(pageContext.getRequest());
			isAuthorized = userChecker.check(yukonUser);
		}
		
		if (var != null) {
			pageContext.setAttribute(var, isAuthorized);
			return SKIP_BODY; 
		} else {
			if (isAuthorized) {
				return EVAL_BODY_INCLUDE;
			} else {
				return SKIP_BODY; 
			}
		}
    }
	
	
	public void setVar(String var) {
		this.var = var;
	}
	
	public void setUserChecker(UserChecker userChecker) {
		this.userChecker = userChecker;
	}
}
