package com.cannontech.web.group;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.common.bulk.field.BulkField;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.bulk.field.BulkFieldService;
import com.cannontech.common.bulk.field.impl.BulkYukonDeviceFieldFactory;
import com.cannontech.common.bulk.field.processor.BulkFieldProcessor;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.tools.csv.CSVReader;

@Controller
@RequestMapping("/updater/*")
public class DeviceGroupUpdaterController {
	
	private BulkYukonDeviceFieldFactory bulkYukonDeviceFieldFactory;
	private BulkFieldService bulkFieldService;
	private DeviceGroupEditorDao deviceGroupEditorDao;
	private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;

	@RequestMapping
    public void upload(HttpServletRequest request, LiteYukonUser user, ModelMap model) throws ServletException {

		String error = ServletRequestUtils.getStringParameter(request, "error", null);
		boolean success = ServletRequestUtils.getBooleanParameter(request, "success", false);
		
		model.addAttribute("error", error);
		model.addAttribute("success", success);
    }
	
	@RequestMapping
    public String parseUpload(HttpServletRequest request, LiteYukonUser user, ModelMap model) throws ServletException, IOException {

		String error = null;
		
		MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest)request;
        MultipartFile dataFile = mRequest.getFile("dataFile");
        
        // get file from request
        if (dataFile == null || StringUtils.isBlank(dataFile.getOriginalFilename())) {
        	error = "No file selected.";
        } else {
        	
            InputStream inputStream = dataFile.getInputStream();
            if (inputStream.available() <= 0) {
            	error = "File is empty.";
            }
            
            // header row
            InputStreamReader inputStreamReader = new InputStreamReader(dataFile.getInputStream());
            CSVReader csvReader = new CSVReader(inputStreamReader);
            String[] headerRow = csvReader.readNext();
            
            if (headerRow.length < 2) {
            	error = "File header should contain an Identifier column and at least one action column.";
            } else {
            
            	int currentLineNumber = 0;
	            String currentIdentifier = "";
	            String currentColumnValue = "";
	            BulkField<?, YukonDevice> identifierBulkField = null;
	            
            	try {
		            
		            // identifier bulk field
		            BulkFieldColumnHeader identifierColunHeader = BulkFieldColumnHeader.valueOf(headerRow[currentLineNumber++].trim());
		            identifierBulkField = bulkYukonDeviceFieldFactory.getBulkField(identifierColunHeader.getFieldName());
		            
		            // processors
		            DeviceGroupProcessorFactory deviceGroupProcessorFactory = new DeviceGroupProcessorFactory();
		            
		            List<BulkFieldProcessor<YukonDevice, String>> processors = new ArrayList<BulkFieldProcessor<YukonDevice, String>>();
		            for (int columnIdx = 1; columnIdx < headerRow.length; columnIdx++) {
		            	
		            	String header = headerRow[columnIdx].trim();
		            	
	            		String[] columnTypeParts = header.split(":");
	            		String columnType = columnTypeParts[0];
	            		String[] valueParts = columnTypeParts[1].split("=");
	            		String dataName = valueParts[0];
	            		String dataValue = valueParts[1];
	            		
	            		processors.add(deviceGroupProcessorFactory.getProcessor(columnType, dataName, dataValue));
		            }
		            
		            // process rows
	            	String [] line = null;
		            while((line = csvReader.readNext()) != null) {
		            		
		                currentIdentifier = StringUtils.trim(line[0]);
		                YukonDevice device = bulkFieldService.getYukonDeviceForIdentifier(identifierBulkField, currentIdentifier);
		                
		                int idx = 1;
		                for (BulkFieldProcessor<YukonDevice, String> processor : processors) {
		                	
		                	currentColumnValue = line[idx++].trim();
	                		processor.updateField(device, currentColumnValue);
		                }
		                
		                currentLineNumber++;
		            }
		            
	            } catch (ObjectMappingException e) {
	            	error = "Error (line " + currentLineNumber + "): No device with " + identifierBulkField.getInputSource().getDisplayName() + ": " + currentIdentifier + ".";
	            } catch (IndexOutOfBoundsException e) {
	            	error = "Error (line " + currentLineNumber + "): Incompete row, each row must have a value for each header column.";
	            } catch (IllegalArgumentException e) {
	            	Set<BulkFieldColumnHeader> identifierFields = bulkFieldService.getUpdateIdentifierBulkFieldColumnHeaders();
	            	error = "Error (line 1): Invalid header column. Identifier types: " + StringUtils.join(identifierFields, " ,") + ". Header types: " + StringUtils.join(DeviceGroupUpdaterColumn.values(), " ,");
	        	} finally {
	        		csvReader.close();
	        	}
            }
        }
        
        model.addAttribute("error", error);
        model.addAttribute("success", error == null);
        return "redirect:upload";
    }
	
	
	
	// PREFIX PROCESSOR
	private class DeviceGroupPrefixProcessor implements BulkFieldProcessor<YukonDevice, String> {

		private StoredDeviceGroup group;
		
		public DeviceGroupPrefixProcessor (String groupName) {
			this.group = deviceGroupEditorDao.getStoredGroup(groupName, true);
		}

		@Override
		public void updateField(YukonDevice device, String subgroup) {

			// remove from all child groups
			for (StoredDeviceGroup childGroup : deviceGroupEditorDao.getStaticGroups(this.group)) {
				removeDeviceFromChildGroup(childGroup, device);
			}
			
			// find subgroup, add device to it
			StoredDeviceGroup subgroupGroup = deviceGroupEditorDao.getGroupByName(this.group, subgroup, true);
			deviceGroupMemberEditorDao.addDevices(subgroupGroup, device);
		}
		
		private void removeDeviceFromChildGroup(StoredDeviceGroup removeFromGroup, YukonDevice device) {
			
			deviceGroupMemberEditorDao.removeDevices(removeFromGroup, device);
			
			for (StoredDeviceGroup childGroup : deviceGroupEditorDao.getStaticGroups(removeFromGroup)) {
				removeDeviceFromChildGroup(childGroup, device);
			}
		}
		
		@Override
		public Set<BulkField<?, YukonDevice>> getUpdatableFields() {
			return null;
		}
	}
	
	// GROUP PROCESSOR
	private class DeviceGroupGroupProcessor implements BulkFieldProcessor<YukonDevice, String> {
		
		private StoredDeviceGroup group;
		
		public DeviceGroupGroupProcessor (String groupName) {
			this.group = deviceGroupEditorDao.getStoredGroup(groupName, true);
		}

		@Override
		public void updateField(YukonDevice device, String addStr) {

			boolean add = Boolean.valueOf(addStr);
			if (add) {
				deviceGroupMemberEditorDao.addDevices(this.group, device);
			} else {
				deviceGroupMemberEditorDao.removeDevices(this.group, device);
			}
		}
		
		@Override
		public Set<BulkField<?, YukonDevice>> getUpdatableFields() {
			return null;
		}
	}
	
	private class DeviceGroupProcessorFactory {
		
		public BulkFieldProcessor<YukonDevice, String> getProcessor(String columnType, String dataName, String dataValue) throws IllegalArgumentException {
			
			DeviceGroupUpdaterColumn deviceGroupUpdaterColumn = DeviceGroupUpdaterColumn.valueOf(columnType);
			
			if (deviceGroupUpdaterColumn.equals(DeviceGroupUpdaterColumn.DEVICE_GROUP_PREFIX)) {
				return new DeviceGroupPrefixProcessor(dataValue); 
			} else if (deviceGroupUpdaterColumn.equals(DeviceGroupUpdaterColumn.DEVICE_GROUP_SET)) {
				return new DeviceGroupGroupProcessor(dataValue);
			} else {
				throw new IllegalArgumentException("Invalid processorType");
			}
		}
	}
	
	
	@Autowired
	public void setBulkYukonDeviceFieldFactory(BulkYukonDeviceFieldFactory bulkYukonDeviceFieldFactory) {
		this.bulkYukonDeviceFieldFactory = bulkYukonDeviceFieldFactory;
	}
	@Autowired
	public void setBulkFieldService(BulkFieldService bulkFieldService) {
		this.bulkFieldService = bulkFieldService;
	}
	@Autowired
	public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
		this.deviceGroupEditorDao = deviceGroupEditorDao;
	}
	@Autowired
	public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
		this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
	}
}
