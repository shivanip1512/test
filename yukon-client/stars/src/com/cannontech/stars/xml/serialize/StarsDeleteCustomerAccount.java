package com.cannontech.stars.xml.serialize;

public class StarsDeleteCustomerAccount {
    private boolean _disableReceivers;
    private boolean _has_disableReceivers;

    public StarsDeleteCustomerAccount() {
        
    }

    public void deleteDisableReceivers()
    {
        this._has_disableReceivers= false;
    } 

    public boolean getDisableReceivers()
    {
        return this._disableReceivers;
    }

    public boolean hasDisableReceivers()
    {
        return this._has_disableReceivers;
    }

    public void setDisableReceivers(boolean disableReceivers)
    {
        this._disableReceivers = disableReceivers;
        this._has_disableReceivers = true;
    }

}
