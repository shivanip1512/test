package com.cannontech.web.picker.v2;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.inventory.LMHardwareClass;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.hardware.dao.AvailableLmHardwareFilter;
import com.cannontech.stars.dr.hardware.dao.DisplayableLmHardwareRowMapper;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class AvailableThermostatPicker extends DatabasePicker<DisplayableLmHardware> {
    
    private StarsDatabaseCache starsDatabaseCache;
    private ECMappingDao ecMappingDao;
    
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
            LiteStarsEnergyCompany energyCompany =
                starsDatabaseCache.getEnergyCompany(energyCompanyId);
            
            // gather parents energyCompanyIds
            Set<Integer> energyCompanyIds =
                ecMappingDao.getInheritedEnergyCompanyIds(energyCompany);
            
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
    
    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
		this.ecMappingDao = ecMappingDao;
	}
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }

}