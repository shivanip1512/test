package com.cannontech.cbc.dao;

import java.util.List;

import com.cannontech.cbc.model.Capbank;
import com.cannontech.cbc.model.CapbankController;
import com.cannontech.cbc.model.LiteCapControlObject;
import com.cannontech.common.device.YukonDevice;

public interface CapbankControllerDao {

	public boolean add(CapbankController capbankController);
	
	public boolean update(CapbankController capbankController);
	
	public boolean remove(CapbankController capbankController);
	
    public boolean assignController(Capbank capbank, CapbankController controller);
    public boolean assignController(int capbankId, int controllerId);

    public boolean unassignController(Capbank capbank, CapbankController controller);
    public boolean unassignController(int capbankId, int controller);
	
	public void changeSerialNumber(YukonDevice device, int newSerialNumber);
	
	public List<Integer> getUnassignedControllerIds();
	public List<LiteCapControlObject> getOrphans();
	
}
