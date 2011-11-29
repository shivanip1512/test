package com.cannontech.common.pao.service.providers.fields;

import com.cannontech.common.pao.service.PaoTemplatePart;

public class DeviceAddressFields implements PaoTemplatePart {
	private int masterAddress = 1;
	private int slaveAddress = 0;
	private int postCommWait = 0;
	
	public int getMasterAddress() {
		return masterAddress;
	}
	
	public void setMasterAddress(int masterAddress) {
		this.masterAddress = masterAddress;
	}
	
	public int getSlaveAddress() {
		return slaveAddress;
	}
	
	public void setSlaveAddress(int slaveAddress) {
		this.slaveAddress = slaveAddress;
	}
	
	public int getPostCommWait() {
		return postCommWait;
	}
	
	public void setPostCommWait(int postCommWait) {
		this.postCommWait = postCommWait;
	}
}
