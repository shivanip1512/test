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

            out.write("<img class=\"logoImage\" src=\"");
            out.write(imageUrl);
            out.write("\"");

            if (hoverText != null) {
                out.write(" alt=\"");
                out.write(hoverText);
                out.write("\"");
            }
            out.write("> ");
            
            out.write("<span>");
            out.write(labelText);
            out.write("</span>");
            
            if (StringUtils.isNotBlank(href)) {
                out.write("</a>");
            }

            out.write("</span>");
        } finally {
            MessageScopeHelper.forRequest(getRequest()).popScope();
        }
    }
}
