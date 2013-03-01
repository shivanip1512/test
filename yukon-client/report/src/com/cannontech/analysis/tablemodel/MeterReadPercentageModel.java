package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.RowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MeterReadPercentageModel extends BareDatedReportModelBase<MeterReadPercentageModel.ModelRow> implements UserContextModelAttributes {
        
    public enum MeterReadPercentagePeriod {
        
        TWO_DAYS("2 days", 35), 
        SEVEN_DAYS("7 days", 52),
        MONTHLY("Monthly", 12);

        private String displayName;
        //number of rows to display
        int rowsToDisplay;

        private MeterReadPercentagePeriod (String displayName, int rowsToDisplay) {
            this.displayName = displayName;
            this.rowsToDisplay = rowsToDisplay;
        }

        public String getDisplayName() {
            return displayName;
        }
        
        public int getTotal() {
            return rowsToDisplay;
        }
    }
    
    static public class ModelRow {
        public String groupName;
        public Date startDate;
        public Date endDate;
        public Double readPercent = 0.0;
        public Integer countSuccessfull = 0;
        public Integer countMissed = 0;
        public Integer countTotal = 0;
        public Integer countDisabled = 0 ;
        public Integer countNoData = 0;
        public Integer countUnsupported = 0;
    }
        
    private class DateRange{

        private DateMidnight startDate;
        private DateMidnight stopDate;
        
        DateRange(DateMidnight startDate, DateMidnight stopDate){
            this.startDate = startDate;
            this.stopDate = stopDate;
        }
        
        public DateMidnight getStartDate() {
            return startDate;
        }

        public DateMidnight getStopDate() {
            return stopDate;
        } 
    }
    
    private Logger log = YukonLogManager.getLogger(MeterReadPercentageModel.class);
    
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private AttributeService attributeService;
    @Autowired private DeviceGroupService deviceGroupService;
    private List<String> groupNames;
    private YukonUserContext context;
    private MeterReadPercentagePeriod period = MeterReadPercentagePeriod.SEVEN_DAYS;
    protected List<ModelRow> data = new ArrayList<ModelRow>();
    
  
    @Override
    public void doLoadData() {
        List<DateRange> dateRanges = createDateRanges();
        
        Set<? extends DeviceGroup> groups = deviceGroupService.resolveGroupNames(groupNames);
        
        for(DeviceGroup group : groups) { 
            int deviceCount = deviceGroupService.getDeviceCount(Collections.singleton(group));
            if(deviceCount > 0){
                int countUnsupported = 0;
                Set<SimpleDevice> devicesInGroup = deviceGroupService.getDevices(Collections.singleton(group));
                //This list contains the device ids of the devices that support attribute "USAGE"
                Map<Integer, PaoPointIdentifier> deviceIdToPointIdentifier = Maps.newHashMap();
              
                for(SimpleDevice device : devicesInGroup) {
                    try {
                        PaoPointIdentifier identifier = attributeService.getPaoPointIdentifierForAttribute(device, BuiltInAttribute.USAGE);
                        deviceIdToPointIdentifier.put(device.getDeviceId(), identifier);
                    } catch (IllegalUseOfAttribute e) {
                        //This device does not support USAGE attribute.
                        countUnsupported++;
                        continue;  
                    }
                } ;
                log.info("Proccessing Device Group: "+group.getFullName()+ "  devices:"+deviceCount);
                
                final List<Integer> disabledDeviceIds = getDisabledDeviceIds(Lists.newArrayList(deviceIdToPointIdentifier.keySet()));
               //remove disabled devices from the list of devices
                removeDevices(deviceIdToPointIdentifier, disabledDeviceIds);
                
                int count = 0;
                
                //this map holds device ids of the devices that have no reads for the previous 10 years
                Map<Integer,Integer> noDataDeviceIds = Maps.newHashMap();
                for (DateRange range : dateRanges) {
                    ModelRow groupRow = new ModelRow();
                    groupRow.groupName = "#" + ++count + " "+group.getFullName();
                    groupRow.countUnsupported = countUnsupported;
                    groupRow.startDate = range.getStartDate().toDate();
                    groupRow.endDate = range.getStopDate().toDate();
                    groupRow.countTotal = deviceCount;
                    groupRow.countDisabled = disabledDeviceIds.size();
                    
                    // create a map devices that have reads
                    Map<Integer, PaoPointIdentifier> filteredDevices =
                        filterDevices(deviceIdToPointIdentifier, Lists.newArrayList(noDataDeviceIds.values()));

                    if (!filteredDevices.isEmpty()) {
                        // find devices with reads
                        List<Integer> successfulDeviceIds =
                            getSuccessfulDeviceIds(filteredDevices, range);
                        groupRow.countSuccessfull = successfulDeviceIds.size();

                        // remove devices that have successful reads
                         removeDevices(filteredDevices, successfulDeviceIds);

                        // The devices left in the map are either missed or have never been read
                        if (!filteredDevices.isEmpty()) {
                            DateMidnight stopDate = range.getStopDate();
                            DateMidnight startDate = stopDate.minusYears(10);
                            DateRange previousDateRange = new DateRange(startDate, stopDate);
                            // find that devices that were successfully read within the last 10 years
                            List<Integer> previouslySuccessfulDeviceIds =
                                getSuccessfulDeviceIds(filteredDevices, previousDateRange);
                            // count the successful devices as missed
                            groupRow.countMissed = previouslySuccessfulDeviceIds.size();
                            /*
                             * remove devices that were missed, so that only devices that have no data are
                             * left
                             */
                            removeDevices(filteredDevices, previouslySuccessfulDeviceIds);
                            // save the devices without data to be omitted from the next search
                            for (Integer deviceId : filteredDevices.keySet()) {
                                noDataDeviceIds.put(deviceId, deviceId);
                            }
                        }
                    }
                    groupRow.countNoData = noDataDeviceIds.size();
                          
                    groupRow.readPercent =
                        groupRow.countSuccessfull
                                / (deviceCount - groupRow.countUnsupported.doubleValue()
                                   - groupRow.countDisabled.doubleValue() - groupRow.countNoData
                                    .doubleValue());
                    data.add(groupRow);   
                }
                log.info("Proccessing Device Group: "+group.getFullName()+ " compeleted.");
            }
        }
        log.info("Proccessed "+groups.size() +" device groups");
    }
    
    /* This method removes objects from deviceIdToPointIdentifier which are listed in deviceIds*/
    private void removeDevices(Map<Integer, PaoPointIdentifier> deviceIdToPointIdentifier, List<Integer> deviceIds){
        Iterator<Entry<Integer, PaoPointIdentifier>> it = deviceIdToPointIdentifier.entrySet().iterator();
        while (it.hasNext()){
           Entry<Integer, PaoPointIdentifier> item = it.next();
           if(deviceIds.contains(item.getKey())){
               it.remove();
           }
        }
    }
    
    /* This method creates new map of deviceIdToPointIdentifier which does not include keys listed in deviceIds*/
    private Map<Integer, PaoPointIdentifier> filterDevices(Map<Integer, PaoPointIdentifier> deviceIdToPointIdentifier, List<Integer> deviceIds){
        Map<Integer, PaoPointIdentifier> filteredDevices = Maps.newHashMap();
        for(Integer deviceId:deviceIdToPointIdentifier.keySet()){
            if(!deviceIds.contains(deviceId)){
                filteredDevices.put(deviceId, deviceIdToPointIdentifier.get(deviceId));
            }
        }
        return filteredDevices;
    }

        
    private List<Integer> getSuccessfulDeviceIds(Map<Integer, PaoPointIdentifier> devices, final DateRange range){
       
        ImmutableMultimap<PointIdentifier, PaoIdentifier> paoPointIdentifiersMap = PaoUtils.mapPaoPointIdentifiers(new ArrayList<PaoPointIdentifier>(devices.values())); 
        List<Integer> ids = new ArrayList<>(paoPointIdentifiersMap.keys().size());
        
        for(final PointIdentifier pointIdentifier : paoPointIdentifiersMap.keySet()){
            List<Integer> deviceIds = Lists.newArrayList();
            for(PaoIdentifier paoIdentifier :  paoPointIdentifiersMap.get(pointIdentifier)){
                deviceIds.add(paoIdentifier.getPaoId());
            }
    
            ChunkingSqlTemplate template = new ChunkingSqlTemplate(yukonJdbcTemplate);
            SqlFragmentGenerator<Integer> gen = new SqlFragmentGenerator<Integer>() {
                @Override
                public SqlFragmentSource generate(List<Integer> subList) {
                    SqlStatementBuilder sql = new SqlStatementBuilder();
                    sql.append("select distinct(ypo.PAObjectID) ");
                    sql.append("from YukonPAObject ypo");
                    sql.append(" inner join Point p on p.PAObjectID = ypo.PAObjectID and p.POINTOFFSET = ");
                    sql.appendArgument(pointIdentifier.getOffset()).append(" and p.POINTTYPE = ")
                        .appendArgument(pointIdentifier.getPointType());
                    sql.append("  inner join RAWPOINTHISTORY rph on rph.POINTID = p.POINTID");
                    sql.append("and rph.quality").eq(PointQuality.Normal.getDatabaseRepresentation());
                    sql.append("and ypo.DisableFlag = 'N'");
                    sql.append("and rph.TIMESTAMP").gte(range.getStartDate());
                    //stop date should be included
                    sql.append("and rph.TIMESTAMP").lt(range.getStopDate().plusDays(1));
                    sql.append("where ypo.PAObjectID in ( ").appendArgumentList(subList).append(" ) ");
                    return sql;
                }
            };
            List<Integer> successfulDeviceIds = template.query(gen, deviceIds, RowMapper.INTEGER);
            ids.addAll(successfulDeviceIds);
        }
        return ids;
    }
       
    private List<Integer> getDisabledDeviceIds(List<Integer> deviceIds){
        
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(yukonJdbcTemplate);
        SqlFragmentGenerator<Integer> gen = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("select ypo.PAObjectID from YukonPAObject ypo");
                sql.append("where ypo.PAObjectID in ( ").appendArgumentList(subList).append(" ) ");
                sql.append("and ypo.DisableFlag = 'Y'");
                return sql;
            }
        };
        List<Integer> disabledDeviceIds = template.query(gen, deviceIds, RowMapper.INTEGER);
        return disabledDeviceIds;
    }
    
    /* This method creates date ranges for each row in the report and the date range for the report header*/
    private List<DateRange> createDateRanges() {
        List<DateRange> ranges = Lists.newArrayList();       
        DateMidnight stopDate = new DateMidnight(context.getJodaTimeZone()).minusDays(1);
        DateMidnight startDate = null;
        
        switch (period) {
        case TWO_DAYS:
            startDate = stopDate.minusDays(1);
            ranges.add(new DateRange(startDate, stopDate));
            for (int i = 0; i < period.getTotal() - 1; i++) {
                startDate = startDate.minusDays(1);
                stopDate = stopDate.minusDays(1);
                ranges.add(new DateRange(startDate, stopDate));
            }
            break;
        case SEVEN_DAYS:
            startDate = stopDate.minusDays(6);
            ranges.add(new DateRange(startDate, stopDate));
            for (int i = 0; i < period.getTotal() - 1; i++) {
                startDate = startDate.minusDays(7);
                stopDate = stopDate.minusDays(7);
                ranges.add(new DateRange(startDate, stopDate));
            }
            break;
        case MONTHLY:
            startDate = stopDate.dayOfMonth().withMinimumValue();
            ranges.add(new DateRange(startDate, stopDate));
            for (int i = 0; i < period.getTotal() - 1; i++) {
                startDate = startDate.minusMonths(1);
                stopDate = startDate.dayOfMonth().withMaximumValue();
                ranges.add(new DateRange(startDate, stopDate));
            }
            break;
        }
        DatedModelAttributes datedModel = (DatedModelAttributes) this;
        // date range for the report header
        datedModel.setStartDate(startDate.toDate());
        datedModel.setStopDate(new DateTime(new DateMidnight(context.getJodaTimeZone())).minusSeconds(1).toDate()); 
        return ranges;
    }
    
    public MeterReadPercentagePeriod getPeriod() {
        return period;
    }

    public void setPeriod(MeterReadPercentagePeriod period) {
        this.period = period;
    }
    
    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public String getTitle() {
        return "Successful Meter Reading % Report";
    }

    @Override
    public void setUserContext(YukonUserContext context) {
        this.context = context;
    }
    
    @Override
    public YukonUserContext getUserContext() {
        return context;
    }

    public void setGroupsFilter(List<String> groupNames) {
        this.groupNames = groupNames;
    }

    @Override
    public boolean useStartDate(){
        return false;
    }

    @Override
    public boolean useStopDate(){
        return false;
    }

    @Override
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }

    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }
}
