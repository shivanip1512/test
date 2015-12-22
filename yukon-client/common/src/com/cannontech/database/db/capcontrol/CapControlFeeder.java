package com.cannontech.database.db.capcontrol;

import java.sql.SQLException;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.DBPersistent;

public class CapControlFeeder extends DBPersistent {
    private Integer feederID = null;
    private Integer currentVarLoadPointID = CtiUtilities.NONE_ZERO_ID;
    private Integer currentWattLoadPointID = CtiUtilities.NONE_ZERO_ID;
    private String mapLocationID = "0"; // old integer default
    private Integer currentVoltLoadPointID = CtiUtilities.NONE_ZERO_ID;
    private boolean multiMonitorControl = false;
    private boolean usePhaseData = false;
    private Integer phaseB = CtiUtilities.NONE_ZERO_ID;
    private Integer phaseC = CtiUtilities.NONE_ZERO_ID;
    private boolean controlFlag = false;

    public static final String TABLE_NAME = "CapControlFeeder";
    public static final String CONSTRAINT_COLUMNS[] = { "FeederID" };
    public static final String SETTER_COLUMNS[] = {
        "CurrentVarLoadPointID",
        "CurrentWattLoadPointID",
        "MapLocationID",
        "CurrentVoltLoadPointID",
        "MultiMonitorControl",
        "UsePhaseData",
        "PhaseB",
        "PhaseC",
        "ControlFlag"
    };

    public CapControlFeeder() {
        super();
    }

    public void add() throws SQLException {
        Object[] addValues = {
            getFeederID(),
            getCurrentVarLoadPointID(),
            getCurrentWattLoadPointID(),
            getMapLocationID(),
            getCurrentVoltLoadPointID(),
            getMultiMonitorControl(),
            getUsePhaseData(),
            getPhaseB(),
            getPhaseC(),
            getControlFlag()
        };

        add(TABLE_NAME, addValues);
    }

    public void delete() throws SQLException {
        delete(TABLE_NAME, CONSTRAINT_COLUMNS[0], getFeederID());
    }

    public Integer getCurrentVarLoadPointID() {
        return currentVarLoadPointID;
    }

    public Integer getCurrentWattLoadPointID() {
        return currentWattLoadPointID;
    }

    public Integer getFeederID() {
        return feederID;
    }

    public String getMapLocationID() {
        return mapLocationID;
    }

    public void retrieve() throws SQLException {
        Object constraintValues[] = { getFeederID() };
        Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == SETTER_COLUMNS.length) {
            setCurrentVarLoadPointID((Integer) results[0]);
            setCurrentWattLoadPointID((Integer) results[1]);
            setMapLocationID((String) results[2]);
            setCurrentVoltLoadPointID((Integer) results[3]);
            setMultiMonitorControl((String) results[4]);
            setUsePhaseData((String) results[5]);
            setPhaseB((Integer) results[6]);
            setPhaseC((Integer) results[7]);
            setControlFlags((String) results[8]);
        } else {
            throw new Error(getClass() + " - Incorrect Number of results retrieved");
        }
    }

    public void setCurrentVarLoadPointID(Integer newCurrentVarLoadPointID) {
        currentVarLoadPointID = newCurrentVarLoadPointID;
    }

    public void setCurrentWattLoadPointID(Integer newCurrentWattLoadPointID) {
        currentWattLoadPointID = newCurrentWattLoadPointID;
    }

    public void setFeederID(Integer newFeederID) {
        feederID = newFeederID;
    }

    public void setMapLocationID(String newMapLocationID) {
        mapLocationID = newMapLocationID;
    }

    public void update() throws SQLException {
        Object setValues[] = {
            getCurrentVarLoadPointID(),
            getCurrentWattLoadPointID(),
            getMapLocationID(),
            getCurrentVoltLoadPointID(),
            getMultiMonitorControl(),
            getUsePhaseData(),
            getPhaseB(),
            getPhaseC(),
            getControlFlag()
        };

        Object constraintValues[] = { getFeederID() };

        update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
    }

    public Integer getCurrentVoltLoadPointID() {
        return currentVoltLoadPointID;
    }

    public void setCurrentVoltLoadPointID(Integer integer) {
        currentVoltLoadPointID = integer;
    }

    public String getMultiMonitorControl() {
        return multiMonitorControl ? CtiUtilities.TRUE_STRING : CtiUtilities.FALSE_STRING;
    }

    public void setMultiMonitorControl(String multiMonitorControl) {
        this.multiMonitorControl = CtiUtilities.isTrue(multiMonitorControl);
    }

    public Integer getPhaseB() {
        return phaseB;
    }

    public void setPhaseB(Integer phaseB) {
        this.phaseB = phaseB;
    }

    public Integer getPhaseC() {
        return phaseC;
    }

    public void setPhaseC(Integer phaseC) {
        this.phaseC = phaseC;
    }

    public String getUsePhaseData() {
        return usePhaseData ? CtiUtilities.TRUE_STRING : CtiUtilities.FALSE_STRING;
    }

    public void setUsePhaseData(String usePhaseData) {
        this.usePhaseData = CtiUtilities.isTrue(usePhaseData);
    }

    public String getControlFlag() {
        return controlFlag ? CtiUtilities.TRUE_STRING : CtiUtilities.FALSE_STRING;
    }

    public void setControlFlags(String controlFlag) {
        this.controlFlag = CtiUtilities.isTrue(controlFlag);
    }

    public boolean getControlFlagBoolean() {
        return controlFlag;
    }

    public void setControlFlagBoolean(boolean bool) {
        this.controlFlag = bool;
    }

    public boolean getUsePhaseDataBoolean() {
        return usePhaseData;
    }

    public void setUsePhaseDataBoolean(boolean bool) {
        this.usePhaseData = bool;
    }

}