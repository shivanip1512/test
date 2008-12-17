package com.cannontech.stars.dr.optout.exception;

/**
 * Exception thrown when the device is not opted out and it was expected to be
 */
public class AlreadyOptedOutException extends RuntimeException {

	private Integer deviceId;
	private Integer customerAccountId;

	public AlreadyOptedOutException(int deviceId, int customerAccountId) {
		this.deviceId = deviceId;
		this.customerAccountId = customerAccountId;
	}

	@Override
	public String getMessage() {
		return "Device id: " + deviceId
				+ " is already currently opted out for customer account id: "
				+ customerAccountId;
	}

	public Integer getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	public Integer getCustomerAccountId() {
		return customerAccountId;
	}

	public void setCustomerAccountId(Integer customerAccountId) {
		this.customerAccountId = customerAccountId;
	}

}
