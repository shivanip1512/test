package com.cannontech.common.bulk.service.impl;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.TranslationImportCallbackResult;
import com.cannontech.common.bulk.model.FdrImportFileInterfaceInfo;
import com.cannontech.common.bulk.model.FdrInterfaceDisplayable;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.bulk.processor.SingleProcessor;
import com.cannontech.common.bulk.service.FdrTranslationManagerCsvHelper;
import com.cannontech.common.bulk.service.FdrTranslationManagerService;
import com.cannontech.common.exception.ImportFileFormatException;
import com.cannontech.common.fdr.FdrDirection;
import com.cannontech.common.fdr.FdrInterfaceOption;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrOptionType;
import com.cannontech.common.fdr.FdrTranslation;
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
    
    private static final String ADD = "ADD";
    private static final String REMOVE = "REMOVE";
    
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
                String description = "";
                if(option.getOptionType() == FdrOptionType.TEXT) {
                    description = messageSourceAccessor.getMessage("yukon.web.modules.amr.fdrTranslationManagement.columns." + option);
                } else if(option.getOptionType() == FdrOptionType.COMBO) {
                    String optionText = messageSourceAccessor.getMessage("yukon.web.modules.amr.fdrTranslationManagement.columns.optionText");
                    description = optionText + description;
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
        
        List<FdrInterfaceType> allFdrInterfaces = FdrInterfaceType.valuesList();
        
        for(String header : headers) {
            boolean headerMatchFound = false;
            
            for(FdrInterfaceType fdrInterface : allFdrInterfaces) {
                if(fdrTranslationManagerCsvHelper.matchesDefaultColumn(header)) {
                    //Default column. Move on to the next header.
                    headerMatchFound = true;
                    break;
                } else if(header.startsWith(fdrInterface.toString() + "_")) {
                    if(interfaceInfo.getInterfaces().contains(fdrInterface)) {
                        //Interface found, but already added. Move on to the next header.
                        headerMatchFound = true;
                        break;
                    } else {
                        interfaceInfo.addInterface(fdrInterface);
                        headerMatchFound = true;
                        break;
                    }
                }
            }
            if(!headerMatchFound) {
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
        final List<FdrInterfaceType> allFdrInterfaces = FdrInterfaceType.valuesList();
        
        SingleProcessor<String[]> translationProcessor = new SingleProcessor<String[]>() {
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            
            @Override
            public void process(String[] line) throws ProcessingException {
                String deviceName = null;
                String deviceType = null;
                String pointName = null;
                String direction = null;
                String action = null;
                
                FdrInterfaceType fdrInterface = null;
                List<FdrInterfaceOption> interfaceOptions = Lists.newArrayList();
                String[] interfaceOptionValues = new String[0];
                
                //iterate over columns (checking if they're ignored) 
                for(int i = 0; i < line.length; i++) {
                    if(columnsToIgnore.contains(i)) continue; //ignored column, skip to next
                    
                    String header = headers.get(i);
                    String columnValue = line[i];
                    
                    if(columnValue.equalsIgnoreCase("")) continue; //no value present, skip to next
                    
                    if(header.equals(FdrTranslationManagerCsvHelper.DEVICE_NAME)) {
                        deviceName = columnValue;
                    } else if(header.equals(FdrTranslationManagerCsvHelper.DEVICE_TYPE)) {
                        deviceType = columnValue;
                    } else if(header.equals(FdrTranslationManagerCsvHelper.POINT_NAME)) {
                        pointName = columnValue;
                    } else if(header.equals(FdrTranslationManagerCsvHelper.DIRECTION)) {
                        direction = columnValue;
                    } else if(header.equals(FdrTranslationManagerCsvHelper.ACTION)) {
                        action = columnValue;
                    } else {
                        //interface-specific column
                        //if interface hasn't been determined yet, parse column name to get it
                        if(fdrInterface == null) {
                            for(FdrInterfaceType currentInterface : allFdrInterfaces) {
                                String thisInterfaceName = currentInterface.toString();
                                if(header.startsWith(thisInterfaceName + "_")) {
                                    fdrInterface = currentInterface;
                                    interfaceOptions = FdrInterfaceOption.getByInterface(currentInterface);
                                    interfaceOptionValues = new String[interfaceOptions.size()];
                                    break;
                                }
                            }
                            if(fdrInterface == null) {
                                String error = messageSourceAccessor.getMessage("yukon.web.modules.amr.fdrTranslationManagement.error.noValidInterface", columnValue);
                                throw new ProcessingException(error);
                            }
                        }
                        
                        //find an option that matches
                        boolean foundMatch = false;
                        for(int j = 0; j < interfaceOptions.size(); j++) {
                            String formattedOptionName = interfaceOptions.get(j).toString();
                            if(header.equals(formattedOptionName)) {
                                //insert value in option values array
                                interfaceOptionValues[j] = columnValue;
                                foundMatch = true;
                                break;
                            }
                        }
                        if(!foundMatch) {
                            String error = messageSourceAccessor.getMessage("yukon.web.modules.amr.fdrTranslationManagement.error.invalidColumnForInterface", header, fdrInterface.toString());
                            throw new ProcessingException(error);
                        }
                    }
                }
                
                //make sure all required columns are filled or fail
                String missingColumn = null;
                
                if(deviceName == null){
                    missingColumn = FdrTranslationManagerCsvHelper.DEVICE_NAME;
                } else if(deviceType == null) {
                    missingColumn = FdrTranslationManagerCsvHelper.DEVICE_TYPE;
                } else if(pointName == null) {
                    missingColumn = FdrTranslationManagerCsvHelper.POINT_NAME;
                } else if(direction == null) {
                    missingColumn = FdrTranslationManagerCsvHelper.DIRECTION;
                } else if(action == null) {
                    missingColumn = FdrTranslationManagerCsvHelper.ACTION;
                } else {
                    for(int i = 0; i < interfaceOptions.size(); i++) {
                        if(interfaceOptionValues[i] == null) {
                            missingColumn = interfaceOptions.get(i).toString();
                            break;
                        }
                    }
                }
                if(missingColumn != null) {
                    String error = messageSourceAccessor.getMessage("yukon.web.modules.amr.fdrTranslationManagement.error.missingAnyColumn", missingColumn);
                    throw new ProcessingException(error);
                }
                //find point from device name, type, point name or fail
                PaoType paoType = PaoType.getForDbString(deviceType);
                YukonPao pao = paoDao.findYukonPao(deviceName, paoType);
                if(pao == null) {
                    String error = messageSourceAccessor.getMessage("yukon.web.modules.amr.fdrTranslationManagement.error.deviceNotFound", deviceName, paoType);
                    throw new ProcessingException(error);
                }
                LitePoint point = pointDao.findPointByName(pao, pointName);
                if(point == null) {
                    String error = messageSourceAccessor.getMessage("yukon.web.modules.amr.fdrTranslationManagement.error.pointNotFound", pointName, deviceName);
                    throw new ProcessingException(error);
                }
                
                FdrDirection fdrDirection = FdrDirection.getEnum(direction);
                if(fdrDirection == null) {
                    String error = messageSourceAccessor.getMessage("yukon.web.modules.amr.fdrTranslationManagement.error.invalidDirection", direction);
                    throw new ProcessingException(error);
                }
                
                //build translation
                FdrTranslation translation = new FdrTranslation();

                String translationString = "";
                for(int i = 0; i < interfaceOptions.size(); i++) {
                    String optionLabel = interfaceOptions.get(i).getOptionLabel();
                    translationString += optionLabel + ":" + interfaceOptionValues[i] + ";";
                    translation.getParameterMap().put(optionLabel, interfaceOptionValues[i]);
                }
                translationString += "POINTTYPE:" + point.getPointTypeEnum().toString() + ";";

                translation.setTranslation(translationString);
                translation.setDirection(fdrDirection);
                translation.setPointId(point.getPointID());
                translation.setInterfaceType(fdrInterface);
                
                if(action.equalsIgnoreCase(ADD)) {
                    try {
                        fdrTranslationDao.add(translation);
                    } catch(DataIntegrityViolationException e) {
                        String error = messageSourceAccessor.getMessage("yukon.web.modules.amr.fdrTranslationManagement.error.unableToInsert");
                        throw new ProcessingException(error);
                    }
                } else if(action.equalsIgnoreCase(REMOVE)) {
                    boolean success = fdrTranslationDao.delete(translation);
                    if(!success) {
                        String error = messageSourceAccessor.getMessage("yukon.web.modules.amr.fdrTranslationManagement.error.unableToRemove");
                        throw new ProcessingException(error);
                    }
                } else {
                    String error = messageSourceAccessor.getMessage("yukon.web.modules.amr.fdrTranslationManagement.error.invalidAction");
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
