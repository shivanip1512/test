package com.cannontech.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.style.ToStringCreator;


public class KnownExceptionType {

    private Class<? extends Throwable> exceptionClass = null;
    private String friendlyExceptionPropertyKey = null;
    private boolean includeCauseMessage = false;

    public boolean matchesException(Throwable exception) {

    	Throwable matchingCause = getMatchingCause(exception);
    	return matchingCause != null;
    }
    
    public String getCauseMessage(Throwable exception) {
    	
    	Throwable matchingCause = getMatchingCause(exception);
    	if (matchingCause != null) {
    		return matchingCause.getMessage();
    	}
    	
    	return "";
    }
    
    private Throwable getMatchingCause(Throwable exception) {
    	
    	Throwable matchingCause = null;
    	
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
            	matchingCause = c;
            	break;
            }
        }
        
        return matchingCause;
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
    
    public boolean isIncludeCauseMessage() {
		return includeCauseMessage;
	}
    
    public void setIncludeCauseMessage(boolean includeCauseMessage) {
		this.includeCauseMessage = includeCauseMessage;
	}
    
    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("exceptionClass", exceptionClass);
        tsc.append("key", friendlyExceptionPropertyKey);
        return tsc.toString();
    }
}