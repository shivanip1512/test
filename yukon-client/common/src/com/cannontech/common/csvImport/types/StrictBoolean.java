package com.cannontech.common.csvImport.types;

import com.cannontech.common.util.CtiUtilities;

public class StrictBoolean {
    
    /**
     * Provides a stricter parsing of string into boolean than Boolean.valueOf. Specifically,
     * arbitrary string values cause an exception to be thrown, rather than simply returning false.
     */
    public final static boolean valueOf(String s) {
        if(CtiUtilities.isTrue(s)) return true;
        else if(CtiUtilities.isFalse(s)) return false;
        else throw new IllegalArgumentException("Provided string is not a true or false value.");
    }     
   
}
