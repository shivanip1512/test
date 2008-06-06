package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;

@Configurable("pointValueFormatterTagPrototype")
public class PointValueFormatterTag extends YukonTagSupport {
    private PointFormattingService pointFormattingService;
    private Format format = Format.FULL;
    private PointValueHolder value;
    
    @Override
    public void doTag() throws JspException, IOException {
        if (value != null) {
            YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(getJspContext());
            String valueString = pointFormattingService.getValueString(value, format, userContext);

            JspWriter out = getJspContext().getOut();
            out.print(valueString);
        }
    }

    /**
     * @param format either a Format enum value or a string compatible with the PointFormattingService
     */
    public void setFormat(String format) {
        this.format = Format.valueOf(format);
    }
    
    public String getFormat() {
        return this.format.name();
    }
    
    public void setValue(PointValueHolder value) {
        this.value = value;
    }
    
    public PointValueHolder getValue() {
        return this.value;
    }
    
    public void setPointFormattingService(PointFormattingService pointFormattingService) {
        this.pointFormattingService = pointFormattingService;
    }

}
