package com.cannontech.web.i18n;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.web.taglib.MessageScopeHelper;
import com.cannontech.web.taglib.MessageScopeHelper.MessageScope;
import com.cannontech.web.taglib.UniqueIdentifierTag;
import com.cannontech.web.taglib.YukonTagSupport;

public class IconTag extends YukonTagSupport implements DynamicAttributes {

    private String icon = null;
    private String id = null;
    private String nameKey = null;
    private String href = null;
    private String classes = "";
    private String title;

    private Map<String, Object> dynamicAttributes = new HashMap<>();

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public void setNameKey(String nameKey) {
        this.nameKey = nameKey;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void doTag() throws JspException, IOException, NoSuchMessageException {
        MessageScope messageScope = MessageScopeHelper.forRequest(getRequest());

        JspWriter out = getJspContext().getOut();

        if (StringUtils.isNotBlank(href)) out.write("<a href=\"" + href + "\">");

        out.write("<i class=\"icon " + icon + " " + classes + "\"" );

        id = StringUtils.isBlank(id) ? UniqueIdentifierTag.generateIdentifier(getJspContext(), "icon_") : id;
        out.write(" id=\"" + id + '"');
        
        String hoverText = null;
        if (StringUtils.isNotBlank(nameKey)) {
            try {
                MessageScopeHelper.forRequest(getRequest()).pushScope("." + nameKey, "components.image." + nameKey);
                hoverText = getLocalMessage(messageScope, ".hoverText");
            } finally {
                MessageScopeHelper.forRequest(getRequest()).popScope();
            }
        }
        if (StringUtils.isNotBlank(title)) hoverText = StringEscapeUtils.escapeHtml4(title);
        
        if (StringUtils.isNotBlank(hoverText)) {
            out.write(" title=\"");
            out.write(hoverText);
            out.write("\"");
        }

        for (String attributeName : dynamicAttributes.keySet()) {
            out.write(" " + attributeName + "=\"" + dynamicAttributes.get(attributeName) + "\"");
        }

        out.write(">"
        		+ IconSvg.getIconForClass(icon).getSvg()
        		+ "</i>");

        if (StringUtils.isNotBlank(href)) {
            out.write("</a>");
        }
    }

    private String getLocalMessage(MessageScope messageScope, String key) {
        MessageSourceResolvable resolvable = messageScope.generateResolvable(key);
        String retVal;
        try {
            retVal = getMessageSource().getMessage(resolvable);
        } catch (NoSuchMessageException nsme) {
            return null;
        }
        return retVal;
    }

    @Override
    public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
        dynamicAttributes.put(localName, value == null ? "" : value);
    }
}