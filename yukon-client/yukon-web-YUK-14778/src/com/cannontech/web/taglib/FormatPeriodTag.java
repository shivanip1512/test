package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.joda.time.ReadableDuration;
import org.joda.time.ReadablePeriod;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.core.service.durationFormatter.DurationFormat;

@Configurable("formatPeriodTagPrototype")
public class FormatPeriodTag extends YukonTagSupport {
    private DurationFormattingService durationFormattingService;
    private Object value;
    private String type;
    private String var;
    private String defaultText;

    @Override
    public void doTag() throws JspException, IOException {
        DurationFormat enumType = DurationFormat.valueOf(type);

        String formattedPeriod = "";
        if (value != null) {
            if (value instanceof ReadablePeriod) {
                formattedPeriod = durationFormattingService.formatPeriod((ReadablePeriod)value, enumType, getUserContext());
            } else if (value instanceof ReadableDuration) {
                formattedPeriod = durationFormattingService.formatDuration((ReadableDuration)value, enumType, getUserContext());
            }
        } else {
            if (defaultText != null) {
                formattedPeriod = defaultText;
            } else {
                getJspContext().getOut().print("Duration doesn't exist");
            }
        }

        if (var == null) {
            getJspContext().getOut().print(formattedPeriod);
        } else {
            this.getJspContext().setAttribute(var, formattedPeriod);
        }
    }

    public void setValue(final Object value) {
        this.value = value;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public void setVar(final String var) {
        this.var = var;
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }

    public void setDurationFormattingService(DurationFormattingService durationFormattingService) {
        this.durationFormattingService = durationFormattingService;
    }
    
}