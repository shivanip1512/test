package com.cannontech.web.picker.v2;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.hardware.dao.AvailableZigbeeDeviceOnAccountFilter;
import com.cannontech.stars.dr.hardware.dao.DisplayableLmHardwareRowMapper;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class AvailableZigbeeDevicePicker extends DatabasePicker<DisplayableLmHardware> {
    
    
    private final static String[] searchColumnNames = new String[] {"lmhw.manufacturerSerialNumber"};
    private final static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.picker.zigbee.";

        columns.add(new OutputColumn("serialNumber", titleKeyPrefix + "serialNumber"));
        columns.add(new OutputColumn("deviceType", titleKeyPrefix + "deviceType"));

        outputColumns = Collections.unmodifiableList(columns);
    }

    public AvailableZigbeeDevicePicker() {
        super(new DisplayableLmHardwareRowMapper(), searchColumnNames);
    }

    @Override
    protected void updateFilters(
            List<SqlFilter> sqlFilters,
            List<PostProcessingFilter<DisplayableLmHardware>> postProcessingFilters,
            String extraArgs, YukonUserContext userContext) {
        if (extraArgs != null) {
            int accountId = NumberUtils.toInt(extraArgs);
            
            AvailableZigbeeDeviceOnAccountFilter filter =
                new AvailableZigbeeDeviceOnAccountFilter(accountId);
            sqlFilters.add(filter);
        }
    }

    @Override
    public String getIdFieldName() {
        return "inventoryId";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }
}