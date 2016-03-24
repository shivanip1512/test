package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.activemq.openwire.v2.IntegerResponseMarshaller;
import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

public class MeterReadPercentageModel extends BareDatedReportModelBase<MeterReadPercentageModel.ModelRow> implements
        UserContextModelAttributes {
                                     
    public enum MeterReadPercentagePeriod {

        TWO_DAYS("2", 35),
        THREE_DAYS("3", 35),
        FOUR_DAYS("4", 35),
        FIVE_DAYS("5", 35),
        SIX_DAYS("6", 35),
        SEVEN_DAYS("7", 52),
        MONTHLY("Monthly", 12);

        private String displayName;
        private int rowsToDisplay;

        private MeterReadPercentagePeriod(String displayName, int rowsToDisplay) {
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
        public Integer countSuccessful = 0;
        public Integer countMissed = 0;
        public Integer countTotal = 0;
        public Integer countDisabled = 0;
        public Integer countNoData = 0;
        public Integer countUnsupported = 0;
    }

    private class DateRange {

        private DateMidnight startDate;
        private DateMidnight stopDate;

        DateRange(DateMidnight startDate, DateMidnight stopDate) {
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
    @Autowired private PaoDefinitionService paoDefinitionService;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PaoDao paoDao;
    private List<String> groupNames;
    private YukonUserContext context;
    private MeterReadPercentagePeriod period;
    protected List<ModelRow> data = new ArrayList<ModelRow>();
    private final static Set<? extends Attribute> usageAttributes =
            ImmutableSet.of(BuiltInAttribute.USAGE, BuiltInAttribute.USAGE_WATER);
   
    @Override
    public void doLoadData() {
        List<DateRange> dateRanges = createDateRanges();

        Set<? extends DeviceGroup> groups = deviceGroupService.resolveGroupNames(groupNames);

        Multimap<PaoType, Attribute> allDefinedAttributes = paoDefinitionDao.getPaoTypeAttributesMultiMap();
        Multimap<Attribute, PaoType> dest = HashMultimap.create();
        Multimaps.invertFrom(allDefinedAttributes, dest);
        Collection<PaoType> possiblePaoTypes = new ArrayList<>();
        for(Attribute attribute: usageAttributes){
            possiblePaoTypes.addAll(dest.get(attribute));
        }
        
        for (DeviceGroup group : groups) {
            // Get list of possible PAO types in the group we are using. This will reduce the
            // number of queries later.
            Collection<PaoType> actualPaoTypes = paoDefinitionService.findListOfPaoTypesInGroup(group, possiblePaoTypes);
            Map<PaoType, Attribute> paoTypeToAttribute = new HashMap<>();
            for (PaoType paoType : actualPaoTypes) {  
                //lookup the usage attribute for the paoType
                Set<Attribute> availAttributes = new HashSet<Attribute>(allDefinedAttributes.get(paoType));
                Attribute usageAttribute = Sets.intersection(availAttributes, usageAttributes).iterator().next();
                paoTypeToAttribute.put(paoType, usageAttribute);
            }
            
            int deviceCount = deviceGroupService.getDeviceCount(Collections.singleton(group));
            if (deviceCount > 0) {

                // Find the count of devices that are Unsupported but are not disabled. This code
                // assumes unsupported and disabled devices
                // will be counted in the disabled list not the unsupported list.
                int unsupportedCount = getUnsupportedDeviceCount(group, actualPaoTypes);

                // As noted above, this has no restrictions so every other list must remove disabled
                // devices from their counts.
                int disabledCount = paoDao.getDisabledDeviceCount(group);

                // find that devices that were successfully read within the last 10 years
                // This must cover a wider time range than the report or counts will be wrong and can go negative.
                DateMidnight tenYrStopDate = new DateMidnight(context.getJodaTimeZone()).plusDays(1);
                DateMidnight tenYrStartDate = tenYrStopDate.minusYears(10);
                DateRange tenYrDateRange = new DateRange(tenYrStartDate, tenYrStopDate);

                int successTenYearCount = getSuccessfulDeviceCount(paoTypeToAttribute, group, actualPaoTypes, tenYrDateRange);

                for (DateRange range : dateRanges) {
                    int successCount = getSuccessfulDeviceCount(paoTypeToAttribute, group, actualPaoTypes, range);
                    ModelRow groupRow = new ModelRow();
                    groupRow.groupName = group.getFullName();
                    groupRow.countUnsupported = unsupportedCount;
                    groupRow.startDate = range.getStartDate().toDate();
                    groupRow.endDate = range.getStopDate().toDate();
                    groupRow.countTotal = deviceCount;
                    groupRow.countDisabled = disabledCount;
                    groupRow.countSuccessful = successCount;
                    groupRow.countNoData = deviceCount - successTenYearCount - unsupportedCount - disabledCount;
                    groupRow.countMissed = deviceCount - unsupportedCount - disabledCount - groupRow.countNoData
                                           - successCount;
                    groupRow.readPercent = calculateReadPercent(groupRow);
                    data.add(groupRow);

                }
                log.info("Proccessing Device Group: " + group.getFullName() + " compeleted.");
            }
        }
        log.info("Proccessed " + groups.size() + " device groups");
    }
    
    /**
     * This method returns unsupported device count
     * 
     * @param group
     * @param actualPaoTypes
     * @return
     */
    private int getUnsupportedDeviceCount(DeviceGroup group, Collection<PaoType> actualPaoTypes) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select count(PAObjectID) from YukonPAObject ypo");
        sql.append("where ypo.DisableFlag = 'N'");
        SqlFragmentSource groupSqlWhereClause =
            deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(group),
                                                            "YPO.paObjectId");
        sql.append("AND").appendFragment(groupSqlWhereClause);
        sql.append("AND ypo.type").notIn(actualPaoTypes);

        return yukonJdbcTemplate.queryForInt(sql);
    }

    /**
     * This method returns successful device count
     * 
     * @param group
     * @param actualPaoTypes
     * @param dateRange
     * @return
     */
    private int getSuccessfulDeviceCount(Map<PaoType, Attribute> paoTypeToAttribute,
                                         DeviceGroup group, Collection<PaoType> actualPaoTypes,
                                         DateRange dateRange) {
        int successCount = 0;
        for (PaoType paoType : actualPaoTypes) {           
            PaoTypePointIdentifier paoTypePointIdentifier =
                attributeService.getPaoTypePointIdentifierForAttribute(paoType, paoTypeToAttribute.get(paoType));
            SqlStatementBuilder sql = new SqlStatementBuilder();
            // In the end all we care about is the count.
            sql.append("select count(PAObjectID)");
            sql.append("from (");
            // This gets us all distinct PAO's with normal quality, not disabled, with a particular
            // offset based on the attribute found above
            // and in the time range selected.
            sql.append("select distinct(ypo.PAObjectID) ");
            sql.append("from YukonPAObject ypo");
            sql.append("join Point p on p.PAObjectID = ypo.PAObjectID");
            sql.append("and p.POINTOFFSET").eq_k(paoTypePointIdentifier.getPointIdentifier().getOffset());
            sql.append("and p.POINTTYPE").eq_k(paoTypePointIdentifier.getPointIdentifier().getPointType());
            sql.append("  join RAWPOINTHISTORY rph on rph.POINTID = p.POINTID");
            sql.append("and rph.quality").eq(PointQuality.Normal.getDatabaseRepresentation());
            sql.append("and rph.TIMESTAMP").gt(dateRange.getStartDate());
            // stop date should be included
            sql.append("and rph.TIMESTAMP").lte(dateRange.getStopDate().plusDays(1));
            sql.append("where ypo.PAObjectID in (");
            // This is forcing the DB to find the list of devices for us. This loads all pao's of a
            // particular type from a particular group.
            sql.append("SELECT YPO.paobjectid");
            sql.append("FROM Device d");
            sql.append("JOIN YukonPaObject YPO ON (d.deviceid = YPO.paobjectid)");
            sql.append("WHERE YPO.type").eq(paoType);
            SqlFragmentSource groupSqlWhereClause =
                deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(group), "YPO.paObjectId");
            sql.append("AND").appendFragment(groupSqlWhereClause);
            sql.append("AND YPO.DisableFlag").eq_k(YNBoolean.NO);
            sql.append(")");
            sql.append(") as count");
            successCount += yukonJdbcTemplate.queryForInt(sql);
        }
        return successCount;
    }

    /**
     * 
     * @param This method calculates read percentage (successful/(total - unsupported - disabled - no data))
     * @return Returns 0 if (adjusted) total is 0.
     */
    private double calculateReadPercent(ModelRow groupRow) {
	    int totalCount = groupRow.countTotal - groupRow.countUnsupported - groupRow.countDisabled - groupRow.countNoData;
	    if (totalCount == 0) {
	    	return 0;
	    }
	    return groupRow.countSuccessful / (double) totalCount;
    }

    /**
     * This method creates date ranges for each row in the report and the date range for the report
     * header
     * @return
     */
    private List<DateRange> createDateRanges() {
        List<DateRange> ranges = Lists.newArrayList();
        DateMidnight stopDate = new DateMidnight(context.getJodaTimeZone()).minusDays(1);
        DateMidnight startDate = null;

        if (period == MeterReadPercentagePeriod.MONTHLY) {
            startDate = stopDate.dayOfMonth().withMinimumValue();
            ranges.add(new DateRange(startDate, stopDate));
            for (int i = 0; i < period.getTotal() - 1; i++) {
                startDate = startDate.minusMonths(1);
                stopDate = startDate.dayOfMonth().withMaximumValue();
                ranges.add(new DateRange(startDate, stopDate));
            }
        } else {
            Integer numDays = Integer.valueOf(period.getDisplayName());
            startDate = (stopDate.minusDays(numDays));
            ranges.add(new DateRange(startDate, stopDate));
            for (int i = 0; i < period.getTotal() - 1; i++) {
                stopDate = startDate;
                startDate = startDate.minusDays(numDays);
                ranges.add(new DateRange(startDate, stopDate));
            }
        }
		DatedModelAttributes datedModel = this;
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
    public boolean useStartDate() {
        return false;
    }

    @Override
    public boolean useStopDate() {
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
