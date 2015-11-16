package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.joda.time.Instant;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;

@Configurable(value="formatIntervalTagPrototype", autowire=Autowire.BY_NAME)
public class FormatIntervalTag extends YukonTagSupport {
    private DateFormattingService dateFormattingService;
    private Object value;
    private Long start;
    private Long end;
    private String type;
    private DateFormatEnum enumValue;
    private String var;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @Override
    public void doTag() throws JspException, IOException {
        enumValue = DateFormattingService.DateFormatEnum.valueOf(type);
        
        String formattedStart ="";
        String formattedEnd ="";
        if (value == null  && (start == null || end == null)) {
            getJspContext().getOut().print("Interval doesn't exist");
        } else {
            if (value != null) {
                Interval interval = (Interval) value;
                start = interval.getStartMillis();
                end = interval.getEndMillis();
            }
            formattedStart = dateFormattingService.format(new Instant(start), enumValue, getUserContext());
            formattedEnd = dateFormattingService.format(new Instant(end), enumValue, getUserContext());
        
            YukonUserContext context = YukonUserContextUtils.getYukonUserContext(getJspContext());
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
            String intervalSeparator = messageSourceAccessor.getMessage("yukon.common.dateFormatting.intervalSeparator");
            if (var == null) {
                getJspContext().getOut().print(formattedStart + intervalSeparator + formattedEnd);
            } else {
                getJspContext().setAttribute(var, formattedStart + intervalSeparator + formattedEnd);
            }
        }
    }

    public void setValue(final Object value) {
        this.value = value;
    }
    
    public void setStart(Long start) {
        this.start = start;
    }
    
    public void setEnd(Long end) {
        this.end = end;
    }

    public void setType(final String type) {
        this.type = type;
    }
   
    public void setVar(final String var) {
        this.var = var;
    }

    @Autowired
    public void setDateFormattingService(DateFormattingService dfs) {
        this.dateFormattingService = dfs;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
}