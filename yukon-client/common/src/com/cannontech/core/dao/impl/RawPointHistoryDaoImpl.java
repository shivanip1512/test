package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Instant;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.IterableUtils;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ReadableRange;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueBuilder;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.DatabaseVendorResolver;
import com.cannontech.database.vendor.VendorSpecificSqlBuilder;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;

public class RawPointHistoryDaoImpl implements RawPointHistoryDao {
    private static final Logger log = YukonLogManager.getLogger(RawPointHistoryDaoImpl.class);

    @Autowired private YukonJdbcTemplate yukonTemplate = null;
    @Autowired private VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory;
    @Autowired private AttributeService attributeService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private DatabaseVendorResolver databaseConnectionVendorResolver;
    
    YukonRowMapper<Map.Entry<Integer, PointValueQualityHolder>> rphYukonRowMapper =
        new YukonRowMapper<Map.Entry<Integer, PointValueQualityHolder>>() {
            final LiteRPHQualityRowMapper liteRPHQualityRowMapper = new LiteRPHQualityRowMapper();

            @Override
            public Map.Entry<Integer, PointValueQualityHolder> mapRow(YukonResultSet rs) throws SQLException {
                int paObjectId = rs.getInt("PAObjectID");

                PointValueQualityHolder pointValueQualityHolder = liteRPHQualityRowMapper.mapRow(rs);
                return Maps.immutableEntry(paObjectId, pointValueQualityHolder);
            }
        };

    private SqlFragmentSource buildSql(Range<Instant> range, Iterable<Integer> pointIds, Order order, boolean excludeDisabledPaos) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT rph.pointid, rph.timestamp, rph.value, rph.quality, p.pointtype");
        appendFromAndWhereClause(sql, pointIds, range, excludeDisabledPaos);
        appendOrderByClause(sql, order);

        return sql;
    }

    private SqlFragmentSource buildLimitedSql(Range<Instant> instantRange, int pointId, boolean excludeDisabledPaos, int maxRows, Order order, OrderBy orderBy) {
        VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();
        
        SqlBuilder sqla = builder.buildForAllMsDatabases();
        sqla.append("SELECT DISTINCT TOP " + maxRows);
        sqla.append(  "rph.pointid, rph.timestamp, rph.value, rph.quality, p.pointtype");
        appendFromAndWhereClause(sqla, Collections.singleton(pointId), instantRange, excludeDisabledPaos);
        appendOrderByClause(sqla, order, orderBy);
        
        SqlBuilder sqlb = builder.buildOther();
        sqlb.append("select * from (");
        sqlb.append(  "SELECT DISTINCT rph.pointid, rph.timestamp,");
        sqlb.append(    "rph.value, rph.quality, p.pointtype,");
        sqlb.append(    "ROW_NUMBER() over (");
        appendOrderByClause(sqlb, order, orderBy);
        sqlb.append(    ") rn");
        appendFromAndWhereClause(sqlb, Collections.singleton(pointId), instantRange, excludeDisabledPaos);
        sqlb.append(") numberedRows");
        sqlb.append("where numberedRows.rn").lte(maxRows);
        sqlb.append("ORDER BY numberedRows.rn");
       
        return builder;
    }

    private static void appendFromAndWhereClause(SqlBuilder sql, Iterable<Integer> pointIds,
                                                 ReadableRange<Instant> range,
                                                 boolean excludeDisabledPaos) {
        sql.append("FROM rawpointhistory rph");
        sql.append(  "JOIN point p on rph.pointId = p.pointId");
        if (excludeDisabledPaos) {
            sql.append(  "JOIN YukonPaobject yp ON p.paobjectid = yp.paobjectid");
        }
        sql.append("WHERE rph.pointid").in(pointIds);
        if (excludeDisabledPaos) {
            sql.append("AND yp.DisableFlag").eq(YNBoolean.NO);
        }

        appendTimeStampClause(sql, range);
    }

    private static void appendTimeStampClause(SqlBuilder sql, ReadableRange<Instant> dateRange) {
        Instant startDate = dateRange == null ? null : dateRange.getMin();
        if (startDate != null) {
            if (dateRange.isIncludesMinValue()) {
                sql.append("AND rph.timestamp").gte(startDate);
            } else {
                sql.append("AND rph.timestamp").gt(startDate);
            }
        }
        Instant stopDate = dateRange == null ? null : dateRange.getMax();
        if (stopDate != null) {
            if (dateRange.isIncludesMaxValue()) {
                sql.append("AND rph.timestamp").lte(stopDate);
            } else {
                sql.append("AND rph.timestamp").lt(stopDate);
            }
        }
    }

    /**
     * Append a change id range where necessary. Nothing will be added if changeIdRange is null or if it
     * is an unbounded range.
     */
    private static void appendChangeIdClause(SqlBuilder sql, ReadableRange<Long> changeIdRange) {
        if (changeIdRange == null || changeIdRange.isUnbounded()) {
            return;
        }

        if (changeIdRange.getMin() != null) {
            if (changeIdRange.isIncludesMinValue()) {
                sql.append("AND rph.changeId").gte(changeIdRange.getMin());
            } else {
                sql.append("AND rph.changeId").gt(changeIdRange.getMin());
            }
        }
        if (changeIdRange.getMax() != null) {
            if (changeIdRange.isIncludesMaxValue()) {
                sql.append("AND rph.changeId").lte(changeIdRange.getMax());
            } else {
                sql.append("AND rph.changeId").lt(changeIdRange.getMax());
            }
        }
    }

    private static void appendOrderByClause(SqlBuilder sql, Order order) {
        appendOrderByClause(sql, order, OrderBy.TIMESTAMP);
    }

    private static void appendOrderByClause(SqlBuilder sql, Order order, OrderBy orderBy) {
        appendOrderByClause(sql, order, orderBy, "rph");
    }
    
    private static void appendOrderByClause(SqlBuilder sql, Order order, OrderBy orderBy, String table) {
        sql.append("ORDER BY");
        if (orderBy == OrderBy.TIMESTAMP) {
            sql.append(table + ".timestamp");
        }else if (orderBy == OrderBy.VALUE) {
            sql.append(table + ".value");
        }
        if (order == Order.REVERSE) {
            sql.append("DESC");
        }
    }

    private List<PointValueHolder> executeQuery(SqlFragmentSource sql) {
        List<PointValueHolder> result = yukonTemplate.query(sql, new LiteRphRowMapper());
        result = Collections.unmodifiableList(result);
        return result;
    }

    @Override
    public List<PointValueHolder> getPointDataWithDisabledPaos(int pointId, Date startDate, Date stopDate) {
        Range<Date> dateRange = new Range<>(startDate, true, stopDate, true);
        SqlFragmentSource sql = buildSql(dateRange.translate(CtiUtilities.INSTANT_FROM_DATE),
                                         Collections.singleton(pointId),
                                         Order.FORWARD,
                                         false);
        return executeQuery(sql);
    }

    @Override
    public List<PointValueHolder> getPointData(int pointId, Date startDate, Date stopDate) {
    	Range<Date> dateRange = new Range<>(startDate, false, stopDate, true);
        return getPointData(pointId, dateRange.translate(CtiUtilities.INSTANT_FROM_DATE), Order.FORWARD);
    }

    @Override
	public List<PointValueHolder> getPointData(int pointId,Range<Instant> instantRange, Order order) {
		SqlFragmentSource sql = buildSql( instantRange, Collections.singleton(pointId), order,true);
		return executeQuery(sql);
	}

    @Override
    public void queuePointData(int pointId, Range<Instant> instantRange, Order order,
            BlockingQueue<PointValueHolder> queue, AtomicBoolean isCompleted) {
        SqlFragmentSource sql = buildSql(instantRange, Collections.singleton(pointId), order, false);
        executeQueryToQueue(sql, queue, isCompleted);
    }

    private void executeQueryToQueue(SqlFragmentSource sql, BlockingQueue<PointValueHolder> q,AtomicBoolean isCompleted) {
        yukonTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                PointValueBuilder builder = PointValueBuilder.create();
                builder.withResultSet(rs);
                builder.withType(rs.getString("pointtype"));
                PointValueQualityHolder pvh = builder.build();
                try {
                    q.put(pvh);
                } catch (InterruptedException e) {
                    log.debug("Error while queuing data " + e);
                }

            }
        });
        if(q.isEmpty()) {
            isCompleted.set(true);
        }
    }
    
    @Override
    public List<PointValueHolder> getPointData(Set<Integer> pointIds, final ReadableRange<Instant> range,
                                               final boolean excludeDisabledPaos, final Order order) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(yukonTemplate);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {

            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                Iterable<Integer> subList1 = subList;
                return buildSql((Range<Instant>)range, subList1, order, excludeDisabledPaos);
            }
        };
        return template.query(sqlGenerator, pointIds, new LiteRphRowMapper());
    }
 
    @Override
    public List<PointValueHolder> getLimitedPointData(int pointId, Range<Instant> range, boolean excludeDisabledPaos, Order order, OrderBy orderBy, int maxRows) {
        SqlFragmentSource sql = buildLimitedSql(range, pointId, excludeDisabledPaos, maxRows, order, orderBy);
        return executeQuery(sql);
    }

    @Override
    public List<PointValueHolder> getLimitedPointData(int pointId, Range<Instant> range, boolean excludeDisabledPaos, Order order, int maxRows) {
        return getLimitedPointData(pointId, range, excludeDisabledPaos, order, OrderBy.TIMESTAMP, maxRows);
    }

    @Override
    public Map<PaoIdentifier, PointValueQualityHolder> getSingleAttributeData(Iterable<? extends YukonPao> displayableDevices,
            Attribute attribute,
            final boolean excludeDisabledPaos,
            final Set<PointQuality> excludeQualities) {

    	Range<Instant> range = new Range<Date>(null, false, null, false).translate(CtiUtilities.INSTANT_FROM_DATE);
        ListMultimap<PaoIdentifier, PointValueQualityHolder> limitedStuff =
            getLimitedAttributeData(displayableDevices, attribute, 1, excludeDisabledPaos, range, Order.REVERSE,
                excludeQualities);

        return Maps.transformValues(limitedStuff.asMap(), Iterables::getOnlyElement);
    }
    
    @Override
    public Map<PaoIdentifier, PointValueQualityHolder> getSingleAttributeData(
            Iterable<? extends YukonPao> displayableDevices, Attribute attribute, boolean excludeDisabledPaos,
            Set<PointQuality> excludeQualities, ReadableRange<Long> changeIdRange) {

        Range<Instant> range = new Range<Date>(null, false, null, false).translate(CtiUtilities.INSTANT_FROM_DATE);
        ListMultimap<PaoIdentifier, PointValueQualityHolder> limitedStuff =
            getLimitedAttributeData(displayableDevices, attribute, range, changeIdRange, 1, excludeDisabledPaos,
                Order.REVERSE, OrderBy.TIMESTAMP, excludeQualities);

        return Maps.transformValues(limitedStuff.asMap(), Iterables::getOnlyElement);
    }

    @Override
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getLimitedAttributeData(
            Iterable<? extends YukonPao> displayableDevices,
            Attribute attribute,
            final ReadableRange<Instant> dateRange,
            final ReadableRange<Long> changeIdRange,
            final int maxRows,
            final boolean excludeDisabledPaos,
            final Order order,
            final OrderBy orderBy,
            final Set<PointQuality> excludeQualities) {
        
        Stopwatch stopwatch = null;
        if (log.isInfoEnabled()) {
            stopwatch = Stopwatch.createStarted();
        }
        
        SqlFragmentGeneratorFactory factory = new SqlFragmentGeneratorFactory() {
            @Override
            public SqlFragmentGenerator<Integer> create(final PointIdentifier pointIdentifier) {
                return new SqlFragmentGenerator<Integer>() {
                    @Override
                    public SqlFragmentSource generate(List<Integer> subList) {
                        SqlStatementBuilder sql = new SqlStatementBuilder();
                        
                        sql.append("SELECT");
                        sql.append("    limited.paobjectid, limited.pointid, limited.timestamp,");
                        sql.append("    limited.value, limited.quality, limited.pointtype, limited.rownumber AS rn");
                        sql.append("FROM (");
                        sql.append("    SELECT");
                        sql.append("        cleaned.paobjectid, cleaned.pointid, cleaned.timestamp,");
                        sql.append("        cleaned.value, cleaned.quality, cleaned.pointtype,");
                        sql.append("        ROW_NUMBER() OVER (PARTITION BY cleaned.pointid"); 
                        appendOrderByClause(sql, order, orderBy, "cleaned");
                        sql.append("        ) rownumber");
                        sql.append("    FROM (");
                        sql.append("        SELECT DISTINCT");
                        sql.append("            yp.paobjectid, rph.pointid, rph.timestamp,");
                        sql.append("            rph.value, rph.quality, p.pointtype");
                        sql.append("        FROM rawpointhistory rph");
                        sql.append("        JOIN point p ON rph.pointId = p.pointId");
                        sql.append("        JOIN YukonPaobject yp ON p.paobjectid = yp.paobjectid");
                        sql.append("        WHERE p.PointOffset").eq_k(pointIdentifier.getOffset());
                        sql.append("        AND p.PointType").eq_k(pointIdentifier.getPointType());
                        if (excludeQualities != null && !excludeQualities.isEmpty()) {
                            sql.append("    AND rph.Quality").notIn(excludeQualities);
                        }
                        appendChangeIdClause(sql, changeIdRange);
                        appendTimeStampClause(sql, dateRange);
                        sql.append("        AND yp.PAObjectID").in(subList);
                        if (excludeDisabledPaos) {
                            sql.append("    AND yp.DisableFlag").eq(YNBoolean.NO);
                        }
                        sql.append("    ) cleaned");
                        sql.append(") limited");        
                        sql.append("WHERE limited.rownumber").lte(maxRows);
                        sql.append("ORDER BY limited.pointid, limited.rownumber");

                        return sql;
                    }
                };
            }
        };
        ListMultimap<PaoIdentifier, PointValueQualityHolder> values = loadValuesForGeneratorFactory(factory,
                displayableDevices, attribute, maxRows);

        if (log.isInfoEnabled()){
            stopwatch.stop();
            int numPaos = Iterables.size(displayableDevices);
            String logMessage = "getLimitedAttributeData() - " + numPaos + " paos. Attribute: " + attribute.getKey();
            if (dateRange != null) {
                logMessage += ". Date range: " + (dateRange.getMin() == null ? null : dateRange.getMin().toString()); 
                logMessage += " to " + (dateRange.getMax() == null ? null : dateRange.getMax().toString());
            }
            logMessage +=  ".  Elapsed time: " + stopwatch.toString();
            log.info(logMessage);
        }
        
        return values; 
    }

    @Override
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getLimitedAttributeData(
            Iterable<? extends YukonPao> displayableDevices,
            Attribute attribute,
            final int maxRows,
            final boolean excludeDisabledPaos,
            final Range<Instant> instantRange,
            final Order order,
            final Set<PointQuality> excludeQualities) {

        return getLimitedAttributeData(displayableDevices, attribute, instantRange, null,
            maxRows, excludeDisabledPaos, order, OrderBy.TIMESTAMP, excludeQualities);
    }

    @Override
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getLimitedAttributeData(Iterable<? extends YukonPao> displayableDevices,
                                                                                        Iterable<? extends Attribute> attributes,
                                                                                        ReadableRange<Instant> instantRange,
                                                                                        final int maxRows,
                                                                                        final boolean excludeDisabledPaos,
                                                                                        final Order order,
                                                                                        final Set<PointQuality> excludeQualities) {
        ListMultimap<PaoIdentifier, PointValueQualityHolder> limitedAttributeDatas = ArrayListMultimap.create(IterableUtils.guessSize(displayableDevices), 20);
        for (Attribute attribute : attributes) {
            ListMultimap<PaoIdentifier, PointValueQualityHolder> limitedAttributeData = getLimitedAttributeData(displayableDevices, attribute, instantRange, null,
                                    maxRows, excludeDisabledPaos, order, OrderBy.TIMESTAMP, excludeQualities);
            limitedAttributeDatas.putAll(limitedAttributeData);
        }
        return limitedAttributeDatas;
    }

    @Override
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getAttributeData(Iterable<? extends YukonPao> paos,
            Attribute attribute, final ReadableRange<Instant> dateRange, final ReadableRange<Long> changeIdRange,
            final boolean excludeDisabledPaos, final Order order, final Double minimumValue,
            final Set<PointQuality> excludeQualities) {

        SqlFragmentGeneratorFactory factory = new SqlFragmentGeneratorFactory() {
            @Override
            public SqlFragmentGenerator<Integer> create(final PointIdentifier pointIdentifier) {
                return new SqlFragmentGenerator<Integer>() {
                    @Override
                    public SqlFragmentSource generate(List<Integer> subList) {
                        SqlStatementBuilder sql = new SqlStatementBuilder();
                        appendAttributeDataSql(sql, subList, pointIdentifier, dateRange, changeIdRange,
                            excludeDisabledPaos, minimumValue, excludeQualities);
                        appendOrderByClause(sql, order);
                        return sql;
                    }
                };
            }
        };

        return loadValuesForGeneratorFactory(factory, paos, attribute, 20);
    }
    
    @Override
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getAttributeData(Iterable<? extends YukonPao> paos,
            Attribute attribute, final ReadableRange<Instant> dateRange, final LocalTime time, final ReadableRange<Long> changeIdRange,
            final boolean excludeDisabledPaos, final Order order, final Double minimumValue,
            final Set<PointQuality> excludeQualities) {
        
        SqlFragmentGeneratorFactory factory = new SqlFragmentGeneratorFactory() {
            @Override
            public SqlFragmentGenerator<Integer> create(final PointIdentifier pointIdentifier) {
                return new SqlFragmentGenerator<Integer>() {
                    @Override
                    public SqlFragmentSource generate(List<Integer> subList) {
                        
                        SqlStatementBuilder sql = new SqlStatementBuilder();
                        appendAttributeDataSql(sql, subList, pointIdentifier, dateRange, changeIdRange,
                            excludeDisabledPaos, minimumValue, excludeQualities);
                        
                        VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();
                        SqlBuilder sqla = builder.buildFor(DatabaseVendor.getMsDatabases());
                        sqla.append(sql);  
                        int hour = time.get(DateTimeFieldType.hourOfDay());
                        int minute = time.get(DateTimeFieldType.minuteOfHour());
                        sqla.append("AND DATEPART(hour, timestamp)").eq(hour);
                        sqla.append("AND DATEPART(minute, timestamp)").eq(minute);
                        appendOrderByClause(sqla, order);
                        
                        SqlBuilder sqlb = builder.buildOther();
                        sqlb.append(sql); 
                        String hoursAndMinutes = time.toString("HHmm");
                        sqlb.append("AND to_char(timestamp,'hh24mi')").eq(hoursAndMinutes);
                        appendOrderByClause(sqlb, order);
                        
                        return builder;
                    }
                };
            }
        };

        return loadValuesForGeneratorFactory(factory, paos, attribute, 20);
    }

    private void appendAttributeDataSql(SqlStatementBuilder sql, List<Integer> subList,
            PointIdentifier pointIdentifier, ReadableRange<Instant> dateRange, ReadableRange<Long> changeIdRange,
            boolean excludeDisabledPaos, Double minimumValue, Set<PointQuality> excludeQualities) {
        sql.append("SELECT DISTINCT yp.paobjectid, rph.pointid, rph.timestamp,");
        sql.append("rph.value, rph.quality, p.pointtype");
        sql.append("FROM rawpointhistory rph");
        sql.append("JOIN point p ON rph.pointId = p.pointId");
        sql.append("JOIN YukonPaobject yp ON p.paobjectid = yp.paobjectid");
        sql.append("WHERE p.PointOffset").eq_k(pointIdentifier.getOffset());
        sql.append("AND p.PointType").eq_k(pointIdentifier.getPointType());
        if (minimumValue != null) {
            sql.append("AND rph.value").gt(minimumValue);
        }
        if (excludeQualities != null && !excludeQualities.isEmpty()) {
            sql.append("AND rph.Quality").notIn(excludeQualities);
        }
        appendChangeIdClause(sql, changeIdRange);
        appendTimeStampClause(sql, dateRange);
        sql.append("AND yp.PAObjectID").in(subList);
        if (excludeDisabledPaos) {
            sql.append("AND yp.DisableFlag").eq(YNBoolean.NO);
        }
    }
    
    @Override
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getAttributeData(Iterable<? extends YukonPao> paos,
            Attribute attribute,
            boolean excludeDisabledPaos,
            Range<Instant> instantRange,
            Order order,
            Set<PointQuality> excludeQualities) {

        return getAttributeData(paos, attribute, instantRange, null, excludeDisabledPaos, order, null, excludeQualities);
    }

    @Override
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getAttributeDataByChangeIdRange(
            Iterable<? extends YukonPao> displayableDevices, Attribute attribute, final ReadableRange<Long> changeIdRange,
            final boolean excludeDisabledPaos, Range<Instant> dateRange, final Order order) {
        SqlFragmentGeneratorFactory factory = new SqlFragmentGeneratorFactory() {
            @Override
            public SqlFragmentGenerator<Integer> create(final PointIdentifier pointIdentifier) {
                return new SqlFragmentGenerator<Integer>() {
                    @Override
                    public SqlFragmentSource generate(List<Integer> subList) {
                        SqlStatementBuilder sql = new SqlStatementBuilder();
                        sql.append("SELECT DISTINCT yp.paobjectid, rph.pointid, rph.timestamp,");
                        sql.append(  "rph.value, rph.quality, p.pointtype");
                        sql.append("FROM rawpointhistory rph");
                        sql.append(  "JOIN point p ON rph.pointId = p.pointId");
                        sql.append(  "JOIN YukonPaobject yp ON p.paobjectid = yp.paobjectid");
                        sql.append("WHERE p.PointOffset").eq_k(pointIdentifier.getOffset());
                        sql.append(  "AND p.PointType").eq_k(pointIdentifier.getPointType());
                        appendChangeIdClause(sql, changeIdRange);
                        sql.append(  "AND yp.PAObjectID").in(subList);
                        if (excludeDisabledPaos) {
                            sql.append(  "AND yp.DisableFlag").eq(YNBoolean.NO);
                        }
                        appendOrderByClause(sql, order);

                        return sql;
                    }
                };
            }
        };

        return loadValuesForGeneratorFactory(factory, displayableDevices, attribute, 20);
    }


    @Override
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getDataByPointName(Iterable<PaoIdentifier> paos,
        final String pointName, final ReadableRange<Instant> dateRange, final ReadableRange<Long> changeIdRange, final Order order) {
        SqlFragmentGenerator<Integer> sqlFragmentGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT DISTINCT p.paobjectid, rph.pointid, rph.timestamp,");
                sql.append(  "rph.value, rph.quality, p.pointtype");
                sql.append("FROM rawpointhistory rph");
                sql.append(  "JOIN point p ON rph.pointId = p.pointId");
                sql.append("WHERE p.PointName").eq(pointName);
                appendChangeIdClause(sql, changeIdRange);
                appendTimeStampClause(sql, dateRange);
                sql.append(  "AND p.PAObjectID").in(subList);
                appendOrderByClause(sql, order);

                return sql;
            }
        };

        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(yukonTemplate);

        ListMultimap<PaoIdentifier, PointValueQualityHolder> rows = template.multimappedQuery(sqlFragmentGenerator,
                                                                                              paos,
                                                                                              rphYukonRowMapper,
                                                                                              PaoUtils.getPaoIdFunction());
        return rows;
    }

    @Override
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getLimitedDataByPointName(Iterable<PaoIdentifier> paos,
        final String pointName, final ReadableRange<Instant> dateRange, final ReadableRange<Long> changeIdRange, final int maxRows,
        final Order order) {
        SqlFragmentGenerator<Integer> sqlFragmentGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT * FROM (");
                sql.append("SELECT DISTINCT p.paobjectid, rph.pointid, rph.timestamp,");
                sql.append(  "rph.value, rph.quality, p.pointtype");
                sql.append(    ", ROW_NUMBER() OVER (");
                sql.append(      "PARTITION BY rph.pointid");
                appendOrderByClause(sql, order);
                sql.append(    ") rn");
                sql.append("FROM rawpointhistory rph");
                sql.append(  "JOIN point p ON rph.pointId = p.pointId");
                sql.append("WHERE p.PointName").eq(pointName);
                appendChangeIdClause(sql, changeIdRange);
                appendTimeStampClause(sql, dateRange);
                sql.append(  "AND p.PAObjectID").in(subList);
                sql.append(") numberedRows");
                sql.append("WHERE numberedRows.rn").lte(maxRows);
                sql.append("ORDER BY numberedRows.pointid, numberedRows.rn");

                return sql;
            }
        };

        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(yukonTemplate);

        ListMultimap<PaoIdentifier, PointValueQualityHolder> rows = template.multimappedQuery(sqlFragmentGenerator,
                                                                                              paos,
                                                                                              rphYukonRowMapper,
                                                                                              PaoUtils.getPaoIdFunction());

        return rows;
    }

    @Override
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getDataByTypeAndOffset(Iterable<PaoIdentifier> paos,
            final PointType pointType, final int offset, final ReadableRange<Instant> dateRange, final ReadableRange<Long> changeIdRange,
            final Order order) {
        SqlFragmentGeneratorFactory factory = new SqlFragmentGeneratorFactory() {
            @Override
            public SqlFragmentGenerator<Integer> create(final PointIdentifier pointIdentifier) {
                return new SqlFragmentGenerator<Integer>() {
                    @Override
                    public SqlFragmentSource generate(List<Integer> subList) {
                        SqlStatementBuilder sql = new SqlStatementBuilder();
                        sql.append("SELECT DISTINCT yp.paobjectid, rph.pointid, rph.timestamp,");
                        sql.append(  "rph.value, rph.quality, p.pointtype");
                        sql.append("FROM rawpointhistory rph");
                        sql.append(  "JOIN point p ON rph.pointId = p.pointId");
                        sql.append(  "JOIN YukonPaobject yp ON p.paobjectid = yp.paobjectid");
                        sql.append("WHERE p.PointOffset").eq_k(pointIdentifier.getOffset());
                        sql.append(  "AND p.PointType").eq_k(pointIdentifier.getPointType());
                        sql.append(  "AND yp.PAObjectID").in(subList);
                        appendChangeIdClause(sql, changeIdRange);
                        appendTimeStampClause(sql, dateRange);
                        appendOrderByClause(sql, order);
                        return sql;
                    }
                };

            }
        };
        PointIdentifier pointIdentifier = new PointIdentifier(pointType, offset);

        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(yukonTemplate);
        ListMultimap<PaoIdentifier, PointValueQualityHolder> result =
            template.multimappedQuery(factory.create(pointIdentifier),
                                      paos,
                                      rphYukonRowMapper,
                                      PaoUtils.getPaoIdFunction());

        return result;
    }

    @Override
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getLimitedDataByTypeAndOffset(
            Iterable<PaoIdentifier> paos, final PointType pointType, final int offset, final ReadableRange<Instant> dateRange,
            final ReadableRange<Long> changeIdRange, final int maxRows, final Order order) {
        SqlFragmentGeneratorFactory factory = new SqlFragmentGeneratorFactory() {
            @Override
            public SqlFragmentGenerator<Integer> create(final PointIdentifier pointIdentifier) {
                return new SqlFragmentGenerator<Integer>() {
                    @Override
                    public SqlFragmentSource generate(List<Integer> subList) {
                        SqlStatementBuilder sql = new SqlStatementBuilder();
                        sql.append("SELECT * FROM (");
                        sql.append("SELECT DISTINCT yp.paobjectid, rph.pointid, rph.timestamp,");
                        sql.append(  "rph.value, rph.quality, p.pointtype");
                        sql.append(    ", ROW_NUMBER() OVER (");
                        sql.append(      "PARTITION BY rph.pointid");
                        appendOrderByClause(sql, order);
                        sql.append(    ") rn");
                        sql.append("FROM rawpointhistory rph");
                        sql.append(  "JOIN point p ON rph.pointId = p.pointId");
                        sql.append(  "JOIN YukonPaobject yp ON p.paobjectid = yp.paobjectid");
                        sql.append("WHERE p.PointOffset").eq_k(pointIdentifier.getOffset());
                        sql.append(  "AND p.PointType").eq_k(pointIdentifier.getPointType());
                        appendChangeIdClause(sql, changeIdRange);
                        appendTimeStampClause(sql, dateRange);
                        sql.append(  "AND yp.PAObjectID").in(subList);
                        sql.append(") numberedRows");
                        sql.append("WHERE numberedRows.rn").lte(maxRows);
                        sql.append("ORDER BY numberedRows.pointid, numberedRows.rn");

                        return sql;
                    }
                };

            }
        };
        PointIdentifier pointIdentifier = new PointIdentifier(pointType, offset);

        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(yukonTemplate);
        ListMultimap<PaoIdentifier, PointValueQualityHolder> result =
            template.multimappedQuery(factory.create(pointIdentifier),
                                      paos,
                                      rphYukonRowMapper,
                                      PaoUtils.getPaoIdFunction());

        return result;
    }

    @Override
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getLimitedDataByDefaultPointName(
            Iterable<PaoIdentifier> paos, final String defaultPointName, final ReadableRange<Instant> dateRange,
            final ReadableRange<Long> changeIdRange, final int maxRows, final Order order) {
        SqlFragmentGeneratorFactory factory = new SqlFragmentGeneratorFactory() {
            @Override
            public SqlFragmentGenerator<Integer> create(final PointIdentifier pointIdentifier) {
                return new SqlFragmentGenerator<Integer>() {
                    @Override
                    public SqlFragmentSource generate(List<Integer> subList) {
                        SqlStatementBuilder sql = new SqlStatementBuilder();
                        sql.append("SELECT * FROM (");
                        sql.append("SELECT DISTINCT yp.paobjectid, rph.pointid, rph.timestamp,");
                        sql.append(  "rph.value, rph.quality, p.pointtype");
                        sql.append(    ", ROW_NUMBER() OVER (");
                        sql.append(      "PARTITION BY rph.pointid");
                        appendOrderByClause(sql, order);
                        sql.append(    ") rn");
                        sql.append("FROM rawpointhistory rph");
                        sql.append(  "JOIN point p ON rph.pointId = p.pointId");
                        sql.append(  "JOIN YukonPaobject yp ON p.paobjectid = yp.paobjectid");
                        sql.append("WHERE p.PointOffset").eq_k(pointIdentifier.getOffset());
                        sql.append(  "AND p.PointType").eq_k(pointIdentifier.getPointType());
                        appendChangeIdClause(sql, changeIdRange);
                        appendTimeStampClause(sql, dateRange);
                        sql.append(  "AND yp.PAObjectID").in(subList);
                        sql.append(") numberedRows");
                        sql.append("WHERE numberedRows.rn").lte(maxRows);
                        sql.append("ORDER BY numberedRows.pointid, numberedRows.rn");

                        return sql;
                    }
                };
            }
        };

        ListMultimap<PaoIdentifier, PointValueQualityHolder> result =
            ArrayListMultimap.create(IterableUtils.guessSize(paos), 20); // 20 should be based on maxRows, with some care taken if maxRows happens to be gigantic
        ImmutableMultimap<PaoType, PaoIdentifier> paosByType = PaoUtils.mapPaoTypes(paos);

        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(yukonTemplate);

        for (Entry<PaoType, Collection<PaoIdentifier>> typeWithPaos : paosByType.asMap().entrySet()) {
            PointIdentifier pointIdentifier =
                paoDefinitionDao.getPointIdentifierByDefaultName(typeWithPaos.getKey(), defaultPointName);

            ListMultimap<PaoIdentifier, PointValueQualityHolder> rows =
                template.multimappedQuery(factory.create(pointIdentifier),
                                          typeWithPaos.getValue(),
                                          rphYukonRowMapper,
                                          PaoUtils.getPaoIdFunction());
            result.putAll(rows);
        }

        return result;
    }

    @Override
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getDataByDefaultPointName(Iterable<PaoIdentifier> paos,
            final String defaultPointName, final ReadableRange<Instant> dateRange, final ReadableRange<Long> changeIdRange,
            final Order order) {
        ListMultimap<PaoIdentifier, PointValueQualityHolder> result =
            ArrayListMultimap.create(IterableUtils.guessSize(paos), 20); // 20 should be based on maxRows, with some care taken if maxRows happens to be gigantic
        ImmutableMultimap<PaoType, PaoIdentifier> paosByType = PaoUtils.mapPaoTypes(paos);

        for (Entry<PaoType, Collection<PaoIdentifier>> typeWithPaos : paosByType.asMap().entrySet()) {
            PointIdentifier pointIdentifier =
                    paoDefinitionDao.getPointIdentifierByDefaultName(typeWithPaos.getKey(), defaultPointName);

            ListMultimap<PaoIdentifier, PointValueQualityHolder> rows =
                getDataByTypeAndOffset(typeWithPaos.getValue(), pointIdentifier.getPointType(),
                    pointIdentifier.getOffset(), dateRange, changeIdRange, order);
            result.putAll(rows);
        }

        return result;
    }

    @Override
    public long getMaxChangeId() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT MAX(ChangeId)");
        sql.append("FROM RawPointHistory");
        long maxChangeId = yukonTemplate.queryForLong(sql);

        return maxChangeId;
    }

    private ListMultimap<PaoIdentifier, PointValueQualityHolder> loadValuesForGeneratorFactory(
            SqlFragmentGeneratorFactory sqlGeneratorFactory,
            Iterable<? extends YukonPao> paos, Attribute attribute, int valuePerPaoHint) {
        /*
         * The general idea here is that input PAOs will probably be of a mix of PaoTypes. Any of those
         * PaoTypes could use a different point for a given attribute, and in the case of mappable
         * attributes, some PAOs' points could be on different PAOs.
         *
         * For example, if the input list contains a mix of 430s, 410s, and a LTC, there would be three
         * different ways of finding the voltage point:
         *   - for the 430s it would be at Analog offset 41
         *   - for the 410s it would be at Demand Accumulator offset 4
         *   - for the LTC it would be any arbitrary point on any device
         *
         * If we wanted to just loop over all of the devices, lookup the point for the attribute, and
         * then load the data, this problem would be easy.
         *
         * But, we want to go to the database as few times as possible and we want to ensure that the
         * possibility of having mixed lists and mappable attributes doesn't slow down a simple query
         * of all 410s, for instance.
         *
         * Example input:
         * 1   MCT-430SL
         * 2   MCT-430SL
         * 3   MCT-410IL
         * 4   MCT-410IL
         * 5   LOAD_TAP_CHANGER
         * 6   LOAD_TAP_CHANGER
         *
         * For the VOLTAGE attribute, we may lookup the following PaoPointIdentifiers:
         * 1   MCT-430SL          -->  1  MCT-430SL  Analog              41
         * 2   MCT-430SL          -->  2  MCT-430SL  Analog              41
         * 3   MCT-410IL          -->  3  MCT-410IL  Demand Accumulator  4
         * 4   MCT-410IL          -->  4  MCT-410IL  Demand Accumulator  4
         * 5   LOAD_TAP_CHANGER   -->  10 RTU        Analog              1045
         * 6   LOAD_TAP_CHANGER   -->  10 RTU        Analog              1045
         *
         * The important thing to note is that the PaoIdentifier part of the PaoPointIdentifier
         * refers to the same PAO as the input in the first four cases, but not the last two. Additionally,
         * the last two PAOs are mapped to the same point (unusual, but legal).
         *
         * As we determine the PaoPointIdentifers, we build a lookup map so that we can go back
         * from the PaoPointIdentifer to the original PaoIdentifier.
         *
         * To efficiently query for this data, we'd group it into three groups:
         * Analog             41   : {1,2}
         * Demand Accumulator 4    : {3,4}
         * Analog             1045 : {10}
         *
         * Now we have three groups that we can process. Because the list of PAOs could be quite large, we use
         * a chunking query to be flexible. To be able to put everything back together, we select
         * back the PAO ID and return it as the key in a ChunkingMappedSqlTemplate result. For the first query,
         * that result may look something like:
         * 1 : {[many RPH rows]}
         * 2 : {[many RPH rows]}
         *
         * To process the first row, we'd take the 1 and combine it with the group's PointIdentifier to
         * get back a PaoPointIdentifier. In this case "1 MCT-430SL Analog 41". That can then be used
         * to get the original PaoIdentifier from that lookup map we built, "1 MCT-430SL". Finally, we
         * can associate the RPH rows with that PaoIdentifier. Note, that in the case of processing
         * the "Analog 1045" row, there would be two PaoIdentifiers that would be returned from the lookup
         * map (because it is actually a Multimap) and we would associate the same rows with both.
         */

        // maps from the point identifier to the pao, it is possible, although
        // unlikely, that two paos would have the same point
        // for an attribute, thus this is a SetMultimap instead of a Map
        
        Stopwatch stopwatch = null;
        if (log.isDebugEnabled()) {
            stopwatch = Stopwatch.createStarted();
        }
        
        SetMultimap<PaoPointIdentifier, PaoIdentifier> paoIdentifierLookup =
            HashMultimap.create(IterableUtils.guessSize(paos), 1);

        for (YukonPao pao : paos) {
            try {
                // for non-mapped attributes, the following does not require a DB hit
                PaoPointIdentifier identifier = attributeService.getPaoPointIdentifierForAttribute(pao, attribute);
                paoIdentifierLookup.put(identifier, pao.getPaoIdentifier());
            } catch (IllegalUseOfAttribute e) {
                if (log.isDebugEnabled()) {
                    String output = String.format("unable to look up values for %s on %s: %s", attribute, pao, e.toString());
                    log.debug(output);
                }
                continue; //This device does not support the selected attribute.
            }
        }

        // maps from the pao to the values, this is the pao associated with the attribute
        // not the pao associated with the value's point (they could be the same, but may not be)
        ListMultimap<PaoIdentifier, PointValueQualityHolder> result =
            ArrayListMultimap.create(IterableUtils.guessSize(paos), valuePerPaoHint);
        ImmutableMultimap<PointIdentifier, PaoIdentifier> paoPointIdentifiersMap =
            PaoUtils.mapPaoPointIdentifiers(paoIdentifierLookup.keySet());

        for (final PointIdentifier pointIdentifier : paoPointIdentifiersMap.keySet()) {
            ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(yukonTemplate);

            ListMultimap<PaoIdentifier, PointValueQualityHolder> rows =
                template.multimappedQuery(sqlGeneratorFactory.create(pointIdentifier),
                                          paoPointIdentifiersMap.get(pointIdentifier),
                                          rphYukonRowMapper,
                                          PaoUtils.getPaoIdFunction());

            for (PaoIdentifier pao : rows.keySet()) {
                List<PointValueQualityHolder> list = rows.get(pao);
                PaoPointIdentifier ppi = new PaoPointIdentifier(pao, pointIdentifier);
                Set<PaoIdentifier> paoIdentifiers = paoIdentifierLookup.get(ppi);

                // typically there will only be one PaoIdentifier here
                for (PaoIdentifier paoIdentifier : paoIdentifiers) {
                    result.putAll(paoIdentifier, list);
                }
            }

            // end of per-PointIdentifier loop
        }

        if (log.isDebugEnabled()) {
            stopwatch.stop();
            int numPaos = Iterables.size(paos);
            int numPointIdentifiers = paoPointIdentifiersMap.keySet().size();
            log.debug("loadValuesForGeneratorFactory() # paos : " + numPaos + ".  # point identifiers: " + numPointIdentifiers
                    + ".  # values resturned: " + result.size() + ".  elapsed time: " + stopwatch.toString());
        }
        
        return result;
    }

    /**
     * Helper class which maps a result set row into a PointValueHolder
     */
    private class LiteRphRowMapper implements YukonRowMapper<PointValueHolder> {
        @Override
        public PointValueQualityHolder mapRow(YukonResultSet rs) throws SQLException {
            PointValueBuilder builder = PointValueBuilder.create();
            builder.withResultSet(rs);
            builder.withType(rs.getString("pointtype"));
            return builder.build();
        }
    }

    private class LiteRPHQualityRowMapper implements YukonRowMapper<PointValueQualityHolder> {
        @Override
        public PointValueQualityHolder mapRow(YukonResultSet rs) throws SQLException {
            PointValueBuilder builder = PointValueBuilder.create();
            builder.withResultSet(rs);
            builder.withType(rs.getString("pointtype"));
            return builder.build();
        }
    }

    @Override
    public List<PointValueHolder> getIntervalPointData(int pointId, Date startDate, Date stopDate, ChartInterval resolution, Mode mode) {
        // unlike the other code, this expects to process things in increasing order

        List<PointValueHolder> result = new ArrayList<>();
        List<PointValueHolder> pointData = getPointData(pointId, startDate, stopDate);
        // we know this list is ordered by timestamp

        PointValueHolder lastGoodValue = null;
        Date lastGoodResolved = null;
        for (PointValueHolder currentValue : pointData) {
            Date currentResolved = resolution.roundDownToIntervalUnit(currentValue.getPointDataTimeStamp());
            if (lastGoodValue != null) {
                // check if current value is in the same interval as the previous
                if (currentResolved.equals(lastGoodResolved)) {
                    // okay, we need to decide which to keep
                    if (mode == Mode.HIGHEST) {
                        // we'll choose to pick the latest in a tie
                        if (currentValue.getValue() < lastGoodValue.getValue()) {
                            // ignore it
                            continue;
                        }
                    }
                } else {
                    // current value belongs in the next bin
                    // promote lastGoodValue to result
                    result.add(lastGoodValue);
                }
            }
            // set last to current
            lastGoodValue = currentValue;
            lastGoodResolved = currentResolved;
        }

        // now that we're done, add the last good value
        if (lastGoodValue != null) {
            result.add(lastGoodValue);
        }
        return result;
    }

    @Override
    public void changeQuality(long changeId, PointQuality questionable) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("update RawPointHistory");
        sql.append("set Quality").eq(questionable);
        sql.append("where ChangeId").eq(changeId);

        yukonTemplate.update(sql);

    }

    @Override
    public PointValueQualityHolder getPointValueQualityForChangeId(long changeId) {

    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT DISTINCT");
    	sql.append("rph.pointId,");
    	sql.append("rph.timestamp,");
    	sql.append("rph.value,");
    	sql.append("rph.quality,");
    	sql.append("p.pointtype");
    	sql.append("FROM RawPointHistory rph");
    	sql.append("JOIN Point p ON (rph.pointId = p.pointId)");
    	sql.append("WHERE rph.changeId").eq(changeId);

    	return yukonTemplate.queryForObject(sql, new LiteRPHQualityRowMapper());
    }


    @Override
    public PointValueQualityHolder getSpecificValue(int pointId, long timestamp)
    throws NotFoundException {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT");
        sql.append("rph.pointId,");
        sql.append("rph.timestamp,");
        sql.append("rph.value,");
        sql.append("rph.quality,");
        sql.append("p.pointtype");
        sql.append("FROM RawPointHistory rph");
        sql.append("JOIN Point p ON (rph.pointId = p.pointId)");
        sql.append("WHERE p.pointid").eq(pointId);
        sql.append("AND timestamp").eq(new Date(timestamp));

        try {
            return yukonTemplate.queryForObject(sql, new LiteRPHQualityRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("No point value for pointid " + pointId + " and timestamp " + timestamp, e);
        }
    }

    private interface SqlFragmentGeneratorFactory {
        public SqlFragmentGenerator<Integer> create(PointIdentifier pointIdentifier);
    }

    @Override
    public AdjacentPointValues getAdjacentPointValues(PointValueHolder pvh) {

        int pointId = pvh.getId();
        Date centerDate = pvh.getPointDataTimeStamp();
        Range<Date> dateRange = new Range<>(null, true, centerDate, false);
		List<PointValueHolder> precedingList = getLimitedPointData(pointId,
				dateRange.translate(CtiUtilities.INSTANT_FROM_DATE)
				/* Clusivity.INCLUSIVE_EXCLUSIVE */, false,
				Order.REVERSE, 1);
		PointValueHolder preceding = Iterables.getOnlyElement(precedingList, null);
		dateRange = new Range<>(centerDate, false, null, true);
		List<PointValueHolder> succeedingList = getLimitedPointData(pointId,
				dateRange.translate(CtiUtilities.INSTANT_FROM_DATE)
				/* Clusivity.EXCLUSIVE_INCLUSIVE */, false,
				Order.FORWARD, 1);
		PointValueHolder succeeding = Iterables.getOnlyElement(succeedingList, null);

        AdjacentPointValues result = new AdjacentPointValues(preceding, succeeding);
        return result;
    }

    @Override
    public void deleteValue(long changeId) {

    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("DELETE");
    	sql.append("FROM RawPointHistory");
    	sql.append("WHERE ChangeId").eq(changeId);

    	yukonTemplate.update(sql);
    }
    
    @Override
    public List<PointValueQualityHolder> getMostRecentValues(int pointId, int rows){

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT");
        sql.append("pointId,");
        sql.append("timestamp,");
        sql.append("value,");
        sql.append("quality,");
        sql.append("pointtype");
        sql.append("FROM (");
        sql.append("  SELECT ROW_NUMBER() OVER (ORDER BY Timestamp DESC, Changeid DESC) as rowNumber, rph.pointId, rph.timestamp, rph.value, rph.quality, p.pointtype");
        sql.append("  FROM RawPointHistory rph");
        sql.append("  JOIN Point p ON (rph.pointId = p.pointId)");
        sql.append("  WHERE rph.pointId").eq(pointId);
        sql.append(") T");
        sql.append("WHERE T.rowNumber").lte(rows);

        return yukonTemplate.query(sql, new LiteRPHQualityRowMapper());
    }
    
    @Override
    public void deletePointData(int pointId, double value, Instant timestamp) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM RawPointHistory");
        sql.append("WHERE pointId").eq(pointId);
        sql.append("   AND value").eq(value);
        sql.append("   AND timestamp").eq(timestamp);  
        yukonTemplate.update(sql);
    }

    private ListMultimap<PaoIdentifier, PointValueQualityHolder> getMostRecentAttributeDataByValue(
            Iterable<? extends YukonPao> displayableDevices, Attribute attribute, boolean excludeDisabledPaos,
            final Order order, final OrderBy orderBy, int value, Set<PointQuality> excludeQualities) {
        Stopwatch stopwatch = null;
        if (log.isDebugEnabled()) {
            stopwatch = Stopwatch.createStarted();
        }

        SqlFragmentGeneratorFactory factory = new SqlFragmentGeneratorFactory() {
            @Override
            public SqlFragmentGenerator<Integer> create(final PointIdentifier pointIdentifier) {
                return new SqlFragmentGenerator<Integer>() {
                    @Override
                    public SqlFragmentSource generate(List<Integer> subList) {
                        SqlStatementBuilder sql = new SqlStatementBuilder();
                        sql.append("SELECT * FROM (");
                        sql.append("SELECT DISTINCT yp.paobjectid, rph.pointid, rph.timestamp,");
                        sql.append("rph.value, rph.quality, p.pointtype");
                        sql.append(", ROW_NUMBER() OVER (");
                        sql.append("PARTITION BY rph.pointid");
                        appendOrderByClause(sql, order, orderBy);
                        sql.append(") rn");
                        sql.append("FROM rawpointhistory rph");
                        sql.append("JOIN point p ON rph.pointId = p.pointId");
                        sql.append("JOIN YukonPaobject yp ON p.paobjectid = yp.paobjectid");
                        sql.append("WHERE p.PointOffset").eq_k(pointIdentifier.getOffset());
                        sql.append("AND p.PointType").eq_k(pointIdentifier.getPointType());
                        sql.append("AND yp.PAObjectID").in(subList);
                        if (excludeDisabledPaos) {
                            sql.append("AND yp.DisableFlag").eq(YNBoolean.NO);
                        }
                        if (excludeQualities != null && !excludeQualities.isEmpty()) {
                            sql.append("AND rph.Quality").notIn(excludeQualities);
                        }
                        sql.append("AND rph.Value").eq_k(value);
                        sql.append(") numberedRows");
                        sql.append("WHERE numberedRows.rn").lte(1);
                        sql.append("ORDER BY numberedRows.pointid, numberedRows.rn");

                        return sql;
                    }
                };
            }
        };
        ListMultimap<PaoIdentifier, PointValueQualityHolder> values = loadValuesForGeneratorFactory(factory, displayableDevices, attribute, 1);

        if (log.isDebugEnabled()) {
            stopwatch.stop();
            int numPaos = Iterables.size(displayableDevices);
            String logMessage =
                "getMostRecentAttributeDataByValue() - " + numPaos + " paos. Attribute: " + attribute.getKey();
            logMessage += ".  Elapsed time: " + stopwatch.toString();
            log.debug(logMessage);
        }

        return values;
    }

    @Override
    public Map<PaoIdentifier, PointValueQualityHolder> getMostRecentAttributeDataByValue(
            Iterable<? extends YukonPao> displayableDevices, Attribute attribute, boolean excludeDisabledPaos,
            int value, Set<PointQuality> excludeQualities) {

        ListMultimap<PaoIdentifier, PointValueQualityHolder> limitedStuff =
            getMostRecentAttributeDataByValue(displayableDevices, attribute, excludeDisabledPaos, Order.REVERSE,
                OrderBy.TIMESTAMP, value, excludeQualities);

        return Maps.transformValues(limitedStuff.asMap(), Iterables::getOnlyElement);
    }
}
