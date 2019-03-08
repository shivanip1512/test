package com.cannontech.database.data.device.lm;

import java.sql.SQLException;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.dr.itron.model.ItronCycleType;

public class ItronCycleGear extends com.cannontech.database.db.device.lm.LMProgramDirectGear {

    private static final long serialVersionUID = 1L;
    
    private ItronCycleType itronCycleType = null;
    
    public static final String SETTER_COLUMNS[] = { "CycleOption" };
    public static final String CONSTRAINT_COLUMNS[] = { "GearId" };
    public static final String TABLE_NAME = "LMItronCycleGear";

    public ItronCycleGear() {
        setControlMethod(GearControlMethod.ItronCycle);
    }
    
    
    @Override
    public void add() throws SQLException {
        super.add();
        
        Object addValues[] = { getGearID(), itronCycleType.name() };
        add(TABLE_NAME, addValues);
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        Object addValues[] = { getGearID(), itronCycleType.name() };
        add(TABLE_NAME, addValues);
    }

    @Override
    public void delete() throws SQLException {
        delete(TABLE_NAME, "GearId", getGearID());
        super.delete();
    }

    @Override
    public void retrieve() throws SQLException {
        super.retrieve();

        Object constraintValues[] = { getGearID() };
        Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == SETTER_COLUMNS.length) {
            itronCycleType = ItronCycleType.valueOf((String) results[0]);
        } else {
            throw new Error(getClass() + " - Incorrect Number of results retrieved");
        }
    }

    @Override
    public void update() throws SQLException {
        super.update();
        try {
            addPartial();
        } catch (SQLException e) {
            // add fail try to update
            Object setValues[] = { itronCycleType.name() };

            Object constraintValues[] = { getGearID() };

            update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
        }
    }


    public boolean isFrontRampEnabled() {
        return RAMP_RANDOM.equals(getFrontRampOption());
    }

    public void setFrontRampEnabled(boolean doRamp) {
        if (doRamp) {
            setFrontRampOption(RAMP_RANDOM);
        } else {
            setFrontRampOption(RAMP_NO_RAMP);
        }
    }

    public boolean isBackRampEnabled() {
        return RAMP_RANDOM.equals(getBackRampOption());
    }

    public void setBackRampEnabled(boolean doRamp) {
        if (doRamp) {
            setBackRampOption(RAMP_RANDOM);
        } else {
            setBackRampOption(RAMP_NO_RAMP);
        }
    }

    public Integer getCriticality() {
        return Integer.parseInt(getMethodOptionType());
    }

    public void setCriticality(Integer criticality) {
        this.setMethodOptionType(criticality.toString());
    }

    public Integer getControlPercent() {
        return getMethodRate();
    }

    public Integer getCyclePeriod() {
        return getMethodPeriod() / 60;
    }

    public void setControlPercent(Integer percent) {
        setMethodRate(percent);
    }

    public void setCyclePeriod(Integer period) {
        setMethodPeriod(period * 60);
    }
    
    public void setCycleType(String type) {
        itronCycleType = ItronCycleType.of(type);
    }
    
    public String getCycleType() {
        return itronCycleType.toString();
    }
}
