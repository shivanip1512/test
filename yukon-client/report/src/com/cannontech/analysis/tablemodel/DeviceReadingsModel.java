package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.device.definition.model.PaoPointIdentifier;
import com.cannontech.common.device.definition.model.PointIdentifier;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoCollections;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dynamic.impl.SimplePointValue;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;

public class DeviceReadingsModel extends BareDatedReportModelBase<DeviceReadingsModel.ModelRow> {
    
    private Logger log = YukonLogManager.getLogger(DeviceReadingsModel.class);
    
    // dependencies
    private SimpleJdbcOperations simpleJdbcTemplate;
    private AttributeService attributeService;
    
    // member variables
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private Attribute attribute;
    private List<String> groupsFilter;
    private List<String> deviceFilter;

    private DeviceGroupService deviceGroupService;

    private DeviceGroupEditorDao deviceGroupEditorDao;

    private boolean getAll = true;

    private DeviceDao deviceDao;

    private PointFormattingService pointFormattingService;

    private YukonUserContext yukonUserContext;

    static public class ModelRow {
        public String deviceName;
        public String type;
        public String date;
        public String value;
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
        return "Device Readings Report";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        List<SimpleDevice> devices = getDeviceList();
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
        
        for(final PointIdentifier pointIdentifier : paoPointIdentifiersMap.keySet()){
            final String sql = buildSQLStatement(pointIdentifier);
            List<Integer> deviceIds = Lists.newArrayList();
            for(PaoIdentifier paoIdentifier :  paoPointIdentifiersMap.get(pointIdentifier)){
                deviceIds.add(paoIdentifier.getPaoId());
            }
            
            ChunkingSqlTemplate<Integer> template = new ChunkingSqlTemplate<Integer>(simpleJdbcTemplate);
            
            SqlFragmentGenerator<Integer> gen = new SqlFragmentGenerator<Integer>() {
                @Override
                public SqlFragmentSource generate(List<Integer> subList) {
                    SqlStatementBuilder sqlBuilder = new SqlStatementBuilder(sql);
                    if(getAll){
                        sqlBuilder.append("and rph.Timestamp > ").appendArgument(getStartDate()).append(" and rph.Timestamp <= ").appendArgument(getStopDate());
                        sqlBuilder.append("and yp.PAObjectID in (");
                        sqlBuilder.appendArgumentList(subList);
                        sqlBuilder.append(") order by deviceName, date desc");
                    }else {
                        sqlBuilder.append("and yp.PAObjectID in (");
                        sqlBuilder.appendArgumentList(subList);
                        sqlBuilder.append(") group by yp.PAOName, p.PointType, p.PointId");
                        sqlBuilder.append(") holderTable,");
                        sqlBuilder.append("(select DISTINCT yp.PAOName deviceName, yp.Type type, rph.Value value,");
                        sqlBuilder.append("p.PointType pointType, p.PointId pointId,");
                        sqlBuilder.append("rph.Timestamp date from YukonPAObject yp");
                        sqlBuilder.append("join Point p on p.PAObjectID = yp.PAObjectID");
                        sqlBuilder.append("join RawPointHistory rph on p.PointId = rph.PointId");
                        sqlBuilder.append("where p.PointOffset = " + pointIdentifier.getOffset() + " and p.PointType = '" + PointTypes.getType(pointIdentifier.getType()) + "'");
                        sqlBuilder.append(") attributeValues");
                        sqlBuilder.append("where holderTable.deviceName = attributeValues.deviceName"); 
                        sqlBuilder.append("and holderTable.date = attributeValues.date;");
                    }
                    return sqlBuilder;
                }
            };
            
            List<ModelRow> rows = template.query(gen, deviceIds, new ParameterizedRowMapper<ModelRow>() {
                @Override
                public ModelRow mapRow(ResultSet rs, int rowNum) throws SQLException {
                    DeviceReadingsModel.ModelRow row = new DeviceReadingsModel.ModelRow();
                    row.deviceName = rs.getString("deviceName");
                    row.type = rs.getString("type");
                    String value = rs.getString("value"); 
                    String pointtype = rs.getString("pointType");
                    Double doubleValue = Double.parseDouble(value);
                    row.date = rs.getString("date");
                    
                    SimplePointValue pvh = new SimplePointValue(rs.getInt("pointId"), rs.getDate("date"), PointTypes.getType(pointtype), doubleValue.doubleValue());
                    row.value = pointFormattingService.getValueString(pvh, Format.SHORT, yukonUserContext);
                    return row;
                }
            });
            data.addAll(rows);
        }
            
        log.info("Report Records Collected from Database: " + data.size());
    }
    
    private String buildSQLStatement(PointIdentifier pointIdentifier) {
        if(getAll){
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("select distinct yp.PAOName deviceName, yp.Type type, rph.Value value,");
            sql.append("rph.Timestamp date, p.PointType pointType, p.PointId pointId from YukonPAObject yp");
            sql.append("join Point p on p.PAObjectID = yp.PAObjectID");
            sql.append("join RawPointHistory rph on p.PointId = rph.PointId");
            sql.append("where p.PointOffset = " + pointIdentifier.getOffset() + " and p.PointType = '" + PointTypes.getType(pointIdentifier.getType()) + "'");
            return sql.toString();
        } else {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("select attributeValues.* from (");
            sql.append("select distinct yp.PAOName deviceName, p.PointType pointType, p.PointId pointId,");
            sql.append("max(rph.Timestamp) date from YukonPAObject yp");
            sql.append("join Point p on p.PAObjectID = yp.PAObjectID ");
            sql.append("join RawPointHistory rph on p.PointId = rph.PointId");
            sql.append("where p.PointOffset = " + pointIdentifier.getOffset() + " and p.PointType = '" + PointTypes.getType(pointIdentifier.getType()) + "'");
            return sql.toString();
        }
    }
    
    private List<SimpleDevice> getDeviceList() {
        if(groupsFilter != null && !groupsFilter.isEmpty()){
            List<StoredDeviceGroup> groups = Lists.newArrayList();
            for(String groupName : groupsFilter){
                StoredDeviceGroup group = deviceGroupEditorDao.getStoredGroup(groupName, false);
                if(group != null){
                    groups.add(group);
                }
            }
            return Lists.newArrayList(deviceGroupService.getDevices(groups));
        } else if (deviceFilter != null && !deviceFilter.isEmpty()) {
            List<SimpleDevice> devices = Lists.newArrayList();
            for(String deviceName : deviceFilter){
                try {
                    devices.add(deviceDao.getYukonDeviceObjectByName(deviceName));
                } catch (DataAccessException e) {
                    log.error("Unable to find device with name: " + deviceName + ". This device will be skipped.");
                    continue;
                }
            }
            return devices;
        } else {
            /* If they didn't pick anything to filter on, assume all devices. */
            /* Use contents of SystemGroupEnum.DEVICETYPES. */
            StoredDeviceGroup group = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.DEVICETYPES);
            return Lists.newArrayList(deviceGroupService.getDevices(Collections.singletonList(group)));
        }
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setRetrieveAll(boolean all) {
        this.getAll = all;
    }
    
    public void setGroupsFilter(List<String> namesList) {
        this.groupsFilter = namesList;
    }
    
    public void setDeviceFilter(List<String> idsSet) {
        this.deviceFilter = idsSet;
    }
    
    public void setYukonUserContext(YukonUserContext context){
        this.yukonUserContext = context;
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
    
    @Required
    public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }
    
    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
    
    @Required
    public void setPointFormattingService(PointFormattingService pointFormattingService){
        this.pointFormattingService = pointFormattingService;
    }
}