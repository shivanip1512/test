package com.cannontech.web.loadcontrol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.LMDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMScenarioWrapper;
import com.cannontech.spring.YukonSpringHook;

/**
 * Maintains information from the load control server and makes
 * it available in a form useful for loadcontrol.com
 *
 * Observes com.cannontech.loadcontrol.LoadControlClientConnection for
 * new control area messages.
 */
public class LoadcontrolCache implements Observer {

    private static int startupRefreshRate = 15 * 1000;
    private static int normalRefreshRate = 60 * 5 * 1000; // 5 minutes
    private ScheduledExecutor refreshTimer = YukonSpringHook.getGlobalExecutor();

    private final LoadControlClientConnection lcConn;

    private String dbAlias = "yukon";

    // key = (Integer) energy company id, value = (long[]) customer id
    private Map<Integer, long[]> energyCompanyCustomer = new HashMap<>();

    // key = (Integer) customer, value = long energy company id
    private Map<Integer, Integer> customerEnergyCompany = new HashMap<>();

    // key = (Integer) customer, value = Integer baseline pointid
    private Map<Integer, Integer> customerBaseLine = new HashMap<>();

    public LoadcontrolCache(final LoadControlClientConnection lcConn) {
        this.lcConn = lcConn;
        Runnable timerTask = new Runnable() {
            @Override
            public void run() {
                refresh();
            }

        };
        refreshTimer.scheduleWithFixedDelay(timerTask, startupRefreshRate, normalRefreshRate, TimeUnit.MILLISECONDS);
    }

    public LMProgramBase[] getDirectPrograms() {

        Set<LMProgramBase> programSet = new HashSet<>();
        LMControlArea[] areas = lcConn.getAllLMControlAreas();
        // Get the programs from the control areas to make sure they are up to date
        for (LMControlArea area : areas) {
            Vector<LMProgramBase> programVector = area.getLmProgramVector();
            programSet.addAll(programVector);
        }

        return programSet.toArray(new LMProgramBase[] {});
    }

    public LMControlArea getControlArea(Integer areaID) {
        return lcConn.getControlArea(areaID);
    }

    public LMProgramBase getProgram(Integer progID) {
        return lcConn.getProgram(progID);
    }

    /**
     * Only returns LMGroupBase clases, this excludes customers
     *
     * @return LMGroupBase
     */
    public LMGroupBase getGroup(Integer grpID) {
        return lcConn.getGroup(grpID);
    }

    public LMScenarioWrapper getScenario(Integer scenarioID) {
        LiteYukonPAObject[] scenarios = YukonSpringHook.getBean(LMDao.class).getAllLMScenarios();
        for (int i = 0; i < scenarios.length; i++) {
            if (scenarios[i].getYukonID() == scenarioID.intValue()) {
                LMScenarioWrapper scenario = new LMScenarioWrapper(scenarios[i]);
                return scenario;
            }
        }

        return null;
    }

    private synchronized void refresh() {
        CTILogger.debug("Refreshing customer-energycompany mappings");

        // Update energy company - customer mapping from db
        energyCompanyCustomer.clear();
        customerEnergyCompany.clear();

        long[] ids = com.cannontech.database.db.company.EnergyCompany.getAllEnergyCompanyIDs();

        for (int i = 0; i < ids.length; i++) {
            long[] custIDs = com.cannontech.database.db.web.EnergyCompanyCustomerList.getCustomerIDs(ids[i], dbAlias);

            energyCompanyCustomer.put(new Integer((int) ids[i]), custIDs);

            for (int j = 0; j < custIDs.length; j++) {
                customerEnergyCompany.put(new Integer((int) custIDs[j]), new Integer((int) ids[i]));
            }
        }

        CTILogger.debug("Refreshing customer baselines");

        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rset = null;

        try {
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT CustomerID,PointID FROM CustomerBaseLinePoint");

            while (rset.next()) {
                int customerID = rset.getInt(1);
                int pointID = rset.getInt(2);
                customerBaseLine.put(customerID, pointID);
            }
        } catch (java.sql.SQLException e) {
            CTILogger.debug("An error occured refreshing customerbaselines");
        } finally {
            try {
                if (rset != null) {
                    rset.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (java.sql.SQLException e2) {}
        }
        CTILogger.debug("Loaded " + customerBaseLine.size() + " customer baselines.");

        CTILogger.debug("Refreshing control areas");

        if (lcConn != null) {
            com.cannontech.loadcontrol.messages.LMCommand c = new com.cannontech.loadcontrol.messages.LMCommand();

            c.setCommand(com.cannontech.loadcontrol.messages.LMCommand.RETRIEVE_ALL_CONTROL_AREAS);
            lcConn.write(c);
            lcConn.addObserver(this);
        }

    }

    public void setDbAlias(String newDbAlias) {
        dbAlias = newDbAlias;
    }

    public String getDbAlias() {
        return dbAlias;
    }

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's <code>notifyObservers</code> method to have all the
     * object's
     * observers notified of the change.
     *
     * @param o the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code> method.
     */
    @Override
    public synchronized void update(Observable o, Object arg) {
        // Not used
    }
    /*
     * also, do we need to listen for a LCChangeEvent still and call the local update or something so the
     * browser knows to refresh?
     */
}