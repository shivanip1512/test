package com.cannontech.common.bulk.callbackResult;

import java.util.List;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.bulk.processor.ProcessorCallbackException;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

public class MassDeleteCallbackResult extends BackgroundProcessBulkProcessorCallback<YukonDevice> implements BackgroundProcessResultHolder {

	private DeviceCollection deviceCollection;
	private StoredDeviceGroup processingExceptionGroup;
	private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
	private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
	
	public MassDeleteCallbackResult(String resultsId, 
									DeviceCollection deviceCollection, 
									StoredDeviceGroup processingExceptionGroup, 
									DeviceGroupMemberEditorDao deviceGroupMemberEditorDao,
									DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
		
		super(BackgroundProcessTypeEnum.MASS_DELETE, resultsId, (int)deviceCollection.getDeviceCount());
	
		this.deviceCollection = deviceCollection;
		this.processingExceptionGroup = processingExceptionGroup;
		this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
		this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
	}
	
	public DeviceCollection getDeviceCollection() {
		return deviceCollection;
	}
	
	// callback overrides
	@Override
    public void receivedProcessingException(int rowNumber, YukonDevice object, ProcessorCallbackException e) {
        super.receivedProcessingException(rowNumber, object, e);
        deviceGroupMemberEditorDao.addDevices(processingExceptionGroup, object);
    }
	
	// results overrides
	@Override
	public boolean isSuccessDevicesSupported() {
		return false;
	}
	@Override
	public DeviceCollection getSuccessDeviceCollection() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean isFailureDevicesSupported() {
		return true;
	}
	@Override
	public DeviceCollection getFailureDeviceCollection() {
		return deviceGroupCollectionHelper.buildDeviceCollection(processingExceptionGroup);
	}

	@Override
	public boolean isFailureReasonsListSupported() {
		return true;
	}
	
	@Override
	public boolean isFailureFileSupported() {
		return false;
	}
	
	@Override
	public boolean isBulkFieldListingSupported() {
		return false;
	}
	@Override
    public List<BulkFieldColumnHeader> getBulkFieldColumnHeaders() {
		throw new UnsupportedOperationException();
	}
}
