package com.cannontech.database.data.device.lm;

import java.sql.SQLException;

import com.cannontech.database.db.device.lm.GearControlMethod;

public class HoneywellSetpointGear extends WifiThermostatSetpointGear {
    private static final long serialVersionUID = 1L;
    private int precoolOffset = 0;
    
    public HoneywellSetpointGear() {
        setControlMethod(GearControlMethod.HoneywellSetpoint);
    }
    
    public int getPrecoolOffset() {
        return precoolOffset;
    }
    
    public void setPrecoolOffset(int precoolOffset) {
        this.precoolOffset = precoolOffset;
    }
    
    @Override
    public void add() throws SQLException {
        super.add();
        Object[] addValues = {getGearID(), getHeatCool().getDbValue(), 0, getSetpointOffset(), precoolOffset, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        add(TABLE_NAME, addValues);
    }
    
    @Override
    public void addPartial() throws SQLException {
        Object[] addValues = {getGearID(), getHeatCool().getDbValue(), 0, getSetpointOffset(), precoolOffset, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        add(TABLE_NAME, addValues);
    }

    @Override
    public void delete() throws SQLException {
        delete(TABLE_NAME, "GearID", getGearID());
        super.delete();
    }

    @Override
    public void deletePartial() throws SQLException {
        delete(TABLE_NAME, "GearID", getGearID());
    }
    
    @Override
    public boolean useCustomDbRetrieve() {
        return true;
    }
    
    @Override
    public void retrieve() throws SQLException {
        super.retrieve();
        Object[] constraintValues = {getGearID()};    
        Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
        setSetpointOffset((int) results[2]);
        setPrecoolOffset((int) results[3]);
        setHeatCool(HeatCool.of(results[0]));
    }
    
    /**
     * Updates the base classes, then always tries to add the thermostat gear table
     * and calls update if the insert fails.
     */
    @Override
    public void update() throws SQLException {
        super.update();
        
        try {
            addPartial();
        } catch (SQLException e) {
            // Add failed, do the update instead
            Object[] setValues = {getGearID(), getHeatCool().getDbValue(), 0, getSetpointOffset(), precoolOffset, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            Object[] constraintValues = {getGearID()};
            update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
        }
    }
}
