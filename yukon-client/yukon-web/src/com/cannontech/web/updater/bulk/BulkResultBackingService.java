package com.cannontech.web.updater.bulk;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.bulk.service.BulkOperationCallbackResults;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.FormattingTemplateProcessor;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;

public class BulkResultBackingService implements UpdateBackingService {

    private RecentResultsCache<BulkOperationCallbackResults<?>> recentResultsCache = null;
    private YukonUserContextMessageSourceResolver messageSourceResolver = null;
    private TemplateProcessorFactory templateProcessorFactory;
    
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {

        // split identifier
        String[] idParts = StringUtils.split(identifier, "/");
        String requestId = idParts[0];
        String resultTypeStr = idParts[1];
        
        // get result
        BulkOperationCallbackResults<?> bulkOperationCallbackResults = recentResultsCache.getResult(requestId);
        
        if (bulkOperationCallbackResults == null) {
            return "";
        }
        
        BulkResultTypeEnum bulkResultTypeEnum = BulkResultTypeEnum.valueOf(resultTypeStr);
        Object value = bulkResultTypeEnum.getValue(bulkOperationCallbackResults);
        
        // check instanceof ResolvableTemplate, handles as such, else call toString
        if (value instanceof ResolvableTemplate) {
            
            ResolvableTemplate resolvableValue = (ResolvableTemplate)value;
            
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String template = messageSourceAccessor.getMessage(resolvableValue.getCode());
            FormattingTemplateProcessor templateProcessor = templateProcessorFactory.getFormattingTemplateProcessor(userContext);
            return templateProcessor.process(template, resolvableValue.getData());
        }
        
        return value.toString();
    }

    @Required
    public void setRecentBulkOperationResultsCache(RecentResultsCache<BulkOperationCallbackResults<?>> recentResultsCache) {
        this.recentResultsCache = recentResultsCache;
    }
    
    @Required
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Required
    public void setTemplateProcessorFactory(
            TemplateProcessorFactory templateProcessorFactory) {
        this.templateProcessorFactory = templateProcessorFactory;
    }
}
