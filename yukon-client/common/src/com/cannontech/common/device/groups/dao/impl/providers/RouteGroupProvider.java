package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.groups.util.DeviceGroupUtil;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

/**
 * In this class, the "bin" is the LiteYukonPAObject for the route.
 *
 */
public class RouteGroupProvider extends BinningDeviceGroupProviderBase<LiteYukonPAObject> {
    @Autowired private IDatabaseCache dbCache;
    
    @Override
    protected List<LiteYukonPAObject> getAllBins() {
        List<LiteYukonPAObject> allLiteRoutes = dbCache.getAllRoutes();
        return allLiteRoutes;
    }
    
    @Override
    protected SqlFragmentSource getChildSqlSelectForBin(LiteYukonPAObject bin) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT d.deviceid");
        sql.append("FROM Device d");
        sql.append("JOIN DeviceRoutes dr ON (d.deviceid = dr.deviceid)");
        sql.append("WHERE dr.routeid").eq(bin.getLiteID());
        return sql;
    }
    
    @Override
    protected SqlFragmentSource getAllBinnedDeviceSqlSelect() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT d.deviceid");
        sql.append("FROM Device d");
        sql.append("JOIN DeviceRoutes dr ON (d.deviceid = dr.deviceid)");
        sql.append("WHERE dr.routeid > 0");
        return sql;
    }
    
    @Override
    protected Set<LiteYukonPAObject> getBinsForDevice(YukonDevice device) {
        LiteYukonPAObject liteYukonPAO = dbCache.getAllPaosMap().get(device.getPaoIdentifier().getPaoId());
        int routeID = liteYukonPAO.getRouteID();
        if (routeID > 0) {
            LiteYukonPAObject routePao = dbCache.getAllPaosMap().get(routeID);
            return Collections.singleton(routePao);
        } else {
            return Collections.emptySet();
        }
    }
    
    @Override
    protected String getGroupName(LiteYukonPAObject bin) {
        String groupName = DeviceGroupUtil.removeInvalidDeviceGroupNameCharacters(bin.getPaoName());
        return groupName;
    }
}