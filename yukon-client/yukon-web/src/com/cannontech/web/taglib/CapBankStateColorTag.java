package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.web.updater.DataType;
import com.cannontech.web.updater.DataUpdaterService;
import com.cannontech.web.updater.UpdateValue;

@Configurable("capBankStateColorTagPrototype")
public class CapBankStateColorTag extends YukonTagSupport {
    private DataUpdaterService updaterService;
    private int paoId;
    private boolean isPaoIdSet;
    private String type;
    private boolean isTypeSet;
    private String format;
    
    @Override
    public void doTag() throws JspException ,IOException {
        if (!isPaoIdSet) {
            throw new JspException("paoId must be set");
        }

        if (!isTypeSet) {
            throw new JspException("type must be set");
        }
        
        DataType dataType = DataType.valueOf(type);
        
        String id = dataType + "/" + paoId + "/" + format;
        UpdateValue value = updaterService.getFirstValue(id, getUserContext());
        final String color = value.getValue();
        
        final StringBuilder stateBoxContentBuilder = new StringBuilder();
        stateBoxContentBuilder.append("<span id=\"data-color-updater_" + paoId + "\" ");
        stateBoxContentBuilder.append("class=\"box state-box\" ");
        stateBoxContentBuilder.append("style=\"background-color: " + color + "\" ");
        stateBoxContentBuilder.append("data-color-updater=\""
                + StringEscapeUtils.escapeHtml4(value.getIdentifier().getFullIdentifier()) + "\" data-format=\"background\" >");
        stateBoxContentBuilder.append("</span>\n");
        String stateBoxContent = stateBoxContentBuilder.toString();

        final JspWriter writer = getJspContext().getOut();
        writer.print(stateBoxContent);
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
