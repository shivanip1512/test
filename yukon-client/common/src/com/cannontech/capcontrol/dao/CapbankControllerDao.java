package com.cannontech.capcontrol.dao;

import java.util.List;

import com.cannontech.capcontrol.dao.providers.fields.DeviceAddressFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceCbcFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceDirectCommSettingsFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceScanRateFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceWindowFields;
import com.cannontech.capcontrol.model.CapbankController;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoTemplate;
import com.cannontech.common.search.SearchResult;
import com.cannontech.database.data.point.PointBase;

public interface CapbankControllerDao {
	
    public boolean assignController(int controllerId, String capbankName);
    public boolean assignController(int capbankId, int controllerId);

    public boolean unassignController(CapbankController controller);
    public boolean unassignController(int controller);
    
    public PaoTemplate getCbcPaoTemplate(PaoIdentifier paoIdentifier);
    
    public DeviceFields getDeviceData(PaoIdentifier paoIdentifier);
    public DeviceScanRateFields getDeviceScanRateData(PaoIdentifier paoIdentifier);
    public DeviceWindowFields getDeviceWindowData(PaoIdentifier paoIdentifier);
    public DeviceDirectCommSettingsFields getDeviceDirectCommSettingsData(PaoIdentifier paoIdentifier);
    public DeviceCbcFields getDeviceCbcData(PaoIdentifier paoIdentifier);
    public DeviceAddressFields getDeviceAddressData(PaoIdentifier paoIdentifier);
    
	public void changeSerialNumber(SimpleDevice device, int newSerialNumber);
	
	public List<PointBase> getPointsForPao(int paoId);
	
	public void applyPoints(int deviceId, List<PointBase> points);
	
	public List<Integer> getUnassignedControllerIds();
	public SearchResult<LiteCapControlObject> getOrphans(final int start,final int count);
}