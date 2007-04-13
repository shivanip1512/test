package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.cannontech.common.version.VersionTools;

public class IsStarsExistTag extends SimpleTagSupport {
    @Override
    public void doTag() throws JspException, IOException {
        if (VersionTools.starsExists()) {
            getJspBody().invoke(null);
        }
    }
}
