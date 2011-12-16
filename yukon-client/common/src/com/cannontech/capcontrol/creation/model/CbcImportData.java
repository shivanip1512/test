package com.cannontech.capcontrol.creation.model;

import com.cannontech.common.pao.PaoType;

public class CbcImportData {
	
	private String templateName;
	private final String cbcName;
	private final PaoType cbcType;
	private String capBankName;
	private final String commChannel;
	private String scanEnabled;
	private final int cbcSerialNumber;
	private final int masterAddress;
	private final int slaveAddress;
	private int scanInterval;
	private int altInterval;
	private final ImportAction importAction;
	
	public CbcImportData(String cbcName, ImportAction importAction, PaoType cbcType, String commChannel, 
	                     int cbcSerialNumber, int masterAddress, int slaveAddress) {
	    this.cbcName = cbcName;
	    this.cbcType = cbcType;
	    this.commChannel = commChannel;
	    this.cbcSerialNumber = cbcSerialNumber;
	    this.masterAddress = masterAddress;
	    this.slaveAddress = slaveAddress;
	    this.importAction = importAction;
	}
	
	/**
	 * This method is a workaround for the strict constructor of the CbcImportData object.
	 * Since removes require only a name, this calls the constructor with dummy data.
	 * @param name
	 * @return a removal-ready CbcImportData object.
	 */
	public static CbcImportData createRemovalImportData(String name) {
	    return new CbcImportData(name, ImportAction.REMOVE, null, null, 0, 0, 0);
	}
	
	public String getCbcName() {
		return cbcName;
	}
	
	public PaoType getCbcType() {
		return cbcType;
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
	
	public int getCbcSerialNumber() {
		return cbcSerialNumber;
	}
	
	public int getMasterAddress() {
		return masterAddress;
	}
	
	public int getSlaveAddress() {
		return slaveAddress;
	}
	
	public int getScanInterval() {
		return scanInterval;
	}
	
	public void setScanInterval(int scanInterval) {
		this.scanInterval = scanInterval;
	}
	
	public int getAltInterval() {
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
	
	public ImportAction getImportAction() {
		return importAction;
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