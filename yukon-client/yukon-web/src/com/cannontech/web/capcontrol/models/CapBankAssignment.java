
package com.cannontech.web.capcontrol.models;

public final class CapBankAssignment {
    
    private int id;
    private String name;
    private float controlOrder;
    private float tripOrder;
    private float closeOrder;
    
    private CapBankAssignment(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    private CapBankAssignment(int id, String name, float controlOrder, float tripOrder, float closeOrder) {
        this.id = id;
        this.name = name;
        this.controlOrder = controlOrder;
        this.tripOrder = tripOrder;
        this.closeOrder = closeOrder;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public static CapBankAssignment of(int id, String name) {
        return new CapBankAssignment(id, name);
    }
    
    public static CapBankAssignment of(int id, String name, float controlOrder, float tripOrder, float closeOrder) {
        return new CapBankAssignment(id, name, controlOrder, tripOrder, closeOrder);
    }
    
    @Override
    public String toString() {
        return String.format("Assignment [id=%s, name=%s]", id, name);
    }

    public float getControlOrder() {
        return controlOrder;
    }

    public void setControlOrder(float controlOrder) {
        this.controlOrder = controlOrder;
    }

    public float getTripOrder() {
        return tripOrder;
    }

    public void setTripOrder(float tripOrder) {
        this.tripOrder = tripOrder;
    }

    public float getCloseOrder() {
        return closeOrder;
    }

    public void setCloseOrder(float closeOrder) {
        this.closeOrder = closeOrder;
    }
    
}