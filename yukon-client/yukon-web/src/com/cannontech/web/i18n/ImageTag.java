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
    protected String name = null;
    protected String key = null;
    protected String href = null;
    protected String styleClass = null;
    protected String type = null;
    protected String value = null;
    protected boolean isButton = false;
    protected boolean hide = false;

    public void setId(String id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
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
    
    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setIsButton(Boolean isButton) {
        this.isButton = isButton;
    }

    public void setHide(Boolean hide) {
        this.hide = hide;
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

            JspWriter out = getJspContext().getOut();

            if (StringUtils.isNotBlank(href)) {
                out.write("<a href=\"");
                out.write(href);
                out.write("\" class=\"simpleLink\"");
                if (hide) {
                    out.write(" style=\"display: none;\"");
                }
                out.write(">");
            }

            if (StringUtils.isNotBlank(type) && type.equalsIgnoreCase("input")) {
                out.write("<input type=\"image\"");
            } else {
                out.write("<img");
                if (hide && StringUtils.isBlank(href)) {
                    out.write(" style=\"display: none;\"");
                }
            }
            
            if (StringUtils.isNotBlank(id)) {
                out.write(" id=\"");
                out.write(id);
                out.write('"');
            }
            
            String imgClass = "logoImage";

            if (StringUtils.isNotBlank(href)
                    || StringUtils.isNotBlank(type) && type.equalsIgnoreCase("input")
                    || isButton) {
                imgClass += " hoverableImage";
            }

            if (StringUtils.isNotBlank(styleClass)) {
                imgClass += " " + styleClass;
            }
            if (isButton) {
                imgClass += " simpleLink";
            }
            out.write(" class=\"" + imgClass + "\"");
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

            if (StringUtils.isNotBlank(name)) {
                out.write(" name=\"");
                out.write(name);
                out.write('"');
            }
            
            if (StringUtils.isNotBlank(value) && (StringUtils.isNotBlank(type) && type.equalsIgnoreCase("input"))) {
                out.write(" value=\"");
                out.write(value);
                out.write('"');
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
