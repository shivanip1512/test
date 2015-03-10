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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

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
import com.cannontech.database.RowMapper;
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
    
    private final static String litePaoSql = "select y.PAObjectID, y.Category, y.PAOName, "
        + "y.Type, y.PAOClass, y.Description, y.DisableFlag, d.PORTID, dcs.ADDRESS, dr.routeid "
        + "from yukonpaobject y " + "left outer join devicedirectcommsettings d on y.paobjectid = d.deviceid "
        + "left outer join devicecarriersettings DCS on Y.PAOBJECTID = DCS.DEVICEID "
        + "left outer join deviceroutes dr on y.paobjectid = dr.deviceid ";

    private final ParameterizedRowMapper<LiteYukonPAObject> litePaoRowMapper = new LitePaoRowMapper();

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
            sql.append("select PAObjectID, Category, Type");
            sql.append("from YukonPAObject");
            sql.append("where PAObjectID").eq(paoId);

            YukonPao pao = jdbcTemplate.queryForObject(sql, RowMapper.PAO_IDENTIFIER);

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
        sql.append("select PAObjectID, Type");
        sql.append("from YukonPAObject");
        sql.append("where PaoName").eq(paoName);
        sql.append("and Category").eq(paoCategory);
        sql.append("and PaoClass").eq(paoClass);

        PaoIdentifier paoIdentifier = jdbcTemplate.queryForObject(sql, RowMapper.PAO_IDENTIFIER);

        return paoIdentifier;
    }

    @Override
    public LiteYukonPAObject getLiteYukonPAO(int paoID) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append(litePaoSql);
            sql.append("where y.PAObjectId").eq(paoID);
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
                sql.append("where y.PAObjectId").in(subList);
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
    public LiteYukonPAObject findUnique(String paoName, PaoType paoType) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder(litePaoSql);
            sql.append("where UPPER(y.PAOName) = UPPER(").appendArgument(paoName).append(")");
            sql.append("and y.Category").eq_k(paoType.getPaoCategory());
            sql.append("and y.PAOClass").eq_k(paoType.getPaoClass());
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
        sql.append("where type").eq_k(paoType);

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
            sql.append("select PAOName");
            sql.append("from YukonPAObject");
            sql.append("where PAObjectID").eq(paoId);
            String name = jdbcTemplate.queryForObject(sql, RowMapper.STRING);
            return name;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A PAObject with id " + paoId + "cannot be found.");
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
                sql.append("select paobjectid, paoname");
                sql.append("from yukonpaobject");
                sql.append("where paobjectid").in(subList);

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
        sql.append("select y.PAObjectID, y.Category, y.PAOName, y.Type, y.PAOClass, y.Description, y.DisableFlag,");
        sql.append("null as PortId, null as Address, null as RouteId"); // needed for the mapper, always null
                                                                        // for routes
        sql.append("from  YukonPAObject y");
        sql.append("where Category").eq(PaoCategory.ROUTE);
        sql.append("order BY y.PAOName");

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
            sqlBuilder.append("where UPPER(y.PAOName) LIKE UPPER(?) ");
        } else {
            sqlBuilder.append("where UPPER(y.PAOName) = UPPER(?) ");
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
                "select pao.PAObjectID, pao.Category, pao.PAOName, "
                    + " pao.Type, pao.PAOClass, pao.Description, pao.DisableFlag, d.PORTID, dcs.ADDRESS, dr.routeid "
                    + " from " + YukonPAObject.TABLE_NAME + " pao " + " left outer join "
                    + DeviceDirectCommSettings.TABLE_NAME + " d on pao.paobjectid = d.deviceid " + " left outer join "
                    + DeviceCarrierSettings.TABLE_NAME + " DCS on pao.PAOBJECTID = DCS.DEVICEID " + " left outer join "
                    + DeviceRoutes.TABLE_NAME + " dr on pao.paobjectid = dr.deviceid " + " where address = ? "
                    + " order BY pao.Category, pao.PAOClass, pao.PAOName";

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
            sql.append("select pao.PAObjectID, pao.Type");
            sql.append("from YukonPAObject pao");
            sql.append("join DeviceCarrierSettings DCS on pao.PAOBJECTID = DCS.DEVICEID");
            sql.append("where address").gte(startAddress);
            sql.append("and address").lte(endAddress);
            sql.append("and pao.PAObjectID").neq(Device.SYSTEM_DEVICE_ID);
            sql.append("order BY pao.Category, pao.PAOClass, pao.PAOName");

            List<PaoIdentifier> result = jdbcTemplate.query(sql, RowMapper.PAO_IDENTIFIER);

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
                sql.append("select ypo.PAObjectID, ypo.PAOName from YukonPAObject ypo where ypo.PAObjectID").in(subList);
                return sql;
            }
        };

        Function<PaoIdentifier, Integer> inputTypeToSqlGeneratorTypeMapper = new Function<PaoIdentifier, Integer>() {
            @Override
            public Integer apply(PaoIdentifier from) {
                return from.getPaoId();
            }
        };

        ParameterizedRowMapper<Entry<Integer, String>> rowMapper =
            new ParameterizedRowMapper<Entry<Integer, String>>() {
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
                sql.append("select ypo.PAObjectID, ypo.Type");
                sql.append("from YukonPAObject ypo");
                sql.append("where ypo.PAObjectID IN (");
                sql.appendArgumentList(subList);
                sql.append(")");
                return sql;
            }
        };

        return template.query(sqlGenerator, paoIds, RowMapper.PAO_IDENTIFIER);
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
                sql.append("select YP.PaobjectId, YP.Type, CS.Address");
                sql.append("from YukonPaobject YP");
                sql.append("join DeviceCarrierSettings CS on YP.PaobjectId = CS.DeviceId");
                sql.append("where CS.Address").in(subList);
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
                sql.append("select YP.PaobjectId, YP.Type, MG.MeterNumber");
                sql.append("from YukonPaobject YP");
                sql.append("left join DeviceMeterGroup MG on YP.PaobjectId = MG.DeviceId");
                sql.append("where MG.MeterNumber").in(subList);
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
                sql.append("select PaobjectId, Type, PaoName");
                sql.append("from YukonPaobject");
                sql.append("where PaoName").in(subList);
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
            sql.append("select y.PAObjectID");
            sql.append("from  YukonPAObject y");
            sql.append("where Category").eq(PaoCategory.ROUTE);
            sql.append("and upper(y.PAOName)").eq(routeName.toUpperCase());

            result = jdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException e) {
            // return default
        }

        return result;
    }

    @Override
    public int getDisabledDeviceCount(DeviceGroup group) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select count(PAObjectID) from YukonPAObject ypo");
        sql.append("where ypo.DisableFlag").eq_k(YNBoolean.YES);
        SqlFragmentSource groupSqlWhereClause =
            deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(group), "YPO.paObjectId");
        sql.append("and").appendFragment(groupSqlWhereClause);
        return jdbcTemplate.queryForInt(sql);
    }

    @Override
    public List<PaoIdentifier> getAllPaoIdentifiersForTags(PaoTag paoTag, PaoTag... paoTags) {
        Set<PaoType> paoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(paoTag, paoTags);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select paObjectId, type from yukonPAObject");
        sql.append("where type").in(paoTypes);

        List<PaoIdentifier> paoIdentifiers = jdbcTemplate.query(sql, RowMapper.PAO_IDENTIFIER);

        return paoIdentifiers;
    }
    
    @Override
    public List<LiteYukonPAObject> getAllPaos() {
        return jdbcTemplate.query(litePaoSql, litePaoRowMapper);
    }
    
    @Override
    public List<PaoType> getExistingPaoTypes() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT Type FROM YukonPaobject ORDER BY Type");
        return jdbcTemplate.query(sql, RowMapper.PAO_TYPE);
    }
}