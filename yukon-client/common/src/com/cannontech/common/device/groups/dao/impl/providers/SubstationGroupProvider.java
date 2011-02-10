package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.model.Substation;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.substation.dao.SubstationDao;
import com.cannontech.core.substation.dao.SubstationToRouteMappingDao;
import com.google.common.collect.ImmutableSet;

/**
 * In this class, the "bin" is the LiteYukonPAObject for the route.
 *
 */
public class SubstationGroupProvider extends BinningDeviceGroupProviderBase<Substation> {
	private SubstationDao substationDao;
	private SubstationToRouteMappingDao substationToRouteMappingDao;
	
    @Override
    protected List<Substation> getAllBins() {
        List<Substation> result = substationDao.getAll();
        return result;
    }
    
    @Override
    protected SqlFragmentSource getChildSqlSelectForBin(Substation bin) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select distinct Device.DEVICEID");
        sql.append("from Device");
        sql.append("  join DeviceRoutes on (DeviceRoutes.DEVICEID = Device.DEVICEID)");
        sql.append("  join SubstationToRouteMapping on (SubstationToRouteMapping.RouteID = DeviceRoutes.ROUTEID)");
        sql.append("where SubstationToRouteMapping.SubstationID").eq(bin.getId());
        return sql;
    }
    
    @Override
    protected SqlFragmentSource getAllBinnedDeviceSqlSelect() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select distinct Device.DEVICEID");
        sql.append("from Device");
        sql.append("  join DeviceRoutes on (DeviceRoutes.DEVICEID = Device.DEVICEID)");
        sql.append("  join SubstationToRouteMapping on (SubstationToRouteMapping.RouteID = DeviceRoutes.ROUTEID)");
        sql.append("where SubstationID != 0"); // substationDao.getAll() does the same != 0 filter
        return sql;
    }
    
    @Override
    protected Set<Substation> getBinsForDevice(YukonDevice device) {
    	List<Substation> substations = substationToRouteMappingDao.getSubstationsForDevice(device);
    	return ImmutableSet.copyOf(substations);
    }
    
    @Override
    protected String getGroupName(Substation bin) {
        return bin.getName();
    }

    @Autowired
    public void setSubstationDao(SubstationDao substationDao) {
		this.substationDao = substationDao;
	}
    
    @Autowired
    public void setSubstationToRouteMappingDao(
			SubstationToRouteMappingDao substationToRouteMappingDao) {
		this.substationToRouteMappingDao = substationToRouteMappingDao;
	}
}
