package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.StringUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * In this class, the "bin" is the LiteYukonPAObject for the route.
 *
 */
public class RouteGroupProvider extends BinningDeviceGroupProviderBase<LiteYukonPAObject> {
    private PaoDao paoDao;
    
    @Override
    protected List<LiteYukonPAObject> getAllBins() {
        LiteYukonPAObject[] allLiteRoutes = paoDao.getAllLiteRoutes();
        return Arrays.asList(allLiteRoutes);
    }
    
    @Override
    protected SqlFragmentSource getChildSqlSelectForBin(LiteYukonPAObject bin) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT d.deviceid");
        sql.append("FROM Device d");
        sql.append("JOIN DeviceRoutes dr ON (d.deviceid = dr.deviceid)");
        sql.append("WHERE dr.routeid = ").appendArgument(bin.getLiteID());
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
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(device.getPaoIdentifier().getPaoId());
        int routeID = liteYukonPAO.getRouteID();
        if (routeID > 0) {
            LiteYukonPAObject routePao = paoDao.getLiteYukonPAO(routeID);
            return Collections.singleton(routePao);
        } else {
            return Collections.emptySet();
        }
    }
    
    @Override
    protected String getGroupName(LiteYukonPAObject bin) {
        String groupName = StringUtils.removeInvalidDeviceGroupNameCharacters(bin.getPaoName());
        return groupName;
    }

    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

}
