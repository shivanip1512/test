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
	 * @throws TransactionException 
	 */
    public void createArea(Area area) throws TransactionException;
	
	/**
	 * Adds a Substation to the database as an orphan.
	 * 
	 * @param substation
	 * @throws TransactionException 
	 */
    public void createSubstation(Substation substation) throws TransactionException;
	
	public boolean assignSubstation(int substationId, int areaId);
	public boolean assignSubstation(int substationId, String areaName);
	public boolean unassignSubstation(int substationId);
	
	/**
	 * Adds the Substation bus to the database as an orphan.
	 * 
	 * @param subBus
	 * @throws TransactionException 
	 */
	public void createSubstationBus(SubstationBus subBus) throws TransactionException;
	
	public boolean assignSubstationBus(int substationBusId, int substationId);
	public boolean assignSubstationBus(int substationBusId, String substationName);
	public boolean unassignSubstationBus(int substationBudId);
	
	/**
	 * Adds the Feeder to the database as an orphan. 
	 * 
	 * @param feeder
	 * @throws TransactionException 
	 */
	public void createFeeder(Feeder feeder) throws TransactionException;
	
	public boolean assignFeeder(int feederId, int subBusId);
	public boolean assignFeeder(int feederId, String subBusName);
	public boolean unassignFeeder(int feederId);
	
	/**
	 * Adds the CapBank to the database as an orphan. 
	 * 
	 * @param bank
	 * @throws TransactionException 
	 */
	/* This might be handled in the Bulk Importer */
	public void createCapbank(Capbank capbank) throws TransactionException;
	
	public boolean assignCapbank(int capbankId, int feederId);
	public boolean assignCapbank(int capbankId, String feederName);
	public boolean unassignCapbank(int capbankId);
	
	/**
	 * Adds the Controllers to the Database as an orphan.
	 * @throws TransactionException 
	 * 
	 */
	public void createController(CapbankController controller) throws TransactionException;
	public boolean createControllerFromTemplate(String templateName, CapbankController controller);
	
	public boolean assignController(CapbankController controller, int capbankId);
	public boolean assignController(CapbankController controller, String capBankName);
	public boolean unassignController(int controllerId);
	
	public int create(int type, String name, boolean disabled, int portId) throws TransactionException;
    public void createSpecialArea(SpecialArea specialArea) throws TransactionException;
    public int createPAOSchedule(String name, boolean disabled);
    public int createStrategy(String name);
}
