package com.cannontech.amr.porterResponseMonitor.model;

public class PorterResponseMonitorErrorCode {
	private Integer errorCodeId;
	private Integer ruleId;
	private Integer errorCode;

    public PorterResponseMonitorErrorCode() {}

	public PorterResponseMonitorErrorCode(Integer errorCode) {
	    this.errorCode = errorCode;
	}

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

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}
}
