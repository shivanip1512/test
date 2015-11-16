package com.cannontech.web.i18n;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.FormattingTemplateProcessor;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.web.taglib.YukonTagSupport;

@Configurable("formatTemplateTagPrototype")
public class FormatTemplateTag extends YukonTagSupport {
    private TemplateProcessorFactory templateProcessorFactory;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    private ResolvableTemplate resolvableTemplate;
    
    @Override
    public void doTag() throws JspException, IOException {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(getUserContext());
        String template = messageSourceAccessor.getMessage(resolvableTemplate.getCode());
        FormattingTemplateProcessor templateProcessor = templateProcessorFactory.getFormattingTemplateProcessor(getUserContext());
        String result = templateProcessor.process(template, resolvableTemplate.getData());
        getJspContext().getOut().write(result);
    }
    
    public void setTemplateProcessorFactory(
            TemplateProcessorFactory templateProcessorFactory) {
        this.templateProcessorFactory = templateProcessorFactory;
    }
    
    public void setMessageSourceResolver(
            YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }

    public ResolvableTemplate getMessage() {
        return resolvableTemplate;
    }

    public void setMessage(ResolvableTemplate resolvableTemplate) {
        this.resolvableTemplate = resolvableTemplate;
    }
}
