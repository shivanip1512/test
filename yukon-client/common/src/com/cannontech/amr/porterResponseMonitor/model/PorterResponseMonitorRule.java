package com.cannontech.amr.porterResponseMonitor.model;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class PorterResponseMonitorRule implements Comparable<PorterResponseMonitorRule>{
	private Integer ruleId;
	private int ruleOrder;
	private boolean success;
	private List<PorterResponseMonitorErrorCode> errorCodes = Lists.newArrayList();
	private PorterResponseMonitorMatchStyle matchStyle = PorterResponseMonitorMatchStyle.any;
	private String state;
    private Set<Integer> errorCodesAsIntegers = Sets.newHashSet();

	public PorterResponseMonitorRule() {
	}

	public PorterResponseMonitorRule(PorterResponseMonitorRuleDto ruleDto) throws NumberFormatException {
	    ruleId = ruleDto.getRuleId();
	    ruleOrder = ruleDto.getRuleOrder();
	    success = ruleDto.isSuccess();
	    matchStyle = ruleDto.getMatchStyle();
	    state = ruleDto.getState();

	    List<PorterResponseMonitorErrorCode> tempErrorCodes = Lists.newArrayList();
	    String ruleCodes = ruleDto.getErrorCodes();
	    if (ruleCodes.isEmpty()) {
	        PorterResponseMonitorErrorCode errorCode = new PorterResponseMonitorErrorCode();
            if (ruleDto.getRuleId() != null) {
                errorCode.setRuleId(ruleDto.getRuleId());
            }
            tempErrorCodes.add(errorCode);
	    } else {
            String[] errors = ruleCodes.trim().split("\\s*,\\s*");
            for (String error : errors) {
                PorterResponseMonitorErrorCode errorCode = new PorterResponseMonitorErrorCode();
                if (!error.isEmpty()) {
                    errorCode.setErrorCode(Integer.valueOf(error));
                }
                if (ruleDto.getRuleId() != null) {
                    errorCode.setRuleId(ruleDto.getRuleId());
                }
                tempErrorCodes.add(errorCode);
            }
	    }
	    setErrorCodes(tempErrorCodes);
	}

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
		return Collections.unmodifiableList(errorCodes); // not perfect, PRMEC could implement an interface
	}

    public Set<Integer> getErrorCodesAsIntegers() {
        return errorCodesAsIntegers;
    }

	public void setErrorCodes(List<PorterResponseMonitorErrorCode> errorCodes) {
	    Builder<Integer> builder = ImmutableSet.builder();
        for (PorterResponseMonitorErrorCode errorCode : errorCodes) {
            if (errorCode.getErrorCode() != null) {
                this.errorCodes.add(errorCode);
                builder.add(errorCode.getErrorCode());
            }
        }
        errorCodesAsIntegers = builder.build();
	}

	public PorterResponseMonitorMatchStyle getMatchStyle() {
		return matchStyle;
	}

	public void setMatchStyle(PorterResponseMonitorMatchStyle matchStyle) {
		this.matchStyle = matchStyle;
	}

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getStateInt() throws NumberFormatException {
        return convertStateToInt(state);
    }

    public int convertStateToInt(String state) throws NumberFormatException {
        return Integer.parseInt(state);
    }

    @Override
    public String toString() {
        return String.format("PorterResponseMonitorRule [ruleId=%s, ruleOrder=%s, success=%s, errorCodes=%s, matchStyle=%s, state=%s]",
                    ruleId,
                    errorCodes,
                    matchStyle,
                    state);
    }

    @Override
    public int compareTo(PorterResponseMonitorRule o) {
        return this.getRuleOrder() - o.getRuleOrder();
    }
}
