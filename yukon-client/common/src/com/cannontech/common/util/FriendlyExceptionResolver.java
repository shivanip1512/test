package com.cannontech.common.util;

import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;

public class FriendlyExceptionResolver {

        Set<KnownExceptionType> knownExceptions = new HashSet<KnownExceptionType>();
        Properties exceptionStrings;
        
        // getFriendlyExceptionMessage - default to US locale
        public String getFriendlyExceptionMessage(Throwable exception){
            return getFriendlyExceptionMessage(exception, Locale.US);
        }
        
        // getFriendlyExceptionMessage
        public String getFriendlyExceptionMessage(Throwable exception, Locale locale) {

        String friendlyExceptionMessage = null;

        KnownExceptionType knownExceptionType = getKnownExceptionType(exception);

        if (knownExceptionType != null) {

            try {
                String friendlyExceptionPropertyKey = knownExceptionType.getFriendlyExceptionPropertyKey();
                friendlyExceptionMessage = exceptionStrings.getProperty(friendlyExceptionPropertyKey);
            } catch (NullPointerException e) {
                friendlyExceptionMessage = null;
            }
        }

        return friendlyExceptionMessage;
    }
        
        // find matching KnownExceptionType
        private KnownExceptionType getKnownExceptionType(Throwable exception){
            
            Throwable rootException = getRootException(exception);
            
            KnownExceptionType matchingKnownExceptionType = null;
            
            for(KnownExceptionType k : knownExceptions){
                if(k.matchesException(rootException)){
                    matchingKnownExceptionType = k;
                    break;
                }
            }
            
            return matchingKnownExceptionType;
        }
        
        // helper to get root exception
        private Throwable getRootException(Throwable exception) {
           
            Throwable t = exception;
            while(t != null){
                
                Throwable cause = ExceptionUtils.getCause(t);
                if (cause == null) {
                    break;
                }
                t = cause;
            }
            return t;
        }
      

        // setter/getters
        public Set<KnownExceptionType> getKnownExceptions() {
            return knownExceptions;
        }

        public void setKnownExceptions(Set<KnownExceptionType> knownExceptions) {
            this.knownExceptions = knownExceptions;
        }

        public Properties getExceptionStrings() {
            return exceptionStrings;
        }

        public void setExceptionStrings(Properties exceptionStrings) {
            this.exceptionStrings = exceptionStrings;
        }
    
}
