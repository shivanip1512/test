package com.cannontech.core.dao.impl;

/**
 * Implementation of PaoDao Creation date: (7/1/2006 9:40:33 AM)
 * @author: alauinger
 */
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
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
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.device.Device;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.database.db.device.DeviceDirectCommSettings;
import com.cannontech.database.db.device.DeviceRoutes;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
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
    private final ParameterizedRowMapper<LiteYukonPAObject> litePaoRowMapper = new LitePaoRowMapper();

    private JdbcOperations jdbcOps;
    private YukonJdbcOperations yukonJdbcOperations;
    private YukonJdbcTemplate yukonJdbcTemplate;
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
            SqlStatementBuilder sqlBuilder = new SqlStatementBuilder(litePaoSql);
            sqlBuilder.append("WHERE UPPER(y.PAOName) = UPPER(?) ");
            sqlBuilder.append("AND y.Category = ? "); 
            sqlBuilder.append("AND y.PAOClass = ? ");
            sqlBuilder.append("AND y.Type = ? ");
            String stringCategory = PAOGroups.getCategory(category);
            String stringClass = PAOGroups.getPAOClass(category, paoClass);
            String stringType = PAOGroups.getPAOTypeString(type);
            
            LiteYukonPAObject pao = 
                (LiteYukonPAObject) jdbcOps.queryForObject(sqlBuilder.getSql(), 
                                                           new Object[]{deviceName, stringCategory, stringClass, stringType}, 
                                                           litePaoRowMapper);
            return pao;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A PAObject with deviceName '" + deviceName + "' cannot be found.");
        }
    }

    @Override
    @Deprecated /* Use findUnique(String paoName, PaoCategory category, PaoClass paoClass) */
    public LiteYukonPAObject findUnique(final String paoName, final String category, final String paoClass) {
        return findUnique(paoName, PaoCategory.getForDbString(category), PaoClass.getForDbString(paoClass));
    }
    
    @Override
    public LiteYukonPAObject findUnique(String paoName, PaoCategory category, PaoClass paoClass) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder(litePaoSql);
            sql.append("WHERE UPPER(y.PAOName) = UPPER(").appendArgument(paoName).append(")");
            sql.append(  "AND y.Category").eq(category);
            sql.append(  "AND y.PAOClass").eq(paoClass);
            LiteYukonPAObject pao = yukonJdbcTemplate.queryForObject(sql, litePaoRowMapper);
            return pao;
        } catch (IncorrectResultSizeDataAccessException e) {
                //no objects returned is good
                return null;
        }
    }
    
    @Override
    public boolean isNameAvailable(String paoName, PaoType paoType) {
        return findUnique(paoName, paoType.getPaoCategory(), paoType.getPaoClass()) == null;
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
    public Map<Integer, String> getYukonPAONames(Iterable<Integer> ids) {
        
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(yukonJdbcTemplate);
        
        final Map<Integer, String> resultMap = new HashMap<Integer, String>();
        
        template.query(new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT paobjectid, paoname");
                sql.append("FROM yukonpaobject");
                sql.append("WHERE paobjectid").in(subList);
                
                return sql;
            }
        }, 
        ids, 
        new RowCallbackHandler() {
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
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT y.PAObjectID, y.Category, y.PAOName, y.Type, y.PAOClass, y.Description, y.DisableFlag,");
    	sql.append(  "null as PortId, null as Address, null as RouteId"); // needed for the mapper, always null for routes
    	sql.append("FROM  YukonPAObject y");
    	sql.append("WHERE Category").eq(PaoCategory.ROUTE);
    	sql.append("ORDER BY y.PAOName");

    	List<LiteYukonPAObject> routeList = yukonJdbcOperations.query(sql, new LitePaoRowMapper());

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
            List<LiteYukonPAObject> routes = databaseCache.getAllRoutes();
            java.util.Collections.sort(routes,
                                       com.cannontech.database.data.lite.LiteComparators.liteStringComparator);

            for (LiteYukonPAObject litePao : routes) {
                for (int j = 0; j < routeTypes.length; j++)
                    if (litePao.getPaoType().getDeviceTypeId() != routeTypes[j]) {
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
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        if (partialMatch) {
            sqlBuilder.append("SELECT COUNT(*) ");
            sqlBuilder.append("FROM YukonPAObject ");
            sqlBuilder.append("WHERE UPPER(PAOName) LIKE UPPER('" + name + "%')");
        } else {
            sqlBuilder.append("SELECT COUNT(*) ");
            sqlBuilder.append("FROM YukonPAObject ");
            sqlBuilder.append("WHERE UPPER(PAOName) = UPPER('" + name + "')");
        }

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        return (Integer) jdbcOps.queryForObject(sqlBuilder.getSql(), Integer.class);
    }

    public List<LiteYukonPAObject> getLiteYukonPaoByName(String name,
            boolean partialMatch) {

        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder(litePaoSql);
        
        if (partialMatch) {
            sqlBuilder.append("WHERE UPPER(y.PAOName) LIKE UPPER(?) ");
        } else {
            sqlBuilder.append("WHERE UPPER(y.PAOName) = UPPER(?) ");
        }
        sqlBuilder.append(" ORDER BY y.Category, y.PAOClass, y.PAOName ");

        if (partialMatch) {
            name += "%";
        }

        List<LiteYukonPAObject> paos = jdbcOps.query(sqlBuilder.getSql(),
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
    
    public List<PaoIdentifier> getPaosByAddressRange(int startAddress, int endAddress) {

        try {
        	SqlStatementBuilder sql = new SqlStatementBuilder();
        	sql.append("SELECT pao.PAObjectID, pao.Type");
        	sql.append("FROM YukonPAObject pao");
        	sql.append(  "JOIN DeviceCarrierSettings DCS ON pao.PAOBJECTID = DCS.DEVICEID");
        	sql.append("WHERE address").gte(startAddress);
        	sql.append(  "and address").lte(endAddress);
        	sql.append(  "and pao.PAObjectID").neq(Device.SYSTEM_DEVICE_ID);
        	sql.append("ORDER BY pao.Category, pao.PAOClass, pao.PAOName");
            
           List<PaoIdentifier> result = yukonJdbcOperations.query(sql, new YukonPaoRowMapper());
           
           return result;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("No Paos found in (carrier) address range(" + startAddress + " - " + endAddress + ")");
        }
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
    	
    	ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(yukonJdbcOperations);
		
		SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
		    public SqlFragmentSource generate(List<Integer> subList) {
		    	SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ypo.PAObjectID, ypo.PAOName FROM YukonPAObject ypo WHERE ypo.PAObjectID").in(subList);
                return sql;
		    }
		};
        
        Function<PaoIdentifier, Integer> inputTypeToSqlGeneratorTypeMapper = new Function<PaoIdentifier, Integer>() {
			public Integer apply(PaoIdentifier from) {
				return from.getPaoId();
			}
		};

        ParameterizedRowMapper<Entry<Integer, String>> rowMapper = new ParameterizedRowMapper<Entry<Integer, String>>() {
            public Entry<Integer, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
                return Maps.immutableEntry(rs.getInt("PAObjectID"), rs.getString("PAOName"));
            }
        };

        return template.mappedQuery(sqlGenerator, identifiers, rowMapper, inputTypeToSqlGeneratorTypeMapper);
    }

    @Override
    public List<PaoIdentifier> getPaoIdentifiersForPaoIds(List<Integer> paoIds) {
    	
    	ChunkingSqlTemplate template = new ChunkingSqlTemplate(yukonJdbcOperations);

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
    
    @Override
    public PaoIdentifier getPaoIdentifierForPaoId(Integer paoId) {
        List<PaoIdentifier> listOfOne = getPaoIdentifiersForPaoIds(Lists.newArrayList(paoId));
        return listOfOne.get(0);
    }
    
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
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
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
        
    	Integer result = null;
		try {
			SqlStatementBuilder sql = new SqlStatementBuilder();
			sql.append("SELECT y.PAObjectID");
			sql.append("FROM  YukonPAObject y");
			sql.append("WHERE Category").eq(PaoCategory.ROUTE);
			sql.append(  "and upper(y.PAOName)").eq(routeName.toUpperCase());

			result = yukonJdbcOperations.queryForInt(sql);
		} catch (EmptyResultDataAccessException e) {
			// return default
		}

    	return result;
    }
}
