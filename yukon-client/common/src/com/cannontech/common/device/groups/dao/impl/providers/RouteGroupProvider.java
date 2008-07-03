package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.SqlStatementBuilder;
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
    protected String getChildSqlSelectForBin(LiteYukonPAObject bin) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.paobjectid");
        sql.append("FROM DeviceMeterGroup d");
        sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
        sql.append("JOIN DeviceRoutes dr ON (d.deviceid = dr.deviceid)");
        sql.append("WHERE dr.routeid = ", bin.getLiteID());
        return sql.toString();
    }
    
    @Override
    protected String getAllBinnedDeviceSqlSelect() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.paobjectid");
        sql.append("FROM DeviceMeterGroup d");
        sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
        sql.append("JOIN DeviceRoutes dr ON (d.deviceid = dr.deviceid)");
        sql.append("WHERE dr.routeid > 0");
        return sql.toString();
    }
    
    @Override
    protected LiteYukonPAObject getBinForDevice(YukonDevice device) {
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(device.getDeviceId());
        int routeID = liteYukonPAO.getRouteID();
        if (routeID > 0) {
            LiteYukonPAObject routePao = paoDao.getLiteYukonPAO(routeID);
            return routePao;
        } else {
            return null;
        }
    }
    
    @Override
    protected String getGroupName(LiteYukonPAObject bin) {
        return bin.getPaoName();
    }

    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

}
