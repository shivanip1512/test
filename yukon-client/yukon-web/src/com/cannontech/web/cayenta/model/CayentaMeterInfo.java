package com.cannontech.web.cayenta.model;

import org.springframework.core.style.ToStringCreator;

public class CayentaMeterInfo {

	private String accountNumber;
	private String name;
	private String locationNumber;
	private String serialNumber;
	private String address;
	
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocationNumber() {
		return locationNumber;
	}
	public void setLocationNumber(String locationNumber) {
		this.locationNumber = locationNumber;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {

		ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("accountNumber", getAccountNumber());
        tsc.append("name", getName());
        tsc.append("locationNumber", getLocationNumber());
        tsc.append("serialNumber", getSerialNumber());
        tsc.append("address", getAddress());
        return tsc.toString();
	}
}
