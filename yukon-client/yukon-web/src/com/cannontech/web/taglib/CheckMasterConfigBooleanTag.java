package com.cannontech.web.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.spring.YukonSpringHook;

/**
 * Check a Master Config Boolean setting, if the condition is met, then the body of the tag is evaluated, otherwise it is skipped.
 */
public class CheckMasterConfigBooleanTag extends TagSupport {

    private String setting;

    @Override
    public int doStartTag() throws JspException {
        
        boolean inverted = false;
        if (setting.startsWith("!")) {
            setting = setting.substring(1);
            inverted = true;
        }
        
        ConfigurationSource configurationSource =
                YukonSpringHook.getBean(ConfigurationSource.class);
        
        MasterConfigBoolean key = MasterConfigBoolean.valueOf(setting);
        boolean configValue = configurationSource.getBoolean(key);
        
        if (inverted) {
            return !configValue ? EVAL_BODY_INCLUDE : SKIP_BODY;
        } else {
            return configValue ? EVAL_BODY_INCLUDE : SKIP_BODY;
        }
    }
    
    public void setSetting(String setting) {
        this.setting = setting;
    }
}