package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.Device;
import com.cannontech.yukon.IDatabaseCache;


public class DeviceTypeGroupProvider extends CompleteBinningDeviceGroupProviderBase<String> {
    
    @Autowired private IDatabaseCache dbCache;
    @Autowired private PaoDao paoDao;
    
    @Override
    protected List<String> getAllBins() {
        Set<String> paoTypes = dbCache.getAllYukonPAObjects().stream().filter(new Predicate<LiteYukonPAObject>() {
            @Override
            public boolean test(LiteYukonPAObject pao) {
                return pao.getPaoType().getPaoCategory() == PaoCategory.DEVICE
                    && pao.getLiteID() != Device.SYSTEM_DEVICE_ID;
            }
        }).map(new Function<LiteYukonPAObject, String>() {
            @Override
            public String apply(LiteYukonPAObject pao) {
                return pao.getPaoType().getDbString();
            }
        }).collect(Collectors.toSet());
        return new ArrayList<String>(paoTypes);
    }

    @Override
    protected SqlFragmentSource getChildSqlSelectForBin(String bin) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.paobjectid");
        sql.append("FROM Device d");
        sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
        sql.append("WHERE ypo.type = ");
        sql.appendArgument(bin);
        return sql;
    }
    
    @Override
    protected Set<String> getBinsForDevice(YukonDevice device) {
        LiteYukonPAObject devicePao = paoDao.getLiteYukonPAO(device.getPaoIdentifier().getPaoId());
        String type = devicePao.getPaoType().getDbString();
        return Collections.singleton(type);
    }
}
