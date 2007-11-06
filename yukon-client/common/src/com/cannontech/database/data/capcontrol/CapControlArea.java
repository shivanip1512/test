package com.cannontech.database.data.capcontrol;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.editor.EditorPanel;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.capcontrol.CCSubAreaAssignment;

@SuppressWarnings("serial")
public class CapControlArea extends CapControlYukonPAOBase implements
        EditorPanel {

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
            ((com.cannontech.database.db.capcontrol.CCSubAreaAssignment) getChildList().get(i)).add();
        }

    }

    public com.cannontech.database.db.capcontrol.CapControlArea getCapControlArea() {
        if (capControlArea == null) {
            capControlArea = new com.cannontech.database.db.capcontrol.CapControlArea();
        }
        return capControlArea;
    }

    public ArrayList getChildList() {
        if (areaSubs == null) {
            areaSubs = new ArrayList<CCSubAreaAssignment>();
        }
        return areaSubs;
    }

    public void setCapControlPAOID(Integer paoID) {
        super.setPAObjectID(paoID);
        getCapControlArea().setAreaID(paoID);

        for (int i = 0; i < getChildList().size(); i++)
            ((com.cannontech.database.db.capcontrol.CCSubAreaAssignment) getChildList().get(i)).setAreaID(paoID);

    }

    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
        getCapControlArea().setDbConnection(conn);

        for (int i = 0; i < getChildList().size(); i++)
            ((com.cannontech.database.db.capcontrol.CCSubAreaAssignment) getChildList().get(i)).setDbConnection(conn);

    }

    public void setAreaSubs(ArrayList areaSubs) {
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
            CCSubAreaAssignment subAreaAssignment = ((CCSubAreaAssignment) getChildList().get(i));
            subAreaAssignment.setDbConnection(connection );
            subAreaAssignment.add();
        }
        connection.close();
    }

   /* public static CapControlArea getDefaultArea() {
        Connection connection = PoolManager.getInstance()
                                           .getConnection(CtiUtilities.getDatabaseAlias());
        CapControlArea noneArea = new CapControlArea();
        synchronized (noneArea) {
            try {
                noneArea = retrieveArea(connection);
            } catch (EmptyResultDataAccessException erda) {
                addArea(connection);
                close(connection);
                getDefaultArea();

            }

        }
        close(connection);
        return noneArea;
    }*/

/*    private static void close(Connection connection) {
        if (connection == null) {
            try {
                connection.close();
            } catch (SQLException e) {
                CTILogger.error(e);
            }
        }
    }*/

/*    private static CapControlArea retrieveArea(Connection connection) {
        Integer areaID;
        SqlStatementBuilder builder = new SqlStatementBuilder();
        builder.append("select areaid from capcontrolarea as c, yukonpaobject as y where");
        builder.append("c.areaid = y.paobjectid");
        builder.append("and");
        builder.append("y.paoname like '(none)'");
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        CapControlArea noneArea = new CapControlArea();
        areaID = yukonTemplate.queryForInt(builder.toString());
        noneArea.setCapControlPAOID(areaID);
        noneArea.setDbConnection(connection);
        try {
            noneArea.retrieve();
            return noneArea;
        } catch (SQLException e) {
            noneArea = null;
            CTILogger.error(e);
        }
        return noneArea;
    }*/

/*    private static void addArea(Connection connection) {
        CapControlArea area = new CapControlArea();
        area.setPAOName("(none)");
        area.getCapControlArea().setStrategyID(0);
        area.setDbConnection(connection);
        try {
            area.add();
        } catch (SQLException e) {
            CTILogger.error(e);
        }
    }*/

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
