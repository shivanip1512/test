package com.cannontech.database.db.capcontrol;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.db.DBPersistent;

@SuppressWarnings("serial")
public class CCSubSpecialAreaAssignment extends DBPersistent {

    private Integer areaID;
    private Integer substationBusID;
    private Integer displayOrder;

    public static final String TABLE_NAME = "CCSubSpecialAreaAssignment";
    public static final String SETTER_COLUMNS[] = { "DisplayOrder" };

    public static final String CONSTRAINT_COLUMNS[] = { "AreaID", "SubstationBusID" };
    // will need a cbc dao in the common project to have the Spring set this
    private static JdbcOperations yukonTemplate;

    public static void setYukonTemplate(JdbcOperations yukonTemplate) {
        CCSubSpecialAreaAssignment.yukonTemplate = yukonTemplate;
    }

    public void add() throws SQLException {
        add(TABLE_NAME, new Object[] { getAreaID(), getSubstationBusID(), getDisplayOrder() });
    }

    public void delete() throws SQLException {
        delete(TABLE_NAME, CONSTRAINT_COLUMNS, new Object[] { getAreaID(), getSubstationBusID() });
    }

    public void retrieve() throws SQLException {

    }

    @SuppressWarnings("unchecked")
    public static List<CCSubSpecialAreaAssignment> getAllSpecialAreaSubs(Integer areaID) {

        SqlStatementBuilder allSubs = new SqlStatementBuilder();
        allSubs.append("SELECT AreaID, SubstationBusID, DisplayOrder FROM ");
        allSubs.append("CCSubSpecialAreaAssignment");
        allSubs.append("WHERE AreaID = ?");
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        List<CCSubSpecialAreaAssignment> list;
        list = yukonTemplate.query(allSubs.toString(), new Integer[] { areaID }, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            CCSubSpecialAreaAssignment assign = new CCSubSpecialAreaAssignment();
            assign.setAreaID(rs.getInt(1));
            assign.setSubstationBusID(rs.getInt(2));
            assign.setDisplayOrder(rs.getInt(3));
            return assign;
           }
        });
        Collections.sort(list, new Comparator<CCSubSpecialAreaAssignment>() {
            public int compare(CCSubSpecialAreaAssignment o1, CCSubSpecialAreaAssignment o2) {
                try {
                    Integer strA = o1.getDisplayOrder();
                    Integer strB = o2.getDisplayOrder();

                    return strA.compareTo(strB);
                } catch (Exception e) {
                    CTILogger.error("Something went wrong with sorting, ignoring sorting rules", e);
                    return 0;
                }

            }
    	}
    );

        return list;
    }

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

    public static List<Integer> getAreaIdsForSub(Integer subID) {
        SqlStatementBuilder allSubs = new SqlStatementBuilder();
        allSubs.append("SELECT AreaID FROM");
        allSubs.append("CCSubSpecialAreaAssignment");
        allSubs.append("WHERE SubstationBusID = ?");
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        List<Integer> areaID;
        try {
            areaID = yukonTemplate.queryForList(allSubs.toString(), new Integer[] { subID }, new int[]{Types.INTEGER}, Integer.class );
        } catch (EmptyResultDataAccessException erda) {
            areaID = null;
        }
        return areaID;
    }

    public static ArrayList<Integer> getAsIntegerList( List<CCSubSpecialAreaAssignment> allSpecialAreaSubs) {
        ArrayList<Integer> returnList = new ArrayList<Integer>();
        for (CCSubSpecialAreaAssignment assgn : allSpecialAreaSubs) {
            returnList.add(assgn.getSubstationBusID());
        }
        return returnList;
    }

    public static ArrayList<CCSubSpecialAreaAssignment> asList(List<Integer> newIntList, Integer areaID) {
        ArrayList<CCSubSpecialAreaAssignment> returnList = new ArrayList<CCSubSpecialAreaAssignment>();
        for (Integer subID : newIntList) {
            CCSubSpecialAreaAssignment temp = new CCSubSpecialAreaAssignment();
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
        yukonTemplate.update(deleteStmt.toString(), new Integer[] {areaID});
    }
    
    public static void deleteSubstation(Integer substationId) {
        SqlStatementBuilder deleteStmt = new SqlStatementBuilder();
        deleteStmt.append("DELETE FROM ");
        deleteStmt.append(TABLE_NAME);
        deleteStmt.append(" WHERE SubstationBusID = ? ");
        yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        yukonTemplate.update(deleteStmt.toString(), new Integer[] {substationId});
    }
}
