package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.database.db.state.YukonImage;

public class YukonImageLoader implements Runnable {
    private ArrayList allStateImages = null;
    private String databaseAlias = null;

    public YukonImageLoader(ArrayList sImageArray, String alias) {
        this.allStateImages = sImageArray;
        this.databaseAlias = alias;
    }

    @Override
    public void run() {
        // temp code
        Date timerStart = null;
        Date timerStop = null;
        // temp code

        // temp code
        timerStart = new Date();
        // temp code

        // get all the customer contacts that are assigned to a customer
        String sqlString = "SELECT ImageID,ImageCategory,ImageName,ImageValue from "
                + YukonImage.TABLE_NAME + " where ImageID > " + YukonImage.NONE_IMAGE_ID;

        Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;
        try {
            conn = PoolManager.getInstance().getConnection(this.databaseAlias);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(sqlString);

            while (rset.next()) {
                int imgID = rset.getInt(1);
                String imgCat = rset.getString(2).trim();
                String imgName = rset.getString(3).trim();
                byte[] bStream = rset.getBytes(4);

                LiteYukonImage lsi = new LiteYukonImage(imgID, imgCat, imgName, bStream);

                allStateImages.add(lsi);
            }
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, stmt, conn);
            // temp code
            timerStop = new Date();
            CTILogger.info((timerStop.getTime() - timerStart.getTime()) * .001
                + " Secs for StateImageLoader (" + allStateImages.size() + " loaded)");
            // temp code
        }
    }
}
