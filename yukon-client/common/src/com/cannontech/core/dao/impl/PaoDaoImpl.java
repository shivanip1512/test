package com.cannontech.core.dao.impl;

/**
 * Implementation of PaoDao Creation date: (7/1/2006 9:40:33 AM)
 * @author: alauinger
 */
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.service.impl.PaoLoader;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.YukonJdbcOperations;
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
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public final class PaoDaoImpl implements PaoDao {
    
	
	private static final String yukonPaoSql = "SELECT y.PAObjectID, y.Category, y.Type " 
        + "FROM yukonpaobject y ";
    
    private static final String litePaoSql = "SELECT y.PAObjectID, y.Category, y.PAOName, " 
        + "y.Type, y.PAOClass, y.Description, y.DisableFlag, d.PORTID, dcs.ADDRESS, dr.routeid " 
        + "FROM yukonpaobject y "
        + "left outer join devicedirectcommsettings d ON y.paobjectid = d.deviceid "
        + "left outer join devicecarriersettings DCS ON Y.PAOBJECTID = DCS.DEVICEID " 
        + "left outer join deviceroutes dr ON y.paobjectid = dr.deviceid ";
    
    private final ParameterizedRowMapper<PaoIdentifier> yukonPaoRowMapper = new YukonPaoRowMapper();
    private final RowMapper litePaoRowMapper = new LitePaoRowMapper();

    private JdbcOperations jdbcOps;
    private YukonJdbcOperations yukonJdbcOperations;
    private IDatabaseCache databaseCache;
    private NextValueHelper nextValueHelper;
    private AuthDao authDao;
	
    /*
     * (non-Javadoc)
     * @see com.cannontech.core.dao.PaoDao#getLiteYukonPAO(int)
     */
    public YukonPao getYukonPao(int paoId) {
        try {
            String sql = yukonPaoSql + "where y.paobjectid=?";
            YukonPao pao = yukonJdbcOperations.queryForObject(sql,
                                                              yukonPaoRowMapper, 
                                                              paoId);
            return pao;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A PAObject with id " + paoId + " cannot be found.");
        }
    }
    
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

    public LiteYukonPAObject findUnique(final String paoName, final String category, final String paoClass) {
        
        try {
            String sql = litePaoSql + "WHERE y.PAOName = ? AND y.Category = ? AND y.PAOClass = ? ";
            LiteYukonPAObject pao = 
                (LiteYukonPAObject) jdbcOps.queryForObject(
                                                           sql, 
                                                           new Object[]{paoName, category, paoClass}, 
                                                           litePaoRowMapper);
            return pao;
        } catch (IncorrectResultSizeDataAccessException e) {
        		//no objects returned is good
        		return null;
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
    
    @Override
    public Map<Integer, String> getYukonPAONames(Collection<Integer> ids) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("select paobjectid,paoname from yukonpaobject where paobjectid in (");
        sqlBuilder.append(ids);
        sqlBuilder.append(")");
        String sql = sqlBuilder.toString();
        
        final Map<Integer, String> resultMap = new HashMap<Integer, String>();
        
        jdbcOps.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Integer key = rs.getInt("paobjectid");
                String value = rs.getString("paoname");
                resultMap.put(key, value);
            }
        });
        
        return resultMap;
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
    
    
    
    
    
    
    
    
    
    
    @Override
    public PaoLoader<DisplayablePao> getDisplayablePaoLoader() {
        return new PaoLoader<DisplayablePao>() {
            @Override
            public Map<PaoIdentifier, DisplayablePao> getForPaos(Iterable<PaoIdentifier> identifiers) {
                Map<PaoIdentifier, String> namesForYukonDevices = getNamesForPaos(identifiers);
                Map<PaoIdentifier, DisplayablePao> result = Maps.newHashMapWithExpectedSize(namesForYukonDevices.size());

                for (Entry<PaoIdentifier, String> entry : namesForYukonDevices.entrySet()) {
                    DisplayableDevice displayableMeter = new DisplayableDevice(entry.getKey().getPaoIdentifier(), entry.getValue());
                    result.put(entry.getKey(), displayableMeter);
                }

                return result;
            }
        };
    }

    public Map<PaoIdentifier, String> getNamesForPaos(Iterable<PaoIdentifier> identifiers) {
        // build a lookup map based on the pao id (will also be used as a set of ids for the sql)
        final ImmutableMap<Integer, PaoIdentifier> deviceLookup = Maps.uniqueIndex(identifiers, new Function<PaoIdentifier, Integer>() {
            @Override
            public Integer apply(PaoIdentifier device) {
                return device.getPaoId();
            }
        });
        ChunkingSqlTemplate<Integer> template = new ChunkingSqlTemplate<Integer>(yukonJdbcOperations);

        // build the sql generator, selects name and id for a set of ids
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ypo.PAObjectID, ypo.PAOName FROM YukonPAObject ypo WHERE ypo.PAObjectID IN (").appendList(subList).append(")");
                return sql;
            }
        };
        
        // build mapper, builds a throw away structure that contains the id and name
        ParameterizedRowMapper<PaoIdAndName> rowMapper = new ParameterizedRowMapper<PaoIdAndName>() {
            @Override
            public PaoIdAndName mapRow(ResultSet rs, int rowNum)
            throws SQLException {
                PaoIdAndName result = new PaoIdAndName();
                result.paoId = rs.getInt("PAObjectID");
                result.name = rs.getString("PAOName");
                return result;
            }
        };

        // run the query with the generator, the set of ids passed in, and the mapper
        List<PaoIdAndName> names = template.query(sqlGenerator, deviceLookup.keySet(), rowMapper);

        // convert the resulting list into a lookup table
        ImmutableMap<PaoIdentifier, PaoIdAndName> intermediaryResult = Maps.uniqueIndex(names, new Function<PaoIdAndName, PaoIdentifier>() {
            public PaoIdentifier apply(PaoIdAndName pao) {
                return deviceLookup.get(pao.paoId);
            }
        });

        // transform the lookup table into the identifier to string map that will be returned
        Map<PaoIdentifier,String> result = Maps.transformValues(intermediaryResult, new Function<PaoIdAndName, String>() {
            public String apply(PaoIdAndName paoIdAndName) {
                return paoIdAndName.name;
            }
        });

        return result;
    }

    private static class PaoIdAndName {
        int paoId;
        String name;
    }
    
    
    
    @Override
    public List<PaoIdentifier> getPaoIdentifiersForPaoIds(List<Integer> paoIds) {
    	
    	ChunkingSqlTemplate<Integer> template = new ChunkingSqlTemplate<Integer>(yukonJdbcOperations);

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {
            	
            	SqlStatementBuilder sql = new SqlStatementBuilder();
            	sql.append("SELECT ypo.PAObjectID, ypo.Type");
            	sql.append("FROM YukonPAObject ypo");
            	sql.append("WHERE ypo.PAObjectID IN (");
            	sql.appendArgumentList(subList);
            	sql.append(")");
                return sql;
            }
        };
        
    	return template.query(sqlGenerator, paoIds, new YukonPaoRowMapper());
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
    
    @Autowired
    public void setYukonJdbcOperations(YukonJdbcOperations yukonJdbcOperations) {
        this.yukonJdbcOperations = yukonJdbcOperations;
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
