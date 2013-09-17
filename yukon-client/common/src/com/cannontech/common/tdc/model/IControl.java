package com.cannontech.common.tdc.model;

public interface IControl {

    /* Status Point State definitions */
    // 100900 CGP States changed to 0 and 1
    public static final int CONTROL_INVALID = -1;
    public static final int CONTROL_OPENED = 0;
    public static final int CONTROL_CLOSED = 1;
    public static final int CONTROL_INDETERMINATE = 2;
    
    public static final int CONTROL_STATEZERO = 0;
    public static final int CONTROL_STATEONE = 1;
    public static final int CONTROL_STATETWO = 2;
    public static final int CONTROL_STATETHREE = 3;
    public static final int CONTROL_STATEFOUR = 4;
    public static final int CONTROL_STATEFIVE = 5;
    public static final int CONTROL_STATESIX = 6;
}
