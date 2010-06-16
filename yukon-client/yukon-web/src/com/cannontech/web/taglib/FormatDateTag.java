package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;

@Configurable("formatDateTagPrototype")
public class FormatDateTag extends YukonTagSupport {
//    private YukonUserDao userDao;
    private DateFormattingService dateFormattingService;
    private Object value;
    private String type;
    private DateFormatEnum enumValue;
    private String var;
    private String nullText;
    
    @Override
    public void doTag() throws JspException, IOException {
        enumValue = DateFormattingService.DateFormatEnum.valueOf(type);
        
        String formattedDate ="";
        if (value != null) {
            formattedDate = dateFormattingService.format(value,
                                                         enumValue,
                                                         getUserContext());
        }else{
        	if (nullText != null) {
        		formattedDate = nullText;
        	} else {
        		getJspContext().getOut().print("Date doesn't exist");
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
    public void setNullText(String nullText) {
		this.nullText = nullText;
	}
    public void setDateFormattingService(DateFormattingService dfs) {
        this.dateFormattingService = dfs;
    }
}
