package com.cannontech.core.dao.impl;

/**
 * Implementation of PaoDao Creation date: (7/1/2006 9:40:33 AM)
 * @author: alauinger
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.database.db.device.DeviceDirectCommSettings;
import com.cannontech.database.db.device.DeviceRoutes;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.yukon.IDatabaseCache;

public final class PaoDaoImpl implements PaoDao {
    private static final String litePaoSql = "SELECT y.PAObjectID, y.Category, y.PAOName, " + "y.Type, y.PAOClass, y.Description, y.DisableFlag, d.PORTID, dcs.ADDRESS, dr.routeid " + "FROM yukonpaobject y left outer join devicedirectcommsettings d " + "on y.paobjectid = d.deviceid " + "left outer join devicecarriersettings DCS ON Y.PAOBJECTID = DCS.DEVICEID " + "left outer join deviceroutes dr on y.paobjectid = dr.deviceid ";

    private final RowMapper litePaoRowMapper = new LitePaoRowMapper();

    private JdbcOperations jdbcOps;
    private IDatabaseCache databaseCache;
    private NextValueHelper nextValueHelper;
    private AuthDao authDao;

    /*
     * (non-Javadoc)
     * @see com.cannontech.core.dao.PaoDao#getLiteYukonPAO(int)
     */
    public LiteYukonPAObject getLiteYukonPAO(int paoID) {
        try {
            String sql = litePaoSql + "where y.paobjectid=?";

            LiteYukonPAObject pao = (LiteYukonPAObject) jdbcOps.queryForObject(sql,
                                                                               new Object[] { paoID },
                                                                               litePaoRowMapper);
            return pao;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A PAObject with id " + paoID + " cannot be found.");
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.cannontech.core.dao.PaoDao#getLiteYukonPAO(Sring, int, int, int)
     */
    public LiteYukonPAObject getLiteYukonPAObject(final String deviceName, 
            final int category, final int paoClass, final int type) {
        
        try {
            String sql = litePaoSql + "WHERE y.PAOName = ? AND y.Category = ? AND y.PAOClass = ? AND y.Type = ?";
            String stringCategory = PAOGroups.getCategory(category);
            String stringClass = PAOGroups.getPAOClass(category, paoClass);
            String stringType = PAOGroups.getPAOTypeString(type);
            
            LiteYukonPAObject pao = 
                (LiteYukonPAObject) jdbcOps.queryForObject(
                                                           sql, 
                                                           new Object[]{deviceName, stringCategory, stringClass, stringType}, 
                                                           litePaoRowMapper);
            return pao;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A PAObject with deviceName '" + deviceName + "' cannot be found.");
        }
    }

    public List<LiteYukonPAObject> getLiteYukonPAObjectByType(int paoType) {
        String typeStr = PAOGroups.getPAOTypeString(paoType);
        String sql = litePaoSql;
        sql += "where UPPER(type)=? ";

        List<LiteYukonPAObject> paos = jdbcOps.query(sql,
                                                     new Object[] { StringUtils.upperCase(typeStr) },
                                                     litePaoRowMapper);
        return paos;
    }

    public List<LiteYukonPAObject> getLiteYukonPAObjectBy(Integer[] paoType,
            Integer[] paoCategory, Integer[] paoClass, Integer[] pointTypes,
            Integer[] uOfMId) {

        StringBuilder sql = new StringBuilder(litePaoSql);
        sql.append("left outer join point p on y.paobjectid=p.paobjectid " + "left outer join pointunit pu on p.pointid=pu.pointid ");

        String[] pointTypesStr = PointTypes.convertPointTypes(pointTypes);
        SqlUtil.buildInClause("where", "y", "type", paoType, sql);
        SqlUtil.buildInClause("and", "y", "category", paoCategory, sql);
        SqlUtil.buildInClause("and", "y", "paoclass", paoClass, sql);
        SqlUtil.buildInClause("and", "p", "pointtype", pointTypesStr, sql);
        SqlUtil.buildInClause("and", "pu", "uomid", uOfMId, sql);

        List<LiteYukonPAObject> paos = jdbcOps.query(sql.toString(),
                                                     litePaoRowMapper);
        return paos;
    }

    /*
     * (non-Javadoc)
     * @see com.cannontech.core.dao.PaoDao#getAllCapControlSubBuses()
     */
    public List getAllCapControlSubBuses() {
        List subBusList = null;
        synchronized (databaseCache) {
            subBusList = databaseCache.getAllCapControlSubBuses();
        }

        return subBusList;
    }
    
    public List<LiteYukonPAObject> getAllCapControlSubStations() {
        List<LiteYukonPAObject> subStationList = null;
        synchronized (databaseCache) {
            subStationList = databaseCache.getAllCapControlSubStations();
        }

        return subStationList;
    }

    /*
     * (non-Javadoc)
     * @see com.cannontech.core.dao.PaoDao#getMaxPAOid()
     */
    public int getMaxPAOid() {
        return jdbcOps.queryForInt("select max(paObjectId) from YukonPaObject");
    }

    public int getNextPaoId() {
        return nextValueHelper.getNextValue("YukonPaObject");
    }

    public int[] getNextPaoIds(int count) {
        int[] ids = new int[count];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = nextValueHelper.getNextValue("YukonPaObject");
        }
        return ids;
    }

    public String getYukonPAOName(int paoID) {
        try {
            String sql = "select paoname from yukonpaobject where paobjectid=?";
            String name = (String) jdbcOps.queryForObject(sql,
                                                          new Object[] { paoID },
                                                          String.class);
            return name;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A PAObject with id " + paoID + "cannot be found.");
        }
    }

    public LiteYukonPAObject[] getAllLiteRoutes() {
        // Get an instance of the databaseCache.
        List<LiteYukonPAObject> routeList;
        synchronized (databaseCache) {
            List<LiteYukonPAObject> routes = databaseCache.getAllRoutes();
            routeList = new ArrayList<LiteYukonPAObject>(routes);
            Collections.sort(routeList, LiteComparators.liteStringComparator);
        }

        LiteYukonPAObject retVal[] = new LiteYukonPAObject[routeList.size()];
        routeList.toArray(retVal);
        return retVal;
    }
    
    /*
     * (non-Javadoc)
     * @see com.cannontech.core.dao.PaoDao#getRoutesByType(int[])
     */
    public LiteYukonPAObject[] getRoutesByType(int[] routeTypes) {
        java.util.ArrayList routeList = new java.util.ArrayList(10);
        synchronized (databaseCache) {
            java.util.List routes = databaseCache.getAllRoutes();
            java.util.Collections.sort(routes,
                                       com.cannontech.database.data.lite.LiteComparators.liteStringComparator);

            for (int i = 0; i < routes.size(); i++) {
                LiteYukonPAObject litePao = (LiteYukonPAObject) routes.get(i);

                for (int j = 0; j < routeTypes.length; j++)
                    if (litePao.getType() != routeTypes[j]) {
                        routeList.add(litePao);
                        break;
                    }
            }
        }

        LiteYukonPAObject retVal[] = new LiteYukonPAObject[routeList.size()];
        routeList.toArray(retVal);

        return retVal;
    }

    /*
     * (non-Javadoc)
     * @see com.cannontech.core.dao.PaoDao#getAllUnusedCCPAOs(java.lang.Integer)
     */
    public LiteYukonPAObject[] getAllUnusedCCPAOs(Integer ignoreID) {
        synchronized (databaseCache) {

            List lPaos = databaseCache.getAllUnusedCCDevices();

            LiteYukonPAObject retVal[] = (LiteYukonPAObject[]) lPaos.toArray(new LiteYukonPAObject[lPaos.size()]);

            return retVal;
        }
    }

    public int countLiteYukonPaoByName(String name, boolean partialMatch)
            throws NotFoundException {
        String sql;
        if (partialMatch) {
            sql = "select count(*) from YukonPAObject where paoname like '" + name + "%'";
        } else {
            sql = "select count(*) from YukonPAObject where paoname='" + name + "'";
        }

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        return (Integer) jdbcOps.queryForObject(sql, Integer.class);
    }

    public List<LiteYukonPAObject> getLiteYukonPaoByName(String name,
            boolean partialMatch) {

        String sql = litePaoSql;

        if (partialMatch) {
            sql += "where y.PAOName like ? ";
        } else {
            sql += "where y.PAOName=? ";
        }

        sql += "ORDER BY y.Category, y.PAOClass, y.PAOName";

        if (partialMatch) {
            name += "%";
        }

        List<LiteYukonPAObject> paos = jdbcOps.query(sql,
                                                     new Object[] { name },
                                                     litePaoRowMapper);
        return paos;
    }

    public List<LiteYukonPAObject> getLiteYukonPaobjectsByMeterNumber(String meterNumber) {

        String sql = litePaoSql;
        sql += " LEFT OUTER JOIN deviceMeterGroup dmg ON y.paobjectid = dmg.deviceid ";
        sql += " WHERE upper(meternumber) = ?";

        List<LiteYukonPAObject> paos = jdbcOps.query(sql.toString(),
                                                     new Object[] { StringUtils.upperCase(meterNumber) },
                                                     litePaoRowMapper);

        return paos;

    }
    
    
    /**
     * Returns a list of LiteYukonPaobjects where DeviceCarrierSettings.address is address
     * @param address
     * @return ArrayList<LiteYukonPaobject>
     */
    @SuppressWarnings("unchecked")
    public List<LiteYukonPAObject> getLiteYukonPaobjectsByAddress(int address)
    {
        List<LiteYukonPAObject> liteYukonPaobects = new ArrayList(); 
        try {
            String sqlString = 
                "SELECT pao.PAObjectID, pao.Category, pao.PAOName, " +
                " pao.Type, pao.PAOClass, pao.Description, pao.DisableFlag, d.PORTID, dcs.ADDRESS, dr.routeid " +
                " FROM " + YukonPAObject.TABLE_NAME+ " pao " + 
                " left outer join " + DeviceDirectCommSettings.TABLE_NAME + " d on pao.paobjectid = d.deviceid " +
                " left outer join " + DeviceCarrierSettings.TABLE_NAME + " DCS ON pao.PAOBJECTID = DCS.DEVICEID " +        
                " left outer join " + DeviceRoutes.TABLE_NAME + " dr on pao.paobjectid = dr.deviceid " +
                " where address = ? " +
                " ORDER BY pao.Category, pao.PAOClass, pao.PAOName";
            
            liteYukonPaobects = jdbcOps.query(sqlString, new Object[] { new Integer(address)}, litePaoRowMapper);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("No liteYukonPaobjects found with (carrier) address(" + address + ")");
        }
        return liteYukonPaobects;
    }
    
    public List<LiteYukonPAObject> getLiteYukonPaobjectsByAddressRange(int startAddress,
            int endAddress) {

        List<LiteYukonPAObject> liteYukonPaobects = new ArrayList();
        try {
            String sqlString = 
                "SELECT pao.PAObjectID, pao.Category, pao.PAOName, " +
                " pao.Type, pao.PAOClass, pao.Description, pao.DisableFlag, d.PORTID, dcs.ADDRESS, dr.routeid " +
                " FROM " + YukonPAObject.TABLE_NAME+ " pao " + 
                " left outer join " + DeviceDirectCommSettings.TABLE_NAME + " d on pao.paobjectid = d.deviceid " +
                " left outer join " + DeviceCarrierSettings.TABLE_NAME + " DCS ON pao.PAOBJECTID = DCS.DEVICEID " +        
                " left outer join " + DeviceRoutes.TABLE_NAME + " dr on pao.paobjectid = dr.deviceid " +
                " where address >= ? AND address <= ?" +
                " ORDER BY pao.Category, pao.PAOClass, pao.PAOName";
            
            liteYukonPaobects = jdbcOps.query(sqlString,
                                              new Object[] { startAddress, endAddress },
                                              litePaoRowMapper);

        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("No liteYukonPaobjects found in (carrier) address range(" + startAddress + " - " + endAddress + ")");
        }

        return liteYukonPaobects;
    }
    
    public long getObjectCountByAddressRange(int startAddress, int endAddress) {

       long count = 0;
       
       try {
           String sqlString = 
               "SELECT COUNT(pao.PAObjectID) " +
               " FROM " + YukonPAObject.TABLE_NAME+ " pao " + 
               " left outer join " + DeviceDirectCommSettings.TABLE_NAME + " d on pao.paobjectid = d.deviceid " +
               " left outer join " + DeviceCarrierSettings.TABLE_NAME + " DCS ON pao.PAOBJECTID = DCS.DEVICEID " +        
               " left outer join " + DeviceRoutes.TABLE_NAME + " dr on pao.paobjectid = dr.deviceid " +
               " where address >= ? AND address <= ?" +
               " ORDER BY pao.Category, pao.PAOClass, pao.PAOName";
           
               count = jdbcOps.queryForLong(sqlString, new Object[] { startAddress, endAddress });

       } catch (IncorrectResultSizeDataAccessException e) {
           throw new NotFoundException("No liteYukonPaobjects found in (carrier) address range(" + startAddress + " - " + endAddress + ")");
       }

       return count;
   }
    
    @SuppressWarnings("unchecked")
    public List<LiteYukonPAObject> searchByName(final String name, final String paoClass) {
        String sql = litePaoSql + " WHERE y.PAOClass = ? AND UPPER(y.PAOName) LIKE ?";
        List<LiteYukonPAObject> paoList = this.jdbcOps.query(
                                                             sql,
                                                             new Object[]{paoClass, "%" + name.toUpperCase() + "%"},
                                                             litePaoRowMapper);
        return paoList;
    }

    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }

    public void setJdbcOps(JdbcOperations jdbcOps) {
        this.jdbcOps = jdbcOps;
    }

    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }

    public List getAllSubsForUser(LiteYukonUser user) {
        List subList = new ArrayList(10);
        List temp = getAllCapControlSubBuses();
        for (Iterator iter = temp.iterator(); iter.hasNext();) {
            LiteYukonPAObject element = (LiteYukonPAObject) iter.next();
            if (authDao.userHasAccessPAO(user, element.getLiteID())) {
                subList.add(element);
            }
        }
        return subList;
    }
    
    public Integer getRouteIdForRouteName(String routeName) {
        
        Integer routeId = null;
        LiteYukonPAObject[] routes = getAllLiteRoutes();
        for (LiteYukonPAObject route : routes) {
            if (route.getPaoName().equals(routeName)) {
                routeId = route.getLiteID();
            }
        }
        
        return routeId;
    }
    
    public String getRouteNameForRouteId(int routeId) {
        
        String routeName = "";
        LiteYukonPAObject[] routes = getAllLiteRoutes();
        for (LiteYukonPAObject route : routes) {
            if (route.getLiteID() == routeId) {
                routeName = route.getPaoName();
            }
        }
        
        return routeName;
    }
}
