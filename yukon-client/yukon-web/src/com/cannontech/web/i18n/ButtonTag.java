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
    protected String href = null;
    protected String onclick = null;
    protected String styleClass = null;
    protected String type = null;
    protected String name = null;
    protected Boolean imageOnRight = false;

    public void setId(String id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setHref(String href) {
        this.href = href;
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

    public void setImageOnRight(Boolean imageOnRight) {
        this.imageOnRight = imageOnRight;
    }

    @Override
    public void doTag() throws JspException, IOException, NoSuchMessageException {

        if (StringUtils.isBlank(id)) {
            id = UniqueIdentifierTag.generateIdentifier(getJspContext(), "button");
        }

        String classes = "formSubmit pointer hoverableImageContainer";
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

            if (StringUtils.isNotBlank(onclick)) {
                out.write(" onclick=\"" + onclick + "\"");
            }

            if (StringUtils.isNotBlank(href)) {
                out.write(" onclick=\"javascript:window.location='" + href + "'\"");
            }

            out.write(">");

            boolean hasImage = StringUtils.isNotBlank(imageUrl);

            if (hasImage) {
                imageUrl = ServletUtil.createSafeUrl(getRequest(), imageUrl);
                if (!imageOnRight) {
                    writeImage(out, imageUrl);
                }
            }

            if (!StringUtils.isBlank(labelText)) {
                out.write("<span");
                if (hasImage) {
                    out.write(" class=\"");
                    out.write(imageOnRight ? "leftOfImageLabel" : "rightOfImageLabel");
                    out.write("\"");
                }
                out.write(">");
                out.write(labelText);
                out.write("</span>");
            }

            if (hasImage && imageOnRight) {
                writeImage(out, imageUrl);
            }

            out.write("</button>");

        } finally {
            MessageScopeHelper.forRequest(getRequest()).popScope();
        }
    }

    private void writeImage(JspWriter out, String imageUrl) throws IOException {
        out.write("<img class=\"logoImage hoverableImage\" src=\"");
        out.write(imageUrl);
        out.write("\" alt=\"\">");
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
