package com.cannontech.web.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;


import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigLicenseKey;
import com.cannontech.spring.YukonSpringHook;

/**
 * If at least one property is true then the body of the tag is evaluated, otherwise it is skipped.
 */
public class CheckLicenseKeyTag extends TagSupport {
    
    private String keyName;
   
    @Override
    public int doStartTag() throws JspException {
        
        MasterConfigLicenseKey key = MasterConfigLicenseKey.valueOf(keyName);
        
        ConfigurationSource configurationSource = YukonSpringHook.getBean(ConfigurationSource.class);
        
        boolean enable = configurationSource.isLicenseEnabled(key);

        return enable ? EVAL_BODY_INCLUDE : SKIP_BODY;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

}
