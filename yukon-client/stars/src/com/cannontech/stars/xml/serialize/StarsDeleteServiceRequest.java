package com.cannontech.stars.xml.serialize;

public class StarsDeleteServiceRequest {
    private int _orderID;
    private boolean _has_orderID;

    public StarsDeleteServiceRequest() {
    
    }

    public void deleteOrderID()
    {
        this._has_orderID= false;
    } 

    public int getOrderID()
    {
        return this._orderID;
    } 

    public boolean hasOrderID()
    {
        return this._has_orderID;
    } 

    public void setOrderID(int orderID)
    {
        this._orderID = orderID;
        this._has_orderID = true;
    } 

}
