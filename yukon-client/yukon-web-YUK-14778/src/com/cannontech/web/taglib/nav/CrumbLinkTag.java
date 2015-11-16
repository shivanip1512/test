package com.cannontech.web.taglib.nav;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.taglibs.standard.tag.common.core.ParamParent;

import com.cannontech.util.ServletUtil;
import com.cannontech.web.taglib.YukonTagSupport;

/**
 * Creates a "crumb" in a crumb trail. Can be either a link or static text depending on whether a url is specified in tag.
 * The crumb's text is defined by the title attribute, and/or any content in the tag body.
 * @author m_peterson
 *
 */
public class CrumbLinkTag extends YukonTagSupport implements ParamParent {
    
    private String url = null;
    private String title = null;
    private Map<String, String> encodedParameters = new HashMap<String, String>();
    private Writer bodyContentWriter;
    
    @Override
    public void doTag() throws JspException, IOException {
        
        // access to page writer and request
        JspWriter out = getJspContext().getOut();
        PageContext context = (PageContext)getJspContext();
        HttpServletRequest httpRequest = (HttpServletRequest)context.getRequest();
        
        // the containing BreadCrumbsTag
        BreadCrumbsTag bc = (BreadCrumbsTag)findAncestorWithClass(this, BreadCrumbsTag.class);
        
        JspFragment tagBody = getJspBody();
        if (tagBody != null) {
            bodyContentWriter = new StringWriter();
            tagBody.invoke(bodyContentWriter);
        }
        
        if (bc == null) {
            throw new JspTagException("Unnested tag requires parent tag of type: "  + BreadCrumbsTag.class.toString() );
        }
        
        if (getUrl() == null) { // this crumb is static text
            
            // the link display name will be the title attribute and/or any body content
            out.write("<li>");
            writeTitleIfAvailable();
            writeBodyContentIfAvailable();
            out.write("</li>");
            
        } else { // this crumb is a link
            
            // build the link
            String url = getUrl();
            url= ServletUtil.createSafeUrl(httpRequest, url);
            url = StringEscapeUtils.escapeHtml4(url);
            
            if (encodedParameters.size() > 0 && url.contains("?")) {
                throw new RuntimeException("CrumbLink url may not contain parameters when used with param tags.");
            }
            
            if (encodedParameters.size() > 0) {
                String queryString = ServletUtil.buildQueryStringFromMap(encodedParameters);
                url += "?" + queryString;
            }
            
            out.write("<li><a href=\"" + url + "\">");
            
            // the link display name will be the title attribute and/or any body content
            writeTitleIfAvailable();
            writeBodyContentIfAvailable();
            
            out.write("</a></li>");
            
        }
    }

    private void writeTitleIfAvailable() throws JspException, IOException {
        
        String title = getTitle() == null ? "" : getTitle();
        getJspContext().getOut().write(title);
    }

    private void writeBodyContentIfAvailable() throws JspException, IOException {
        
        if (bodyContentWriter != null) {
            getJspContext().getOut().write(bodyContentWriter.toString().trim());
        }
    }
    
    @Override
    public void addParameter(String name, String value) {
        encodedParameters.put(name, value);
    }
    
    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public void setTitle(String string) {
        title = string;
    }

    public void setUrl(String string) {
        url = string;
    }

}