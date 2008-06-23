package com.cannontech.stars.xml.serialize;

public class StarsDeleteCallReport {
    private int _callID;
    private boolean _has_callID;

    public StarsDeleteCallReport() {
        
    }

    public void deleteCallID() {
        this._has_callID= false;
    } 

    public int getCallID() {
        return this._callID;
    } 

    public boolean hasCallID() {
        return this._has_callID;
    }

    public void setCallID(int callID) {
        this._callID = callID;
        this._has_callID = true;
    }

}
