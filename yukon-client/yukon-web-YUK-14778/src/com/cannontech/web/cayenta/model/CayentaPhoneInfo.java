package com.cannontech.web.cayenta.model;

import org.springframework.core.style.ToStringCreator;

public class CayentaPhoneInfo {

	private String phoneNumber;

	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String toString() {

		ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("phoneNumber", getPhoneNumber());
        return tsc.toString();
	}
}
