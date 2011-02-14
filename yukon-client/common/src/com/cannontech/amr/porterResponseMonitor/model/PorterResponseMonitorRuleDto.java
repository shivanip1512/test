package com.cannontech.amr.porterResponseMonitor.model;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class PorterResponseMonitorRuleDto implements Comparable<PorterResponseMonitorRuleDto> {
    private Integer ruleId;
    private int ruleOrder;
    private boolean success;
    private PorterResponseMonitorMatchStyle matchStyle = PorterResponseMonitorMatchStyle.any;
    private String state;
    private String errorCodes;
    private static Function<PorterResponseMonitorErrorCode, Integer> transformer = new Function<PorterResponseMonitorErrorCode, Integer>() {
        @Override
        public Integer apply(PorterResponseMonitorErrorCode from) {
            return from.getErrorCode();
        }
    };

    public PorterResponseMonitorRuleDto() {
    }

    public PorterResponseMonitorRuleDto(PorterResponseMonitorRule rule) {
        ruleId = rule.getRuleId();
        ruleOrder = rule.getRuleOrder();
        success = rule.isSuccess();
        matchStyle = rule.getMatchStyle();
        state = rule.getState();
        List<Integer> errorCodeIntegers = Lists.transform(Lists.newArrayList(rule.getErrorCodes()), transformer);
        errorCodes = StringUtils.join(errorCodeIntegers, ", ");
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

    public PorterResponseMonitorMatchStyle getMatchStyle() {
        return matchStyle;
    }

    public String getErrorCodes() {
        return errorCodes;
    }

    public void setErrorCodes(String errorCodes) {
        this.errorCodes = errorCodes;
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

    public static Function<PorterResponseMonitorErrorCode, Integer> getTransformer() {
        return transformer;
    }

    @Override
    public int compareTo(PorterResponseMonitorRuleDto o) {
        return this.getRuleOrder() - o.getRuleOrder();
    }
}
