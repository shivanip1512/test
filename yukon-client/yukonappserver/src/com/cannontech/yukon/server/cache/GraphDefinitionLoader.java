package com.cannontech.yukon.server.cache;

import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteGraphDefinition;

public class GraphDefinitionLoader implements Runnable {
    private String databaseAlias = null;
    private List<LiteGraphDefinition> gDefList = null;

    public GraphDefinitionLoader(List<LiteGraphDefinition> gDefList, String databaseAlias) {
        this.gDefList = gDefList;
        this.databaseAlias = databaseAlias;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's <code>run</code> method to be called in
     * that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may take any action whatsoever.
     * 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        long timerStart = System.currentTimeMillis();

        String sqlString = "SELECT GraphDefinitionID,Name FROM GraphDefinition ORDER BY Name";

        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rset = null;
        try {
            conn = PoolManager.getInstance().getConnection(databaseAlias);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(sqlString);

            while (rset.next()) {
                int gDefID = rset.getInt(1);
                String name = rset.getString(2).trim();

                LiteGraphDefinition gDef = new LiteGraphDefinition(gDefID, name);
                gDefList.add(gDef);
            }
        } catch (java.sql.SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, stmt, conn);

            CTILogger.info(((System.currentTimeMillis() - timerStart) * .001)
                + " Secs for GraphDefinitionLoader (" + gDefList.size() + " loaded)");
        }
    }
}
