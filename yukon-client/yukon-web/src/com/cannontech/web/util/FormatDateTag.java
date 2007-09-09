package com.cannontech.web.util;

import java.io.IOException;
import java.util.Date;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.taglib.YukonTagSupport;

@Configurable("formatDateTagPrototype")
public class FormatDateTag extends YukonTagSupport {
//    private YukonUserDao userDao;
    private DateFormattingService dateFormattingService;
    private Date value;
    private String type;
    private DateFormatEnum enumValue;
    private String var;
    
    @Override
    public void doTag() throws JspException, IOException {
        final LiteYukonUser user = this.getYukonUser();
      
        enumValue = DateFormattingService.DateFormatEnum.valueOf(type);
        
        String formattedDate ="";
        if (value != null) {
            formattedDate = dateFormattingService.formatDate(value,
                                                             enumValue,
                                                             user);
        }else{
            getJspContext().getOut().print("Date doesn't exist");
        }
        
        if (var == null) {
            getJspContext().getOut().print(formattedDate);
        } else {
            this.getJspContext().setAttribute(var, formattedDate);
        }
    }

    public void setValue(final Date value) {
        this.value = value;
    }

    public void setType(final String type) {
        this.type = type;
    }
   
    public void setVar(final String var) {
        this.var = var;
    }
    public void setDateFormattingService(DateFormattingService dfs) {
        this.dateFormattingService = dfs;
    }
}
