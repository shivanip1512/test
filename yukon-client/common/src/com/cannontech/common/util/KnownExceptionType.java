package com.cannontech.common.util;


public class KnownExceptionType {

        private Class<? extends Throwable> exceptionClass = null;
        private String friendlyExceptionPropertyKey = null;
        
        public boolean matchesException(Throwable exception) {
            return this.exceptionClass.isInstance(exception);
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

        public void setFriendlyExceptionPropertyKey(String friendlyExceptionPropertyKey) {
            this.friendlyExceptionPropertyKey = friendlyExceptionPropertyKey;
        }
}