package com.cannontech.database.db.device.lm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.NestedDBPersistent;

public class LMControlScenarioProgram extends NestedDBPersistent {
    private Integer scenarioID;
    private Integer programID;
    private Integer startOffset = 0;
    private Integer stopOffset = 0;
    private Integer startGear = 0;

    public static final String SETTER_COLUMNS[] = { "SCENARIOID", "PROGRAMID", "STARTOFFSET", "STOPOFFSET", "STARTGEAR" };
    public static final String CONSTRAINT_COLUMNS[] = { "scenarioID", "programID" };
    public static final String TABLE_NAME = "LMControlScenarioProgram";

    @Override
    public void add() throws SQLException {
        Object addValues[] = { getScenarioID(), getProgramID(), getStartOffset(), getStopOffset(), getStartGear() };

        add(TABLE_NAME, addValues);
    }

    @Override
    public void delete() throws SQLException {
        Object deleteValues[] = { getScenarioID(), getProgramID() };
        delete(TABLE_NAME, CONSTRAINT_COLUMNS, deleteValues);
    }

    public Integer getScenarioID() {
        return scenarioID;
    }

    public Integer getProgramID() {
        return programID;
    }

    public Integer getStartOffset() {
        return startOffset;
    }

    public Integer getStopOffset() {
        return stopOffset;
    }

    public Integer getStartGear() {
        return startGear;
    }

    @Override
    public void retrieve() {
        Integer constraintValues[] = { getScenarioID(), getProgramID() };

        try {
            Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

            if (results.length == SETTER_COLUMNS.length) {
                setProgramID((Integer) results[1]);
                setStartOffset((Integer) results[2]);
                setStopOffset((Integer) results[3]);
                setStartGear((Integer) results[4]);

            } else {
                throw new Error(getClass() + " - Incorrect Number of results retrieved");
            }
        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);
        }
    }

    public void setScenarioID(Integer newScenarioID) {
        scenarioID = newScenarioID;
    }

    public void setProgramID(Integer newProgramID) {
        programID = newProgramID;
    }

    public void setStartOffset(Integer offset) {
        startOffset = offset;
    }

    public void setStopOffset(Integer stOff) {
        stopOffset = stOff;
    }

    public void setStartGear(Integer gear) {
        startGear = gear;
    }

    @Override
    public void update() {
        Object setValues[] = { getScenarioID(), getProgramID(), getStartOffset(), getStopOffset(), getStartGear() };

        Object constraintValues[] = { getScenarioID(), getProgramID() };

        try {
            update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);
        }
    }

    public static final Vector<LMControlScenarioProgram> getAllProgramsForAScenario(Integer scenarioID, Connection conn) {
        Vector<LMControlScenarioProgram> progList = new Vector<>();
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        // get all the programs that are associated with the passed ScenarioID
        String sql = "select scenarioID, programID, startOffset, stopOffset, startGear"
                + " from " + TABLE_NAME + " where scenarioId = ? order by programId";
        try {
            if (conn == null) {
                throw new IllegalArgumentException("Received a (null) database connection");
            }

            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setInt(1, scenarioID.intValue());

            rset = pstmt.executeQuery();

            while (rset.next()) {
                LMControlScenarioProgram prog = new LMControlScenarioProgram();
                prog.setScenarioID(scenarioID);
                prog.setProgramID(new Integer(rset.getInt(2)));
                prog.setStartOffset(new Integer(rset.getInt(3)));
                prog.setStopOffset(new Integer(rset.getInt(4)));
                prog.setStartGear(new Integer(rset.getInt(5)));
                prog.setDbConnection(conn);

                progList.add(prog);
            }
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, pstmt);
        }

        return progList;
    }
}
