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
import com.cannontech.database.db.capcontrol.CCSubstationSubBusList;

@SuppressWarnings("serial")
public class CapControlSubstation extends CapControlYukonPAOBase implements
        EditorPanel {

    private com.cannontech.database.db.capcontrol.CapControlSubstation CapControlSubstation;
    private ArrayList<CCSubstationSubBusList> substationSubBuses;

    public CapControlSubstation() {
        super();
        setPAOCategory(PAOGroups.STRING_CAT_CAPCONTROL);
        setPAOClass(PAOGroups.STRING_CAT_CAPCONTROL);
        getYukonPAObject().setType(PAOGroups.STRING_CAPCONTROL_SUBSTATION);
    }

    public void retrieve() throws SQLException {
        super.retrieve();

        getCapControlSubstation().retrieve();
        ArrayList<CCSubstationSubBusList> allSubstationSubBuses = CCSubstationSubBusList.getCCSubBusesOnSubstation(getCapControlPAOID(), getDbConnection());
        for (CCSubstationSubBusList assignment : allSubstationSubBuses) {
        	 substationSubBuses.add(assignment);
        }
    }

    public void delete() throws SQLException {

        // remove all the associations of Subs to this Area
        com.cannontech.database.db.capcontrol.CCSubstationSubBusList.deleteCCSubBusFromSubstationList(getSubstationID(),
                                                                             null,
                                                                             getDbConnection());

        // Delete from all dynamic objects
        delete("DynamicCCSubstation", "substationID", getSubstationID());

        // delete(Point.TABLE_NAME, Point.SETTER_COLUMNS[2],
        // getCapControlPAOID());

        getCapControlSubstation().delete();
        super.delete();
    }

    private Integer getSubstationID() {
        return getPAObjectID();
    }

    public void add() throws SQLException {

        if (getPAObjectID() == null) {
            PaoDao paoDao = DaoFactory.getPaoDao();
            setCapControlPAOID(paoDao.getNextPaoId());
        }

        super.add();

        getCapControlSubstation().add();

        for (int i = 0; i < getChildList().size(); i++) {
            ((com.cannontech.database.db.capcontrol.CCSubstationSubBusList) getChildList().get(i)).add();
        }

    }

    public com.cannontech.database.db.capcontrol.CapControlSubstation getCapControlSubstation() {
        if (CapControlSubstation == null) {
            CapControlSubstation = new com.cannontech.database.db.capcontrol.CapControlSubstation();
        }
        return CapControlSubstation;
    }

    public ArrayList getChildList() {
        if (substationSubBuses == null) {
        	substationSubBuses = new ArrayList<CCSubstationSubBusList>();
        }
        return substationSubBuses;
    }

    public void setCapControlPAOID(Integer paoID) {
        super.setPAObjectID(paoID);
        getCapControlSubstation().setSubstationID(paoID);

        for (int i = 0; i < getChildList().size(); i++)
            ((com.cannontech.database.db.capcontrol.CCSubstationSubBusList) getChildList().get(i)).setSubstationID(paoID);

    }

    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
        getCapControlSubstation().setDbConnection(conn);

        for (int i = 0; i < getChildList().size(); i++)
            ((com.cannontech.database.db.capcontrol.CCSubstationSubBusList) getChildList().get(i)).setDbConnection(conn);

    }

    public void setSubstationSubBuses(ArrayList substationSubBuses) {
        this.substationSubBuses = substationSubBuses;
    }

    public void setCapControlSubstation(
            com.cannontech.database.db.capcontrol.CapControlSubstation CapControlSubstation) {
        this.CapControlSubstation = CapControlSubstation;
    }

    public void update() throws java.sql.SQLException {
        super.update();

        getCapControlSubstation().update();

        CCSubstationSubBusList.deleteCCSubBusFromSubstationList(getCapControlPAOID(),
                                       null,
                                       getDbConnection());
        Connection connection = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
        for (int i = 0; i < getChildList().size(); i++) {
        	CCSubstationSubBusList substationSubBusList = ((CCSubstationSubBusList) getChildList().get(i));
        	substationSubBusList.setDbConnection(connection );
        	substationSubBusList.add();
        }
        connection.close();
    }
    public static List<Integer> getAllUnassignedSubstations () {
        SqlStatementBuilder allSubstations = new SqlStatementBuilder();
        allSubstations.append("select paobjectid from yukonpaobject where type like 'CCSUBSTATION' ");
        allSubstations.append("and ");
        allSubstations.append("paobjectid not in (select substationbusid from ccsubareaassignment)");
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        return yukonTemplate.queryForList(allSubstations.toString(), Integer.class);

    }
   /* public static CapControlSubstation getDefaultArea() {
        Connection connection = PoolManager.getInstance()
                                           .getConnection(CtiUtilities.getDatabaseAlias());
        CapControlSubstation noneArea = new CapControlSubstation();
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

/*    private static CapControlSubstation retrieveArea(Connection connection) {
        Integer substationID;
        SqlStatementBuilder builder = new SqlStatementBuilder();
        builder.append("select areaid from CapControlSubstation as c, yukonpaobject as y where");
        builder.append("c.areaid = y.paobjectid");
        builder.append("and");
        builder.append("y.paoname like '(none)'");
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        CapControlSubstation noneArea = new CapControlSubstation();
        substationID = yukonTemplate.queryForInt(builder.toString());
        noneArea.setCapControlPAOID(substationID);
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
        CapControlSubstation area = new CapControlSubstation();
        area.setPAOName("(none)");
        area.getCapControlSubstation().setStrategyID(0);
        area.setDbConnection(connection);
        try {
            area.add();
        } catch (SQLException e) {
            CTILogger.error(e);
        }
    }*/

    public static List<Integer> getAllSubstationIDs() {
        SqlStatementBuilder builder = new SqlStatementBuilder();
        builder.append("select substationid from CapControlSubstation");
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        return yukonTemplate.queryForList(builder.toString(), Integer.class);
    }
    
    public static Integer  getSubstationIdByName (String name) {
        SqlStatementBuilder builder = new SqlStatementBuilder();
        builder.append("select substationid from CapControlSubstation, yukonpaobject");
        builder.append("where substationid = paobjectid and paoname like "  + "'" +name+ "'");
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        return (Integer) yukonTemplate.queryForObject(builder.toString(), Integer.class);
    }

}
