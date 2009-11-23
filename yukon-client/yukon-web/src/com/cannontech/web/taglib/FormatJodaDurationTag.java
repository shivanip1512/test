package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DurationFormatEnum;

@Configurable("formatDateTagPrototype")
public class FormatJodaDurationTag extends YukonTagSupport {
    private DateFormattingService dateFormattingService;
    private Object value;
    private String type;
    private String var;
    private String defaultText;

    @Override
    public void doTag() throws JspException, IOException {
        DurationFormatEnum enumType = DurationFormatEnum.valueOf(type);

        String formattedDate = "";
        if (value != null) {
            formattedDate = dateFormattingService.formatDuration(value,
                                                                 enumType,
                                                                 getUserContext());
        } else {
            if (defaultText != null) {
                formattedDate = defaultText;
            } else {
                getJspContext().getOut().print("Duration doesn't exist");
            }
        }

        if (var == null) {
            getJspContext().getOut().print(formattedDate);
        } else {
            this.getJspContext().setAttribute(var, formattedDate);
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
