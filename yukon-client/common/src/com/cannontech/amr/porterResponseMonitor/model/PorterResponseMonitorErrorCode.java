package com.cannontech.amr.porterResponseMonitor.model;

public class PorterResponseMonitorErrorCode {
	private Integer errorCodeId;
	private Integer ruleId;
	private int errorCode;

	public Integer getErrorCodeId() {
		return errorCodeId;
	}

	public void setErrorCodeId(Integer errorCodeId) {
		this.errorCodeId = errorCodeId;
	}

	public Integer getRuleId() {
		return ruleId;
	}

	public void setRuleId(Integer ruleId) {
		this.ruleId = ruleId;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}
