package com.cannontech.common.util;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class TemplateProcessorFactory {
    private DateFormattingService dateFormattingService;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @Autowired
    public void setDateFormattingService(
            DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    public FormattingTemplateProcessor getFormattingTemplateProcessor(YukonUserContext userContext) {
        FormattingTemplateProcessor templateProcessor = new FormattingTemplateProcessor(dateFormattingService, userContext);
        return templateProcessor;
    }
    
    public String processResolvableTemplate(ResolvableTemplate resolvableTemplate, 
                                            YukonUserContext userContext) {
        
        MessageSourceAccessor messageSourceAccessor = 
            messageSourceResolver.getMessageSourceAccessor(userContext);
        
        String template = messageSourceAccessor.getMessage(resolvableTemplate.getCode());
        FormattingTemplateProcessor templateProcessor = getFormattingTemplateProcessor(userContext);
        String result = templateProcessor.process(template, resolvableTemplate.getData());

        return result;
    }

}
