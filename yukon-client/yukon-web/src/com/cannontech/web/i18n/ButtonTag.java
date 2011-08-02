package com.cannontech.web.i18n;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.util.ServletUtil;
import com.cannontech.web.taglib.MessageScopeHelper;
import com.cannontech.web.taglib.MessageScopeHelper.MessageScope;
import com.cannontech.web.taglib.UniqueIdentifierTag;
import com.cannontech.web.taglib.YukonTagSupport;

public class ButtonTag extends YukonTagSupport {

    protected String id = null;
    protected String key = null;
    protected String arguments = null;
    protected String href = null;
    protected String onclick = null;
    protected String styleClass = null;
    
    /* type is the actual type attribute of the generated HTML button tag
     * Possible values are 'submit', 'reset' and 'button' (default) */
    protected String type = "button";
    
    protected String name = null;
    protected String value = null;
    protected boolean imageOnRight = false;
    protected boolean disabled = false;
    
    /* Will add hint to button that it will ask for more info before doing the action.
     * Currently adds ellipsis to end of button text. */
    protected boolean dialogButton = false;
    
    /* renderMode is to describe how the button should look:
     * Possible values for renderMode are 'image', 'labeledImage' and 'button' (default) */
    protected String renderMode = "button";

    public void setId(String id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }
    
    public void setArguments(String arguments) {
        this.arguments = arguments;
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
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public void setImageOnRight(Boolean imageOnRight) {
        this.imageOnRight = imageOnRight;
    }
    
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public void setRenderMode(String renderMode) {
        this.renderMode = renderMode;
    }
    
    public void setDialogButton(Boolean dialogButton) {
        this.dialogButton = dialogButton;
    }

    @Override
    public void doTag() throws JspException, IOException, NoSuchMessageException {

        if (StringUtils.isBlank(id)) {
            id = UniqueIdentifierTag.generateIdentifier(getJspContext(), "button");
        }

        String classes = "pointer";
        
        if (!disabled) {
            classes += " hoverableImageContainer";
        }
        
        if (!renderMode.equalsIgnoreCase("image")) {
            classes += " formSubmit"; // addes padding to left and right inside button tag
        }
        
        if (renderMode.equalsIgnoreCase("labeledImage") || renderMode.equalsIgnoreCase("image")) {
            classes += " naked"; // draws a button with no border and transparent background
        }
        
        if (renderMode.equalsIgnoreCase("labeledImage")) {
            classes += " labeledImage"; // addes text decoration underline when hovering over button
        }
        
        if (renderMode.equalsIgnoreCase("image")) {
            classes += " image";
        }
        
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
            String labelText = "";
            boolean override = false;
            /* Allow localizers to override the complete button text for any specific button.
             * This means the dialogButton attribut's appending of ellipsis feature will not be respected. */
            MessageSourceResolvable overrideLabelTextResolvable = messageScope.generateResolvable(".label.override", arguments);
            labelText = getLocalMessage(overrideLabelTextResolvable, false);
            
            if (StringUtils.isBlank(labelText)) {
                MessageSourceResolvable labelTextResolvable = messageScope.generateResolvable(".label", arguments);
                labelText = getLocalMessage(labelTextResolvable, false);
                if (StringUtils.isNotBlank(labelText)) {
                    labelText = StringEscapeUtils.escapeHtml(labelText);
                }
            } else {
                override = true;
            }
            
            if (StringUtils.isBlank(imageUrl) && StringUtils.isBlank(labelText)) {
                throw new RuntimeException("at least one of .imageUrl or .label is required for " + key);
            }
            
            if (dialogButton && !override) {
                String ellipsis = getMessageSource().getMessage("yukon.web.defaults.moreInfoSuffix");
                labelText += ellipsis;
            }
            
            if ((renderMode.equalsIgnoreCase("labeledImage") || renderMode.equalsIgnoreCase("image")) 
                    && StringUtils.isBlank(imageUrl)) {
                throw new RuntimeException(".imageUrl is required for " + key + " when renderMode is 'image' or 'labeledImage'");
            }

            /* Hover Text */
            MessageSourceResolvable hoverTextResolvable = messageScope.generateResolvable(".hoverText", arguments);
            String hoverText = getLocalMessage(hoverTextResolvable, false);
            if (!StringUtils.isBlank(hoverText)) {
                hoverText = StringEscapeUtils.escapeHtml(hoverText);
            }
            
            JspWriter out = getJspContext().getOut();

            out.write("<button id=\"");
            out.write(id);
            out.write("\"");

            /* Type */
            out.write(" type=\"");
            out.write(type);
            out.write("\"");

            /* Name */
            if (name != null) {
                out.write(" name=\"");
                out.write(name);
                out.write("\"");
            }
            
            /* Value */
            if (value != null) {
                out.write(" value=\"");
                out.write(value);
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
            
            if (disabled) {
                out.write(" disabled=\"disabled\"");
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

            if (!renderMode.equalsIgnoreCase("image") && !StringUtils.isBlank(labelText)) {
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
        String imageClass = "logoImage";
        if (!disabled) {
            imageClass += " hoverableImage";
        }
        out.write("<img class=\"" + imageClass + "\" src=\"");
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