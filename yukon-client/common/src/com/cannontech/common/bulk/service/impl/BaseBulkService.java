package com.cannontech.common.bulk.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.FileSystemResource;

import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.BulkProcessorCallback;
import com.cannontech.common.bulk.TranslatingBulkProcessorCallback;
import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.field.BulkField;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.bulk.field.BulkFieldService;
import com.cannontech.common.bulk.field.impl.BulkYukonDeviceFieldFactory;
import com.cannontech.common.bulk.field.impl.UpdateableDevice;
import com.cannontech.common.bulk.field.impl.YukonDeviceDto;
import com.cannontech.common.bulk.field.processor.BlankHandlingEnum;
import com.cannontech.common.bulk.field.processor.impl.BulkYukonDeviceFieldProcessor;
import com.cannontech.common.bulk.iterator.CsvReaderIterator;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.bulk.mapper.UpdateableDeviceMapper;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.bulk.processor.SingleProcessor;
import com.cannontech.common.bulk.service.BulkFileInfo;
import com.cannontech.common.bulk.service.BulkOperationCallbackResults;
import com.cannontech.common.bulk.service.BulkOperationTypeEnum;
import com.cannontech.common.bulk.service.ParsedBulkFileInfo;
import com.cannontech.common.bulk.service.UpdateImportCallbackResults;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.tools.csv.CSVReader;
import com.cannontech.web.input.Input;
import com.cannontech.web.input.InputRoot;
import com.cannontech.web.input.InputSource;
import com.cannontech.web.input.InputUtil;

public abstract class BaseBulkService {
    
    protected static interface YukonDeviceResolver {
        public YukonDevice returnDevice(String[] from);
    }
    
    private BulkFieldService bulkFieldService = null;
    private BulkProcessor bulkProcessor = null;
    private BulkYukonDeviceFieldFactory bulkYukonDeviceFieldFactory = null;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper = null;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao = null;
    private RecentResultsCache<BulkOperationCallbackResults<?>> recentBulkOperationResultsCache = null;
    private TemporaryDeviceGroupService temporaryDeviceGroupService = null;

    protected boolean checkForDuplicates(List<BulkFieldColumnHeader> headerColumnSet, ParsedBulkFileInfo result) {
        boolean foundDuplicate = false;
        Set<BulkFieldColumnHeader> duplicateCheckSet = new HashSet<BulkFieldColumnHeader>();
        for (BulkFieldColumnHeader headerColumn : headerColumnSet) {
            if (!duplicateCheckSet.add(headerColumn)) {
                result.addError(new YukonMessageSourceResolvable("yukon.common.device.bulk.fileUpload.error.duplicateColumn", headerColumn.name()));
                foundDuplicate = true;
            }
        }
        return foundDuplicate;
    }  


    // VALIDATE HEADER
    protected List<BulkFieldColumnHeader> createColumnHeaders(BulkFileInfo info, 
                                                              ParsedBulkFileInfo result)
                                                              throws IOException {
        
        CSVReader csvReader = getCSVReader(info.getFileResource());
        String[] headerRow = csvReader.readNext();
        csvReader.close();

        //  make list of header row columns
        List<BulkFieldColumnHeader> headerColumnList = new ArrayList<BulkFieldColumnHeader>();
        for (String columnHeaderName : headerRow) {

            columnHeaderName = StringUtils.deleteWhitespace(columnHeaderName);
            try {

                BulkFieldColumnHeader headerColumn = BulkFieldColumnHeader.valueOf(columnHeaderName);
                headerColumnList.add(headerColumn);

            } catch (IllegalArgumentException e) {

                if (!info.isIgnoreInvalidCols()) {
                    result.addError(new YukonMessageSourceResolvable("yukon.common.device.bulk.columnHeader.error.invalidColumnName", columnHeaderName));
                }
            }
        }
        return headerColumnList;
    }


    protected String doStartBulkImport(final ParsedBulkFileInfo parsedBulkImportFileInfo, final BulkOperationTypeEnum bulkOperationType, final YukonDeviceResolver resolver) throws IOException {
        String resultsId = null;
        FileSystemResource fileResource = parsedBulkImportFileInfo.getBulkFileInfo().getFileResource();

        // SETUP MAPPER
        //------------------------------------------------------------------------------------------

        // creation and update column headers
        List<BulkFieldColumnHeader> updateBulkFieldColumnHeaders = parsedBulkImportFileInfo.getUpdateBulkFieldColumnHeaders();

        // update bulk fields
        final List<BulkField<?, YukonDevice>> bulkFieldList = bulkYukonDeviceFieldFactory.getBulkFieldsForBulkColumnHeaders(updateBulkFieldColumnHeaders);

        // bulk field index map
        final Map<BulkField<?, YukonDevice>, Integer> bulkFieldIndexMap = getBulkFieldIndexes(fileResource);


        // SETUP PROCESSOR
        //------------------------------------------------------------------------------------------

        // iterator
        CSVReader csvReader = getCSVReader(fileResource);
        CsvReaderIterator csvReaderIterator = new CsvReaderIterator(csvReader);
        csvReaderIterator.next();

        try {

            ObjectMapper<String[], UpdateableDevice> mapper = new ObjectMapper<String[], UpdateableDevice>() {
                public UpdateableDevice map(String[] from) throws ObjectMappingException {

                    try {

                        YukonDevice device = resolver.returnDevice(from);

                        // setup updatable device
                        return setupUpdateableDevice(from, device, bulkFieldList, bulkFieldIndexMap);
                    }
                    catch (DeviceCreationException e) {
                        throw new ObjectMappingException(e.getMessage());
                    }
                }
            };

            resultsId = runProcess(csvReaderIterator, bulkFieldList, updateBulkFieldColumnHeaders, mapper, parsedBulkImportFileInfo.getBulkFileInfo(), bulkOperationType);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
        

        return resultsId;
    }


    protected Map<BulkField<?, YukonDevice>, Integer> getBulkFieldIndexes(FileSystemResource fileResource) throws IOException {

        CSVReader csvReader = getCSVReader(fileResource);
        String[] headerRow = csvReader.readNext();
        csvReader.close();

        Map<BulkField<?, YukonDevice>, Integer> bulkFieldIndexes = new HashMap<BulkField<?, YukonDevice>, Integer>();

        int index = 0;
        for (String columnHeaderName : headerRow) {

            columnHeaderName = StringUtils.strip(columnHeaderName);

            try {
                BulkFieldColumnHeader bulkFieldColumnHeader = BulkFieldColumnHeader.valueOf(columnHeaderName);
                BulkField<? ,YukonDevice> bulkField = bulkYukonDeviceFieldFactory.getBulkField(bulkFieldColumnHeader.getFieldName());
                bulkFieldIndexes.put(bulkField, index);
            }
            catch (IllegalArgumentException e) {
            }

            index++;
        }

        return bulkFieldIndexes;
    }

    public BulkFieldService getBulkFieldService() {
        return bulkFieldService;
    }

    public BulkYukonDeviceFieldFactory getBulkYukonDeviceFieldFactory() {
        return bulkYukonDeviceFieldFactory;
    }

    protected CSVReader getCSVReader(FileSystemResource fileResource) throws IOException  {

        InputStreamReader inputStreamReader = new InputStreamReader(fileResource.getInputStream());
        BufferedReader reader = new BufferedReader(inputStreamReader);
        return new CSVReader(reader);
    }

    protected Processor<UpdateableDevice> getMultiBulkFieldUpdateProcessor(final List<BulkField<?, YukonDevice>> bulkFields) {

        return new SingleProcessor<UpdateableDevice>() {

            @Override
            public void process(UpdateableDevice updateableDevice) throws ProcessingException {

                YukonDevice device = updateableDevice.getDevice();
                YukonDeviceDto deviceDto = updateableDevice.getDeviceDto();
                
                // get list of processors that should get run
                // takes into account those fields that don't need updating
                List<BulkYukonDeviceFieldProcessor> bulkFieldProcessors = findYukonDeviceFieldProcessors(updateableDevice, bulkFields);
                
                // run processors
                for (BulkYukonDeviceFieldProcessor bulkFieldProcessor : bulkFieldProcessors) {
                    device = bulkFieldProcessor.updateField(device, deviceDto);
                }
            }
        };
    }

    //
    protected List<BulkYukonDeviceFieldProcessor> findYukonDeviceFieldProcessors(UpdateableDevice updateableDevice,
                                                                                 List<BulkField<?, YukonDevice>> bulkFields) {
        
        // dto wraper
        YukonDeviceDto deviceDto = updateableDevice.getDeviceDto();
        BeanWrapper dtoAccessor = PropertyAccessorFactory.forBeanPropertyAccess(deviceDto);
        
        // narrow list down to just those fields that need to process (i.e. cannot be ignored)
        // based on their blankness, and BlankHandlingEnum value
        List<BulkField<?, YukonDevice>> nonIgnorableFields = new ArrayList<BulkField<?,YukonDevice>>();
        for (BulkField<?, YukonDevice> updateableField : bulkFields) {
            
            BlankHandlingEnum fieldBlankHandlingEnum = updateableField.getBlankHandlingEnum();
            
            // NOT_APPLICABLE
            if (fieldBlankHandlingEnum.equals(BlankHandlingEnum.NOT_APPLICABLE)) {
                continue;
            }

            // IGNORE_BLANK
            if (fieldBlankHandlingEnum.equals(BlankHandlingEnum.IGNORE_BLANK)) {

                String fieldName = updateableField.getInputSource().getField();
                Object fieldValue = dtoAccessor.getPropertyValue(fieldName);

                // blank data for IGNORE_BLANK fields has been normalized to null already
                if (fieldValue == null) {
                    continue;
                }
            }
            
            // if you've gotten to this line you are not a special case scuh that you
            // do not need to be processed
            nonIgnorableFields.add(updateableField);
        }

        return bulkFieldService.findProcessorsForFields(nonIgnorableFields);
    }

    protected String runProcess(CsvReaderIterator csvReaderIterator, 
            List<BulkField<?, YukonDevice>> bulkFieldList, 
            List<BulkFieldColumnHeader> updateBulkFieldColumnHeaders, 
            ObjectMapper<String[], UpdateableDevice> mapper,
            BulkFileInfo bulkFileInfo,
            BulkOperationTypeEnum bulkOperationType) {

        // updater
        Processor<UpdateableDevice> bulkUpdater = getMultiBulkFieldUpdateProcessor(bulkFieldList);

        // callback
        StoredDeviceGroup successGroup = temporaryDeviceGroupService.createTempGroup(null);
        StoredDeviceGroup processingExceptionGroup = temporaryDeviceGroupService.createTempGroup(null);

        // init callcback, use a TranslatingBulkProcessorCallback to get from UpdateableDevice to YukonDevice
        UpdateImportCallbackResults bulkOperationCallbackResults = new UpdateImportCallbackResults(successGroup, processingExceptionGroup, deviceGroupMemberEditorDao, deviceGroupCollectionHelper, updateBulkFieldColumnHeaders, bulkOperationType);

        BulkProcessorCallback<String[],UpdateableDevice> bulkProcessorCallback = new TranslatingBulkProcessorCallback<String[],UpdateableDevice, YukonDevice>(bulkOperationCallbackResults, new UpdateableDeviceMapper());

        // save reference to callback in cache
        String id = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        bulkOperationCallbackResults.setResultsId(id);
        bulkOperationCallbackResults.setBulkFileInfo(bulkFileInfo);
        String resultsId = recentBulkOperationResultsCache.addResult(id, bulkOperationCallbackResults);

        // process
        bulkProcessor.backgroundBulkProcess(csvReaderIterator, mapper, bulkUpdater, bulkProcessorCallback);

        return resultsId;
    }
    
    public int getLineCount(BulkFileInfo info) throws IOException {

        CSVReader csvReader = getCSVReader(info.getFileResource());

        int lineCount = 0;
        while(csvReader.readNext() != null) {
            lineCount++;
        }
        csvReader.close();
        
        lineCount--;
        
        info.setLineCount(lineCount);
        
        return lineCount;
    }

    @Required
    public void setBulkFieldService(BulkFieldService bulkFieldService) {
        this.bulkFieldService = bulkFieldService;
    }

    @Required
    public void setBulkProcessor(BulkProcessor bulkProcessor) {
        this.bulkProcessor = bulkProcessor;
    }

    @Required
    public void setBulkYukonDeviceFieldFactory(
            BulkYukonDeviceFieldFactory bulkYukonDeviceFieldFactory) {
        this.bulkYukonDeviceFieldFactory = bulkYukonDeviceFieldFactory;
    }

    @Required
    public void setDeviceGroupCollectionHelper(
            DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
    }

    @Required
    public void setDeviceGroupMemberEditorDao(
            DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }


    @Required
    public void setRecentBulkOperationResultsCache(
            RecentResultsCache<BulkOperationCallbackResults<?>> recentBulkOperationResultsCache) {
        this.recentBulkOperationResultsCache = recentBulkOperationResultsCache;
    }


    @Required
    public void setTemporaryDeviceGroupService(
            TemporaryDeviceGroupService temporaryDeviceGroupService) {
        this.temporaryDeviceGroupService = temporaryDeviceGroupService;
    }

    public static UpdateableDevice setupUpdateableDevice(String[] row, YukonDevice device, List<BulkField<?, YukonDevice>> bulkFields, Map<BulkField<?, YukonDevice>, Integer> bulkFieldIndexMap) {

        UpdateableDevice updateableDevice = null;
        
        try {
            
            Map<String, String> valueMap = new HashMap<String, String>();
            List<Input<?>> inputList = new ArrayList<Input<?>>();
    
            for (BulkField<?, YukonDevice> bulkField : bulkFields) {
    
                try {
                    
                    int fieldIndex = bulkFieldIndexMap.get(bulkField);
                    String fieldStringValue = StringUtils.trim(row[fieldIndex]);
        
                    InputSource<?> inputSource = bulkField.getInputSource();
                    inputList.add(inputSource);
                    
                    // normalized blank data
                    // if its blank and is to be ignored, set to null
                    // if its blank and blank handling is not applicable, set to null
                    // otherwise set as-is
                    BlankHandlingEnum blankHandlingEnum = bulkField.getBlankHandlingEnum();
                    if (StringUtils.isBlank(fieldStringValue) 
                            && (blankHandlingEnum.equals(BlankHandlingEnum.IGNORE_BLANK) || 
                                blankHandlingEnum.equals(BlankHandlingEnum.NOT_APPLICABLE))) {
                        fieldStringValue = null;
                    }
        
                    valueMap.put(inputSource.getField(), fieldStringValue);
                
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new DeviceCreationException("Incomplete row.", e);
                }
            }
    
            // make InputRoot obj
            InputRoot inputRoot = new InputRoot();
            inputRoot.setInputList(inputList);
    
            // apply values to dto
            YukonDeviceDto yukonDeviceDtoObj = new YukonDeviceDto();
            InputUtil.applyProperties(inputRoot, yukonDeviceDtoObj, valueMap);
    
            // updateableDevice
            updateableDevice = new UpdateableDevice();
            updateableDevice.setDeviceDto(yukonDeviceDtoObj);
            updateableDevice.setDevice(device);
            
        }
        catch (TypeMismatchException e) {
            throw new ObjectMappingException("Contains invalid value: " + (e.getValue() == null ? "":e.getValue()), e);
        }

        return updateableDevice;
    }
}
