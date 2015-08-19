package com.cannontech.web.dr.cc.model;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.Displayable;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public class Exclusion implements Displayable {
    public enum Status {EXCLUDE_OVERRIDABLE, EXCLUDE}
    
    private String key;
    private Status status;
    private Object[] arguments;
    
    public Exclusion(ExclusionType type, Status status, Object... arguments) {
        key = type.getFormatKey();
        this.status = status;
        this.arguments = arguments;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public boolean isForceExcluded() {
        return status == Status.EXCLUDE;
    }

    @Override
    public MessageSourceResolvable getMessage() {
        return new YukonMessageSourceResolvable(key, arguments);
    }
}
