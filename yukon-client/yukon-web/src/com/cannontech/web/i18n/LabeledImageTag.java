package com.cannontech.web.i18n;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.util.ServletUtil;
import com.cannontech.web.taglib.MessageScopeHelper;
import com.cannontech.web.taglib.UniqueIdentifierTag;
import com.cannontech.web.taglib.MessageScopeHelper.MessageScope;

public class LabeledImageTag extends ImageTag {
    private String labelStyleClass = null;
    private boolean imageOnRight = false;

    public void setLabelStyleClass(String labelStyleClass) {
        this.labelStyleClass = labelStyleClass;
    }

    public void setImageOnRight(boolean imageOnRight) {
        this.imageOnRight = imageOnRight;
    }

    @Override
    public void doTag() throws JspException, IOException,
            NoSuchMessageException {

        MessageScope messageScope = MessageScopeHelper.forRequest(getRequest());
        if (StringUtils.isBlank(id)) {
            id = UniqueIdentifierTag.generateIdentifier(getJspContext(),
                                                        "labeledImage");
        }

        try {
            MessageScopeHelper.forRequest(getRequest())
                .pushScope("." + key, "components.image." + key);
            String imageUrl = getLocalMessage(messageScope, ".imageUrl", true);
            imageUrl = ServletUtil.createSafeUrl(getRequest(), imageUrl);
            String labelText = getLocalMessage(messageScope, ".label", true);
            if (StringUtils.isBlank(styleClass)) {
                styleClass = "simpleLink";
            }
            String hoverUrl = getLocalMessage(messageScope, ".hoverUrl", false);
            String hoverText = getLocalMessage(messageScope, ".hoverText", false);

            JspWriter out = getJspContext().getOut();

            out.write("<span id=\"");
            out.write(id);
            out.write("\" class=\"");
            out.write(styleClass);
            out.write("\"");
            if (hoverText != null) {
                out.write(" title=\"");
                out.write(hoverText);
                out.write("\"");
            }
            if (hoverUrl != null) {
                hoverUrl = ServletUtil.createSafeUrl(getRequest(),
                                                          hoverUrl);

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
            out.write(">");

            if (StringUtils.isNotBlank(href)) {
                out.write("<a href=\"");
                out.write(href);
                out.write("\">");
            }

            if (!imageOnRight) {
                writeImage(out, imageUrl);
            }

            out.write("<span class=\"");
            out.write(imageOnRight ? "leftOfImageLabel" : "rightOfImageLabel");
            if (!StringUtils.isBlank(labelStyleClass)) {
                out.write(' ');
                out.write(labelStyleClass);
            }
            out.write("\">");
            out.write(labelText);
            out.write("</span>");

            if (imageOnRight) {
                writeImage(out, imageUrl);
            }

            if (StringUtils.isNotBlank(href)) {
                out.write("</a>");
            }

            out.write("</span>");
        } finally {
            MessageScopeHelper.forRequest(getRequest()).popScope();
        }
    }

    private void writeImage(JspWriter out, String imageUrl) throws IOException {
        out.write("<img class=\"logoImage\" src=\"");
        out.write(imageUrl);
        out.write("\">");
    }
}
