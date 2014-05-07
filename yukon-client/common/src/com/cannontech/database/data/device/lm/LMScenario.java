package com.cannontech.database.data.device.lm;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import com.cannontech.common.editor.EditorPanel;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.NestedDBPersistent;
import com.cannontech.database.db.NestedDBPersistentComparators;
import com.cannontech.database.db.device.lm.LMControlScenarioProgram;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.spring.YukonSpringHook;

public class LMScenario extends YukonPAObject implements CTIDbChange, EditorPanel {
    private Vector<LMControlScenarioProgram> allThePrograms;

    public LMScenario() {
        getYukonPAObject().setType(PAOGroups.STRING_LM_SCENARIO[0]);
    }

    @Override
    public void add() throws SQLException {
        if (getScenarioID() == null) {
            PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);
            setScenarioID(paoDao.getNextPaoId());
            if (getAllThePrograms() != null) {
                for (int j = 0; j < getAllThePrograms().size(); j++) {
                    getAllThePrograms().elementAt(j).setScenarioID(getScenarioID());
                }
            }
        }
        super.add();

        // add all the wee programs to the database
        if (getAllThePrograms() != null) {
            for (int j = 0; j < getAllThePrograms().size(); j++) {
                getAllThePrograms().elementAt(j).add();
            }
        }
    }

    @Override
    public void delete() throws SQLException {
        retrieve();
        // delete all the wee programs from the database
        if (getAllThePrograms() != null) {
            for (int j = 0; j < getAllThePrograms().size(); j++) {
                getAllThePrograms().elementAt(j).delete();
            }
        }
        super.delete();
    }

    public Integer getScenarioID() {
        return getPAObjectID();
    }

    public String getScenarioName() {
        return getPAOName();
    }

    public Vector<LMControlScenarioProgram> getAllThePrograms() {
        if (allThePrograms == null) {
            allThePrograms = new Vector<>();
        }
        return allThePrograms;
    }

    @Override
    public void retrieve() throws SQLException {
        super.retrieve();

        // retrieve all the programs for this scenario
        Vector<LMControlScenarioProgram> progs =
            LMControlScenarioProgram.getAllProgramsForAScenario(getPAObjectID(), getDbConnection());
        for (int i = 0; i < progs.size(); i++) {
            getAllThePrograms().add(progs.elementAt(i));
        }
    }

    public void setScenarioID(Integer newID) {
        setPAObjectID(newID);

        if (getAllThePrograms() != null) {
            for (int j = 0; j < getAllThePrograms().size(); j++) {
                getAllThePrograms().elementAt(j).setScenarioID(newID);
            }
        }
    }

    public void setScenarioName(String newName) {
        setPAOName(newName);
    }

    @Override
    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);

        // do all the programs for this scenario
        if (getAllThePrograms() != null) {
            for (int j = 0; j < getAllThePrograms().size(); j++) {
                getAllThePrograms().elementAt(j).setDbConnection(conn);
            }
        }
    }

    public void setAllThePrograms(Vector<LMControlScenarioProgram> newVector) {
        allThePrograms = newVector;
    }

    @Override
    public void update() throws SQLException {
        super.update();

        // grab all the previous program entries for this scenario
        Vector<LMControlScenarioProgram> oldProgs =
            LMControlScenarioProgram.getAllProgramsForAScenario(getPAObjectID(), getDbConnection());

        // unleash the power of the NestedDBPersistent
        Vector<LMControlScenarioProgram> comparedPrograms =
            NestedDBPersistentComparators.NestedDBPersistentCompare(oldProgs, getAllThePrograms(),
                NestedDBPersistentComparators.lmControlScenarioProgramComparator);

        // throw the programs into the Db
        for (int i = 0; i < comparedPrograms.size(); i++) {
            comparedPrograms.elementAt(i).setScenarioID(getPAObjectID());
            ((NestedDBPersistent) comparedPrograms.elementAt(i)).executeNestedOp();

        }
    }

    public static Integer getDefaultGearID(Integer programID) {
        Connection conn = null;

        conn = PoolManager.getInstance().getConnection("yukon");

        Integer id = 0;

        try {
            id = LMProgramDirectGear.getDefaultGearID(programID, conn);
            conn.close();
        } catch (SQLException e2) {
            e2.printStackTrace(); // something is up
        }
        return id;
    }
}
