package com.cannontech.web.taglib;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.taglibs.standard.tag.common.core.ParamParent;

import com.cannontech.util.ServletUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/*
 * Rewrite the JSTL UrlTag (eg. from Apache standard.jar) so it doesn't append
 * the JSESSIONID in the URL.
 */
public class UrlTag extends YukonTagSupport implements ParamParent {
    private String var;
    private String value;
    private boolean htmlEscape;

    private int scope;

    private ListMultimap<String, String> encodedParameters;

    private static final String REQUEST = "request";
    private static final String SESSION = "session";
    private static final String APPLICATION = "application";

    public UrlTag() {
        init();
    }

    private void init() {
        var = null;
        value = null;
        htmlEscape = false;
        scope = PageContext.PAGE_SCOPE;
        encodedParameters = ArrayListMultimap.create();
    }

    @Override
    public void doTag() throws JspException, IOException {

        // get the baseUrl
        String baseUrl = ServletUtil.createSafeUrl(getRequest(), value);
        
        //process the params in the body of the tag
        StringWriter bodyWriter = new StringWriter();
        if (getJspBody() != null) {
            getJspBody().invoke(bodyWriter);
        }
        
        // add parameters to the baseUrl
        String result = appendParams(baseUrl);
        
        // escape the full url if need be
        if (htmlEscape) {
            result = StringEscapeUtils.escapeHtml4(result);
        }
        
        // store or print the output
        if (var != null) {
            getPageContext().setAttribute(var, result, scope);
        } else {
            try {
                getPageContext().getOut().print(result);
            } catch (java.io.IOException ex) {
                throw new JspTagException(ex.toString(), ex);
            }
        }
    }

    @Override
    public void addParameter(String name, String value) {
        encodedParameters.put(name, value);
    }

    // appends params to the baseUrl provided
    private String appendParams(String baseUrl) {
        String result = baseUrl;

        // build query string
        String queryString = ServletUtil.buildQueryStringFromMap(encodedParameters, false);

        // insert these parameters into the URL as appropriate
        if (queryString.length() > 0) {
            int questionMark = baseUrl.indexOf('?');
            if (questionMark == -1) {
                result = (baseUrl + "?" + queryString);
            } else {
                StringBuffer workingUrl = new StringBuffer(baseUrl);
                workingUrl.insert(questionMark + 1, (queryString + "&"));
                result = workingUrl.toString();
            }
        }
        return result;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setHtmlEscape(boolean htmlEscape) {
        this.htmlEscape = htmlEscape;
    }
    
    public void setScope(String scope) {
        this.scope = getScope(scope);
    }

    public static int getScope(String scope) {
        int ret = PageContext.PAGE_SCOPE; // default

        if (REQUEST.equalsIgnoreCase(scope)) {
            ret = PageContext.REQUEST_SCOPE;
        } else if (SESSION.equalsIgnoreCase(scope)) {
            ret = PageContext.SESSION_SCOPE;
        } else if (APPLICATION.equalsIgnoreCase(scope)) {
            ret = PageContext.APPLICATION_SCOPE;
        }

        return ret;
    }
}
