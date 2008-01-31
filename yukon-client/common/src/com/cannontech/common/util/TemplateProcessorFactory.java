package com.cannontech.common.util;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.user.YukonUserContext;

public class TemplateProcessorFactory {
    private DateFormattingService dateFormattingService;
    
    @Autowired
    public void setDateFormattingService(
            DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    
    public FormattingTemplateProcessor getFormattingTemplateProcessor(YukonUserContext userContext) {
        FormattingTemplateProcessor templateProcessor = new FormattingTemplateProcessor(dateFormattingService, userContext);
        return templateProcessor;
    }

}
