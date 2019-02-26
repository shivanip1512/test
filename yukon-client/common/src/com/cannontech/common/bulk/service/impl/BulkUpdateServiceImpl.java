package com.cannontech.common.bulk.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.bulk.callbackResult.BackgroundProcessTypeEnum;
import com.cannontech.common.bulk.field.BulkField;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.bulk.service.BulkUpdateFileInfo;
import com.cannontech.common.bulk.service.BulkUpdateService;
import com.cannontech.common.bulk.service.ParsedBulkUpdateFileInfo;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.opencsv.CSVReader;

public class BulkUpdateServiceImpl extends BaseBulkService implements BulkUpdateService {
    
    public ParsedBulkUpdateFileInfo createParsedBulkUpdateFileInfo(BulkUpdateFileInfo info) {
        
        ParsedBulkUpdateFileInfo result = new ParsedBulkUpdateFileInfo(info);
        
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

            // look for identifier column
            BulkFieldColumnHeader identifierColunHeader = null;
            
            if (headerColumnList.size() < 1) {
                result.addError(new YukonMessageSourceResolvable("yukon.common.device.bulk.columnHeader.update.error.noIdentifier"));
            }
            else if (headerColumnList.size() < 2) {
                result.addError(new YukonMessageSourceResolvable("yukon.common.device.bulk.columnHeader.update.error.noUpdateColumn"));
            }
            else {
                
                identifierColunHeader = headerColumnList.get(0);
                BulkField<?, SimpleDevice> identifierBulkField = getBulkYukonDeviceFieldFactory().getBulkField(identifierColunHeader.getFieldName());

                if (identifierBulkField == null || identifierBulkField.getIdentifierMapper() == null) {
                    result.addError(new YukonMessageSourceResolvable("yukon.common.device.bulk.columnHeader.update.error.badIdentifier", identifierColunHeader.name()));
                }
            }
            
            if (result.hasErrors()) {
                return result;
            }
            
            // remove identifier, set on info
            List<BulkFieldColumnHeader> updateBulkFieldColumnHeaders = new ArrayList<BulkFieldColumnHeader>(headerColumnList);
            updateBulkFieldColumnHeaders.remove(identifierColunHeader);
            result.setIdentifierBulkFieldColumnHeader(identifierColunHeader);
            
            // check that remaining update columns are ok (updateable)
            checkUpdateBulkFieldColumnHeaders(result, updateBulkFieldColumnHeaders);
            if (result.hasErrors()) {
                return result;
            }
            
            // look for duplicates (after identifier has been found and removed)
            boolean foundDuplicate = checkForDuplicates(updateBulkFieldColumnHeaders, result);
            if (foundDuplicate) {
                return result;
            }

            // the update bulk fields
            result.addUpdateBulkFieldColumnHeaders(updateBulkFieldColumnHeaders);
            
            // idenifier errors
            if (!info.isIgnoreInvalidIdentifiers()) {
                findIdentifierErrors(info, result);
            }

        } catch (IOException e) {
            result.addError(new YukonMessageSourceResolvable("yukon.common.device.bulk.fileUpload.error.emptyFile"));
            return result;
        }

        return result;
    }
    
    private void findIdentifierErrors(BulkUpdateFileInfo bulkUpdateFileInfo, ParsedBulkUpdateFileInfo result) throws IOException {
        
        CSVReader f = getCSVReader(bulkUpdateFileInfo.getFileResource());
        
        // assume header and id are ok or we wouldn't be here..
        BulkFieldColumnHeader identifierBulkFieldColumnHeader = result.getIdentifierBulkFieldColumnHeader();
        BulkField<?, SimpleDevice> bulkField = getBulkYukonDeviceFieldFactory().getBulkField(identifierBulkFieldColumnHeader.getFieldName());
        
        f.readNext();
        String [] line = null;
        while((line = f.readNext()) != null) {

            String identifier = StringUtils.trim(line[0]);
            
            try {
                getBulkFieldService().getYukonDeviceForIdentifier(bulkField, identifier);
            }
            catch (ObjectMappingException e) {
                result.addError(new YukonMessageSourceResolvable("yukon.common.device.bulk.columnHeader.update.error.noDeviceForIdenifier", identifierBulkFieldColumnHeader.toString(), identifier));
            }
            catch (IllegalArgumentException e) {
                result.addError(new YukonMessageSourceResolvable("yukon.common.device.bulk.columnHeader.update.error.identifierNotApplicable", identifier, identifierBulkFieldColumnHeader.toString(), identifier));
            }
        }
        
        f.close();
    }

    public String startBulkUpdate(ParsedBulkUpdateFileInfo parsedBulkUpdateFileInfo) throws IOException {
        
        BulkFieldColumnHeader identifierBulkFieldColumnHeader = parsedBulkUpdateFileInfo.getIdentifierBulkFieldColumnHeader();
        final BulkField<?, SimpleDevice> identifierBulkField = getBulkYukonDeviceFieldFactory().getBulkField(identifierBulkFieldColumnHeader.getFieldName());
        
        return doStartBulkImport(parsedBulkUpdateFileInfo, BackgroundProcessTypeEnum.UPDATE, new YukonDeviceResolver() {

            @Override
            public SimpleDevice returnDevice(String[] from) {
                
                SimpleDevice device = getBulkFieldService().getYukonDeviceForIdentifier(identifierBulkField, StringUtils.trim(from[0]));
                return device;
            }
        });

    }
}
