package com.cannontech.database.db.capcontrol;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.db.DBPersistent;

public class CCSubAreaAssignment extends DBPersistent{
    private Integer areaID;
    private Integer substationBusID;
    private Integer displayOrder;
    public static final String TABLE_NAME = "CCSubAreaAssignment";
    public static final String SETTER_COLUMNS[] = { "DisplayOrder" };
    public static final String CONSTRAINT_COLUMNS[] = { "AreaID", "SubstationBusID" };
    // will need a cbc dao in the common project to have the Spring set this
    private static JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
    
    public CCSubAreaAssignment() {
        super();
    }
    
    public CCSubAreaAssignment(Integer areaId, Integer subId, Integer displayOrder) {
        super();
        this.areaID = areaId; 
        this.substationBusID = subId;
        this.displayOrder = displayOrder;
    }
    
    public static void setYukonTemplate(JdbcOperations yukonTemplate) {
        CCSubAreaAssignment.yukonTemplate = yukonTemplate;
    }

    @Override
    public void add() throws SQLException {
        add(TABLE_NAME, new Object[] { getAreaID(), getSubstationBusID(), getDisplayOrder() });
    }

    @Override
    public void delete() throws SQLException {
        delete(TABLE_NAME, CONSTRAINT_COLUMNS, new Object[] { getAreaID(), getSubstationBusID() });
    }

    @Override
    public void retrieve() throws SQLException {
    }

    public static List<CCSubAreaAssignment> getAllAreaSubStations(Integer areaID) {
        SqlStatementBuilder allSubs = new SqlStatementBuilder();
        allSubs.append("SELECT AreaID, SubstationBusID, DisplayOrder FROM ");
        allSubs.append("CCSubAreaAssignment");
        allSubs.append("WHERE AreaID = ? ORDER BY DisplayOrder");
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        List<CCSubAreaAssignment> subStationsForAreaList =
            yukonTemplate.query(allSubs.toString(), new Integer[] { areaID }, new RowMapper<CCSubAreaAssignment>() {
                @Override
                public CCSubAreaAssignment mapRow(ResultSet rs, int rowNum) throws SQLException {
                    CCSubAreaAssignment assign = new CCSubAreaAssignment();
                    assign.setAreaID(rs.getInt(1));
                    assign.setSubstationBusID(rs.getInt(2));
                    assign.setDisplayOrder(rs.getInt(3));
                    return assign;
                }
            });
        return subStationsForAreaList;
    }

    @Override
    public void update() throws SQLException {
        Object setValues[] = { getDisplayOrder() };
        Object constraintValues[] = { getAreaID(), getSubstationBusID() };
        update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
    }

    public void setAreaID(Integer paoID) {
        areaID = paoID;
    }

    public Integer getAreaID() {
        return areaID;
    }

    public static void deleteSubs(Integer areaID, Integer subID, Connection dbConnection) {
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

    public static Integer getAreaIDForSubStation(Integer subID) {
        SqlStatementBuilder allSubs = new SqlStatementBuilder();
        allSubs.append("SELECT AreaID FROM");
        allSubs.append("CCSubAreaAssignment");
        allSubs.append("WHERE SubstationBusID = ?");
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        Integer areaID;
        try {
            areaID = yukonTemplate.queryForObject(allSubs.toString(), Integer.class, subID);
        } catch (EmptyResultDataAccessException erda) {
            areaID = -1;
        }
        return areaID;
    }
    
    public static ArrayList<Integer> getAsIntegerList( List<CCSubAreaAssignment> allAreaSubs) {
        ArrayList<Integer> returnList = new ArrayList<Integer>();
        for (CCSubAreaAssignment assgn : allAreaSubs) {
            returnList.add(assgn.getSubstationBusID());
        }
        return returnList;
    }

    public static ArrayList<CCSubAreaAssignment> asList(List<Integer> newIntList, Integer areaID) {
        ArrayList<CCSubAreaAssignment> returnList = new ArrayList<CCSubAreaAssignment>();
        for (Integer subID : newIntList) {
            CCSubAreaAssignment temp = new CCSubAreaAssignment();
            temp.setAreaID(areaID);
            temp.setSubstationBusID(subID);
            temp.setDisplayOrder(newIntList.indexOf(subID));
            returnList.add(temp);
        }
        return returnList;
    }

    public static void deleteSubs(ArrayList<Integer> idsToRemove, Integer areaID) {
        SqlStatementBuilder deleteStmt = new SqlStatementBuilder();
        deleteStmt.append("DELETE FROM ");
        deleteStmt.append(TABLE_NAME);
        deleteStmt.append(" WHERE SubstationBusID in (");
        deleteStmt.append(idsToRemove);
        deleteStmt.append(")");
        deleteStmt.append("AND AreaID = ?");
        yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        yukonTemplate.update(deleteStmt.toString(), areaID);
    }
    
    public static void deleteSubstation(Integer substationId) {
        SqlStatementBuilder deleteStmt = new SqlStatementBuilder();
        deleteStmt.append("DELETE FROM ");
        deleteStmt.append(TABLE_NAME);
        deleteStmt.append(" WHERE SubstationBusID = ? ");
        yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        yukonTemplate.update(deleteStmt.toString(), substationId);
    }
}
