package com.cannontech.analysis.tablemodel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.db.point.stategroup.Disconnect410State;
import com.cannontech.database.db.point.stategroup.RfnDisconnectStatusState;
import com.cannontech.util.NaturalOrderComparator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;

public class DisconnectModel extends FilteredReportModelBase<DisconnectModel.DisconnectRow> implements Comparator<DisconnectModel.DisconnectRow> {

    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private PointDao pointDao;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;

    public enum DiscStatusStateFilter {
        CONNECTED_STATE("Show All Connected"),
        DISCONNECTED_STATE("Show All Disconnected"),
        ALL_STATES("Show All"),
        ;
        private final String displayName;
        DiscStatusStateFilter (String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }

    public enum DiscStatusOrderByFilter {
        DEVICE_NAME("Device Name"),
        ROUTE_NAME("Route Name"),
        METER_NUMBER("Meter Number"),
        TIMESTAMP("Timestamp"),
        STATE("Disconnect State"),
        ;
        
        private final String displayName;
        DiscStatusOrderByFilter (String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    private final Logger log = YukonLogManager.getLogger(DisconnectModel.class);

	/** A string for the title of the data */
	private static String title = "Disconnect State Report";
    
    /** Class fields */
    private final List<DisconnectRow> data = new ArrayList<DisconnectRow>();
    private final Attribute attribute = BuiltInAttribute.DISCONNECT_STATUS;
    private DiscStatusStateFilter discStateFilter = DiscStatusStateFilter.ALL_STATES;
    private DiscStatusOrderByFilter orderBy = DiscStatusOrderByFilter.DEVICE_NAME;
    private boolean showHistory = false;

    
    static public class DisconnectMeter implements YukonPao {
        public YukonMeter meter;
        public Integer discAddress;
        
        @Override
        public PaoIdentifier getPaoIdentifier() {
            return meter.getPaoIdentifier();
        }
    }
    
    static public class DisconnectRow {
        public String deviceName;
        public String meterNumber;
        public String address;
        public String deviceType;
        public String routeName;
        public Integer discAddress;
        public Date timestamp;
        public String state;

        public void loadData(DisconnectMeter disconnectMeter, PointValueQualityHolder pointValue, String stateText) {
            deviceName = disconnectMeter.meter.getName();
            meterNumber = disconnectMeter.meter.getMeterNumber();
            address = disconnectMeter.meter.getSerialOrAddress();
            deviceType = disconnectMeter.meter.getPaoType().getPaoTypeName();
            routeName = disconnectMeter.meter.getRoute();
            discAddress = disconnectMeter.discAddress;
            timestamp = pointValue.getPointDataTimeStamp();
            state = stateText;
        }
    }

    @Override
    protected DisconnectRow getRow(int rowIndex) {
    	return data.get(rowIndex);
    }
    
    @Override
    protected Class<DisconnectRow> getRowClass() {
    	return DisconnectRow.class;
    }
    
    @Override
    public int getRowCount() {
    	return data.size();
    }

    @Override
    public String getTitle() {
        if( discStateFilter == DiscStatusStateFilter.ALL_STATES) {
            title = "All Disconnect";
        } else if (discStateFilter == DiscStatusStateFilter.CONNECTED_STATE) {
            title = "Connected";
        } else if (discStateFilter == DiscStatusStateFilter.DISCONNECTED_STATE) {
            title = "Disconnected";
        }

        if (showHistory) {
            title += " - Meter History Report";
        } else {
            title += " - Current State Report";
        }

        return title;
    }
    
    @Override
    public void doLoadData() {
        Iterable<? extends YukonPao> devices = getYukonPaoList();
        List<DisconnectMeter> meters = getMetersForYukonPaos(devices);

        ListMultimap<PaoIdentifier, PointValueQualityHolder> intermediateResults;
        if (showHistory) {
            Range<Date> dateRange = new Range<Date>(getStartDate(), false, getStopDate(), true);
            intermediateResults =
                rawPointHistoryDao.getAttributeData(meters, attribute, false,
                    dateRange.translate(CtiUtilities.INSTANT_FROM_DATE), Order.FORWARD, null);
        } else {
            Range<Instant> instantRange =
                new Range<Date>(null, false, null, true).translate(CtiUtilities.INSTANT_FROM_DATE);
            intermediateResults =
                rawPointHistoryDao.getLimitedAttributeData(meters, attribute, 1, false, instantRange, Order.REVERSE,
                    null);
        }

        for (DisconnectMeter meter : meters) {
            List<PointValueQualityHolder> values = intermediateResults.get(meter.getPaoIdentifier());
            for (PointValueQualityHolder pointValueHolder : values) {

                if (isIncluded((int)pointValueHolder.getValue())) {    //limit by disconnectStatus filter
                    DisconnectRow disconnect = new DisconnectRow();
            	    LitePoint litePoint = pointDao.getLitePoint(pointValueHolder.getId()); // THIS HITS THE DB!!!! If showHistory, may be cheaper outside the values for loop
            	    LiteState liteState = stateGroupDao.findLiteState(litePoint.getStateGroupID(), (int) pointValueHolder.getValue());
            	    disconnect.loadData(meter, pointValueHolder, liteState.getStateText());   //Adding meter again probably duplicates the memory footprint, need to cleanup.
            	    data.add(disconnect);
            	}
            }
        }

        //Order the records
        Collections.sort(data, this);
        
        log.info("Report Records Collected from Database: " + data.size());
    }
	
    private <I extends YukonPao> List<DisconnectMeter> getMetersForYukonPaos(Iterable<I> identifiers) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(yukonJdbcTemplate);

        SqlFragmentGenerator<I> sqlGenerator = new SqlFragmentGenerator<I>() {
            @Override
            public SqlFragmentSource generate(List<I> subList) {
                ImmutableList<Integer> paoIdList = PaoUtils.asPaoIdList(subList);
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ypo.paobjectId, ypo.paoName, ypo.type, ypo.disableFlag,");
                sql.append("dmg.meterNumber, dcs.address, dr.routeId, rypo.paoName as route,");
                sql.append("rfna.serialNumber, rfna.Manufacturer, rfna.Model, mct.disconnectAddress");
                sql.append("FROM YukonPaobject ypo");
                sql.append("JOIN Device d ON ypo.paObjectId = d.deviceId");
                sql.append("JOIN DeviceMeterGroup dmg ON d.deviceId = dmg.deviceId");
                sql.append("LEFT JOIN DeviceCarrierSettings dcs ON d.deviceId = dcs.deviceId");
                sql.append("LEFT JOIN DeviceRoutes dr ON d.deviceId = dr.deviceId");
                sql.append("LEFT JOIN YukonPaobject rypo ON dr.routeId = rypo.paObjectId");
                sql.append("LEFT JOIN RFNAddress rfna ON rfna.deviceId = d.deviceid");
                sql.append("LEFT JOIN DeviceMCT400Series mct ON mct.deviceId = d.deviceId");
                sql.append("WHERE ypo.paObjectId").in(paoIdList);
                return sql;
            }
        };

        YukonRowMapper<DisconnectMeter> rowMapper = new YukonRowMapper<DisconnectMeter>() {

            @Override
            public DisconnectMeter mapRow(YukonResultSet rs) throws SQLException {
                PaoIdentifier paoIdentifier = rs.getPaoIdentifier("paobjectId",  "type");

                
                String paoName = rs.getString("paoName");
                String meterNumber = rs.getString("meterNumber");
                boolean disabled = rs.getBoolean("disableFlag");
                YukonMeter yukonMeter;
                if (paoIdentifier.getPaoType().isRfn()) {
                    String serialNumber = rs.getString("serialNumber");
                    String manufacturer = rs.getString("manufacturer");
                    String model = rs.getString("model");
                    
                    RfnIdentifier rfnIdentifier = new RfnIdentifier(serialNumber, manufacturer, model);
                    yukonMeter = new RfnMeter(paoIdentifier, rfnIdentifier, meterNumber, paoName, disabled);
                } else {    //assume PLC
                    String address = rs.getString("address");
                    String routeName = rs.getString("route");
                    int routeId = rs.getInt("routeId");
                    yukonMeter = new PlcMeter(paoIdentifier, meterNumber, paoName, disabled, routeName, routeId, address);
                }
                
                DisconnectMeter discMeter = new DisconnectMeter();
                discMeter.meter = yukonMeter;
                discMeter.discAddress = rs.getNullableInt("disconnectAddress");
                return discMeter;
            }
        };

        List<DisconnectMeter> result = template.query(sqlGenerator, identifiers, rowMapper);
        return Collections.unmodifiableList(result);
    }
    
	/*
	 * When disconnectState is CONNECTED_STATE, 
	 *     return true for Disconnect410State values CONNECTED(1) or CONNECT_ARMED(3)
	 *     return true for RfnMeterDisconnectState values CONNECTED(1) or ARMED(3)
	 * When disconnectState is DISCONNECTED_STATE, 
	 *     return true for Disconnect410States values CONFIRMED_DISCONNECTED(0) or UNCONFIRMED_DISCONNECTED(2)
     *     return true for RfnMeterDisconnectState values UNKNOWN(0) or DISCONNECTED(2)
	 */
	private boolean isIncluded(int value) {

	    if (discStateFilter == DiscStatusStateFilter.DISCONNECTED_STATE) {
	        return value == Disconnect410State.CONFIRMED_DISCONNECTED.getRawState() || 
	                value == Disconnect410State.UNCONFIRMED_DISCONNECTED.getRawState() ||
	                value == RfnDisconnectStatusState.DISCONNECTED.getRawState() ||
	                value == RfnDisconnectStatusState.UNKNOWN.getRawState();
	    } else if (discStateFilter == DiscStatusStateFilter.CONNECTED_STATE) {
	        return value == Disconnect410State.CONNECTED.getRawState() || 
	                value == Disconnect410State.CONNECT_ARMED.getRawState() ||
	                value == RfnDisconnectStatusState.CONNECTED.getRawState() ||
	                value == RfnDisconnectStatusState.ARMED.getRawState();	               
	    } else if (discStateFilter == DiscStatusStateFilter.ALL_STATES) {
	        return true;
	    }
	    
	    return false;
	}
	
    @Override
    public int compare(DisconnectRow o1, DisconnectRow o2) {

        if (orderBy == DiscStatusOrderByFilter.ROUTE_NAME) {
            return o1.routeName.compareToIgnoreCase(o2.routeName);
        } 

        if (orderBy == DiscStatusOrderByFilter.METER_NUMBER) {
            NaturalOrderComparator noComp = new NaturalOrderComparator();
            return noComp.compare(o1.meterNumber, o2.meterNumber);
        }

        if (orderBy == DiscStatusOrderByFilter.TIMESTAMP) {
            return o1.timestamp.compareTo(o2.timestamp);
        }

        if (orderBy == DiscStatusOrderByFilter.STATE) {
            return o1.state.compareTo(o2.state);
        }

        return o1.deviceName.compareToIgnoreCase(o2.deviceName);
    }

    public void setDiscStateFilter(DiscStatusStateFilter discStateFilter) {
        this.discStateFilter = discStateFilter;
    }
    
    public void setOrderBy(DiscStatusOrderByFilter orderBy) {
        this.orderBy = orderBy;
    }
	
	public void setShowHistory(boolean showHistory) {
        this.showHistory = showHistory;
    }
	
	public boolean isShowHistory() {
        return showHistory;
    }
}
