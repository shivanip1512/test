package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;

@Configurable("userNameTagPrototype")
public class UserNameTag extends YukonTagSupport {
	
    private Integer userId;
    private String var;
    private YukonUserDao yukonUserDao;
    
    @Override
    public void doTag() throws JspException, IOException {
    	
    	String userName = "N/A";
    	LiteYukonUser liteYukonUser = yukonUserDao.getLiteYukonUser(this.userId);
    	if (liteYukonUser != null) {
    		userName = liteYukonUser.getUsername();
    	}
    	
    	if (this.var == null) {
            getJspContext().getOut().print(userName);
        } else {
            this.getJspContext().setAttribute(this.var, userName);
        }
    }

    public void setUserId(Integer userId) {
		this.userId = userId;
	}
    
    public Integer getUserId() {
		return userId;
	}
    
    public void setVar(final String var) {
        this.var = var;
    }
    
    @Required
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
		this.yukonUserDao = yukonUserDao;
	}
}
