package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.IterableUtils;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueBuilder;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.DatabaseVendorResolver;
import com.cannontech.database.vendor.VendorSpecificSqlBuilder;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;
import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.SetMultimap;

/**
 * Implementation of RawPointHistoryDao
 */
public class RawPointHistoryDaoImpl implements RawPointHistoryDao {
    private static final Logger log = YukonLogManager.getLogger(RawPointHistoryDaoImpl.class);

    private YukonJdbcTemplate yukonTemplate = null;
    private VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory;
    private AttributeService attributeService;
    private DatabaseVendorResolver databaseConnectionVendorResolver;
    
    private SqlFragmentSource buildSql(boolean startInclusive, int pointId, Date startDate, Date stopDate, boolean reverseOrder) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT rph.pointid, rph.timestamp, rph.value, rph.quality, p.pointtype");
        appendFromAndWhereClause(sql, pointId, startDate, stopDate, startInclusive);
        appendOrderByClause(sql, reverseOrder);

        return sql;
    }

    private SqlFragmentSource buildLimitedSql(boolean startInclusive, int pointId, Date startDate, Date stopDate, int maxRows, boolean reverseOrder) {
        VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();
        SqlBuilder sqla = builder.buildFor(DatabaseVendor.MS2000);
        sqla.append("SELECT DISTINCT TOP " + maxRows);
        sqla.append(  "rph.pointid, rph.timestamp, rph.value, rph.quality, p.pointtype");
        appendFromAndWhereClause(sqla, pointId, startDate, stopDate, startInclusive);
        appendOrderByClause(sqla, reverseOrder);

        SqlBuilder sqlb = builder.buildOther();
        sqlb.append("select * from (");
        sqlb.append(  "SELECT DISTINCT rph.pointid, rph.timestamp,");
        sqlb.append(    "rph.value, rph.quality, p.pointtype,");
        sqlb.append(    "ROW_NUMBER() over (");
        appendOrderByClause(sqlb, reverseOrder);
        sqlb.append(    ") rn");
        appendFromAndWhereClause(sqlb, pointId, startDate, stopDate, startInclusive);
        sqlb.append(") numberedRows");
        sqlb.append("where numberedRows.rn").lte(maxRows);
        sqlb.append("ORDER BY numberedRows.rn");
        
        return builder;
    }

    private static void appendFromAndWhereClause(SqlBuilder sql, int pointId, Date startDate,
            Date stopDate, boolean startInclusive) {
        
        sql.append("FROM rawpointhistory rph");
        sql.append(  "JOIN point p on rph.pointId = p.pointId");
        sql.append("WHERE rph.pointid").eq(pointId); //used to be an IN clause?
        
        appendTimeStampClause(sql, startDate, stopDate, startInclusive);
    }

    public static void appendTimeStampClause(SqlBuilder sql, Date startDate, Date stopDate,
            boolean startInclusive) {
        if (startInclusive) {
            if (startDate != null) {
                sql.append("AND rph.timestamp").gte(startDate);
            }
            if (stopDate != null) {
                sql.append("AND rph.timestamp").lt(stopDate);
            }
        } else {
            if (startDate != null) {
                sql.append("AND rph.timestamp").gt(startDate);
            }
            if (stopDate != null) {
                sql.append("AND rph.timestamp").lte(stopDate);
            }
        }
    }
    
    public static void appendOrderByClause(SqlBuilder sql, boolean reverseOrder) {
        sql.append("ORDER BY rph.timestamp");
        if (reverseOrder) {
            sql.append("DESC");
        }
    }
    
    private List<PointValueHolder> executeQuery(SqlFragmentSource sql) {
        List<PointValueHolder> result = yukonTemplate.query(sql, new LiteRphRowMapper());
        result = Collections.unmodifiableList(result);
        return result;
    }
    
    public List<PointValueHolder> getPointData(int pointId, Date startDate, Date stopDate) {
        return getPointData(pointId, startDate, stopDate, false, false);
    }

    public List<PointValueHolder> getPointData(int pointId, Date startDate, Date stopDate, boolean startInclusive, boolean reverseOrder) {
        SqlFragmentSource sql = buildSql(startInclusive, pointId, startDate, stopDate, reverseOrder);
        return executeQuery(sql);
    }

    public List<PointValueHolder> getLimitedPointData(int pointId, Date startDate, Date stopDate, boolean startInclusive, boolean reverseOrder, int maxRows) {
        SqlFragmentSource sql = buildLimitedSql(startInclusive, pointId, startDate, stopDate, maxRows, reverseOrder);
        return executeQuery(sql);
    }
    
    public Map<PaoIdentifier, PointValueQualityHolder> getSingleAttributeData(Iterable<? extends YukonPao> displayableDevices, Attribute attribute, final boolean excludeDisabledPaos) {
        ListMultimap<PaoIdentifier, PointValueQualityHolder> limitedStuff = getLimitedAttributeData(displayableDevices, attribute, null, null, 1, excludeDisabledPaos);
        
        return Maps.transformValues(limitedStuff.asMap(), new Function<Collection<PointValueQualityHolder>, PointValueQualityHolder>() {
            public PointValueQualityHolder apply(Collection<PointValueQualityHolder> from) {
                return Iterables.getOnlyElement(from);
            }
        });
    }
    
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getLimitedAttributeData(Iterable<? extends YukonPao> displayableDevices, Attribute attribute, final Date startDate, final Date stopDate, final int maxRows, final boolean excludeDisabledPaos) {
        SqlFragmentGeneratorFactory factory = new SqlFragmentGeneratorFactory() {
            public SqlFragmentGenerator<Integer> create(final PointIdentifier pointIdentifier) {
                return new SqlFragmentGenerator<Integer>() {
                    @Override
                    public SqlFragmentSource generate(List<Integer> subList) {
                        SqlStatementBuilder sql = new SqlStatementBuilder();
                        sql.append("SELECT * FROM (");
                        sql.append("SELECT DISTINCT yp.paobjectid, rph.pointid, rph.timestamp,");
                        sql.append(  "rph.value, rph.quality, p.pointtype");
                        sql.append(    ", ROW_NUMBER() OVER (");
                        sql.append(      "PARTITION BY rph.pointid ORDER BY rph.timestamp DESC");
                        sql.append(    ") rn");
                        sql.append("FROM rawpointhistory rph");
                        sql.append(  "JOIN point p ON rph.pointId = p.pointId");
                        sql.append(  "JOIN YukonPaobject yp ON p.paobjectid = yp.paobjectid");
                        sql.append("WHERE p.PointOffset").eq(pointIdentifier.getOffset());
                        sql.append(  "AND p.PointType").eq(pointIdentifier.getPointType());
                        appendTimeStampClause(sql, startDate, stopDate, false);
                        sql.append(  "AND yp.PAObjectID").in(subList);
                        if (excludeDisabledPaos) {
                            sql.append(  "AND yp.DisableFlag = 'N'");
                        }
                        sql.append(") numberedRows");
                        sql.append("WHERE numberedRows.rn").lte(maxRows);
                        sql.append("ORDER BY numberedRows.pointid, numberedRows.rn");

                        return sql;
                    }
                };
            }
        };
        
        return loadValuesForGeneratorFactory(factory, displayableDevices, attribute, maxRows, excludeDisabledPaos);
    }

    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getAttributeData(Iterable <? extends YukonPao> displayableDevices, Attribute attribute, final Date startDate, final Date stopDate, final boolean excludeDisabledPaos) {
        SqlFragmentGeneratorFactory factory = new SqlFragmentGeneratorFactory() {
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
                        sql.append("WHERE p.PointOffset").eq(pointIdentifier.getOffset());
                        sql.append(  "AND p.PointType").eq(pointIdentifier.getPointType());
                        appendTimeStampClause(sql, startDate, stopDate, false);
                        sql.append(  "AND yp.PAObjectID").in(subList);
                        if (excludeDisabledPaos) {
                            sql.append(  "AND yp.DisableFlag = 'N'");
                        }
                        appendOrderByClause(sql, true);
                        
                        return sql;
                    }
                };
            }
        };
        
        return loadValuesForGeneratorFactory(factory, displayableDevices, attribute, 20, excludeDisabledPaos);
    }
    
    private ListMultimap<PaoIdentifier, PointValueQualityHolder> loadValuesForGeneratorFactory(
            SqlFragmentGeneratorFactory sqlGeneratorFactory,
            Iterable<? extends YukonPao> paos, Attribute attribute, int valuePerPaoHint,
            final boolean excludeDisabledDevices) {
        // maps from the point identifier to the pao, it is possible, although
        // unlikely, that two paos would have the same point
        // for an attribute, thus this is a SetMultimap instead of a Map
        SetMultimap<PaoPointIdentifier, PaoIdentifier> paoIdentifierLookup = HashMultimap.create(IterableUtils.guessSize(paos),
                                                                                                 1);
        
        // maps from the pao to the values, this is the pao associated with the attribute
        // not the pao associated with the value's point (they could be the same, but may not be)
        ListMultimap<PaoIdentifier, PointValueQualityHolder> result = ArrayListMultimap.create(IterableUtils.guessSize(paos),
                                                                                               valuePerPaoHint);

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
        
        for (YukonPao pao : paos) {
            try {
                // for non-mapped attributes, the following does not require a DB hit
                PaoPointIdentifier identifier = attributeService.getPaoPointIdentifierForAttribute(pao,
                                                                                                   attribute);
                paoIdentifierLookup.put(identifier, pao.getPaoIdentifier());
            } catch (IllegalArgumentException e) {
                LogHelper.debug(log, "unable to look up values for %s on %s: %s", attribute, pao, e.toString());
                continue; //This device does not support the selected attribute.
            }
        }
        
        ImmutableMultimap<PointIdentifier, PaoIdentifier> paoPointIdentifiersMap = PaoUtils.mapPaoPointIdentifiers(paoIdentifierLookup.keySet());

        for (final PointIdentifier pointIdentifier : paoPointIdentifiersMap.keySet()) {
            ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(yukonTemplate);

            final LiteRPHQualityRowMapper liteRPHQualityRowMapper = new LiteRPHQualityRowMapper();

            ParameterizedRowMapper<Map.Entry<Integer, PointValueQualityHolder>> parameterizedRowMapper = new ParameterizedRowMapper<Map.Entry<Integer, PointValueQualityHolder>>() {
                @Override
                public Map.Entry<Integer, PointValueQualityHolder> mapRow(ResultSet rs, int rowNum)
                        throws SQLException {
                    int paObjectId = rs.getInt("PAObjectID");

                    PointValueQualityHolder pointValueQualityHolder = liteRPHQualityRowMapper.mapRow(rs, 0);
                    return Maps.immutableEntry(paObjectId, pointValueQualityHolder);
                }
            };

            ListMultimap<PaoIdentifier, PointValueQualityHolder> rows = template.multimappedQuery(sqlGeneratorFactory.create(pointIdentifier),
                                                                                                  paoPointIdentifiersMap.get(pointIdentifier),
                                                                                                  parameterizedRowMapper,
                                                                                                  PaoUtils.getPaoIdentifierIdFunction());

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
        return result;
    }
    
    /**
     * Helper class which maps a result set row into a PointValueHolder
     */
    private class LiteRphRowMapper implements ParameterizedRowMapper<PointValueHolder> {

        public PointValueQualityHolder mapRow(ResultSet rs, int rowNum) throws SQLException {

            PointValueBuilder builder = PointValueBuilder.create();
            builder.withResultSet(rs);
            builder.withType(rs.getString("pointtype"));
            return builder.build();
        }
    }
    
    private class LiteRPHQualityRowMapper implements ParameterizedRowMapper<PointValueQualityHolder> {

        public PointValueQualityHolder mapRow(ResultSet rs, int rowNum) throws SQLException {
            PointValueBuilder builder = PointValueBuilder.create();
            builder.withResultSet(rs);
            builder.withType(rs.getString("pointtype"));
            return builder.build();
        }
    }

    public List<PointValueHolder> getIntervalPointData(int pointId, Date startDate, Date stopDate, ChartInterval resolution, Mode mode) {
        // unlike the other code, this expects to process things in increasing order

        List<PointValueHolder> result = new ArrayList<PointValueHolder>();
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
    public void changeQuality(int changeId, PointQuality questionable) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("update RawPointHistory");
        sql.append("set Quality").eq(questionable);
        sql.append("where ChangeId").eq(changeId);
        
        yukonTemplate.update(sql);
        
    }
    
    @Override
    public PointValueQualityHolder getPointValueQualityForChangeId(int changeId) {
    
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT");
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
    public List<PointValueQualityHolder> getAdjacentPointValues(final int changeId, int ... offsets) throws SQLException {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT rph.*, p.PointType");
    	sql.append("FROM RawPointHistory rph");
    	sql.append("JOIN Point p ON (rph.PointId = p.PointId)");
    	sql.append("WHERE rph.PointId = (");
    	sql.append("	SELECT rph_inner.PointId");
    	sql.append("	FROM RawPointHistory rph_inner");
    	sql.append("	WHERE rph_inner.ChangeId ").eq(changeId);
    	sql.append(")");
    	appendOrderByClause(sql, true);
    	
    	AdjacentPointValuesRse rse = new AdjacentPointValuesRse(changeId, Arrays.asList(ArrayUtils.toObject(offsets)));
        yukonTemplate.query(sql, rse);
        
        int indexOfChange = rse.getIndexOfChange();
        List<PointValueQualityHolder> resultsTail = rse.getResultsTail();
        
        List<PointValueQualityHolder> adjacentPointValues = new ArrayList<PointValueQualityHolder>(offsets.length);
        for (int offset : offsets) {
        	int index = indexOfChange - offset;
        	if (index >= 0 && index < resultsTail.size()) {
        		adjacentPointValues.add(resultsTail.get(index));
        	} else {
        		adjacentPointValues.add(null);
        	}
        }
        
        return adjacentPointValues;
    }
    
    private interface SqlFragmentGeneratorFactory {
        public SqlFragmentGenerator<Integer> create(PointIdentifier pointIdentifier);
    }
    
    public List<PointValueHolder> getImmediatelyAdjacentPointValues(PointValueHolder pvh) throws SQLException {
        
        int pointId = pvh.getId();
        Date centerDate = pvh.getPointDataTimeStamp();
        
        List<PointValueHolder> pvhs = Lists.newArrayListWithCapacity(2);
        
        pvhs.addAll(getLimitedPointData(pointId, null, centerDate, true, false, 1));
        pvhs.addAll(getLimitedPointData(pointId, centerDate, null, false, false, 1));
        
        return pvhs;
    }
    
    public List<PointValueHolder> getImmediatelyAdjacentPointValues(PointValueHolder pvh, int changeId) throws SQLException {
        
        DatabaseVendor databaseVendor = databaseConnectionVendorResolver.getDatabaseVendor();
        
        if (databaseVendor == DatabaseVendor.MS2000) {
            
            return getImmediatelyAdjacentPointValues(pvh);
        
        } else {
            
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT *");
            sql.append("FROM (");
            sql.append("    SELECT rph_inner1.*, pt.PointType, ROW_NUMBER() OVER (ORDER BY timestamp, changeid) row1");
            sql.append("    FROM RAWPOINTHISTORY rph_inner1");
            sql.append("    JOIN Point pt ON (rph_inner1.PointId = pt.PointId)");
            sql.append("    WHERE rph_inner1.PointId").eq(pvh.getId());
            sql.append(") rph1");
            sql.append("WHERE PointId").eq(pvh.getId());
            sql.append("AND rph1.row1 - (");
            sql.append("    SELECT rph_inner2.row2 FROM (");
            sql.append("        SELECT rph_inner3.*, ROW_NUMBER() OVER (ORDER BY timestamp, changeid) row2");
            sql.append("        FROM RAWPOINTHISTORY rph_inner3");
            sql.append("        WHERE PointId").eq(pvh.getId());
            sql.append("    ) rph_inner2");
            sql.append("    WHERE rph_inner2.changeid").eq(changeId);
            sql.append(")");
            sql.append("IN (-1,1)");
            
            List<PointValueHolder> pvhs = yukonTemplate.query(sql, new LiteRphRowMapper());
            return pvhs;
        }
    }
    
    private final class AdjacentPointValuesRse implements ResultSetExtractor {
    	
    	private Integer indexOfChange = null;
    	private List<PointValueQualityHolder> resultsTail = new ArrayList<PointValueQualityHolder>();
    	int minimumIndex;
    	int changeId;
    	
    	public AdjacentPointValuesRse(int changeId, List<Integer> offsetsCollection) {
    		this.changeId = changeId;
    		this.minimumIndex = Ordering.natural().min(offsetsCollection);
    	}
		
		public int getIndexOfChange() {
			return this.indexOfChange;
		}
		public List<PointValueQualityHolder> getResultsTail() {
			return resultsTail;
		}
		
		@Override
		public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
			
			int currentChangeId = 0;
			PointValueQualityHolder prevHolder = null;
			
			while (rs.next()) {
				
				currentChangeId = rs.getInt("ChangeId");
				
				PointValueBuilder builder = PointValueBuilder.create();
				builder.withResultSet(rs);
				builder.withType(rs.getString("PointType"));
				PointValueQualityHolder holder = builder.build();
				
				if (prevHolder == null || !holder.equals(prevHolder)) {
					resultsTail.add(holder);
					prevHolder = holder;
				}
				
            	if (currentChangeId == changeId) {
            		indexOfChange = resultsTail.size() - 1;
            	}
            	
            	if (indexOfChange != null && (resultsTail.size() - 1) >= indexOfChange - minimumIndex) {
            		return indexOfChange;
            	}
			}
			if (indexOfChange == null) {
				throw new IllegalStateException();
			}
			return null;
		}
	}
    
    @Override
    public void deleteValue(int changeId) {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("DELETE");
    	sql.append("FROM RawPointHistory");
    	sql.append("WHERE ChangeId").eq(changeId);
    	
    	yukonTemplate.update(sql);
    }
    
    @Autowired
    public void setJdbcTemplate(YukonJdbcTemplate yukonTemplate) {
        this.yukonTemplate = yukonTemplate;
    }
    
    @Autowired
    public void setVendorSpecificSqlBuilderFactory(
            VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory) {
        this.vendorSpecificSqlBuilderFactory = vendorSpecificSqlBuilderFactory;
    }
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @Autowired
    public void setDatabaseConnectionVendorResolver(DatabaseVendorResolver databaseConnectionVendorResolver) {
		this.databaseConnectionVendorResolver = databaseConnectionVendorResolver;
	}
}
