package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.web.i18n.IconSvg;

@Configurable(value="getIconSvgTagPrototype", autowire=Autowire.BY_NAME)
public class GetIconSvgTag extends YukonTagSupport {
        
    private String iconClass;

    @Override
    public void doTag() throws IOException {
        String iconSvg = IconSvg.getIconForClass(iconClass).getSvg();
        JspWriter out = getJspContext().getOut();
        out.write(iconSvg);
    }

    public String getIconClass() {
        return iconClass;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }
}