package com.cannontech.capcontrol.dao;

import java.util.List;

import com.cannontech.capcontrol.dao.providers.fields.DeviceAddressFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceScanRateFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceWindowFields;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.search.SearchResult;
import com.cannontech.database.data.point.PointBase;

public interface CapbankControllerDao {
	
    public boolean assignController(int controllerId, String capbankName);
    public boolean assignController(int capbankId, int controllerId);

    public boolean unassignController(int controller);
    
    // TODO: These should be removed ASAP and moved to the Providers in the PaoCreation Service -jg
    public DeviceFields getDeviceData(PaoIdentifier paoIdentifier);
    public DeviceScanRateFields getDeviceScanRateData(PaoIdentifier paoIdentifier);
    public DeviceWindowFields getDeviceWindowData(PaoIdentifier paoIdentifier);
    public DeviceAddressFields getDeviceAddressData(PaoIdentifier paoIdentifier);
	
	public List<PointBase> getPointsForPao(int paoId);
	
	public SearchResult<LiteCapControlObject> getOrphans(final int start,final int count);
}