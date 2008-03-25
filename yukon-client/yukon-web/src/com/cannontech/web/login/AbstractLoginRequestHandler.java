package com.cannontech.web.login;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.core.dao.YukonUserDao;

public abstract class AbstractLoginRequestHandler implements LoginRequestHandler {
    protected LoginService loginService;
    protected ConfigurationSource config;
    protected YukonUserDao yukonUserDao;
    
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }

    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }
    
    public void setConfig(ConfigurationSource config) {
        this.config = config;
    }
    
}
