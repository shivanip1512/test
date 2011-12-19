package com.cannontech.common.bulk.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.cannontech.common.bulk.service.FdrTranslationManagerService;
import com.cannontech.common.exception.ImportFileFormatException;
import com.cannontech.common.fdr.FdrDirection;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrTranslation;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.dao.FdrTranslationDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.fdr.FDRInterface;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.point.fdr.FDRInterfaceOption;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class FdrTranslationManagerServiceImpl implements FdrTranslationManagerService {
    private Logger log = YukonLogManager.getLogger(FdrTranslationManagerServiceImpl.class);
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private RecentResultsCache<BackgroundProcessResultHolder> bpRecentResultsCache;
    private BulkProcessor bulkProcessor = null;
    private PaoDao paoDao;
    private PointDao pointDao;
    private FdrTranslationDao fdrTranslationDao;
    
    private static final String ADD = "ADD";
    private static final String REMOVE = "REMOVE";
    
    private static final String ACTION = "ACTION";
    private static final String DEVICE_NAME = "DEVICE_NAME";
    private static final String DEVICE_TYPE = "DEVICE_TYPE";
    private static final String POINT_NAME = "POINT_NAME";
    private static final String DIRECTION = "DIRECTION";
    
    private static final String POINTTYPE = "POINTTYPE";
    
    private static final int DEVICE_NAME_COL = 0;
    private static final int DEVICE_TYPE_COL = 1;
    private static final int POINT_NAME_COL = 2;
    private static final int DIRECTION_COL = 3;
    private static final int DEFAULT_COLS_FOR_EXPORT = 4;
    
    private static final String[] defaultImportColumnHeaders = {ACTION, DEVICE_NAME, DEVICE_TYPE, POINT_NAME, DIRECTION};
    
    @Override
    public String formatOptionForColumnHeader(String optionString, String interfaceName) {
        return interfaceName
               + "_"
               + optionString.toUpperCase()
                             .replaceAll("[ /]", "_")
                             .replaceAll("[()]", "");
    }
    
    @Override
    public void addDefaultColumnsToList(List<String> list) {
        list.add(DEVICE_NAME);
        list.add(DEVICE_TYPE);
        list.add(POINT_NAME);
        list.add(DIRECTION);
    }
    
    @Override
    public boolean matchesDefaultColumn(String header) {
        return header.equals(ACTION)
            || header.equals(DEVICE_NAME) 
            || header.equals(DEVICE_TYPE) 
            || header.equals(POINT_NAME) 
            || header.equals(DIRECTION);
    }
    
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
        
        FDRInterface[] allFdrInterfaces = com.cannontech.database.db.point.fdr.FDRInterface.getALLFDRInterfaces();
        
        for(FDRInterface fdrInterface : allFdrInterfaces) {
            FdrInterfaceDisplayable displayable = new FdrInterfaceDisplayable(fdrInterface.toString());

            for(FDRInterfaceOption option : fdrInterface.getInterfaceOptionList()) {
                String columnName = formatOptionForColumnHeader(option.getOptionLabel(), fdrInterface.toString());
                
                MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
                String description = option.getOptionValues();
                if(option.getOptionType().equalsIgnoreCase(FDRInterfaceOption.OPTION_TEXT) && description.equals(CtiUtilities.STRING_NONE)) {
                        description = messageSourceAccessor.getMessage("yukon.web.modules.amr.fdrTranslationManagement.columns." + columnName);
                } else if(option.getOptionType().equalsIgnoreCase(FDRInterfaceOption.OPTION_COMBO)) {
                    String optionText = messageSourceAccessor.getMessage("yukon.web.modules.amr.fdrTranslationManagement.columns.optionText");
                    description = optionText + description.replaceAll(",", ", "); //easier to read and allows line breaks
                }
                displayable.addColumnAndDescription(columnName, description);
            }
            
            displayables.add(displayable);
        }
        
        return displayables;
    }
    
    @Override
    public void addHeadersFromTranslations(List<String> headers, List<FdrTranslation> translations) {
        List<FdrInterfaceType> interfaceTypesAdded = Lists.newArrayList(); 
        for(FdrTranslation translation : translations) {
            FdrInterfaceType thisInterfaceType = translation.getFdrInterfaceType();
            if(!interfaceTypesAdded.contains(thisInterfaceType)) {
                interfaceTypesAdded.add(thisInterfaceType);
                String interfaceName = translation.getFdrInterfaceType().toString();
                Set<String> thisTranslationColumns = translation.getParameterMap().keySet();
                for(String unformattedColumnHeader : thisTranslationColumns) {
                    //do not add POINTTYPE to import files
                    if(!unformattedColumnHeader.equals(POINTTYPE)) {
                        String formattedColumnHeader = formatOptionForColumnHeader(unformattedColumnHeader, interfaceName);
                        headers.add(formattedColumnHeader);
                    }
                }
            }
        }
    }
    
    @Override
    public void populateExportArray(String[][] dataGrid, List<FdrTranslation> translationsList) {
        for(int i = 0; i < translationsList.size(); i++) {
            FdrTranslation translation = translationsList.get(i);
            String translationName = translation.getFdrInterfaceType().name();
            
            //Add default column values
            LitePoint point = pointDao.getLitePoint(translation.getPointId());
            LiteYukonPAObject pao = paoDao.getLiteYukonPAO(point.getPaobjectID());
            String deviceName = pao.getPaoName();
            String deviceType = pao.getPaoType().getPaoTypeName();
            String pointName = point.getPointName();
            String direction = translation.getDirection().getValue();
            dataGrid[i+1][DEVICE_NAME_COL] = deviceName;
            dataGrid[i+1][DEVICE_TYPE_COL] = deviceType;
            dataGrid[i+1][POINT_NAME_COL] = pointName;
            dataGrid[i+1][DIRECTION_COL] = direction;
            
            //Iterate through interface-specific columns
            for(int j = DEFAULT_COLS_FOR_EXPORT; j < dataGrid[0].length; j++) {
                //If the translation has a value for a given column, add it
                String formattedColumnHeader = dataGrid[0][j];
                boolean matchFound = false;
                for(Map.Entry<String, String> option : translation.getParameterMap().entrySet()) {
                    String translationPart = formatOptionForColumnHeader(option.getKey(), translationName);
                    if(translationPart.equals(formattedColumnHeader)) {
                        dataGrid[i+1][j] = option.getValue();
                        matchFound = true;
                    }
                }
                
                //If the translation does not have a value for a given column, add an empty string
                if(!matchFound) {
                    dataGrid[i+1][j] = "";
                }
            }
        }
    }
    
    @Override
    public String checkForMissingDefaultImportHeaders(List<String> headers) {
        String returnValue = null;
        for(String defaultHeader : defaultImportColumnHeaders) {
            if(!headers.contains(defaultHeader)) {
                if(returnValue != null) {
                    returnValue += ", " + defaultHeader;
                } else {
                    returnValue = defaultHeader;
                }
            }
        }
        return returnValue;
    }
    
    @Override
    public List<String> cleanAndValidateHeaders(String[] inputHeaders) throws ImportFileFormatException {
        List<String> outputHeaders = Lists.newArrayList();
        
        for(String inputHeader : inputHeaders) {
            String noWhiteSpaceHeaderToAdd = StringUtils.deleteWhitespace(inputHeader).toUpperCase();
            if(outputHeaders.contains(noWhiteSpaceHeaderToAdd)) {
                ImportFileFormatException exception =  new ImportFileFormatException("Duplicate column header: " + noWhiteSpaceHeaderToAdd);
                exception.setHeaderName(noWhiteSpaceHeaderToAdd);
                throw exception;
            } else {
                outputHeaders.add(noWhiteSpaceHeaderToAdd);
            }
        }
        
        return outputHeaders;
    }
    
    @Override
    public FdrImportFileInterfaceInfo getInterfaceInfo(List<String> headers, boolean ignoreInvalidColumns) throws ImportFileFormatException {
        FdrImportFileInterfaceInfo interfaceInfo = new FdrImportFileInterfaceInfo();
        
        FDRInterface[] allFdrInterfaces = com.cannontech.database.db.point.fdr.FDRInterface.getALLFDRInterfaces();
        List<FDRInterface> allFdrInterfacesList = Lists.newArrayList(allFdrInterfaces);
        
        for(String header : headers) {
            boolean headerMatchFound = false;
            
            for(FDRInterface fdrInterface : allFdrInterfacesList) {
                if(matchesDefaultColumn(header)) {
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
    
    @Override
    public void validateInterfaceHeadersPresent(FdrImportFileInterfaceInfo interfaceInfo, List<String> headers) throws ImportFileFormatException {
        for(FDRInterface fdrInterface : interfaceInfo.getInterfaces()) {
            String interfaceName = fdrInterface.toString();
            //a. Get list of all columns required for each interface
            List<FDRInterfaceOption> options = fdrInterface.getInterfaceOptionList();
            //b. Check headers for all columns for each interface
            for(FDRInterfaceOption option : options) {
                String formattedOption = formatOptionForColumnHeader(option.getOptionLabel(), interfaceName);
                if(!headers.contains(formattedOption)) {
                    ImportFileFormatException exception = new ImportFileFormatException("Missing column header \"" + formattedOption + "\" for interface \"" + interfaceName);
                    exception.setHeaderName(formattedOption);
                    exception.setInterfaceName(interfaceName);
                    throw exception;
                }
            }
        }
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
        FDRInterface[] allFdrInterfaces = com.cannontech.database.db.point.fdr.FDRInterface.getALLFDRInterfaces();
        final List<FDRInterface> allFdrInterfacesList = Lists.newArrayList(allFdrInterfaces);
        
        SingleProcessor<String[]> translationProcessor = new SingleProcessor<String[]>() {
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            
            @Override
            public void process(String[] line) throws ProcessingException {
                String deviceName = null;
                String deviceType = null;
                String pointName = null;
                String direction = null;
                String action = null;
                
                FDRInterface fdrInterface = null;
                List<FDRInterfaceOption> interfaceOptions = Lists.newArrayList();
                String[] interfaceOptionValues = new String[0];
                
                //iterate over columns (checking if they're ignored) 
                for(int i = 0; i < line.length; i++) {
                    if(columnsToIgnore.contains(i)) continue; //ignored column, skip to next
                    
                    String header = headers.get(i);
                    String columnValue = line[i];
                    
                    if(columnValue.equalsIgnoreCase("")) continue; //no value present, skip to next
                    
                    if(header.equals(DEVICE_NAME)) {
                        deviceName = columnValue;
                    } else if(header.equals(DEVICE_TYPE)) {
                        deviceType = columnValue;
                    } else if(header.equals(POINT_NAME)) {
                        pointName = columnValue;
                    } else if(header.equals(DIRECTION)) {
                        direction = columnValue;
                    } else if(header.equals(ACTION)) {
                        action = columnValue;
                    } else {
                        //interface-specific column
                        //if interface hasn't been determined yet, parse column name to get it
                        if(fdrInterface == null) {
                            for(FDRInterface currentInterface : allFdrInterfacesList) {
                                String thisInterfaceName = currentInterface.toString();
                                if(header.startsWith(thisInterfaceName + "_")) {
                                    fdrInterface = currentInterface;
                                    interfaceOptions = currentInterface.getInterfaceOptionList();
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
                            String formattedOptionName = formatOptionForColumnHeader(interfaceOptions.get(j).getOptionLabel(), fdrInterface.toString());
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
                    missingColumn = DEVICE_NAME;
                } else if(deviceType == null) {
                    missingColumn = DEVICE_TYPE;
                } else if(pointName == null) {
                    missingColumn = POINT_NAME;
                } else if(direction == null) {
                    missingColumn = DIRECTION;
                } else if(action == null) {
                    missingColumn = ACTION;
                } else {
                    for(int i = 0; i < interfaceOptions.size(); i++) {
                        if(interfaceOptionValues[i] == null) {
                            String formattedColumnName = formatOptionForColumnHeader(interfaceOptions.get(i).getOptionLabel(), fdrInterface.toString());
                            missingColumn = formattedColumnName;
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
                
                FdrInterfaceType interfaceType = FdrInterfaceType.valueOf(fdrInterface.toString());
                
                //build translation
                FdrTranslation translation = new FdrTranslation();

                Map<String, String> parameterMap = translation.getParameterMap();
                String translationString = "";
                for(int i = 0; i < interfaceOptions.size(); i++) {
                    translationString += interfaceOptions.get(i).getOptionLabel() + ":" + interfaceOptionValues[i] + ";";
                    parameterMap.put(interfaceOptions.get(i).getOptionLabel(), interfaceOptionValues[i]);
                }
                translationString += "POINTTYPE:" + point.getPointTypeEnum().toString() + ";";

                translation.setTranslation(translationString);
                translation.setDirection(fdrDirection);
                translation.setPointId(point.getPointID());
                translation.setInterfaceType(interfaceType);
                
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
