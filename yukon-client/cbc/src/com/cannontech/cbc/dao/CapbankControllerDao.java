package com.cannontech.cbc.dao;

import java.util.List;

import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.cbc.model.Capbank;
import com.cannontech.cbc.model.CapbankController;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.search.SearchResult;

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
	
	public void changeSerialNumber(SimpleDevice device, int newSerialNumber);
	
	public List<Integer> getUnassignedControllerIds();
	public SearchResult<LiteCapControlObject> getOrphans(final int start,final int count);
	
}
