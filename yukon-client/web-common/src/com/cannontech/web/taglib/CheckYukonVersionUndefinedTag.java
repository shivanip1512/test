package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.version.VersionTools;


/**
 * If the current YukonVersion is undefined or unknown, then the body of the tag is evaluated, otherwise it is skipped
 */
@Configurable("checkYukonVersionUndefinedTagPrototype")
public class CheckYukonVersionUndefinedTag extends YukonTagSupport {

    @Override
    public void doTag() throws JspException, IOException {
        if (!VersionTools.isYukonVersionDefined()) {
            getJspBody().invoke(null);
        }
    }
}