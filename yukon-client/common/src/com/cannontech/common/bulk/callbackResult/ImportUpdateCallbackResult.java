package com.cannontech.common.bulk.callbackResult;

import java.util.List;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.bulk.processor.ProcessorCallbackException;
import com.cannontech.common.bulk.service.BulkFileInfo;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

public class ImportUpdateCallbackResult extends BackgroundProcessBulkProcessorCallback<String[]> implements BackgroundProcessResultHolder {

	private List<BulkFieldColumnHeader> bulkFieldColumnHeaders;
	private BulkFileInfo bulkFileInfo;
	private StoredDeviceGroup successGroup;
	private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
	private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
	
	public ImportUpdateCallbackResult(BackgroundProcessTypeEnum backgroundProcessType,
									List<BulkFieldColumnHeader> bulkFieldColumnHeaders,
									BulkFileInfo bulkFileInfo,
									String resultsId, 
									StoredDeviceGroup successGroup, 
									DeviceGroupMemberEditorDao deviceGroupMemberEditorDao,
									DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
		
		super(backgroundProcessType, resultsId, bulkFileInfo.getDataCount());
	
		this.bulkFieldColumnHeaders = bulkFieldColumnHeaders;
		this.bulkFileInfo = bulkFileInfo;
		this.successGroup = successGroup;
		this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
		this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
	}
	
	public BulkFileInfo getBulkFileInfo() {
		return bulkFileInfo;
	}
	
	// callback overrides
	@Override
    public void processedObject(int rowNumber, YukonDevice object) {
        super.processedObject(rowNumber, object);
        deviceGroupMemberEditorDao.addDevices(successGroup, object);
    }
	
	@Override
    public void receivedProcessingException(int rowNumber, String[] object, ProcessorCallbackException e) {
        super.receivedProcessingException(rowNumber, object, e);
    }
	
	// results overrides
	@Override
	public boolean isSuccessDevicesSupported() {
		return true;
	}
	@Override
	public DeviceCollection getSuccessDeviceCollection() {
		return deviceGroupCollectionHelper.buildDeviceCollection(successGroup);
	}
	
	@Override
	public boolean isFailureDevicesSupported() {
		return false;
	}
	@Override
	public DeviceCollection getFailureDeviceCollection() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isFailureReasonsListSupported() {
		return true;
	}
	
	@Override
	public boolean isFailureFileSupported() {
		return true;
	}
	
	@Override
	public boolean isBulkFieldListingSupported() {
		return true;
	}
	@Override
    public List<BulkFieldColumnHeader> getBulkFieldColumnHeaders() {
		return bulkFieldColumnHeaders;
	}
}