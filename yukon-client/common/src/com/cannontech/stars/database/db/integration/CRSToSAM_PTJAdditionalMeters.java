package com.cannontech.stars.database.db.integration;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;

public class CRSToSAM_PTJAdditionalMeters extends DBPersistent {

    private Integer ptjID;
    private String meterNumber;

    private static final String CONSTRAINT_COLUMNS[] = { "PTJID" };

    private static final String SETTER_COLUMNS[] = { "PTJID", "MeterNumber" };

    public static final String TABLE_NAME = "CRSToSAM_PTJAdditionalMeters";

    @Override
    public void add() throws SQLException {
        Object setValues[] = { getPTJID(), getMeterNumber() };

        add(TABLE_NAME, setValues);
    }

    @Override
    public void delete() throws SQLException {
        Object constraintValues[] = { getPTJID() };

        delete(TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
    }

    @Override
    public void retrieve() throws SQLException {
        Object constraintValues[] = { getPTJID() };

        Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == SETTER_COLUMNS.length) {
            setMeterNumber((String) results[0]);
        } else {
            throw new Error(getClass() + "::retrieve - Incorrect number of results");
        }
    }

    @Override
    public void update() throws SQLException {
        Object setValues[] = { getPTJID(), getMeterNumber() };

        Object constraintValues[] = { getPTJID() };

        update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
    }

    /**
     * Loads from database all additional ptj meters. Returns map of ptjID <Integer> to ArrayList (of
     * CRSToSAM_PTJAdditionalMeters objects)
     * 
     * @param ptjID_
     * @return
     */
    public static Map<Integer, List<CRSToSAM_PTJAdditionalMeters>> retrieveAllCurrentPTJAdditionalMeterEntriesMap() {
        Map<Integer, List<CRSToSAM_PTJAdditionalMeters>> ptjToAdditionMeterMap = new HashMap<>();
        List<CRSToSAM_PTJAdditionalMeters> changes = new ArrayList<>();

        SqlStatement stmt = new SqlStatement("SELECT * FROM " + TABLE_NAME, CtiUtilities.getDatabaseAlias());

        try {
            stmt.execute();

            if (stmt.getRowCount() > 0) {
                int lastPTJID = -1;
                CRSToSAM_PTJAdditionalMeters currentEntry = null;
                for (int i = 0; i < stmt.getRowCount(); i++) {
                    currentEntry = new CRSToSAM_PTJAdditionalMeters();
                    currentEntry.setPTJID(new Integer(stmt.getRow(i)[0].toString()));
                    currentEntry.setMeterNumber(stmt.getRow(i)[1].toString());

                    if (currentEntry.getPTJID().intValue() != lastPTJID && lastPTJID > -1) {
                        ptjToAdditionMeterMap.put(lastPTJID, changes);
                        changes = new ArrayList<>();
                    }

                    changes.add(currentEntry);
                    lastPTJID = currentEntry.getPTJID().intValue();
                }
                // add the last one
                if (!changes.isEmpty()) {
                    ptjToAdditionMeterMap.put(currentEntry.getPTJID(), changes);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ptjToAdditionMeterMap;
    }

    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public Integer getPTJID() {
        return ptjID;
    }

    public void setPTJID(Integer ptjID) {
        this.ptjID = ptjID;
    }
}
