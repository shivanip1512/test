package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.device.definition.model.PaoPointIdentifier;
import com.cannontech.common.device.definition.model.PointIdentifier;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoCollections;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.point.PointTypes;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;

public class DeviceReadStatisticsSummaryModel extends BareDatedReportModelBase<DeviceReadStatisticsSummaryModel.ModelRow> {

    private Logger log = YukonLogManager.getLogger(DeviceReadStatisticsSummaryModel.class);

    // dependencies
    private SimpleJdbcOperations simpleJdbcTemplate;
    private AttributeService attributeService;
    
    // member variables
    private String title = "Device Read Statistics Summary Report (31 day)";
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private Attribute attribute;
    private List<String> groupNames;
    private DeviceGroupService deviceGroupService;
    private DateTime lastMonth;

    static public class ModelRow {
        public String groupName;
        public Integer deviceCount;
        public Integer devicesWithReads;
        public Double readPercent;
    }
    
    static public class GroupResultsModelRow {
        public String deviceName;
        public int reads;
    }

    @Override
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }

    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        Set<? extends DeviceGroup> groups = deviceGroupService.resolveGroupNames(groupNames);
        for(DeviceGroup group : groups) {
            Set<SimpleDevice> devicesInGroup = deviceGroupService.getDevices(Collections.singleton(group));
            List<GroupResultsModelRow> groupResultRows = getGroupResultRows(devicesInGroup);
            DeviceReadStatisticsSummaryModel.ModelRow groupRow = buildModelRow(group.getFullName(), deviceGroupService.getDeviceCount(Collections.singleton(group)), groupResultRows);
            data.add(groupRow);
        }
        /* Do total line */
        Set<SimpleDevice> devicesInGroup = deviceGroupService.getDevices(groups);
        List<GroupResultsModelRow> groupResultRows = getGroupResultRows(devicesInGroup);
        
        DeviceReadStatisticsSummaryModel.ModelRow groupRow = buildModelRow("Total For All Devices In Selected Groups", deviceGroupService.getDeviceCount(groups), groupResultRows);
        data.add(groupRow);
        
        log.info("Report Records Collected from Database: " + data.size());
    }

    private DeviceReadStatisticsSummaryModel.ModelRow buildModelRow(String name, int deviceCount, List<GroupResultsModelRow> rows) {
        int successCount = 0;
        for(GroupResultsModelRow row : rows) {
            if(row.reads > 0){
                successCount++;
            }
        }
        double successPercent = (new Double(successCount) / new Double(deviceCount));
        DeviceReadStatisticsSummaryModel.ModelRow groupRow = new DeviceReadStatisticsSummaryModel.ModelRow();
        groupRow.groupName = name;
        groupRow.deviceCount = deviceCount;
        groupRow.devicesWithReads = successCount;
        groupRow.readPercent = successPercent;
        return groupRow;
    }

    private List<GroupResultsModelRow> getGroupResultRows(Set<SimpleDevice> devices){
        List<PaoPointIdentifier> identifiers = Lists.newArrayList();
        for(SimpleDevice device : devices) {
            try {
                PaoPointIdentifier identifier = attributeService.getPaoPointIdentifierForAttribute(device, attribute);
                identifiers.add(identifier);
            } catch (IllegalArgumentException e) {
                continue;  /* This device does not support the choosen attribute. */
            }
        }
        ImmutableMultimap<PointIdentifier, PaoIdentifier> paoPointIdentifiersMap = PaoCollections.mapPaoPointIdentifiers(identifiers);
        
        List<GroupResultsModelRow> groupResultRows = Lists.newArrayList();
        
        for(final PointIdentifier pointIdentifier : paoPointIdentifiersMap.keySet()){
            List<Integer> deviceIds = Lists.newArrayList();
            for(PaoIdentifier paoIdentifier :  paoPointIdentifiersMap.get(pointIdentifier)){
                deviceIds.add(paoIdentifier.getPaoId());
            }

            ChunkingSqlTemplate<Integer> template = new ChunkingSqlTemplate<Integer>(simpleJdbcTemplate);

            SqlFragmentGenerator<Integer> gen = new SqlFragmentGenerator<Integer>() {
                @Override
                public SqlFragmentSource generate(List<Integer> subList) {
                    SqlStatementBuilder sql = new SqlStatementBuilder();
                    sql.append("select ypo.PAOName deviceName");
                    sql.append(", COUNT(rph.changeId) reads");
                    sql.append("from YukonPAObject ypo");
                    sql.append("  left outer join Point p on p.PAObjectID = ypo.PAObjectID and p.POINTOFFSET = ");
                    sql.appendArgument(pointIdentifier.getOffset()).append(" and p.POINTTYPE = ").appendArgument(PointTypes.getType(pointIdentifier.getType()));
                    sql.append("  left outer join RAWPOINTHISTORY rph on rph.POINTID = p.POINTID and rph.TIMESTAMP >= ").appendArgument(lastMonth.toDate());
                    sql.append("where ypo.PAObjectID in ( ").appendArgumentList(subList).append(" ) "); 
                    sql.append("group by ypo.PAOName");
                    return sql;
                }
            };
            
            List<GroupResultsModelRow> rows = template.query(gen, deviceIds, new ParameterizedRowMapper<GroupResultsModelRow>() {
                @Override
                public GroupResultsModelRow mapRow(ResultSet rs, int rowNum) throws SQLException {
                    DeviceReadStatisticsSummaryModel.GroupResultsModelRow row = new DeviceReadStatisticsSummaryModel.GroupResultsModelRow();
                    row.deviceName = rs.getString("deviceName");
                    row.reads = rs.getInt("reads");
                    return row;
                }
            });
            groupResultRows.addAll(rows);
        }
        return groupResultRows;
    }
    
    public void setLastMonthDate(DateTime lastMonth) {
        this.lastMonth = lastMonth;
    }
    
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setGroupsFilter(List<String> groupNames) {
        this.groupNames = groupNames;
    }

    @Required
    public void setSimpleJdbcTemplate(SimpleJdbcOperations simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    @Required
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @Required
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }

    @Override
    public boolean useStartDate(){
        return false;
    }
}