package com.cannontech.common.bulk.service.impl;

import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.TranslationImportCallbackResult;
import com.cannontech.common.bulk.model.FdrImportDataRow;
import com.cannontech.common.bulk.model.FdrImportFileInterfaceInfo;
import com.cannontech.common.bulk.model.FdrInterfaceDisplayable;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.bulk.processor.SingleProcessor;
import com.cannontech.common.bulk.service.FdrCsvHeader;
import com.cannontech.common.bulk.service.FdrImportAction;
import com.cannontech.common.bulk.service.FdrTranslationManagerCsvHelper;
import com.cannontech.common.bulk.service.FdrTranslationManagerService;
import com.cannontech.common.exception.ImportFileFormatException;
import com.cannontech.common.fdr.FdrDirection;
import com.cannontech.common.fdr.FdrInterfaceOption;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrOptionType;
import com.cannontech.common.fdr.FdrTranslation;
import com.cannontech.common.fdr.FdrUtils;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.dao.FdrTranslationDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;


public class FdrTranslationManagerServiceImpl implements FdrTranslationManagerService {
    private FdrTranslationManagerCsvHelper fdrTranslationManagerCsvHelper;
    private Logger log = YukonLogManager.getLogger(FdrTranslationManagerServiceImpl.class);
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private RecentResultsCache<BackgroundProcessResultHolder> bpRecentResultsCache;
    private BulkProcessor bulkProcessor = null;
    private PaoDao paoDao;
    private PointDao pointDao;
    private FdrTranslationDao fdrTranslationDao;
    
    @Override
    public String startImport(List<String> headers, List<Integer> columnsToIgnore, List<String[]> fileLines, YukonUserContext userContext) {
        SingleProcessor<String[]> translationProcessor = getTranslationProcessor(headers, columnsToIgnore, userContext);
        String resultsId = startProcessor(fileLines, headers, translationProcessor, userContext);
        
        return resultsId;
    }
    
    @Override
    public List<FdrTranslation> getFilteredTranslationList(String filterString) {
        List<FdrTranslation> filteredTranslationsList;
        if(filterString.equals("AllInterfaces")) {
            filteredTranslationsList = fdrTranslationDao.getAllTranslations();
        } else {
            FdrInterfaceType selectedInterfaceType = FdrInterfaceType.valueOf(filterString);
            filteredTranslationsList = fdrTranslationDao.getByInterfaceType(selectedInterfaceType);
        }
        return filteredTranslationsList;
    }
    
    @Override
    public List<FdrInterfaceDisplayable> getAllInterfaceDisplayables(YukonUserContext userContext) {
        List<FdrInterfaceDisplayable> displayables = Lists.newArrayList();
        
        FdrInterfaceType[] allFdrInterfaces = FdrInterfaceType.values();
        
        for(FdrInterfaceType fdrInterface : allFdrInterfaces) {
            FdrInterfaceDisplayable displayable = new FdrInterfaceDisplayable(fdrInterface.toString());

            for(FdrInterfaceOption option : fdrInterface.getInterfaceOptions()) {
                MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
                String description;
                if(option.getOptionType() == FdrOptionType.TEXT) {
                    description = messageSourceAccessor.getMessage("yukon.web.modules.tools.bulk.fdrTranslationManagement.columns." + option);
                } else {
                    String optionText = messageSourceAccessor.getMessage("yukon.web.modules.tools.bulk.fdrTranslationManagement.columns.optionText");
                    description = optionText + option.getOptionValuesString();
                }
                displayable.addColumnAndDescription(option.toString(), description);
            }
            
            displayables.add(displayable);
        }
        
        return displayables;
    }
    
    @Override
    public FdrImportFileInterfaceInfo getInterfaceInfo(List<String> headers, boolean ignoreInvalidColumns) throws ImportFileFormatException {
        FdrImportFileInterfaceInfo interfaceInfo = new FdrImportFileInterfaceInfo();
        
        for(String header : headers) {
            if(fdrTranslationManagerCsvHelper.matchesDefaultColumn(header)) {
                //Default column. Move on to the next header.
            } else {
                try {
                    FdrInterfaceOption option = FdrInterfaceOption.valueOf(header);
                    FdrInterfaceType fdrInterface = FdrUtils.OPTION_TO_INTERFACE_MAP.get(option);
                    interfaceInfo.addInterface(fdrInterface);
                } catch(IllegalArgumentException e) {
                    //Header doesn't match any interface and isn't a default
                    if(ignoreInvalidColumns) {
                        int colIndex = headers.indexOf(header);
                        interfaceInfo.addColumnToIgnore(colIndex);
                        log.warn("FDR Translation Import ignoring column \"" + header + "\" - not a recognized default or interface-specific column.");
                    } else {
                        ImportFileFormatException exception = new ImportFileFormatException("Column header \"" + header + "\" doesn't match any interface and isn't a default header.");
                        exception.setHeaderName(header);
                        throw exception;
                    }
                }
            }
        }
        
        return interfaceInfo;
    }
    
    private String startProcessor(List<String[]> fileLines, List<String> headers, Processor<String[]> processor, YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String resultsId = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        TranslationImportCallbackResult callbackResult = new TranslationImportCallbackResult(resultsId, headers, fileLines, messageSourceAccessor);
        bpRecentResultsCache.addResult(resultsId, callbackResult);
        
        bulkProcessor.backgroundBulkProcess(fileLines.iterator(), processor, callbackResult);
        
        return resultsId;
    }
    
    private SingleProcessor<String[]> getTranslationProcessor(final List<String> headers, final List<Integer> columnsToIgnore, final YukonUserContext userContext) {
        SingleProcessor<String[]> translationProcessor = new SingleProcessor<String[]>() {
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            
            @Override
            public void process(String[] line) throws ProcessingException { 
                //parse data array into import object
                FdrImportDataRow dataRow = new FdrImportDataRow();
                for(int i = 0; i < line.length; i++) {
                    if(columnsToIgnore.contains(i)) continue; //ignored column, skip to next
                    String header = headers.get(i);
                    String columnValue = line[i];
                    if(columnValue.equalsIgnoreCase("")) continue; //no value present, skip to next
                    
                    if(columnValue.equalsIgnoreCase(FdrUtils.EMPTY)){
                        columnValue = line[i] = "";
                    }
                    
                    boolean isDefaultColumn = false;
                    for(FdrCsvHeader defaultHeader : FdrCsvHeader.values()) {
                        if(defaultHeader.toString().equals(header)) {
                            dataRow.setDefaultColumn(defaultHeader, columnValue);
                            isDefaultColumn = true;
                        }
                    }
                    if(!isDefaultColumn) {
                        //interface-specific column
                        try {
                            FdrInterfaceOption column = FdrInterfaceOption.valueOf(header);
                            boolean insertSucceeded = dataRow.setInterfaceColumn(column, columnValue);
                            if(!insertSucceeded) {
                                //Throw exception - option doesn't match fdr interface
                                String error = messageSourceAccessor.getMessage("yukon.web.modules.tools.bulk.fdrTranslationManagement.error.invalidColumnForInterface", header, dataRow.getInterface().toString());
                                throw new ProcessingException(error);
                            }
                        } catch(IllegalArgumentException e) {
                            //Throw exception - not a valid interface column
                            String error = messageSourceAccessor.getMessage("yukon.web.modules.tools.bulk.fdrTranslationManagement.error.noValidInterface", columnValue);
                            throw new ProcessingException(error, e);
                        }  
                    }
                }
                
                //Validate data object
                if(dataRow.getInterface() == null) {
                    String error = messageSourceAccessor.getMessage("yukon.web.modules.tools.bulk.fdrTranslationManagement.error.noInterfaceColumns");
                    throw new ProcessingException(error);
                }
                String missingColumn = dataRow.getMissingColumn();
                if(missingColumn != null) {
                    String error = messageSourceAccessor.getMessage("yukon.web.modules.tools.bulk.fdrTranslationManagement.error.missingAnyColumn", missingColumn);
                    throw new ProcessingException(error);
                }
                
                //Attempt to find the device and point to attach this translation to.
                //Generate an error if lookup fails along the way
                PaoType paoType;
                try {
                    paoType = PaoType.getForDbString(dataRow.getDeviceType());
                } catch(IllegalArgumentException e) {
                    String error = messageSourceAccessor.getMessage("yukon.web.modules.tools.bulk.fdrTranslationManagement.error.invalidDeviceType", dataRow.getDeviceType());
                    throw new ProcessingException(error, e);
                }
                YukonPao pao = paoDao.findYukonPao(dataRow.getDeviceName(), paoType);
                if(pao == null) {
                    String error = messageSourceAccessor.getMessage("yukon.web.modules.tools.bulk.fdrTranslationManagement.error.deviceNotFound", dataRow.getDeviceName(), paoType);
                    throw new ProcessingException(error);
                }
                LitePoint point = pointDao.findPointByName(pao, dataRow.getPointName());
                if(point == null) {
                    String error = messageSourceAccessor.getMessage("yukon.web.modules.tools.bulk.fdrTranslationManagement.error.pointNotFound", dataRow.getPointName(), dataRow.getDeviceName());
                    throw new ProcessingException(error);
                }
                FdrDirection fdrDirection = FdrDirection.getEnum(dataRow.getDirection());
                if(fdrDirection == null) {
                    String error = messageSourceAccessor.getMessage("yukon.web.modules.tools.bulk.fdrTranslationManagement.error.invalidDirection", dataRow.getDirection());
                    throw new ProcessingException(error);
                }
                List<FdrDirection> supportedDirections = dataRow.getInterface().getSupportedDirectionsList();
                if(!supportedDirections.contains(fdrDirection)) {
                    String error = messageSourceAccessor.getMessage("yukon.web.modules.tools.bulk.fdrTranslationManagement.error.unsupportedDirection", fdrDirection, dataRow.getInterface());
                    throw new ProcessingException(error);
                }
                                
                //build translation
                FdrTranslation translation = new FdrTranslation();
                String translationString = "";
                for(Entry<FdrInterfaceOption, String> entry : dataRow.getInterfaceColumns().entrySet()) {
                    translationString += entry.getKey().getOptionLabel() + ":" + entry.getValue() + ";";
                    translation.getParameterMap().put(entry.getKey().getOptionLabel(), entry.getValue());
                }
                translationString += "POINTTYPE:" + point.getPointTypeEnum().toString() + ";";
                
                translation.setTranslation(translationString);
                translation.setDirection(fdrDirection);
                translation.setPointId(point.getPointID());
                translation.setInterfaceType(dataRow.getInterface());
                
                //Attempt to perform add or remove operation
                if(dataRow.getAction().equalsIgnoreCase(FdrImportAction.ADD.toString())) {
                    try {
                        fdrTranslationDao.add(translation);
                    } catch(DataIntegrityViolationException e) {
                        String error = messageSourceAccessor.getMessage("yukon.web.modules.tools.bulk.fdrTranslationManagement.error.unableToInsert");
                        throw new ProcessingException(error, e);
                    }
                } else if(dataRow.getAction().equalsIgnoreCase(FdrImportAction.REMOVE.toString())) {
                    boolean success = fdrTranslationDao.delete(translation);
                    if(!success) {
                        String error = messageSourceAccessor.getMessage("yukon.web.modules.tools.bulk.fdrTranslationManagement.error.unableToRemove");
                        throw new ProcessingException(error);
                    }
                } else {
                    String error = messageSourceAccessor.getMessage("yukon.web.modules.tools.bulk.fdrTranslationManagement.error.invalidAction");
                    throw new ProcessingException(error);
                }
            }
        };
        
        return translationProcessor;
    }
    
    @Resource(name="recentResultsCache")
    public void setBackgroundProcessRecentResultsCache(RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache) {
        this.bpRecentResultsCache = recentResultsCache;
    }
    
    @Resource(name="transactionPerItemProcessor")
    public void setBulkProcessor(BulkProcessor bulkProcessor) {
        this.bulkProcessor = bulkProcessor;
    }
    
    @Autowired
    public void setFdrTranslationManagerCsvHelper(FdrTranslationManagerCsvHelper fdrTranslationManagerCsvHelper) {
        this.fdrTranslationManagerCsvHelper = fdrTranslationManagerCsvHelper;
    }
    
    @Autowired
    public void setFdrTranslationDao(FdrTranslationDao fdrTranslationDao) {
        this.fdrTranslationDao = fdrTranslationDao;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
}
