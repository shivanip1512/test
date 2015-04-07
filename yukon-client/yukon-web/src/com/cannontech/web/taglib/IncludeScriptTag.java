package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.web.JsLibrary;

@Configurable(value = "includeScriptTagPrototype", autowire = Autowire.BY_NAME)
public class IncludeScriptTag extends YukonTagSupport {

    @Autowired private ConfigurationSource configurationSource;

    private String link;
    private boolean force = false; // force a <script> tag to be written to the output

    @Override
    public void doTag() throws JspException {
        
        StandardPageTag spTag = StandardPageTag.find(getJspContext());
        if (this.force) {
            // Beware of multiple includes!
            JspWriter out = getJspContext().getOut();
            try {
                PageContext pageContext = (PageContext) getJspContext();
                HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
                out.write("<script type=\"text/javascript\" src=\"");
                out.write(request.getContextPath() + resolveLink());
                out.write("\"></script>");
            } catch (IOException e) {
                // This should not happen unless the sky is falling.
                throw new RuntimeException(e);
            }
        } else if (spTag != null) {
            spTag.addScriptFile(resolveLink());
        }
        
        return;
    }
    
    private String resolveLink() {

        boolean devMode = configurationSource.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE);

        try {
            return JsLibrary.valueOf(link).getPath(devMode);
        } catch (IllegalArgumentException e) {
            return link;
        }
    }
    
    public void setForce(boolean force) {
        this.force = force;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    
}
