package com.cannontech.yukon.cbc;

import java.util.Vector;

public class LtcMessage extends CapControlMessage {
    
    private Vector<Ltc> ltcs;
    
    public LtcMessage() {
        
    }
    
    public void setLtcs(Vector<Ltc> ltcs) {
        this.ltcs = ltcs;
    }
    
    public Vector<Ltc> getLtcs() {
        return ltcs;
    }
}
