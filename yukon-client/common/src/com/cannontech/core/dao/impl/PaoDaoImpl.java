package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.service.impl.PaoLoader;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.device.Device;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.database.db.device.DeviceDirectCommSettings;
import com.cannontech.database.db.device.DeviceRoutes;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;

public final class PaoDaoImpl implements PaoDao {
    
    private final static String litePaoSql = "SELECT y.PAObjectID, y.Category, y.PAOName, "
        + "y.Type, y.PAOClass, y.Description, y.DisableFlag, d.PORTID, dcs.ADDRESS, dr.routeid "
        + "FROM yukonpaobject y " + "LEFT OUTER JOIN devicedirectcommsettings d ON y.paobjectid = d.deviceid "
        + "LEFT OUTER JOIN devicecarriersettings DCS ON Y.PAOBJECTID = DCS.DEVICEID "
        + "LEFT OUTER JOIN deviceroutes dr ON y.paobjectid = dr.deviceid ";

    private final RowMapper<LiteYukonPAObject> litePaoRowMapper = new LitePaoRowMapper();

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private AuthDao authDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;

    @Override
    public YukonPao getYukonPao(int paoId) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT PAObjectID, Category, Type");
            sql.append("FROM YukonPAObject");
            sql.append("WHERE PAObjectID").eq(paoId);

            YukonPao pao = jdbcTemplate.queryForObject(sql, TypeRowMapper.PAO_IDENTIFIER);

            return pao;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A PAObject with id " + paoId + " cannot be found.");
        }
    }

    @Override
    public YukonPao findYukonPao(String paoName, PaoType paoType) {
        return findYukonPao(paoName, paoType.getPaoCategory(), paoType.getPaoClass());
    }

    @Override
    public YukonPao findYukonPao(String paoName, PaoCategory paoCategory, PaoClass paoClass) {
        try {
            return queryForPao(paoName, paoCategory, paoClass);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    };

    @Override
    public YukonPao getYukonPao(String paoName, PaoType paoType) throws NotFoundException {
        return getYukonPao(paoName, paoType.getPaoCategory(), paoType.getPaoClass());
    };

    @Override
    public YukonPao getYukonPao(String paoName, PaoCategory paoCategory, PaoClass paoClass) throws NotFoundException {
        try {
            return queryForPao(paoName, paoCategory, paoClass);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A PAObject named " + paoName + " cannot be found.");
        }
    };

    private PaoIdentifier queryForPao(String paoName, PaoCategory paoCategory, PaoClass paoClass) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectID, Type");
        sql.append("FROM YukonPAObject");
        sql.append("WHERE PaoName").eq(StringUtils.trim(paoName));
        sql.append("AND Category").eq(paoCategory);
        sql.append("AND PaoClass").eq(paoClass);

        PaoIdentifier paoIdentifier = jdbcTemplate.queryForObject(sql, TypeRowMapper.PAO_IDENTIFIER);

        return paoIdentifier;
    }

    @Override
    public LiteYukonPAObject getLiteYukonPAO(int paoID) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append(litePaoSql);
            sql.append("WHERE y.PAObjectId").eq(paoID);
            LiteYukonPAObject pao = jdbcTemplate.queryForObject(sql, litePaoRowMapper);
            return pao;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A PAObject with id " + paoID + " cannot be found.");
        }
    }

    @Override
    public Map<PaoIdentifier, LiteYukonPAObject> getLiteYukonPaosById(Iterable<PaoIdentifier> paos) {
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(jdbcTemplate);

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append(litePaoSql);
                sql.append("WHERE y.PAObjectId").in(subList);
                return sql;
            }
        };

        Function<PaoIdentifier, Integer> transform = new Function<PaoIdentifier, Integer>() {
            @Override
            public Integer apply(PaoIdentifier pao) {
                return pao.getPaoId();
            }
        };

        YukonRowMapper<Map.Entry<Integer, LiteYukonPAObject>> mappingRowMapper =
            new YukonRowMapper<Map.Entry<Integer, LiteYukonPAObject>>() {
                @Override
                public Entry<Integer, LiteYukonPAObject> mapRow(YukonResultSet rs) throws SQLException {
                    LiteYukonPAObject pao = litePaoRowMapper.mapRow(rs.getResultSet(), 0);
                    return Maps.immutableEntry(pao.getPaoIdentifier().getPaoId(), pao);
                }
            };

        return template.mappedQuery(sqlGenerator, paos, mappingRowMapper, transform);
    }
    
    @Override
    public List<LiteYukonPAObject> getLiteYukonPaos(Iterable<Integer> paoIds) {
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append(litePaoSql);
                sql.append("WHERE y.PAObjectId").in(subList);
                return sql;
            }
        };
        
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);
        
        return template.query(sqlGenerator, paoIds, litePaoRowMapper);
    }

    @Override
    public LiteYukonPAObject findUnique(String paoName, PaoType paoType) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder(litePaoSql);
            sql.append("WHERE UPPER(y.PAOName) = UPPER(").appendArgument(StringUtils.trim(paoName)).append(")");
            sql.append("AND y.Category").eq_k(paoType.getPaoCategory());
            sql.append("AND y.PAOClass").eq_k(paoType.getPaoClass());
            LiteYukonPAObject pao = jdbcTemplate.queryForObject(sql, litePaoRowMapper);
            return pao;
        } catch (IncorrectResultSizeDataAccessException e) {
            // no objects returned is good
            return null;
        }
    }

    @Override
    public boolean isNameAvailable(String paoName, PaoType paoType) {
        return findUnique(paoName, paoType) == null;
    }

    @Override
    public List<LiteYukonPAObject> getLiteYukonPAObjectByType(PaoType paoType) {
        SqlStatementBuilder sql = new SqlStatementBuilder(litePaoSql);
        sql.append("WHERE type").eq_k(paoType);

        List<LiteYukonPAObject> paos = jdbcTemplate.query(sql, litePaoRowMapper);
        return paos;
    }

    @Override
    public List<LiteYukonPAObject> getAllCapControlSubBuses() {
        synchronized (databaseCache) {
            List<LiteYukonPAObject> allCapControlSubBuses = databaseCache.getAllCapControlSubBuses();
            return allCapControlSubBuses;
        }
    }

    public List<LiteYukonPAObject> getAllCapControlSubStations() {
        List<LiteYukonPAObject> subStationList = null;
        synchronized (databaseCache) {
            subStationList = databaseCache.getAllCapControlSubStations();
        }

        return subStationList;
    }

    @Override
    public int getNextPaoId() {
        return nextValueHelper.getNextValue("YukonPaObject");
    }

    @Override
    public String getYukonPAOName(int paoId) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT PAOName");
            sql.append("FROM YukonPAObject");
            sql.append("WHERE PAObjectID").eq(paoId);
            String name = jdbcTemplate.queryForObject(sql, TypeRowMapper.STRING);
            return name;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A PAObject with id " + paoId + " cannot be found.");
        }
    }

    @Override
    public Map<Integer, String> getYukonPAONames(Iterable<Integer> ids) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);

        final Map<Integer, String> resultMap = new HashMap<Integer, String>();

        template.query(new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT paobjectid, paoname");
                sql.append("FROM yukonpaobject");
                sql.append("WHERE paobjectid").in(subList);

                return sql;
            }
        }, ids, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Integer key = rs.getInt("paobjectid");
                String value = rs.getString("paoname");
                resultMap.put(key, value);
            }
        });

        return resultMap;
    }

    @Override
    public LiteYukonPAObject[] getAllLiteRoutes() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT y.PAObjectID, y.Category, y.PAOName, y.Type, y.PAOClass, y.Description, y.DisableFlag,");
        sql.append("null as PortId, null as Address, null as RouteId"); // needed for the mapper, always null
                                                                        // for routes
        sql.append("FROM  YukonPAObject y");
        sql.append("WHERE Category").eq(PaoCategory.ROUTE);
        sql.append("ORDER BY y.PAOName");

        List<LiteYukonPAObject> routeList = jdbcTemplate.query(sql, new LitePaoRowMapper());

        LiteYukonPAObject retVal[] = new LiteYukonPAObject[routeList.size()];
        routeList.toArray(retVal);
        return retVal;
    }

    @Override
    public LiteYukonPAObject[] getRoutesByType(PaoType... routeTypes) {
        List<LiteYukonPAObject> routeList = new ArrayList<>(10);
        synchronized (databaseCache) {
            List<LiteYukonPAObject> routes = databaseCache.getAllRoutes();
            
            for (LiteYukonPAObject litePao : routes) {
                for (int j = 0; j < routeTypes.length; j++) {
                    if (litePao.getPaoType() != routeTypes[j]) {
                        routeList.add(litePao);
                        break;
                    }
                }
            }
        }

        LiteYukonPAObject retVal[] = new LiteYukonPAObject[routeList.size()];
        routeList.toArray(retVal);

        return retVal;
    }

    @Override
    public List<LiteYukonPAObject> getLiteYukonPaoByName(String name, boolean partialMatch) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder(litePaoSql);

        if (partialMatch) {
            sqlBuilder.append("WHERE UPPER(y.PAOName) LIKE UPPER(?) ");
        } else {
            sqlBuilder.append("WHERE UPPER(y.PAOName) = UPPER(?) ");
        }
        sqlBuilder.append(" order BY y.Category, y.PAOClass, y.PAOName ");

        if (partialMatch) {
            name += "%";
        }

        List<LiteYukonPAObject> paos = jdbcTemplate.query(sqlBuilder.getSql(), new Object[] { name }, litePaoRowMapper);
        return paos;
    }

    /**
     * Returns a list of LiteYukonPaobjects where DeviceCarrierSettings.address is address
     */
    @Override
    public List<LiteYukonPAObject> getLiteYukonPaobjectsByAddress(int address) {
        List<LiteYukonPAObject> liteYukonPaobects = new ArrayList<>();
        try {
            String sqlString =
                "SELECT pao.PAObjectID, pao.Category, pao.PAOName, "
                    + " pao.Type, pao.PAOClass, pao.Description, pao.DisableFlag, d.PORTID, dcs.ADDRESS, dr.routeid "
                    + " FROM " + YukonPAObject.TABLE_NAME + " pao " + " left outer join "
                    + DeviceDirectCommSettings.TABLE_NAME + " d ON pao.paobjectid = d.deviceid " + " LEFT OUTER JOIN "
                    + DeviceCarrierSettings.TABLE_NAME + " DCS ON pao.PAOBJECTID = DCS.DEVICEID " + " LEFT OUTER JOIN "
                    + DeviceRoutes.TABLE_NAME + " dr ON pao.paobjectid = dr.deviceid " + " WHERE address = ? "
                    + " ORDER BY pao.Category, pao.PAOClass, pao.PAOName";

            liteYukonPaobects = jdbcTemplate.query(sqlString, new Object[] { new Integer(address) }, litePaoRowMapper);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("No liteYukonPaobjects found with (carrier) address(" + address + ")");
        }
        return liteYukonPaobects;
    }

    @Override
    public List<PaoIdentifier> getPaosByAddressRange(int startAddress, int endAddress) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT pao.PAObjectID, pao.Type");
            sql.append("FROM YukonPAObject pao");
            sql.append("JOIN DeviceCarrierSettings DCS on pao.PAOBJECTID = DCS.DEVICEID");
            sql.append("WHERE address").gte(startAddress);
            sql.append("AND address").lte(endAddress);
            sql.append("AND pao.PAObjectID").neq(Device.SYSTEM_DEVICE_ID);
            sql.append("ORDER BY pao.Category, pao.PAOClass, pao.PAOName");

            List<PaoIdentifier> result = jdbcTemplate.query(sql, TypeRowMapper.PAO_IDENTIFIER);

            return result;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("No Paos found in (carrier) address range(" + startAddress + " - " + endAddress
                + ")");
        }
    }

    @Override
    public PaoLoader<DisplayablePao> getDisplayablePaoLoader() {
        return new PaoLoader<DisplayablePao>() {
            @Override
            public Map<PaoIdentifier, DisplayablePao> getForPaos(Iterable<PaoIdentifier> identifiers) {
                Map<PaoIdentifier, String> namesForYukonDevices = getNamesForPaos(identifiers);
                Map<PaoIdentifier, DisplayablePao> result =
                    Maps.newHashMapWithExpectedSize(namesForYukonDevices.size());

                for (Entry<PaoIdentifier, String> entry : namesForYukonDevices.entrySet()) {
                    DisplayableDevice displayableMeter =
                        new DisplayableDevice(entry.getKey().getPaoIdentifier(), entry.getValue());
                    result.put(entry.getKey(), displayableMeter);
                }

                return result;
            }
        };
    }

    public Map<PaoIdentifier, String> getNamesForPaos(Iterable<PaoIdentifier> identifiers) {
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(jdbcTemplate);

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ypo.PAObjectID, ypo.PAOName");
                sql.append("FROM YukonPAObject ypo");
                sql.append("WHERE ypo.PAObjectID").in(subList);
                return sql;
            }
        };

        Function<PaoIdentifier, Integer> inputTypeToSqlGeneratorTypeMapper = new Function<PaoIdentifier, Integer>() {
            @Override
            public Integer apply(PaoIdentifier from) {
                return from.getPaoId();
            }
        };

        RowMapper<Entry<Integer, String>> rowMapper =
            new RowMapper<Entry<Integer, String>>() {
                @Override
                public Entry<Integer, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return Maps.immutableEntry(rs.getInt("PAObjectID"), rs.getString("PAOName"));
                }
            };

        return template.mappedQuery(sqlGenerator, identifiers, rowMapper, inputTypeToSqlGeneratorTypeMapper);
    }

    @Override
    public List<PaoIdentifier> getPaoIdentifiersForPaoIds(Iterable<Integer> paoIds) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {

                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ypo.PAObjectID, ypo.Type");
                sql.append("FROM YukonPAObject ypo");
                sql.append("WHERE ypo.PAObjectID").in(subList);
                return sql;
            }
        };

        return template.query(sqlGenerator, paoIds, TypeRowMapper.PAO_IDENTIFIER);
    }

    public List<LiteYukonPAObject> getAllSubsForUser(LiteYukonUser user) {
        List<LiteYukonPAObject> subList = new ArrayList<>(10);
        for (LiteYukonPAObject element : getAllCapControlSubBuses()) {
            if (authDao.userHasAccessPAO(user, element.getLiteID())) {
                subList.add(element);
            }
        }
        return subList;
    }

    @Override
    public Map<Integer, PaoIdentifier> findPaoIdentifiersByCarrierAddress(Iterable<Integer> carrierAddresses) {
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(jdbcTemplate);

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT YP.PaobjectId, YP.Type, CS.Address");
                sql.append("FROM YukonPaobject YP");
                sql.append("JOIN DeviceCarrierSettings CS ON YP.PaobjectId = CS.DeviceId");
                sql.append("WHERE CS.Address").in(subList);
                return sql;
            }
        };

        Function<Integer, Integer> typeMapper = Functions.identity();
        YukonRowMapper<Map.Entry<Integer, PaoIdentifier>> rowMapper =
            new YukonRowMapper<Entry<Integer, PaoIdentifier>>() {
                @Override
                public Entry<Integer, PaoIdentifier> mapRow(YukonResultSet rs) throws SQLException {
                    PaoIdentifier paoIdentifier =
                        new PaoIdentifier(rs.getInt("PaobjectId"), rs.getEnum("Type", PaoType.class));
                    Integer carrierAddress = rs.getInt("Address");
                    return Maps.immutableEntry(carrierAddress, paoIdentifier);
                }
            };

        Map<Integer, PaoIdentifier> retVal =
            template.mappedQuery(sqlGenerator, carrierAddresses, rowMapper, typeMapper);

        return retVal;
    }

    @Override
    public Map<String, PaoIdentifier> findPaoIdentifiersByMeterNumber(Iterable<String> meterNumbers) {
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(jdbcTemplate);

        SqlFragmentGenerator<String> sqlGenerator = new SqlFragmentGenerator<String>() {
            @Override
            public SqlFragmentSource generate(List<String> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT YP.PaobjectId, YP.Type, MG.MeterNumber");
                sql.append("FROM YukonPaobject YP");
                sql.append("LEFT JOIN DeviceMeterGroup MG ON YP.PaobjectId = MG.DeviceId");
                sql.append("WHERE MG.MeterNumber").in(subList);
                return sql;
            }
        };

        Function<String, String> typeMapper = Functions.identity();
        YukonRowMapper<Map.Entry<String, PaoIdentifier>> rowMapper =
            new YukonRowMapper<Entry<String, PaoIdentifier>>() {
                @Override
                public Entry<String, PaoIdentifier> mapRow(YukonResultSet rs) throws SQLException {
                    PaoIdentifier paoIdentifier =
                        new PaoIdentifier(rs.getInt("PaobjectId"), rs.getEnum("Type", PaoType.class));
                    String meterNumber = rs.getString("MeterNumber");
                    return Maps.immutableEntry(meterNumber, paoIdentifier);
                }
            };

        Map<String, PaoIdentifier> retVal = template.mappedQuery(sqlGenerator, meterNumbers, rowMapper, typeMapper);

        return retVal;
    }

    @Override
    public Map<String, PaoIdentifier> findPaoIdentifiersByName(Iterable<String> names) {
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(jdbcTemplate);

        SqlFragmentGenerator<String> sqlGenerator = new SqlFragmentGenerator<String>() {
            @Override
            public SqlFragmentSource generate(List<String> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT PaobjectId, Type, PaoName");
                sql.append("FROM YukonPaobject");
                sql.append("WHERE PaoName").in(subList);
                return sql;
            }
        };

        Function<String, String> typeMapper = Functions.identity();
        YukonRowMapper<Map.Entry<String, PaoIdentifier>> rowMapper =
            new YukonRowMapper<Entry<String, PaoIdentifier>>() {
                @Override
                public Entry<String, PaoIdentifier> mapRow(YukonResultSet rs) throws SQLException {
                    PaoIdentifier paoIdentifier =
                        new PaoIdentifier(rs.getInt("PaobjectId"), rs.getEnum("Type", PaoType.class));
                    String paoName = rs.getString("PaoName");
                    return Maps.immutableEntry(paoName, paoIdentifier);
                }
            };

        Map<String, PaoIdentifier> retVal = template.mappedQuery(sqlGenerator, names, rowMapper, typeMapper);

        return retVal;
    }

    @Override
    public Integer getRouteIdForRouteName(String routeName) {
        Integer result = null;
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT y.PAObjectID");
            sql.append("FROM YukonPAObject y");
            sql.append("WHERE Category").eq(PaoCategory.ROUTE);
            sql.append("AND upper(y.PAOName)").eq(routeName.toUpperCase());

            result = jdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException e) {
            // return default
        }

        return result;
    }

    @Override
    public int getDisabledDeviceCount(DeviceGroup group) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT count(PAObjectID)");
        sql.append("FROM YukonPAObject ypo");
        sql.append("WHERE ypo.DisableFlag").eq_k(YNBoolean.YES);
        
        SqlFragmentSource groupSqlWhereClause =
            deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(group), "YPO.paObjectId");
        sql.append("AND").appendFragment(groupSqlWhereClause);
        
        return jdbcTemplate.queryForInt(sql);
    }

    @Override
    public List<PaoIdentifier> getAllPaoIdentifiersForTags(PaoTag paoTag, PaoTag... paoTags) {
        Set<PaoType> paoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(paoTag, paoTags);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT paObjectId, type");
        sql.append("FROM yukonPAObject");
        sql.append("WHERE type").in(paoTypes);

        List<PaoIdentifier> paoIdentifiers = jdbcTemplate.query(sql, TypeRowMapper.PAO_IDENTIFIER);

        return paoIdentifiers;
    }
    
    @Override
    public List<LiteYukonPAObject> getAllPaos() {
        return jdbcTemplate.query(litePaoSql, litePaoRowMapper);
    }
    
    @Override
    public List<PaoType> getExistingPaoTypes() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT Type");
        sql.append("FROM YukonPaobject");
        sql.append("ORDER BY Type");
        
        return jdbcTemplate.query(sql, TypeRowMapper.PAO_TYPE);
    }
    
    @Override
    public int getPaoCount(Set<PaoType> paoTypes) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(PAObjectID)");
        sql.append("FROM YukonPAObject");
        sql.append("WHERE Type").in(paoTypes);
        
        return jdbcTemplate.queryForInt(sql);
    }

    @Override
    public YukonPao findPort(String paoName) {
        YukonPao pao = findYukonPao(paoName, PaoCategory.PORT, PaoClass.PORT);
        if (pao == null) {
            return findYukonPao(paoName, PaoType.RFN_1200);
        } else {
            return pao;
        }
    }

}