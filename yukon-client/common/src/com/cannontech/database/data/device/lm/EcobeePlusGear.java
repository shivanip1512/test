package com.cannontech.database.data.device.lm;

import java.sql.SQLException;

import com.cannontech.database.db.device.lm.GearControlMethod;

public class EcobeePlusGear extends com.cannontech.database.db.device.lm.LMProgramDirectGear {
    private static final long serialVersionUID = 1L;
    private HeatCool heatCool = HeatCool.HEAT;
    
    protected static final String TABLE_NAME = "LMThermostatGear";
    protected static final String[] CONSTRAINT_COLUMNS = {"GearID"};
    protected static final String[] SETTER_COLUMNS = {"Settings", "MinValue", "MaxValue", "ValueB", "ValueD", "ValueF", 
            "Random", "ValueTa", "ValueTb", "ValueTc", "ValueTd", "ValueTe", "ValueTf", "RampRate"};

    public EcobeePlusGear() {
        setControlMethod(GearControlMethod.EcobeePlus);
    }

    public void setHeatCool(HeatCool heatCool) {
        this.heatCool = heatCool;
    }

    public HeatCool getHeatCool() {
        return heatCool;
    }

    public int getRandomTimeSeconds() {
        return getMethodRate();
    }

    public void setRandomTimeSeconds(int randomTimeSeconds) {
        setMethodRate(randomTimeSeconds);
    }

    @Override
    public boolean useCustomDbRetrieve() {
        return true;
    }

    @Override
    public void add() throws SQLException {
        super.add();
        Object[] addValues = { getGearID(), getHeatCool().getDbValue(), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        add(TABLE_NAME, addValues);
    }

    @Override
    public void addPartial() throws SQLException {
        Object[] addValues = { getGearID(), getHeatCool().getDbValue(), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
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
    public void retrieve() throws SQLException {
        super.retrieve();
        Object[] constraintValues = { getGearID() };
        Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

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
            Object[] setValues = { getGearID(), getHeatCool().getDbValue(), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            Object[] constraintValues = { getGearID() };
            update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
        }
    }

}