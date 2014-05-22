package com.cannontech.database.data.device.lm;

import java.util.Vector;

import com.cannontech.common.editor.EditorPanel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.device.lm.DeviceListItem;
import com.cannontech.database.db.device.lm.LMControlAreaProgram;
import com.cannontech.database.db.device.lm.LMControlScenarioProgram;
import com.cannontech.database.db.device.lm.LMProgram;
import com.cannontech.database.db.device.lm.LMProgramControlWindow;
import com.cannontech.spring.YukonSpringHook;

public abstract class LMProgramBase extends YukonPAObject implements EditorPanel {
    LMProgram program = null;

    private Vector<LMProgramControlWindow> lmProgramControlWindowVector = null;

    // used to store a list of data for each program.
    private Vector<DeviceListItem> lmProgramStorageVector = null;

    public static final String OPSTATE_AUTOMATIC = "Automatic";
    public static final String OPSTATE_MANUALONLY = "ManualOnly";
    public static final String OPSTATE_TIMED = "Timed";

    public LMProgramBase(PaoType paoType) {
        super(paoType);
    }

//    protected abstract PaoType getProgramPaoType();

    @Override
    public void add() throws java.sql.SQLException {
        if (getPAObjectID() == null) {
            PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);
            setPAObjectID(paoDao.getNextPaoId());
        }

        super.add();
        getProgram().add();

        for (LMProgramControlWindow lmProgramControlWindow : getLmProgramControlWindowVector()) {
            lmProgramControlWindow.add();
        }
    }

    @Override
    public void delete() throws java.sql.SQLException {
        // delete all the program windows
        LMProgramControlWindow.deleteAllProgramControlWindows(getPAObjectID(), getDbConnection());

        delete("DynamicLMProgram", "DeviceID", getPAObjectID());
        delete(LMControlAreaProgram.TABLE_NAME, "LMProgramDeviceID", getPAObjectID());
        delete(LMControlScenarioProgram.TABLE_NAME, "ProgramID", getPAObjectID());

        getProgram().delete();
        super.delete();
    }

    public Vector<LMProgramControlWindow> getLmProgramControlWindowVector() {
        if (lmProgramControlWindowVector == null)
            lmProgramControlWindowVector = new Vector<LMProgramControlWindow>(2);

        return lmProgramControlWindowVector;
    }

    public Vector<DeviceListItem> getLmProgramStorageVector() {
        if (lmProgramStorageVector == null)
            lmProgramStorageVector = new Vector<DeviceListItem>(10);

        return lmProgramStorageVector;
    }

    public com.cannontech.database.db.device.lm.LMProgram getProgram() {
        if (program == null)
            program = new com.cannontech.database.db.device.lm.LMProgram();

        return program;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getProgram().retrieve();

        // get all the LMProgramControlWindow for this LMProgramBase
        LMProgramControlWindow[] windows = LMProgramControlWindow.getAllLMProgramControlWindows(getPAObjectID(), getDbConnection());

        for (int i = 0; i < windows.length; i++)
            getLmProgramControlWindowVector().add(windows[i]);
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getProgram().setDbConnection(conn);

        for (LMProgramControlWindow lmProgramControlWindow : getLmProgramControlWindowVector()) {
            lmProgramControlWindow.setDbConnection(conn);
        }
    }

    public void setLmProgramControlWindowVector(Vector<LMProgramControlWindow> newLmProgramControlWindowVector) {
        lmProgramControlWindowVector = newLmProgramControlWindowVector;
    }

    public void setLmProgramStorageVector(Vector<DeviceListItem> newlmProgramStorageVector) {
        lmProgramStorageVector = newlmProgramStorageVector;
    }

    public void setName(String name) {
        getYukonPAObject().setPaoName(name);
    }

    @Override
    public void setPAObjectID(Integer paoID) {
        super.setPAObjectID(paoID);
        getProgram().setDeviceID(paoID);

        for (LMProgramControlWindow lmProgramControlWindow : getLmProgramControlWindowVector()) {
            lmProgramControlWindow.setDeviceID(paoID);
        }
    }

    public void setProgram(LMProgram newProgram) {
        program = newProgram;
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getProgram().update();

        // delete all the program windows
        LMProgramControlWindow.deleteAllProgramControlWindows(getPAObjectID(), getDbConnection());

        for (LMProgramControlWindow lmProgramControlWindow : getLmProgramControlWindowVector()) {
            lmProgramControlWindow.add();
        }
    }
}