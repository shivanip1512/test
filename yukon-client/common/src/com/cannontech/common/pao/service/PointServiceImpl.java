package com.cannontech.common.pao.service;

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
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.YukonJdbcOperations;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.PointTypes;

/**
 * Implementation class for PointService
 */
public class PointServiceImpl implements PointService {

    private PointDao pointDao = null;
    private RawPointHistoryDao rphDao = null;
    private AttributeService attributeService;
    private DeviceGroupService deviceGroupService;
    private YukonJdbcOperations jdbcTemplate;

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
    public PreviousReadings getPreviousReadings(LitePoint lp) {

        PreviousReadings previousReadings = new PreviousReadings();
        
        // ask for six months, with a max of 36 results
        Date today = new Date();

        // first 36 hours - all points
        Date sixMonthsAgo = DateUtils.addMonths(today, -6);
        List<PointValueHolder> previous36 = rphDao.getLimitedPointData(lp.getPointID(), sixMonthsAgo, today, Clusivity.EXCLUSIVE_INCLUSIVE, Order.REVERSE, 36);

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
                        beforeDate, ninetyThreeDaysAgo,
                        ChartInterval.DAY_MIDNIGHT,
                        RawPointHistoryDao.Mode.HIGHEST);
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
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select count(*)");
        SqlFragmentSource lookupSql = attributeService.getAttributeLookupSql(attribute);
        sql.append("from (").appendFragment(lookupSql).append(") pao_point_lookup");
        sql.append(  "join Point p on p.pointId = pao_point_lookup.pointId");
        sql.append("where");
        sql.append(  "p.stateGroupId").eq_k(stateGroup.getStateGroupID());
        SqlFragmentSource groupSqlWhereClause = deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(group), "pao_point_lookup.paObjectId");
        sql.append(  "and").appendFragment(groupSqlWhereClause);
        
        int result = jdbcTemplate.queryForInt(sql);
        
        return result;
    }
    
    @Autowired
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
    
    @Autowired
    public void setRphDao(RawPointHistoryDao rphDao) {
        this.rphDao = rphDao;
    }
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Autowired
    public void setJdbcTemplate(YukonJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Autowired
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }    
}
