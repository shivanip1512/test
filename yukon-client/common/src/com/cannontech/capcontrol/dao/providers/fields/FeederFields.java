package com.cannontech.capcontrol.dao.providers.fields;

import com.cannontech.common.pao.service.PaoTemplatePart;
import com.cannontech.database.YNBoolean;

public class FeederFields implements PaoTemplatePart {
	private int currentVarLoadPointId = 0;
	private int currentWattLoadPointId = 0;
	private int currentVoltLoadPointId = 0;
	private int phaseB = 0;
	private int phaseC = 0;
	private String mapLocationId = "0";
	private YNBoolean multiMonitorControl = YNBoolean.NO;
	private YNBoolean usePhaseData = YNBoolean.NO;
	private YNBoolean controlFlag = YNBoolean.NO;
	
	public int getCurrentVarLoadPointId() {
		return currentVarLoadPointId;
	}
	
	public void setCurrentVarLoadPointId(int currentVarLoadPointId) {
		this.currentVarLoadPointId = currentVarLoadPointId;
	}
	
	public int getCurrentWattLoadPointId() {
		return currentWattLoadPointId;
	}
	
	public void setCurrentWattLoadPointId(int currentWattLoadPointId) {
		this.currentWattLoadPointId = currentWattLoadPointId;
	}
	
	public int getcurrentVoltLoadPointId() {
		return currentVoltLoadPointId;
	}
	
	public void setcurrentVoltLoadPointId(int currentVoltLoadPointId) {
		this.currentVoltLoadPointId = currentVoltLoadPointId;
	}
	
	public int getPhaseB() {
		return phaseB;
	}
	
	public void setPhaseB(int phaseB) {
		this.phaseB = phaseB;
	}
	
	public int getPhaseC() {
		return phaseC;
	}
	
	public void setPhaseC(int phaseC) {
		this.phaseC = phaseC;
	}
	
	public String getMapLocationId() {
		return mapLocationId;
	}
	
	public void setMapLocationId(String mapLocationId) {
		this.mapLocationId = mapLocationId;
	}
	
	public YNBoolean getMultiMonitorControl() {
		return multiMonitorControl;
	}
	
	public void setMultiMonitorControl(YNBoolean multiMonitorControl) {
		this.multiMonitorControl = multiMonitorControl;
	}
	
	public YNBoolean getUsePhaseData() {
		return usePhaseData;
	}
	
	public void setUsePhaseData(YNBoolean usePhaseData) {
		this.usePhaseData = usePhaseData;
	}
	
	public YNBoolean getControlFlag() {
		return controlFlag;
	}
	
	public void setControlFlag(YNBoolean controlFlag) {
		this.controlFlag = controlFlag;
	}
}