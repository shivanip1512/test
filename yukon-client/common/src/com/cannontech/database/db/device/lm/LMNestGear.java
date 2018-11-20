package com.cannontech.database.db.device.lm;

import java.sql.SQLException;

import com.cannontech.dr.nest.model.v3.PeakLoadShape;
import com.cannontech.dr.nest.model.v3.PostLoadShape;
import com.cannontech.dr.nest.model.v3.PrepLoadShape;

public abstract class LMNestGear extends LMProgramDirectGear {

    private static final long serialVersionUID = 1L;
    private PrepLoadShape loadShapingPreparation = null;
    private PeakLoadShape loadShapingPeak = null;
    private PostLoadShape loadShapingPost = null;

    public static final String SETTER_COLUMNS[] = { "PreparationOption", "PeakOption", "PostPeakOption" };

    public static final String CONSTRAINT_COLUMNS[] = { "GearId" };

    public static final String TABLE_NAME = "LMNestLoadShapingGear";

    public LMNestGear() {
        super();
    }

    @Override
    public void add() throws SQLException {
        super.add();

        Object addValues[] = { getGearID(), getPrepLoadShape().getDatabaseRepresentation(),
            getPeakLoadShape().getDatabaseRepresentation(), getPostLoadShape().getDatabaseRepresentation() };

        add(TABLE_NAME, addValues);
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        Object addValues[] = { getGearID(), getPrepLoadShape().getDatabaseRepresentation(),
            getPeakLoadShape().getDatabaseRepresentation(), getPostLoadShape().getDatabaseRepresentation() };

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
            setPrepLoadShape(PrepLoadShape.getPrepLoadShape(results[0]));
            setPeakLoadShape(PeakLoadShape.getPeakLoadShape(results[1]));
            setPostLoadShape(PostLoadShape.getPostLoadShape(results[2]));
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
            Object setValues[] = { getPrepLoadShape().getDatabaseRepresentation(),
                getPeakLoadShape().getDatabaseRepresentation(), getPostLoadShape().getDatabaseRepresentation() };

            Object constraintValues[] = { getGearID() };

            update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
        }
    }

    public PrepLoadShape getPrepLoadShape() {
        return loadShapingPreparation;
    }

    public void setPrepLoadShape(PrepLoadShape loadShapingPreparation) {
        this.loadShapingPreparation = loadShapingPreparation;
    }

    public PeakLoadShape getPeakLoadShape() {
        return loadShapingPeak;
    }

    public void setPeakLoadShape(PeakLoadShape loadShapingPeak) {
        this.loadShapingPeak = loadShapingPeak;
    }

    public PostLoadShape getPostLoadShape() {
        return loadShapingPost;
    }

    public void setPostLoadShape(PostLoadShape loadShapingPost) {
        this.loadShapingPost = loadShapingPost;
    }
}