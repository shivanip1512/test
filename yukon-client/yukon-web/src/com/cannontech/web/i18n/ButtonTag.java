package com.cannontech.web.i18n;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.util.ServletUtil;
import com.cannontech.web.taglib.MessageScopeHelper;
import com.cannontech.web.taglib.UniqueIdentifierTag;
import com.cannontech.web.taglib.YukonTagSupport;
import com.cannontech.web.taglib.MessageScopeHelper.MessageScope;

public class ButtonTag extends YukonTagSupport {

    protected String id = null;
    protected String key = null;
    protected String onclick = null;
    protected String styleClass = null;
    protected String type = null;
    protected String name = null;

    public void setId(String id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void doTag() throws JspException, IOException, NoSuchMessageException {

        if (StringUtils.isBlank(id)) {
            id = UniqueIdentifierTag.generateIdentifier(getJspContext(), "labeledImage");
        }

        String classes = "formSubmit";
        if (StringUtils.isNotBlank(styleClass)) {
            classes += " " + styleClass;
        }

        try {
            MessageScopeHelper.forRequest(getRequest()).pushScope("." + key, "components.button." + key);

            MessageScope messageScope = MessageScopeHelper.forRequest(getRequest());

            /* Image Url */
            MessageSourceResolvable imgUrlResolvable = messageScope.generateResolvable(".imageUrl");
            String imageUrl = getLocalMessage(imgUrlResolvable, false);

            /* Button Text */
            MessageSourceResolvable labelTextResolvable = messageScope.generateResolvable(".label");
            String labelText = getLocalMessage(labelTextResolvable, false);

            if (StringUtils.isBlank(imageUrl) && StringUtils.isBlank(labelText)) {
                throw new RuntimeException("at least one of .imageUrl or .label is required for " + key);
            }

            /* Hover Url */
            MessageSourceResolvable hoverUrlResolvable = messageScope.generateResolvable(".hoverUrl");
            String hoverUrl = getLocalMessage(hoverUrlResolvable, false);

            /* Hover Text */
            MessageSourceResolvable hoverTextResolvable = messageScope.generateResolvable(".hoverText");
            String hoverText = getLocalMessage(hoverTextResolvable, false);

            JspWriter out = getJspContext().getOut();

            out.write("<button id=\"");
            out.write(id);
            out.write("\"");

            /* Type */
            if (type == null) {
                type = "button";
            }
            out.write(" type=\"");
            out.write(type);
            out.write("\"");

            /* Name */
            if (name != null) {
                out.write(" name=\"");
                out.write(name);
                out.write("\"");
            }

            /* Class */
            out.write(" class=\"");
            out.write(classes);
            out.write("\"");

            /* Title */
            if (hoverText != null) {
                out.write(" title=\"");
                out.write(hoverText);
                out.write("\"");
            }

            /* Hover events */
            if (hoverUrl != null) {
                hoverUrl = ServletUtil.createSafeUrl(getRequest(), hoverUrl);

                out.write(" onmouseover=\"javascript:$$('#");
                out.write(id);
                out.write(" img')[0].src='");
                out.write(hoverUrl);
                out.write("'\" onmouseout=\"javascript:$$('#");
                out.write(id);
                out.write(" img')[0].src='");
                out.write(imageUrl);
                out.write("'\"");
            }

            if (StringUtils.isNotBlank(onclick)) {
                out.write(" onclick=\"" + onclick + "\"");
            }

            out.write(">");

            boolean hasImage = StringUtils.isNotBlank(imageUrl);
            
            if (hasImage) {
                imageUrl = ServletUtil.createSafeUrl(getRequest(), imageUrl);
                out.write("<img class=\"logoImage\" src=\"");
                out.write(imageUrl);
                out.write("\">");
            }

            if (!StringUtils.isBlank(labelText)) {
                out.write("<span");
                
                if (hasImage) {
                    out.write(" style=\"padding-left: 5px;\"");
                }
                out.write(">");
                out.write(labelText);
                out.write("</span>");
            }

            out.write("</button>");

        } finally {
            MessageScopeHelper.forRequest(getRequest()).popScope();
        }
    }

    protected String getLocalMessage(MessageSourceResolvable resolvable, boolean required) {
        String retVal;
        try {
            retVal = getMessageSource().getMessage(resolvable);
        } catch (NoSuchMessageException e) {
            if (required) {
                throw e;
            }
            return null;
        }
        return retVal;
    }
}
