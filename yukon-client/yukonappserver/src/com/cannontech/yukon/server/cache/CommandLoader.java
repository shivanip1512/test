package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.db.command.Command;

public class CommandLoader implements Runnable {
    private List<LiteCommand> allCommands = null;
    // command id -> LiteCommand
    private Map<Integer, LiteCommand> allCommandsMap = null;
    private String databaseAlias = null;

    public CommandLoader(List<LiteCommand> allCommands, Map<Integer, LiteCommand> allCommandsMap, String databaseAlias) {
        this.allCommands = allCommands;
        this.allCommandsMap = allCommandsMap;
        this.databaseAlias = databaseAlias;
    }

    @Override
    public void run() {
        // temp code
        Date timerStart = new Date();
        Date timerStop = null;
        // temp code

        String sqlString = "SELECT COMMANDID, COMMAND, LABEL, CATEGORY " + " FROM " + Command.TABLE_NAME;

        Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;
        try {
            conn = PoolManager.getInstance().getConnection(databaseAlias);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(sqlString);

            while (rset.next()) {
                int cmdId = rset.getInt(1);
                String command = rset.getString(2);
                String label = rset.getString(3);
                String category = rset.getString(4);
                LiteCommand lc = new LiteCommand(cmdId, command, label, category);
                allCommands.add(lc);
                allCommandsMap.put(cmdId, lc);
            }
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, stmt, conn);
            // temp code
            timerStop = new Date();
            CTILogger.info((timerStop.getTime() - timerStart.getTime()) * .001
                + " Secs for Commands (" + allCommands.size() + " loaded)");
            // temp code
        }
    }
}
