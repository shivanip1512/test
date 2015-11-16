package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.Date;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;

@Configurable("formatMillisTagPrototype")
public class FormatMillisTag extends YukonTagSupport {
    private DateFormattingService dateFormattingService;
    private Long value;
    private String type;
    private DateFormatEnum enumValue;
    private String var;
    private String invalidStr = "";
    
    @Override
    public void doTag() throws JspException, IOException {
        enumValue = DateFormattingService.DateFormatEnum.valueOf(type);
        
        String formattedDate = invalidStr;
        if (value != null) {
            
            if( value > 0) {   //0 is "epoch time", treat as a null value.
                final Date valueDate = new Date(value);
                formattedDate = dateFormattingService.format(valueDate,
                                                             enumValue,
                                                             getUserContext());
            }
        }else{
            getJspContext().getOut().print("Date doesn't exist");
        }
        
        if (var == null) {
            getJspContext().getOut().print(formattedDate);
        } else {
            this.getJspContext().setAttribute(var, formattedDate);
        }
    }

    public void setValue(final Long value) {
        this.value = value;
    }

    public void setType(final String type) {
        this.type = type;
    }
   
    public void setVar(final String var) {
        this.var = var;
    }
    
    public void setInvalidStr(String invalidStr) {
        this.invalidStr = invalidStr;
    }
    
    public void setDateFormattingService(DateFormattingService dfs) {
        this.dateFormattingService = dfs;
    }
}
