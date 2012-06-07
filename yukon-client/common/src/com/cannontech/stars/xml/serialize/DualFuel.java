package com.cannontech.stars.xml.serialize;

public class DualFuel {
    private SwitchOverType switchOverType;
    private int secondaryKWCapacity;
    private boolean hasSecondaryKWCapacity;
    private SecondaryEnergySource secondaryEnergySource;

    public DualFuel() {
        this.switchOverType = new SwitchOverType();
        this.secondaryEnergySource = new SecondaryEnergySource();
    }

    public void deleteSecondaryKWCapacity() {
        this.hasSecondaryKWCapacity= false;
    } 

    public SecondaryEnergySource getSecondaryEnergySource() {
        return this.secondaryEnergySource;
    } 

    public int getSecondaryKWCapacity() {
        return this.secondaryKWCapacity;
    } 

    public SwitchOverType getSwitchOverType() {
        return this.switchOverType;
    } 

    public boolean hasSecondaryKWCapacity() {
        return this.hasSecondaryKWCapacity;
    } 

    public void setSecondaryEnergySource(SecondaryEnergySource secondaryEnergySource) {
        this.secondaryEnergySource = secondaryEnergySource;
    } 

    public void setSecondaryKWCapacity(int secondaryKWCapacity) {
        this.secondaryKWCapacity = secondaryKWCapacity;
        this.hasSecondaryKWCapacity = true;
    } 

    public void setSwitchOverType(SwitchOverType switchOverType) {
        this.switchOverType = switchOverType;
    } 

}
