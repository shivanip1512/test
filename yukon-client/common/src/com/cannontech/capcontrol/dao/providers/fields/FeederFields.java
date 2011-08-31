package com.cannontech.capcontrol.dao.providers.fields;

import com.cannontech.common.pao.service.PaoTemplatePart;

public class FeederFields implements PaoTemplatePart {
	private int currentVarLoadPointId = 0;
	private int currentWattLoadPointId = 0;
	private int currentVoltPointLoadId = 0;
	private int phaseB = 0;
	private int phaseC = 0;
	private String mapLocationId = "0";
	private String multiMonitorControl = "N";
	private String usePhaseData = "N";
	private String controlFlag = "N";
	
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
	
	public int getCurrentVoltPointLoadId() {
		return currentVoltPointLoadId;
	}
	
	public void setCurrentVoltPointLoadId(int currentVoltPointLoadId) {
		this.currentVoltPointLoadId = currentVoltPointLoadId;
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
	
	public String getMultiMonitorControl() {
		return multiMonitorControl;
	}
	
	public void setMultiMonitorControl(String multiMonitorControl) {
		this.multiMonitorControl = multiMonitorControl;
	}
	
	public String getUsePhaseData() {
		return usePhaseData;
	}
	
	public void setUsePhaseData(String usePhaseData) {
		this.usePhaseData = usePhaseData;
	}
	
	public String getControlFlag() {
		return controlFlag;
	}
	
	public void setControlFlag(String controlFlag) {
		this.controlFlag = controlFlag;
	}
}