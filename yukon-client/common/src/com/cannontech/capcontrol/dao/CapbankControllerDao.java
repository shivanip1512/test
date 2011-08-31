package com.cannontech.capcontrol.dao;

import java.util.List;

import com.cannontech.capcontrol.dao.providers.fields.DeviceAddressFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceCbcFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceDirectCommSettingsFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceScanRateFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceWindowFields;
import com.cannontech.capcontrol.model.Capbank;
import com.cannontech.capcontrol.model.CapbankController;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.search.SearchResult;
import com.cannontech.database.data.point.PointBase;

public interface CapbankControllerDao {

	public void add(CapbankController capbankController);
	public void add(CapbankController capbankController, boolean addPoints);
	
	public boolean update(CapbankController capbankController);
	
	public boolean remove(CapbankController capbankController);
	
	public boolean createControllerFromTemplate(String templateName, CapbankController controller);
	
    public boolean assignController(Capbank capbank, CapbankController controller);
    public boolean assignController(int capbankId, int controllerId);

    public boolean unassignController(CapbankController controller);
    public boolean unassignController(int controller);
    
    public DeviceFields getDeviceData(PaoIdentifier paoIdentifier);
    public DeviceScanRateFields getDeviceScanRateData(PaoIdentifier paoIdentifier);
    public DeviceWindowFields getDeviceWindowData(PaoIdentifier paoIdentifier);
    public DeviceDirectCommSettingsFields getDeviceDirectCommSettingsData(PaoIdentifier paoIdentifier);
    public DeviceCbcFields getDeviceCbcData(PaoIdentifier paoIdentifier);
    public DeviceAddressFields getDeviceAddressData(PaoIdentifier paoIdentifier);
    
    // Various CBC required database inserts.
    public void insertDeviceData(PaoIdentifier paoIdentifier, DeviceFields fields);
    public void insertScanRateData(PaoIdentifier paoIdentifier, DeviceScanRateFields fields);
	public void insertDeviceWindowData(PaoIdentifier paoIdentifier, DeviceWindowFields fields);
	public void insertCommSettingsData(PaoIdentifier paoIdentifier, DeviceDirectCommSettingsFields fields);
    public void insertDeviceCbcData(PaoIdentifier paoIdentifier, DeviceCbcFields fields);
    public void insertDeviceAddressData(PaoIdentifier paoIdentifier, DeviceAddressFields fields);
	
    // Various CBC required database updates.
    public void updateDeviceData(PaoIdentifier paoIdentifier, DeviceFields fields);
    public void updateScanRateData(PaoIdentifier paoIdentifier, DeviceScanRateFields fields);
	public void updateDeviceWindowData(PaoIdentifier paoIdentifier, DeviceWindowFields fields);
	public void updateCommSettingsData(PaoIdentifier paoIdentifier, DeviceDirectCommSettingsFields fields);
    public void updateDeviceCbcData(PaoIdentifier paoIdentifier, DeviceCbcFields fields);
    public void updateDeviceAddressData(PaoIdentifier paoIdentifier, DeviceAddressFields fields);
    
    // CBC required database deletions.
    public void deleteControllerData(PaoProviderTableEnum table, PaoIdentifier paoIdentifier);
    
	public void changeSerialNumber(SimpleDevice device, int newSerialNumber);
	
	public List<PointBase> getPointsForPao(int paoId);
	
	public void applyPoints(int deviceId, List<PointBase> points);
	
	public List<Integer> getUnassignedControllerIds();
	public SearchResult<LiteCapControlObject> getOrphans(final int start,final int count);
	
}
