package com.cannontech.web.updater.capcontrol;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringEscapeUtils;
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
    private boolean isTypeSet;
    private String styleClass = "";
    
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
        UpdateValue value = updaterService.getFirstValue(id, getUserContext());

        JspWriter out = getJspContext().getOut();
        out.print("<span class=\"" + styleClass + "\" cannonUpdater=\"" + StringEscapeUtils.escapeHtml(value.getFullIdentifier()) + "\">");
        out.print(value.getValue());
        out.print("</span>");
    }
    
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }
    
    public void setPaoId(int paoId) {
        isPaoIdSet = true;
        this.paoId = paoId;
    }
    
    public void setType(final String type) {
        this.isTypeSet = true;
        this.type = type;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }

    @Required
    public void setUpdaterService(DataUpdaterService updaterService) {
        this.updaterService = updaterService;
    }
}
