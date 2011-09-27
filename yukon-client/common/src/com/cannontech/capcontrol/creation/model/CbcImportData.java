package com.cannontech.capcontrol.creation.model;

import com.cannontech.common.pao.PaoType;

public class CbcImportData {
	
	private String templateName;
	private String cbcName;
	private PaoType cbcType;
	private String capBankName;
	private String commChannel;
	private String scanEnabled;
	private int cbcSerialNumber;
	private int masterAddress;
	private int slaveAddress;
	private int scanInterval;
	private int altInterval;
	private ImportActionsEnum importAction;
	
	public String getCbcName() {
		return cbcName;
	}
	
	public void setCbcName(String cbcName) {
		this.cbcName = cbcName;
	}
	
	public PaoType getCbcType() {
		return cbcType;
	}
	
	public void setCbcType(PaoType cbcType) {
		this.cbcType = cbcType;
	}
	
	public String getCapBankName() {
		return capBankName;
	}
	
	public void setCapBankName(String capBankName) {
		this.capBankName = capBankName;
	}
	
	public String getCommChannel() {
		return commChannel;
	}
	
	public void setCommChannel(String commChannel) {
		this.commChannel = commChannel;
	}
	
	public Integer getCbcSerialNumber() {
		return cbcSerialNumber;
	}
	
	public void setCbcSerialNumber(int cbcSerialNumber) {
		this.cbcSerialNumber = cbcSerialNumber;
	}
	
	public Integer getMasterAddress() {
		return masterAddress;
	}
	
	public void setMasterAddress(int masterAddress) {
		this.masterAddress = masterAddress;
	}
	
	public Integer getSlaveAddress() {
		return slaveAddress;
	}
	
	public void setSlaveAddress(int slaveAddress) {
		this.slaveAddress = slaveAddress;
	}
	
	public Integer getScanInterval() {
		return scanInterval;
	}
	
	public void setScanInterval(int scanInterval) {
		this.scanInterval = scanInterval;
	}
	
	public Integer getAltInterval() {
		return altInterval;
	}
	
	public void setAltInterval(int altInterval) {
		this.altInterval = altInterval;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	
	public ImportActionsEnum getImportAction() {
		return importAction;
	}
	
	public void setImportAction(ImportActionsEnum importAction) {
		this.importAction = importAction;
	}
	
	public boolean isTemplate() {
		return templateName != null;
	}

	public String getScanEnabled() {
		return scanEnabled;
	}

	public void setScanEnabled(String scanEnabled) {
		this.scanEnabled = scanEnabled;
	}
}