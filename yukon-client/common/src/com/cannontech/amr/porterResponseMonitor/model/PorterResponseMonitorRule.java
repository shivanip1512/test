package com.cannontech.amr.porterResponseMonitor.model;

import java.util.List;

import com.google.common.collect.Lists;

public class PorterResponseMonitorRule {
	private Integer ruleId;
	private int ruleOrder;
	private boolean success;
	private List<PorterResponseMonitorErrorCode> errorCodes = Lists.newArrayList();
	private PorterResponseMonitorMatchStyle matchStyle = PorterResponseMonitorMatchStyle.any;
	private PorterResponseMonitorAction action = PorterResponseMonitorAction.normal;

	public Integer getRuleId() {
		return ruleId;
	}

	public void setRuleId(Integer ruleId) {
		this.ruleId = ruleId;
	}

	public int getRuleOrder() {
		return ruleOrder;
	}

	public void setRuleOrder(int ruleOrder) {
		this.ruleOrder = ruleOrder;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<PorterResponseMonitorErrorCode> getErrorCodes() {
		return errorCodes;
	}

	public void setErrorCodes(List<PorterResponseMonitorErrorCode> errorCodes) {
		this.errorCodes = errorCodes;
	}

	public PorterResponseMonitorMatchStyle getMatchStyle() {
		return matchStyle;
	}

	public void setMatchStyle(PorterResponseMonitorMatchStyle matchStyle) {
		this.matchStyle = matchStyle;
	}

	public PorterResponseMonitorAction getAction() {
		return action;
	}

	public void setAction(PorterResponseMonitorAction action) {
		this.action = action;
	}
}
