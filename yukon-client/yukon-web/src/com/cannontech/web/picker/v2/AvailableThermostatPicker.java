package com.cannontech.web.picker.v2;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.inventory.LMHardwareClass;
import com.cannontech.core.roleproperties.YukonEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.service.EnergyCompanyService;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.hardware.dao.AvailableLmHardwareFilter;
import com.cannontech.stars.dr.hardware.dao.DisplayableLmHardwareRowMapper;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class AvailableThermostatPicker extends DatabasePicker<DisplayableLmHardware> {
    
    private EnergyCompanyService energyCompanyService;
    
    private final static String[] searchColumnNames = new String[] {"lmhw.manufacturerSerialNumber"};
    private final static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.picker.thermostat.";

        columns.add(new OutputColumn("serialNumber", titleKeyPrefix + "serialNumber"));
        columns.add(new OutputColumn("deviceType", titleKeyPrefix + "deviceType"));

        outputColumns = Collections.unmodifiableList(columns);
    }

    public AvailableThermostatPicker() {
        super(new DisplayableLmHardwareRowMapper(), searchColumnNames);
    }

    @Override
    protected void updateFilters(
            List<SqlFilter> sqlFilters,
            List<PostProcessingFilter<DisplayableLmHardware>> postProcessingFilters,
            String extraArgs, YukonUserContext userContext) {
        if (extraArgs != null) {
            int energyCompanyId = NumberUtils.toInt(extraArgs);
            
            // gather parents energyCompanyIds
            List<YukonEnergyCompany> parentEnergyCompanies = 
                energyCompanyService.getAccessibleParentEnergyCompanies(energyCompanyId);
            List<Integer> energyCompanyIds =
                Lists.transform(parentEnergyCompanies, LiteStarsEnergyCompany.getEnergyCompanyToEnergyCompanyIdsFunction());
            
            AvailableLmHardwareFilter energyCompanyIdsFilter =
                new AvailableLmHardwareFilter(energyCompanyIds,
                                              LMHardwareClass.THERMOSTAT);
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
    
    // DI Setters
    @Autowired
    public void setEnergyCompanyService(EnergyCompanyService energyCompanyService) {
        this.energyCompanyService = energyCompanyService;
    }

}