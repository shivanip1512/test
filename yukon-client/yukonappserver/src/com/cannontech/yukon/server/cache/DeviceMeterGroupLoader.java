package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;

public class DeviceMeterGroupLoader implements Runnable {
    private ArrayList<LiteDeviceMeterNumber> devMetNumList = null;
    private String databaseAlias = null;

    public DeviceMeterGroupLoader(ArrayList<LiteDeviceMeterNumber> deviceList, String alias) {
        this.devMetNumList = deviceList;
        this.databaseAlias = alias;
    }

    @Override
    public void run() {
        long timer = System.currentTimeMillis();
        String sqlString = "SELECT dmg.DeviceId, dmg.MeterNumber, pao.Type FROM DeviceMeterGroup dmg "
                + " JOIN YukonPaobject pao ON dmg.DeviceId = pao.PaobjectId " + " ORDER BY dmg.MeterNumber";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;

        try {
            conn = PoolManager.getInstance().getConnection(this.databaseAlias);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(sqlString);

            while (rset.next()) {
                int deviceID = rset.getInt(1);
                String meterNumber = rset.getString(2).trim();
                String paoTypeStr = rset.getString(3);
                PaoType paoType = PaoType.getForDbString(paoTypeStr);

                LiteDeviceMeterNumber liteDevMetNum = new LiteDeviceMeterNumber(deviceID, meterNumber, paoType);
                devMetNumList.add(liteDevMetNum);
            }
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, stmt, conn);

            CTILogger.info(((System.currentTimeMillis() - timer) * .001)
                + " Secs for DeviceMeterGroupLoader (" + devMetNumList.size() + " loaded)");
        }
    }
}
