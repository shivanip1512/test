package com.cannontech.web.updater.capcontrol;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.web.taglib.YukonTagSupport;
import com.cannontech.web.updater.DataType;
import com.cannontech.web.updater.DataUpdaterService;
import com.cannontech.web.updater.UpdateValue;

@Configurable("capControlValueTagPrototype")
public class CapControlValueTag extends YukonTagSupport {
    
    private DataUpdaterService updaterService;
    private int paoId;
    private boolean isPaoIdSet;
    private String format;
    private String type;
    private String styleClass;
    private boolean isTypeSet;
    private boolean initialize = true;
    private boolean defaultBlank;
    
    @Override
    public void doTag() throws JspException, IOException {
        if (!isPaoIdSet) {
            throw new JspException("paoId must be set");
        }

        if (!isTypeSet) {
            throw new JspException("type must be set");
        }
        
        DataType dataType = DataType.valueOf(type);
        
        String id = dataType + "/" + paoId + "/" + format;

        JspWriter out = getJspContext().getOut();
        out.print("<span data-updater=\"" + StringEscapeUtils.escapeHtml4(id) + "\"");
        if (StringUtils.isNotBlank(styleClass)) {
            out.print(" class=\"" + styleClass + "\"");
        }
        out.print(">");
        if (initialize) {
            UpdateValue value = updaterService.getFirstValue(id, getUserContext());
            out.print(StringEscapeUtils.escapeHtml4(value.getValue()));
        } else if (!defaultBlank){
            out.print(getMessageSource().getMessage("yukon.common.point.pointFormatting.unavailablePlaceholder"));
        }
        out.print("</span>");
    }
    
    public void setPaoId(int paoId) {
        isPaoIdSet = true;
        this.paoId = paoId;
    }
    
    public void setType(final String type) {
        isTypeSet = true;
        this.type = type;
    }
    
    public void setFormat(String format) {
    	this.format = format;
    }
    
    public void setInitialize(boolean initialize) {
        this.initialize = initialize;
    }

    public void setDefaultBlank(boolean defaultBlank) {
        this.defaultBlank = defaultBlank;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    @Required
    public void setUpdaterService(DataUpdaterService updaterService) {
        this.updaterService = updaterService;
    }
}
