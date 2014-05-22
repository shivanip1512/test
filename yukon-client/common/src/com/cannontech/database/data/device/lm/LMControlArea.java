package com.cannontech.database.data.device.lm;

import java.util.Vector;

import com.cannontech.common.editor.EditorPanel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.NestedDBPersistent;
import com.cannontech.database.db.NestedDBPersistentComparators;
import com.cannontech.database.db.device.lm.LMControlAreaProgram;
import com.cannontech.database.db.device.lm.LMControlAreaTrigger;
import com.cannontech.spring.YukonSpringHook;

public class LMControlArea extends YukonPAObject implements EditorPanel {
    private com.cannontech.database.db.device.lm.LMControlArea controlArea = null;

    private Vector<LMControlAreaTrigger> lmControlAreaTriggerVector = null;
    private Vector<LMControlAreaProgram> lmControlAreaProgramVector = null;

    public LMControlArea() {
        super(PaoType.LM_CONTROL_AREA);
    }

    @Override
    public void add() throws java.sql.SQLException {
        if (getPAObjectID() == null) {
            PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);
            setPAObjectID(paoDao.getNextPaoId());
        }

        super.add();
        getControlArea().add();

        // add all the triggers for this control area
        if (getLmControlAreaTriggerVector() != null) {
            for (int i = 0; i < getLmControlAreaTriggerVector().size(); i++) {
                getLmControlAreaTriggerVector().elementAt(i).add();
            }
        }

        // add all the associated LMPrograms for this control area
        if (getLmControlAreaProgramVector() != null) {
            for (int i = 0; i < getLmControlAreaProgramVector().size(); i++) {
                getLmControlAreaProgramVector().elementAt(i).add();
            }
        }
    }

    @Override
    public void delete() throws java.sql.SQLException {
        setDbConnection(getControlArea().getDbConnection());

        deleteFromDynamicTables();

        // delete all the triggers for this control area
        LMControlAreaTrigger.deleteAllControlAreaTriggers(
            getControlArea().getDeviceID(), getDbConnection());

        // delete all the associated LMPrograms for this control area
        LMControlAreaProgram.deleteAllControlAreaProgramList(
            getControlArea().getDeviceID(), getDbConnection());

        getControlArea().delete();

        super.delete();

        setDbConnection(null);

    }

    private void deleteFromDynamicTables() throws java.sql.SQLException {
        delete("DynamicLMControlAreaTrigger", "deviceID", getPAObjectID());
        delete("DynamicLMControlArea", "deviceID", getPAObjectID());
    }

    public com.cannontech.database.db.device.lm.LMControlArea getControlArea() {
        if (controlArea == null) {
            controlArea = new com.cannontech.database.db.device.lm.LMControlArea();
        }

        return controlArea;
    }

    public Vector<LMControlAreaProgram> getLmControlAreaProgramVector() {
        if (lmControlAreaProgramVector == null) {
            lmControlAreaProgramVector = new Vector<>(10);
        }

        return lmControlAreaProgramVector;
    }

    public Vector<LMControlAreaTrigger> getLmControlAreaTriggerVector() {
        if (lmControlAreaTriggerVector == null) {
            lmControlAreaTriggerVector = new Vector<>(5);
        }

        return lmControlAreaTriggerVector;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getControlArea().retrieve();

        try {
            LMControlAreaTrigger triggers[] =
                LMControlAreaTrigger.getAllControlAreaTriggers(getControlArea().getDeviceID());
            for (int i = 0; i < triggers.length; i++) {
                triggers[i].setDbConnection(getDbConnection());
                getLmControlAreaTriggerVector().addElement(triggers[i]);
            }

            LMControlAreaProgram programs[] =
                LMControlAreaProgram.getAllControlAreaList(getControlArea().getDeviceID(), getDbConnection());
            for (int i = 0; i < programs.length; i++) {
                programs[i].setDbConnection(getDbConnection());
                getLmControlAreaProgramVector().addElement(programs[i]);
            }

        } catch (java.sql.SQLException e) {
            // not necessarily an error
        }

    }

    public void setControlArea(com.cannontech.database.db.device.lm.LMControlArea newArea) {
        controlArea = newArea;
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getControlArea().setDbConnection(conn);

        // do all the triggers for this control area
        if (getLmControlAreaTriggerVector() != null) {
            for (int i = 0; i < getLmControlAreaTriggerVector().size(); i++) {
                getLmControlAreaTriggerVector().elementAt(i).setDbConnection(conn);
            }
        }

        // do all the associated LMPrograms for this control area
        if (getLmControlAreaProgramVector() != null) {
            for (int i = 0; i < getLmControlAreaProgramVector().size(); i++) {
                getLmControlAreaProgramVector().elementAt(i).setDbConnection(conn);
            }
        }

    }

    public void setName(String name) {
        getYukonPAObject().setPaoName(name);
    }

    @Override
    public void setPAObjectID(Integer paoID) {
        super.setPAObjectID(paoID);
        getControlArea().setDeviceID(paoID);

        // do all the triggers for this control area
        if (getLmControlAreaTriggerVector() != null) {
            for (int i = 0; i < getLmControlAreaTriggerVector().size(); i++) {
                getLmControlAreaTriggerVector().elementAt(i).setDeviceID(paoID);
            }
        }

        // do all the associated LMPrograms for this control area
        if (getLmControlAreaProgramVector() != null) {
            for (int i = 0; i < getLmControlAreaProgramVector().size(); i++) {
                getLmControlAreaProgramVector().elementAt(i).setDeviceID(paoID);
            }
        }
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getControlArea().update();

        setPAObjectID(controlArea.getDeviceID());

        // grab all the previous trigger entries for this control area
        Vector<LMControlAreaTrigger> oldTrigs =
            LMControlAreaTrigger.getAllTriggersForAnArea(getPAObjectID(), getDbConnection());

        // unleash the power of the NestedDBPersistent
        Vector<LMControlAreaTrigger> comparedTrigs =
            NestedDBPersistentComparators.NestedDBPersistentCompare(oldTrigs, getLmControlAreaTriggerVector(),
                NestedDBPersistentComparators.lmControlAreaTriggerComparator);

        // throw the triggers at the Db
        for (int i = 0; i < comparedTrigs.size(); i++) {
            ((NestedDBPersistent) comparedTrigs.elementAt(i)).executeNestedOp();
        }

        // grab all the previous program entries for this scenario
        Vector<LMControlAreaProgram> oldProgs =
            LMControlAreaProgram.getAllProgramsForAnArea(getPAObjectID(), getDbConnection());

        // unleash the power of the NestedDBPersistent
        Vector<LMControlAreaProgram> comparedPrograms =
            NestedDBPersistentComparators.NestedDBPersistentCompare(oldProgs, getLmControlAreaProgramVector(),
                NestedDBPersistentComparators.lmControlAreaProgramComparator);

        // throw the programs into the Db
        for (int i = 0; i < comparedPrograms.size(); i++) {
            comparedPrograms.elementAt(i).setDeviceID(getPAObjectID());
            ((NestedDBPersistent) comparedPrograms.elementAt(i)).executeNestedOp();
        }
    }
}
