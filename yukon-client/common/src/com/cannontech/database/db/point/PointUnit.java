package com.cannontech.database.db.point;

import java.sql.SQLException;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.DBPersistent;

public class PointUnit extends DBPersistent {
    
    public static final int DEFAULT_DECIMAL_PLACES = 3;
    public static final int DEFAULT_METER_DIALS = 0;

    private Integer pointID = null;
    private Integer uomID = UnitOfMeasure.KWH.getId();
    private Integer decimalPlaces = DEFAULT_DECIMAL_PLACES;
    private Double highReasonabilityLimit = CtiUtilities.INVALID_MAX_DOUBLE;
    private Double lowReasonabilityLimit = CtiUtilities.INVALID_MIN_DOUBLE;
    private Integer meterDials = new Integer(0);

    public static final String CONSTRAINT_COLUMNS[] = { "POINTID" };
    public static final String SETTER_COLUMNS[] = { 
        "UomID",
        "DecimalPlaces",
        "HighReasonabilityLimit",
        "LowReasonabilityLimit",
        "DecimalDigits" };

    public final static String TABLE_NAME = "PointUnit";

    public PointUnit() {
        super();
    }

    public PointUnit(Integer pointID, Integer umID, Integer newDecimalPlaces, Double highReasonValue, Double lowReasonValue,
            Integer newMeterDials) {
        super();

        setPointID(pointID);
        setUomID(umID);
        setDecimalPlaces(newDecimalPlaces);
        setHighReasonabilityLimit(highReasonValue);
        setLowReasonabilityLimit(lowReasonValue);
        setMeterDials(newMeterDials);
    }

    public void add() throws SQLException {
        Object addValues[] =
            { getPointID(), getUomID(), getDecimalPlaces(), getHighReasonabilityLimit(), getLowReasonabilityLimit(),
                getMeterDials() };

        add(TABLE_NAME, addValues);
    }

    public void delete() throws SQLException {
        delete(TABLE_NAME, CONSTRAINT_COLUMNS[0], getPointID());
    }

    public Integer getDecimalPlaces() {
        return decimalPlaces;
    }

    public Double getHighReasonabilityLimit() {
        return highReasonabilityLimit;
    }

    public Double getLowReasonabilityLimit() {
        return lowReasonabilityLimit;
    }

    public Integer getPointID() {
        return pointID;
    }

    public Integer getUomID() {
        return uomID;
    }

    public void retrieve() throws SQLException {
        Object constraintValues[] = { getPointID() };

        Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == SETTER_COLUMNS.length) {
            setUomID((Integer) results[0]);
            setDecimalPlaces((Integer) results[1]);
            setHighReasonabilityLimit((Double) results[2]);
            setLowReasonabilityLimit((Double) results[3]);
            setMeterDials((Integer) results[4]);
        } else
            throw new Error(getClass() + " - Incorrect Number of results retrieved");

    }

    public void setDecimalPlaces(Integer newDecimalPlaces) {
        decimalPlaces = newDecimalPlaces;
    }

    public void setHighReasonabilityLimit(Double newHighReasonabilityLimit) {
        highReasonabilityLimit = newHighReasonabilityLimit;
    }

    public void setLowReasonabilityLimit(Double newLowReasonabilityLimit) {
        lowReasonabilityLimit = newLowReasonabilityLimit;
    }

    public void setPointID(Integer newValue) {
        this.pointID = newValue;
    }

    public void setUomID(Integer newUomID) {
        uomID = newUomID;
    }

    public void update() throws SQLException {
        Object setValues[] =
            { getUomID(), getDecimalPlaces(), getHighReasonabilityLimit(), getLowReasonabilityLimit(), getMeterDials() };

        Object constraintValues[] = { getPointID() };

        update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
    }

    public Integer getMeterDials() {
        return meterDials;
    }

    public void setMeterDials(Integer decimalDigits) {
        this.meterDials = decimalDigits;
    }

}