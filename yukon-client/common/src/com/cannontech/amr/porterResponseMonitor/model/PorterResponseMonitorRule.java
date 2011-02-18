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
            String[] errors = ruleCodes.split("\\s*,\\s*");
            for (String error : errors) {
                PorterResponseMonitorErrorCode errorCode = new PorterResponseMonitorErrorCode();
                error = error.trim();
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
		this.errorCodes = errorCodes;
		Builder<Integer> builder = ImmutableSet.builder();
        for (PorterResponseMonitorErrorCode errorCode : errorCodes) {
            if (errorCode.getErrorCode() != null) {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((errorCodes == null) ? 0 : errorCodes.hashCode());
        result = prime * result + ((matchStyle == null) ? 0 : matchStyle.hashCode());
        result = prime * result + ((ruleId == null) ? 0 : ruleId.hashCode());
        result = prime * result + ruleOrder;
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        result = prime * result + (success ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PorterResponseMonitorRule other = (PorterResponseMonitorRule) obj;
        if (errorCodes == null) {
            if (other.errorCodes != null)
                return false;
        } else if (!errorCodes.equals(other.errorCodes))
            return false;
        if (matchStyle != other.matchStyle)
            return false;
        if (ruleId == null) {
            if (other.ruleId != null)
                return false;
        } else if (!ruleId.equals(other.ruleId))
            return false;
        if (ruleOrder != other.ruleOrder)
            return false;
        if (state == null) {
            if (other.state != null)
                return false;
        } else if (!state.equals(other.state))
            return false;
        if (success != other.success)
            return false;
        return true;
    }
}
