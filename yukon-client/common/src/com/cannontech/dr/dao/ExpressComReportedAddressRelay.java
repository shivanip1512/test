package com.cannontech.dr.dao;

import java.io.Serializable;
import java.util.Comparator;

public class ExpressComReportedAddressRelay implements Serializable {

    public static final Comparator<ExpressComReportedAddressRelay> BY_RELAY_NUMBER = 
            new Comparator<ExpressComReportedAddressRelay>() {
        @Override
        public int compare(ExpressComReportedAddressRelay a, ExpressComReportedAddressRelay b){
            return Integer.compare(a.relayNumber, b.relayNumber);
        }
    };
    
    private int relayNumber;
    private int program;
    private int splinter;
    
    public ExpressComReportedAddressRelay(int relayNumber) {
        this.relayNumber = relayNumber;
    }
    
    public int getRelayNumber() {
        return relayNumber;
    }
    
    public void setRelayNumber(int relayNumber) {
        this.relayNumber = relayNumber;
    }
    
    public int getProgram() {
        return program;
    }
    
    public void setProgram(int program) {
        this.program = program;
    }
    
    public int getSplinter() {
        return splinter;
    }
    
    public void setSplinter(int splinter) {
        this.splinter = splinter;
    }
    
    @Override
    public String toString() {
        return String.format("ExpressComReportedAddressRelay [relayNumber=%s, program=%s, splinter=%s]",
                    relayNumber,
                    program,
                    splinter);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + program;
        result = prime * result + relayNumber;
        result = prime * result + splinter;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ExpressComReportedAddressRelay other = (ExpressComReportedAddressRelay) obj;
        if (program != other.program) {
            return false;
        }
        if (relayNumber != other.relayNumber) {
            return false;
        }
        if (splinter != other.splinter) {
            return false;
        }
        return true;
    }
    
}