package com.cannontech.cbc.service;

import com.cannontech.capcontrol.model.CapbankController;
import com.cannontech.common.pao.PaoType;

public interface CapControlCreationService {

	/**
	 * Adds the area to the database.
	 * @param area
	 */
    public int createArea(String name);
    
    public int createSpecialArea(String name);
    
    public int createRegulator(String name, PaoType paoType);
	
	/**
	 * Adds a Substation to the database as an orphan.
	 * 
	 * @param substation
	 */
    public int createSubstation(String name);
	
	public boolean assignSubstation(int substationId, int areaId);
	public boolean assignSubstation(int substationId, String areaName);
	public boolean unassignSubstation(int substationId);
	
	/**
	 * Adds the Substation bus to the database as an orphan.
	 * 
	 * @param subBus
	 */
	public int createSubstationBus(String name);
	
	public boolean assignSubstationBus(int substationBusId, int substationId);
	public boolean assignSubstationBus(int substationBusId, String substationName);
	public boolean unassignSubstationBus(int substationBudId);
	
	/**
	 * Adds the Feeder to the database as an orphan. 
	 * 
	 * @param feeder
	 */
	public int createFeeder(String name);
	
	public boolean assignFeeder(int feederId, int subBusId);
	public boolean assignFeeder(int feederId, String subBusName);
	public boolean unassignFeeder(int feederId);
	
	/**
	 * Adds the CapBank to the database as an orphan. 
	 * 
	 * @param bank
	 */
	/* This might be handled in the Bulk Importer */
	public int createCapbank(String name);
	
	public boolean assignCapbank(int capbankId, int feederId);
	public boolean assignCapbank(int capbankId, String feederName);
	public boolean unassignCapbank(int capbankId);
	
	/**
	 * Adds the Controllers to the Database as an orphan.
	 * 
	 */
	public void createController(CapbankController controller);
	public boolean createControllerFromTemplate(String templateName, CapbankController controller);
	
	public boolean assignController(CapbankController controller, int capbankId);
	public boolean assignController(CapbankController controller, String capBankName);
	public boolean assignController(int controllerId, PaoType controllerType, int capbankId);
	public boolean assignController(int controllerId, PaoType controllerType, String capBankName);
	public boolean unassignController(int controllerId);
	
	public int create(int type, String name, boolean disabled);
	public int createCbc(PaoType paoType, String name, boolean disabled, int portId);

    public int createPAOSchedule(String name, boolean disabled);
    public int createStrategy(String name);
}
