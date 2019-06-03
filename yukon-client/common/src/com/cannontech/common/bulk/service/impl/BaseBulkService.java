package com.cannontech.common.bulk.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.FileSystemResource;

import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessTypeEnum;
import com.cannontech.common.bulk.callbackResult.BulkProcessorCallback;
import com.cannontech.common.bulk.callbackResult.ImportUpdateCallbackResult;
import com.cannontech.common.bulk.callbackResult.TranslatingBulkProcessorCallback;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.field.BulkField;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.bulk.field.BulkFieldService;
import com.cannontech.common.bulk.field.impl.BulkYukonDeviceFieldFactory;
import com.cannontech.common.bulk.field.impl.LatitudeBulkField;
import com.cannontech.common.bulk.field.impl.LongitudeBulkField;
import com.cannontech.common.bulk.field.impl.NameBulkField;
import com.cannontech.common.bulk.field.impl.UpdateableDevice;
import com.cannontech.common.bulk.field.impl.YukonDeviceDto;
import com.cannontech.common.bulk.field.processor.BlankHandlingEnum;
import com.cannontech.common.bulk.field.processor.impl.BulkYukonDeviceFieldProcessor;
import com.cannontech.common.bulk.field.processor.impl.LatitudeLongitudeBulkFieldProcessor;
import com.cannontech.common.bulk.iterator.CsvReaderIterator;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.bulk.mapper.UpdateableDeviceMapper;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.bulk.processor.SingleProcessor;
import com.cannontech.common.bulk.service.BulkFileInfo;
import com.cannontech.common.bulk.service.ParsedBulkFileInfo;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.ToolsEventLogService;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.input.Input;
import com.cannontech.web.input.InputRoot;
import com.cannontech.web.input.InputSource;
import com.cannontech.web.input.InputUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.opencsv.CSVReader;

public abstract class BaseBulkService {
    
    protected static interface YukonDeviceResolver {
        public SimpleDevice returnDevice(String[] from);
    }
    
    private BulkFieldService bulkFieldService;
    private BulkProcessor bulkProcessor;
    private BulkYukonDeviceFieldFactory bulkYukonDeviceFieldFactory;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    private RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache;
    private TemporaryDeviceGroupService temporaryDeviceGroupService;
    @Autowired private ToolsEventLogService toolsEventLogService;

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

    protected void checkUpdateBulkFieldColumnHeaders(ParsedBulkFileInfo result, Collection<BulkFieldColumnHeader> updateBulkFieldColumnHeaders) {
        
        try {
            bulkFieldService.processorExistsForBulkFieldColumnHeaders(Lists.newArrayList(updateBulkFieldColumnHeaders));
        } catch (UnprocessableHeadersException e) {
            for (BulkFieldColumnHeader bulkFieldColumnHeader : e.getBadHeaders()) {
                result.addError(new YukonMessageSourceResolvable("yukon.common.device.bulk.columnHeader.error.notUpdateableColumnName", bulkFieldColumnHeader.toString()));
            }
        }

    }

    protected String doStartBulkImport(final ParsedBulkFileInfo parsedBulkImportFileInfo, final BackgroundProcessTypeEnum bulkOperationType, final YukonDeviceResolver resolver) throws IOException {
        String resultsId = null;
        FileSystemResource fileResource = parsedBulkImportFileInfo.getBulkFileInfo().getFileResource();

        // SETUP MAPPER
        //------------------------------------------------------------------------------------------

        // creation and update column headers
        List<BulkFieldColumnHeader> updateBulkFieldColumnHeaders = parsedBulkImportFileInfo.getUpdateBulkFieldColumnHeaders();

        // update bulk fields
        final List<BulkField<?, SimpleDevice>> bulkFieldList = bulkYukonDeviceFieldFactory.getBulkFieldsForBulkColumnHeaders(updateBulkFieldColumnHeaders);

        // bulk field index map
        final Map<BulkField<?, SimpleDevice>, Integer> bulkFieldIndexMap = getBulkFieldIndexes(fileResource);


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

                        SimpleDevice device = resolver.returnDevice(from);

                        // setup updatable device
                        return setupUpdateableDevice(from, device, bulkFieldList, bulkFieldIndexMap);
                    }
                    catch (DeviceCreationException e) {
                        throw new ObjectMappingException(e);
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


    protected Map<BulkField<?, SimpleDevice>, Integer> getBulkFieldIndexes(FileSystemResource fileResource) throws IOException {

        CSVReader csvReader = getCSVReader(fileResource);
        String[] headerRow = csvReader.readNext();
        csvReader.close();

        Map<BulkField<?, SimpleDevice>, Integer> bulkFieldIndexes = new HashMap<BulkField<?, SimpleDevice>, Integer>();

        int index = 0;
        for (String columnHeaderName : headerRow) {

            columnHeaderName = StringUtils.strip(columnHeaderName);

            try {
                BulkFieldColumnHeader bulkFieldColumnHeader = BulkFieldColumnHeader.valueOf(columnHeaderName);
                BulkField<? ,SimpleDevice> bulkField = bulkYukonDeviceFieldFactory.getBulkField(bulkFieldColumnHeader.getFieldName());
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
        BOMInputStream bomInputStream = new BOMInputStream(fileResource.getInputStream(), ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE,
                ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_32LE, ByteOrderMark.UTF_32BE);
        InputStreamReader inputStreamReader = new InputStreamReader(bomInputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        return new CSVReader(reader);
    }

    protected Processor<UpdateableDevice> getMultiBulkFieldUpdateProcessor(final List<BulkField<?, SimpleDevice>> bulkFields) {

        return new SingleProcessor<UpdateableDevice>() {

            @Override
            public void process(UpdateableDevice updateableDevice) throws ProcessingException {

                SimpleDevice device = updateableDevice.getDevice();
                YukonDeviceDto deviceDto = updateableDevice.getDeviceDto();
                
                // get list of processors that should get run
                // takes into account those fields that don't need updating
                List<BulkYukonDeviceFieldProcessor> bulkFieldProcessors;
                try {
                    bulkFieldProcessors = findYukonDeviceFieldProcessors(updateableDevice, bulkFields);
                } catch (UnprocessableHeadersException e) {
                    throw new ProcessingException("Unable to find processor for field(s).", "processorNotFound", e );
                }
                
                // run processors
                for (BulkYukonDeviceFieldProcessor bulkFieldProcessor : bulkFieldProcessors) {
                    bulkFieldProcessor.updateField(device, deviceDto);
                }
            }
        };
    }

    //
    protected List<BulkYukonDeviceFieldProcessor> findYukonDeviceFieldProcessors(UpdateableDevice updateableDevice,
                                                                                 List<BulkField<?, SimpleDevice>> bulkFields) {
        
        // dto wraper
        YukonDeviceDto deviceDto = updateableDevice.getDeviceDto();
        BeanWrapper dtoAccessor = PropertyAccessorFactory.forBeanPropertyAccess(deviceDto);
        
        // narrow list down to just those fields that need to process (i.e. cannot be ignored)
        // based on their blankness, and BlankHandlingEnum value
        Set<BulkField<?, SimpleDevice>> nonIgnorableFields = Sets.newHashSet();
        for (BulkField<?, SimpleDevice> updateableField : bulkFields) {
            
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
            List<BulkField<?, SimpleDevice>> bulkFieldList, 
            List<BulkFieldColumnHeader> updateBulkFieldColumnHeaders, 
            ObjectMapper<String[], UpdateableDevice> mapper,
            BulkFileInfo bulkFileInfo,
            BackgroundProcessTypeEnum backgroundProcessType) {

        // updater
        Processor<UpdateableDevice> bulkUpdater = getMultiBulkFieldUpdateProcessor(bulkFieldList);

        // CALLBACK
        String resultsId = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        StoredDeviceGroup successGroup = temporaryDeviceGroupService.createTempGroup();
        
        ImportUpdateCallbackResult callbackResult =
            new ImportUpdateCallbackResult(backgroundProcessType, updateBulkFieldColumnHeaders, bulkFileInfo, resultsId,
                successGroup, deviceGroupMemberEditorDao, deviceGroupCollectionHelper, toolsEventLogService);

        BulkProcessorCallback<String[], UpdateableDevice> translatingCallback = new TranslatingBulkProcessorCallback<String[], UpdateableDevice, SimpleDevice>(callbackResult, new UpdateableDeviceMapper());

        // CACHE
        recentResultsCache.addResult(resultsId, callbackResult);

        // PROCESS
        bulkProcessor.backgroundBulkProcess(csvReaderIterator, mapper, bulkUpdater, translatingCallback);

        return resultsId;
    }
    
    public int calculateLineCount(BulkFileInfo info) throws IOException {

        CSVReader csvReader = getCSVReader(info.getFileResource());

        int lineCount = 0;
        while(csvReader.readNext() != null) {
            lineCount++;
        }
        csvReader.close();
        
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
    public void setDeviceGroupMemberEditorDao(
            DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }

    @Autowired
    public void setDeviceGroupCollectionHelper(DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
		this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
	}

    @Required
    public void setRecentResultsCache(RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache) {
        this.recentResultsCache = recentResultsCache;
    }


    @Required
    public void setTemporaryDeviceGroupService(
            TemporaryDeviceGroupService temporaryDeviceGroupService) {
        this.temporaryDeviceGroupService = temporaryDeviceGroupService;
    }

    public static UpdateableDevice setupUpdateableDevice(String[] row, SimpleDevice device,
            List<BulkField<?, SimpleDevice>> bulkFields, Map<BulkField<?, SimpleDevice>, Integer> bulkFieldIndexMap) {

        UpdateableDevice updateableDevice = null;

        try {

            Map<String, String> valueMap = new HashMap<String, String>();
            List<Input<?>> inputList = new ArrayList<Input<?>>();

            for (BulkField<?, SimpleDevice> bulkField : bulkFields) {

                try {

                    int fieldIndex = bulkFieldIndexMap.get(bulkField);
                    String fieldStringValue = StringUtils.trim(row[fieldIndex]);

                    InputSource<?> inputSource = bulkField.getInputSource();
                    inputList.add(inputSource);

                    // normalized blank data
                    // if its blank and is to be ignored, set to null
                    // if its blank and blank handling is not applicable, set to null
                    // if its lat/long and it contains DELETE/NULL, set to null so that location will be
                    // removed
                    // otherwise set as-is
                    BlankHandlingEnum blankHandlingEnum = bulkField.getBlankHandlingEnum();
                    if (StringUtils.isBlank(fieldStringValue)
                        && (blankHandlingEnum.equals(BlankHandlingEnum.IGNORE_BLANK)
                            || blankHandlingEnum.equals(BlankHandlingEnum.NOT_APPLICABLE))) {
                        fieldStringValue = null;
                    } else if ((bulkField instanceof NameBulkField) && !(PaoUtils.isValidPaoName(fieldStringValue))) {
                        throw new DeviceCreationException(
                            "Device name cannot include any of the following characters "
                                + String.valueOf(PaoUtils.ILLEGAL_NAME_CHARS),
                            "invalidCharacters", String.valueOf(PaoUtils.ILLEGAL_NAME_CHARS));
                    } else if ((bulkField instanceof LatitudeBulkField || bulkField instanceof LongitudeBulkField)) {
                        if ("DELETE".equalsIgnoreCase(fieldStringValue) || "NULL".equalsIgnoreCase(fieldStringValue)) {
                            fieldStringValue = null;
                        } else if (StringUtils.isEmpty(fieldStringValue)) {
                            fieldStringValue = Double.toString(LatitudeLongitudeBulkFieldProcessor.IGNORE_FIELD);
                        }
                    }

                    valueMap.put(inputSource.getField(), fieldStringValue);

                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new DeviceCreationException("Incomplete row.", "incompleteRow", e);
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

        } catch (TypeMismatchException e) {
            throw new ObjectMappingException("Contains invalid value: " + (e.getValue() == null ? "" : e.getValue()),
                "invalidValue", ("in field "+e.getPropertyName() + ":- " + (e.getValue() == null ? "" : e.getValue())), e);
        }

        return updateableDevice;
    }
}
