package com.cannontech.web.test.capcontrolarea;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.capcontrol.CapControlStrategy;

public class Util {

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
        Connection connection = PoolManager.getInstance()
                                           .getConnection(CtiUtilities.getDatabaseAlias());
        return connection;
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {}
        }
    }

    public static Integer getSubIDToUpdate() {
        List<LiteYukonPAObject> buses = DaoFactory.getPaoDao()
                                                  .getAllCapControlSubBuses();
        Integer newSubID = null;
        for (LiteYukonPAObject bus : buses) {
            if (bus.getLiteID() != Util.getValidSubId().intValue())
                newSubID = new Integer(bus.getLiteID());
            break;
        }
        return newSubID;
    }

    public static Integer getStratIDToUpdate() {
        CapControlStrategy[] allCBCStrategies = CapControlStrategy.getAllCBCStrategies();
        Integer newStratID = null;
        for (int i = 0; i < allCBCStrategies.length; i++) {
            CapControlStrategy strategy = allCBCStrategies[i];
            if (!strategy.getStrategyID().equals(getStrategyID())) {
                newStratID = strategy.getStrategyID();
                break;
            }

        }
        return newStratID;
    }

}
