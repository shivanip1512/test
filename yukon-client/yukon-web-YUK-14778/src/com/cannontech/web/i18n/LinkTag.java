package com.cannontech.web.i18n;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.taglibs.standard.tag.common.core.ParamParent;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.util.ServletUtil;
import com.google.common.collect.ImmutableSet;

/**
 * Tag used to output the right side logo for a page if one exists
 */
public class LinkTag extends HtmlTagBase implements ParamParent {
    private String href;
    private boolean escapeBody = true;
    private Map<String, String> encodedParameters = new HashMap<String, String>();
    private Set<String> supportedAttributes = ImmutableSet.of("title", "onclick", "id", "class", "style");

    @Override
    public void doTag() throws JspException, IOException {
		MessageSourceAccessor messageSourceAccessor = getMessageSource();
		
		if (StringUtils.isBlank(getKey())) {
		    throw new JspException("key attribute must not be blank");
		}
		
		// get href
		String hrefUrl = getAttributeValue("href", href);
		hrefUrl = ServletUtil.createSafeUrl(getRequest(), hrefUrl);
		hrefUrl = StringEscapeUtils.escapeHtml4(hrefUrl);

		StringWriter bodyWriter = new StringWriter();
		if (getJspBody() != null) {
		    getJspBody().invoke(bodyWriter);
		}
		
		String bodyText;
		String tagBodyStr = bodyWriter.toString().trim();
        try {
            // see if there is message text
            bodyText = messageSourceAccessor.getMessage(getKey(), tagBodyStr);
        } catch (NoSuchMessageException e) {
            // okay, I guess we'll use the body
            bodyText = tagBodyStr;
        }
        
        // build query string
        String queryString = ServletUtil.buildQueryStringFromMap(encodedParameters);
        
        JspWriter out = getJspContext().getOut();
        out.write("<a href=\"");
        out.write(hrefUrl);
        if (StringUtils.isNotBlank(queryString)) {
            out.write("?");
            out.write(queryString);
        }
        out.write("\" ");
        
        outputBaseAttributes(out);
        
        out.write(">");
        if (escapeBody) {
            out.write(StringEscapeUtils.escapeHtml4(bodyText));
        } else {
            out.write(bodyText);
        }
        out.write("</a>");
    }

    @Override
    public void addParameter(String name, String value) {
        encodedParameters.put(name, value);
    }

    public void setHref(String href) {
        this.href = href;
    }

    public void setEscapeBody(boolean escapeBody) {
        this.escapeBody = escapeBody;
    }

    @Override
    protected Set<String> getSupportedAttributes() {
        return supportedAttributes;
    }
}
