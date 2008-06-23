package com.cannontech.stars.xml.serialize;

public class StarsFailure {
    private int _statusCode;
    private boolean _has_statusCode;
    private String _description;
    private Exception exception;

    public StarsFailure() {
    
    } 

    public void deleteStatusCode() {
        this._has_statusCode = false;
    }

    public java.lang.String getDescription() {
        return _description;
    } 

    public int getStatusCode() {
        return _statusCode;
    } 

    public boolean hasStatusCode() {
        return _has_statusCode;
    } 

    public void setDescription(java.lang.String description) {
        this._description = description;
    }
    
    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }
    
    public void setStatusCode(int statusCode) {
        this._statusCode = statusCode;
        this._has_statusCode = true;
    } 

}
