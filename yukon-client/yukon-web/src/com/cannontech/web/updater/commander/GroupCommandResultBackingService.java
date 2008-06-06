package com.cannontech.web.updater.commander;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.FormattingTemplateProcessor;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;

public class GroupCommandResultBackingService implements UpdateBackingService {

    private GroupCommandExecutor groupCommandExecutor = null;
    private YukonUserContextMessageSourceResolver messageSourceResolver = null;
    private TemplateProcessorFactory templateProcessorFactory;
    
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {

        // split identifier
        String[] idParts = StringUtils.split(identifier, "/", 2);
        String resultKey = idParts[0];
        String resultTypeStr = idParts[1];
        
        // get result
        GroupCommandResult result = groupCommandExecutor.getResult(resultKey);
        
        // get count
        GroupCommandResultFieldEnum fieldEnum = GroupCommandResultFieldEnum.valueOf(resultTypeStr);
        Object fieldValue = fieldEnum.getField(result);
        
        // check instanceof ResolvableTemplate, handles as such, else call toString
        if (fieldValue instanceof ResolvableTemplate) {
            
            ResolvableTemplate resolvableValue = (ResolvableTemplate)fieldValue;
            
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String template = messageSourceAccessor.getMessage(resolvableValue.getCode());
            FormattingTemplateProcessor templateProcessor = templateProcessorFactory.getFormattingTemplateProcessor(userContext);
            return templateProcessor.process(template, resolvableValue.getData());
        }
        
        return fieldValue.toString();
    }

    @Autowired
    public void setGroupCommandExecutor(GroupCommandExecutor groupCommandExecutor) {
        this.groupCommandExecutor = groupCommandExecutor;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setTemplateProcessorFactory(
            TemplateProcessorFactory templateProcessorFactory) {
        this.templateProcessorFactory = templateProcessorFactory;
    }
}
