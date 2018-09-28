package com.cannontech.database.db.device.lm;

import java.sql.SQLException;

import com.cannontech.dr.nest.model.LoadShapingPeak;
import com.cannontech.dr.nest.model.LoadShapingPost;
import com.cannontech.dr.nest.model.LoadShapingPreparation;

public abstract class LMNestGear extends LMProgramDirectGear {

    private static final long serialVersionUID = 1L;
    private LoadShapingPreparation loadShapingPreparation = null;
    private LoadShapingPeak loadShapingPeak = null;
    private LoadShapingPost loadShapingPost = null;

    public static final String SETTER_COLUMNS[] = { "PreparationOption", "PeakOption", "PostPeakOption" };

    public static final String CONSTRAINT_COLUMNS[] = { "GearId" };

    public static final String TABLE_NAME = "LMNestLoadShapingGear";

    public LMNestGear() {
        super();
    }

    @Override
    public void add() throws SQLException {
        super.add();

        Object addValues[] = { getGearID(), getLoadShapingPreparation().getDatabaseRepresentation(),
            getLoadShapingPeak().getDatabaseRepresentation(), getLoadShapingPost().getDatabaseRepresentation() };

        add(TABLE_NAME, addValues);
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        Object addValues[] = { getGearID(), getLoadShapingPreparation().getDatabaseRepresentation(),
            getLoadShapingPeak().getDatabaseRepresentation(), getLoadShapingPost().getDatabaseRepresentation() };

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
            setLoadShapingPreparation(LoadShapingPreparation.getLoadShapingPreparation(results[0]));
            setLoadShapingPeak(LoadShapingPeak.getLoadShapingPeak(results[1]));
            setLoadShapingPost(LoadShapingPost.getLoadShapingPost(results[2]));
        } else
            throw new Error(getClass() + " - Incorrect Number of results retrieved");

    }

    @Override
    public void update() throws SQLException {
        super.update();
        try {
            addPartial();
        } catch (SQLException e) {
            // add fail try to update
            Object setValues[] = { getLoadShapingPreparation().getDatabaseRepresentation(),
                getLoadShapingPeak().getDatabaseRepresentation(), getLoadShapingPost().getDatabaseRepresentation() };

            Object constraintValues[] = { getGearID() };

            update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
        }
    }

    public LoadShapingPreparation getLoadShapingPreparation() {
        return loadShapingPreparation;
    }

    public void setLoadShapingPreparation(LoadShapingPreparation loadShapingPreparation) {
        this.loadShapingPreparation = loadShapingPreparation;
    }

    public LoadShapingPeak getLoadShapingPeak() {
        return loadShapingPeak;
    }

    public void setLoadShapingPeak(LoadShapingPeak loadShapingPeak) {
        this.loadShapingPeak = loadShapingPeak;
    }

    public LoadShapingPost getLoadShapingPost() {
        return loadShapingPost;
    }

    public void setLoadShapingPost(LoadShapingPost loadShapingPost) {
        this.loadShapingPost = loadShapingPost;
    }
}