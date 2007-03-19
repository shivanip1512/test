package com.cannontech.database.db.capcontrol;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.db.DBPersistent;

@SuppressWarnings("serial")
public class CCSubAreaAssignment extends DBPersistent {

    private Integer areaID;
    private Integer substationBusID;
    private Integer displayOrder;

    public static final String TABLE_NAME = "CCSubAreaAssignment";
    public static final String SETTER_COLUMNS[] = { "DisplayOrder" };

    public static final String CONSTRAINT_COLUMNS[] = { "AreaID",
            "SubstationBusID" };
    // will need a cbc dao in the common project to have the Spring set this
    private static JdbcOperations yukonTemplate;

    public static void setYukonTemplate(JdbcOperations yukonTemplate) {
        CCSubAreaAssignment.yukonTemplate = yukonTemplate;
    }

    public void add() throws SQLException {
        add(TABLE_NAME, new Object[] { getAreaID(), getSubstationBusID(),
                getDisplayOrder() });
    }

    public void delete() throws SQLException {
        delete(TABLE_NAME, CONSTRAINT_COLUMNS, new Object[] { getAreaID(),
                getSubstationBusID() });
    }

    public void retrieve() throws SQLException {

    }

    public static List<CCSubAreaAssignment> getAllAreaSubs(Integer areaID) {

        SqlStatementBuilder allSubs = new SqlStatementBuilder();
        allSubs.append("SELECT AreaID, SubstationBusID, DisplayOrder FROM ");
        allSubs.append("CCSubAreaAssignment");
        allSubs.append("WHERE AreaID = ?");
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        return yukonTemplate.query(allSubs.toString(), new Integer[] {areaID}, new RowMapper () {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                CCSubAreaAssignment assign = new CCSubAreaAssignment();
                assign.setAreaID(rs.getInt(1));
                assign.setSubstationBusID(rs.getInt(2));
                assign.setDisplayOrder(rs.getInt(3));
                return assign;
            }
            
        });

    }

    public void update() throws SQLException {

        Object setValues[] = { getDisplayOrder() };

        Object constraintValues[] = { getAreaID(), getSubstationBusID() };

        update(TABLE_NAME,
               SETTER_COLUMNS,
               setValues,
               CONSTRAINT_COLUMNS,
               constraintValues);

    }

    public void setAreaID(Integer paoID) {
        areaID = paoID;
    }

    public Integer getAreaID() {
        return areaID;
    }

    public static void deleteSubs(Integer areaID, Integer subID,
            Connection dbConnection) {
        SqlStatementBuilder deleteStmt = new SqlStatementBuilder();
        deleteStmt.append("DELETE FROM ");
        deleteStmt.append(TABLE_NAME);
        if (subID == null) {

            deleteStmt.append("WHERE AreaId = ");
            deleteStmt.append(areaID);
        } else {
            deleteStmt.append("WHERE SubId = ");
            deleteStmt.append(subID);
        }
        yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        yukonTemplate.execute(deleteStmt.toString());
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Integer getSubstationBusID() {
        return substationBusID;
    }

    public void setSubstationBusID(Integer substationBusID) {
        this.substationBusID = substationBusID;
    }

}
