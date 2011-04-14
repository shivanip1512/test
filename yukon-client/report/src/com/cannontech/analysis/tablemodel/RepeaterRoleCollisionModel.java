package com.cannontech.analysis.tablemodel;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.device.RepeaterRoleCollisionData;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.data.route.RouteUsageHelper;
import com.cannontech.database.db.DBPersistent;

public class RepeaterRoleCollisionModel extends ReportModelBase {
    
    /** Number of columns */
    protected final int NUMBER_COLUMNS = 4;
    
    /** Enum values for column representation */
    public final static int CCU_NAME_COLUMN = 0;
    public final static int ROUTE_NAME_COLUMN = 1;
    public final static int FIXEDBIT_COLUMN = 2;
    public final static int VARIABLEBIT_COLUMN = 3;
    
    /** String values for column representation */
    public final static String CCU_NAME_STRING = "CCU Name";
    public final static String ROUTE_NAME_STRING = "Route Name";
    public final static String FIXEDBIT_STRING = "Fixed Bit";
    public final static String VARIABLEBIT_STRING = "Variable Bit";
    
    /** A string for the title of the data */
    private static String title = "Repeater Role Collision Report";

    /**
     * Default Constructor
     */
    public RepeaterRoleCollisionModel()
    {
        super();
    }
    
    public void collectData() {
        setData(null);
        
        RouteUsageHelper routeMaster = new RouteUsageHelper();
        for(int i = 0; i < 32; i++) {
            for(int j = 0; j < 7; j++) {
                Vector dups = routeMaster.findDuplicatesForBits(i,j);
                if(dups != null) {
                    Enumeration enummers = dups.elements();
                    while(enummers.hasMoreElements()) {
                        LiteYukonPAObject liteYuk = DaoFactory.getPaoDao().getLiteYukonPAO(((Integer)enummers.nextElement()).intValue());
                        DBPersistent heavyRoute = LiteFactory.createDBPersistent(liteYuk);
                        java.sql.Connection conn = null;
                        try {
                            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
                            heavyRoute.setDbConnection(conn);
                            heavyRoute.retrieve();
                        } catch (SQLException e) {
                            CTILogger.error(e);
                        }
                        finally {
                        	SqlUtils.close(conn);
                        }
                        int deviceID = ((RouteBase)heavyRoute).getDeviceID().intValue();
                        String ccuName = DaoFactory.getPaoDao().getYukonPAOName(deviceID);
                        String routeName = liteYuk.getPaoName();
                        Integer fixedBit = new Integer(i);
                        Integer variableBit = new Integer(j);
                        
                        RepeaterRoleCollisionData data = new RepeaterRoleCollisionData(ccuName, routeName, fixedBit, variableBit);
                        getData().add(data);
                        if(!enummers.hasMoreElements()) {
                            // add a blank row
                            RepeaterRoleCollisionData blankRow = new RepeaterRoleCollisionData("","",null,null);
                            getData().add(blankRow);
                        }
                    }
                }
            }
        }
        
        CTILogger.info("Report Records Collected from Database: " + getData().size());
        return;
    }

    public Object getAttribute(int columnIndex, Object o) {
        if ( o instanceof RepeaterRoleCollisionData)
        {
            RepeaterRoleCollisionData data = ((RepeaterRoleCollisionData)o);
            
            switch( columnIndex)
            {
                case CCU_NAME_COLUMN:
                    return data.getCCUName();
    
                case ROUTE_NAME_COLUMN:
                    return data.getRouteName();
                    
                case FIXEDBIT_COLUMN:
                    return data.getFixedBit();
                    
                case VARIABLEBIT_COLUMN:
                    return data.getVariableBit();
            }
        }
        return null;
    }

    public String[] getColumnNames() {
        if( columnNames == null)
        {
            columnNames = new String[]{
                    CCU_NAME_STRING,
                    ROUTE_NAME_STRING,
                    FIXEDBIT_STRING,
                    VARIABLEBIT_STRING
            };
        }
        return columnNames;
    }

    public ColumnProperties[] getColumnProperties() {
        if(columnProperties == null)
        {
            columnProperties = new ColumnProperties[]{
                new ColumnProperties(0, 1, 200, null),
                new ColumnProperties(200, 1, 200, null),
                new ColumnProperties(400, 1, 200, null),
                new ColumnProperties(600, 1, 200, null),
            };
        }
        return columnProperties;
    }

    public Class[] getColumnTypes() {
        if( columnTypes == null)
        {
            columnTypes = new Class[]{
                String.class,
                String.class,
                String.class,
                String.class,
            };
        }
        return columnTypes;
    }

    public String getTitleString() {
        return title ;
    }
    
    @Override
    public boolean useStartDate() {
        return false;
    }
    
    @Override
    public boolean useStopDate() {
        return false;
    }
}
