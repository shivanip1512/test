package com.cannontech.database.data.capcontrol;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.common.editor.EditorPanel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.capcontrol.CCSubAreaAssignment;
import com.cannontech.spring.YukonSpringHook;

public class CapControlArea extends CapControlYukonPAOBase implements EditorPanel {

    private com.cannontech.database.db.capcontrol.CapControlArea capControlArea;
    private ArrayList<CCSubAreaAssignment> areaSubs;

    public CapControlArea() {
        super(PaoType.CAP_CONTROL_AREA);
    }

    @Override
    public void retrieve() throws SQLException {
        super.retrieve();

        getCapControlArea().retrieve();
        List<CCSubAreaAssignment> allAreaSubs = CCSubAreaAssignment.getAllAreaSubStations(getCapControlPAOID());
        for (CCSubAreaAssignment assignment : allAreaSubs) {
            areaSubs.add(assignment);
        }
    }

    @Override
    public void delete() throws SQLException {
        // remove all the associations of Subs to this Area
        CCSubAreaAssignment.deleteSubs(getAreaID(), null, getDbConnection());

        // Delete from all dynamic objects
        delete("DynamicCCArea", "AreaID", getAreaID());
        delete("CapControlComment", "paoID", getAreaID());
        getCapControlArea().delete();
        super.delete();
    }

    private Integer getAreaID() {
        return getPAObjectID();
    }

    @Override
    public void add() throws SQLException {

        if (getPAObjectID() == null) {
            PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);
            setCapControlPAOID(paoDao.getNextPaoId());
        }

        super.add();

        getCapControlArea().add();

        for (int i = 0; i < getChildList().size(); i++) {
            getChildList().get(i).add();
        }
    }

    public com.cannontech.database.db.capcontrol.CapControlArea getCapControlArea() {
        if (capControlArea == null) {
            capControlArea = new com.cannontech.database.db.capcontrol.CapControlArea();
        }
        return capControlArea;
    }

    @Override
    public ArrayList<CCSubAreaAssignment> getChildList() {
        if (areaSubs == null) {
            areaSubs = new ArrayList<CCSubAreaAssignment>();
        }
        return areaSubs;
    }

    @Override
    public void setCapControlPAOID(Integer paoID) {
        super.setPAObjectID(paoID);
        getCapControlArea().setAreaID(paoID);

        for (int i = 0; i < getChildList().size(); i++)
            getChildList().get(i).setAreaID(paoID);

    }

    @Override
    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
        getCapControlArea().setDbConnection(conn);

        for (int i = 0; i < getChildList().size(); i++)
            getChildList().get(i).setDbConnection(conn);

    }

    public void setAreaSubs(ArrayList<CCSubAreaAssignment> areaSubs) {
        this.areaSubs = areaSubs;
    }

    public void setCapControlArea(
            com.cannontech.database.db.capcontrol.CapControlArea capControlArea) {
        this.capControlArea = capControlArea;
    }

    @Override
    public void update() throws SQLException {
        super.update();

        getCapControlArea().update();

        CCSubAreaAssignment.deleteSubs(getCapControlPAOID(),
                                       null,
                                       getDbConnection());
        Connection connection = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
        for (int i = 0; i < getChildList().size(); i++) {
            CCSubAreaAssignment subAreaAssignment = getChildList().get(i);
            subAreaAssignment.setDbConnection(connection );
            subAreaAssignment.add();
        }
        connection.close();
    }

    public static List<Integer> getAllAreaIDs() {
        SqlStatementBuilder builder = new SqlStatementBuilder();
        builder.append("select areaid from capcontrolarea");
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        return yukonTemplate.queryForList(builder.toString(), Integer.class);
    }

    public static Integer  getAreaIdByName (String name) {
        SqlStatementBuilder builder = new SqlStatementBuilder();
        builder.append("select areaid from capcontrolarea, yukonpaobject");
        builder.append("where areaid = paobjectid and paoname like "  + "'" +name+ "'");
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        return yukonTemplate.queryForObject(builder.toString(), Integer.class);
    }

}
