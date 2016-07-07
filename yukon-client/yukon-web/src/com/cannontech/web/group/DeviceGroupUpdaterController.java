package com.cannontech.web.group;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.field.BulkField;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.bulk.field.BulkFieldService;
import com.cannontech.common.bulk.field.impl.BulkYukonDeviceFieldFactory;
import com.cannontech.common.bulk.field.processor.BulkFieldProcessor;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.groups.IllegalGroupNameException;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.tools.csv.CSVReader;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/updater/*")
@CheckRoleProperty(YukonRoleProperty.BULK_UPDATE_OPERATION)
public class DeviceGroupUpdaterController {
    
    @Autowired private BulkYukonDeviceFieldFactory bulkYukonDeviceFieldFactory;
    @Autowired private BulkFieldService bulkFieldService;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private TransactionOperations transactionTemplate;
    
    private Logger log = YukonLogManager.getLogger(DeviceGroupUpdaterController.class);

    @RequestMapping("upload")
    public void upload(HttpServletRequest request, LiteYukonUser user, ModelMap model) throws ServletException {

        String error = ServletRequestUtils.getStringParameter(request, "error", null);
        boolean success = ServletRequestUtils.getBooleanParameter(request, "success", false);
        int deviceCount = ServletRequestUtils.getIntParameter(request, "deviceCount", 0);
        // options checked included? pass along
        Boolean createGroups = ServletRequestUtils.getBooleanParameter(request, "createGroups", true);
        Boolean ignoreInvalidHeaders = ServletRequestUtils.getBooleanParameter(request, "ignoreInvalidCols", false);
        model.addAttribute("ignoreInvalidHeaders", ignoreInvalidHeaders);
        model.addAttribute("createGroups", createGroups);
        
        model.addAttribute("error", error);
        model.addAttribute("success", success);
        model.addAttribute("deviceCount", deviceCount);
    }
    
    @RequestMapping("parseUpload")
    public String parseUpload(HttpServletRequest request, LiteYukonUser user, ModelMap model) throws ServletException, IOException {

        boolean createGroups = ServletRequestUtils.getBooleanParameter(request, "createGroups", false);
        boolean ignoreInvalidHeaders = ServletRequestUtils.getBooleanParameter(request, "ignoreInvalidHeaders", false);
        String error = null;
        int deviceCount = 0;
        
        MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest)request;
        MultipartFile dataFile = mRequest.getFile("dataFile");
        
        // get file from request
        if (dataFile == null || StringUtils.isBlank(dataFile.getOriginalFilename())) {
            error = "No file selected.";
        } else {
            InputStream inputStream = dataFile.getInputStream();
            
            if (inputStream.available() <= 0) {
                error = "File is empty.";
            } else {
                BOMInputStream bomInputStream = new BOMInputStream(inputStream, ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE,
                        ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_32LE, ByteOrderMark.UTF_32BE);
                InputStreamReader inputStreamReader = new InputStreamReader(bomInputStream);
                CSVReader csvReader = new CSVReader(inputStreamReader);

                // header row
                String[] headerRow = csvReader.readNext();
                
                if (headerRow.length < 2) {
                    error = "File header should contain an Identifier column and at least one action column.";
                } else {
                
                    BulkField<?, SimpleDevice> identifierBulkField = null;
                    String header = "";
                    String columnType = "";
                    
                    try {
                        
                        // identifier bulk field
                        String identifier = "";
                        BulkFieldColumnHeader identifierColunHeader;
                        try {
                            identifier = headerRow[0].trim();
                            identifierColunHeader = BulkFieldColumnHeader.valueOf(identifier);
                        } catch (IllegalArgumentException e) {
                            throw new InvalidIndentifierException(identifier);
                        }
                        identifierBulkField = bulkYukonDeviceFieldFactory.getBulkField(identifierColunHeader.getFieldName());
                        
                        // processors
                        DeviceGroupProcessorFactory deviceGroupProcessorFactory = new DeviceGroupProcessorFactory();
                        
                        List<BulkFieldProcessor<SimpleDevice, String>> processors = new ArrayList<BulkFieldProcessor<SimpleDevice, String>>();
                        for (int columnIdx = 1; columnIdx < headerRow.length; columnIdx++) {

                            header = headerRow[columnIdx].trim();
                            String[] columnTypeParts = header.split(":");
                            columnType = columnTypeParts[0];
                            if (ignoreInvalidHeaders && columnTypeParts.length < 2) {
                                continue;
                            } else {
                                String[] valueParts = columnTypeParts[1].split("=");
                                String dataName = valueParts[0];
                                String dataValue = valueParts[1];
                                processors.add(deviceGroupProcessorFactory.getProcessor(columnType, dataName,
                                    dataValue, createGroups));
                            }
                        }
                        // process rows
                        ProcessingResultInfo processingResultInfo = runProcessing(csvReader, identifierBulkField, processors);
                        error = processingResultInfo.getError();
                        deviceCount = processingResultInfo.getDeviceCount();
                        
                    } catch (InvalidIndentifierException e) {
                        Set<BulkFieldColumnHeader> identifierFields = bulkFieldService.getUpdateIdentifierBulkFieldColumnHeaders();
                        error = "Error (line 1): Invalid identifier type: " + e.getIdentifier() + ". Valid identifier types are: " + StringUtils.join(identifierFields, " ,");
                        log.error(error, e);
                    } catch (IllegalArgumentException e) {
                        error = "Error (line 1): Invalid header type: " + columnType + ". Valid header types: " + StringUtils.join(DeviceGroupUpdaterColumn.values(), " ,");
                        log.error(error, e);
                    } catch (IndexOutOfBoundsException e) {
                        error = "Error (line 1): Invalid header syntax.";
                        log.error(error, e);
                    } catch (NotFoundException e) {
                        error = "Error (line 1): " + e.getMessage() + ". Please check the spelling, or if you would like groups to automatically be created if they do not exist, check the Create Groups option.";
                        log.error(error, e);
                    } 
                }
            }
        }
        
        model.addAttribute("error", error);
        model.addAttribute("success", error == null);
        model.addAttribute("deviceCount", deviceCount);
        
        return "redirect:upload";
    }
    
    private class InvalidIndentifierException extends RuntimeException {
        private String identifier;
        public InvalidIndentifierException(String identifier) {
            this.identifier = identifier;
        }
        public String getIdentifier() {
            return identifier;
        }
    }
    
    private ProcessingResultInfo runProcessing(final CSVReader csvReader, final BulkField<?, SimpleDevice> identifierBulkField, final List<BulkFieldProcessor<SimpleDevice, String>> processors) {
        
        ProcessingResultInfo processingResultInfo = transactionTemplate.execute(new TransactionCallback<ProcessingResultInfo>() {
            public ProcessingResultInfo doInTransaction(TransactionStatus status) {
                
                String processError = null;
                int currentLineNumber = 1;
                String currentIdentifier = "";
                String currentColumnValue = "";
                int deviceCount = 0;
                String [] line = null;
                
                try {
                    
                    while((line = csvReader.readNext()) != null) {
                            
                        currentIdentifier = StringUtils.trim(line[0]);
                        SimpleDevice device = bulkFieldService.getYukonDeviceForIdentifier(identifierBulkField, currentIdentifier);
                        
                        int idx = 1;
                        for (BulkFieldProcessor<SimpleDevice, String> processor : processors) {
                            
                            currentColumnValue = line[idx++].trim();
                            processor.updateField(device, currentColumnValue);
                        }
                        
                        currentLineNumber++;
                        deviceCount++;
                    }
                    
                } catch (IOException e) {
                    status.setRollbackOnly();
                    processError = "Can't read file";
                    log.error(processError, e);
                } catch (ObjectMappingException e) {
                    status.setRollbackOnly();
                    processError = "Error (line " + currentLineNumber + "): No device with " + identifierBulkField.getInputSource().getDisplayName() + ": " + currentIdentifier + ".";
                    log.error(processError, e);
                } catch (IndexOutOfBoundsException e) {
                    status.setRollbackOnly();
                    processError = "Error (line " + currentLineNumber + "): Incomplete row, each row must have a value for each header column.";
                    log.error(processError, e);
                } catch (IllegalGroupNameException e) {
                    status.setRollbackOnly();
                    processError = "Error (line " + currentLineNumber + "): " + e.getMessage();
                    log.error(processError, e);
                } catch (NotFoundException e) {
                    status.setRollbackOnly();
                    processError = e.getMessage() + ". Please check the spelling, or if you would like groups to automatically be created if they do not exist, check the Create Groups option.";
                    log.error(processError, e);
                } finally {
                    try {
                        csvReader.close();
                    } catch (IOException e){}
                }
                
                return new ProcessingResultInfo(processError, deviceCount);
            }
        });
        
        return processingResultInfo;
    }
    
    private class ProcessingResultInfo {
        
        private String error = null;
        private int deviceCount = 0;
        
        ProcessingResultInfo(String error, int deviceCount) {
            this.error = error;
            this.deviceCount = deviceCount;
        }
        
        public String getError() {
            return error;
        }
        public int getDeviceCount() {
            return deviceCount;
        }
    }
    
    /** PREFIX PROCESSOR */
    private class DeviceGroupPrefixProcessor implements BulkFieldProcessor<SimpleDevice, String> {

        private StoredDeviceGroup group;
        private boolean createGroups;
        
        public DeviceGroupPrefixProcessor (String groupName, boolean createGroups) {
            this.group = deviceGroupEditorDao.getStoredGroup(groupName, createGroups);
            this.createGroups = createGroups;
        }

        @Override
        public void updateField(SimpleDevice device, String subgroup) {
            
            if (StringUtils.isBlank(subgroup)) {
                log.debug("DeviceGroupPrefixProcessor - skipping blank subgroup, will not process device: " + device);
                return;
            }

            // remove from all child groups
            for (StoredDeviceGroup childGroup : deviceGroupEditorDao.getStaticGroups(this.group)) {
                removeDeviceFromChildGroup(childGroup, device);
            }
            
            // find subgroup, add device to it
            StoredDeviceGroup subgroupGroup = deviceGroupEditorDao.getGroupByName(this.group, subgroup, this.createGroups);
            deviceGroupMemberEditorDao.addDevices(subgroupGroup, device);
        }
        
        private void removeDeviceFromChildGroup(StoredDeviceGroup removeFromGroup, SimpleDevice device) {
            
            deviceGroupMemberEditorDao.removeDevices(removeFromGroup, device);
            
            for (StoredDeviceGroup childGroup : deviceGroupEditorDao.getStaticGroups(removeFromGroup)) {
                removeDeviceFromChildGroup(childGroup, device);
            }
        }
        
        @Override
        public Set<BulkField<?, SimpleDevice>> getUpdatableFields() {
            return null;
        }
    }
    
    /** GROUP PROCESSOR */
    private class DeviceGroupGroupProcessor implements BulkFieldProcessor<SimpleDevice, String> {
        
        private StoredDeviceGroup group;
        
        public DeviceGroupGroupProcessor (String groupName, boolean createGroups) {
            this.group = deviceGroupEditorDao.getStoredGroup(groupName, createGroups);
        }

        @Override
        public void updateField(SimpleDevice device, String addStr) {

            boolean add = Boolean.valueOf(addStr);
            if (add) {
                deviceGroupMemberEditorDao.addDevices(this.group, device);
            } else {
                deviceGroupMemberEditorDao.removeDevices(this.group, device);
            }
        }
        
        @Override
        public Set<BulkField<?, SimpleDevice>> getUpdatableFields() {
            return null;
        }
    }
    
    private class DeviceGroupProcessorFactory {
        
        public BulkFieldProcessor<SimpleDevice, String> getProcessor(String columnType, String dataName, String dataValue, boolean createGroups) throws IllegalArgumentException {
            
            DeviceGroupUpdaterColumn deviceGroupUpdaterColumn = DeviceGroupUpdaterColumn.valueOf(columnType);
            
            if (deviceGroupUpdaterColumn.equals(DeviceGroupUpdaterColumn.DEVICE_GROUP_PREFIX)) {
                return new DeviceGroupPrefixProcessor(dataValue, createGroups); 
            } else if (deviceGroupUpdaterColumn.equals(DeviceGroupUpdaterColumn.DEVICE_GROUP_SET)) {
                return new DeviceGroupGroupProcessor(dataValue, createGroups);
            } else {
                throw new IllegalArgumentException("Invalid processorType");
            }
        }
    }
    
}
