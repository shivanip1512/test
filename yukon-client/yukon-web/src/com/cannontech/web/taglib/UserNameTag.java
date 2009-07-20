package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;

@Configurable("userNameTagPrototype")
public class UserNameTag extends YukonTagSupport {
	
    private int userId;
    private String var;
    private YukonUserDao yukonUserDao;
    
    @Override
    public void doTag() throws JspException, IOException {
    	
    	LiteYukonUser liteYukonUser = yukonUserDao.getLiteYukonUser(this.userId);
    	
    	String userName = liteYukonUser.getUsername();
    	
    	if (this.var == null) {
            getJspContext().getOut().print(userName);
        } else {
            this.getJspContext().setAttribute(this.var, userName);
        }
    }

    public void setUserId(int userId) {
		this.userId = userId;
	}
    
    public void setVar(final String var) {
        this.var = var;
    }
    
    @Required
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
		this.yukonUserDao = yukonUserDao;
	}
}
