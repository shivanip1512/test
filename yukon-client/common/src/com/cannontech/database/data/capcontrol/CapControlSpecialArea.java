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
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.capcontrol.CCSubSpecialAreaAssignment;

@SuppressWarnings("serial")
public class CapControlSpecialArea extends CapControlYukonPAOBase implements EditorPanel {

    private com.cannontech.database.db.capcontrol.CapControlSpecialArea capControlSpecialArea;
    private ArrayList<CCSubSpecialAreaAssignment> specialAreaSubs;

    public CapControlSpecialArea() {
        super();
        setPAOCategory(PAOGroups.STRING_CAT_CAPCONTROL);
        setPAOClass(PAOGroups.STRING_CAT_CAPCONTROL);
        getYukonPAObject().setType(PAOGroups.STRING_CAPCONTROL_AREA);
    }

    public void retrieve() throws SQLException {
        super.retrieve();

        getCapControlSpecialArea().retrieve();
        List<CCSubSpecialAreaAssignment> allSpecialAreaSubs = CCSubSpecialAreaAssignment.getAllSpecialAreaSubs(getCapControlPAOID());
        for (CCSubSpecialAreaAssignment assignment : allSpecialAreaSubs) {
            specialAreaSubs.add(assignment);
        }
    }

    public void delete() throws SQLException {

        // remove all the associations of Subs to this Area
        com.cannontech.database.db.capcontrol.CCSubSpecialAreaAssignment.deleteSubs(getAreaID(), null, getDbConnection());
        getCapControlSpecialArea().delete();
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

        getCapControlSpecialArea().add();

        for (int i = 0; i < getChildList().size(); i++) {
            ((com.cannontech.database.db.capcontrol.CCSubSpecialAreaAssignment) getChildList().get(i)).add();
        }

    }

    public com.cannontech.database.db.capcontrol.CapControlSpecialArea getCapControlSpecialArea() {
        if (capControlSpecialArea == null) {
            capControlSpecialArea = new com.cannontech.database.db.capcontrol.CapControlSpecialArea();
        }
        return capControlSpecialArea;
    }

    public ArrayList getChildList() {
        if (specialAreaSubs == null) {
            specialAreaSubs = new ArrayList<CCSubSpecialAreaAssignment>();
        }
        return specialAreaSubs;
    }

    public void setCapControlPAOID(Integer paoID) {
        super.setPAObjectID(paoID);
        getCapControlSpecialArea().setAreaID(paoID);

        for (int i = 0; i < getChildList().size(); i++)
            ((com.cannontech.database.db.capcontrol.CCSubSpecialAreaAssignment) getChildList().get(i)).setAreaID(paoID);

    }

    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
        getCapControlSpecialArea().setDbConnection(conn);

        for (int i = 0; i < getChildList().size(); i++)
            ((com.cannontech.database.db.capcontrol.CCSubSpecialAreaAssignment) getChildList().get(i)).setDbConnection(conn);

    }

    public void setSpecialAreaSubs(ArrayList specialAreaSubs) {
        this.specialAreaSubs = specialAreaSubs;
    }

    public void setCapControlSpecialArea(
            com.cannontech.database.db.capcontrol.CapControlSpecialArea capControlSpecialArea) {
        this.capControlSpecialArea = capControlSpecialArea;
    }

    public void update() throws java.sql.SQLException {
        super.update();

        getCapControlSpecialArea().update();

        CCSubSpecialAreaAssignment.deleteSubs(getCapControlPAOID(), null, getDbConnection());
        Connection connection = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
        for (int i = 0; i < getChildList().size(); i++) {
            CCSubSpecialAreaAssignment subSpecialAreaAssignment = ((CCSubSpecialAreaAssignment) getChildList().get(i));
            subSpecialAreaAssignment.setDbConnection(connection );
            subSpecialAreaAssignment.add();
        }
        connection.close();
    }

    public static List<Integer> getAllSpecialAreaIDs() {
        SqlStatementBuilder builder = new SqlStatementBuilder();
        builder.append("select areaid from capcontrolspecialarea");
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        return yukonTemplate.queryForList(builder.toString(), Integer.class);
    }
    
    public static Integer  getSpecialAreaIdByName (String name) {
        SqlStatementBuilder builder = new SqlStatementBuilder();
        builder.append("select areaid from capcontrolspecialarea, yukonpaobject");
        builder.append("where areaid = paobjectid and paoname like "  + "'" +name+ "'");
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        return (Integer) yukonTemplate.queryForObject(builder.toString(), Integer.class);
    }

}
