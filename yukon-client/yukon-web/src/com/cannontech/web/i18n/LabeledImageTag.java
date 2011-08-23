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
    private String imgStyleClass = null;
    private boolean imageOnRight = false;

    public void setLabelStyleClass(String labelStyleClass) {
        this.labelStyleClass = labelStyleClass;
    }
    
    public void setImgStyleClass(String imgStyleClass) {
        this.imgStyleClass = imgStyleClass;
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
                .pushScope("." + nameKey, "components.image." + nameKey);
            String imageUrl = getLocalMessage(messageScope, ".imageUrl", true);
            imageUrl = ServletUtil.createSafeUrl(getRequest(), imageUrl);
            String labelText = getLocalMessage(messageScope, ".label", true);
            
            String containerClass = "simpleLink";
            if (StringUtils.isNotBlank(href)) {
                containerClass += " hoverableImageContainer";
            }
            
            if (StringUtils.isNotBlank(styleClass)) {
                containerClass += " " + styleClass;
            }
            
            String hoverText = getLocalMessage(messageScope, ".hoverText", false);

            JspWriter out = getJspContext().getOut();

            out.write("<span id=\"");
            out.write(id);
            out.write("\" class=\"");
            out.write(containerClass);
            out.write("\"");
            if (hoverText != null) {
                out.write(" title=\"");
                out.write(hoverText);
                out.write("\"");
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
        String imgClass = "logoImage";
        
        if (StringUtils.isNotBlank(imgStyleClass)) {
            imgClass += (" " + imgStyleClass);
        }
        
        if (StringUtils.isNotBlank(href)) {
            imgClass += " hoverableImage";
        }
        
        out.write("<img class=\"" + imgClass + "\" src=\"");
        out.write(imageUrl);
        out.write("\">");
    }
}
