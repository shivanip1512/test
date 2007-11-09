package com.cannontech.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.core.style.ToStringCreator;


public class KnownExceptionType {

    private Class<? extends Throwable> exceptionClass = null;
    private String friendlyExceptionPropertyKey = null;

    public boolean matchesException(Throwable exception) {

        List<Throwable> causes = new ArrayList<Throwable>();
        causes.add(exception);
        Throwable cause = ExceptionUtils.getCause(exception);
        while(cause != null) {
            causes.add(cause);
            cause = ExceptionUtils.getCause(cause);
        }
        Collections.reverse(causes);
        
        for(Throwable c : causes) {
            if(this.exceptionClass.isInstance(c)) {
                return true;
            }
        }
        return false;
    }

    public Class<? extends Throwable> getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(Class<? extends Throwable> exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public String getFriendlyExceptionPropertyKey() {
        return friendlyExceptionPropertyKey;
    }

    public void setFriendlyExceptionPropertyKey(
            String friendlyExceptionPropertyKey) {
        this.friendlyExceptionPropertyKey = friendlyExceptionPropertyKey;
    }
    
    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("exceptionClass", exceptionClass);
        tsc.append("key", friendlyExceptionPropertyKey);
        return tsc.toString();
    }
}