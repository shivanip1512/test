package com.cannontech.common.device.programming.model;

public class MeterProgramUploadCancelResult {
	private boolean isSuccess;
	private String errorText;
	public String getErrorText() {
		return errorText;
	}
	public void setErrorText(String errorText) {
		this.errorText = errorText;
	}
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
}	
