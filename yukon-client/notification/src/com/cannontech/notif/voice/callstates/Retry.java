package com.cannontech.notif.voice.callstates;


/**
 * 
 */
public class Retry extends CallState {

    public boolean isDone() {
        return false;
    }
    
    public boolean isReady() {
        return true;
    }
  
    public String toString() {
        return "Retry";
    }
    

}
