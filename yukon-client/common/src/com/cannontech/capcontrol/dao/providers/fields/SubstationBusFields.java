package com.cannontech.capcontrol.dao.providers.fields;

import com.cannontech.common.pao.service.PaoTemplatePart;
import com.cannontech.database.YNBoolean;

public class SubstationBusFields implements PaoTemplatePart {
    private int currentVarLoadPointId = 0;
    private int currentWattLoadPointId = 0;
    private String mapLocationId = "0";
    private int currentVoltLoadPointId = 0;
    private int altSubId = 0;
    private int switchPointId = 0;
    private int phaseB = 0;
    private int phaseC = 0;
    private int voltReductionPointId = 0;
    private YNBoolean controlFlag = YNBoolean.NO;
    private YNBoolean dualBusEnabled = YNBoolean.NO;
    private YNBoolean multiMonitorControl = YNBoolean.NO;
    private YNBoolean usePhaseData = YNBoolean.NO;
    
    private int disableBusPointId = 0;

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

	public String getMapLocationId() {
		return mapLocationId;
	}

	public void setMapLocationId(String mapLocationId) {
		this.mapLocationId = mapLocationId;
	}

	public int getCurrentVoltLoadPointId() {
		return currentVoltLoadPointId;
	}

	public void setCurrentVoltLoadPointId(int currentVoltLoadPointId) {
		this.currentVoltLoadPointId = currentVoltLoadPointId;
	}

	public int getAltSubId() {
		return altSubId;
	}

	public void setAltSubId(int altSubId) {
		this.altSubId = altSubId;
	}

	public int getSwitchPointId() {
		return switchPointId;
	}

	public void setSwitchPointId(int switchPointId) {
		this.switchPointId = switchPointId;
	}

	public YNBoolean getDualBusEnabled() {
		return dualBusEnabled;
	}

	public void setDualBusEnabled(YNBoolean dualBusEnabled) {
		this.dualBusEnabled = dualBusEnabled;
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

	public YNBoolean getControlFlag() {
		return controlFlag;
	}

	public void setControlFlag(YNBoolean controlFlag) {
		this.controlFlag = controlFlag;
	}

	public int getVoltReductionPointId() {
		return voltReductionPointId;
	}

	public void setVoltReductionPointId(int voltReductionPointId) {
		this.voltReductionPointId = voltReductionPointId;
	}

	public int getDisableBusPointId() {
		return disableBusPointId;
	}

	public void setDisableBusPointId(int disableBusPointId) {
		this.disableBusPointId = disableBusPointId;
	}
}
