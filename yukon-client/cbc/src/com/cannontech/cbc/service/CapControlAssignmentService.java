package com.cannontech.cbc.service;

import com.cannontech.common.pao.PaoType;

public interface CapControlAssignmentService {

	public boolean assignSubstation(int substationId, int areaId);
	public boolean assignSubstation(int substationId, String areaName);
	public boolean unassignSubstation(int substationId);

	public boolean assignSubstationBus(int substationBusId, int substationId);
	public boolean assignSubstationBus(int substationBusId, String substationName);
	public boolean unassignSubstationBus(int substationBudId);
	
	public boolean assignFeeder(int feederId, int subBusId);
	public boolean assignFeeder(int feederId, String subBusName);
	public boolean unassignFeeder(int feederId);
	
	public boolean assignCapbank(int capbankId, int feederId);
	public boolean assignCapbank(int capbankId, String feederName);
	public boolean unassignCapbank(int capbankId);
	
	public boolean assignController(int controllerId, PaoType controllerType, int capbankId);
	public boolean assignController(int controllerId, PaoType controllerType, String capBankName);
	
	public boolean unassignController(int controllerId);
}
