package com.cannontech.cbc.service;

import com.cannontech.cbc.model.Area;
import com.cannontech.cbc.model.Capbank;
import com.cannontech.cbc.model.CapbankController;
import com.cannontech.cbc.model.Feeder;
import com.cannontech.cbc.model.SpecialArea;
import com.cannontech.cbc.model.Substation;
import com.cannontech.cbc.model.SubstationBus;
import com.cannontech.database.TransactionException;

public interface CapControlCreationService {

	/**
	 * Adds the area to the database.
	 * @param area
	 */
	public boolean createArea(Area area);
	
	/**
	 * Adds a Substation to the database as an orphan.
	 * 
	 * @param substation
	 */
	public boolean createSubstation(Substation substation);
	
	public boolean assignSubstation(int substationId, int areaId);
	public boolean assignSubstation(int substationId, String areaName);
	public boolean unassignSubstation(int substationId);
	
	/**
	 * Adds the Substation bus to the database as an orphan.
	 * 
	 * @param subBus
	 */
	public boolean createSubstationBus(SubstationBus subBus);
	
	public boolean assignSubstationBus(int substationBusId, int substationId);
	public boolean assignSubstationBus(int substationBusId, String substationName);
	public boolean unassignSubstationBus(int substationBudId);
	
	/**
	 * Adds the Feeder to the database as an orphan. 
	 * 
	 * @param feeder
	 */
	public boolean createFeeder(Feeder feeder);
	
	public boolean assignFeeder(int feederId, int subBusId);
	public boolean assignFeeder(int feederId, String subBusName);
	public boolean unassignFeeder(int feederId);
	
	/**
	 * Adds the CapBank to the database as an orphan. 
	 * 
	 * @param bank
	 */
	/* This might be handled in the Bulk Importer */
	public boolean createCapbank(Capbank capbank);
	
	public boolean assignCapbank(int capbankId, int feederId);
	public boolean assignCapbank(int capbankId, String feederName);
	public boolean unassignCapbank(int capbankId);
	
	/**
	 * Adds the Controllers to the Database as an orphan.
	 * 
	 */
	public boolean createController(CapbankController controller);
	public boolean createControllerFromTemplate(String templateName, CapbankController controller);
	
	public boolean assignController(CapbankController controller, int capbankId);
	public boolean assignController(CapbankController controller, String capBankName);
	public boolean unassignController(int controllerId);
	
	public int create(int type, String name, boolean disabled, int portId) throws TransactionException;
    public boolean createSpecialArea(SpecialArea specialArea);
    public int createPAOSchedule(String name, boolean disabled);
    public int createStrategy(String name);
}
