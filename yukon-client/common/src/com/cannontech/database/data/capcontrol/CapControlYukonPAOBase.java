package com.cannontech.database.data.capcontrol;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.HolidayScheduleDao;
import com.cannontech.core.dao.SeasonScheduleDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;

public abstract class CapControlYukonPAOBase extends com.cannontech.database.data.pao.YukonPAObject {
    public static final String DEFAULT_MAPLOCATION_ID = "0";
    private static final HolidayScheduleDao holidayScheduleDao = YukonSpringHook.getBean("holidayScheduleDao", HolidayScheduleDao.class);
    private static final SeasonScheduleDao seasonScheduleDao = YukonSpringHook.getBean("seasonScheduleDao", SeasonScheduleDao.class);

    public CapControlYukonPAOBase(PaoType paoType) {
        super(paoType);
    }

    public abstract List<? extends DBPersistent> getChildList();

    public abstract void setCapControlPAOID(Integer paoID);
    
    @Override
    public void add() throws SQLException {
        super.add();
    }
    
    @Override
    public void update() throws SQLException{
        super.update();
    }

    @Override
    public void delete() throws SQLException {
        int paoId = getCapControlPAOID();
        holidayScheduleDao.deleteStrategyAssigment(paoId);
        seasonScheduleDao.deleteStrategyAssigment(paoId);
        delete("DynamicCCOperationStatistics", "PAObjectID", paoId);
        super.delete();
    }
    
    public static String[] getAllUsedCapControlMapIDs() {
        return getAllUsedCapControlMapIDs( CapControlYukonPAOBase.DEFAULT_MAPLOCATION_ID );
    }

    public static String[] getAllUsedCapControlMapIDs(String currentMapLocID) {
        Connection conn = null;
        Statement pstmt = null;
        ResultSet rset = null;
        Map<String,String> mapIDs = new HashMap<String,String>(64);

        try {		
            conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
            
            if (conn == null) {
                throw new IllegalStateException("Error getting database connection.");
            }

            String[] tableNames = {
                    com.cannontech.database.db.capcontrol.CapBank.TABLE_NAME,
                    com.cannontech.database.db.capcontrol.CapControlFeeder.TABLE_NAME,
                    com.cannontech.database.db.capcontrol.CapControlSubstationBus.TABLE_NAME
            };

            pstmt = conn.createStatement();

            for (int i = 0; i < tableNames.length; i++) {
                rset = pstmt.executeQuery("select distinct maplocationid " +
                                          "from " + tableNames[i] +
                                          " order by maplocationid");

                String value = null;
                while (rset.next()) {
                    value = rset.getString(1);

                    if( !mapIDs.containsKey(value) && !value.equalsIgnoreCase(currentMapLocID) 
                            && !value.equalsIgnoreCase(CapControlYukonPAOBase.DEFAULT_MAPLOCATION_ID) )
                        mapIDs.put( value, value );
                }

            }

        } catch (SQLException e) {
            CTILogger.error( e.getMessage(), e );
        } finally {
            SqlUtils.close(rset, pstmt, conn );
        }

        String[] vals = new String[ mapIDs.size() ];
        vals = mapIDs.keySet().toArray(vals);
        java.util.Arrays.sort(vals);

        return vals;
    }
    
    public Integer getCapControlPAOID() {
        return getPAObjectID();
    }
    
    public Character getDisableFlag() {
        return getYukonPAObject().getDisableFlag();
    }
    
    public String getGeoAreaName() {
        return getYukonPAObject().getDescription();
    }
    
    public void setDisableFlag(Character flag) {
        getYukonPAObject().setDisableFlag(flag);
    }
    
    public void setGeoAreaName(String geoName) {
        getYukonPAObject().setDescription(geoName);
    }
    
    public void setName(String name) {
        getYukonPAObject().setPaoName(name);
    }
    
    @Override
    public String toString() {
        if (getPAOName() != null) return getPAOName();
        return null;
    }

}
