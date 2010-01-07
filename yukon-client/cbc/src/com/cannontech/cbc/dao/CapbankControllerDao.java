package com.cannontech.cbc.dao;

import java.util.List;

import com.cannontech.cbc.model.Capbank;
import com.cannontech.cbc.model.CapbankController;
import com.cannontech.cbc.model.LiteCapControlObject;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.database.TransactionException;

public interface CapbankControllerDao {

	public void add(CapbankController capbankController) throws TransactionException;;
	public void add(CapbankController capbankController, boolean addPoints) throws TransactionException;
	
	public boolean update(CapbankController capbankController);
	
	public boolean remove(CapbankController capbankController);
	
	public boolean createControllerFromTemplate(String templateName, CapbankController controller);
	
    public boolean assignController(Capbank capbank, CapbankController controller);
    public boolean assignController(int capbankId, int controllerId);

    public boolean unassignController(CapbankController controller);
    public boolean unassignController(int controller);
	
	public void changeSerialNumber(SimpleDevice device, int newSerialNumber);
	
	public List<Integer> getUnassignedControllerIds();
	public List<LiteCapControlObject> getOrphans();
	
}
