package com.cannontech.common.pao.service.impl;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.PreviousReadings;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.common.util.ReverseList;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.RowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.VendorSpecificSqlBuilder;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;

/**
 * Implementation class for PointService
 */
public class PointServiceImpl implements PointService {

    @Autowired private AttributeService                 attributeService;
    @Autowired private DeviceGroupService               deviceGroupService;
    @Autowired public  PointDao                         pointDao;
    @Autowired private RawPointHistoryDao               rphDao;
    @Autowired private VendorSpecificSqlBuilderFactory  vendorSpecificSqlBuilderFactory;
    @Autowired private YukonJdbcTemplate                yukonJdbcTemplate;

    @Override
    public LitePoint getPointForPao(YukonPao pao, PointIdentifier pointIdentifier) throws NotFoundException {

        LitePoint point = pointDao.getLitePointIdByDeviceId_Offset_PointType(pao.getPaoIdentifier().getPaoId(),
																        		pointIdentifier.getOffset(),
																        		pointIdentifier.getPointType().getPointTypeId());

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
            if (point.getPointType() == PointTypes.SYSTEM_POINT) {
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
        List<PointValueHolder> previous36 = rphDao.getLimitedPointData(lp.getPointID(), sixMonthsAgo, today, Clusivity.EXCLUSIVE_INCLUSIVE, false, Order.REVERSE, 36);

        List<PointValueHolder> previous3Months = Collections.emptyList();
        if (previous36.size() == 36) {
            // great, let's go get some more
            PointValueHolder lastPvhOfThe36 = previous36.get(36 - 1);
            Date lastDateOfThe36 = lastPvhOfThe36.getPointDataTimeStamp();
            Date beforeDate = DateUtils
                    .truncate(lastDateOfThe36, Calendar.DATE);
            beforeDate = DateUtils.addSeconds(beforeDate, -1);
            previousReadings.setCutoffDate(beforeDate);
            // ask for daily readings from 93 days ago to "before"
            Date today1 = new Date();

            Date ninetyThreeDaysAgo = DateUtils.addDays(today1, -93);

            if (!beforeDate.before(ninetyThreeDaysAgo)) {
                previous3Months = rphDao.getIntervalPointData(lp.getPointID(),
                        ninetyThreeDaysAgo, beforeDate,
                        ChartInterval.DAY_MIDNIGHT,
                        RawPointHistoryDao.Mode.HIGHEST);
                
                previous3Months = new ReverseList<PointValueHolder>(previous3Months);
            }
        } else {
            previousReadings.setCutoffDate(sixMonthsAgo);
        }
        
        previousReadings.setPrevious36(previous36);
        previousReadings.setPrevious3Months(previous3Months);
        
        return previousReadings;
    }
    
    @Override
    public int getCountOfGroupAttributeStateGroup(DeviceGroup group, Attribute attribute,
                                                              LiteStateGroup stateGroup) {
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select pao_point_lookup.paObjectid");
        final SqlFragmentSource lookupSql = attributeService.getAttributeLookupSql(attribute);
        sql.append("from (").appendFragment(lookupSql).append(") pao_point_lookup");
        sql.append(  "join Point p on p.pointId = pao_point_lookup.pointId");
        sql.append("where");
        sql.append(  "p.stateGroupId").eq_k(stateGroup.getStateGroupID());
        final SqlFragmentSource groupSqlWhereClause = deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(group), "pao_point_lookup.paObjectId");
        sql.append(  "and").appendFragment(groupSqlWhereClause);
        
        final int result = yukonJdbcTemplate.queryForInt(sql);
        return result;
    }

    /**
     * 
     * @param group
     * @param attribute
     * @param stateGroup
     * @return
     */
    @Override
    public List<Integer> getPaoIdsForGroupAttributeStateGroup(
            DeviceGroup group, Attribute attribute, LiteStateGroup stateGroup) {
        if (stateGroup == null)
            return Collections.emptyList();
        return getPaoIdsForGroupAttributeStateGroupId(group, attribute,
                stateGroup.getStateGroupID());
    }

    /**
     * 
     * @param group
     * @param attribute
     * @param stateGroupId
     * @return
     */
    @Override
    public List<Integer> getPaoIdsForGroupAttributeStateGroupId(
            DeviceGroup group, Attribute attribute, Integer stateGroupId) {
        if (stateGroupId == null)
            return Collections.emptyList();
        final SqlStatementBuilder   sql         = new SqlStatementBuilder();
        final SqlFragmentSource     lookupSql   = attributeService.getAttributeLookupSql(attribute);
        sql.append("select pao_point_lookup.paObjectid");
        sql.append("from (").appendFragment(lookupSql).append(") pao_point_lookup");
        sql.append("join Point p on p.pointId = pao_point_lookup.pointId");
        sql.append("where");
        sql.append("p.stateGroupId").eq_k(stateGroupId);
        final SqlFragmentSource groupSqlWhereClause = deviceGroupService
                .getDeviceGroupSqlWhereClause(Collections.singleton(group), "pao_point_lookup.paObjectId");
        sql.append("and").appendFragment(groupSqlWhereClause);
        final List<Integer> results = yukonJdbcTemplate.query(sql,
                RowMapper.INTEGER);
        return results;
    }

    @Override
    public List<Integer> findDeviceIdsInGroupWithAttributePointStateGroup(
            DeviceGroup group, Attribute attribute, LiteStateGroup stateGroup) {
        return getPaoIdsForGroupAttributeStateGroup(group, attribute,stateGroup);
    }

    @Override
    public List<Integer> findDeviceIdsInGroupWithAttributePointStateGroupId(
            DeviceGroup group, Attribute attribute, Integer stateGroupId) {
        return getPaoIdsForGroupAttributeStateGroupId(group, attribute,stateGroupId);
    }

    /**
     * YUK-11992 Same query as in getCountOfGroupAttributeStateGroup(..) without
     * the state group clause.
     */
    @Override
    public int countDevicesInGroupWithAttributePoint(DeviceGroup group, Attribute attribute) {
        final SqlStatementBuilder   sql       = new SqlStatementBuilder();
        final SqlFragmentSource     lookupSql = attributeService
                .getAttributeLookupSql(attribute);
        sql.append("select count(*)");
        sql.append("from (").appendFragment(lookupSql).append(") pao_point_lookup");
        sql.append("join Point p on p.pointId = pao_point_lookup.pointId");
        sql.append("where");
        final SqlFragmentSource groupSqlWhereClause = deviceGroupService
                .getDeviceGroupSqlWhereClause(Collections.singleton(group), "pao_point_lookup.paObjectId");
        sql.appendFragment(groupSqlWhereClause);

        int result = yukonJdbcTemplate.queryForInt(sql);
        return result;
    }

    /**
     * YUK-11992 Same query as countDevicesInGroupWithAttributePoint(..) but
     * with TOP clauses.
     */
    @Override
    public int countDevicesInGroupWithAttributePoint(DeviceGroup group,
            Attribute attribute, int limitToRowCount) {
        final VendorSpecificSqlBuilder  builder = vendorSpecificSqlBuilderFactory.create();
        final SqlBuilder                sqla    = builder.buildFor(DatabaseVendor.MS2000);
        sqla.append("select count(*)");
        final SqlFragmentSource         lookupSql = 
                attributeService.getAttributeLookupSqlLimit(attribute, limitToRowCount);
        sqla.append("from (").appendFragment(lookupSql).append(") pao_point_lookup");
        sqla.append("join Point p on p.pointId = pao_point_lookup.pointId");
        sqla.append("where");
        final SqlFragmentSource groupSqlWhereClause = deviceGroupService
                .getDeviceGroupSqlWhereClause(Collections.singleton(group),"pao_point_lookup.paObjectId");
        sqla.appendFragment(groupSqlWhereClause);

        final SqlBuilder sqlb = builder.buildOther();
        sqlb.append("select count(*)");
        sqlb.append("from (").appendFragment(lookupSql).append(") pao_point_lookup");
        sqlb.append("join Point p on p.pointId = pao_point_lookup.pointId");
        sqlb.append("where");
        sqlb.appendFragment(groupSqlWhereClause);
        final int result = yukonJdbcTemplate.queryForInt(builder);
        return result;
    }

    /**
     * Same query as in countDevicesInGroupWithAttributePoint(..) except select
     * clause.
     */
    @Override
    public List<Integer> findDeviceIdsInGroupWithAttributePoint(
            DeviceGroup group, Attribute attribute) {
        final SqlStatementBuilder   sql = new SqlStatementBuilder();
        sql.append("select pao_point_lookup.paObjectid");
        final SqlFragmentSource     lookupSql = attributeService.getAttributeLookupSql(attribute);
        sql.append("from (").appendFragment(lookupSql).append(") pao_point_lookup");
        sql.append("join Point p on p.pointId = pao_point_lookup.pointId");
        sql.append("where");
        final SqlFragmentSource     groupSqlWhereClause = deviceGroupService
                .getDeviceGroupSqlWhereClause(Collections.singleton(group), "pao_point_lookup.paObjectId");
        sql.appendFragment(groupSqlWhereClause);

        return yukonJdbcTemplate.query(sql, RowMapper.INTEGER);
    }
}
