package com.cannontech.stars.util;


public class OptOutConstraintViolatedException extends WebClientException {
    public enum ConstraintType {
        TIMES_ALLOWED,
        HOURS_ALLOWED,
        DUE_TIME
    };
    
    private final ConstraintType type;
    private int allowed;
    private int actual;
    private String period;
    private String dueDate;
    
    public OptOutConstraintViolatedException(String message, ConstraintType type, String dueDate) {
        super(message);
        this.type = type;
        this.dueDate = dueDate;
    }
    
    public OptOutConstraintViolatedException(String message, ConstraintType type, 
            int allowed, int actual, String period) {
        super(message);
        this.type = type;
        this.allowed = allowed;
        this.actual = actual;
        this.period = period;
    }
    
    public ConstraintType getType() {
        return type;
    }
    
    public int getAllowed() {
        return allowed;
    }
    
    public int getActual() {
        return actual;
    }
    
    public String getPeriod() {
        return period;
    }
    
    public String getDueDate() {
        return dueDate;
    }
    
}
