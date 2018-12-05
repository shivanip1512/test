package com.cannontech.database.data.device.lm;

import java.sql.SQLException;

import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.dr.nest.model.v3.LoadShapingOptions;
import com.cannontech.dr.nest.model.v3.PeakLoadShape;
import com.cannontech.dr.nest.model.v3.PostLoadShape;
import com.cannontech.dr.nest.model.v3.PrepLoadShape;

public class NestStandardCycleGear extends com.cannontech.database.db.device.lm.LMNestGear {

    private LoadShapingOptions loadShapingOptions = null;

    public static final String SETTER_COLUMNS[] = { "PreparationOption", "PeakOption", "PostPeakOption" };
    public static final String CONSTRAINT_COLUMNS[] = { "GearId" };
    public static final String TABLE_NAME = "LMNestLoadShapingGear";

    public NestStandardCycleGear() {
        setControlMethod(GearControlMethod.NestStandardCycle);
        setGearID(super.getGearID());
    }

    @Override
    public void add() throws SQLException {
        super.add();
        Object addValues[] = { getGearID(),
                loadShapingOptions.getPrepLoadShape().getDatabaseRepresentation(),
                loadShapingOptions.getPeakLoadShape().getDatabaseRepresentation(),
                loadShapingOptions.getPostLoadShape().getDatabaseRepresentation() };

        add(TABLE_NAME, addValues);
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        Object addValues[] = { getGearID(),
                loadShapingOptions.getPrepLoadShape().getDatabaseRepresentation(),
                loadShapingOptions.getPeakLoadShape().getDatabaseRepresentation(),
                loadShapingOptions.getPostLoadShape().getDatabaseRepresentation() };
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
            loadShapingOptions = new LoadShapingOptions(PrepLoadShape.getPrepLoadShape(results[0]), 
                                                                PeakLoadShape.getPeakLoadShape(results[1]), 
                                                                PostLoadShape.getPostLoadShape(results[2]));
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
            Object setValues[] = { loadShapingOptions.getPrepLoadShape().getDatabaseRepresentation(),
                    loadShapingOptions.getPeakLoadShape().getDatabaseRepresentation(),
                    loadShapingOptions.getPostLoadShape().getDatabaseRepresentation() };

            Object constraintValues[] = { getGearID() };

            update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
        }
    }
    
 
    public void setLoadShapingOptions(LoadShapingOptions loadShapingOptions) {
        this.loadShapingOptions = loadShapingOptions;
    }
    
    public void setLoadShapingOptions(PrepLoadShape prepLoadShape, PeakLoadShape peakLoadShape, PostLoadShape postLoadShape) {
        this.loadShapingOptions = new LoadShapingOptions(prepLoadShape, peakLoadShape, postLoadShape);
    }    
    
    public LoadShapingOptions getLoadShapingOptions() {
        return loadShapingOptions;
    }
    
    public PrepLoadShape getPrepLoadShape() {
        return getLoadShapingOptions().getPrepLoadShape();
    }

    public PeakLoadShape getPeakLoadShape() {
        return getLoadShapingOptions().getPeakLoadShape();
    }

    public PostLoadShape getPostLoadShape() {
        return getLoadShapingOptions().getPostLoadShape();
    }
}