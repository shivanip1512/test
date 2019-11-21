package com.cannontech.database.data.device.lm;

import java.sql.SQLException;

import com.cannontech.common.dr.gear.setup.Mode;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;

public class EcobeeSetpointGear extends LMProgramDirectGear {
    private static final long serialVersionUID = 1L;
    
    private static final String TABLE_NAME = "LMThermostatGear";
    private static final String[] CONSTRAINT_COLUMNS = {"GearID"};
    private static final String[] SETTER_COLUMNS = {"Settings", "MinValue", "MaxValue", "ValueB", "ValueD", "ValueF", 
            "Random", "ValueTa", "ValueTb", "ValueTc", "ValueTd", "ValueTe", "ValueTf", "RampRate"};
    
    private int setpointOffset = 0;
    private HeatCool heatCool = HeatCool.COOL;
    
    /**
     * Enum to map between LmThermostatGear.Settings and Heat/Cool UI selector
     */
    public enum HeatCool {
        HEAT("--H-"),
        COOL("---I");
        
        private String dbValue;
        
        private HeatCool(String dbValue) {
            this.dbValue = dbValue;
        }
        
        public String getDbValue() {
            return dbValue;
        }
        
        public Mode getMode() {
            if(this == HEAT) {
                return Mode.HEAT;
            }
            return Mode.COOL;
        }
        
        public static HeatCool fromMode(Mode mode) {
            if(mode == Mode.HEAT) {
                return HEAT;
            }
            return COOL;
        }
        
        public static HeatCool of(Object dbValue) {
            if (HEAT.dbValue.equals(dbValue)) {
                return HEAT;
            }
            return COOL;
        }
    }
    
    public EcobeeSetpointGear() {
        setControlMethod(GearControlMethod.EcobeeSetpoint);
    }
    
    public void setMethodOptionType(boolean isSelected) {
        if (isSelected) {
            setMethodOptionType(OPTION_MANDATORY);
        } else {
            setMethodOptionType(OPTION_OPTIONAL);
        }
    }

    public boolean isMandatorySelected(String methodOptionType) {
        return OPTION_MANDATORY.equalsIgnoreCase(methodOptionType);
    }
    
    public void setSetpointOffset(int setpointOffset) {
        this.setpointOffset = setpointOffset;
    }
    
    public int getSetpointOffset() {
        return setpointOffset;
    }
    
    public void setHeatCool(HeatCool heatCool) {
        this.heatCool = heatCool;
    }
    
    public HeatCool getHeatCool() {
        return heatCool;
    }
    
    @Override
    public void add() throws SQLException {
        super.add();
        Object[] addValues = {getGearID(), heatCool.getDbValue(), 0, setpointOffset, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        add(TABLE_NAME, addValues);
    }
    
    @Override
    public void addPartial() throws SQLException {
        Object[] addValues = {getGearID(), heatCool.getDbValue(), 0, setpointOffset, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
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
        Object[] constraintValues = {getGearID()};    
        Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
        setSetpointOffset((int) results[2]);
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
            Object[] setValues = {getGearID(), heatCool.getDbValue(), 0, setpointOffset, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            Object[] constraintValues = {getGearID()};
            update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
        }
    }
}
