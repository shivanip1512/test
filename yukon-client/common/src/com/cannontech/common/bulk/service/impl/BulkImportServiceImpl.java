package com.cannontech.common.bulk.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.FileSystemResource;

import com.cannontech.common.bulk.callbackResult.BackgroundProcessTypeEnum;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.bulk.service.BulkFileInfo;
import com.cannontech.common.bulk.service.BulkImportFileInfo;
import com.cannontech.common.bulk.service.BulkImportMethod;
import com.cannontech.common.bulk.service.BulkImportService;
import com.cannontech.common.bulk.service.BulkImportType;
import com.cannontech.common.bulk.service.ParsedBulkImportFileInfo;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.opencsv.CSVReader;

public class BulkImportServiceImpl extends BaseBulkService implements BulkImportService {
    
    private List<BulkImportMethod> supportedImportMethods = null;

    public ParsedBulkImportFileInfo createParsedBulkImportFileInfo(BulkImportFileInfo info, BulkImportType bulkImportType) {
        
        ParsedBulkImportFileInfo result = new ParsedBulkImportFileInfo(info);
        
        try {

            // line count for BulkFileInfo
            calculateLineCount(info);
            
            // set reference to BulkFileInfo
            result.setBulkFileInfo(info);
            
            // create-check headers
            List<BulkFieldColumnHeader> headerColumnList = createColumnHeaders(info, result);
            if (result.hasErrors()) {
                return result;
            }

            // look for duplicates
            List<BulkFieldColumnHeader> headerColumnSet = new ArrayList<BulkFieldColumnHeader>(headerColumnList);
            boolean foundDuplicate = checkForDuplicates(headerColumnSet, result);
            if (foundDuplicate) {
                return result;
            }

            // check for all required fields for each method
            BulkImportMethod chosenMethod = null;
            for (BulkImportMethod method : supportedImportMethods) {
                if (method.getType() == bulkImportType) {
                    Set<BulkFieldColumnHeader> requiredColumns = method.getRequiredColumns();
                    if (headerColumnSet.containsAll(requiredColumns)) {
                        chosenMethod = method;
                        break;
                    }
                }
            }

            if (chosenMethod == null) {
                result.addError(new YukonMessageSourceResolvable("yukon.common.device.bulk.columnHeader.import.error.requiredFields"));
                return result;
            }
            
            // set method
            result.setBulkImportMethod(chosenMethod);

            // remove requird columns from list
            Set<BulkFieldColumnHeader> updateBulkFieldColumnHeaders = new HashSet<BulkFieldColumnHeader>(headerColumnSet);
            updateBulkFieldColumnHeaders.removeAll(chosenMethod.getRequiredColumns());
            
            // check that remaining update columns are ok (updateable)
            checkUpdateBulkFieldColumnHeaders(result, updateBulkFieldColumnHeaders);
            if (!result.hasErrors()) {
                for (BulkFieldColumnHeader bulkFieldColumnHeader : updateBulkFieldColumnHeaders) {
                    if (!chosenMethod.getOptionalColumns().contains(bulkFieldColumnHeader)) {
                        result.addError(new YukonMessageSourceResolvable("yukon.common.device.bulk.columnHeader.error.notUpdateableColumnName",
                                                                       bulkFieldColumnHeader.toString()));
                    }
                }
            }

            if (result.hasErrors()) {
                return result;
            }
            // set the update bulk fields
            result.addUpdateBulkFieldColumnHeaders(updateBulkFieldColumnHeaders);
            
        } catch (IOException e) {
            result.addError(new YukonMessageSourceResolvable("yukon.common.device.bulk.fileUpload.error.emptyFile"));
            return result;
        }

        return result;

    }
    
    public String startBulkImport(final ParsedBulkImportFileInfo parsedBulkImportFileInfo) throws IOException {

        final Map<BulkFieldColumnHeader, Integer> requiredFieldIndexMap = getRequiredFieldIndexes(parsedBulkImportFileInfo);
        
        return doStartBulkImport(parsedBulkImportFileInfo, BackgroundProcessTypeEnum.IMPORT, new YukonDeviceResolver() {
            
            @Override
            public SimpleDevice returnDevice(String[] from) {

                // DEVICE CREATION
                final Map<BulkFieldColumnHeader, String> creationFields = new HashMap<BulkFieldColumnHeader, String>();

                for (BulkFieldColumnHeader bulkHeader : parsedBulkImportFileInfo.getBulkImportMethod().getRequiredColumns()) {

                    try {
                        
                        int fieldIndex = requiredFieldIndexMap.get(bulkHeader);
                        String fieldStringValue = StringUtils.trim(from[fieldIndex]);

                        creationFields.put(bulkHeader, fieldStringValue);
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        throw new DeviceCreationException("Missing required column: " + bulkHeader.name(),"missingRequiredColumn", bulkHeader.name(), e);
                    }
                    
                }

                SimpleDevice device = parsedBulkImportFileInfo.getBulkImportMethod().initDevice(creationFields);
                return device;
            }
        });
    }
    
    private Map<BulkFieldColumnHeader, Integer> getRequiredFieldIndexes(ParsedBulkImportFileInfo parsedBulkImportFileInfo) throws IOException {

        // read header
        BulkFileInfo bulkFileInfo = parsedBulkImportFileInfo.getBulkFileInfo();
        FileSystemResource fileResource = bulkFileInfo.getFileResource();
        CSVReader csvReader = getCSVReader(fileResource);
        String[] headerRow = csvReader.readNext();
        csvReader.close();

        Map<BulkFieldColumnHeader, Integer> bulkRequiredFieldIndexes = new HashMap<BulkFieldColumnHeader, Integer>();

        int index = 0;
        for (String columnHeaderName : headerRow) {

            columnHeaderName = StringUtils.strip(columnHeaderName);

            try {
                BulkFieldColumnHeader bulkFieldColumnHeader = BulkFieldColumnHeader.valueOf(columnHeaderName);
                bulkRequiredFieldIndexes.put(bulkFieldColumnHeader, index);
            }
            catch (IllegalArgumentException e) {
            }

            index++;
        }

        return bulkRequiredFieldIndexes;
    }
    
    @Required
    public void setSupportedImportMethods(List<BulkImportMethod> supportedImportMethods) {
        this.supportedImportMethods = supportedImportMethods;
    }

}
