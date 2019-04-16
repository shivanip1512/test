package com.cannontech.common.bulk.service.impl;

import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.PointImportCallbackResult;
import com.cannontech.common.bulk.model.PointImportType;
import com.cannontech.common.bulk.processor.AccumulatorPointImportProcessor;
import com.cannontech.common.bulk.processor.AnalogPointImportProcessor;
import com.cannontech.common.bulk.processor.CalcAnalogPointImportProcessor;
import com.cannontech.common.bulk.processor.CalcStatusPointImportProcessor;
import com.cannontech.common.bulk.processor.PointImportProcessor;
import com.cannontech.common.bulk.processor.PointImportProcessorFactory;
import com.cannontech.common.bulk.processor.StatusPointImportProcessor;
import com.cannontech.common.bulk.service.PointImportService;
import com.cannontech.common.csvImport.ImportData;
import com.cannontech.common.events.loggers.ToolsEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.point.PointCalculation;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class PointImportServiceImpl implements PointImportService {
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private PointImportProcessorFactory pointImportProcessorFactory;
    @Autowired private ToolsEventLogService toolsEventLogService;
    @Resource(name="recentResultsCache") 
    private RecentResultsCache<BackgroundProcessResultHolder> bpRecentResultsCache;
    @Resource(name="transactionPerItemProcessor")
    private BulkProcessor bulkProcessor;
    
    public String startImport(ImportData data, PointImportType importType, Map<String, PointCalculation> calcMap, YukonUserContext userContext) {
        switch(importType) {
            case ANALOG:
                return startAnalogImport(data, userContext);
            case STATUS:
                return startStatusImport(data, userContext);
            case ACCUMULATOR:
                return startAccumulatorImport(data, userContext);
            case CALC_ANALOG:
                return startCalcAnalogImport(data, calcMap, userContext);
            case CALC_STATUS:
                return startCalcStatusImport(data, calcMap, userContext);
            default:
                throw new IllegalArgumentException("Unable to import point type \"" + importType + "\".");
        }
    }
    
    private String setUpProcessing(ImportData data, MessageSourceAccessor messageSourceAccessor, PointImportProcessor processor) {
        String resultId = getRandomId();
        PointImportCallbackResult callbackResult = new PointImportCallbackResult(resultId, data, messageSourceAccessor, toolsEventLogService);
        bpRecentResultsCache.addResult(resultId, callbackResult);
        bulkProcessor.backgroundBulkProcess(data.iterator(), processor, callbackResult);
        return resultId;
    }
    
    private String startAnalogImport(ImportData data, YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        AnalogPointImportProcessor processor = pointImportProcessorFactory.getAnalogProcessor(data.getFormat(), messageSourceAccessor);
        return setUpProcessing(data, messageSourceAccessor, processor);
    }
    
    private String startStatusImport(ImportData data, YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        StatusPointImportProcessor processor = pointImportProcessorFactory.getStatusProcessor(data.getFormat(), messageSourceAccessor);
        return setUpProcessing(data, messageSourceAccessor, processor);
    }
    
    private String startAccumulatorImport(ImportData data, YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        AccumulatorPointImportProcessor processor = pointImportProcessorFactory.getAccumulatorProcessor(data.getFormat(), messageSourceAccessor);
        return setUpProcessing(data, messageSourceAccessor, processor);
    }
    
    private String startCalcAnalogImport(ImportData data, Map<String, PointCalculation> calcMap, YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        CalcAnalogPointImportProcessor processor = pointImportProcessorFactory.getCalcAnalogProcessor(data.getFormat(), calcMap, messageSourceAccessor);
        return setUpProcessing(data, messageSourceAccessor, processor);
    }
    
    private String startCalcStatusImport(ImportData data, Map<String, PointCalculation> calcMap, YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        CalcStatusPointImportProcessor processor = pointImportProcessorFactory.getCalcStatusProcessor(data.getFormat(), calcMap, messageSourceAccessor);
        return setUpProcessing(data, messageSourceAccessor, processor);
    }
    
    private String getRandomId() {
        return StringUtils.replace(UUID.randomUUID().toString(), "-", "");
    }
}
