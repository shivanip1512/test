package com.cannontech.capcontrol.creation.model;

import com.cannontech.common.csvImport.ImportAction;
import com.cannontech.common.pao.PaoType;

public class CbcImportData {
	
	private String templateName;
	private final String cbcName;
	private final PaoType cbcType;
	private String capBankName;
	private String commChannel;
	private Integer cbcSerialNumber;
	private Integer masterAddress;
	private Integer slaveAddress;
	private Integer scanInterval;
	private Integer altInterval;
	private boolean scanEnabled;
	private final ImportAction importAction;
	
	public CbcImportData(String cbcName, ImportAction importAction, PaoType cbcType) {
	    this.cbcName = cbcName;
	    this.importAction = importAction;
	    this.cbcType = cbcType;
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
	
	public Integer getCbcSerialNumber() {
		return cbcSerialNumber;
	}
	
	public Integer getMasterAddress() {
		return masterAddress;
	}
	
	public Integer getSlaveAddress() {
		return slaveAddress;
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
	
	public ImportAction getImportAction() {
		return importAction;
	}
	
	public boolean isTemplate() {
		return templateName != null;
	}

    public void setScanInterval(Integer scanInterval) {
        this.scanInterval = scanInterval;
    }

    public void setAltInterval(Integer altInterval) {
        this.altInterval = altInterval;
    }

    public void setCommChannel(String commChannel) {
        this.commChannel = commChannel;
    }

    public void setCbcSerialNumber(Integer cbcSerialNumber) {
        this.cbcSerialNumber = cbcSerialNumber;
    }

    public void setMasterAddress(Integer masterAddress) {
        this.masterAddress = masterAddress;
    }

    public void setSlaveAddress(Integer slaveAddress) {
        this.slaveAddress = slaveAddress;
    }

    public boolean isScanEnabled() {
        return scanEnabled;
    }

    public void setScanEnabled(boolean scanEnabled) {
        this.scanEnabled = scanEnabled;
    }
}