package com.cannontech.web.i18n;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.web.taglib.MessageScopeHelper;
import com.cannontech.web.taglib.MessageScopeHelper.MessageScope;
import com.cannontech.web.taglib.UniqueIdentifierTag;
import com.cannontech.web.taglib.YukonTagSupport;

public class ButtonTag extends YukonTagSupport implements DynamicAttributes {
    
    protected String id = null;
    protected String title = null;
    protected String nameKey = null;
    protected String label = null;
    protected String arguments = null;
    protected String href = null;
    protected String onclick = null;
    protected String classes = null;
    protected String icon = null;

    /* type is the actual type attribute of the generated HTML button tag
     * Possible values are 'submit', 'reset' and 'button' (default) */
    protected String type = "button";

    protected String name = null;
    protected String value = null;
    protected boolean disabled = false;
    protected boolean busy = false;
    protected boolean blockPage = false;

    /* Will add hint to button that it will ask for more info before doing the action.
     * Currently adds ellipsis to end of button text. */
    protected boolean dialogButton = false;

    /* renderMode is to describe how the button should look:
     * Possible values for renderMode are 'image', 'labeledImage', 'imageButton', 'appButton' and 'button' (default) */
    protected String renderMode = "button";
    
    private Map<String, Object> dynamicAttributes = new HashMap<>();

    public void setId(String id) {
        this.id = id;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    public void setNameKey(String nameKey) {
        this.nameKey = nameKey;
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

    public void setClasses(String classes) {
        this.classes = classes;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
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
    
    public void setDisabled(Boolean disabled) {
        this.disabled = BooleanUtils.toBoolean(disabled);
    }
    
    public void setBusy(Boolean busy) {
        this.busy = busy;
    }
    
    public void setBlockPage(Boolean blockPage) {
        this.blockPage = blockPage;
    }

    public void setRenderMode(String renderMode) {
        this.renderMode = renderMode;
    }

    public void setDialogButton(Boolean dialogButton) {
        this.dialogButton = dialogButton;
    }

    @Override
    public void doTag() throws JspException, IOException, NoSuchMessageException {
        
        RenderMode mode = renderMode == null ? RenderMode.BUTTON : RenderMode.getByValue(renderMode);
        
        MessageScope scope = MessageScopeHelper.forRequest(getRequest());
        try {
            scope.pushScope("." + nameKey, "components.button." + nameKey);
            
            /* Button Text */
            String labelText = "";
            boolean override = false;
            /* Allow localizers to override the complete button text for any specific button.
             * This means the dialogButton attribute's appending of ellipsis feature will not be respected. */
            labelText = getLocalMessage(scope, ".label.override");
            
            if (StringUtils.isBlank(labelText)) {
                labelText = getLocalMessage(scope, ".label");
                if (StringUtils.isNotBlank(labelText)) {
                    labelText = StringEscapeUtils.escapeHtml4(labelText);
                }
            } else {
                override = true;
            }
            
            if (StringUtils.isBlank(label) && StringUtils.isBlank(labelText) && (mode == RenderMode.BUTTON || mode == RenderMode.LABELED_IMAGE)) {
                throw new RuntimeException("the label attribute or nameKey (<nameKey>.label) is required for renderMode: " + renderMode);
            }
            
            /* Always use label when provide */
            if (StringUtils.isNotBlank(label)) {
                labelText = label;
            }
            
            if (dialogButton && !override) {
                String ellipsis = getMessageSource().getMessage("yukon.common.moreInfoSuffix");
                labelText += ellipsis;
            }
            
            /* Busy Text */
            String busyText = getLocalMessage(scope, ".labelBusy");
            if (StringUtils.isNotBlank(busyText)) {
                busyText = StringEscapeUtils.escapeHtml4(busyText);
            }
            
            /* Icon */
            if ((mode == RenderMode.LABELED_IMAGE || mode == RenderMode.IMAGE) 
                    && StringUtils.isBlank(icon)) {
                throw new RuntimeException("'icon' is required when renderMode is 'image' or 'labeledImage'");
            }
            
            /* Title */
            String hoverText = null;
            if (StringUtils.isNotBlank(title)) {
                hoverText = title;
            } else {
                hoverText = getLocalMessage(scope, ".hoverText");
                if (StringUtils.isNotBlank(hoverText)) {
                    hoverText = StringEscapeUtils.escapeHtml4(hoverText);
                }
            }
            
            /* Class */
            StringBuilder classes = new StringBuilder().append("button");
            if (mode == RenderMode.LABELED_IMAGE || mode == RenderMode.IMAGE || mode == RenderMode.LABEL) {
                classes.append(" naked");
            }
            if (mode == RenderMode.APPBUTTON) {
                classes.append(" app-button");
            }
            if (StringUtils.isNotBlank(this.classes)) {
                classes.append(" " + this.classes);
            }
            if (busy) {
                classes.append(" js-disable-after-click");
            }
            
            id = StringUtils.isBlank(id) ? UniqueIdentifierTag.generateIdentifier(getJspContext(), "button") : id;

            JspWriter out = getJspContext().getOut();
            out.write("<button role=\"button\" id=\"" + id + "\" type=\"" + type + "\" class=\"" + classes.toString() + "\"");
            
            for (String attributeName : dynamicAttributes.keySet()) {
                out.write(" " + attributeName + "=\"" + dynamicAttributes.get(attributeName) + "\"");
            }
            
            if (StringUtils.isNotBlank(labelText)) {
                out.write(" aria-label=\"" + labelText + "\"");
            }
            if (StringUtils.isNotBlank(name)) {
                out.write(" name=\"" + name + "\"");
            }
            if (StringUtils.isNotBlank(value)) {
                out.write(" value=\"" + value + "\"");
            }
            if (StringUtils.isNotBlank(hoverText)) {
                out.write(" title=\"" + hoverText + "\"");
            }
            if (StringUtils.isNotBlank(onclick)) {
                out.write(" onclick=\"" + onclick + "\"");
            }
            if (StringUtils.isNotBlank(href)) {
                out.write(" data-href=\"" + href + "\"");
            }
            if (disabled) {
                out.write(" disabled=\"disabled\"");
            }
            
            if (busy) {
                if (StringUtils.isNotBlank(busyText)) {
                    out.write(" data-busy=\"" + busyText + "\""); 
                } else {
                    out.write(" data-busy");
                }
            }
            
            if (blockPage) {
                out.write(" data-block-page");
            }
            
            out.write(">");
            
            boolean hasImage = StringUtils.isNotBlank(icon);
            
            /* icon order matters here */
            if (busy) {
                out.write("<i class=\"icon busy\"></i>");
            }
            if (hasImage) {
                out.write("<i class=\"icon " + icon + "\"></i>");
            }
            
            if (mode != RenderMode.IMAGE 
                    && mode != RenderMode.BUTTON_IMAGE
                    && !StringUtils.isBlank(labelText)) {
                out.write("<span class=\"b-label\">");
                out.write(labelText);
                out.write("</span>");
            }
            
            out.write("</button>");
            
        } finally {
            scope.popScope();
        }
    }

    private String getLocalMessage(MessageScope scope, String key) {
        String retVal;
        try {
            MessageSourceResolvable resolvable = scope.generateResolvable(key, arguments);
            retVal = getMessageSource().getMessage(resolvable);
        } catch (NoSuchMessageException | IllegalArgumentException e) {
            return null;
        }
        return retVal;
    }

    /* renderMode is to describe how the button should look:
     * Possible values for renderMode are 'image', 'labeledImage', 'appButton' and 'button' (default) */
    private enum RenderMode {
        IMAGE("image"),
        LABELED_IMAGE("labeledImage"),
        BUTTON_IMAGE("buttonImage"),
        BUTTON("button"),
        LABEL("label"),
        APPBUTTON("appButton");  //Displays an App-Like button (used with 32x32 icons)

        private String attributeValue;

        private RenderMode(String attributeValue) {
            this.attributeValue = attributeValue;
        }

        private static RenderMode getByValue(String attribute) {
            for (RenderMode mode : RenderMode.values()) {
                if (mode.attributeValue.equalsIgnoreCase(attribute)) {
                    return mode;
                }
            }
            throw new IllegalArgumentException("Invalid render mode [" + attribute + "], possible values are image, labeledImage, buttonImage, and button (default)");
        }
    }

    @Override
    public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
        dynamicAttributes.put(localName, value == null ? "" : value);
    }
}