package util;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.data.capcontrol.CapControlArea;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.capcontrol.CCSubAreaAssignment;
import com.cannontech.web.util.CBCDBUtil;

public class CBCTestUtil {

    public static Integer getAreaID() {
        SqlStatementBuilder areaID = new SqlStatementBuilder();
        areaID.append("SELECT MAX(paobjectID)FROM ");
        areaID.append("YukonPaObject");
        areaID.append("WHERE type like 'CCAREA'");
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        return yukonTemplate.queryForInt(areaID.toString());
    }

    public static Integer getStrategyID() {
        SqlStatementBuilder stratID = new SqlStatementBuilder();
        stratID.append("SELECT MAX(strategyID)FROM ");
        stratID.append("CapControlStrategy");
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        return yukonTemplate.queryForInt(stratID.toString());
    }

    public static Integer getValidSubId() {
        SqlStatementBuilder subID = new SqlStatementBuilder();
        subID.append("SELECT MAX(paobjectID)FROM ");
        subID.append("YukonPaObject");
        subID.append("WHERE type like 'CCSUBBUS'");
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        return yukonTemplate.queryForInt(subID.toString());
    }

    public static Connection getConnection() {
        return CBCDBUtil.getConnection();
    }



    public static void closeConnection(Connection connection) {
        CBCDBUtil.closeConnection(connection);
    }

   

    public static Integer getSubIDToUpdate() {
        List<LiteYukonPAObject> buses = DaoFactory.getPaoDao().getAllCapControlSubBuses();
        Integer newSubID = null;
        for (LiteYukonPAObject bus : buses) {
            if (bus.getLiteID() != CBCTestUtil.getValidSubId().intValue())
                newSubID = new Integer(bus.getLiteID());
            break;
        }
        return newSubID;
    }

    public static ArrayList<CCSubAreaAssignment> getFirstFourSubs(Integer areaID) {
        List<LiteYukonPAObject> buses = DaoFactory.getPaoDao()
                                                  .getAllCapControlSubBuses();
        ArrayList<CCSubAreaAssignment> subArea = new ArrayList<CCSubAreaAssignment>();
        for (LiteYukonPAObject bus : buses.subList(0, 4)) {
            CCSubAreaAssignment newAssignment = new CCSubAreaAssignment();
            newAssignment.setAreaID(areaID);
            newAssignment.setSubstationBusID(new Integer (bus.getLiteID()));
            newAssignment.setDisplayOrder(buses.indexOf(bus));
            subArea.add(newAssignment);
        }
        return subArea;
    }

    public static Integer getValidCapBankId() {
        SqlStatementBuilder stratID = new SqlStatementBuilder();
        stratID.append("SELECT MAX(deviceID)FROM ");
        stratID.append("CapBank");
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        return yukonTemplate.queryForInt(stratID.toString());
    }

    public static Integer getSubIdNotInArea(Integer capControlPAOID) {
        List<Integer> allAreas = CapControlArea.getAllAreaIDs ();
        allAreas.remove(capControlPAOID);
        Integer subID = null;
        if (allAreas.size() > 0) {
            Integer areaID = allAreas.get(allAreas.size()-1);
            List<CCSubAreaAssignment> allAreaSubs = CCSubAreaAssignment.getAllAreaSubStations(areaID);
            if (allAreaSubs.size() > 0) {
                subID = (allAreaSubs.get(allAreaSubs.size() - 1)).getSubstationBusID();
            }
        }
        
        return subID;
    }
}
