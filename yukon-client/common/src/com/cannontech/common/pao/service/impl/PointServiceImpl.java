package com.cannontech.common.pao.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.PreviousReadings;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ReverseList;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.VendorSpecificSqlBuilder;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;

public class PointServiceImpl implements PointService {

    @Autowired private AttributeService attributeService;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private PointDao pointDao;
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    
    public @Override Map<PointType, List<LitePoint>> getSortedPointTree(int paoId, MessageSourceAccessor accessor) {
        
        List<LitePoint> points = pointDao.getLitePointsByPaObjectId(paoId);
        Map<PointType, List<LitePoint>> typeToPoints = new LinkedHashMap<>();
        Map<PointType, List<LitePoint>> initial = new HashMap<>();
        Set<PointType> types = new HashSet<>();
        
        // Build distinct set of point types and store points in lists
        for (LitePoint point : points) {
            PointType type = point.getPointTypeEnum();
            types.add(type);
            List<LitePoint> pointList = initial.get(type);
            if (pointList == null) {
                pointList = new ArrayList<>();
                initial.put(type, pointList);
            }
            pointList.add(point);
        }
        
        // Sort distinct list of point types
        List<PointType> distinctTypes = new ArrayList<>(types);
        Collections.sort(distinctTypes, new Comparator<PointType>() {
            @Override
            public int compare(PointType o1, PointType o2) {
                return accessor.getMessage(o1).compareTo(accessor.getMessage(o2));
            }
        });
        
        // Map the sorted point types to point lists; remembering insertion order.
        for (PointType type : distinctTypes) {
            List<LitePoint> sortedPoints = initial.get(type);
            Collections.sort(sortedPoints, LiteComparators.liteNameComparator);
            typeToPoints.put(type, sortedPoints);
        }
        
        return typeToPoints;
    }
    
    @Override
    public LitePoint getPointForPao(YukonPao pao, PointIdentifier pointIdentifier) throws NotFoundException {

        LitePoint point =
            pointDao.getLitePointIdByDeviceId_Offset_PointType(pao.getPaoIdentifier().getPaoId(),
                pointIdentifier.getOffset(), pointIdentifier.getPointType().getPointTypeId());

        return point;
    }

    @Override
    public LitePoint getPointForPao(PaoPointIdentifier paoPointIdentifier) throws NotFoundException {
        return getPointForPao(paoPointIdentifier.getPaoIdentifier(), paoPointIdentifier.getPointIdentifier());
    }

    @Override
    public boolean pointExistsForPao(YukonPao pao, PointIdentifier pointIdentifier) {

        try {
            LitePoint point = this.getPointForPao(pao, pointIdentifier);
            if (point.getPointTypeEnum() == PointType.System) {
                return false;
            }
        } catch (NotFoundException e) {
            return false;
        }

        return true;
    }

    @Override
    public boolean pointExistsForPao(PaoPointIdentifier paoPointIdentifier) {
        return pointExistsForPao(paoPointIdentifier.getPaoIdentifier(), paoPointIdentifier.getPointIdentifier());
    }

    /**
     * Gets all the previous reading information for a given point.
     * 
     * @param mav
     * @param lp
     */
    @Override
    public PreviousReadings getPreviousReadings(LitePoint lp) {

        PreviousReadings previousReadings = new PreviousReadings();

        // ask for six months, with a max of 36 results
        Date today = new Date();

        // first 36 hours - all points
        Date sixMonthsAgo = DateUtils.addMonths(today, -6);
        Range<Date> dateRange = new Range<Date>(sixMonthsAgo, false, today, true);
        List<PointValueHolder> previous36 =
            rawPointHistoryDao.getLimitedPointData(lp.getPointID(),dateRange.translate(CtiUtilities.INSTANT_FROM_DATE), false, Order.REVERSE, 36);

        List<PointValueHolder> previous3Months = Collections.emptyList();
        if (previous36.size() == 36) {
            // great, let's go get some more
            PointValueHolder lastPvhOfThe36 = previous36.get(36 - 1);
            Date lastDateOfThe36 = lastPvhOfThe36.getPointDataTimeStamp();
            Date beforeDate = DateUtils.truncate(lastDateOfThe36, Calendar.DATE);
            beforeDate = DateUtils.addSeconds(beforeDate, -1);
            previousReadings.setCutoffDate(beforeDate);
            // ask for daily readings from 93 days ago to "before"
            Date today1 = new Date();

            Date ninetyThreeDaysAgo = DateUtils.addDays(today1, -93);

            if (!beforeDate.before(ninetyThreeDaysAgo)) {
                previous3Months =
                    rawPointHistoryDao.getIntervalPointData(lp.getPointID(), ninetyThreeDaysAgo, beforeDate,
                        ChartInterval.DAY_MIDNIGHT, RawPointHistoryDao.Mode.HIGHEST);

                previous3Months = new ReverseList<>(previous3Months);
            }
        } else {
            previousReadings.setCutoffDate(sixMonthsAgo);
        }

        previousReadings.setPrevious36(previous36);
        previousReadings.setPrevious3Months(previous3Months);

        return previousReadings;
    }

    @Override
    public int getCountOfGroupAttributeStateGroup(DeviceGroup group, Attribute attribute, LiteStateGroup stateGroup) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        SqlFragmentSource lookupSql = pointDao.getAttributeLookupSql(attribute);
        sql.append("FROM (").appendFragment(lookupSql).append(") PaoPointLookup");
        sql.append("JOIN Point PT on PT.pointId = PaoPointLookup.pointId");
        sql.append("WHERE");
        sql.append("PT.stateGroupId").eq(stateGroup.getStateGroupID());
        SqlFragmentSource groupSqlWhereClause =
            deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(group), "PaoPointLookup.paObjectId");
        sql.append("AND").appendFragment(groupSqlWhereClause);

        return yukonJdbcTemplate.queryForInt(sql);
    }

    /**
     * 
     * @param group
     * @param attribute
     * @param stateGroup
     * @return
     */
    @Override
    public List<Integer> getPaoIdsForGroupAttributeStateGroup(DeviceGroup group, Attribute attribute,
            LiteStateGroup stateGroup) {
        if (stateGroup == null) {
            return Collections.emptyList();
        }
        return getPaoIdsForGroupAttributeStateGroupId(group, attribute, stateGroup.getStateGroupID());
    }

    /**
     * 
     * @param group
     * @param attribute
     * @param stateGroupId
     * @return
     */
    @Override
    public List<Integer> getPaoIdsForGroupAttributeStateGroupId(DeviceGroup group, Attribute attribute,
            Integer stateGroupId) {
        if (stateGroupId == null) {
            return Collections.emptyList();
        }
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlFragmentSource lookupSql = pointDao.getAttributeLookupSql(attribute);
        sql.append("SELECT PaoPointLookup.paObjectid");
        sql.append("FROM (").appendFragment(lookupSql).append(") PaoPointLookup");
        sql.append("JOIN Point PT ON PT.pointId = PaoPointLookup.pointId");
        sql.append("WHERE");
        sql.append("PT.stateGroupId").eq(stateGroupId);
        SqlFragmentSource groupSqlWhereClause =
            deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(group), "PaoPointLookup.paObjectId");
        sql.append("AND").appendFragment(groupSqlWhereClause);
        return yukonJdbcTemplate.query(sql, TypeRowMapper.INTEGER);
    }

    @Override
    public List<Integer> findDeviceIdsInGroupWithAttributePointStateGroup(DeviceGroup group, Attribute attribute,
            LiteStateGroup stateGroup) {

        return getPaoIdsForGroupAttributeStateGroup(group, attribute, stateGroup);
    }

    @Override
    public List<Integer> findDeviceIdsInGroupWithAttributePointStateGroupId(DeviceGroup group, Attribute attribute,
            Integer stateGroupId) {

        return getPaoIdsForGroupAttributeStateGroupId(group, attribute, stateGroupId);
    }

    /**
     * Same query as in getCountOfGroupAttributeStateGroup(..) without
     * the state group clause.
     */
    @Override
    public int getCountDevicesInGroupWithAttributePoint(DeviceGroup group, Attribute attribute) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlFragmentSource lookupSql = pointDao.getAttributeLookupSql(attribute);
        sql.append("SELECT COUNT(*)");
        sql.append("FROM (").appendFragment(lookupSql).append(") PaoPointLookup");
        sql.append("JOIN Point PT ON PT.pointId = PaoPointLookup.pointId");
        sql.append("WHERE");
        SqlFragmentSource groupSqlWhereClause =
            deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(group), "PaoPointLookup.paObjectId");
        sql.appendFragment(groupSqlWhereClause);

        return yukonJdbcTemplate.queryForInt(sql);
    }

    /**
     * Same query as countDevicesInGroupWithAttributePoint(..) but
     * with TOP clauses.
     */
    @Override
    public int getCountDevicesInGroupWithAttributePoint(DeviceGroup group, Attribute attribute, int limitToRowCount) {
        VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();
        SqlBuilder sqla = builder.buildFor(DatabaseVendor.getMsDatabases());
        sqla.append("SELECT COUNT(*)");
        SqlFragmentSource lookupSql = pointDao.getAttributeLookupSqlLimit(attribute, limitToRowCount);
        sqla.append("FROM (").appendFragment(lookupSql).append(") PaoPointLookup");
        sqla.append("JOIN Point PT ON PT.pointId = PaoPointLookup.pointId");
        sqla.append("WHERE");
        SqlFragmentSource groupSqlWhereClause =
            deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(group), "PaoPointLookup.paObjectId");
        sqla.appendFragment(groupSqlWhereClause);

        SqlBuilder sqlb = builder.buildOther();
        sqlb.append("SELECT COUNT(*)");
        sqlb.append("FROM (");
        sqlb.append("SELECT * ");
        sqlb.append("FROM (").appendFragment(lookupSql).append(") PaoPointLookup");
        sqlb.append("JOIN Point PT ON PT.pointId = PaoPointLookup.pointId");
        sqlb.append("WHERE");
        sqlb.appendFragment(groupSqlWhereClause);
        sqlb.append(")");
        
        return yukonJdbcTemplate.queryForInt(builder);
    }

    /**
     * Same query as in countDevicesInGroupWithAttributePoint(..) except select
     * clause.
     */
    @Override
    public List<Integer> findDeviceIdsInGroupWithAttributePoint(DeviceGroup group, Attribute attribute) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PaoPointLookup.paObjectid");
        SqlFragmentSource lookupSql = pointDao.getAttributeLookupSql(attribute);
        sql.append("FROM (").appendFragment(lookupSql).append(") PaoPointLookup");
        sql.append("JOIN Point PT ON PT.pointId = PaoPointLookup.pointId");
        sql.append("WHERE");
        SqlFragmentSource groupSqlWhereClause =
            deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(group), "PaoPointLookup.paObjectId");
        sql.appendFragment(groupSqlWhereClause);

        return yukonJdbcTemplate.query(sql, TypeRowMapper.INTEGER);
    }
}
