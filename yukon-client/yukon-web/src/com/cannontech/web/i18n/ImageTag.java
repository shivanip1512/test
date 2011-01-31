package com.cannontech.web.i18n;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.util.ServletUtil;
import com.cannontech.web.taglib.MessageScopeHelper;
import com.cannontech.web.taglib.YukonTagSupport;
import com.cannontech.web.taglib.MessageScopeHelper.MessageScope;

public class ImageTag extends YukonTagSupport {

    protected String id = null;
    protected String key = null;
    protected String href = null;
    protected String styleClass = null;

    public void setId(String id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    @Override
    public void doTag() throws JspException, IOException,
            NoSuchMessageException {
        MessageScope messageScope = MessageScopeHelper.forRequest(getRequest());

        try {
            MessageScopeHelper.forRequest(getRequest())
                .pushScope("." + key, "components.image." + key);
            String imageUrl = getLocalMessage(messageScope, ".imageUrl", true);
            imageUrl = ServletUtil.createSafeUrl(getRequest(), imageUrl);
            String hoverText = getLocalMessage(messageScope, ".hoverText", false);
            String hoverUrl = getLocalMessage(messageScope, ".hoverUrl", false);

            JspWriter out = getJspContext().getOut();

            if (StringUtils.isNotBlank(href)) {
                if (StringUtils.isBlank(styleClass)) {
                    styleClass = "simpleLink";
                }
                out.write("<a href=\"");
                out.write(href);
                out.write("\" class=\"");
                out.write(styleClass);
                out.write("\">");
            }

            out.write("<img");
            if (StringUtils.isNotBlank(id)) {
                out.write(" id=\"");
                out.write(id);
                out.write('"');
            }
            
            String style = "logoImage";
            if (StringUtils.isNotBlank(styleClass)) {
                style += (" " + styleClass);
            }
            out.write(" class=\"" + style + "\"");
            out.write(" src=\"");
            out.write(imageUrl);
            out.write("\"");

            if (hoverText != null) {
                out.write(" alt=\"");
                out.write(hoverText);
                out.write("\"");
                out.write(" title=\"");
                out.write(hoverText);
                out.write("\"");
            } else {
                out.write(" alt=\"\"");
            }

            if (hoverUrl != null) {
                hoverUrl = ServletUtil.createSafeUrl(getRequest(), hoverUrl);

                out.write(" onmouseover=\"javascript:this.src='");
                out.write(hoverUrl);
                out.write("'\" onmouseout=\"javascript:this.src='");
                out.write(imageUrl);
                out.write("'\"");
            }
            out.write(">");

            if (StringUtils.isNotBlank(href)) {
                out.write("</a>");
            }
        } finally {
            MessageScopeHelper.forRequest(getRequest()).popScope();
        }
    }

    protected String getLocalMessage(MessageScope messageScope, String key,
            boolean required) {
        MessageSourceResolvable resolvable = messageScope.generateResolvable(key);
        String retVal;
        try {
            retVal = getMessageSource().getMessage(resolvable);
        } catch (NoSuchMessageException nsme) {
            if (required) {
                throw nsme;
            }
            return null;
        }
        return retVal;
    }
}
