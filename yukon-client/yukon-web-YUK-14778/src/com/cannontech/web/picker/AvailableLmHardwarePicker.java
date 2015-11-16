package com.cannontech.web.picker;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.inventory.HardwareClass;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.hardware.dao.AvailableLmHardwareFilter;
import com.cannontech.stars.dr.hardware.dao.DisplayableLmHardwareRowMapper;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class AvailableLmHardwarePicker extends DatabasePicker<DisplayableLmHardware> {
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private ECMappingDao ecMappingDao;

    private HardwareClass hardwareClass;
    
    private final static String[] searchColumnNames = new String[] {"lmhw.manufacturerSerialNumber"};
    private final static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.picker.thermostat.";

        columns.add(new OutputColumn("serialNumber", titleKeyPrefix + "serialNumber"));
        columns.add(new OutputColumn("deviceType", titleKeyPrefix + "deviceType"));

        outputColumns = Collections.unmodifiableList(columns);
    }

    public AvailableLmHardwarePicker() {
        super(new DisplayableLmHardwareRowMapper(), searchColumnNames);
    }

    @Override
    protected void updateFilters(List<SqlFilter> sqlFilters,
            List<PostProcessingFilter<DisplayableLmHardware>> postProcessingFilters, String extraArgs,
            YukonUserContext userContext) {
        if (extraArgs != null) {
            int energyCompanyId = NumberUtils.toInt(extraArgs);
            
            // gather parents energyCompanyIds
            EnergyCompany energyCompany = ecDao.getEnergyCompany(energyCompanyId);
            List<Integer> parentIds = Lists.transform(energyCompany.getAncestors(true), EnergyCompanyDao.TO_ID_FUNCTION);
            AvailableLmHardwareFilter energyCompanyIdsFilter = new AvailableLmHardwareFilter(parentIds, hardwareClass);
            sqlFilters.add(energyCompanyIdsFilter);
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
    
    public void setHardwareClass(HardwareClass hardwareClass) {
        this.hardwareClass = hardwareClass;
    }
}
