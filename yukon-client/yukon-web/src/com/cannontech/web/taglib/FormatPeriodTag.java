package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.PeriodFormatEnum;

@Configurable("formatDateTagPrototype")
public class FormatPeriodTag extends YukonTagSupport {
    private DateFormattingService dateFormattingService;
    private Object value;
    private String type;
    private String var;
    private String defaultText;

    @Override
    public void doTag() throws JspException, IOException {
        PeriodFormatEnum enumType = PeriodFormatEnum.valueOf(type);

        String formattedPeriod = "";
        if (value != null) {
            formattedPeriod = dateFormattingService.formatPeriod(value,
                                                               enumType,
                                                               getUserContext());
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

    public void setDateFormattingService(DateFormattingService dfs) {
        this.dateFormattingService = dfs;
    }
}
