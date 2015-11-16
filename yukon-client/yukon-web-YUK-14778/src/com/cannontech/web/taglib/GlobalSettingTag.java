package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

@Configurable("globalSettingTagPrototype")
public class GlobalSettingTag extends YukonTagSupport {
    
    private GlobalSettingDao globalSettingDao;
    
    private String var;
    private GlobalSettingType globalSettingType = null;
    
    @Override
    public void doTag() throws JspException, IOException {
        Validate.notNull(var, "var must be set");
        Validate.notNull(globalSettingType, "globalSettingType must be set");
        String settingValue = globalSettingDao.getString(globalSettingType);
        
        getJspContext().setAttribute(var, settingValue);
    }
    
    public void setVar(String var) {
        this.var = var;
    }
    
    public void setGlobalSettingType(String type) {
        this.globalSettingType = GlobalSettingType.valueOf(type);
    }

    public void setGlobalSettingDao(GlobalSettingDao globalSettingDao) {
        this.globalSettingDao = globalSettingDao;
    }
}
