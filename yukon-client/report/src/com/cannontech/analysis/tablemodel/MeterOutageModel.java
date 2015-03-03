package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.impl.MeterRowMapper;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.util.NaturalOrderComparator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;

public class MeterOutageModel extends FilteredReportModelBase<MeterOutageModel.MeterOutageRow> implements Comparator<MeterOutageModel.MeterOutageRow> {

    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private MeterRowMapper meterRowMapper;

    public enum MeterOutageOrderByFilter {
        DEVICE_NAME("Device Name"),
        METER_NUMBER("Meter Number"),
        ROUTE_NAME("Route Name"),
        TIMESTAMP("Timestamp"),
        DURATION("Duration"),
        ;
        
        private String displayName;
        MeterOutageOrderByFilter (String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    private Logger log = YukonLogManager.getLogger(MeterOutageModel.class);

    /** A string for the title of the data */
    private static String title = "Outage Report";
    
    /** Class fields */
    private List<MeterOutageRow> data = new ArrayList<MeterOutageRow>();
    private Attribute attribute = BuiltInAttribute.OUTAGE_LOG;
    private int minOutageSecs = 0;
    private MeterOutageOrderByFilter orderBy = MeterOutageOrderByFilter.DEVICE_NAME;
    private Order orderDirection = Order.FORWARD;


    static public class MeterOutageRow {
        public String deviceName;
        public String meterNumber;
        public String address;
        public String deviceType;
        public String routeName;
        public Date timestamp;
        public String duration;

        public void loadData(YukonMeter meter, PointValueQualityHolder pointValue) {
            deviceName = meter.getName();
            meterNumber =meter.getMeterNumber();
            address = meter.getSerialOrAddress();
            deviceType = meter.getPaoType().getPaoTypeName();
            routeName = meter.getRoute();
            timestamp = pointValue.getPointDataTimeStamp();
            duration = TimeUtil.convertSecondsToTimeString(pointValue.getValue());
        }
    }

    @Override
    protected MeterOutageRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }
    
    @Override
    protected Class<MeterOutageRow> getRowClass() {
        return MeterOutageRow.class;
    }
    
    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void doLoadData() {
        Iterable<? extends YukonPao> devices = getYukonPaoList();
        List<YukonMeter> meters = getMetersForYukonPaos(devices);
        Range<Date> dateRange = new Range<Date>( getStartDate(), true, getStopDate(), true);
        ListMultimap<PaoIdentifier, PointValueQualityHolder> intermediateResults;
        intermediateResults = rawPointHistoryDao.getAttributeData(meters, attribute,false, 
        		dateRange.translate(CtiUtilities.INSTANT_FROM_DATE), getOrderDirection(), null);

        for (YukonMeter meter : meters) {
            List<PointValueQualityHolder> values = intermediateResults.get(meter.getPaoIdentifier());
            for (PointValueQualityHolder pointValueHolder : values) {
                
                if (isIncluded(pointValueHolder.getValue())) {    //limit by minimum value filter; COULD BE DONE IN SQL but...
                    MeterOutageRow meterOutage = new MeterOutageRow();
                    meterOutage.loadData(meter, pointValueHolder);   //This probably duplicates the memory footprint, need to cleanup.
                    data.add(meterOutage);
                }
            }
        }

        //Order the records
        Collections.sort(data, this);
        
        log.info("Report Records Collected from Database: " + data.size());
    }
    
    private <I extends YukonPao> List<YukonMeter> getMetersForYukonPaos(Iterable<I> identifiers) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(yukonJdbcTemplate);

        SqlFragmentGenerator<I> sqlGenerator = new SqlFragmentGenerator<I>() {
            @Override
            public SqlFragmentSource generate(List<I> subList) {
                ImmutableList<Integer> paoIdList = PaoUtils.asPaoIdList(subList);
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append(meterRowMapper.getSql());
                sql.append("WHERE ypo.paObjectId").in(paoIdList);
                return sql;
            }
        };


        List<YukonMeter> result = template.query(sqlGenerator, identifiers, meterRowMapper);
        return Collections.unmodifiableList(result);
    }
    
    @Override
    public int compare(MeterOutageRow o1, MeterOutageRow o2) {

        if (orderBy == MeterOutageOrderByFilter.METER_NUMBER) {
            NaturalOrderComparator noComp = new NaturalOrderComparator();
            return noComp.compare(o1.meterNumber, o2.meterNumber);
        }

        if (orderBy == MeterOutageOrderByFilter.ROUTE_NAME) {
            return o1.routeName.compareToIgnoreCase(o2.routeName);
        } 

        if (orderBy == MeterOutageOrderByFilter.TIMESTAMP) {
            return o1.timestamp.compareTo(o2.timestamp);
        }

        if (orderBy == MeterOutageOrderByFilter.DURATION) {
            return o1.duration.compareTo(o2.duration);
        }

        return o1.deviceName.compareToIgnoreCase(o2.deviceName);
    }


    private boolean isIncluded(double value) {
        return value >= getMinOutageSecs();
    }

    
    public void setMinOutageSecs(int minOutageSecs) {
        this.minOutageSecs = minOutageSecs;
    }
    
    public int getMinOutageSecs() {
        return minOutageSecs;
    }
    
    public void setOrderBy(MeterOutageOrderByFilter orderBy) {
        this.orderBy = orderBy;
    }
    
    public void setOrderDirection(Order orderDirection) {
        this.orderDirection = orderDirection;
    }
    
    public Order getOrderDirection() {
        return orderDirection;
    }
}
