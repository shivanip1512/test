package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoCollections;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dynamic.PointValueBuilder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.CachingPointFormattingService;
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
            List<Integer> deviceIds = Lists.newArrayList();
            for(PaoIdentifier paoIdentifier :  paoPointIdentifiersMap.get(pointIdentifier)){
                deviceIds.add(paoIdentifier.getPaoId());
            }

            ChunkingSqlTemplate<Integer> template = new ChunkingSqlTemplate<Integer>(simpleJdbcTemplate);

            SqlFragmentGenerator<Integer> gen = new SqlFragmentGenerator<Integer>() {
                @Override
                public SqlFragmentSource generate(List<Integer> subList) {
                    SqlStatementBuilder sql = new SqlStatementBuilder();
                    if(getAll){
                        sql.append("select distinct yp.PAOName deviceName, yp.Type type, rph.Value value,");
                        sql.append("  rph.Timestamp dateTime, p.PointType pointType, p.PointId pointId ");
                        sql.append("from YukonPAObject yp");
                        sql.append("  join Point p on p.PAObjectID = yp.PAObjectID");
                        sql.append("  join RawPointHistory rph on p.PointId = rph.PointId");
                        sql.append("where p.PointOffset = ").appendArgument(pointIdentifier.getOffset());
                        sql.append("  and p.PointType = ").appendArgument(PointTypes.getType(pointIdentifier.getType()));
                        sql.append("  and rph.Timestamp > ").appendArgument(getStartDate());
                        sql.append("  and rph.Timestamp <= ").appendArgument(getStopDate());
                        sql.append("  and yp.PAObjectID in (").appendArgumentList(subList).append(") ");
                        sql.append("order by deviceName, dateTime desc");
                    }else {
                        sql.append("select distinct yp.PAOName deviceName, yp.Type type, rph.Value value,");
                        sql.append("    p.PointType pointType, p.PointId pointId, rph.Timestamp dateTime ");
                        sql.append("from (");
                        sql.append("  select p.PointId pointId, max(rph.Timestamp) dateTime"); 
                        sql.append("  from YukonPAObject yp");
                        sql.append("    join Point p on p.PAObjectID = yp.PAObjectID"); 
                        sql.append("    join RawPointHistory rph on p.PointId = rph.PointId");
                        sql.append("  where p.PointOffset = ").appendArgument(pointIdentifier.getOffset());
                        sql.append("    and p.PointType = ").appendArgument(PointTypes.getType(pointIdentifier.getType()));
                        sql.append("    and yp.PAObjectID in (").appendArgumentList(subList).append(")");
                        sql.append("  group by p.PointId");
                        sql.append(") lastReading");
                        sql.append("  join RawPointHistory rph on lastReading.pointId = rph.pointId and lastReading.dateTime = rph.TIMESTAMP");
                        sql.append("  join Point p on rph.POINTID = p.POINTID");
                        sql.append("  join YukonPAObject yp on yp.PAObjectID = p.PAObjectID");
                    }
                    return sql;
                }
            };
            
            final CachingPointFormattingService cachingPointFormattingService = pointFormattingService.getCachedInstance();

            List<ModelRow> rows = template.query(gen, deviceIds, new ParameterizedRowMapper<ModelRow>() {
                @Override
                public ModelRow mapRow(ResultSet rs, int rowNum) throws SQLException {
                    DeviceReadingsModel.ModelRow row = new DeviceReadingsModel.ModelRow();
                    row.deviceName = rs.getString("deviceName");
                    row.type = rs.getString("type");
                    String value = rs.getString("value"); 
                    String pointtype = rs.getString("pointType");
                    Double doubleValue = Double.parseDouble(value);
                    row.date = rs.getString("dateTime");

                    PointValueBuilder builder = PointValueBuilder.create();
                    builder.withPointId(rs.getInt("pointId"));
                    builder.withTimeStamp(rs.getDate("dateTime"));
                    builder.withType(PointTypes.getType(pointtype));
                    builder.withValue(doubleValue.doubleValue());
                    PointValueQualityHolder pointValueQualityHolder = builder.build();
                    row.value = cachingPointFormattingService.getValueString(pointValueQualityHolder, Format.SHORT, yukonUserContext);
                    return row;
                }
            });
            data.addAll(rows);
        }

        log.info("Report Records Collected from Database: " + data.size());
    }


    private List<SimpleDevice> getDeviceList() {
        if (groupsFilter != null && !groupsFilter.isEmpty()) {
            Set<? extends DeviceGroup> groups = deviceGroupService.resolveGroupNames(groupsFilter);
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
            DeviceGroup group = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.DEVICETYPES);
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