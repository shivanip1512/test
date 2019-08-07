package com.cannontech.web.group;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
import com.cannontech.common.util.FileUploadUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.WebFileUtils;
import com.cannontech.common.exception.FileImportException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.opencsv.CSVReader;

@Controller
@RequestMapping("/updater/*")
@CheckRoleProperty(YukonRoleProperty.BULK_UPDATE_OPERATION)
public class DeviceGroupUpdaterController {

    @Autowired private BulkYukonDeviceFieldFactory bulkYukonDeviceFieldFactory;
    @Autowired private BulkFieldService bulkFieldService;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private TransactionOperations transactionTemplate;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    private static final String baseKey = "yukon.web.modules.tools.deviceGroupUpload.error";
    private Cache<String, Map<String, List<String[]>>> errorResultCache =
            CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build();
    private Logger log = YukonLogManager.getLogger(DeviceGroupUpdaterController.class);

    @RequestMapping("upload")
    public void upload(HttpServletRequest request, LiteYukonUser user, ModelMap model) throws ServletException {

        String error = ServletRequestUtils.getStringParameter(request, "error", null);
        boolean success = ServletRequestUtils.getBooleanParameter(request, "success", false);
        int deviceCount = ServletRequestUtils.getIntParameter(request, "deviceCount", 0);
        int deviceErrorCount = ServletRequestUtils.getIntParameter(request, "deviceErrorCount", 0);
        String resultId = ServletRequestUtils.getStringParameter(request, "resultId", null);
        Boolean ignoreInvalidHeaders = ServletRequestUtils.getBooleanParameter(request, "ignoreInvalidHeaders", true);
        String fileName = ServletRequestUtils.getStringParameter(request, "fileName", null);
        model.addAttribute("ignoreInvalidHeaders", ignoreInvalidHeaders);
        boolean ignoreInvalidIdentifiers = ServletRequestUtils.getBooleanParameter(request, "ignoreInvalidIdentifiers", true);
        model.addAttribute("ignoreInvalidIdentifiers", ignoreInvalidIdentifiers);
  
        model.addAttribute("error", error);
        model.addAttribute("success", success);
        model.addAttribute("deviceCount", deviceCount);
        model.addAttribute("deviceErrorCount", deviceErrorCount);
        model.addAttribute("resultId", resultId);
        model.addAttribute("fileName", fileName);
    }

    @RequestMapping(value = "downloadFileUploadErrors", method = RequestMethod.GET)
    public void downloadFileUploadErrors(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String resultId = ServletRequestUtils.getStringParameter(request, "resultId", null);
        String fileName = ServletRequestUtils.getStringParameter(request, "fileName", null);
        if (resultId != null) {
            String[] headers = errorResultCache.getIfPresent(resultId).get("header").get(0);
            List<String[]> failedRows = errorResultCache.getIfPresent(resultId).get("failedRows");
            WebFileUtils.writeToCSV(response, headers, failedRows,
                FilenameUtils.removeExtension(fileName) + "_errors.csv");
        }
    }

    @RequestMapping("parseUpload")
    public String parseUpload(HttpServletRequest request, LiteYukonUser user, ModelMap model, FlashScope flashScope)
            throws ServletException, IOException {

        boolean createGroups = ServletRequestUtils.getBooleanParameter(request, "createGroups", false);
        boolean ignoreInvalidHeaders = ServletRequestUtils.getBooleanParameter(request, "ignoreInvalidHeaders", false);
        boolean ignoreInvalidIdentifiers = ServletRequestUtils.getBooleanParameter(request, "ignoreInvalidIdentifiers", false);
        int deviceCount = 0;
        
        MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
        MultipartFile dataFile = mRequest.getFile("dataFile");
        
        model.addAttribute("fileName", dataFile.getOriginalFilename());
        // get file from request
        try {
            FileUploadUtils.validateTabularDataUploadFileType(dataFile);
        } catch (FileImportException e) {
            flashScope.setError(new YukonMessageSourceResolvable(e.getMessage()));
            return "redirect:upload";
        }

            InputStream inputStream = dataFile.getInputStream();
                BOMInputStream bomInputStream = new BOMInputStream(inputStream, ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE,
                        ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_32LE, ByteOrderMark.UTF_32BE);
                InputStreamReader inputStreamReader = new InputStreamReader(bomInputStream);
                CSVReader csvReader = new CSVReader(inputStreamReader);

                // header row
                String[] headerRow = csvReader.readNext();
                
                if (headerRow.length < 2) {
                    flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".missingRequiredColumns"));
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
                        
                        List<BulkFieldProcessor<SimpleDevice, String>> processors = new ArrayList<>();
                        Boolean[] isInvalidColumnByIndex = new Boolean[headerRow.length];
                        for (int columnIdx = 1; columnIdx < headerRow.length; columnIdx++) {
                            isInvalidColumnByIndex[columnIdx] = false; // All columns by default marked,
                                                                       // invalid = false
                            header = headerRow[columnIdx].trim();
                            String[] columnTypeParts = header.split(":");
                            columnType = columnTypeParts[0];
                            if (ignoreInvalidHeaders && columnTypeParts.length < 2) {
                                isInvalidColumnByIndex[columnIdx] = true; // Mark column as invalid = true
                                continue;
                            } else {
                                String[] valueParts = columnTypeParts[1].split("=");
                                String dataName = valueParts[0];
                                String dataValue = valueParts[1];
                                if (ignoreInvalidHeaders) {
                                    try {
                                        DeviceGroupUpdaterColumn.valueOf(columnType);
                                    } catch (IllegalArgumentException e) {
                                        isInvalidColumnByIndex[columnIdx] = true; // Mark column as invalid = true
                                        continue;
                                    }
                                }
                                processors.add(deviceGroupProcessorFactory.getProcessor(columnType, dataName,
                                    dataValue, createGroups));
                            }
                        }
                        if (processors.size() != 0) {
                            // process rows
                            ProcessingResultInfo processingResultInfo = runProcessing(csvReader, identifierBulkField, 
                                processors, isInvalidColumnByIndex, ignoreInvalidIdentifiers);
                            if (StringUtils.isNoneBlank(processingResultInfo.getError())) {
                                flashScope.setError(YukonMessageSourceResolvable.createDefaultWithoutCode(
                                    processingResultInfo.getError()));
                            } else {
                                deviceCount = processingResultInfo.getDeviceCount();
                                model.addAttribute("deviceCount", deviceCount);
                                if (!processingResultInfo.getUnknownDevices().isEmpty()) {
                                    log.error("Unable to upload unknown devices: "
                                        + StringUtils.join(processingResultInfo.getUnknownDevices(), ','));
                                    String resultId = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
                                    List<String[]> headers = new ArrayList<>();
                                    headers.add(headerRow);
                                    Map<String, List<String[]>> invalidImportData =
                                        new ConcurrentHashMap<String, List<String[]>>();
                                    invalidImportData.put("header", headers);
                                    invalidImportData.put("failedRows", processingResultInfo.getFailedRows());
                                    errorResultCache.put(resultId, invalidImportData);
                                    model.addAttribute("resultId", resultId);
                                    model.addAttribute("deviceErrorCount", invalidImportData.get("failedRows").size());
                                    flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".unknownDevices",
                                        invalidImportData.get("failedRows").size()));
                                }
                            }
                        } else {
                            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".missingUpdateColumn"));
                        }
                    } catch (InvalidIndentifierException e) {
                        Set<BulkFieldColumnHeader> identifierFields = bulkFieldService.getUpdateIdentifierBulkFieldColumnHeaders();
                        String error = "Error (line 1): Invalid identifier type: " + e.getIdentifier() + ". Valid identifier types are: " + StringUtils.join(identifierFields, " ,");
                        log.error(error, e);
                        flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".invalidIdentifiers"));
                    } catch (IllegalArgumentException e) {
                        String error = "Error (line 1): Invalid header type: " + columnType + ". Valid header types: " + StringUtils.join(DeviceGroupUpdaterColumn.values(), " ,");
                        log.error(error, e);
                        flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".invalidHeaders"));
                    } catch (IndexOutOfBoundsException e) {
                        String error = "Error (line 1): Invalid header syntax.";
                        log.error(error, e);
                        flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".invalidHeaders"));
                    } catch (NotFoundException e) {
                        String error = "Error (line 1): " + e.getMessage() + ". Please check the spelling, or if you would like groups to automatically be created if they do not exist, check the Create Groups option.";
                        log.error(error, e);
                        flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".unknownGroup"));
                    } 
                }
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
    
    private ProcessingResultInfo runProcessing(final CSVReader csvReader, final BulkField<?, SimpleDevice> identifierBulkField, final List<BulkFieldProcessor<SimpleDevice, String>> processors, Boolean[] isInvalidColumnByIndex, boolean ignoreInvalidIdentifiers) {
        
        ProcessingResultInfo processingResultInfo = transactionTemplate.execute(new TransactionCallback<ProcessingResultInfo>() {
            @Override
            public ProcessingResultInfo doInTransaction(TransactionStatus status) {
                
                String processError = null;
                int currentLineNumber = 1;
                String currentIdentifier = "";
                String currentColumnValue = "";
                int deviceCount = 0;
                List<String> unknownDevices = new ArrayList<>();
                List<String[]> failedRows = new ArrayList<>();
                String [] line = null;
                try {
                    while ((line = csvReader.readNext()) != null) {
                        currentIdentifier = StringUtils.trim(line[0]);
                        SimpleDevice device = null;
                        try {
                            device = bulkFieldService.getYukonDeviceForIdentifier(identifierBulkField, currentIdentifier);
                        } catch (ObjectMappingException e) {
                           if(!ignoreInvalidIdentifiers){
                               throw e;
                           }
                        }
                        int idx = 1;
                        for (BulkFieldProcessor<SimpleDevice, String> processor : processors) {
                            while (isInvalidColumnByIndex[idx]) {
                                idx++;
                            }
                            currentColumnValue = line[idx++].trim();
                            if (device != null) {
                                processor.updateField(device, currentColumnValue);
                            }
                        }
                        currentLineNumber++;
                        if (device != null) {
                            deviceCount++;
                        } else {
                            unknownDevices.add(currentIdentifier);
                            failedRows.add(line);
                        }
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
                
                return new ProcessingResultInfo(processError, deviceCount, unknownDevices, failedRows);
            }
        });
        
        return processingResultInfo;
    }
    
    private class ProcessingResultInfo {
        
        private String error = null;
        private int deviceCount = 0;
        private List<String> unknownDevices = new ArrayList<>();
        private List<String[]> failedRows = new ArrayList<>();
        
        ProcessingResultInfo(String error, int deviceCount,  List<String> unknownDevices, List<String[]> failedRows) {
            this.error = error;
            this.deviceCount = deviceCount;
            this.unknownDevices = unknownDevices;
            this.failedRows = failedRows;
        }
        
        public String getError() {
            return error;
        }
        public int getDeviceCount() {
            return deviceCount;
        }

        public List<String> getUnknownDevices() {
            return unknownDevices;
        }

        public List<String[]> getFailedRows() {
            return failedRows;
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
