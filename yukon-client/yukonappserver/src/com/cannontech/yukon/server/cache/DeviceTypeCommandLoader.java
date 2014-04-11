package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.db.command.DeviceTypeCommand;

public class DeviceTypeCommandLoader implements Runnable {
    private List<LiteDeviceTypeCommand> allDeviceTypeCommands = null;
    private String databaseAlias = null;

    public DeviceTypeCommandLoader(List<LiteDeviceTypeCommand> allDeviceTypeCommands, String databaseAlias) {
        this.allDeviceTypeCommands = allDeviceTypeCommands;
        this.databaseAlias = databaseAlias;
    }

    @Override
    public void run() {
        // temp code
        Date timerStart = new Date();
        Date timerStop = null;
        // temp code

        String sqlString = "SELECT DEVICECOMMANDID, COMMANDID, DEVICETYPE, DISPLAYORDER, VISIBLEFLAG "
                + " FROM " + DeviceTypeCommand.TABLE_NAME + " ORDER BY DEVICETYPE, DISPLAYORDER ";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;
        try {
            conn = PoolManager.getInstance().getConnection(databaseAlias);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(sqlString);

            while (rset.next()) {
                int devCmdId = rset.getInt(1);
                int cmdId = rset.getInt(2);
                String devType = rset.getString(3);
                if (devType == null) {
                    System.out.println("HERRE ");
                }
                int order = rset.getInt(4);
                char visible = rset.getString(5).charAt(0);
                LiteDeviceTypeCommand ldtc = new LiteDeviceTypeCommand(devCmdId, cmdId, devType, order, visible);
                allDeviceTypeCommands.add(ldtc);
            }

        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, stmt, conn);
            // temp code
            timerStop = new Date();
            CTILogger.info((timerStop.getTime() - timerStart.getTime()) * .001
                + " Secs for DeviceTypeCommands (" + allDeviceTypeCommands.size() + " loaded)");
            // temp code
        }
    }
}
