package com.cannontech.database.data.capcontrol;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.common.editor.EditorPanel;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.SeasonScheduleDao;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.capcontrol.CCSubAreaAssignment;

public class CapControlArea extends CapControlYukonPAOBase implements EditorPanel {

    private com.cannontech.database.db.capcontrol.CapControlArea capControlArea;
    private ArrayList<CCSubAreaAssignment> areaSubs;

    public CapControlArea() {
        super();
        setPAOCategory(PAOGroups.STRING_CAT_CAPCONTROL);
        setPAOClass(PAOGroups.STRING_CAT_CAPCONTROL);
        getYukonPAObject().setType(PAOGroups.STRING_CAPCONTROL_AREA);
    }

    public void retrieve() throws SQLException {
        super.retrieve();

        getCapControlArea().retrieve();
        List<CCSubAreaAssignment> allAreaSubs = CCSubAreaAssignment.getAllAreaSubStations(getCapControlPAOID());
        for (CCSubAreaAssignment assignment : allAreaSubs) {
            areaSubs.add(assignment);
        }
    }

    public void delete() throws SQLException {

        // remove all the associations of Subs to this Area
        com.cannontech.database.db.capcontrol.CCSubAreaAssignment.deleteSubs(getAreaID(),
                                                                             null,
                                                                             getDbConnection());

        // Delete from all dynamic objects
        delete("DynamicCCArea", "AreaID", getAreaID());

        // delete(Point.TABLE_NAME, Point.SETTER_COLUMNS[2],
        // getCapControlPAOID());
        
        SeasonScheduleDao ssDao = DaoFactory.getSeasonSchedule();
        ssDao.deleteStrategyAssigment(getCapControlPAOID());

        getCapControlArea().delete();
        super.delete();
    }

    private Integer getAreaID() {
        return getPAObjectID();
    }

    public void add() throws SQLException {

        if (getPAObjectID() == null) {
            PaoDao paoDao = DaoFactory.getPaoDao();
            setCapControlPAOID(paoDao.getNextPaoId());
        }

        super.add();

        getCapControlArea().add();

        for (int i = 0; i < getChildList().size(); i++) {
            getChildList().get(i).add();
        }
        SeasonScheduleDao ssDao = DaoFactory.getSeasonSchedule();
        ssDao.saveDefaultSeasonStrategyAssigment(getCapControlPAOID());

    }

    public com.cannontech.database.db.capcontrol.CapControlArea getCapControlArea() {
        if (capControlArea == null) {
            capControlArea = new com.cannontech.database.db.capcontrol.CapControlArea();
        }
        return capControlArea;
    }

    public ArrayList<CCSubAreaAssignment> getChildList() {
        if (areaSubs == null) {
            areaSubs = new ArrayList<CCSubAreaAssignment>();
        }
        return areaSubs;
    }

    public void setCapControlPAOID(Integer paoID) {
        super.setPAObjectID(paoID);
        getCapControlArea().setAreaID(paoID);

        for (int i = 0; i < getChildList().size(); i++)
            getChildList().get(i).setAreaID(paoID);

    }

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

    public void update() throws java.sql.SQLException {
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

    @SuppressWarnings("unchecked")
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
        return (Integer) yukonTemplate.queryForObject(builder.toString(), Integer.class);
    }

}
